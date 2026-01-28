package net.phospherion.kazekatana.item.specialz;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import org.jetbrains.annotations.Nullable;
import net.phospherion.kazekatana.util.TagHelper;

// GeckoLib 4.6.3 imports
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;

/**
 * SkySplitItem
 *
 * - Extends SwordItem (uses the constructor signature present in your mappings).
 * - Implements GeoItem so GeckoLib can animate it.
 * - Stores per-stack pending animation name in NBT; the renderer will consume it and trigger the animation.
 */
public class SkySplitItem extends SwordItem implements GeoItem {

    private static final String NBT_PENDING_ANIM = "kazekatana:pending_animation";

    // AnimatableInstanceCache used by GeckoLib to manage per-instance animation managers
    private final AnimatableInstanceCache instanceCache = GeckoLibUtil.createInstanceCache(this);

    public SkySplitItem(Tier tier, Properties properties) {
        super(tier, properties);
        // If you want synced animatables across network, call GeoItem.registerSyncedAnimatable(this) here.
    }

    // -------------------------
    // GeoAnimatable contract
    // -------------------------
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return instanceCache;
    }

    /**
     * Register animation controllers for this item.
     * The ControllerRegistrar will collect controllers and GeckoLib will tick them.
     */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Add a single controller named "item_controller".
        // The lambda is an AnimationStateHandler that GeckoLib calls each frame for this controller.
        controllers.add(new AnimationController<>(
                this,
                "item_controller",
                state -> {
                    // Default loop: hold. Renderer will trigger one-shots by calling AnimatableManager.tryTriggerAnimation(...)
                    state.getController().setAnimation(RawAnimation.begin().thenLoop("animation.hold"));
                    return PlayState.CONTINUE;
                }
        ));
    }

    // -------------------------
    // Per-stack helper methods
    // -------------------------
    /**
     * Request a one-shot animation for this ItemStack.
     * Gameplay code (server or client) should call this when an animation should play for that stack.
     */

    public static void requestAnimation(ItemStack stack, String animationName) {
        if (stack == null) return;
        CompoundTag tag = TagHelper.getOrCreateTag(stack);
        tag.putString(NBT_PENDING_ANIM, animationName);
        net.phospherion.kazekatana.util.TagHelper.setTag(stack, tag); // ensure it's attached (safe no-op if already attached)
    }

    @Nullable
    public static String consumePendingAnimation(ItemStack stack) {
        if (stack == null) return null;
        CompoundTag tag = TagHelper.getTag(stack);
        if (tag == null) return null;
        if (!tag.contains(NBT_PENDING_ANIM)) return null;
        String anim = tag.getString(NBT_PENDING_ANIM);
        tag.remove(NBT_PENDING_ANIM);
        if (tag.isEmpty()) {
            TagHelper.setTag(stack, null); // remove tag if empty
        } else {
            TagHelper.setTag(stack, tag); // reattach updated tag
        }
        return anim;
    }


    // -------------------------
    // Example gameplay hook
    // -------------------------
    /**
     * Called server-side when this item damages an entity.
     * We request the client-side animation here (singleplayer will show it automatically).
     * In multiplayer, you should send a small packet to the client to request the animation.
     */
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        requestAnimation(stack, "animation.slashleftbright");
        return super.hurtEnemy(stack, target, attacker);
    }
}
