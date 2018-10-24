package org.terasology.alternateWorldGenerator;

import com.fasterxml.uuid.Logger;
import javafx.geometry.Bounds;
import org.slf4j.LoggerFactory;
import org.terasology.math.AABB;
import org.terasology.math.ChunkMath;
import org.terasology.math.geom.BaseVector3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;
import org.terasology.world.generation.facets.SurfaceHeightFacet;

import java.util.Random;

public class AlternateWorldRasterizer implements WorldRasterizer {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AlternateWorldRasterizer.class);
    private Block dirt;
    private Block grass;
    private Block tallGrass;

    //sets initial values
    @Override
    public void initialize() {
        dirt = CoreRegistry.get(BlockManager.class).getBlock("Core:Dirt");
        grass = CoreRegistry.get(BlockManager.class).getBlock("Core:Grass");
        tallGrass = CoreRegistry.get(BlockManager.class).getBlock("Core:TallGrass1");
    }

    //generates the blocks
    //TODO: figure out why it's taking so long to do stuff
    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        Random rand = new Random();
        SurfaceHeightFacet surfaceHeightFacet = chunkRegion.getFacet(SurfaceHeightFacet.class);
        for (Vector3i position : chunkRegion.getRegion()) {
            float surfaceHeight = surfaceHeightFacet.getWorld(position.x, position.z);
            AABB boundsOfObj = chunk.getBlock(ChunkMath.calcBlockPos(position)).getBounds(position);
            if (position.y < surfaceHeight) {
                if (surfaceHeight<-5){
                    //TODO: figure out how to put water or something in this location
                    chunk.setBlock(ChunkMath.calcBlockPos(position), dirt);
                }
                else{
                    chunk.setBlock(ChunkMath.calcBlockPos(position), grass);
                    //possibly adds grass
                    if (rand.nextInt(9)>7) {
                        //TODO: test to see if putting grass here works
                        BaseVector3i positionOfNew = ChunkMath.calcBlockPos(position);
                        ((Vector3i) positionOfNew).y+=((int)boundsOfObj.maxY()-(int)boundsOfObj.minY());
                        //logger.info("positionOfNew: "+((Vector3i) positionOfNew).y);
                        chunkRegion.getRegion().expandToContain(positionOfNew);
                        if (positionOfNew.y() < 64) {
                            chunk.setBlock(positionOfNew,tallGrass);
                        }
                    }
                }

            }
        }
    }
}
