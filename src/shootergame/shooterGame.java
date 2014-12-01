package shootergame;

import java.io.File;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Vector3f;
//import org.newdawn.slick.Color;
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
import org.lwjgl.opengl.ARBTextureRg;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL15;
import static org.lwjgl.util.glu.GLU.gluPerspective;
//import org.newdawn.slick.opengl.Texture;
//import org.newdawn.slick.opengl.TextureLoader;

public class shooterGame {
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static boolean VSync = true;
    private static int SyncFPS = 60; 
    private static float fov = 75;
    private static float aspectRatio = WIDTH / HEIGHT;
    private static float zNear = 0.001f;
    private static float zFar = 100f;
    
    private static int fps;
    private static long lastFPS;
    private static long lastFrame;
    private static int delta;
    
    private static boolean exit = false;
    
    private static List<Integer> textures;
    private static int floor;
    private static int wall;
    private static int ceiling;
    private static int box;
    
        private static final int amountOfVertices = 4;
    private static final int vertexSize = 3;
    private static final int colorSize = 2;
    private static FloatBuffer vertexCeiling;
    private static FloatBuffer texCoordCeiling;
    private static FloatBuffer vertexWalls,vertexWallWest,vertexWallEast,vertexWallSouth;
    private static FloatBuffer texCoordWalls;
    private static FloatBuffer vertexFloor;
    private static FloatBuffer vertexBox;
    private static FloatBuffer texCoordBox;
    
    private static float ceilingHeight = 5f;
    private static float floorHeight = -1f;
    private static float gridSizeX = 5f;   
    private static float maxSizeX = gridSizeX - 0.5f;
    private static float gridSizeY = ceilingHeight-Math.abs(floorHeight);
    private static float gridSizeZ = 5f;
    private static float maxSizeZ = gridSizeZ - 0.5f;
    private static float texSize = 1f;
    
    private static float boxSize = 0.2f;
    
    private static Vector3f position = new Vector3f(0, 0, 0);
    private static Vector3f rotation = new Vector3f(0, 0, 0);
    private static float transSpeed = 0f;
    private static float x = 0.01f;
    private static float walkingSpeed = 15f;
    private static float mouseSpeed = 1f;
    private static float maxLookUp = 85f;
    private static float maxLookDown = -85f;
    
