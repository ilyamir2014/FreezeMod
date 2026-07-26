package com.example.freeze;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = FreezeMod.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = FreezeMod.MODID, value = Dist.CLIENT)
public class FreezeModClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        FreezeMod.LOGGER.info("HELLO FROM CLIENT SETUP");
        FreezeMod.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}