package net.jujutsukaisen.init;

import net.jujutsukaisen.JujutsuMod;
import net.jujutsukaisen.entity.CursedBlastEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityInit {
        public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(
                        ForgeRegistries.ENTITY_TYPES,
                        JujutsuMod.MODID);

        public static final RegistryObject<EntityType<CursedBlastEntity>> CURSED_BLAST = ENTITIES.register(
                        "cursed_blast",
                        () -> EntityType.Builder.<CursedBlastEntity>of(CursedBlastEntity::new, MobCategory.MISC)
                                        .sized(0.5f, 0.5f)
                                        .build("cursed_blast"));

        public static final RegistryObject<EntityType<net.jujutsukaisen.entity.BlueEntity>> BLUE = ENTITIES.register(
                        "blue",
                        () -> EntityType.Builder.<net.jujutsukaisen.entity.BlueEntity>of(
                                        net.jujutsukaisen.entity.BlueEntity::new, MobCategory.MISC)
                                        .sized(1.0f, 1.0f)
                                        .clientTrackingRange(10)
                                        .updateInterval(1)
                                        .build("blue"));

        public static final RegistryObject<EntityType<net.jujutsukaisen.entity.RedEntity>> RED = ENTITIES.register(
                        "red",
                        () -> EntityType.Builder.<net.jujutsukaisen.entity.RedEntity>of(
                                        net.jujutsukaisen.entity.RedEntity::new, MobCategory.MISC)
                                        .sized(0.5f, 0.5f)
                                        .clientTrackingRange(10)
                                        .updateInterval(1)
                                        .build("red"));

        public static final RegistryObject<EntityType<net.jujutsukaisen.entity.HollowPurpleEntity>> HOLLOW_PURPLE = ENTITIES
                        .register(
                                        "hollow_purple",
                                        () -> EntityType.Builder.<net.jujutsukaisen.entity.HollowPurpleEntity>of(
                                                        net.jujutsukaisen.entity.HollowPurpleEntity::new,
                                                        MobCategory.MISC)
                                                        .sized(2.0f, 2.0f)
                                                        .clientTrackingRange(20)
                                                        .updateInterval(1)
                                                        .build("hollow_purple"));

        public static final RegistryObject<EntityType<net.jujutsukaisen.entity.DivineDogEntity>> DIVINE_DOG = ENTITIES
                        .register(
                                        "divine_dog",
                                        () -> EntityType.Builder.<net.jujutsukaisen.entity.DivineDogEntity>of(
                                                        net.jujutsukaisen.entity.DivineDogEntity::new,
                                                        MobCategory.CREATURE)
                                                        .sized(0.6f, 0.85f)
                                                        .clientTrackingRange(10)
                                                        .updateInterval(3)
                                                        .build("divine_dog"));
}
