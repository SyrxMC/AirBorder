package br.dev.brunoxkk0.airborder.mixins;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import br.dev.brunoxkk0.airborder.SafeEmptyChunk;
import br.dev.brunoxkk0.airborder.api.AirBorderAPI;

@Mixin(AnvilChunkLoader.class)
public class MixinAnvilChunkLoader {

    @Inject(
        method = "Lnet/minecraft/world/chunk/storage/AnvilChunkLoader;chunkExists(Lnet/minecraft/world/World;II)Z",
        at = @At("HEAD"),
        cancellable = true,
        remap = false)
    public void airborder$chunkExists(World world, int chunkX, int chunkZ,
        CallbackInfoReturnable<Boolean> callbackInfo) {
        if (AirBorderAPI.getBorderProvider() != null && AirBorderAPI.getBorderProvider()
            .isOutsideBorder(chunkX, chunkZ, world.provider.dimensionId)) {
            callbackInfo.setReturnValue(true);
        }
    }

    @Inject(
        method = "Lnet/minecraft/world/chunk/storage/AnvilChunkLoader;loadChunk__Async(Lnet/minecraft/world/World;II)[Ljava/lang/Object;",
        at = @At("HEAD"),
        cancellable = true,
        remap = false)
    public void airborder$createFakeChunk(World world, int chunkX, int chunkZ,
        CallbackInfoReturnable<Object[]> callbackInfo) {
        if (AirBorderAPI.getBorderProvider() != null && AirBorderAPI.getBorderProvider()
            .isOutsideBorder(chunkX, chunkZ, world.provider.dimensionId)) {

            var fakeLevelNbt = new NBTTagCompound();
            var fakeChunkNbt = new NBTTagCompound();

            fakeChunkNbt.setTag("Level", fakeLevelNbt);

            callbackInfo.setReturnValue(
                new Object[] { new SafeEmptyChunk(world, chunkX, chunkZ, Blocks.wool, 5, true), fakeChunkNbt });
        }
    }
}
