package me.voidxwalker.serversiderng.mixin;

import me.voidxwalker.serversiderng.RNGHandler;
import me.voidxwalker.serversiderng.Speedrun;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.HoldingPatternPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(HoldingPatternPhase.class)
public class HoldingPatternPhaseMixin {
    /**
     * Uses the from {@link RNGHandler#getRngValue(RNGHandler.RNGTypes)} obtained random {@code Long}, that has been generated by the verification server, as a seed for the {@link RNGHandler.RNGTypes#ENDER_DRAGON_LANDING_APPROACH} RNG.
     * @author Void_X_Walker
     */
    @Redirect(method = "method_6841",at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;getRandom()Ljava/util/Random;"))
    public Random modifyDragonLandingPhase(EnderDragonEntity instance){
        if(Speedrun.inSpeedrun()){
            return new Random(Speedrun.currentSpeedrun.getCurrentRNGHandler().getRngValue(RNGHandler.RNGTypes.ENDER_DRAGON_LANDING_APPROACH));
        }
        return instance.getRandom();
    }
    /**
     * Uses the from {@link RNGHandler#getRngValue(RNGHandler.RNGTypes)} obtained random {@code Long}, that has been generated by the verification server, as a seed for the {@link RNGHandler.RNGTypes#ENDER_DRAGON_TARGET_HEIGHT} RNG.
     * @author Void_X_Walker
     */
    @Redirect(method = "method_6842",at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;getRandom()Ljava/util/Random;"))
    public Random modifyTargetHeight(EnderDragonEntity instance){
        if(Speedrun.inSpeedrun()){
            return new Random(Speedrun.currentSpeedrun.getCurrentRNGHandler().getRngValue(RNGHandler.RNGTypes.ENDER_DRAGON_TARGET_HEIGHT));
        }
        return instance.getRandom();
    }
}
