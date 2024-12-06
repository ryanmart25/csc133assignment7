package org.martinez.listeners;


import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

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

    public static XYMouseListener getInstance() {
        if (my_instance == null) {
            my_instance = new XYMouseListener();
        }
        return my_instance;
    }
    public static GLFWCursorPosCallback cursorPosCallback = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double xpos, double ypos) {
            getInstance().lastX = getInstance().xPos;
            getInstance().lastY = getInstance().yPos;
            getInstance().xPos = xpos;
            getInstance().yPos = ypos;
            getInstance().isDragging = getInstance().mouseButtonPressed[0] ||
                    getInstance().mouseButtonPressed[1] ||
                    getInstance().mouseButtonPressed[2];
        }
    };

    // mouse button callback
    public static GLFWMouseButtonCallback mouseButtonCallback = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            if (action == GLFW_PRESS) {
                if (button < XYMouseListener.getInstance().mouseButtonPressed.length) {
                    XYMouseListener.getInstance().mouseButtonPressed[button] = true;
                }
            } else if (action == GLFW_RELEASE) {
                if (button < XYMouseListener.getInstance().mouseButtonPressed.length) {
                    XYMouseListener.getInstance().mouseButtonPressed[button] = false;
                    XYMouseListener.getInstance().isDragging = false;
                }
            }
        }
    };



    public static void mouseButtonDownReset(int button) {
        getInstance().mouseButtonPressed[button] = false;
    }

    public static GLFWScrollCallback mouseScrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double xoffset, double yoffset) {
            getInstance().scrollX = xoffset;
            getInstance().scrollY = yoffset;
        }
    };


    public static void endFrame() {
        getInstance().scrollX = 0;
        getInstance().scrollY = 0;
        getInstance().lastX = getInstance().xPos;
        getInstance().lastY = getInstance().yPos;
    }

    public static float getX() {
        return (float) getInstance().xPos;
    }

    public static float getY() {
        return (float) getInstance().yPos;
    }

    public static float getDeltaX() {
        return (float) (getInstance().lastX - getInstance().xPos);
    }

    public static float getDeltaY() {
        return (float) (getInstance().lastY - getInstance().yPos);
    }

    public static float getScrollX() {
        return (float) getInstance().scrollX;
    }

    public static float getScrollY() {
        return (float) getInstance().scrollY;
    }

    public static boolean isDragging() {
        return (boolean) getInstance().isDragging;
    }

    public static boolean mouseButtonDown(int button) {
        if (button < getInstance().mouseButtonPressed.length) {
            return getInstance().mouseButtonPressed[button];
        } else {
            return false;
        }
    }


}