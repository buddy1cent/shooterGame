package shootergame;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Buddy-1cent
 */
public class Model{
    public List<Vector3f> vertices = new ArrayList<Vector3f>();
    public List<Vector3f> normals = new ArrayList<Vector3f>();
    public List<Vector2f> textures = new ArrayList<Vector2f>();
    public List<Face> faces = new ArrayList<Face>();
    
    public Model(){
        
    }
    
    public static class Face{
        public Vector3f vertexIndices = new Vector3f();
        public Vector3f normalIndices = new Vector3f();
        public Vector3f textureIndices = new Vector3f();
        
        public Face(Vector3f vertexIndicies, Vector3f normalIndices, Vector3f textureIndices){
            this.vertexIndices = vertexIndicies;
            this.normalIndices = normalIndices;
            this.textureIndices = textureIndices;
        }
    }
    
}
