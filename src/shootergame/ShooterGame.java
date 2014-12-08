package shootergame;

import java.io.File;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.PNGDecoder;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL15;
import static org.lwjgl.util.glu.GLU.gluPerspective;
import org.lwjgl.util.vector.Vector2f;
import static shootergame.Room.Wall;
import static shootergame.Model.Face;


public class ShooterGame {
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static boolean VSync = true;
    private static int SyncFPS = 60; 
    private static float fov = 70;
    private static float aspectRatio = WIDTH / HEIGHT;
    private static float zNear = 0.001f;
    private static float zFar = 100f;
    
    private static int fps;
    private static long lastFPS;
    private static long lastFrame;
    private static int delta;
    
    private static boolean exit = false;
    
    private static List<Integer> textures;
    private static List<Box> boxes;
    private static Room room;
    
    private static int floor;
    private static int wall;
    private static int ceiling;
    private static int box;
    private static int mp5;
    
    private static final int amountOfVertices = 4;
    private static final int vertexSize = 3;
    private static final int colorSize = 2;
    private static FloatBuffer vertexCeiling;
    private static FloatBuffer texCoordCeiling;
    private static FloatBuffer vertexWalls;
    private static FloatBuffer texCoordWalls;
    private static FloatBuffer vertexFloor;
    private static FloatBuffer vertexBox;
    private static FloatBuffer texCoordBox;
    private static Box a,b,c,d ;
    private static Model model;
    
    private static float ceilingHeight = 5f;
    private static float floorHeight = -1f;
    private static float gridSizeX = 10f;   
    private static float maxSizeX = gridSizeX - 0.5f;
    private static float gridSizeY = ceilingHeight+Math.abs(floorHeight);
    private static float gridSizeZ = 10f;
    private static float maxSizeZ = gridSizeZ - 0.5f;
    private static float texSize = 1f;
    
    private static Vector3f position = new Vector3f(-5, 0, -5);
    private static Vector3f rotation = new Vector3f(0, 0, 0);
    private static float moveSpeed = 0f;
    private static float x = 0.01f;
    private static float walkingSpeed = 15f;
    private static float mouseSpeed = 1f;
    private static float maxLookUp = 85f;
    private static float maxLookDown = -85f;
    
    public ShooterGame(){
        
        try {
            Display.setTitle("3D Shooter Game Adíka & Budíka");
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.setVSyncEnabled(VSync);
            Display.create();
        } catch (LWJGLException ex) {
            ex.printStackTrace();
            Display.destroy();
            System.exit(0);
        }
    }
    
    private static long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    private static int getDelta() {
        long currentTime = getTime();
        int delta = (int) (currentTime - lastFrame);
        lastFrame = getTime();
        return delta;
    }

    private static void updateFPS() {
        if (getTime() - lastFPS > 1000) {
            
            Display.setTitle("3D Shooter Game Adíka & Budíka | FPS: " + fps);
            
            fps = 0;
            lastFPS += 1000;
        }
        fps++;
    }
    
