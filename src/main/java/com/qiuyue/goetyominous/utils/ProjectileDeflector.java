package com.qiuyue.goetyominous.utils;

import net.minecraft.world.entity.projectile.Projectile;

public interface ProjectileDeflector {
    ProjectileDeflection deflection(Projectile projectile);
}
