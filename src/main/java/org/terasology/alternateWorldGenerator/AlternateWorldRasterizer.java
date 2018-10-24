package org.terasology.alternateWorldGenerator;

import com.fasterxml.uuid.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.math.ChunkMath;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;
import org.terasology.world.generation.facets.SurfaceHeightFacet;

public class AlternateWorldRasterizer implements WorldRasterizer {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AlternateWorldRasterizer.class);
    private Block dirt;
    private Block grass;

    //sets initial values
    @Override
    public void initialize() {
        dirt = CoreRegistry.get(BlockManager.class).getBlock("Core:Dirt");
        grass = CoreRegistry.get(BlockManager.class).getBlock("Core:Grass");
    }

    //generates the blocks
    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        SurfaceHeightFacet surfaceHeightFacet = chunkRegion.getFacet(SurfaceHeightFacet.class);
        for (Vector3i position : chunkRegion.getRegion()) {
            float surfaceHeight = surfaceHeightFacet.getWorld(position.x, position.z);
            if (position.y < surfaceHeight) {
                if (surfaceHeight<-5){
                    //TODO: figure out how to put water or something in this location
                    chunk.setBlock(ChunkMath.calcBlockPos(position), dirt);
                }
                else{
                    chunk.setBlock(ChunkMath.calcBlockPos(position), grass);
                }

            }
        }
    }
}
