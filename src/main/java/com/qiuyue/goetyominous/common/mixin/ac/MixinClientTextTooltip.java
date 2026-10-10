package com.qiuyue.goetyominous.common.mixin.ac;

import com.qiuyue.goetyominous.client.rlyeh.ac.RlyehFont;
import com.qiuyue.goetyominous.client.rlyeh.ac.RlyehStyledText;
import com.qiuyue.goetyominous.client.rlyeh.ac.RlyehTooltipHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.fml.ModList;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientTextTooltip.class)
public class MixinClientTextTooltip {

    @Shadow
    @Final
    private FormattedCharSequence text;

    private static final boolean MODERN_UI =
            ModList.get() != null && ModList.get().isLoaded("modernui");

    @Inject(method = "renderText", at = @At("HEAD"), cancellable = true)
    private void rlyeh$outline(Font font, int x, int y, Matrix4f matrix,
                               MultiBufferSource.BufferSource source, CallbackInfo ci) {
        FormattedCharSequence seq = this.text;
        if (seq == null) {
            return;
        }
        if (!usesRlyehFont(seq) && !(RlyehTooltipHandler.isRlyehTooltip() && usesAbyssMark(seq))) {
            return;
        }
        if (MODERN_UI) {
            RlyehStyledText.draw(font, seq, x, y, matrix);
        } else {
            RlyehStyledText.drawNative(font, seq, x, y, matrix);
        }
        ci.cancel();
    }

    private static boolean usesRlyehFont(FormattedCharSequence seq) {
        boolean[] hit = {false};
        seq.accept((pos, style, codePoint) -> {
            if (RlyehFont.isRlyehFont(style.getFont())) {
                hit[0] = true;
            }
            return true;
        });
        return hit[0];
    }

    private static boolean usesAbyssMark(FormattedCharSequence seq) {
        boolean[] hit = {false};
        seq.accept((pos, style, codePoint) -> {
            if (RlyehFont.isAbyssMark(style)) {
                hit[0] = true;
            }
            return true;
        });
        return hit[0];
    }
}
