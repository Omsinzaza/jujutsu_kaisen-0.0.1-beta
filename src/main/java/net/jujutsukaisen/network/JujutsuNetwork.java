package net.jujutsukaisen.network;

import net.jujutsukaisen.network.packet.SyncCursedEnergyS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class JujutsuNetwork {
        private static final String PROTOCOL_VERSION = "1";
        public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
                        ResourceLocation.fromNamespaceAndPath("jujutsukaisen", "main"),
                        () -> PROTOCOL_VERSION,
                        PROTOCOL_VERSION::equals,
                        PROTOCOL_VERSION::equals);

        public static void register() {
                int id = 0;
                INSTANCE.messageBuilder(SyncCursedEnergyS2CPacket.class, id++,
                                net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT)
                                .encoder(SyncCursedEnergyS2CPacket::toBytes)
                                .decoder(SyncCursedEnergyS2CPacket::new)
                                .consumerMainThread(SyncCursedEnergyS2CPacket::handle)
                                .add();

                INSTANCE.messageBuilder(net.jujutsukaisen.network.packet.SwitchMoveC2SPacket.class, id++,
                                net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER)
                                .encoder(net.jujutsukaisen.network.packet.SwitchMoveC2SPacket::toBytes)
                                .decoder(net.jujutsukaisen.network.packet.SwitchMoveC2SPacket::new)
                                .consumerMainThread(net.jujutsukaisen.network.packet.SwitchMoveC2SPacket::handle)
                                .add();

                INSTANCE.messageBuilder(net.jujutsukaisen.network.packet.CastMoveC2SPacket.class, id++,
                                net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER)
                                .encoder(net.jujutsukaisen.network.packet.CastMoveC2SPacket::toBytes)
                                .decoder(net.jujutsukaisen.network.packet.CastMoveC2SPacket::new)
                                .consumerMainThread(net.jujutsukaisen.network.packet.CastMoveC2SPacket::handle)
                                .add();

                INSTANCE.messageBuilder(net.jujutsukaisen.network.packet.MeleeComboC2SPacket.class, id++,
                                net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER)
                                .encoder(net.jujutsukaisen.network.packet.MeleeComboC2SPacket::toBytes)
                                .decoder(net.jujutsukaisen.network.packet.MeleeComboC2SPacket::new)
                                .consumerMainThread(net.jujutsukaisen.network.packet.MeleeComboC2SPacket::handle)
                                .add();

                INSTANCE.messageBuilder(net.jujutsukaisen.network.packet.DashC2SPacket.class, id++,
                                net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER)
                                .encoder(net.jujutsukaisen.network.packet.DashC2SPacket::toBytes)
                                .decoder(net.jujutsukaisen.network.packet.DashC2SPacket::new)
                                .consumerMainThread(net.jujutsukaisen.network.packet.DashC2SPacket::handle)
                                .add();
        }
}