/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shootergame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Adamek
 */
public class BoxLoader {
    public static List<Box> load(String box)throws FileNotFoundException, IOException{
        BufferedReader reader = new BufferedReader(new FileReader(new File(box)));
        List<Box> boxes = new ArrayList<>();
        Box b = null;
        String line;
        while((line = reader.readLine()) != null){
            float x = Float.valueOf(line.split(" ")[0]);
            float y = Float.valueOf(line.split(" ")[1]);
            float z = Float.valueOf(line.split(" ")[2]);
            float w = Float.valueOf(line.split(" ")[3]);
            float h = Float.valueOf(line.split(" ")[4]);
            float d = Float.valueOf(line.split(" ")[5]);
            float texSize = Float.valueOf(line.split(" ")[6]);
            String tex = line.split(" ")[7];
            if(tex.equals("target"))
                b = new Box(x, y, z, w, h, d, texSize, ShooterGame.target);
            else
                b = new Box(x, y, z, w, h, d, texSize, ShooterGame.box);
            boxes.add(b);
        }
        reader.close();
        
        return boxes;
    }
}
