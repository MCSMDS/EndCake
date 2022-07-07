package com.mcsmds.endcake.fabric;

import com.mcsmds.endcake.EndCake;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class EndCakeBlock extends CakeBlock {

    public static final Block block = new EndCakeBlock();
    public static final Item item = new BlockItem(block, new Item.Settings().maxCount(1).group(ItemGroup.FOOD));

    protected EndCakeBlock() {
        super(AbstractBlock.Settings.of(Material.CAKE).strength(0.5f).sounds(BlockSoundGroup.GLASS));
    }

    public static void register() {
        Registry.register(Registry.BLOCK, new Identifier(EndCake.MODID, "end_cake"), block);
        Registry.register(Registry.ITEM, new Identifier(EndCake.MODID, "end_cake"), item);
        Item.BLOCK_ITEMS.put(block, item);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        if (world.isClient) {
            ItemStack itemStack = player.getStackInHand(hand);
            if (this.tryEat(state, world, pos, player, hand).isAccepted()) {
                return ActionResult.SUCCESS;
            }
            if (itemStack.isEmpty()) {
                return ActionResult.CONSUME;
            }
        }
        return this.tryEat(state, world, pos, player, hand);
    }

    private ActionResult tryEat(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand) {
        if (this.restoreEndcake(state, world, pos, player, hand)) {
            return ActionResult.SUCCESS;
        }
        if (!player.canConsume(false)) {
            return ActionResult.PASS;
        }
        if (!this.canTeleport(world, player)) {
            return ActionResult.PASS;
        }
        player.incrementStat(Stats.EAT_CAKE_SLICE);
        player.getHungerManager().add(2, 0.1f);
        int i = state.get(BITES);
        if (i < 6) {
            world.setBlockState(pos, (BlockState) state.with(BITES, i + 1), 3);
        } else {
            world.removeBlock(pos, false);
        }
        return ActionResult.SUCCESS;
    }

    private boolean restoreEndcake(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand) {
        int i = state.get(BITES);
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.getItem() == Items.ENDER_EYE) {
            if (i > 0) {
                world.setBlockState(pos, (BlockState) state.with(BITES, i - 1), 3);
                if (!player.isCreative()) {
                    itemStack.decrement(1);
                }
            }
            return true;
        }
        return false;
    }

    private boolean canTeleport(World world, PlayerEntity player) {
        if (world instanceof ServerWorld && !player.hasVehicle() && !player.hasPassengers()
                && player.canUsePortals()) {
            RegistryKey<World> registryKey = world.getRegistryKey() == World.END ? World.OVERWORLD : World.END;
            if (!player.isCreative() && world.getRegistryKey() == World.END) {
                return false;
            }
            ServerWorld serverWorld = ((ServerWorld) world).getServer().getWorld(registryKey);
            if (serverWorld == null) {
                return false;
            }
            player.moveToWorld(serverWorld);
            return true;
        }
        return false;
    }

}