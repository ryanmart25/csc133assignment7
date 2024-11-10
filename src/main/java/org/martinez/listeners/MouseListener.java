package org.martinez.listeners;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.*;

public class MouseListener {
    private static MouseListener instance;

    public static boolean getMouseButtonsState(int index) {
        return mouseButtonsState[index];
    }

    private static void setMouseButtonsState(int index, boolean value) {
        mouseButtonsState[index] = value;
    }

    private static boolean[] mouseButtonsState;
    private MouseListener(){
        mouseButtonsState = new boolean[8];
    }
    public static MouseListener getInstance(){
        if(instance == null){
            instance = new MouseListener();
        }
        return instance;
    }
    public static GLFWMouseButtonCallback mouseButtonCallback = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            /* do something */
            if(button == GLFW_MOUSE_BUTTON_1 && action ==GLFW_PRESS){
                System.out.println("Mouse button 1 was clicked!");
            }
        }
    };
    public static GLFWCursorPosCallback cursorPosCallback = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double xpos, double ypos) {
            /* do something */
            System.out.println("x: " +xpos+ " y: " + ypos);
        }
    };
}
