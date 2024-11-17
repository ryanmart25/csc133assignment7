package org.martinez.renderers;


import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

import org.martinez.cameras.Camera;
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
    public static final int NUMVERTEXESPERQUAD = 4;
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
    private float radius;

    // constructors
    public RenderEngine(WindowManager manager, ShaderObject so){
        this.so = so;
        this.manager = manager;
        this.rows = 8;
        this.columns = 16;
        initOpenGL();
    }
    // methods
    private void setupPGP(){
         // allocate NIO array for Vertex data
         this.positionStride = 3;
         this.textureStride = 2;
         this.vertexStride = 5;
        float[] vertexes = generateVertices();
        this.floatBuffer = BufferUtils.createFloatBuffer(vertexes.length);
        this.floatBuffer.put(vertexes).flip();
        this.VertexArrayObjectHandle = glGenVertexArrays();
        glBindVertexArray(this.VertexArrayObjectHandle);
        this.VertexBufferObjectHandle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VertexBufferObjectHandle);
        // setting vertex attribute pointers
        this.attributePointer0 = 0;
        this.attributePointer1 = 1;

        glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(this.attributePointer0, this.positionStride, GL_FLOAT, false,this.vertexStride, 0 );
        glVertexAttribPointer(this.attributePointer1, this.textureStride, GL_FLOAT, false, this.vertexStride, 12);
        so.useProgram();
        Camera camera = new Camera();
        so.loadMatrix4f("uProjMatrix", camera.getprojectionMatrix());
        so.loadMatrix4f("uViewMatrix", camera.getViewingMatrix());
        // this argument represents the offset, in bytes. it should be set the
        // number of bytes of data for position coordinates. They are floats, 4 bytes, 3 coordinates, 4 * 3 = 12. If this doesn't work, you need to figure out what the correct offset is

    }
    private void initOpenGL(){
        //GL.createCapabilities();
        setupPGP();
    }

    public void render(int framedelay) {
        while(!manager.isGlfwWindowClosed()){
            glfwPollEvents();
            glClear(GL_COLOR_BUFFER_BIT);
            manager.swapBuffers();
            if(framedelay > 0){
                try {
                    Thread.sleep(framedelay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        manager.destroyGlfwWindow();

    }
    private float[] generateVertices(){
        // defining 1 triangle.
        float[] triangleVertices = {
                0f,0f,0f,0.0f,1.0f,
                0.5f,0.0f,0f,0.0f,1.0f,
                0.25f, 0.5f, 0f,0.0f,1.0f
        };
        return triangleVertices;
    }
    private float[][][] generateROWCOLCenterPoints(int rows, int columns){
        float[][][] centerpoints = new float[rows][columns][2];
        float prospective1, prospective2;
        prospective1 = (1.0f / rows);
        prospective2 = (1.0f) / columns;
        float radius = Math.min(prospective1, prospective2);
        setRADIUS(radius);
        float initialX = -1.0f + radius;
        float initialY = 1.0f - radius;
        float offset = 2.0f * radius ; // twice the radius of a polygon.

        for (int row = 0; row < rows; row++){ // generate vertex components
            for (int col = 0; col < columns; col++){

                centerpoints[row][col][0] = initialX + (col * offset);
                centerpoints[row][col][1] = initialY - (row * offset);
            }
        }
        return centerpoints;
        //generatePolygons();
    }
    private float[] generateVertices(float[][][] centerpoints){
        float[] positionCoordinates = new float[rows * columns * vertexStride * NUMVERTEXESPERQUAD];
        float VertexZ = 0f;
        float uMin = 0f;
        float uMax = 1.0f;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {

                float Vertex1X = centerpoints[row][column][0] + radius;
                float Vertex1Y = centerpoints[row][column][1] + radius;
                float Vertex2X = centerpoints[row][column][0] - radius;
                float Vertex2Y = centerpoints[row][column][1] + radius;
                float Vertex3Y = centerpoints[row][column][1] - radius;
                float Vertex3X = centerpoints[row][column][0] - radius;
                float Vertex4X = centerpoints[row][column][0] + radius;
                float Vertex4Y = centerpoints[row][column][1] - radius;
                positionCoordinates
                for (int vertexComponent = 0; vertexComponent < vertexStride; vertexComponent++) {
                    positionCoordinates[vertexComponent] = Vertex1X;

                }
            }
        }
        return new float[][][];

    }
    private void setRADIUS(float radius) {
        this.radius = radius;
    }
}

