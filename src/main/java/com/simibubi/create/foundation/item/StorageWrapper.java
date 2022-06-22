package com.simibubi.create.foundation.item;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Iterators;

import java.util.Iterator;

public abstract class StorageWrapper<T> implements Storage<T> {

	private Storage<T> wrapped;
	private long versionOffset;

	public StorageWrapper(Storage<T> wrapped) {
		setWrapped(wrapped);
	}

	protected abstract boolean allowInsert(T resource);
	protected abstract boolean allowExtract(T resource);
	protected abstract void onInsert(T resource, long amount);
	protected abstract void onExtract(T resource, long amount);

	protected StorageView<T> wrapView(StorageView<T> view) {
		return new StorageViewWrapper(view);
	}

	protected Storage<T> getWrapped() {
		return wrapped;
	}

	protected void setWrapped(Storage<T> wrapped) {
		if (this.wrapped != null) {
			this.versionOffset += this.wrapped.getVersion();
		}

		this.versionOffset -= wrapped.getVersion();
		this.wrapped = wrapped;
	}

	@Override
	public boolean supportsInsertion() {
		return wrapped.supportsInsertion();
	}

	@Override
	public long insert(T resource, long maxAmount, TransactionContext transaction) {
		if (!allowInsert(resource)) {
			return 0;
		}

		long amount = wrapped.insert(resource, maxAmount, transaction);

		if (amount > 0) {
			transaction.addCloseCallback((_transaction, result) -> { 
				if (result.wasCommitted()) {
					onInsert(resource, amount);
				}
			});
		}

		return amount;
	}

	@Override
	public long simulateInsert(T resource, long maxAmount, @Nullable TransactionContext transaction) {
		if (!allowInsert(resource)) {
			return 0;
		}

		return wrapped.simulateInsert(resource, maxAmount, transaction);
	}

	@Override
	public boolean supportsExtraction() {
		return wrapped.supportsExtraction();
	}

	@Override
	public long extract(T resource, long maxAmount, TransactionContext transaction) {
		if (!allowExtract(resource)) {
			return 0;
		}

		long amount = wrapped.extract(resource, maxAmount, transaction);

		if (amount > 0) {
			transaction.addCloseCallback((_transaction, result) -> { 
				if (result.wasCommitted()) {
					onExtract(resource, amount);
				}
			});
		}

		return amount;
	}

	@Override
	public long simulateExtract(T resource, long maxAmount, @Nullable TransactionContext transaction) {
		if (!allowExtract(resource)) {
			return 0;
		}

		return wrapped.simulateExtract(resource, maxAmount, transaction);
	}

	@Override
	public Iterator<? extends StorageView<T>> iterator(TransactionContext transaction) {
		return Iterators.transform(wrapped.iterator(transaction), this::wrapView);
	}

	@Override
	public @Nullable StorageView<T> exactView(TransactionContext transaction, T resource) {
		return wrapView(wrapped.exactView(transaction, resource));
	}

	@Override
	public long getVersion() {
		return this.versionOffset + wrapped.getVersion();
	}

	public class StorageViewWrapper implements StorageView<T> {
		private final StorageView<T> wrapped;

		public StorageViewWrapper(StorageView<T> wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public long extract(T resource, long maxAmount, TransactionContext transaction) {
			if (!allowExtract(resource)) {
				return 0;
			}
	
			long amount = wrapped.extract(resource, maxAmount, transaction);
	
			if (amount > 0) {
				transaction.addCloseCallback((_transaction, result) -> { 
					if (result.wasCommitted()) {
						onExtract(resource, amount);
					}
				});
			}
	
			return amount;
		}

		@Override
		public boolean isResourceBlank() {
			return wrapped.isResourceBlank();
		}

		@Override
		public T getResource() {
			return wrapped.getResource();
		}

		@Override
		public long getAmount() {
			return wrapped.getAmount();
		}

		@Override
		public long getCapacity() {
			return wrapped.getCapacity();
		}

		@Override
		public StorageView<T> getUnderlyingView() {
			return wrapped;
		}
	}
}
