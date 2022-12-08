package me.voidxwalker.serversiderng.mixin;

import me.voidxwalker.serversiderng.RNGHandler;
import me.voidxwalker.serversiderng.Speedrun;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ServerWorldProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    private int serverSideRNG_savedThunderTime;
    @Shadow @Final private ServerWorldProperties worldProperties;

    protected ServerWorldMixin(MutableWorldProperties mutableWorldProperties, RegistryKey<World> registryKey, RegistryKey<DimensionType> registryKey2, DimensionType dimensionType, Supplier<Profiler> profiler, boolean bl, boolean bl2, long l) {
        super(mutableWorldProperties, registryKey, registryKey2, dimensionType, profiler, bl, bl2, l);
    }


    @Inject(method = "tick",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/level/ServerWorldProperties;setRaining(Z)V"))
    public void modifyThunderRandom(CallbackInfo ci){
        if(Speedrun.inSpeedrun()){
            if(serverSideRNG_savedThunderTime!=0){
                this.worldProperties.setThunderTime(serverSideRNG_savedThunderTime);
            }

        }
        serverSideRNG_savedThunderTime =0;
    }
    /**
     * Uses the from {@link RNGHandler#getRngValue(RNGHandler.RNGTypes)} obtained random {@code Long}, that has been generated by the verification server, as a seed for the {@link RNGHandler.RNGTypes#THUNDER} RNG.
     * @author Void_X_Walker
     */
    @Inject(method = "tick",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/MutableWorldProperties;isRaining()Z"))
    public void getRandom(BooleanSupplier shouldKeepTicking, CallbackInfo ci){
        if(Speedrun.inSpeedrun()){
            int i = this.worldProperties.getClearWeatherTime();
            int j = this.worldProperties.getThunderTime();
            boolean bl2 = this.properties.isThundering();
            if (i <= 0&&j <=0) {
                Random random=new Random(Speedrun.currentSpeedrun.getCurrentRNGHandler().getRngValue(RNGHandler.RNGTypes.THUNDER));
                serverSideRNG_savedThunderTime=bl2 ? random.nextInt(12000) + 3600 : random.nextInt(168000) + 12000;
                return;
            }
        }
        serverSideRNG_savedThunderTime=0;

    }
}
