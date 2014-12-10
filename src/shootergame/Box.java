package shootergame;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Buddy-1cent
 */
public class Box extends GameObject{
    
    private final float gap = 0.1f;
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
        
        //System.out.println("initX: " + this.getX() + " ,Y: " + this.getY() + " ,Z: " + this.getZ());
        //System.out.println("conX: " + this.getWidth() + " ,height: " + this.getHeight() + " ,depth: " + this.getDepth());
    }
    
    @Override
    public void render() {
        glDisable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        
        glBindTexture(GL_TEXTURE_2D, texture);
        
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        
        glVertexPointer(VERTEX_SIZE, 0, vertexBox);
        glTexCoordPointer(TEXTURE_COORD_SIZE, 0, texCoordBox);
        
        glDrawArrays(GL_QUADS, 0, AMOUNT_OF_VERTICES);  
    
        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);
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
    public Vector3f intersect(Vector3f position) {
        //jedna stena
        if(((position.z >= (-getZ()-getWidth()-gap)) && (position.z <= (-getZ()-getWidth()+0.01f)))   && (position.x < -getX() && position.x > (-getX()-getWidth()) )){
            
            position.z = -getZ()-getWidth()-gap;              
                    
        }
        if(position.z <= (-getZ()+gap) && (position.x < -getX() && position.x > (-getX()-getWidth())) ){
            if(position.z > (-getZ()-0.01f) ){
                position.z = -getZ()+gap;      
            }
        }
        if(position.x >= (-getX()-getWidth()-gap) && (position.z < -getZ() && position.z > (-getZ()-getWidth()) )){
                    if(position.x < (-getX()-getWidth()+0.01f)){
                        position.x = -getX()-getWidth()-gap;   
                    }
        }
        if(position.x <= (-getX()+gap) && (position.z < -getZ() && position.z > (-getZ()-getWidth()) )){
                    if(position.x > (-getX()-0.01f)){
                        position.x = -getX()+gap;   
                    }
        }
        System.out.println("X: " + position.x + " ,boxx: " + -getX() + "    ,Y: " + position.y + " ,boxY: "+ -getY() +"    ,Z: " + position.z + " ,boxZ: "+ -getZ() +  " ,width: "+getWidth());  
        return position;
    }

    @Override
    public Vector3f intersect() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
