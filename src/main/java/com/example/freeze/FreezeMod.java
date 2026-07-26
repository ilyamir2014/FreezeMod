package net.example.freezemod;

import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

import java.util.UUID;

@Mod(FreezeMod.MOD_ID)
public class FreezeMod {
    public static final String MOD_ID = "tagmod";
    
    // Храним UUID текущего водящего
    public static UUID taggerUUID = null;

    public FreezeMod() {
        // Регистрируем наш класс в шине событий NeoForge
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerAttack(AttackEntityEvent event) {
        // Работаем только на сервере и только если цель — игрок
        if (!event.getEntity().level().isClientSide && event.getTarget() instanceof ServerPlayer target) {
            ServerPlayer attacker = (ServerPlayer) event.getEntity();

            // Проверка: атакующий — водящий и бьет палкой
            if (attacker.getUUID().equals(taggerUUID) && attacker.getMainHandItem().is(Items.STICK)) {
                transferTag(attacker, target);
                // Отменяем стандартный урон, чтобы убегающий не получал повреждений от удара
                event.setCanceled(true);
            }
        }
    }

    private void transferTag(ServerPlayer oldTagger, ServerPlayer newTagger) {
        taggerUUID = newTagger.getUUID();

        // 1. Настройки старого водящего
        oldTagger.setGlowingTag(false);
        // Забираем одну палку
        oldTagger.getInventory().clearOrCountMatchingItems(stack -> stack.is(Items.STICK), 1, oldTagger.inventoryMenu.getCraftSlots());

        // 2. Настройки нового водящего
        newTagger.setGlowingTag(true);
        newTagger.addItem(Items.STICK.getDefaultInstance());

        // 3. Заморозка на 10 секунд (200 тиков)
        // В NeoForge/Minecraft 1.21.1 эффекты замедления и слабости накладываются аналогично Forge
        newTagger.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 255, false, false));
        newTagger.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 255, false, false));

        // 4. Уведомления в чат
        oldTagger.sendSystemMessage(Component.literal("§aВы передали роль водящего!"));
        newTagger.sendSystemMessage(Component.literal("§cТеперь вы водите! Заморозка на 10 секунд."));
    }
}





