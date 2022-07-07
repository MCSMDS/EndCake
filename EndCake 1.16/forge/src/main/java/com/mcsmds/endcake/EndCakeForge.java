package com.mcsmds.endcake;

import com.mcsmds.endcake.forge.EndCakeBlock;

import net.minecraftforge.fml.common.Mod;

@Mod(EndCake.MODID)
public class EndCakeForge {
    public EndCakeForge() {
        EndCakeBlock.register();
    }
}