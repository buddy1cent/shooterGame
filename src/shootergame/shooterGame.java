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
    
    private static int floor;
    private static int wall;
    private static int ceiling;
    
    private static int ceilingDisplayList;
    private static int wallDisplayList;
    private static int floorDisplayList;
    
    private static float ceilingHeight = 3f;
    private static float floorHeight = -1f;
    private static float gridSizeX = 5f;
    private static float gridSizeY = ceilingHeight-Math.abs(floorHeight);
    private static float gridSizeZ = 5f;
    private static float texSize = 1f;
    
    private static Vector3f position = new Vector3f(0, 0, 0);
    private static Vector3f rotation = new Vector3f(0, 0, 0);
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
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
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
        
        floor = initTexture(TextureType.FLOOR);
        wall = initTexture(TextureType.WALL);
        ceiling = initTexture(TextureType.CEILING);
        
        // Ceiling
        ceilingDisplayList = glGenLists(1);
        glNewList(ceilingDisplayList, GL_COMPILE);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex3f(-gridSizeX, ceilingHeight, -gridSizeZ);
        glTexCoord2f(gridSizeX*texSize, 0);
        glVertex3f(gridSizeX, ceilingHeight, -gridSizeZ);
        glTexCoord2f(gridSizeX*texSize, gridSizeZ*texSize);
        glVertex3f(gridSizeX, ceilingHeight, gridSizeZ);
        glTexCoord2f(0, gridSizeZ*texSize);
        glVertex3f(-gridSizeX, ceilingHeight, gridSizeZ);
        glEnd();
        glEndList();
        wallDisplayList = glGenLists(1);
        glNewList(wallDisplayList, GL_COMPILE);

        glBegin(GL_QUADS);
        // North wall
        glTexCoord2f(0, 0);
        glVertex3f(gridSizeX, floorHeight, -gridSizeZ);
        glTexCoord2f(0,gridSizeY*texSize);
        glVertex3f(gridSizeX, ceilingHeight, -gridSizeZ);
        glTexCoord2f(gridSizeX*texSize,gridSizeY*texSize);
        glVertex3f(-gridSizeX, ceilingHeight, -gridSizeZ);
        glTexCoord2f(gridSizeX*texSize, 0);
        glVertex3f(-gridSizeX, floorHeight, -gridSizeZ);
        
        // West wall
        glTexCoord2f(0, 0);
        glVertex3f(-gridSizeX,floorHeight,-gridSizeZ);
        glTexCoord2f(0, gridSizeY*texSize);
        glVertex3f(-gridSizeX,ceilingHeight,-gridSizeZ);
        glTexCoord2f(gridSizeZ*texSize, gridSizeY*texSize);
        glVertex3f(-gridSizeX,ceilingHeight,gridSizeZ);
        glTexCoord2f(gridSizeZ*texSize, 0);
        glVertex3f(-gridSizeX,floorHeight,gridSizeZ);
        
        // East wall
        glTexCoord2f(0, 0);
        glVertex3f(gridSizeX,floorHeight,gridSizeZ);
        glTexCoord2f(0, gridSizeY*texSize);
        glVertex3f(gridSizeX,ceilingHeight,gridSizeZ);
        glTexCoord2f(gridSizeZ*texSize, gridSizeY*texSize);
        glVertex3f(gridSizeX,ceilingHeight,-gridSizeZ);
        glTexCoord2f(gridSizeZ*texSize, 0);
        glVertex3f(gridSizeX,floorHeight,-gridSizeZ);
        
        
        // South wall
        glTexCoord2f(0, 0);
        glVertex3f(-gridSizeX,floorHeight, gridSizeZ);
        glTexCoord2f(0, gridSizeY*texSize);
        glVertex3f(-gridSizeX, ceilingHeight, gridSizeZ);
        glTexCoord2f(gridSizeX*texSize, gridSizeY*texSize);
        glVertex3f(gridSizeX, ceilingHeight, gridSizeZ);
        glTexCoord2f(gridSizeX*texSize, 0);
        glVertex3f(gridSizeX,floorHeight, gridSizeZ);
        
        glEnd();
        glEndList();

        // Floor
        floorDisplayList = glGenLists(1);
        glNewList(floorDisplayList, GL_COMPILE);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex3f(gridSizeX, floorHeight, gridSizeZ);
        glTexCoord2f(0, gridSizeZ*texSize);
        glVertex3f(gridSizeX, floorHeight, -gridSizeZ);
        glTexCoord2f(gridSizeX*texSize, gridSizeZ*texSize);
        glVertex3f(-gridSizeX, floorHeight, -gridSizeZ);
        glTexCoord2f(gridSizeX*texSize, 0);
        glVertex3f(-gridSizeX, floorHeight, gridSizeZ);
        glEnd();
        glEndList();
        
    }
    private static void destroyGL(){
        glDeleteTextures(floor);
        glDeleteTextures(wall);
        glDeleteLists(floorDisplayList, 1);
        glDeleteLists(ceilingDisplayList, 1);
        glDeleteLists(wallDisplayList, 1);
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
    }
    
    private static void render(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        
        
        glEnable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        
        glBindTexture(GL_TEXTURE_2D, floor);
        glCallList(floorDisplayList);
        
        glBindTexture(GL_TEXTURE_2D, ceiling);
        glCallList(ceilingDisplayList);
        
        glBindTexture(GL_TEXTURE_2D, wall);
        glCallList(wallDisplayList);
        
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glBindTexture(GL_TEXTURE_2D, 0);

        glLoadIdentity();
        glRotatef(rotation.x, 1, 0, 0);
        glRotatef(rotation.y, 0, 1, 0);
        glRotatef(rotation.z, 0, 0, 1);
        glTranslatef(position.x, position.y, position.z);
        
        Display.update();
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