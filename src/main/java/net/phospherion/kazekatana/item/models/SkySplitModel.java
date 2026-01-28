package net.phospherion.kazekatana.item.models;

import net.minecraft.resources.ResourceLocation;
import net.phospherion.kazekatana.KazeKatana;
import net.phospherion.kazekatana.item.specialz.SkySplitItem;

public class SkySplitModel extends AnimatedGeoModel<SkySplitItem> {
    @Override
    public ResourceLocation getModelLocation(SkySplitItem object) {
        return ResourceLocation.fromNamespaceAndPath(KazeKatana.MOD_ID, "models/entity/sky_split.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(SkySplitItem object) {
        return ResourceLocation.fromNamespaceAndPath(KazeKatana.MOD_ID, "textures/entity/sky_split_texture.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(SkySplitItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(KazeKatana.MOD_ID, "animations/sky_split.animation.json");
    }
}

