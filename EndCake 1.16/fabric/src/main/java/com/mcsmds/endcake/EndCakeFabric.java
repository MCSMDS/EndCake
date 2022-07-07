package com.mcsmds.endcake;

import com.mcsmds.endcake.fabric.EndCakeBlock;

import net.fabricmc.api.ModInitializer;

public class EndCakeFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		EndCakeBlock.register();
	}
}