    public static int initTexture(TextureType type){
        int texture = glGenTextures();
        InputStream in = null;
        try{
            in = new FileInputStream(type.location);
            PNGDecoder decoder = new PNGDecoder(in);
            ByteBuffer buffer = BufferUtils.createByteBuffer(4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.RGBA);
            buffer.flip();
            
            glBindTexture(GL_TEXTURE_2D, texture);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, 
                    GL_UNSIGNED_BYTE, buffer);
            
        }catch(FileNotFoundException ex){
            System.out.println("Failed to find the textures files.");
            ex.printStackTrace();
            endGame();
        }catch(IOException ex){
            System.out.println("Failed to load the texture file");
            ex.printStackTrace();
            endGame();
        }finally{
            if(in != null){
                try{
                    in.close();
                }catch(IOException ex){
                    ex.printStackTrace();
                }
            }
        }
        textures.add(texture);
        return texture;
    }
   
    private static void initGL(){
        //position = new Vector3f();
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(fov, aspectRatio, zNear, zFar);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glEnable(GL_TEXTURE_2D);
        // Load textures
        textures = new ArrayList<Integer>();
        floor = initTexture(TextureType.FLOOR);
        wall = initTexture(TextureType.WALL);
        ceiling = initTexture(TextureType.CEILING);
        box = initTexture(TextureType.BOX);
        mp5 = initTexture(TextureType.MP5);
        
        room = new Room();
        room.add(new Wall(0, floorHeight, 0, gridSizeX, gridSizeZ, "F", floor));
        room.add(new Wall(0, ceilingHeight, 0, gridSizeX, gridSizeZ, "C", ceiling));
        room.add(new Wall(0, floorHeight, 0, gridSizeX, gridSizeY, "N", wall));
        room.add(new Wall(0, floorHeight, 0, gridSizeX, gridSizeY, "W", wall));
        room.add(new Wall(0, floorHeight, gridSizeZ, gridSizeX, gridSizeY, "S", wall));
        room.add(new Wall(gridSizeX, floorHeight, 0, gridSizeX, gridSizeY, "E", wall));
        //room.add();
        /*
        room.add(new Wall(-gridSizeX, floorHeight, -gridSizeZ, gridSizeX*2, gridSizeZ*2, "Floor", floor));
        room.add(new Wall(-gridSizeX, ceilingHeight, -gridSizeZ, gridSizeX*2, gridSizeZ*2, "Ceiling", ceiling));
        room.add(new Wall(-gridSizeX, floorHeight, -gridSizeZ, gridSizeX*2, gridSizeY, "N", wall));
        room.add(new Wall(-gridSizeX, floorHeight, gridSizeZ, gridSizeX*2, gridSizeY, "S", wall));
        room.add(new Wall(gridSizeX, floorHeight, -gridSizeZ, gridSizeX*2, gridSizeY, "E", wall));
        room.add(new Wall(-gridSizeX, floorHeight, -gridSizeZ, gridSizeX*2, gridSizeY, "W", wall));
        */
        /*
        //par
        room.add(new Wall(-gridSizeX+5f, floorHeight, -gridSizeZ+2, gridSizeX, gridSizeY, "N", ceiling));
        room.add(new Wall(-gridSizeX+5f, floorHeight, gridSizeZ-18, gridSizeX, gridSizeY, "S", ceiling));
        
        
        room.add(new Wall(-gridSizeX+5f, floorHeight+2f, -gridSizeZ-2f, gridSizeX, gridSizeZ, "Floor", wall));
        room.add(new Wall(-gridSizeX+2f, ceilingHeight-2f, -gridSizeZ, gridSizeX, gridSizeZ, "Ceiling", floor));
        room.add(new Wall(-gridSizeX+5f, floorHeight, gridSizeZ-2, gridSizeX, gridSizeY, "S", ceiling));
        room.add(new Wall(gridSizeX-2f, floorHeight, -gridSizeZ+5f, gridSizeX, gridSizeY, "E", ceiling));
        room.add(new Wall(-gridSizeX+2f, floorHeight, -gridSizeZ+5f, gridSizeX, gridSizeY, "W", ceiling));
        */
        boxes = new ArrayList<Box>();
        boxes.add(new Box(6f,-1f,1f,.5f,.5f,.5f,1,box));
        boxes.add(new Box(2f,-1f,1f,.5f,.5f,.5f,1,box));
        boxes.add(new Box(3f,0f,0f,.2f,.2f,.2f,1,box));
        boxes.add(new Box(6f,-1f,1f,.5f,.5f,.5f,1,box));
        boxes.add(new Box(2f,1f,3f,.7f,.7f,.5f,1,box));
        for(Box box: boxes){
            box.initGL();
        }
        
        model = null;
        try {
            model = OBJLoader.load("src/res/objModels/MP5.obj");
        }catch(FileNotFoundException ex){
            ex.printStackTrace();
            System.out.println("OBJ file not found");
            endGame();
        }catch (IOException ex) {
            ex.printStackTrace();
            endGame();
        }
        
        
    }
    private static void destroyGL(){
        for(int tex: textures){
            glDeleteTextures(tex);
        }
    }
    
    private static void input(){
        
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                Mouse.setGrabbed(true);
            }
            if (Mouse.isButtonDown(1)) {
                Mouse.setGrabbed(false);
            }
        }
        if (Mouse.isGrabbed()) {
            float mouseDX = Mouse.getDX() * mouseSpeed * 0.16f;
            float mouseDY = Mouse.getDY() * mouseSpeed * 0.16f;
            if (rotation.y + mouseDX >= 360) {
                rotation.y = rotation.y + mouseDX - 360;
            } else if (rotation.y + mouseDX < 0) {
                rotation.y = 360 - rotation.y + mouseDX;
            } else {
                rotation.y += mouseDX;
            }
           
            if (rotation.x - mouseDY >= maxLookDown && rotation.x - mouseDY <= maxLookUp) {
                rotation.x += -mouseDY;
            } else if (rotation.x - mouseDY < maxLookDown) {
                rotation.x = maxLookDown;
            } else if (rotation.x - mouseDY > maxLookUp) {
                rotation.x = maxLookUp;
            }
        }
        
        walkingSpeed = 15f;
        
        boolean keyUp = Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W);
        boolean keyDown = Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S);
        boolean keyLeft = Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A);
        boolean keyRight = Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D);
        boolean esc = Keyboard.isKeyDown(Keyboard.KEY_ESCAPE);
        boolean run = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
        boolean crouch = Keyboard.isKeyDown(Keyboard.KEY_C);
        boolean jump = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
        boolean jumping = false;
        boolean up = true;
        
        if(jump){
            jumping = true;
        }
        
        if(jumping){
            if(position.y == -2f)
                up = false;
            if(position.y < 1f && up){
                position.y -= 0.05f;
                System.out.println("X: " + position.x + " ,Y: " + position.y + " ,Z: " + position.z);
            }
            if(!up)
                position.y += 0.07f;
            if(position.y == 0 && !up){
                jumping = false;
                up = true;
            }
        }

        if(crouch && !jumping){
            if(position.y < 0.5f)
                position.y += 0.05f;
            if(position.y > 0.5f)
                position.y = 0.5f;
            walkingSpeed = 6f;
        }
        else if(!jumping){
            if(position.y > 0f)
                position.y -= 0.05f;
            if(position.y < 0f)
                position.y = 0f;
        }
        if(run){
            walkingSpeed *= 2f;
        }
        
        if (esc) {
                exit=true;
        }
       
        if (keyUp && keyRight && !keyLeft && !keyDown) {
                float angle = rotation.y + 45;
                Vector3f newPosition = new Vector3f(position);
                float hypotenuse = (walkingSpeed * 0.0002f) * delta;
                float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
                float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
                newPosition.z += adjacent;
                newPosition.x -= opposite;
                position.z = newPosition.z;
                position.x = newPosition.x;
        }
        
        if (keyUp && keyLeft && !keyRight && !keyDown) {
            float angle = rotation.y - 45;
            Vector3f newPosition = new Vector3f(position);
            float hypotenuse = (walkingSpeed * 0.0002f) * delta;
            float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
            float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
            newPosition.z += adjacent;
            newPosition.x -= opposite;
            position.z = newPosition.z;
            position.x = newPosition.x;
        }
        
        if (keyUp && !keyLeft && !keyRight && !keyDown) {
            float angle = rotation.y;
            Vector3f newPosition = new Vector3f(position);
            float hypotenuse = (walkingSpeed * 0.0002f) * delta; //prepona
            float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle)); //prilehly
            float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse); //protejsi
            newPosition.z += adjacent;
            newPosition.x -= opposite;
            position.z = newPosition.z;
            position.x = newPosition.x;
        }
        
        if (keyDown && keyLeft && !keyRight && !keyUp) {
            float angle = rotation.y - 135;
            Vector3f newPosition = new Vector3f(position);
            float hypotenuse = (walkingSpeed * 0.0002f) * delta;
            float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
            float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
            newPosition.z += adjacent;
            newPosition.x -= opposite;
            position.z = newPosition.z;
            position.x = newPosition.x;
        }
        
        if (keyDown && keyRight && !keyLeft && !keyUp) {
            float angle = rotation.y + 135;
            Vector3f newPosition = new Vector3f(position);
            float hypotenuse = (walkingSpeed * 0.0002f) * delta;
            float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
            float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
            newPosition.z += adjacent;
            newPosition.x -= opposite;
            position.z = newPosition.z;
            position.x = newPosition.x;
        }
        
        if (keyDown && !keyUp && !keyLeft && !keyRight) {
            float angle = rotation.y;
            Vector3f newPosition = new Vector3f(position);
            float hypotenuse = -(walkingSpeed * 0.0002f) * delta;
            float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
            float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
            newPosition.z += adjacent;
            newPosition.x -= opposite;
            position.z = newPosition.z;
            position.x = newPosition.x;
        }
        
        if (keyLeft && !keyRight && !keyUp && !keyDown) {
            float angle = rotation.y - 90;
            Vector3f newPosition = new Vector3f(position);
            float hypotenuse = (walkingSpeed * 0.0002f) * delta;
            float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
            float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
            newPosition.z += adjacent;
            newPosition.x -= opposite;
            position.z = newPosition.z;
            position.x = newPosition.x;
        }
        
        if (keyRight && !keyLeft && !keyUp && !keyDown) {
            float angle = rotation.y + 90;
            Vector3f newPosition = new Vector3f(position);
            float hypotenuse = (walkingSpeed * 0.0002f) * delta;
            float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
            float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
            newPosition.z += adjacent;
            newPosition.x -= opposite;
            position.z = newPosition.z;
            position.x = newPosition.x;
        }
        collideWall();
    }
    private static void collideWall(){
        /*if (position.z>=maxSizeZ) {
            position.z=maxSizeZ;   
        }
        if (position.z<=-maxSizeZ) {
            position.z=-maxSizeZ;   
        }
        if (position.x>=maxSizeX) {
            position.x=maxSizeX;   
        }
        if (position.x<=-maxSizeX) {
            position.x=-maxSizeX;   
        }*/
        room.wallIntersect(position);
            System.out.println("True X: " + position.x + " ,Y: " + position.y + " ,Z: " + position.z);
    }
    private static void boxMove(){
        
        if(moveSpeed >= 4.5f)
            x=-0.01f;
        if(moveSpeed <= -4.5f)
            x=0.01f;
            
        moveSpeed+= x;
    }
    private static void render(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        // Light
        /*
        glShadeModel(GL_SMOOTH);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        FloatBuffer buf = BufferUtils.createFloatBuffer(4);
                buf.put(new float[] {0.5f, 0.5f, 0.5f, 1f});
                buf.flip();
        FloatBuffer buf2 = BufferUtils.createFloatBuffer(4);
                buf2.put(new float[] {1f, 2f, 2f, 1f});
                buf2.flip();
        
        glLightModel(GL_LIGHT_MODEL_AMBIENT, buf);
        glLight(GL_LIGHT0, GL_POSITION, buf2);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
*/
        room.render();
        
        for(Box q: boxes){
            q.render();  
        }
        /*
        glBindTexture(GL_TEXTURE_2D, mp5);
        //glBindTexture(GL_TEXTURE_2D, 0);
        glBegin(GL_TRIANGLES);
        for(Face face : model.faces){
            Vector3f v1 = model.vertices.get((int) face.vertexIndices.x-1);
            glVertex3f(v1.x, v1.y, v1.z);
            Vector3f n1 = model.normals.get((int) face.normalIndices.x-1);
            glNormal3f(n1.x, n1.y, n1.z);
            Vector2f t1 = model.textures.get((int) face.textureIndices.x-1); 
            glTexCoord2f(t1.x, t1.y);
            
            Vector3f v2 = model.vertices.get((int) face.vertexIndices.y-1);
            glVertex3f(v2.x, v2.y, v2.z);
            Vector3f n2 = model.normals.get((int) face.normalIndices.y-1);
            glNormal3f(n2.x, n2.y, n2.z);
            Vector2f t2 = model.textures.get((int) face.textureIndices.y-1); 
            glTexCoord2f(t2.x, t2.y);
            
            Vector3f v3 = model.vertices.get((int) face.vertexIndices.z-1);
            glVertex3f(v3.x, v3.y, v3.z);
            Vector3f n3 = model.normals.get((int) face.normalIndices.z-1);
            glNormal3f(n3.x, n3.y, n3.z);
            Vector2f t3 = model.textures.get((int) face.textureIndices.z-1); 
            glTexCoord2f(t3.x, t3.y); 
        }
        glEnd();
*/
        // Player position
        glLoadIdentity();
        glRotatef(rotation.x, 1, 0, 0);
        glRotatef(rotation.y, 0, 1, 0);
        glRotatef(rotation.z, 0, 0, 1);
        glTranslatef(position.x, position.y, position.z);
        
        Display.update();
        if(VSync)
            Display.sync(SyncFPS);
    }

    public static void gameLoop(){
        initGL();
        getDelta();
        lastFPS = getTime();
        while(!Display.isCloseRequested() && !exit){
            delta = getDelta();
            input();
            render();
            updateFPS();
        }
        destroyGL();
        endGame();
    }
    
    private static void endGame(){
        Display.destroy();
        System.exit(0);
    }

    public static void main(String[] args) {
        new ShooterGame();
        gameLoop();
    }
}
