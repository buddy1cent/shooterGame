package shootergame;

import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Buddy-1cent
 */
public interface IntGameObject {

    public void render();
    public void update(int delta);
    public void setPosition(float x,float y, float z);
    public void getPosition();
    /* Location */ 
    public void setX(float x);
    public void setY(float y);
    public void setZ(float z);
    public void setWidth(float width);
    public void setHeight(float height);
    public void setDepth(float depth);
    
    public float getX();
    public float getY();
    public float getZ();
    public float getWidth();
    public float getHeight();
    public float getDepth();
    
    /* Moving */
    public void setDX(float dx);
    public void setDY(float dy);
    public void setDZ(float dz);
    
    public float getDX();
    public float getDY();
    public float getDZ();
    
    public Vector3f intersect();
}
