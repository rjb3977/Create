package com.simibubi.create.foundation.config.ui;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.DelegatedStencilElement;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.simibubi.create.foundation.gui.TextStencilElement;
import com.simibubi.create.foundation.gui.Theme;
import com.simibubi.create.foundation.gui.UIRenderHelper;
import com.simibubi.create.foundation.gui.widgets.BoxWidget;
import com.simibubi.create.foundation.item.TooltipHelper;

import com.simibubi.create.lib.config.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

public class BaseConfigScreen extends ConfigScreen {

	private static final DelegatedStencilElement.ElementRenderer DISABLED_RENDERER = (ms, width, height, alpha) -> UIRenderHelper.angledGradient(ms, 0, 0, height / 2, height, width, Theme.i(Theme.Key.BUTTON_DISABLE, true), Theme.i(Theme.Key.BUTTON_DISABLE, false) | 0x40_000000);

	public static BaseConfigScreen forCreate(Screen parent) {
		return new BaseConfigScreen(parent)
				.withTitles("Client Settings", "World Generation Settings", "Gameplay Settings")
				.withSpecs(AllConfigs.CLIENT.getConfig(), AllConfigs.COMMON.getConfig(), AllConfigs.SERVER.getConfig());
	}

	BoxWidget clientConfigWidget;
	BoxWidget commonConfigWidget;
	BoxWidget serverConfigWidget;
	BoxWidget goBack;
	BoxWidget title;

	Config clientSpec;
	Config commonSpec;
	Config serverSpec;
	String clientTile = "CLIENT CONFIG";
	String commonTile = "COMMON CONFIG";
	String serverTile = "SERVER CONFIG";
	String modID = Create.ID;
	protected boolean returnOnClose;

	/**
	 * Note from Fabric: This is a lie, it will not work. This is a leftover from Forge.
	 *
	 * If you are a Create Addon dev and want to make use of the same GUI
	 * for your mod's config, use this Constructor to create a entry point
	 *
	 * @param parent the previously opened screen
	 * @param modID  the modID of your addon/mod
	 */
	public BaseConfigScreen(Screen parent, @Nonnull String modID) {
		this(parent);
		this.modID = modID;
	}

	private BaseConfigScreen(Screen parent) {
		super(parent);
	}

	/**
	 * If you have static references to your Configs or ConfigSpecs (like Create does in {@link AllConfigs}),
	 * please use {@link #withSpecs(ForgeConfigSpec, ForgeConfigSpec, ForgeConfigSpec)} instead
	 */
	public BaseConfigScreen searchForSpecsInModContainer() {
		try {
			clientSpec = ConfigHelper.findConfigSpecFor("client", this.modID);
		} catch (Exception e) {
			Create.LOGGER.warn("Unable to find ClientConfigSpec for mod: " + this.modID);
		}

		try {
			commonSpec = ConfigHelper.findConfigSpecFor("common", this.modID);
		} catch (Exception e) {
			Create.LOGGER.warn("Unable to find CommonConfigSpec for mod: " + this.modID, e);
		}

		try {
			serverSpec = ConfigHelper.findConfigSpecFor("server", this.modID);
		} catch (Exception e) {
			Create.LOGGER.warn("Unable to find ServerConfigSpec for mod: " + this.modID, e);
		}

		return this;
	}

	public BaseConfigScreen withSpecs(@Nullable Config client, @Nullable Config common, @Nullable Config server) {
		clientSpec = client;
		commonSpec = common;
		serverSpec = server;
		return this;
	}

	public BaseConfigScreen withTitles(@Nullable String client, @Nullable String common, @Nullable String server) {
		if (client != null)
			clientTile = client;

		if (common != null)
			commonTile = common;

		if (server != null)
			serverTile = server;

		return this;
	}

