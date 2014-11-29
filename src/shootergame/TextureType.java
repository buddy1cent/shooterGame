package shootergame;

public enum TextureType {
    WALL("src/res/stone_wall.png"),
    CEILING("src/res/ceiling.png"),
    FLOOR("src/res/floor.png");
    
    public final String location;
    
    TextureType(String location){
        this.location = location;
    }
}
