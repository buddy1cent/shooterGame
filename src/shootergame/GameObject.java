package shootergame;

import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Buddy-1cent
 */
public abstract class GameObject implements IntGameObject {
    protected float x,y,z;
    protected float dx,dy,dz;
    protected float width;
    protected float height;
    protected float depth;
    
    public GameObject(float x,float y,float z,float width, float height, float depth){
        this.x = x;
        this.y = y;
        this.z = z;
        dx = dy = dz = 0;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }
    
    @Override
    public void update(int delta){
        x += delta * x;
        y += delta * y;
        z += delta * z;
    }
    @Override
    public void setPosition(float x,float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    /* Location */ 
    @Override
    public void setX(float x){
        this.x = x;
    }
    @Override
    public void setY(float y){
        this.y = y;
    }
    @Override
    public void setZ(float z){
        this.z = z;
    }
    @Override
    public void setWidth(float width){
        this.width = width;
    }
    @Override
    public void setHeight(float height){
        this.height = height;
    }
    @Override
    public void setDepth(float depth){
        this.depth = depth;
    }
    @Override
    public float getX(){
        return x;
    }
    @Override
    public float getY(){
        return y;
    }
    @Override
    public float getZ(){
        return z;
    }
    @Override
    public float getWidth(){
        return width;
    }
    @Override
    public float getHeight(){
        return height;
    }
    @Override
    public float getDepth(){
        return depth;
    }
    /* Moving */
    @Override
    public void setDX(float dx){
        this.dx = dx;
    }
    @Override
    public void setDY(float dy){
        this.dy = dy;
    }
    @Override
    public void setDZ(float dz){
        this.dz = dz;
    }
    @Override
    public float getDX(){
        return dx;
    }
    @Override
    public float getDY(){
        return dy;
    }
    @Override
    public float getDZ(){
        return dz;
    }
    
    public boolean intersets(GameObject other){
        
        return false;
    }
}