    public shooterGame(){
        
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
    
    private static int initTexture(TextureType type){
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
        
        position = new Vector3f();
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(fov, aspectRatio, zNear, zFar);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glEnable(GL_TEXTURE_2D);
        // Load textures
        textures = new ArrayList<>();
        floor = initTexture(TextureType.FLOOR);
        wall = initTexture(TextureType.WALL);
        ceiling = initTexture(TextureType.CEILING);
        box = initTexture(TextureType.BOX);

        // Ceiling
        vertexCeiling = BufferUtils.createFloatBuffer(amountOfVertices * vertexSize);
        vertexCeiling.put(new float[]{
            -gridSizeX, ceilingHeight,  -gridSizeZ,
            gridSizeX,  ceilingHeight,  -gridSizeZ,
            gridSizeX,  ceilingHeight,  gridSizeZ,
            -gridSizeX, ceilingHeight,  gridSizeZ,
        });
        vertexCeiling.flip();

        // Floor
        vertexFloor = BufferUtils.createFloatBuffer(amountOfVertices * vertexSize);
        vertexFloor.put(new float[]{
            gridSizeX,  floorHeight,    gridSizeZ,
            gridSizeX,  floorHeight,    -gridSizeZ,
            -gridSizeX, floorHeight,    -gridSizeZ,        
            -gridSizeX, floorHeight,    gridSizeZ,
        });
        vertexFloor.flip();
        
        // Texture Coords Ceiling/Floor
        texCoordCeiling = BufferUtils.createFloatBuffer(amountOfVertices * colorSize);
        texCoordCeiling.put(new float[]{
            0, 0, gridSizeX*texSize, 0, gridSizeX*texSize, gridSizeZ*texSize, 0, gridSizeZ*texSize
        });
        texCoordCeiling.flip();
        
        // Walls
        vertexWalls = BufferUtils.createFloatBuffer(amountOfVertices * vertexSize * 4);
        vertexWalls.put(new float[]{
            // North wall
            gridSizeX,  floorHeight,    -gridSizeZ,
            gridSizeX,  ceilingHeight,  -gridSizeZ,
            -gridSizeX, ceilingHeight,  -gridSizeZ,
            -gridSizeX, floorHeight,    -gridSizeZ,
            // West wall
            -gridSizeX, floorHeight,    -gridSizeZ,
            -gridSizeX, ceilingHeight,  -gridSizeZ,
            -gridSizeX, ceilingHeight,  gridSizeZ,
            -gridSizeX, floorHeight,    gridSizeZ,
            // East wall
            gridSizeX,  floorHeight,    gridSizeZ,
            gridSizeX,  ceilingHeight,  gridSizeZ,
            gridSizeX,  ceilingHeight,  -gridSizeZ,
            gridSizeX,  floorHeight,    -gridSizeZ,
            // South wall
            -gridSizeX, floorHeight,    gridSizeZ,
            -gridSizeX, ceilingHeight,  gridSizeZ,
            gridSizeX,  ceilingHeight,  gridSizeZ,
            gridSizeX,  floorHeight,    gridSizeZ,
        });
        vertexWalls.flip();
        
        // Texture coords walls
        texCoordWalls = BufferUtils.createFloatBuffer(amountOfVertices * colorSize * 4);
        texCoordWalls.put(new float[]{
            0, 0, 0, gridSizeY*texSize, gridSizeX*texSize, gridSizeY*texSize, gridSizeX*texSize, 0,
            0, 0, 0, gridSizeY*texSize, gridSizeX*texSize, gridSizeY*texSize, gridSizeX*texSize, 0,
            0, 0, 0, gridSizeY*texSize, gridSizeX*texSize, gridSizeY*texSize, gridSizeX*texSize, 0,
            0, 0, 0, gridSizeY*texSize, gridSizeX*texSize, gridSizeY*texSize, gridSizeX*texSize, 0,
        });
        texCoordWalls.flip();  
        
        // Box
        vertexBox = BufferUtils.createFloatBuffer(amountOfVertices * vertexSize *4);
        vertexBox.put(new float[]{
            // North 
            boxSize,  floorHeight,    -boxSize,
            boxSize,  boxSize*1.5f,  -boxSize,
            -boxSize, boxSize*1.5f,  -boxSize,
            -boxSize, floorHeight,    -boxSize,
            // West 
            -boxSize, floorHeight,    -boxSize,
            -boxSize, boxSize*1.5f,  -boxSize,
            -boxSize, boxSize*1.5f,  boxSize,
            -boxSize, floorHeight,    boxSize,
            // East 
            boxSize,  floorHeight,    boxSize,
            boxSize,  boxSize*1.5f,  boxSize,
            boxSize,  boxSize*1.5f,  -boxSize,
            boxSize,  floorHeight,    -boxSize,
            // South 
            -boxSize, floorHeight,    boxSize,
            -boxSize, boxSize*1.5f,  boxSize,
            boxSize,  boxSize*1.5f,  boxSize,
            boxSize,  floorHeight,    boxSize,
        });
        vertexBox.flip();
        
        // Texture coords box
        texCoordBox = BufferUtils.createFloatBuffer(amountOfVertices * colorSize * 4);
        texCoordBox.put(new float[]{
            0, 0, 0, 2*boxSize*texSize, 2*boxSize*texSize, 2*boxSize*texSize,2*boxSize*texSize,0,
            0, 0, 0, 2*boxSize*texSize, 2*boxSize*texSize, 2*boxSize*texSize,2*boxSize*texSize,0,
            0, 0, 0, 2*boxSize*texSize, 2*boxSize*texSize, 2*boxSize*texSize,2*boxSize*texSize,0,
            0, 0, 0, 2*boxSize*texSize, 2*boxSize*texSize, 2*boxSize*texSize,2*boxSize*texSize,0,
        });
        texCoordBox.flip(); 
        
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
        if (position.z>=maxSizeZ) {
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
        }
    }
    private static void tranSpeed(){
        
        if(transSpeed >= 4.5f)
            x=-0.01f;
        if(transSpeed <= -4.5f)
            x=0.01f;
            
        transSpeed+= x;
    }
    private static void render(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        glEnable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        // Room
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        
        glBindTexture(GL_TEXTURE_2D, ceiling);
        glVertexPointer(vertexSize, 0, vertexCeiling);
        glTexCoordPointer(colorSize, 0, texCoordCeiling);
        glDrawArrays(GL_QUADS, 0, amountOfVertices);
        
        glBindTexture(GL_TEXTURE_2D, floor);
        glVertexPointer(vertexSize, 0, vertexFloor);
        glDrawArrays(GL_QUADS, 0, amountOfVertices);   
        
        glBindTexture(GL_TEXTURE_2D, wall);
        glVertexPointer(vertexSize, 0, vertexWalls);
        glTexCoordPointer(colorSize, 0, texCoordWalls);
        glDrawArrays(GL_QUADS, 0, amountOfVertices * 4);
        
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        // Box
        tranSpeed();
        glTranslatef(transSpeed, 0, 0);
        glBindTexture(GL_TEXTURE_2D, box);
        glVertexPointer(vertexSize, 0, vertexBox);
        glTexCoordPointer(colorSize, 0, texCoordBox);
        glDrawArrays(GL_QUADS, 0, amountOfVertices * 4);

        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        
        glBindTexture(GL_TEXTURE_2D, 0);
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
        new shooterGame();
        gameLoop();
    }
}
