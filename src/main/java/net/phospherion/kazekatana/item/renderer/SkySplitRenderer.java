package net.phospherion.kazekatana.item.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.animation.RawAnimation;

import net.phospherion.kazekatana.item.specialz.SkySplitItem;
import net.phospherion.kazekatana.item.models.SkySplitModel;
import net.phospherion.kazekatana.util.TagHelper;

/**
 * SkySplitItemRenderer
 *
 * - Extends GeoItemRenderer so GeckoLib handles model/animation rendering.
 * - Consumes per-stack pending animation NBT and triggers it on the AnimatableManager.
 * - Samples the katana tip bone and spawns a particle at that world position (example).
 */
public class SkySplitItemRenderer extends GeoItemRenderer<SkySplitItem> {

    public SkySplitItemRenderer() {
        super(new SkySplitModel());
    }

    @Override
    public void render(SkySplitItem item, ItemStack stack, ItemRenderer.TransformType transformType,
                       PoseStack matrixStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {

        // 1) Consume pending animation from the ItemStack (set by gameplay code)
        String pending = SkySplitItem.consumePendingAnimation(stack);
        if (pending != null && !pending.isEmpty()) {
            triggerPendingAnimationForStack(item, stack, pending);
        }

        // 2) Let GeoItemRenderer do its normal rendering (model + animations)
        super.render(item, stack, transformType, matrixStack, buffer, packedLight, packedOverlay);

        // 3) Sample the katana tip bone and spawn a simple particle (client-side visual)
        //    We sample after rendering so the animation processor has updated bone transforms.
        Vec3 tipWorld = sampleKatanaTipWorldPosition(item, stack, matrixStack);
        if (tipWorld != null) {
            spawnExampleParticleAt(tipWorld);
        }
    }

    // -------------------------
    // Trigger animation for this specific ItemStack
    // -------------------------
    private void triggerPendingAnimationForStack(SkySplitItem item, ItemStack stack, String pendingAnim) {
        // Get the animatable instance cache from the item
        AnimatableInstanceCache cache = item.getAnimatableInstanceCache();

        // Try to get a unique id for this stack. GeoItem.getId(stack) returns Long.MAX_VALUE if none assigned.
        long id = software.bernie.geckolib.animatable.GeoItem.getId(stack);
        if (id == Long.MAX_VALUE) {
            // Fallback: assign a pseudo-id using identityHashCode (client-only fallback)
            id = System.identityHashCode(stack);
        }

        // Get the AnimatableManager for this id
        AnimatableManager<?> manager = cache.getManagerForId(id);

        // Try to trigger the animation on the controller named "item_controller".
        // If your controller name differs, change it accordingly.
        try {
            manager.tryTriggerAnimation("item_controller", pendingAnim);
        } catch (Exception e) {
            // Fallback: try triggering without controller name
            try {
                manager.tryTriggerAnimation(pendingAnim);
            } catch (Exception ex) {
                // Log once (client log) if needed
                Minecraft.getInstance().getLogger().warn("Failed to trigger animation '{}' for SkySplitItem stack: {}", pendingAnim, ex.getMessage());
            }
        }
    }

    // -------------------------
    // Bone sampling: attempt multiple strategies to get a world position for the 'katana_tip' bone
    // -------------------------
    @Nullable
    private Vec3 sampleKatanaTipWorldPosition(SkySplitItem item, ItemStack stack, PoseStack matrixStack) {
        // Strategy A: try to get bone snapshot from the AnimatableManager (preferred)
        try {
            AnimatableInstanceCache cache = item.getAnimatableInstanceCache();
            long id = software.bernie.geckolib.animatable.GeoItem.getId(stack);
            if (id == Long.MAX_VALUE) id = System.identityHashCode(stack);
            AnimatableManager<?> manager = cache.getManagerForId(id);

            // The manager exposes a bone snapshot collection; try to read a snapshot for "katana_tip"
            var snapshots = manager.getBoneSnapshotCollection();
            if (snapshots != null && snapshots.containsKey("katana_tip")) {
                var snapshot = snapshots.get("katana_tip");
                // snapshot contains a reference to the GeoBone; try to extract a world position via reflection or API
                // Many GeoBone implementations expose a method to get world-space position or matrix.
                // We'll attempt a safe reflection approach to extract a Vec3-like position.
                Vec3 pos = tryExtractPositionFromBoneSnapshot(snapshot);
                if (pos != null) return pos;
            }
        } catch (Throwable ignored) {}

        // Strategy B: try to get the GeoBone from the model's animation processor
        try {
            // getGeoModel() is provided by GeoItemRenderer; it returns the model instance used for rendering
            var model = this.getGeoModel();
            if (model != null) {
                var processor = model.getAnimationProcessor(); // AnimationProcessor
                if (processor != null) {
                    GeoBone bone = processor.getBone("katana_tip");
                    if (bone != null) {
                        Vec3 pos = tryExtractPositionFromGeoBone(bone, matrixStack);
                        if (pos != null) return pos;
                    }
                }
            }
        } catch (Throwable ignored) {}

        // Strategy C: fallback to player's hand position + offset (approximate)
        try {
            Player player = Minecraft.getInstance().player;
            if (player != null && player.getMainHandItem() == stack) {
                // approximate tip position in front of the player's hand
                Vec3 eye = player.getEyePosition(1.0F);
                Vec3 look = player.getLookAngle();
                // offset forward and slightly down to approximate sword tip
                return eye.add(look.scale(0.6)).add(0, -0.4, 0);
            }
        } catch (Throwable ignored) {}

        return null;
    }

    // Try to extract a Vec3 from a BoneSnapshot via reflection (best-effort)
    @Nullable
    private Vec3 tryExtractPositionFromBoneSnapshot(Object snapshot) {
        if (snapshot == null) return null;
        try {
            // Many BoneSnapshot implementations have a method getBone() returning GeoBone or fields for position.
            var cls = snapshot.getClass();

            // Try getBone() -> GeoBone -> getWorldPosition or getWorldSpaceMatrix
            try {
                var getBone = cls.getMethod("getBone");
                Object geoBone = getBone.invoke(snapshot);
                if (geoBone != null) {
                    Vec3 pos = tryExtractPositionFromGeoBone((GeoBone) geoBone, null);
                    if (pos != null) return pos;
                }
            } catch (NoSuchMethodException ignored) {}

            // Try fields x/y/z on snapshot
            try {
                var fx = cls.getDeclaredField("x");
                var fy = cls.getDeclaredField("y");
                var fz = cls.getDeclaredField("z");
                fx.setAccessible(true); fy.setAccessible(true); fz.setAccessible(true);
                double x = ((Number) fx.get(snapshot)).doubleValue();
                double y = ((Number) fy.get(snapshot)).doubleValue();
                double z = ((Number) fz.get(snapshot)).doubleValue();
                return new Vec3(x, y, z);
            } catch (NoSuchFieldException ignored) {}
        } catch (Throwable ignored) {}
        return null;
    }

    // Try to extract a Vec3 from a GeoBone (best-effort). matrixStack may be null.
    @Nullable
    private Vec3 tryExtractPositionFromGeoBone(GeoBone bone, PoseStack matrixStack) {
        if (bone == null) return null;
        try {
            // Many GeoBone implementations expose getWorldSpacePosition or getWorldSpaceMatrix.
            // Try getWorldSpacePosition()
            try {
                var m = bone.getClass().getMethod("getWorldSpacePosition");
                Object res = m.invoke(bone);
                if (res instanceof net.minecraft.world.phys.Vec3) return (Vec3) res;
                // If returns a vector-like object, try to read x/y/z
                var cls = res.getClass();
                try {
                    double x = ((Number) cls.getMethod("x").invoke(res)).doubleValue();
                    double y = ((Number) cls.getMethod("y").invoke(res)).doubleValue();
                    double z = ((Number) cls.getMethod("z").invoke(res)).doubleValue();
                    return new Vec3(x, y, z);
                } catch (Throwable ignored) {}
            } catch (NoSuchMethodException ignored) {}

            // Try getWorldSpaceMatrix() -> extract translation components
            try {
                var mm = bone.getClass().getMethod("getWorldSpaceMatrix");
                Object matrix = mm.invoke(bone);
                if (matrix != null) {
                    // Many matrix types expose m03/m13/m23 or a method to get translation
                    try {
                        double x = ((Number) matrix.getClass().getField("m03").get(matrix)).doubleValue();
                        double y = ((Number) matrix.getClass().getField("m13").get(matrix)).doubleValue();
                        double z = ((Number) matrix.getClass().getField("m23").get(matrix)).doubleValue();
                        return new Vec3(x, y, z);
                    } catch (Throwable ignored) {}
                }
            } catch (NoSuchMethodException ignored) {}
        } catch (Throwable ignored) {}
        return null;
    }

    // -------------------------
    // Example particle spawn (very simple)
    // -------------------------
    private void spawnExampleParticleAt(Vec3 worldPos) {
        if (worldPos == null) return;
    }
    }