package com.example.freeze;

import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

@Mod(FreezeMod.MODID)
public class FreezeMod {
    // Важно: имя переменной MODID без подчеркивания, чтобы FreezeModClient её видел!
    public static final String MODID = "freezemod";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    
    public static UUID taggerUUID = null;

    public FreezeMod() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerAttack(AttackEntityEvent event) {
        // ИСПРАВЛЕНИЕ 1: Добавили скобки isClientSide()
        if (!event.getEntity().level().isClientSide() && event.getTarget() instanceof ServerPlayer target) {
            ServerPlayer attacker = (ServerPlayer) event.getEntity();

            if (attacker.getUUID().equals(taggerUUID) && attacker.getMainHandItem().is(Items.STICK)) {
                transferTag(attacker, target);
                event.setCanceled(true);
            }
        }
    }

    private void transferTag(ServerPlayer oldTagger, ServerPlayer newTagger) {
        taggerUUID = newTagger.getUUID();

        oldTagger.setGlowingTag(false);
        oldTagger.getInventory().clearOrCountMatchingItems(stack -> stack.is(Items.STICK), 1, oldTagger.inventoryMenu.getCraftSlots());

        newTagger.setGlowingTag(true);
        newTagger.addItem(Items.STICK.getDefaultInstance());

        // ИСПРАВЛЕНИЕ 2: Заменили MOVEMENT_SLOWDOWN на SLOWNESS
        newTagger.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 200, 255, false, false));
        newTagger.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 255, false, false));

        oldTagger.sendSystemMessage(Component.literal("§aВы передали роль водящего!"));
        newTagger.sendSystemMessage(Component.literal("§cТеперь вы водите! Заморозка на 10 секунд."));
    }
}