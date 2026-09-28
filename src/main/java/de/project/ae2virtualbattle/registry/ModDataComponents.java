package de.project.ae2virtualbattle.registry;

import de.project.ae2virtualbattle.AE2VirtualBattle;
import de.project.ae2virtualbattle.cell.partition.BattleCellPartitionList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, AE2VirtualBattle.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BattleCellPartitionList>> PARTITIONS =
            DATA_COMPONENTS.register("partitions", () -> DataComponentType.<BattleCellPartitionList>builder()
                    .persistent(BattleCellPartitionList.CODEC)
                    .networkSynchronized(BattleCellPartitionList.STREAM_CODEC)
                    .build());
}