	@Override
	protected void init() {
		clientSpec = AllConfigs.CLIENT.getConfig();
		commonSpec = AllConfigs.COMMON.getConfig();
		serverSpec = AllConfigs.SERVER.getConfig();
		widgets.clear();
		super.init();
		returnOnClose = true;

		TextStencilElement clientText = new TextStencilElement(minecraft.font, new TextComponent(clientTile)).centered(true, true);
		widgets.add(clientConfigWidget = new BoxWidget(width / 2 - 100, height / 2 - 15 - 30, 200, 16).showingElement(clientText));

		if (clientSpec != null) {
			clientConfigWidget.withCallback(() -> linkTo(new SubMenuConfigScreen(this, "client", clientSpec)));
			clientText.withElementRenderer(BoxWidget.gradientFactory.apply(clientConfigWidget));
		} else {
			clientConfigWidget.active = false;
			clientConfigWidget.updateColorsFromState();
			clientText.withElementRenderer(DISABLED_RENDERER);
		}

		TextStencilElement commonText = new TextStencilElement(minecraft.font, new TextComponent(commonTile)).centered(true, true);
		widgets.add(commonConfigWidget = new BoxWidget(width / 2 - 100, height / 2 - 15, 200, 16).showingElement(commonText));

		if (commonSpec != null) {
			commonConfigWidget.withCallback(() -> linkTo(new SubMenuConfigScreen(this, "common", commonSpec)));
			commonText.withElementRenderer(BoxWidget.gradientFactory.apply(commonConfigWidget));
		} else {
			commonConfigWidget.active = false;
			commonConfigWidget.updateColorsFromState();
			commonText.withElementRenderer(DISABLED_RENDERER);
		}

		TextStencilElement serverText = new TextStencilElement(minecraft.font, new TextComponent(serverTile)).centered(true, true);
		widgets.add(serverConfigWidget = new BoxWidget(width / 2 - 100, height / 2 - 15 + 30, 200, 16).showingElement(serverText));

		if (serverSpec == null) {
			serverConfigWidget.active = false;
			serverConfigWidget.updateColorsFromState();
			serverText.withElementRenderer(DISABLED_RENDERER);
		} else if (Minecraft.getInstance().level == null) {
			serverText.withElementRenderer(DISABLED_RENDERER);
			serverConfigWidget.getToolTip()
					.add(new TextComponent("Stored individually per World"));
			serverConfigWidget.getToolTip()
					.addAll(TooltipHelper.cutTextComponent(
							new TextComponent(
									"Gameplay settings can only be accessed from the in-game menu after joining a World or Server."),
							ChatFormatting.GRAY, ChatFormatting.GRAY));
		} else {
			serverConfigWidget.withCallback(() -> linkTo(new SubMenuConfigScreen(this, ModConfig.Type.SERVER, serverSpec)));
			serverText.withElementRenderer(BoxWidget.gradientFactory.apply(serverConfigWidget));
		}

		TextStencilElement titleText = new TextStencilElement(minecraft.font, modID.toUpperCase(Locale.ROOT))
				.centered(true, true)
				.withElementRenderer((ms, w, h, alpha) -> {
					UIRenderHelper.angledGradient(ms, 0, 0, h / 2, h, w / 2, Theme.p(Theme.Key.CONFIG_TITLE_A));
					UIRenderHelper.angledGradient(ms, 0, w / 2, h / 2, h, w / 2, Theme.p(Theme.Key.CONFIG_TITLE_B));
				});
		int boxWidth = width + 10;
		int boxHeight = 39;
		int boxPadding = 4;
		title = new BoxWidget(-5, height / 2 - 110, boxWidth, boxHeight)
				//.withCustomBackground(new Color(0x20_000000, true))
				.withBorderColors(Theme.p(Theme.Key.BUTTON_IDLE))
				.withPadding(0, boxPadding)
				.rescaleElement(boxWidth / 2f, (boxHeight - 2 * boxPadding) / 2f)//double the text size by telling it the element is only half as big as the available space
				.showingElement(titleText.at(0, 7));
		title.active = false;

		widgets.add(title);


		ConfigScreen.modID = this.modID;

		goBack = new BoxWidget(width / 2 - 134, height / 2, 20, 20).withPadding(2, 2)
				.withCallback(this::onClose);
		goBack.showingElement(AllIcons.I_CONFIG_BACK.asStencil()
				.withElementRenderer(BoxWidget.gradientFactory.apply(goBack)));
		goBack.getToolTip()
				.add(new TextComponent("Go Back"));
		widgets.add(goBack);
	}

	@Override
	protected void renderWindow(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
		drawCenteredString(ms, minecraft.font, "Access Configs for Mod:", width / 2, height / 2 - 105, Theme.i(Theme.Key.TEXT_ACCENT_STRONG));
	}

	private void linkTo(Screen screen) {
		returnOnClose = false;
		ScreenOpener.open(screen);
	}

	@Override
	public void onClose() {
		super.onClose();
		ScreenOpener.open(parent);
	}

}
