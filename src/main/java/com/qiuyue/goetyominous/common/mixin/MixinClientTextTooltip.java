package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.client.rlyeh.RlyehFont;
import com.qiuyue.goetyominous.client.rlyeh.RlyehStyledText;
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
        if (seq == null || !usesRlyehFont(seq)) {
            return;
        }
        if (MODERN_UI) {
            RlyehStyledText.draw(font, seq, x, y, matrix, source);
        } else {
            font.drawInBatch8xOutline(seq, (float) x, (float) y, 0xFFFFFFFF, 0xFF000000,
                    matrix, source, 15728880);
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
}
