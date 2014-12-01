package shootergame;

import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Buddy-1cent
 */
public abstract class GameObject implements IntGameObject {
    private Vector3f position;
    private Vector3f dynamic;
    private float width;
    private float height;
    private float depth;
    
    public GameObject(float x,float y,float z,float width, float height, float depth){
        position = new Vector3f(x, y, z);
        dynamic = new Vector3f(0, 0, 0);
        this.width = width;
        this.height = height;
        this.depth = depth;
    }
    
    @Override
    public void update(int delta){
        position.x += delta * dynamic.x;
        position.y += delta * dynamic.y;
        position.z += delta * dynamic.z;
    }
    @Override
    public void setPosition(float x,float y, float z){
        position = new Vector3f(x, y, z);
    }
    /* Location */ 
    @Override
    public void setX(float x){
        position.x = x;
    }
    @Override
    public void setY(float y){
        position.y = y;
    }
    @Override
    public void setZ(float z){
        position.z = z;
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
        return position.x;
    }
    @Override
    public float getY(){
        return position.y;
    }
    @Override
    public float getZ(){
        return position.z;
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
        dynamic.x = dx;
    }
    @Override
    public void setDY(float dy){
        dynamic.y = dy;
    }
    @Override
    public void setDZ(float dz){
        dynamic.z = dz;
    }
    @Override
    public float getDX(){
        return dynamic.x;
    }
    @Override
    public float getDY(){
        return dynamic.y;
    }
    @Override
    public float getDZ(){
        return dynamic.z;
    }
    
    @Override
    public boolean intersets(IntGameObject other){
        return false;
    }
}
