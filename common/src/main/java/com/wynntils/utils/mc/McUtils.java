/*
 * Copyright © Wynntils 2022-2023.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.utils.mc;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.wynntils.core.WynntilsMod;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * This is a "high-quality misc" class for Minecraft utilities without an aspect on Wynntils
 * commonly used. Tags used more often should be moved elsewhere Keep the names short, but distinct.
 */
public final class McUtils {
    public static Minecraft mc() {
        return Minecraft.getInstance();
    }

    public static LocalPlayer player() {
        return mc().player;
    }

    public static String playerName() {
        return player().getName().getString();
    }

    public static Options options() {
        return mc().options;
    }

    public static InventoryMenu inventoryMenu() {
        return player().inventoryMenu;
    }

    public static AbstractContainerMenu containerMenu() {
        return player().containerMenu;
    }

    public static Inventory inventory() {
        return player().getInventory();
    }

    public static Window window() {
        return mc().getWindow();
    }

    public static double guiScale() {
        return window().getGuiScale();
    }

    public static void playSoundUI(SoundEvent sound) {
        mc().getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F));
    }

    public static void playSoundAmbient(SoundEvent sound) {
        mc().getSoundManager().play(SimpleSoundInstance.forLocalAmbience(sound, 1.0F, 1.0F));
    }

    public static void sendMessageToClient(Component component) {
        if (player() == null) {
            WynntilsMod.error(
                    "Tried to send message to client: \"" + component.getString() + "\", but player was null.");
            return;
        }
        player().sendSystemMessage(component);
    }

    public static void sendErrorToClient(String errorMsg) {
        WynntilsMod.warn("Chat error message sent: " + errorMsg);
        McUtils.sendMessageToClient(Component.literal(errorMsg).withStyle(ChatFormatting.RED));
    }

    public static void sendPacket(Packet<?> packet) {
        if (mc().getConnection() == null) {
            WynntilsMod.error(
                    "Tried to send packet: \"" + packet.getClass().getSimpleName() + "\", but connection was null.");
            return;
        }

        mc().getConnection().send(packet);
    }

    public static void sendSequencedPacket(PredictiveAction predictiveAction) {
        mc().gameMode.startPrediction(mc().level, predictiveAction);
    }

    /**
     * Sends some chat message directly to the server.
     * Does not respect ChatTabFeature settings.
     * @param message The message to send.
     */
    public static void sendChat(String message) {
        mc().getConnection().sendChat(message);
    }

    public static void renderEntityInInventoryFollowsMouse(
            GuiGraphics guiGraphics,
            int x1,
            int y1,
            int x2,
            int y2,
            int scale,
            float yOffset,
            float mouseX,
            float mouseY,
            LivingEntity entity) {
        float f = (float) (x1 + x2) / 2.0f;
        float g = (float) (y1 + y2) / 2.0f;
        guiGraphics.enableScissor(x1, y1, x2, y2);
        float h = (float) Math.atan((f - mouseX) / 40.0f);
        float i = (float) Math.atan((g - mouseY) / 40.0f);
        Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf quaternionf2 = new Quaternionf().rotateX(i * 20.0f * ((float) Math.PI / 180));
        quaternionf.mul(quaternionf2);
        float j = entity.yBodyRot;
        float k = entity.getYRot();
        float l = entity.getXRot();
        float m = entity.yHeadRotO;
        float n = entity.yHeadRot;
        entity.yBodyRot = 180.0f + h * 20.0f;
        entity.setYRot(180.0f + h * 40.0f);
        entity.setXRot(-i * 20.0f);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();
        Vector3f vector3f = new Vector3f(0.0f, entity.getBbHeight() / 2.0f + yOffset, 0.0f);
        renderEntityInInventory(guiGraphics, f, g, scale, vector3f, quaternionf, quaternionf2, entity);
        entity.yBodyRot = j;
        entity.setYRot(k);
        entity.setXRot(l);
        entity.yHeadRotO = m;
        entity.yHeadRot = n;
        guiGraphics.disableScissor();
    }

    public static void renderScrollingString(
            GuiGraphics guiGraphics,
            Font font,
            Component text,
            int i,
            int minX,
            int minY,
            int maxX,
            int maxY,
            int color) {
        int j = font.width(text);
        int k = (minY + maxY - font.lineHeight) / 2 + 1;
        int l = maxX - minX;
        if (j > l) {
            int m = j - l;
            double d = (double) Util.getMillis() / 1000.0;
            double e = Math.max((double) m * 0.5, 3.0);
            double f = Math.sin(1.5707963267948966 * Math.cos(Math.PI * 2 * d / e)) / 2.0 + 0.5;
            double g = Mth.lerp(f, 0.0, m);
            guiGraphics.enableScissor(minX, minY, maxX, maxY);
            guiGraphics.drawString(font, text, minX - (int) g, k, color);
            guiGraphics.disableScissor();
        } else {
            int m = Mth.clamp(i, minX + j / 2, maxX - j / 2);
            guiGraphics.drawCenteredString(font, text, m, k, color);
        }
    }

    public static void renderEntityInInventory(
            GuiGraphics guiGraphics,
            float x,
            float y,
            int scale,
            Vector3f translate,
            Quaternionf pose,
            Quaternionf cameraOrientation,
            LivingEntity entity) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 50.0);
        guiGraphics.pose().mulPoseMatrix(new Matrix4f().scaling(scale, scale, -scale));
        guiGraphics.pose().translate(translate.x, translate.y, translate.z);
        guiGraphics.pose().mulPose(pose);
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        if (cameraOrientation != null) {
            cameraOrientation.conjugate();
            entityRenderDispatcher.overrideCameraOrientation(cameraOrientation);
        }
        entityRenderDispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(
                entity, 0.0, 0.0, 0.0, 0.0f, 1.0f, guiGraphics.pose(), guiGraphics.bufferSource(), 0xF000F0));
        guiGraphics.flush();
        entityRenderDispatcher.setRenderShadow(true);
        guiGraphics.pose().popPose();
        Lighting.setupFor3DItems();
    }
}
