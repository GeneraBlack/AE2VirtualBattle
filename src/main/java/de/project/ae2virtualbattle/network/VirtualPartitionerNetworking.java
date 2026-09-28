package de.project.ae2virtualbattle.network;

import de.project.ae2virtualbattle.AE2VirtualBattle;
import de.project.ae2virtualbattle.menu.VirtualPartitionerMenu;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class VirtualPartitionerNetworking {
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(AE2VirtualBattle.MODID);
        registrar.playToServer(
                SetPartitionsPayload.TYPE,
                SetPartitionsPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    Player player = context.player();
                    if (player.containerMenu instanceof VirtualPartitionerMenu menu) {
                        menu.applyPartitions(player, payload.partitions());
                    }
                })
        );
    }
}
