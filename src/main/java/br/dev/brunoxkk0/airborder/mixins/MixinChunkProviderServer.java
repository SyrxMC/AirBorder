package br.dev.brunoxkk0.airborder.mixins;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import br.dev.brunoxkk0.airborder.SafeEmptyChunk;

@Mixin(value = ChunkProviderServer.class)
public abstract class MixinChunkProviderServer {

    @Inject(
        method = "Lnet/minecraft/world/gen/ChunkProviderServer;safeSaveExtraChunkData(Lnet/minecraft/world/chunk/Chunk;)V",
        at = @At("HEAD"),
        cancellable = true)
    public void airborder$preventSaveChunkExtraData(Chunk chunk, CallbackInfo callbackInfo) {
        if (chunk instanceof SafeEmptyChunk) callbackInfo.cancel();
    }

    @Inject(
        method = "Lnet/minecraft/world/gen/ChunkProviderServer;safeSaveChunk(Lnet/minecraft/world/chunk/Chunk;)V",
        at = @At("HEAD"),
        cancellable = true)
    public void airborder$preventSaveChunkData(Chunk chunk, CallbackInfo callbackInfo) {
        if (chunk instanceof SafeEmptyChunk) callbackInfo.cancel();
    }
}
