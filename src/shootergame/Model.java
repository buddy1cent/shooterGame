/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shootergame;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Buddy-1cent
 */
public class Model{
    public List<Vector3f> vertices = new ArrayList<Vector3f>();
    public List<Vector3f> normals = new ArrayList<Vector3f>();
    public List<Face> faces = new ArrayList<Face>();
    
    public Model(){
        
    }
    
    public static class Face{
        public Vector3f vertex = new Vector3f();
        public Vector3f normal = new Vector3f();
        
        public Face(Vector3f vertex, Vector3f normal){
            this.vertex = vertex;
            this.normal = normal;
        }
    }
    
}
