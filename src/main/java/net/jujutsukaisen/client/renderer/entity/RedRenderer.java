package net.jujutsukaisen.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.jujutsukaisen.entity.RedEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import com.mojang.math.Axis;

public class RedRenderer extends EntityRenderer<RedEntity> {
        private static final ResourceLocation ORB_TEXTURE = ResourceLocation
                        .parse("jujutsukaisen:textures/entity/core_orb.png");

        public RedRenderer(EntityRendererProvider.Context context) {
                super(context);
        }

        @Override
        public void render(RedEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                        MultiBufferSource buffer, int packedLight) {
                poseStack.pushPose();

                Quaternionf cameraOrientation = this.entityRenderDispatcher.cameraOrientation();
                poseStack.mulPose(cameraOrientation);
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

                poseStack.scale(1.5f, 1.5f, 1.5f);

                int tickAge = entity.tickCount;
                int ticksPerFrame = 2;
                int currentFrame;

                int startupDuration = 8 * ticksPerFrame;
                float cols = 4.0f;
                float rows = 4.0f;
                ResourceLocation currentTexture = ORB_TEXTURE;

                if (tickAge < startupDuration) {
                        currentFrame = tickAge / ticksPerFrame;
                } else {
                        int loopingAge = tickAge - startupDuration;
                        currentFrame = 8 + ((loopingAge / ticksPerFrame) % 8);
                }

                float u0 = (currentFrame % (int) cols) / cols;
                float u1 = ((currentFrame % (int) cols) + 1.0f) / cols;
                float v0 = (currentFrame / (int) cols) / rows;
                float v1 = ((currentFrame / (int) cols) + 1.0f) / rows;

                VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(currentTexture));
                PoseStack.Pose posestack$pose = poseStack.last();
                Matrix4f pose = posestack$pose.pose();
                Matrix3f normal = posestack$pose.normal();

                int fullBright = LightTexture.FULL_BRIGHT;

                poseStack.pushPose();
                float pulse = 1.3f + 0.3f * (float) Math.sin(tickAge * 1.5f);
                poseStack.scale(pulse, pulse, pulse);
                PoseStack.Pose glowPose = poseStack.last();
                Matrix4f glowMatrix = glowPose.pose();

                vertexConsumer.vertex(glowMatrix, -0.5F, -0.5F, -0.01F).color(255, 50, 50, 150).uv(u0, v1)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(fullBright)
                                .normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                vertexConsumer.vertex(glowMatrix, 0.5F, -0.5F, -0.01F).color(255, 50, 50, 150).uv(u1, v1)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(fullBright)
                                .normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                vertexConsumer.vertex(glowMatrix, 0.5F, 0.5F, -0.01F).color(255, 50, 50, 150).uv(u1, v0)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(fullBright)
                                .normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                vertexConsumer.vertex(glowMatrix, -0.5F, 0.5F, -0.01F).color(255, 50, 50, 150).uv(u0, v0)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(fullBright)
                                .normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                poseStack.popPose();

                vertexConsumer.vertex(pose, -0.5F, -0.5F, 0.0F).color(255, 20, 20, 255).uv(u0, v1)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(fullBright)
                                .normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                vertexConsumer.vertex(pose, 0.5F, -0.5F, 0.0F).color(255, 20, 20, 255).uv(u1, v1)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(fullBright)
                                .normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                vertexConsumer.vertex(pose, 0.5F, 0.5F, 0.0F).color(255, 20, 20, 255).uv(u1, v0)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(fullBright)
                                .normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
                vertexConsumer.vertex(pose, -0.5F, 0.5F, 0.0F).color(255, 20, 20, 255).uv(u0, v0)
                                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(fullBright)
                                .normal(normal, 0.0F, 1.0F, 0.0F).endVertex();

                poseStack.popPose();
                super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }

        @Override
        public ResourceLocation getTextureLocation(RedEntity entity) {
                return ORB_TEXTURE;
        }
}
