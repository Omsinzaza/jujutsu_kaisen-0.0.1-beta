package net.jujutsukaisen.client.model;// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.jujutsukaisen.JujutsuMod;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class DivineDogsModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(JujutsuMod.MODID, "divinedogsmodel"), "main");
	private final ModelPart head;
	private final ModelPart mane;
	private final ModelPart body;
	private final ModelPart lf_leg;
	private final ModelPart rf_leg;
	private final ModelPart lb_leg;
	private final ModelPart rb_leg;
	private final ModelPart tail;

	public DivineDogsModel(ModelPart root) {
		this.head = root.getChild("head");
		this.mane = root.getChild("mane");
		this.body = root.getChild("body");
		this.lf_leg = root.getChild("lf_leg");
		this.rf_leg = root.getChild("rf_leg");
		this.lb_leg = root.getChild("lb_leg");
		this.rb_leg = root.getChild("rb_leg");
		this.tail = root.getChild("tail");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(26, 39).addBox(-1.0F, 1.0F, -3.25F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(18, 12).addBox(-2.0F, -1.0F, -1.25F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.003F))
		.texOffs(26, 36).addBox(1.0F, -2.25F, 0.5F, 1.0F, 1.5F, 1.0F, new CubeDeformation(0.001F))
		.texOffs(34, 39).addBox(-2.0F, -2.25F, 0.5F, 1.0F, 1.5F, 1.0F, new CubeDeformation(0.001F)), PartPose.offset(0.0F, 11.0F, -6.0F));

		PartDefinition mane = partdefinition.addOrReplaceChild("mane", CubeListBuilder.create(), PartPose.offset(0.0F, 12.75F, -3.5F));

		PartDefinition cube_r1 = mane.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 19).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.002F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 10).addBox(-2.0F, -2.8333F, 1.625F, 4.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-2.0F, -3.3333F, -2.875F, 4.0F, 5.0F, 5.0F, new CubeDeformation(0.003F))
		.texOffs(18, 6).addBox(-2.0F, 1.1667F, -3.125F, 4.0F, 1.0F, 5.0F, new CubeDeformation(0.004F))
		.texOffs(30, 26).addBox(-2.0F, -2.3333F, -3.625F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.002F))
		.texOffs(30, 31).addBox(-2.0F, -2.3333F, -4.625F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(18, 0).addBox(-2.0F, 0.6667F, 1.625F, 4.0F, 1.0F, 5.0F, new CubeDeformation(0.001F)), PartPose.offset(0.0F, 16.3333F, -1.375F));

		PartDefinition lf_leg = partdefinition.addOrReplaceChild("lf_leg", CubeListBuilder.create().texOffs(12, 19).addBox(-1.0F, 0.025F, -1.5F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 32).addBox(-1.0F, 4.025F, -0.75F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(32, 12).addBox(-1.0F, 8.025F, -1.75F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 14.975F, -2.5F));

		PartDefinition rf_leg = partdefinition.addOrReplaceChild("rf_leg", CubeListBuilder.create().texOffs(22, 19).addBox(-1.0F, 0.025F, -1.5F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(32, 16).addBox(-1.0F, 4.025F, -0.75F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(20, 32).addBox(-1.0F, 8.025F, -1.75F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 14.975F, -2.5F));

		PartDefinition lb_leg = partdefinition.addOrReplaceChild("lb_leg", CubeListBuilder.create().texOffs(36, 5).addBox(3.0F, 5.025F, -0.75F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(32, 22).addBox(3.0F, 8.025F, -1.75F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 14.975F, 4.0F));

		PartDefinition cube_r2 = lb_leg.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(36, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(4.0F, 4.775F, 0.0F, 0.5672F, 0.0F, 0.0F));

		PartDefinition cube_r3 = lb_leg.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 25).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 1.775F, -0.75F, -0.0873F, 0.0F, 0.0F));

		PartDefinition rb_leg = partdefinition.addOrReplaceChild("rb_leg", CubeListBuilder.create().texOffs(8, 37).addBox(-5.0F, 5.025F, -0.75F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(8, 33).addBox(-5.0F, 8.025F, -1.75F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 14.975F, 4.0F));

		PartDefinition cube_r4 = rb_leg.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(18, 36).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(-4.0F, 4.775F, 0.0F, 0.5672F, 0.0F, 0.0F));

		PartDefinition cube_r5 = rb_leg.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(10, 26).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 1.775F, -0.75F, -0.0873F, 0.0F, 0.0F));

		PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, 14.8314F, 4.9029F));

		PartDefinition cube_r6 = tail.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 38).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.6686F, 2.0971F, 0.48F, 0.0F, 0.0F));

		PartDefinition cube_r7 = tail.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(30, 35).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.6686F, 2.0971F, 0.48F, 0.0F, 0.0F));

		PartDefinition cube_r8 = tail.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(20, 26).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.9186F, 0.8471F, 0.48F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		mane.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		lf_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		rf_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		lb_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		rb_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		tail.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}