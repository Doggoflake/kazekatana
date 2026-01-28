package net.phospherion.kazekatana.screen.specialz;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.phospherion.kazekatana.KazeKatana; // adjust to your mod main class package/name
import net.minecraft.world.entity.player.Inventory;

public class TataraFurnaceScreen extends AbstractContainerScreen<TataraFurnaceMenu> {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(KazeKatana.MOD_ID, "textures/gui/tatarafurnace/tatarafurnacetexturebasic.png");
    private static final ResourceLocation HEATBAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(KazeKatana.MOD_ID, "textures/gui/tatarafurnace/tatarafurnacebaseheatbar.png");
    private static final ResourceLocation ARROW_TEXTURE = ResourceLocation.fromNamespaceAndPath(KazeKatana.MOD_ID, "textures/gui/tatarafurnace/arrow_progress.png");

    private float displayedHeat = 0f; // for interpolation

    public TataraFurnaceScreen(TataraFurnaceMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // Draw base GUI
        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // Read heat values via menu getters
        int actualHeat = menu.getHeat();
        int maxHeat = menu.getMaxHeat();

        // Interpolate displayedHeat toward actualHeat for smooth motion
        displayedHeat = Mth.lerp(0.12f, displayedHeat, actualHeat);

        // Draw heat bar (inner area: x+12,y+9 size 6x66)
        int barMaxHeight = 67;
        float percent = maxHeat > 0 ? (displayedHeat / (float) maxHeat) : 0f;
        percent = Mth.clamp(percent, 0f, 1f);
        int filled = (int) (percent * barMaxHeight);

        if (filled > 0) {
            RenderSystem.setShaderTexture(0, HEATBAR_TEXTURE);
            int drawX = x + 11;
            int drawY = y + 7 + (barMaxHeight - filled);
            int texY = (barMaxHeight - filled);
            // draw subregion of heatbar.png: width 6, height = filled, texture height = barMaxHeight
            guiGraphics.blit(HEATBAR_TEXTURE, drawX, drawY, 0, texY, 7, filled, 7, barMaxHeight);
        }

        // Draw progress arrow using ARROW_TEXTURE
        RenderSystem.setShaderTexture(0, ARROW_TEXTURE);

        int arrowWidth = 24;   // full width of your arrow texture
        int arrowHeight = 17;  // full height of your arrow texture

        int arrowX = x + 76;   // you will adjust these
        int arrowY = y + 29;

        int arrowProgress = menu.getScaledArrowProgress(); // 0..arrowWidth

        if (arrowProgress > 0) {
            // Draw only the left portion of the arrow (like vanilla furnace)
            guiGraphics.blit(
                    ARROW_TEXTURE,
                    arrowX, arrowY,       // screen position
                    0, 0,                 // texture U,V start
                    arrowProgress,        // width to draw
                    arrowHeight,          // height to draw
                    arrowWidth,           // full texture width
                    arrowHeight           // full texture height
            );
        }


    }

        @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // Do not call renderBackground(guiGraphics) directly to avoid signature mismatch across mappings.
        // super.render will draw the background and call renderBg for us.
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        // Draw tooltips and other overlays
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        // Heat tooltip (use menu getters)
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        if (mouseX >= x + 11 && mouseX <= x + 18 && mouseY >= y + 7  && mouseY <= y + 74) {

            int heat = menu.getHeat();
            int maxHeat = menu.getMaxHeat();
            guiGraphics.renderTooltip(this.font, Component.literal("Heat: " + heat + " / " + maxHeat), mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Title
        guiGraphics.drawString(this.font, "Tatara Furnace", 50, 6, 0x404040, false);
        // Player inventory label
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 26, this.imageHeight - 96 + 2, 0x404040, false);
    }
}

