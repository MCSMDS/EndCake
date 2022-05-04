package com.mcsmds.endcake;

import net.minecraft.block.AbstractBlock;
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

public class EndCakeBlock extends CakeBlock {

    public BlockItem blockItem = new BlockItem(this, new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_FOOD));

    public EndCakeBlock() {
        super(AbstractBlock.Properties.of(Material.CAKE).strength(0.5F).sound(SoundType.GLASS));
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockRayTraceResult blockraytraceresult) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (world.isClientSide) {
            if (this.eat(world, pos, state, player, itemstack).consumesAction()) {
                return ActionResultType.SUCCESS;
            }
            if (itemstack.isEmpty()) {
                return ActionResultType.CONSUME;
            }
        }
        return this.eat(world, pos, state, player, itemstack);
    }

    private ActionResultType eat(World world, BlockPos pos, BlockState state, PlayerEntity player,
            ItemStack itemstack) {
        int i = state.getValue(BITES);
        if (itemstack.getItem() == Items.ENDER_EYE && i > 0) {
            world.setBlock(pos, state.setValue(BITES, Integer.valueOf(i - 1)), 3);
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            return ActionResultType.SUCCESS;
        }
        if (!player.canEat(false)) {
            return ActionResultType.PASS;
        }
        if (world instanceof ServerWorld && !player.isPassenger() && !player.isVehicle()
                && player.canChangeDimensions()) {
            RegistryKey<World> registrykey = world.dimension() == World.END ? World.OVERWORLD : World.END;
            if (!player.isCreative()) {
                if (world.dimension() != World.END) {
                    registrykey = World.END;
                } else {
                    return ActionResultType.PASS;
                }
            }
            ServerWorld serverworld = ((ServerWorld) world).getServer().getLevel(registrykey);
            if (serverworld == null) {
                return ActionResultType.PASS;
            }
            player.changeDimension(serverworld);
        }
        player.awardStat(Stats.EAT_CAKE_SLICE);
        player.getFoodData().eat(2, 0.1F);
        if (i < 6) {
            world.setBlock(pos, state.setValue(BITES, Integer.valueOf(i + 1)), 3);
        } else {
            world.removeBlock(pos, false);
        }
        return ActionResultType.SUCCESS;
    }

}
