package me.voidxwalker.serversiderng.mixin;

import me.voidxwalker.serversiderng.RNGHandler;
import me.voidxwalker.serversiderng.ServerSideRng;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }
    /**
     * Uses the from {@link RNGHandler#getRngValue(RNGHandler.RNGTypes)} obtained random {@code Long}, that has been generated by verification server, as a seed for mob drop RNG.
     * Only mobs killed by the player are affected.
     * @author Void_X_Walker
     */
    @Inject(method = "dropLoot",at = @At(value = "INVOKE",target = "Lnet/minecraft/loot/LootTable;generateLoot(Lnet/minecraft/loot/context/LootContext;Ljava/util/function/Consumer;)V",shift = At.Shift.BEFORE),cancellable = true,locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void modifyMobRandom(DamageSource source, boolean causedByPlayer, CallbackInfo ci, Identifier identifier, LootTable lootTable, net.minecraft.loot.context.LootContext.Builder builder){
        if(ServerSideRng.inSpeedrun()){
            if(causedByPlayer){
                lootTable.generateLoot(builder.random(ServerSideRng.currentSpeedrun.getCurrentRNGHandler().getRngValue(RNGHandler.RNGTypes.MOB_DROP)).build(LootContextTypes.ENTITY), this::dropStack);
                ci.cancel();
            }
        }
    }
}