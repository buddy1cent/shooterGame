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
import org.newdawn.slick.Color;


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
import static org.lwjgl.util.glu.GLU.gluPerspective;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

/**
 * A LWJGL port of the awesome MineFront Pre-ALPHA 0.02 Controls: W/UP = forward; A/LEFT = strafe left; D/RIGHT = strafe
 * right; S/DOWN = backward; SPACE = fly up; SHIFT = fly down; CONTROL = move faster; TAB = move slower; Q = increase
 * walking speed; Z = decrease walking speed; O = increase mouse speed; L = decrease mouse speed; C = reset position
 *
 * @author Oskar Veerhoek, Yan Chernikov
 */
public class shooterGame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static boolean VSync = true;
    private static int SyncFPS = 60;
    private static float fov = 50;
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
    private static float gridSize = 10f;
    private static float tileSize = 0.02f;
    
    private static Vector3f position = new Vector3f(0, 0, 0);
    private static Vector3f rotation = new Vector3f(0, 0, 0);
    private static float walkingSpeed = 15f;
    private static float mouseSpeed = 1f;
    private static float maxLookUp = 80f;
    private static float maxLookDown = -80f;
    
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
    
    private static void initGL(){
        
        position = new Vector3f();
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(fov, aspectRatio, zNear, zFar);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glEnable(GL_TEXTURE_2D);
        
        floor = glGenTextures();
        InputStream in = null;
        try{
            in = new FileInputStream("src/res/floor.png");
            PNGDecoder decoder = new PNGDecoder(in);
            ByteBuffer buffer = BufferUtils.createByteBuffer(4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            buffer.flip();
            
            glBindTexture(GL_TEXTURE_2D, floor);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, 
                    GL_UNSIGNED_BYTE, buffer);
            
            in = new FileInputStream("src/res/wall.png");
            decoder = new PNGDecoder(in);
            buffer = BufferUtils.createByteBuffer(4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            buffer.flip();
            
            glBindTexture(GL_TEXTURE_2D, wall);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, 
                    GL_UNSIGNED_BYTE, buffer);
            glBindTexture(GL_TEXTURE_2D, 0);
            
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
        
                
        ceilingDisplayList = glGenLists(1);
        glNewList(ceilingDisplayList, GL_COMPILE);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex3f(-gridSize, ceilingHeight, -gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, 0);
        glVertex3f(gridSize, ceilingHeight, -gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, gridSize * 10 * tileSize);
        glVertex3f(gridSize, ceilingHeight, gridSize);
        glTexCoord2f(0, gridSize * 10 * tileSize);
        glVertex3f(-gridSize, ceilingHeight, gridSize);
        glEnd();
        glEndList();
        
        wallDisplayList = glGenLists(1);
        glNewList(wallDisplayList, GL_COMPILE);

        glBegin(GL_QUADS);

        // North wall

        glTexCoord2f(0, 0);
        glVertex3f(-gridSize, floorHeight, -gridSize);
        glTexCoord2f(0, gridSize * 10 * tileSize);
        glVertex3f(gridSize, floorHeight, -gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, gridSize * 10 * tileSize);
        glVertex3f(gridSize, ceilingHeight, -gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, 0);
        glVertex3f(-gridSize, ceilingHeight, -gridSize);

        // West wall

        glTexCoord2f(0, 0);
        glVertex3f(-gridSize, floorHeight, -gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, 0);
        glVertex3f(-gridSize, ceilingHeight, -gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, gridSize * 10 * tileSize);
        glVertex3f(-gridSize, ceilingHeight, +gridSize);
        glTexCoord2f(0, gridSize * 10 * tileSize);
        glVertex3f(-gridSize, floorHeight, +gridSize);

        // East wall

        glTexCoord2f(0, 0);
        glVertex3f(+gridSize, floorHeight, -gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, 0);
        glVertex3f(+gridSize, floorHeight, +gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, gridSize * 10 * tileSize);
        glVertex3f(+gridSize, ceilingHeight, +gridSize);
        glTexCoord2f(0, gridSize * 10 * tileSize);
        glVertex3f(+gridSize, ceilingHeight, -gridSize);

        // South wall

        glTexCoord2f(0, 0);
        glVertex3f(-gridSize, floorHeight, +gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, 0);
        glVertex3f(-gridSize, ceilingHeight, +gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, gridSize * 10 * tileSize);
        glVertex3f(+gridSize, ceilingHeight, +gridSize);
        glTexCoord2f(0, gridSize * 10 * tileSize);
        glVertex3f(+gridSize, floorHeight, +gridSize);

        glEnd();

        glEndList();

        floorDisplayList = glGenLists(1);
        glNewList(floorDisplayList, GL_COMPILE);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex3f(-gridSize, floorHeight, -gridSize);
        glTexCoord2f(0, gridSize * 10 * tileSize);
        glVertex3f(-gridSize, floorHeight, gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, gridSize * 10 * tileSize);
        glVertex3f(gridSize, floorHeight, gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, 0);
        glVertex3f(gridSize, floorHeight, -gridSize);
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
        //Dopsat
    }
    
    private static void render(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glBindTexture(GL_TEXTURE_2D, floor);
        
        glEnable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        glCallList(floorDisplayList);
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