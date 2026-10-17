package com.qiuyue.goetyominous.common.items;

import com.qiuyue.goetyominous.common.events.WindBowEvents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * 狂风之弓 —— 一把**不消耗箭**的弓。它射出的是"风箭"：一支普通箭，命中后由
 * {@link WindBowEvents} 给它套上风场（拉拽/牵引）。箭本身不消耗、不捡回
 * （{@link AbstractArrow.Pickup#CREATIVE_ONLY}），所以从背包到落地都是零成本。
 *
 * <p>实现就两条 override，都在 {@link WitchBowItem}（复刻 vanilla 的释放逻辑）之外另写一层：
 * <ol>
 *   <li>{@link #use} —— vanilla 的 {@code BowItem.use} 在"没箭"时直接返回 {@code fail}，
 *       连弓都拉不开。这里恒报"有箭"，所以空手也能拉满。</li>
 *   <li>{@link #releaseUsing} —— 不调 super（super 会 {@code itemstack.shrink(1)}），
 *       而是复刻释放逻辑，但用一根**虚拟箭**（{@code new ItemStack(Items.ARROW)}，
 *       不进背包）造 {@code AbstractArrow}，且 {@code pickup = CREATIVE_ONLY}。
 *       弓的耐久照常消耗。</li>
 * </ol>
 *
 * <p>风场逻辑（粒子、拉拽、落地爆发）全在 {@link WindBowEvents}，本类只管"射"。
 */
public class WindBowItem extends WitchBowItem {

    @Override
    public AbstractArrow customArrow(AbstractArrow arrow) {
        WindBowEvents.mark(super.customArrow(arrow));
        return arrow;
    }

    /**
     * 没箭也拉得开。vanilla 的 {@code BowItem.use} 在 {@code !hasAmmo && !instabuild} 时返回
     * {@code fail}，于是空手右键什么也不发生 —— 风弓要的就是"空手也能射"，所以恒报有箭。
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        InteractionResultHolder<ItemStack> ret =
                ForgeEventFactory.onArrowNock(stack, level, player, hand, true);
        if (ret != null) {
            return ret;
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    /**
     * 射出一根风箭，不消耗背包里的箭。复刻 {@link WitchBowItem#releaseUsing} 的形状（附魔、
     * 暴击、音效都在），但：
     * <ul>
     *   <li>不调 super —— super 的 {@code itemstack.shrink(1)} 是消耗的源头；</li>
     *   <li>用一根**虚拟** {@link ItemStack Items.ARROW} 造实体，它从来不是背包里那一摞的引用，
     *       所以即使 shrink 也缩的是这根临时的；</li>
     *   <li>{@code pickup = CREATIVE_ONLY} —— 落地的箭不捡回，真正零消耗；</li>
     *   <li>弓的耐久照常掉。</li>
     * </ul>
     */
    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) {
            return;
        }
        int i = this.getUseDuration(stack) - timeLeft;
        i = ForgeEventFactory.onArrowLoose(stack, level, player, i, true);
        if (i < 0) {
            return;
        }
        float f = getPowerForTime(i);
        if (f < 0.1F) {
            return;
        }
        if (!level.isClientSide) {
            ItemStack virtualArrow = new ItemStack(Items.ARROW);
            AbstractArrow abstractarrow =
                    ((ArrowItem) Items.ARROW).createArrow(level, virtualArrow, player);
            abstractarrow = customArrow(abstractarrow);
            abstractarrow.shootFromRotation(player, player.getXRot(), player.getYRot(),
                    0.0F, f * 3.0F, 1.0F);
            if (f == 1.0F) {
                abstractarrow.setCritArrow(true);
            }
            int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
            if (j > 0) {
                abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() + (double) j * 0.5D + 0.5D);
            }
            int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
            if (k > 0) {
                abstractarrow.setKnockback(k);
            }
            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
                abstractarrow.setSecondsOnFire(100);
            }
            stack.hurtAndBreak(1, player,
                    p -> p.broadcastBreakEvent(player.getUsedItemHand()));
            abstractarrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            level.addFreshEntity(abstractarrow);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
                1.0F, 1.0F / (player.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
        player.awardStat(Stats.ITEM_USED.get(this));
    }
}
