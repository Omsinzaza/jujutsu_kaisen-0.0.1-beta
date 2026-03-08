package net.jujutsukaisen.init;

import net.jujutsukaisen.JujutsuMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundInit {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(
            ForgeRegistries.SOUND_EVENTS,
            JujutsuMod.MODID
    );

    public static final RegistryObject<SoundEvent> HOLLOW_PURPLE_START = SOUNDS.register(
            "hollow_purple_start",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(JujutsuMod.MODID, "hollow_purple_start"))
    );

    public static final RegistryObject<SoundEvent> HOLLOW_PURPLE_SHOOT = SOUNDS.register(
            "hollow_purple_shoot",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(JujutsuMod.MODID, "hollow_purple_shoot"))
    );
}