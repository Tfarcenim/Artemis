/*
 * Copyright © Wynntils 2023.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.screens.base.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wynntils.utils.colors.CommonColors;
import com.wynntils.utils.colors.CustomColor;
import com.wynntils.utils.mc.McUtils;
import com.wynntils.utils.render.FontRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class WynntilsCheckbox extends Checkbox {
    private final int maxTextWidth;
    private final int color;

    public WynntilsCheckbox(
            int x, int y, int width, int height, Component message, boolean selected, int maxTextWidth) {
        super(x, y, width, height, message, selected);
        this.maxTextWidth = maxTextWidth;
        this.color = CommonColors.WHITE.asInt();
    }

    public WynntilsCheckbox(
            int x,
            int y,
            int width,
            int height,
            Component message,
            boolean selected,
            int maxTextWidth,
            CustomColor color) {
        super(x, y, width, height, message, selected);
        this.maxTextWidth = maxTextWidth;
        this.color = color.asInt();
    }

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableDepthTest();
        Font font = FontRenderer.getInstance().getFont();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        ResourceLocation resourceLocation;

        guiGraphics.blit(
                TEXTURE,
                this.getX(),
                this.getY(),
                this.isFocused() ? 20.0f : 0.0f,
                this.selected ? 20.0f : 0.0f,
                20,
                this.height,
                64,
                64);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.showLabel) {
            int start = this.getX() + this.width + 2;
            int end = start + this.maxTextWidth;

            McUtils.renderScrollingString(
                    guiGraphics,
                    font,
                    this.getMessage(),
                    start,
                    start,
                    this.getY(),
                    end,
                    this.getY() + this.getHeight(),
                    this.color);
        }
    }
}
