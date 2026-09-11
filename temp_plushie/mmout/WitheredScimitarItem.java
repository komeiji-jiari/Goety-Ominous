package com.alexander.mutantmore.items;

import com.alexander.mutantmore.config.mutant_wither_skeleton.MutantWitherSkeletonRewardsCommonConfig;
import com.alexander.mutantmore.entities.WitherSlash;
import com.alexander.mutantmore.events.ShakeCameraEvent;
import com.alexander.mutantmore.init.SoundEventInit;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class WitheredScimitarItem extends SwordItem {
    public WitheredScimitarItem(Tier p_43269_, int p_43270_, float p_43271_, Item.Properties p_43272_) {
        super(p_43269_, p_43270_, p_43271_, p_43272_);
    }

    public void m_7373_(ItemStack stack, @Nullable Level level, List<Component> p_43045_, TooltipFlag p_43046_) {
        p_43045_.add(Component.m_237115_("mutantmore.desc.abilities").m_130940_(ChatFormatting.GRAY));
        p_43045_.add(Component.m_237110_("mutantmore.withered_scimitar.desc", new Object[]{Component.m_237117_(Minecraft.m_91087_().f_91066_.f_92095_.m_90860_())}).m_130940_(ChatFormatting.BLUE));
    }

    public int getMaxDamage(ItemStack stack) {
        int value = this.m_43314_().m_6609_();
        this.f_41371_ = value;
        return value;
    }

    public InteractionResultHolder<ItemStack> m_7203_(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        ItemStack itemstack = p_41433_.m_21120_(p_41434_);
        boolean flag = p_41433_ instanceof Player && p_41433_.m_150110_().f_35937_;
        p_41433_.m_6674_(p_41434_);
        p_41433_.m_36335_().m_41524_(itemstack.m_41720_(), MutantWitherSkeletonRewardsCommonConfig.scimitar_shoot_wither_slash_speed.get());
        ShakeCameraEvent.shake(p_41432_, 5, 0.04F, p_41433_.m_20183_(), 5);
        shootProjectile(p_41432_, p_41433_, p_41434_, itemstack, flag, 1.0F, 1.0F, 0.0F);
        return super.m_7203_(p_41432_, p_41433_, p_41434_);
    }

    public boolean m_7579_(ItemStack p_43278_, LivingEntity p_43279_, LivingEntity p_43280_) {
        p_43279_.m_147207_(new MobEffectInstance(MobEffects.f_19615_, MutantWitherSkeletonRewardsCommonConfig.scimitar_attack_wither_length.get(), MutantWitherSkeletonRewardsCommonConfig.scimitar_attack_wither_level.get()), p_43280_);
        return super.m_7579_(p_43278_, p_43279_, p_43280_);
    }

    private static void shootProjectile(Level p_40895_, Player p_40896_, InteractionHand p_40897_, ItemStack p_40898_, boolean p_40901_, float p_40902_, float p_40903_, float p_40904_) {
        if (!p_40895_.f_46443_) {
            Vec3 vec31 = p_40896_.m_20289_(1.0F);
            Quaternionf quaternion = (new Quaternionf()).setAngleAxis((double)p_40904_, vec31.f_82479_, vec31.f_82480_, vec31.f_82481_);
            Vec3 vec3 = p_40896_.m_20252_(1.0F);
            Vector3f vector3f = quaternion.transform(vec3.m_252839_());
            WitherSlash projectile = new WitherSlash(p_40895_, p_40896_, p_40896_.f_20885_);
            projectile.m_6686_((double)vector3f.x(), (double)vector3f.y(), (double)vector3f.z(), 2.0F, 0.0F);
            projectile.m_6027_(p_40896_.m_20185_(), p_40896_.m_20227_((double)0.6F), p_40896_.m_20189_());
            projectile.damage = ((Double)MutantWitherSkeletonRewardsCommonConfig.scimitar_wither_slash_damage.get()).floatValue();
            projectile.witherLength = MutantWitherSkeletonRewardsCommonConfig.scimitar_wither_slash_wither_length.get();
            projectile.witherLevel = MutantWitherSkeletonRewardsCommonConfig.scimitar_wither_slash_wither_level.get();
            p_40898_.m_41622_(MutantWitherSkeletonRewardsCommonConfig.scimitar_shoot_wither_slash_durability_consumption.get(), p_40896_, (p_40858_) -> p_40858_.m_21190_(p_40897_));
            p_40896_.m_36346_();
            p_40895_.m_7967_(projectile);
            p_40895_.m_6263_((Player)null, p_40896_.m_20185_(), p_40896_.m_20186_(), p_40896_.m_20189_(), (SoundEvent)SoundEventInit.MUTANT_WITHER_SKELETON_FIRE_SLASH.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }

    }

    public Multimap<Attribute, AttributeModifier> m_7167_(EquipmentSlot p_43274_) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.f_22281_, new AttributeModifier(f_41374_, "Weapon modifier", MutantWitherSkeletonRewardsCommonConfig.scimitar_attack_damage.get(), Operation.ADDITION));
        builder.put(Attributes.f_22283_, new AttributeModifier(f_41375_, "Weapon modifier", MutantWitherSkeletonRewardsCommonConfig.scimitar_attack_speed.get(), Operation.ADDITION));
        Multimap<Attribute, AttributeModifier> modifiers = builder.build();
        return (Multimap<Attribute, AttributeModifier>)(p_43274_ == EquipmentSlot.MAINHAND ? modifiers : ImmutableMultimap.of());
    }
}
