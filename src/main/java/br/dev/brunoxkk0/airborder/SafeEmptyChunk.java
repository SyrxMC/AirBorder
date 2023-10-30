package br.dev.brunoxkk0.airborder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import com.github.bsideup.jabel.Desugar;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SafeEmptyChunk extends Chunk {

    private static final Map<BlockMetaPair, ExtendedBlockStorage[]> blockCache = new ConcurrentHashMap<>();
    private final BlockMetaPair chunkData;

    public SafeEmptyChunk(World world, int chunkX, int chunkZ) {
        this(world, chunkX, chunkZ, Blocks.air, 0, false);
    }

    public SafeEmptyChunk(World world, int chunkX, int chunkZ, Block fillBlock, int fillBlockMeta,
        boolean fakeClientData) {
        super(world, chunkX, chunkZ);
        this.chunkData = new BlockMetaPair(fillBlock, fillBlockMeta);
        if (fakeClientData) {
            this.storageArrays = blockCache.computeIfAbsent(chunkData, (chunkData) -> {
                var data = new ExtendedBlockStorage[16];
                for (int x = 0; x < 16; ++x) {
                    for (int z = 0; z < 16; ++z) {
                        for (int y = 0; y < 255; ++y) {
                            int index = y >> 4;
                            if (data[index] == null) {
                                data[index] = new ExtendedBlockStorage(index << 4, true);
                            }

                            data[index].func_150818_a(x, y & 15, z, chunkData.block());
                            data[index].setExtBlockMetadata(x, y & 15, z, chunkData.meta());
                        }
                    }
                }
                return data;
            });
        }
    }

    /**
     * Returns the value in the height map at this x, z coordinate in the chunk
     */
    public int getHeightValue(int x, int z) {
        return 0;
    }

    public void generateSkylightMap() {}

    @SideOnly(Side.CLIENT)
    public void generateHeightMap() {}

    public Block getBlock(int p_150810_1_, int p_150810_2_, int p_150810_3_) {
        return this.chunkData.block();
    }

    public int func_150808_b(int p_150808_1_, int p_150808_2_, int p_150808_3_) {
        return 255;
    }

    public boolean func_150807_a(int p_150807_1_, int p_150807_2_, int p_150807_3_, Block p_150807_4_,
        int p_150807_5_) {
        return true;
    }

    public int getBlockMetadata(int p_76628_1_, int p_76628_2_, int p_76628_3_) {
        return this.chunkData.meta();
    }

    public boolean setBlockMetadata(int p_76589_1_, int p_76589_2_, int p_76589_3_, int p_76589_4_) {
        return false;
    }

    public int getSavedLightValue(EnumSkyBlock p_76614_1_, int p_76614_2_, int p_76614_3_, int p_76614_4_) {
        return 0;
    }

    public void setLightValue(EnumSkyBlock p_76633_1_, int p_76633_2_, int p_76633_3_, int p_76633_4_,
        int p_76633_5_) {}

    public int getBlockLightValue(int p_76629_1_, int p_76629_2_, int p_76629_3_, int p_76629_4_) {
        return 0;
    }

    public boolean canBlockSeeTheSky(int p_76619_1_, int p_76619_2_, int p_76619_3_) {
        return false;
    }

    public TileEntity func_150806_e(int p_150806_1_, int p_150806_2_, int p_150806_3_) {
        return null;
    }

    public void addTileEntity(TileEntity p_150813_1_) {}

    public void func_150812_a(int p_150812_1_, int p_150812_2_, int p_150812_3_, TileEntity p_150812_4_) {}

    public void removeTileEntity(int p_150805_1_, int p_150805_2_, int p_150805_3_) {}

    public void onChunkLoad() {}

    public void onChunkUnload() {}

    public void setChunkModified() {}

    public boolean needsSaving(boolean p_76601_1_) {
        return false;
    }

    public boolean isEmpty() {
        return true;
    }

    public boolean getAreLevelsEmpty(int p_76606_1_, int p_76606_2_) {
        return true;
    }
}

@Desugar
record BlockMetaPair(Block block, int meta) {}
