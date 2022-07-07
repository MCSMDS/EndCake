package com.mcsmds.endcake.forge;

import com.mcsmds.endcake.EndCake;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

public class EndCakeBlock extends CakeBlock {

    public static final Block block = new EndCakeBlock();
    public static final Item item = new BlockItem(block, new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_FOOD));

    public EndCakeBlock() {
        super(AbstractBlock.Properties.of(Material.CAKE).strength(0.5F).sound(SoundType.GLASS));
    }

    public static void register() {
        ForgeRegistries.BLOCKS.register(block.setRegistryName(EndCake.MODID, "end_cake"));
        ForgeRegistries.ITEMS.register(item.setRegistryName(EndCake.MODID, "end_cake"));
        Item.BY_BLOCK.put(block, item);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockRayTraceResult hit) {
        if (world.isClientSide) {
            ItemStack itemstack = player.getItemInHand(hand);
            if (this.eat(state, world, pos, player, hand).consumesAction()) {
                return ActionResultType.SUCCESS;
            }
            if (itemstack.isEmpty()) {
                return ActionResultType.CONSUME;
            }
        }
        return this.eat(state, world, pos, player, hand);
    }

    private ActionResultType eat(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand) {
        if (this.restoreEndcake(state, world, pos, player, hand)) {
            return ActionResultType.SUCCESS;
        }
        if (!player.canEat(false)) {
            return ActionResultType.PASS;
        }
        if (!this.canTeleport(world, player)) {
            return ActionResultType.PASS;
        }
        player.awardStat(Stats.EAT_CAKE_SLICE);
        player.getFoodData().eat(2, 0.1F);
        int i = state.getValue(BITES);
        if (i < 6) {
            world.setBlock(pos, state.setValue(BITES, Integer.valueOf(i + 1)), 3);
        } else {
            world.removeBlock(pos, false);
        }
        return ActionResultType.SUCCESS;
    }

    private boolean restoreEndcake(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand) {
        int i = state.getValue(BITES);
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.getItem() == Items.ENDER_EYE) {
            if (i > 0) {
                world.setBlock(pos, state.setValue(BITES, Integer.valueOf(i - 1)), 3);
                if (!player.isCreative()) {
                    itemstack.shrink(1);
                }
            }
            return true;
        }
        return false;
    }

    private boolean canTeleport(World world, PlayerEntity player) {
        if (world instanceof ServerWorld && !player.isPassenger() && !player.isVehicle()
                && player.canChangeDimensions()) {
            RegistryKey<World> registrykey = world.dimension() == World.END ? World.OVERWORLD : World.END;
            if (!player.isCreative() && world.dimension() == World.END) {
                return false;
            }
            ServerWorld serverworld = ((ServerWorld) world).getServer().getLevel(registrykey);
            if (serverworld == null) {
                return false;
            }
            player.changeDimension(serverworld);
            return true;
        }
        return false;
    }

}