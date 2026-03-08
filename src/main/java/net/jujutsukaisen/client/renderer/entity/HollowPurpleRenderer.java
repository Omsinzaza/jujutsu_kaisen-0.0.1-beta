package net.jujutsukaisen.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.jujutsukaisen.entity.HollowPurpleEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class HollowPurpleRenderer extends EntityRenderer<HollowPurpleEntity> {
    private static final ResourceLocation ORB_TEXTURE = ResourceLocation.fromNamespaceAndPath("jujutsukaisen", "textures/entity/core_orb.png");

    public HollowPurpleRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(HollowPurpleEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        int tickAge = entity.getInternalTick();
        int state = entity.getEntityState();
        int light = LightTexture.FULL_BRIGHT;
        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucent(ORB_TEXTURE));

        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        int frameCount = 16;
        int frame = (entity.tickCount / 2) % frameCount;
        float u0 = (frame % 4) / 4.0f;
        float u1 = (frame % 4 + 1) / 4.0f;
        float v0 = (frame / 4) / 4.0f;
        float v1 = (frame / 4 + 1) / 4.0f;

        if (state == HollowPurpleEntity.STATE_CHARGING) {
            float progress = Math.min((float) tickAge / 324.0f, 1.0f);
            float offset = 5.0f * (1.0f - progress);

            poseStack.pushPose();
            poseStack.translate(-offset, 0, 0);
            poseStack.scale(2.5f, 2.5f, 2.5f);
            drawOrb(vc, poseStack, u0, u1, v0, v1, 20, 100, 255, light);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(offset, 0, 0);
            poseStack.scale(2.5f, 2.5f, 2.5f);
            drawOrb(vc, poseStack, u0, u1, v0, v1, 255, 20, 20, light);
            poseStack.popPose();

            if (progress > 0.4f) {
                float purpleScale = 8.0f * (progress - 0.4f) * 1.66f;
                poseStack.pushPose();
                poseStack.scale(purpleScale, purpleScale, purpleScale);
                drawOrb(vc, poseStack, u0, u1, v0, v1, 150, 0, 255, (int)(180 * progress), light);
                poseStack.popPose();
            }
        } else {

            float finalScale = 12.0f;
            poseStack.scale(finalScale, finalScale, finalScale);
            drawOrb(vc, poseStack, u0, u1, v0, v1, 150, 0, 255, 255, light);
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void drawOrb(VertexConsumer vc, PoseStack poseStack, float u0, float u1, float v0, float v1, int r, int g, int b, int light) {
        drawOrb(vc, poseStack, u0, u1, v0, v1, r, g, b, 255, light);
    }

    private void drawOrb(VertexConsumer vc, PoseStack poseStack, float u0, float u1, float v0, float v1, int r, int g, int b, int a, int light) {
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        vc.vertex(pose, -0.5f, -0.5f, 0).color(r, g, b, a).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 1, 0).endVertex();
        vc.vertex(pose, 0.5f, -0.5f, 0).color(r, g, b, a).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 1, 0).endVertex();
        vc.vertex(pose, 0.5f, 0.5f, 0).color(r, g, b, a).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 1, 0).endVertex();
        vc.vertex(pose, -0.5f, 0.5f, 0).color(r, g, b, a).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 1, 0).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(HollowPurpleEntity entity) {
        return ORB_TEXTURE;
    }
}
