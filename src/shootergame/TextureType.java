package shootergame;

public enum TextureType {
    WALL("src/res/stone_wall.png"),
    CEILING("src/res/ceiling.png"),
    FLOOR("src/res/floor.png"),
    BOX("src/res/Wood_Box.png"),
    MP5("src/res/objModels/Tex_0004_1.png"),
    COLT("src/res/objModels/Tex_0008_1.png");
    
    public final String location;
    
    TextureType(String location){
        this.location = location;
    }
}
