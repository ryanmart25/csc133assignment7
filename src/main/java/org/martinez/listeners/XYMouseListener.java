package org.martinez.listeners;


import org.lwjgl.glfw.GLFWMouseButtonCallback;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class XYMouseListener {
    private static XYMouseListener my_instance;
    private double scrollX, scrollY;
    double xPos, yPos, lastY, lastX;
    private boolean mouseButtonPressed[] = new boolean[3];
    private boolean isDragging;

    private XYMouseListener() {
        this.scrollX = 0.0f;
        this.scrollY = 0.0f;
        this.xPos = 0.0f;
        this.yPos = 0.0f;
        this.lastX = 0.0f;
        this.lastY = 0.0f;
        this.isDragging = false;
    }

    public static XYMouseListener get() {
        if (my_instance == null) {
            my_instance = new XYMouseListener();
        }
        return my_instance;
    }

    public static void mousePosCallback(long my_window, double pos_x, double pos_y) {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = pos_x;
        get().yPos = pos_y;
        get().isDragging = get().mouseButtonPressed[0] ||
                get().mouseButtonPressed[1] ||
                get().mouseButtonPressed[2];
    }
        // todo change thse callbacks to be strongly referenced instances of the corresponding class

    // mouse button callback
    public static GLFWMouseButtonCallback mouseButtonCallback = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            if (action == GLFW_PRESS) {
                if (button < XYMouseListener.get().mouseButtonPressed.length) {
                    XYMouseListener.get().mouseButtonPressed[button] = true;
                }
            } else if (action == GLFW_RELEASE) {
                if (button < XYMouseListener.get().mouseButtonPressed.length) {
                    XYMouseListener.get().mouseButtonPressed[button] = false;
                    XYMouseListener.get().isDragging = false;
                }
            }
        }
    }

    public static void mouseButtonCallback(long my_window, int button, int action, int mods) {

    }

    public static void mouseButtonDownReset(int button) {
        get().mouseButtonPressed[button] = false;
    }

    public static void mouseScrollCallback(long my_window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static void endFrame() {
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    public static float getX() {
        return (float) get().xPos;
    }

    public static float getY() {
        return (float) get().yPos;
    }

    public static float getDeltaX() {
        return (float) (get().lastX - get().xPos);
    }

    public static float getDeltaY() {
        return (float) (get().lastY - get().yPos);
    }

    public static float getScrollX() {
        return (float) get().scrollX;
    }

    public static float getScrollY() {
        return (float) get().scrollY;
    }

    public static boolean isDragging() {
        return (boolean) get().isDragging;
    }

    public static boolean mouseButtonDown(int button) {
        if (button < get().mouseButtonPressed.length) {
            return get().mouseButtonPressed[button];
        } else {
            return false;
        }
    }


}