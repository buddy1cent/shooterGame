package shootergame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import shootergame.Model.Face;

/**
 *
 * @author Buddy-1cent
 */
public class OBJLoader {
    
    public static Model load(String model) throws FileNotFoundException, IOException{
        BufferedReader reader = new BufferedReader(new FileReader(new File(model)));
        Model m = new Model();
        String line;
        while((line = reader.readLine()) != null){
            if(line.startsWith("v  ")){
                float x = Float.valueOf(line.split(" ")[2])/100;
                float y = Float.valueOf(line.split(" ")[3])/100;
                float z = Float.valueOf(line.split(" ")[4])/100;
                m.vertices.add(new Vector3f(x-10.5f, y-.25f, z-1f));   
            }
            if(line.startsWith("vn ")){
                float x = Float.valueOf(line.split(" ")[1])/100;
                float y = Float.valueOf(line.split(" ")[2])/100;
                float z = Float.valueOf(line.split(" ")[3])/100;
                m.normals.add(new Vector3f(x, y, z));
            }
            if(line.startsWith("vt ")){
                float x = Float.valueOf(line.split(" ")[1]);
                float y = Float.valueOf(line.split(" ")[2]);
                m.textures.add(new Vector2f(x,-y));
            }
            if(line.startsWith("f ")){
                float vx = Float.valueOf(line.split(" ")[1].split("/")[0]);
                float vy = Float.valueOf(line.split(" ")[2].split("/")[0]);
                float vz = Float.valueOf(line.split(" ")[3].split("/")[0]);
                Vector3f vertex = new Vector3f(vx,vy,vz);
                
                float tx = Float.valueOf(line.split(" ")[1].split("/")[1]);
                float ty = Float.valueOf(line.split(" ")[2].split("/")[1]);
                float tz = Float.valueOf(line.split(" ")[3].split("/")[1]);
                Vector3f texture = new Vector3f(tx,ty,tz);
                
                float nx = Float.valueOf(line.split(" ")[1].split("/")[2]);
                float ny = Float.valueOf(line.split(" ")[2].split("/")[2]);
                float nz = Float.valueOf(line.split(" ")[3].split("/")[2]);
                Vector3f normal = new Vector3f(nx+.5f,ny,nz);
                
                m.faces.add(new Model.Face(vertex,normal,texture)); 
            }
        }
        reader.close();
        return m;
    }
    
}
