package com.mcsmds.endcake;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("endcake")
public class Main {
    private static final Logger LOGGER = LogManager.getLogger();

    @Mod.EventBusSubscriber(bus = Bus.MOD)
    public static class RegistryEvents {

        public static EndCakeBlock endcake = new EndCakeBlock();
        public static BlockItem endcakeitem = endcake.blockItem;

        @SubscribeEvent
        public static void onBlocksRegistry(Register<Block> blockRegistryEvent) {
            LOGGER.info("\033[32m---------- onBlocksRegistry ----------\033[0m");
            blockRegistryEvent.getRegistry().register(endcake.setRegistryName("end_cake"));
        }

        @SubscribeEvent
        public static void onItemsRegistry(Register<Item> blockRegistryEvent) {
            LOGGER.info("\033[32m---------- onItemsRegistry ----------\033[0m");
            blockRegistryEvent.getRegistry().register(endcakeitem.setRegistryName("end_cake"));
        }

    }
}
