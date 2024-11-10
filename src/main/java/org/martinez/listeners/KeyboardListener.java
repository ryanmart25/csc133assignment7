package org.martinez.listeners;

import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyboardListener { // singleton, houses methods for callbacks. callbacks will be registered in WindowManager
    private static KeyboardListener instance;
    public boolean[] keyPressed= new boolean[99];
    private KeyboardListener(){

    }
    public static KeyboardListener getInstance(){
        if(instance == null){
            instance = new KeyboardListener();
        }
        return instance;
    }
    public static void resetKeypressEvent(int keyCode) {
        if (instance != null && keyCode < getInstance().keyPressed.length) {
            instance.keyPressed[keyCode] = false;
        }
    }

    public static boolean isKeyPressed(int keyCode){
        if (keyCode < getInstance().keyPressed.length) {
            return getInstance().keyPressed[keyCode];
        } else {
            return false;
        }
    }
    public static GLFWKeyCallback keyCallback =
            new GLFWKeyCallback() {
                @Override
                public void invoke(long window, int key, int scancode, int action, int mods) {
                    if (action == GLFW_PRESS) {
                        getInstance().keyPressed[key] = true;
                    } else if (action == GLFW_RELEASE){
                        getInstance().keyPressed[key] = false;
                    }
                }
            };
}
