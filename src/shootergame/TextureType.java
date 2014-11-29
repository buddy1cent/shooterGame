/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shootergame;

/**
 *
 * @author Buddy-1cent
 */
public enum TextureType {
    WALL("src/res/stone_wall.png"),
    CEILING("src/res/ceiling.png"),
    FLOOR("src/res/floor.png");
    
    public final String location;
    
    TextureType(String location){
        this.location = location;
    }
}
