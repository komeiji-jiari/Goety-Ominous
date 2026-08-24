/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.compat.ICompatable
 *  net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
 */
package com.vivideru.masteryofmagic;

import com.Polarice3.Goety.compat.ICompatable;
import java.lang.reflect.Method;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class GMPatchouliIntegration
implements ICompatable {
    public void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(this::registerCompatHooks);
    }

    private void registerCompatHooks() {
        GMPatchouliIntegration.tryInvokeStatic("com.Polarice3.Goety.compat.patchouli.PatchouliIntegration", "registerBook", new Class[]{String.class, String.class}, "goety", "black_book");
        GMPatchouliIntegration.tryInvokeStatic("com.Polarice3.Goety.compat.patchouli.PatchouliIntegration", "registerModRecipes", new Class[]{String.class}, "goety_mastery_of_magic");
        GMPatchouliIntegration.tryInvokeStatic("com.Polarice3.Goety.compat.patchouli.PatchouliIntegration", "registerModRituals", new Class[]{String.class}, "goety_mastery_of_magic");
        GMPatchouliIntegration.tryInvokeStatic("com.Polarice3.Goety.compat.patchouli.PatchouliIntegration", "registerRecipes", new Class[]{String.class}, "goety_mastery_of_magic");
        GMPatchouliIntegration.tryInvokeStatic("com.Polarice3.Goety.compat.patchouli.PatchouliIntegration", "registerRituals", new Class[]{String.class}, "goety_mastery_of_magic");
    }

    private static void tryInvokeStatic(String className, String methodName, Class<?>[] paramTypes, Object ... args) {
        try {
            Class<?> cls = Class.forName(className);
            Method m = cls.getMethod(methodName, paramTypes);
            m.invoke(null, args);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

