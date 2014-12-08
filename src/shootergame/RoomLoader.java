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
import static shootergame.Room.Wall;

/**
 *
 * @author Buddy-1cent
 */
public class RoomLoader {
    
    public static Room load(String room)throws FileNotFoundException, IOException{
        BufferedReader reader = new BufferedReader(new FileReader(new File(room)));
        Room r = new Room();
        String line;
        while((line = reader.readLine()) != null){
            String f = line.split(" ")[0];
            float x = Float.valueOf(line.split(" ")[1]);
            float y = Float.valueOf(line.split(" ")[2]);
            float z = Float.valueOf(line.split(" ")[3]);
            float w = Float.valueOf(line.split(" ")[4]);
            float h = Float.valueOf(line.split(" ")[5]);  
            if(f.equals("C")) 
                r.add(new Wall(x, y, z, w, h, f, ShooterGame.ceiling));
            else if(f.equals("F"))
                r.add(new Wall(x, y, z, w, h, f, ShooterGame.floor));
            else
                r.add(new Wall(x, y, z, w, h, f, ShooterGame.wall));
        }
        reader.close();
        
        return r;
    }
    
}
