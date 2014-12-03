/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shootergame;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Buddy-1cent
 */
public class Room {
    private final List<Wall> walls = new ArrayList<Wall>();
    private final float gap = 0.4f;
    public void add(Wall w){
        w.initGL();
        walls.add(w);
    }
    public void render(){
        glEnable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BITMAP);
        
        for(Wall wall: walls)
            wall.render();
    }
    
    public static class Wall {
        private final float x;
        private final float y;
        private final float z;
        private final float width;
        private final float height;
        private final String facing;
        
        private static final int AMOUNT_OF_VERTICES = 4;
        private static final int VERTEX_SIZE = 3;
        private static final int TEXTURE_COORD_SIZE = 2;
        private static final float TEXTURE_SIZE = 1f;
        private float[] vertexCoords;
        private final int texture;
        private FloatBuffer vertexWall;
        private FloatBuffer texCoordWall;
        
        public  Wall(float x, float y, float z, float width, float height, String facing, int texture) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.width = width;
            this.height = height;
            this.facing = facing;
            this.texture = texture;
        }
        
        public float getX(){
            return x;
        }
        public float getY(){
            return y;
        }
        public float getZ(){
            return z;
        }
        public float getWidth(){
            return width;
        }
        public float getHeight(){
            return height;
        }
        public String getFacing(){
            return facing;
        }
        
        public void initGL(){
            if(facing.compareTo("North") == 0 || facing.compareTo("N") == 0){
                vertexCoords = new float [] {
                    x + width, y, z,
                    x + width, y + height, z,
                    x,y + height, z,
                    x, y, z,
                };
            }
            if(facing.compareTo("South") == 0 || facing.compareTo("S") == 0){
                vertexCoords = new float [] {
                    x, y, z,
                    x,y + height, z,
                    x + width, y + height, z,
                    x + width, y, z
                };
            }
            if(facing.compareTo("East") == 0 || facing.compareTo("E") == 0){
                vertexCoords = new float [] {
                    x, y, z + width,
                    x, y + height, z + width,
                    x,y + height, z,
                    x, y, z,     
                };
            }
            if(facing.compareTo("West") == 0 || facing.compareTo("W") == 0){
                vertexCoords = new float [] {
                    x, y, z,
                    x,y + height, z,
                    x, y + height, z + width,
                    x, y, z + width
                };
            }
            if(facing.compareTo("Ceiling") == 0 || facing.compareTo("C") == 0){
                vertexCoords = new float [] {
                    x, y, z,
                    x + height, y, z,
                    x + height, y, z + width,
                    x, y, z + width,
                };
            }
            if(facing.compareTo("Floor") == 0 || facing.compareTo("F") == 0 ){
                vertexCoords = new float [] {
                    x, y, z + width,
                    x + height, y, z + width,
                    x + height, y, z,
                    x, y, z,  
                };
            }

            // Vertices wall
            vertexWall = BufferUtils.createFloatBuffer(AMOUNT_OF_VERTICES * VERTEX_SIZE);
            vertexWall.put( vertexCoords );
            vertexWall.flip();

            // Texture coords walls
            texCoordWall = BufferUtils.createFloatBuffer(AMOUNT_OF_VERTICES * TEXTURE_COORD_SIZE);
            texCoordWall.put(new float[]{
                0, 0,
                0, height * TEXTURE_SIZE, 
                width * TEXTURE_SIZE, height * TEXTURE_SIZE, 
                width * TEXTURE_SIZE, 0,
            });
            texCoordWall.flip(); 
        }
        
        public void render(){
            glEnableClientState(GL_VERTEX_ARRAY);
            glEnableClientState(GL_TEXTURE_COORD_ARRAY);
            
            glBindTexture(GL_TEXTURE_2D, texture);
            glVertexPointer(VERTEX_SIZE, 0, vertexWall);
            glTexCoordPointer(TEXTURE_COORD_SIZE, 0, texCoordWall);
            glDrawArrays(GL_QUADS, 0, AMOUNT_OF_VERTICES);
            
            glDisableClientState(GL_VERTEX_ARRAY);
            glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        
            glBindTexture(GL_TEXTURE_2D, 0);
        }  
       
    }
    public Vector3f wallIntersect(Vector3f position){
            for(Wall wall: walls){
                //test zda je za zdi a kdyz je tak jeji testovani vynecha
                /*if(position.z < (-wall.getZ()+0.1f)){
                    //testuje jestli se jedna o North a jeji pozici plus jeji sirku
                    if((wall.facing.compareTo("North") == 0 || wall.facing.compareTo("N") == 0) && position.z >= (-wall.getZ()-gap) && (position.x > wall.getX() && position.x < (wall.getX()+wall.getWidth()) ) ){
                    position.z = -wall.getZ()-gap;
                    System.out.println("X: " + position.x + " ,wallx: " + wall.getX() + "    ,Y: " + position.y + " ,wallY: "+ wall.getY() +"    ,Z: " + position.z + " ,wallZ: "+ wall.getZ() + " ,facing: "+ wall.getFacing() +  " ,widht: "+ wall.getWidth());
                }}*/
                
                if((wall.facing.compareTo("North") == 0 || wall.facing.compareTo("N") == 0) && position.z >= (-wall.getZ()-gap) && (position.x > wall.getX() && position.x < (wall.getX()+wall.getWidth()) ) ){
                    if(position.z < (-wall.getZ()+0.1f)){
                        position.z = -wall.getZ()-gap;
                        System.out.println("X: " + position.x + " ,wallx: " + wall.getX() + "    ,Y: " + position.y + " ,wallY: "+ wall.getY() +"    ,Z: " + position.z + " ,wallZ: "+ wall.getZ() + " ,facing: "+ wall.getFacing() +  " ,widht: "+ wall.getWidth());
                    }
                } 
                else if((wall.facing.compareTo("South") == 0 || wall.facing.compareTo("S") == 0) && position.z <= (-wall.getZ()+gap) && (position.x > wall.getX() && position.x < (wall.getX()+wall.getWidth()) )){
                        if(position.z > (-wall.getZ()-0.1f)){
                        position.z = -wall.getZ()+gap;
                    }
                }
                else if((wall.facing.compareTo("East") == 0 || wall.facing.compareTo("E") == 0) && position.x <= (-wall.getX()+gap) && (position.z > wall.getZ() && position.z < (wall.getZ()+wall.getWidth()) )){
                    if(position.x > (-wall.getX()-0.1f)){
                        position.x = -wall.getX()+gap;
                        System.out.println("X: " + position.x + " ,wallx: " + wall.getX() + "    ,Y: " + position.y + " ,wallY: "+ wall.getY() +"    ,Z: " + position.z + " ,wallZ: "+ wall.getZ() + " ,facing: "+ wall.getFacing() +  " ,widht: "+ wall.getWidth());
                    }
                }
                else if((wall.facing.compareTo("West") == 0 || wall.facing.compareTo("W") == 0) && position.x >= (-wall.getX()-gap) && (position.z > wall.getZ() && position.z < (wall.getZ()+wall.getWidth()) )){
                    if(position.x < (-wall.getX()+0.1f)){
                        position.x = -wall.getX()-gap;
                    }
                }
                else if((wall.facing.compareTo("Ceiling") == 0 || wall.facing.compareTo("C") == 0) && position.y <= (-wall.getY()+gap)  /*&& !(position.z > wall.getZ() && position.z < (wall.getZ()+wall.getWidth()) && position.x > wall.getX() && position.x < (wall.getX()+wall.getWidth()))*/ ){
                    position.y = -wall.getY()+gap;
                }
                else if((wall.facing.compareTo("Floor") == 0 || wall.facing.compareTo("F") == 0) && position.y >= (-wall.getY()-gap) && (position.z > wall.getZ() && position.z < (wall.getZ()+wall.getWidth()) && position.x > wall.getX() && position.x < (wall.getX()+wall.getWidth()))){
                    position.y = -wall.getY()-gap;
                    System.out.println("X: " + position.x + " ,wallx: " + wall.getX() + "    ,Y: " + position.y + " ,wallY: "+ wall.getY() +"    ,Z: " + position.z + " ,wallZ: "+ wall.getZ() + " ,facing: "+ wall.getFacing() +  " ,height: "+ wall.getHeight());
                } 
            }    
            return position;
    }
}
