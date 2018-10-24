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
    private Block water;

    //sets initial values
    @Override
    public void initialize() {
        dirt = CoreRegistry.get(BlockManager.class).getBlock("Core:Dirt");
        grass = CoreRegistry.get(BlockManager.class).getBlock("Core:Grass");
        tallGrass = CoreRegistry.get(BlockManager.class).getBlock("Core:TallGrass1");
        water = CoreRegistry.get(BlockManager.class).getBlock("Core:Water");
    }

    //generates the blocks
    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        Random rand = new Random();
        SurfaceHeightFacet surfaceHeightFacet = chunkRegion.getFacet(SurfaceHeightFacet.class);
        BaseVector3i heightInWorld = ChunkMath.calcBlockPos(new Vector3i(0, -5, 0));
        for (Vector3i position : chunkRegion.getRegion()) {
            float surfaceHeight = surfaceHeightFacet.getWorld(position.x, position.z);
            AABB boundsOfObj = chunk.getBlock(ChunkMath.calcBlockPos(position)).getBounds(position);
            if (position.y < surfaceHeight) {
                if (surfaceHeight<-5){
                    chunk.setBlock(ChunkMath.calcBlockPos(position), dirt);
                    //adds water where it should
                    for (float i=position.y; i<surfaceHeight; i+= (boundsOfObj.maxY()-boundsOfObj.minY())) {
                        BaseVector3i positionOfNew0 = ChunkMath.calcBlockPos(position);
                        BaseVector3i positionOfNew1 = positionOfNew0;
                        ((Vector3i) positionOfNew1).y+=i*((int)boundsOfObj.maxY()-(int)boundsOfObj.minY());
                        positionOfNew1 = ChunkMath.calcBlockPos((Vector3i)positionOfNew1);
                        ((Vector3i) positionOfNew1).setX(positionOfNew0.x());
                        ((Vector3i) positionOfNew1).setZ(positionOfNew0.z());
                        chunkRegion.getRegion().expandToContain(positionOfNew1);
                        if (positionOfNew1.y() < heightInWorld.y()) {
                            chunk.setBlock(positionOfNew1,water);
                        }
                    }
                }
                else{
                    chunk.setBlock(ChunkMath.calcBlockPos(position), grass);
                    //possibly adds grass
                    if (rand.nextInt(9)>7) {
                        BaseVector3i positionOfNew = ChunkMath.calcBlockPos(position);
                        ((Vector3i) positionOfNew).y+=((int)boundsOfObj.maxY()-(int)boundsOfObj.minY());
                        chunkRegion.getRegion().expandToContain(positionOfNew);
                        if (positionOfNew.y() < 64) {
                            chunk.setBlock(positionOfNew,tallGrass);
                        }
                    }
                    //random chance for dirt
                    if (rand.nextInt(50)>48) {
                        chunk.setBlock(ChunkMath.calcBlockPos(position), dirt);
                    }
                }

            }
        }
    }
}
