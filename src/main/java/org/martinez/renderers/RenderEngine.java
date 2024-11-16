package org.martinez.renderers;


import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

import org.martinez.listeners.KeyboardListener;
import org.martinez.listeners.MouseListener;
import org.martinez.managers.WindowManager;
import org.martinez.shaders.ShaderObject;
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
    private ShaderObject so;
    private FloatBuffer floatBuffer;
    // constructors
    public RenderEngine(WindowManager manager, ShaderObject so){
        this.so = new ShaderObject("C:\\Users\\timef\\Documents\\Workspaces\\Java\\CSC133Assignment7\\src\\main\\resources\\shaders\\vs_0.glsl",
                "C:\\Users\\timef\\Documents\\Workspaces\\Java\\CSC133Assignment7\\src\\main\\resources\\shaders\\fs_0.glsl");
        initOpenGL();
        this.manager = manager;
        this.rows = 8;
        this.columns = 16;

    }
    // methods
    private void setupPGP(){
         // allocate NIO array for Vertex data
        float[] vertexes = new float[this.rows * this.columns * this.NUMBEROFFLOATINGPOINTNUMBERSPERPOLYGON];
        this.floatBuffer = BufferUtils.createFloatBuffer(vertexes.length);
        this.floatBuffer.put(vertexes).flip();
        this.VertexArrayObjectHandle = glGenVertexArrays();
        glBindVertexArray(this.VertexArrayObjectHandle);
        this.VertexBufferObjectHandle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VertexBufferObjectHandle);
        // setting vertex attribute pointers
        this.attributePointer0 = 0;
        this.attributePointer1 = 1;
        this.positionStride = 3;
        this.textureStride = 2;
        this.vertexStride = 5;
        glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(this.attributePointer0, this.positionStride, GL_FLOAT, false,this.vertexStride, 0 );
        glVertexAttribPointer(this.attributePointer1, this.textureStride, GL_FLOAT, false, this.vertexStride, 12);
        so.useProgram();
        // this argument represents the offset, in bytes. it should be set the
        // number of bytes of data for position coordinates. They are floats, 4 bytes, 3 coordinates, 4 * 3 = 12. If this doesn't work, you need to figure out what the correct offset is

    }
    private void initOpenGL(){
        setupPGP();
        GL.createCapabilities();
    }

    public void render(int framedelay) {
        System.out.println("render() called");
        if(framedelay > 0){
            try {
                Thread.sleep(framedelay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

