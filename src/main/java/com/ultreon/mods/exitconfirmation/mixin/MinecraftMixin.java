package com.ultreon.mods.exitconfirmation.mixin;

import com.ultreon.mods.exitconfirmation.ActionResult;
import com.ultreon.mods.exitconfirmation.ConfirmExitScreen;
import com.ultreon.mods.exitconfirmation.ExitConfirmation;
import com.ultreon.mods.exitconfirmation.WindowCloseEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftApplet;
import net.minecraft.client.gui.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.swing.*;
import java.awt.*;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow private static Minecraft instance;

    @Redirect(at = @At(value = "NEW", target = "Ljava/awt/Frame;"), method = "start(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V")
    private static Frame start(String title) {
        return new JFrame(title);
    }

    @Inject(at = @At(value = "RETURN"), method = "<init>")
    private void exitConfirm$constructorInject(Component canvas, Canvas minecraftApplet, MinecraftApplet i, int j, int flag, boolean par6, CallbackInfo ci) {
        ExitConfirmation.minecraft = instance;
    }
    @Inject(at = @At(value = "HEAD"), method = "scheduleStop", cancellable = true)
    private void exitConfirm$injectScheduleStop(CallbackInfo ci) {
        Minecraft minecraft = ExitConfirmation.minecraft;
        if (minecraft == null) return;
        Screen currentScreen = minecraft.currentScreen;
        if (currentScreen instanceof ConfirmExitScreen) {
            ActionResult closing = WindowCloseEvent.EVENT.invoker().closing(ExitConfirmation.getGameWindow(), WindowCloseEvent.Source.QUIT_BUTTON);
            if (closing.equals(ActionResult.PASS)) {
                minecraft.scheduleStop();
            } else {
                ci.cancel();
            }
        } else {
            ci.cancel();
        }
    }
}