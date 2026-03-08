package net.jujutsukaisen.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.jujutsukaisen.JujutsuMod;
import net.jujutsukaisen.client.model.DivineDogsModel;
import net.jujutsukaisen.entity.DivineDogEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DivineDogRenderer extends MobRenderer<DivineDogEntity, DivineDogsModel<DivineDogEntity>> {

    private static final ResourceLocation TEXTURE_BLACK = ResourceLocation.fromNamespaceAndPath(
            JujutsuMod.MODID, "textures/entity/black_wolf.png");
    private static final ResourceLocation TEXTURE_WHITE = ResourceLocation.fromNamespaceAndPath(
            JujutsuMod.MODID, "textures/entity/white_wolf.png");

    public DivineDogRenderer(EntityRendererProvider.Context context) {
        super(context, new DivineDogsModel<>(context.bakeLayer(DivineDogsModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(DivineDogEntity entity) {
        return entity.isWhite() ? TEXTURE_WHITE : TEXTURE_BLACK;
    }

    @Override
    public void render(DivineDogEntity entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        if (entity.isSummoning()) {
            return;
        }

        poseStack.pushPose();
        poseStack.scale(1.75F, 1.75F, 1.75F);
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
