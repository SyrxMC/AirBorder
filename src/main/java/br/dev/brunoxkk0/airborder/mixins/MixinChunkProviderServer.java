package br.dev.brunoxkk0.airborder.mixins;

import java.lang.reflect.Field;
import java.util.*;

import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.gen.ChunkProviderServer;

import org.bukkit.craftbukkit.v1_7_R4.util.LongHashSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import br.dev.brunoxkk0.airborder.api.AirBorderAPI;

@Mixin(value = ChunkProviderServer.class)
public abstract class MixinChunkProviderServer {

    @Shadow
    public abstract boolean chunkExists(int p_73149_1_, int p_73149_2_);

    @Shadow
    public WorldServer worldObj;

    @Shadow
    public LongHashMap loadedChunkHashMap;

    @Shadow
    public List loadedChunks;

    @Shadow
    public LongHashSet chunksToUnload;

    @Inject(
        method = "Lnet/minecraft/world/gen/ChunkProviderServer;loadChunk(IILjava/lang/Runnable;)Lnet/minecraft/world/chunk/Chunk;",
        at = @At("HEAD"),
        cancellable = true,
        remap = false)
    public void loadChunk(int x, int z, Runnable runnable, CallbackInfoReturnable<Chunk> cir) {
        if (!chunkExists(x, z)) {
            if (AirBorderAPI.getBorderProvider() != null && AirBorderAPI.getBorderProvider()
                .isOutsideBorder(x, z, worldObj.provider.dimensionId)) {

                Chunk chunk = new EmptyChunk(worldObj, x, z);

                loadedChunkHashMap.add(ChunkCoordIntPair.chunkXZ2Int(chunk.xPosition, chunk.zPosition), chunk);
                loadedChunks.add(chunk);

                System.out.println("[DEBUG] Sending an air chunk to " + x + " " + z);

                if (runnable != null) runnable.run();

                cir.setReturnValue(chunk);
            }
        }
    }

    @Inject(
        method = "Lnet/minecraft/world/gen/ChunkProviderServer;safeSaveExtraChunkData(Lnet/minecraft/world/chunk/Chunk;)V",
        at = @At("HEAD"),
        cancellable = true)
    public void saveChunkExtraData(Chunk chunk, CallbackInfo callbackInfo) {
        if (chunk instanceof EmptyChunk) callbackInfo.cancel();
    }

    @Inject(
        method = "Lnet/minecraft/world/gen/ChunkProviderServer;safeSaveChunk(Lnet/minecraft/world/chunk/Chunk;)V",
        at = @At("HEAD"),
        cancellable = true)
    public void saveChunkData(Chunk chunk, CallbackInfo callbackInfo) {
        if (chunk instanceof EmptyChunk) callbackInfo.cancel();
    }

    @Inject(
        method = "Lnet/minecraft/world/gen/ChunkProviderServer;unloadQueuedChunks()Z",
        at = @At("HEAD"),
        remap = true)
    public void unloadQueuedChunks(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {

        if (bukkitChunk == null) {
            try {
                bukkitChunk = Chunk.class.getDeclaredField("bukkitChunk");
                bukkitChunk.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }

        HashMap<Long, Chunk> markToRemove = new HashMap<>();

        for (Iterator it = chunksToUnload.iterator(); it.hasNext();) {
            Long chunk = (Long) it.next();
            Chunk empty = (Chunk) loadedChunkHashMap.getValueByKey(chunk);

            if (empty != null && getBukkitChunk(empty) == null) {
                markToRemove.put(chunk, (Chunk) loadedChunkHashMap.getValueByKey(chunk));
            }
        }

        loadedChunks.removeAll(markToRemove.values());

        for (Long chunk : markToRemove.keySet()) {
            loadedChunkHashMap.remove(chunk);
            chunksToUnload.remove(chunk);
        }
    }

    private static Field bukkitChunk;

    private Object getBukkitChunk(Chunk chunk) {

        if (bukkitChunk != null) {
            try {
                bukkitChunk.setAccessible(true);
                return bukkitChunk.get(chunk);
            } catch (IllegalAccessException ignored) {
                ignored.printStackTrace();
            }
        }

        return null;
    }

}
