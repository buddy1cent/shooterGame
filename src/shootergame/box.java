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
public class box extends GameObject{
    
    private static int texture;
    private static int vertexSize;
    private static int amountOfVertices;
    private static int colorSize;      
    private static final float floorHeight=-1f;
    private static FloatBuffer vertexBox;
    private static FloatBuffer texCoordBox;
    
    public box(float x, float y, float z, float width, float height, float depth){
        super(x, y, z, width, height, depth);
        System.out.println("conX: " + this.getX() + " ,Y: " + this.getY() + " ,Z: " + this.getZ());
        //System.out.println("conX: " + this.getWidth() + " ,height: " + this.getHeight() + " ,depth: " + this.getDepth());
    }
    @Override
    public void render() {
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        //System.out.println("renX: " + this.getX() + " ,Y: " + this.getY() + " ,Z: " + this.getZ());
       
        glBindTexture(GL_TEXTURE_2D, texture);
        //glTranslatef(getX(), getY(), getZ());
        glVertexPointer(vertexSize, 0, vertexBox);
        glTexCoordPointer(colorSize, 0, texCoordBox);
        
        glDrawArrays(GL_QUADS, 0, amountOfVertices * 5);
        glTranslatef(getX(), getY(), getZ());
         
    }
    @Override
    public void getPosition() {
        System.out.println("getposX: " + this.getX() + " ,Y: " + this.getY() + " ,Z: " + this.getZ());
    }
    public void setTexture(int box) {
        this.texture = box;     
    }
    public void setGameParam(int vertexSize, int colorSize, int amountOfVertices, float texSize ) {
        this.vertexSize = vertexSize ; 
        this.colorSize = colorSize;
        this.amountOfVertices = amountOfVertices;
        
        vertexBox = BufferUtils.createFloatBuffer(amountOfVertices * vertexSize *5);
        vertexBox.put(new float[]{
            // North 
            this.getWidth(),  floorHeight, -this.getWidth(),
            this.getWidth(),  this.getHeight(), -this.getWidth(),
            -this.getWidth(), this.getHeight(), -this.getWidth(),
            -this.getWidth(), floorHeight, -this.getWidth(),
            // West 
            -this.getWidth(), floorHeight, -this.getWidth(),
            -this.getWidth(), this.getHeight(),  -this.getWidth(),
            -this.getWidth(), this.getHeight(),  this.getWidth(),
            -this.getWidth(), floorHeight, this.getWidth(),
            // East 
            this.getWidth(),  floorHeight, this.getWidth(),
            this.getWidth(),  this.getHeight(),   this.getWidth(),
            this.getWidth(),  this.getHeight(),  -this.getWidth(),
            this.getWidth(),  floorHeight, -this.getWidth(),
            // South 
            -this.getWidth(), floorHeight, this.getWidth(),
            -this.getWidth(), this.getHeight(),  this.getWidth(),
            this.getWidth(),  this.getHeight(),  this.getWidth(),
            this.getWidth(),  floorHeight, this.getWidth(),
            //top
            -this.getWidth(), this.getHeight(),  -this.getWidth(),
            this.getWidth(),  this.getHeight(),  -this.getWidth(),
            this.getWidth(),  this.getHeight(),  this.getWidth(),
            -this.getWidth(), this.getHeight(),  this.getWidth(),
            
        });
        vertexBox.flip();
        
        texCoordBox = BufferUtils.createFloatBuffer(amountOfVertices * colorSize * 5);
        texCoordBox.put(new float[]{
            0, 0, 0, 4*this.getWidth()*texSize, 4*this.getHeight()*texSize, 4*this.getWidth()*texSize, 4*this.getHeight()*texSize,0,
            0, 0, 0, 4*this.getWidth()*texSize, 4*this.getHeight()*texSize, 4*this.getWidth()*texSize, 4*this.getHeight()*texSize,0,
            0, 0, 0, 4*this.getWidth()*texSize, 4*this.getHeight()*texSize, 4*this.getWidth()*texSize, 4*this.getHeight()*texSize,0,
            0, 0, 0, 4*this.getWidth()*texSize, 4*this.getHeight()*texSize, 4*this.getWidth()*texSize, 4*this.getHeight()*texSize,0,
            0, 0, 4*this.getHeight()*texSize, 0, 4*this.getHeight()*texSize, 4*this.getDepth()*texSize, 0, 4*this.getDepth()*texSize,
        });
        texCoordBox.flip();    
    }
    
}
