package org.martinez.managers;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.martinez.utils.Spot.win_height;
import static org.martinez.utils.Spot.win_width;

public class WindowManager { // singleton
    //fields
    private static WindowManager instance = null;
    private long handle = -1;
    //constructors
    private WindowManager(){

    }
    //methods
    public static WindowManager getInstance() {
        if(instance == null){
            instance = new WindowManager();
        }
        return instance;
    }
    public long initGLFWWindow(int WIN_WIDTH, int WIN_HEIGHT, String title){
        if(handle == -1){
            GLFWErrorCallback.createPrint(System.err).set();
            if (!glfwInit()) {
                throw new IllegalStateException("Could not initialize GLFW");
            }
            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);
            long h = glfwCreateWindow(WIN_WIDTH, WIN_HEIGHT, title, NULL, NULL);
            if(h == NULL){
                throw new RuntimeException("Failed to create GLFW window");
            }
            this.handle = h;
            return this.handle;
        }
        return this.handle;
    }
    // public void enableResizeWindowCallback(...)

    public void swapBuffers(){
        if(this.handle == -1){
            throw new RuntimeException("Invalid window ID. Initialize Window before attempting to use it");
        }
        glfwSwapBuffers(this.handle);
    }
    // window state methods
    public int[] getCurrentWindowSize(){ // gets current size of the window
        int[] width = new int[1];
        int[] height = new int[1];
        glfwGetWindowSize(this.handle, width, height);
        return new int[]{width[0], height[0]};
    }
    public boolean isGlfwWindowClosed(){
        return glfwWindowShouldClose(this.handle);
    }
    public void destroyGlfwWindow(){
        if(this.handle == -1){
            throw new RuntimeException("Invalid window ID. Initialize Window before attempting to use it");
        }
        glfwDestroyWindow(this.handle);
    }
    public int[] getWindowSize(){ // gets initial size of the window
        return new int[]{win_width,win_height};
    }

    // callbacks

    private static GLFWFramebufferSizeCallback resizeWindow =
            new GLFWFramebufferSizeCallback(){
                @Override
                public void invoke(long window, int width, int height){
                    glViewport(0,0,width, height);

                }
            };
    // register callbacks
    public void enableResizeWindowCallback() {
        glfwSetFramebufferSizeCallback(this.handle, resizeWindow);
    }

}
