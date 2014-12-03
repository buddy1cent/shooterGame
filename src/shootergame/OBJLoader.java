package shootergame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.lwjgl.util.vector.Vector3f;
import shootergame.Model.Face;

/**
 *
 * @author Buddy-1cent
 */
public class OBJLoader {
    private static String filePath;
    
    public static Model load(String model) throws FileNotFoundException, IOException{
        BufferedReader reader = new BufferedReader(new FileReader(new File(model)));
        Model m = new Model();
        String line;
        while((line = reader.readLine()) != null){
            if(line.startsWith("v ")){
                float x = Float.valueOf(line.split(" ")[1]);
                float y = Float.valueOf(line.split(" ")[2]);
                float z = Float.valueOf(line.split(" ")[3]);
                m.vertices.add(new Vector3f(x, y, z));
            }
            if(line.startsWith("vn ")){
                float x = Float.valueOf(line.split(" ")[1]);
                float y = Float.valueOf(line.split(" ")[2]);
                float z = Float.valueOf(line.split(" ")[3]);
                m.normals.add(new Vector3f(x, y, z));
            }
            if(line.startsWith("f ")){
                float vx = Float.valueOf(line.split(" ")[1].split("/")[0]);
                float vy = Float.valueOf(line.split(" ")[2].split("/")[0]);
                float vz = Float.valueOf(line.split(" ")[3].split("/")[0]);
                Vector3f vertex = new Vector3f(vx,vy,vz);
                
                float nx = Float.valueOf(line.split(" ")[1].split("/")[2]);
                float ny = Float.valueOf(line.split(" ")[2].split("/")[2]);
                float nz = Float.valueOf(line.split(" ")[3].split("/")[2]);
                Vector3f normal = new Vector3f(nx,ny,nz);

                m.faces.add(new Model.Face(vertex,normal)); 
            }
        }
        
        return m;
    }
    
}
