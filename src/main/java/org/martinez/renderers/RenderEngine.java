package org.martinez.renderers;


import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

import org.martinez.listeners.KeyboardListener;
import org.martinez.listeners.MouseListener;
import org.martinez.managers.WindowManager;
import org.martinez.utils.Spot;

import java.nio.FloatBuffer;

import static java.lang.Thread.sleep;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderEngine{

    public static final int NUMBEROFFLOATINGPOINTNUMBERSPERPOLYGON = 8;
    // fields
    private int VertexArrayObjectHandle;
    private int VertexBufferObjectHandle;
    private int attributePointer0;
    private int attributePointer1;
    private int textureStride;
    private int vertexStride;
    private int positionStride;
    private int rows, columns;
    private WindowManager manager;
    // constructors
    public RenderEngine(){
        this.manager = WindowManager.getInstance();
        this.rows = 8;
        this.columns = 16;

        initOpenGL(manager);
    }
    // methods
    private void setupPGP(){

         // allocate NIO array for Vertex data
        float[] vertexes = new float[rows * columns * NUMBEROFFLOATINGPOINTNUMBERSPERPOLYGON];
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(vertexes.length);
        floatBuffer.put(vertexes).flip();
        this.VertexArrayObjectHandle = glGenVertexArrays();
        glBindVertexArray(VertexArrayObjectHandle);
        this.VertexBufferObjectHandle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VertexBufferObjectHandle);
        // setting vertex attribute pointers // todo decide if i need these as fields or just locals
        this.attributePointer0 = 0;
        this.attributePointer1 = 1;
        this.positionStride = 3;
        this.textureStride = 2;
        this.vertexStride = 5;
        glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(this.attributePointer0, this.positionStride, GL_FLOAT, false,this.vertexStride, 0 );
        glVertexAttribPointer(this.attributePointer1, this.textureStride, GL_FLOAT, false, this.vertexStride, 12);
        // this argument represents the offset, in bytes. it should be set the
        // number of bytes of data for position coordinates. They are floats, 4 bytes, 3 coordinates, 4 * 3 = 12. If this doesn't work, you need to figure out what the correct offset is

    }
    public void initOpenGL(WindowManager manager){ // TODO need to add setup for PGP.
        // How much goes here?
        long handle = manager.initGLFWWindow(Spot.win_width, Spot.win_height, Spot.TITLE);
        glfwSetKeyCallback(handle, KeyboardListener.keyCallback);
        glfwMakeContextCurrent(handle);

        // setting up for PGP
        // allocating and binding buffers
        setupPGP();

        // setting callbacks
        manager.enableResizeWindowCallback();
        glfwSetMouseButtonCallback(handle, MouseListener.mouseButtonCallback);
        glfwSetCursorPosCallback(handle, MouseListener.cursorPosCallback);
        GL.createCapabilities();
    }

}

