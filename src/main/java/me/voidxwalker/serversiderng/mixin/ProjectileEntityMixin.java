package me.voidxwalker.serversiderng.mixin;

import me.voidxwalker.serversiderng.RNGHandler;
import me.voidxwalker.serversiderng.Speedrun;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin {
    @Shadow @Nullable public abstract Entity getOwner();

    private float serverSideRNG_divergence;
    /**
     * Obtains the divergence {@code Float}, so it can later be used in {@link ProjectileEntityMixin#modifyArrowRandom(Vec3d, double, double, double)} method.
     * @author Void_X_Walker
     */
    @Inject(method = "setVelocity",at = @At("HEAD"))
    public void getDivergence(double x, double y, double z, float speed, float divergence, CallbackInfo ci){
        if(Speedrun.inSpeedrun()){
            this.serverSideRNG_divergence=divergence;
        }
    }
    /**
     * Uses the from {@link RNGHandler#getRngValue(RNGHandler.RNGTypes)} obtained random {@code Long}, that has been generated by the verification server, as a seed for the {@link RNGHandler.RNGTypes#PROJECTILE} RNG.
     * @author Void_X_Walker
     */
    @Redirect(method = "setVelocity",at = @At(value = "INVOKE",target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d modifyArrowRandom(Vec3d instance, double x, double y, double z){
        if(Speedrun.inSpeedrun()){
            if(this.getOwner() instanceof PlayerEntity){
                Random random=new Random(Speedrun.currentSpeedrun.getCurrentRNGHandler().getRngValue(RNGHandler.RNGTypes.PROJECTILE));
                instance.add(random.nextGaussian() * 0.007499999832361937D * (double)serverSideRNG_divergence, random.nextGaussian() * 0.007499999832361937D * (double)serverSideRNG_divergence, random.nextGaussian() * 0.007499999832361937D * (double)serverSideRNG_divergence);
                return instance;
            }
        }
        return instance.add(x,y,z);
    }
}
