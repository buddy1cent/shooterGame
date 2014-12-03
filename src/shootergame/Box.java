/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shootergame;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertexPointer;

/**
 *
 * @author Buddy-1cent
 */
public class Box extends GameObject{
    
    private final int texture;
    private final float TEXTURE_SIZE;
    private final int VERTEX_SIZE = 3;
    private final int AMOUNT_OF_VERTICES = 24;
    private final int TEXTURE_COORD_SIZE = 2;      
    private FloatBuffer vertexBox;
    private FloatBuffer texCoordBox;
    
    public Box(float x, float y, float z, float width, float height, float depth,float texSize, int texture){
        super(x, y, z, width, height*1.5f, depth);
        this.texture = texture;
        TEXTURE_SIZE = texSize;
        
        System.out.println("initX: " + this.getX() + " ,Y: " + this.getY() + " ,Z: " + this.getZ());
        //System.out.println("conX: " + this.getWidth() + " ,height: " + this.getHeight() + " ,depth: " + this.getDepth());
    }
    
    @Override
    public void render() {
        glDisable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        
        glBindTexture(GL_TEXTURE_2D, texture);

        glVertexPointer(VERTEX_SIZE, 0, vertexBox);
        glTexCoordPointer(TEXTURE_COORD_SIZE, 0, texCoordBox);
        
        glDrawArrays(GL_QUADS, 0, AMOUNT_OF_VERTICES);  
    }
    
    @Override
    public void getPosition() {
        System.out.println("getposX: " + this.getX() + " ,Y: " + this.getY() + " ,Z: " + this.getZ());
    }
    public void initGL() {
        
        vertexBox = BufferUtils.createFloatBuffer(AMOUNT_OF_VERTICES * VERTEX_SIZE);
        vertexBox.put(new float[]{
            // North 
            x, y, z,
            x, y + height, z,
            x + width, y + height, z,
            x + width, y, z,

            // West 
            x, y, z,
            x, y + height, z,
            x, y + height, z + depth,
            x, y, z + depth,

            // East 
            x + width, y, z,
            x + width, y + height, z,
            x + width, y + height, z + depth,
            x + width, y, z + depth,

            // South 
            x, y, z + depth,
            x, y + height, z + depth,
            x + width, y + height, z + depth,
            x + width, y, z + depth,
            
            //top
            x, y + height, z,
            x, y + height, z + depth,
            x + width, y + height, z +depth,
            x + width, y + height, z, 
            
            //bottom
            x, y, z,
            x, y, z + depth,
            x + width, y, z +depth,
            x + width, y, z,
            
                      
        });
        vertexBox.flip();

        texCoordBox = BufferUtils.createFloatBuffer(AMOUNT_OF_VERTICES * TEXTURE_COORD_SIZE);
        texCoordBox.put(new float[]{
            0, 0, 0, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, 0,
            0, 0, 0, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE,0,
            0, 0, 0, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE,0,
            0, 0, 0, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE,0,
            0, 0, TEXTURE_SIZE, 0, TEXTURE_SIZE, TEXTURE_SIZE, 0, TEXTURE_SIZE,
            0, 0, TEXTURE_SIZE, 0, TEXTURE_SIZE, TEXTURE_SIZE, 0, TEXTURE_SIZE,
        });
        texCoordBox.flip();    
    }

    @Override
    public boolean intersets() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
