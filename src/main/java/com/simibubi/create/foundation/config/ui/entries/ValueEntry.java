package com.simibubi.create.foundation.config.ui.entries;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.config.ui.ConfigScreen;
import com.simibubi.create.foundation.config.ui.ConfigScreenList;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.DelegatedStencilElement;
import com.simibubi.create.foundation.gui.widgets.BoxWidget;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.lib.config.ConfigValue;
import com.simibubi.create.lib.mixin.accessor.AbstractList$AbstractListEntryAccessor;

public class ValueEntry<T> extends ConfigScreenList.LabeledEntry {

	protected static final MutableComponent modComponent = new TextComponent("* ").withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_BLUE).append(TextComponent.EMPTY.plainCopy().withStyle(ChatFormatting.RESET));
	protected static final int resetWidth = 28;//including 6px offset on either side
	public static final Pattern unitPattern = Pattern.compile("\\[(in .*)]");

	protected ConfigValue<T> value;
//	protected ForgeConfigSpec.ValueSpec spec;
	protected BoxWidget resetButton;
	protected boolean editable = true;
	protected String path;

	public ValueEntry(String label, ConfigValue<T> value) {
		super(label);
		this.value = value;
//		this.spec = spec;
		this.path = String.join(".", value.key);

		resetButton = new BoxWidget(0, 0, resetWidth - 12, 16)
				.showingElement(AllIcons.I_CONFIG_RESET.asStencil())
				.withCallback(() -> {
					setValue((T) value.defaultValue);
					this.onReset();
				});
		resetButton.modifyElement(e -> ((DelegatedStencilElement) e).withElementRenderer(BoxWidget.gradientFactory.apply(resetButton)));

		listeners.add(resetButton);

		List path = Arrays.asList(Collections.singleton(value.key).toArray());
		labelTooltip.add(new TextComponent(label).withStyle(ChatFormatting.WHITE));
		String comment = "";
		if (value.comments != null && value.comments.size() != 0) {
			comment = value.comments.get(0);
		}
		if (comment == null || comment.isEmpty())
			return;
		String[] commentLines = comment.split("\n");
		//find unit in the comment
		for (int i = 0; i < commentLines.length; i++) {
			if (commentLines[i].isEmpty()) {
				commentLines = ArrayUtils.remove(commentLines, i);
				i--;
				continue;
			}

			Matcher matcher = unitPattern.matcher(commentLines[i]);
			if (!matcher.matches())
				continue;

			String u = matcher.group(1);
			if (u.equals("in Revolutions per Minute"))
				u = "in RPM";
			if (u.equals("in Stress Units"))
				u = "in SU";
			unit = u;
		}
		// add comment to tooltip
		labelTooltip.addAll(Arrays.stream(commentLines)
				.filter(Predicates.not(s -> s.startsWith("Range")))
				.map(TextComponent::new)
				.flatMap(stc -> TooltipHelper.cutTextComponent(stc, ChatFormatting.GRAY, ChatFormatting.GRAY)
						.stream())
				.collect(Collectors.toList()));
		labelTooltip.add(new TextComponent(ConfigScreen.modID + ":" + path.get(path.size() - 1)).withStyle(ChatFormatting.DARK_GRAY));
	}

	@Override
	protected void setEditable(boolean b) {
		editable = b;
		resetButton.active = editable && !isCurrentValueDefault();
		resetButton.animateGradientFromState();
	}

	@Override
	public void tick() {
		super.tick();
		resetButton.tick();
	}

	@Override
	public void render(PoseStack ms, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean p_230432_9_, float partialTicks) {
		if (isCurrentValueChanged()) {
			MutableComponent original = label.getComponent();
			MutableComponent changed = modComponent.plainCopy().append(original);
			label.withText(changed);
			super.render(ms, index, y, x, width, height, mouseX, mouseY, p_230432_9_, partialTicks);
			label.withText(original);
		} else {
			super.render(ms, index, y, x, width, height, mouseX, mouseY, p_230432_9_, partialTicks);
		}

		resetButton.x = x + width - resetWidth + 6;
		resetButton.y = y + 10;
		resetButton.render(ms, mouseX, mouseY, partialTicks);
	}

	@Override
	protected int getLabelWidth(int totalWidth) {
		return (int) (totalWidth * labelWidthMult) + 30;
	}

	public void setValue(@Nonnull T value) {
		if (value.equals(this.value.get())) {
			ConfigScreen.changes.remove(path);
			onValueChange(value);
			return;
		}

		ConfigScreen.changes.put(path, value);
		onValueChange(value);
	}

	@Nonnull
	public T getValue() {
		//noinspection unchecked
		return (T) ConfigScreen.changes.getOrDefault(path, this.value.get());
	}

	protected boolean isCurrentValueChanged() {
		return ConfigScreen.changes.containsKey(path);
	}

	protected boolean isCurrentValueDefault() {
		return value.defaultValue == getValue();
	}

	public void onReset() {
		onValueChange(getValue());
	}

	public void onValueChange() {
		onValueChange(getValue());
	}
	public void onValueChange(T newValue) {
		resetButton.active = editable && !isCurrentValueDefault();
		resetButton.animateGradientFromState();
	}

	protected void bumpCog() {bumpCog(10f);}
	protected void bumpCog(float force) {
		if (((AbstractList$AbstractListEntryAccessor) this).getList() != null && ((AbstractList$AbstractListEntryAccessor) this).getList() instanceof ConfigScreenList)
			((ConfigScreenList) ((AbstractList$AbstractListEntryAccessor) this).getList()).bumpCog(force);
	}
}
