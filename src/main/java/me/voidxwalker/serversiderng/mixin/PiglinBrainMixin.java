package me.voidxwalker.serversiderng.mixin;

import me.voidxwalker.serversiderng.RNGHandler;
import me.voidxwalker.serversiderng.ServerSideRng;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.loot.context.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(PiglinBrain.class)
public class PiglinBrainMixin {
    /**
     * Uses the from {@link RNGHandler#getRngValue(RNGHandler.RNGTypes)} obtained random {@code Long}, that has been generated by verification server, as a seed for the piglin barter RNG.
     * @author Void_X_Walker
     */
    @Redirect(method = "getBarteredItem",at = @At(value = "INVOKE",target = "Lnet/minecraft/loot/context/LootContext$Builder;random(Ljava/util/Random;)Lnet/minecraft/loot/context/LootContext$Builder;"))
    private static LootContext.Builder modifyBarteredItem(LootContext.Builder instance, Random random){
        if(ServerSideRng.inSpeedrun()){
            return instance.random(ServerSideRng.currentSpeedrun.getCurrentRNGHandler().getRngValue(RNGHandler.RNGTypes.BARTER));
        }
        return instance.random(random);
    }
}