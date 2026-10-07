package com.qiuyue.goetyominous.common.entities.util;

import net.minecraft.world.entity.Shearable;

public interface BoggedLike extends Shearable {

    boolean isSheared();

    void setSheared(boolean sheared);
}
