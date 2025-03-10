package me.voidxwalker.serversiderng.mixin.rng_verification;

import me.voidxwalker.serversiderng.RNGHandler;
import me.voidxwalker.serversiderng.RNGHandler.RNGTypes;
import me.voidxwalker.serversiderng.RNGSession;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(Block.class)
public class BlockMixin {
    /**
     * Uses the from {@link RNGHandler#getRngValue(RNGTypes)} obtained random {@code Long}, that has been generated by the {@code Verification-Server}, as a seed for the {@link RNGTypes#BLOCK_DROP} RNG.
     * Only blocks broken by the player are affected.
     * @see RNGHandler#getRngValue(RNGHandler.RNGTypes)
     * @author Void_X_Walker
     */
    @Inject(
            method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getDroppedStacks(Lnet/minecraft/loot/context/LootContext$Builder;)Ljava/util/List;", shift = At.Shift.BEFORE),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            cancellable = true
    )
    private static void modifyBlockDrops(
            BlockState state,
            ServerWorld world,
            BlockPos pos,
            BlockEntity blockEntity,
            Entity entity,
            ItemStack stack,
            CallbackInfoReturnable<List<ItemStack>> cir,
            net.minecraft.loot.context.LootContext.Builder builder
    ) {
        if (RNGSession.getInstance() != null) {
            if (entity instanceof PlayerEntity) {
                builder.random(RNGSession.getInstance().getCurrentRNGHandler().getRngValue(RNGTypes.BLOCK_DROP));
                cir.setReturnValue( state.getDroppedStacks(builder));
                cir.cancel();
            }
        }
    }
}
