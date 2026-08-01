package com.qiuyue.goetyominous.common.items;

import com.Polarice3.Goety.common.items.research.Scroll;
import com.Polarice3.Goety.common.research.Research;
import net.minecraft.network.chat.Component;

public class ScrollItem extends Scroll {

    public ScrollItem(Research research) {
        super(research);
    }

    @Override
    public Component researchGet() {
        return Component.translatable("info.goetyominous.research." + this.research.getId());
    }
}