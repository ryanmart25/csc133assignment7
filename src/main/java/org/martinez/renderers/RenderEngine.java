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
import java.nio.IntBuffer;

import static java.lang.Thread.sleep;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
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
    private float radius = Spot.RADIUS;
    private int colorStride;
    private int attributePointer2;
    private int elementsLength = elementArray().length;
    // constructors
    public RenderEngine(WindowManager manager, ShaderObject so){
        this.so = so;
        this.manager = manager;
        this.rows = Spot.ROWS;
        this.columns = Spot.COLUMNS;
        initOpenGL();
    }
    // methods
    private void setupPGP(){
        so.useProgram();
        Camera camera = new Camera();
        so.loadMatrix4f("uProjMatrix", camera.getprojectionMatrix());
        so.loadMatrix4f("uViewMatrix", camera.getViewingMatrix());
         // allocate NIO array for Vertex data
         this.positionStride = 3;
         this.textureStride = 2;
         this.colorStride = 4;
         this.vertexStride = (this.positionStride + this.textureStride + this.colorStride) * 4;
        float[] vertexes = createVertexArray();
        int[] elements = elementArray();
        this.floatBuffer = BufferUtils.createFloatBuffer(vertexes.length);
        this.floatBuffer.put(vertexes).flip();
        this.VertexArrayObjectHandle = glGenVertexArrays();
        glBindVertexArray(this.VertexArrayObjectHandle);
        this.VertexBufferObjectHandle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VertexBufferObjectHandle);
        // setting vertex attribute pointers
        this.attributePointer0 = 0;
        this.attributePointer1 = 1;
        this.attributePointer2 = 2;
        glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(this.attributePointer0, this.positionStride, GL_FLOAT, false,this.vertexStride, 0 );
        glEnableVertexAttribArray(this.attributePointer0);
        glVertexAttribPointer(this.attributePointer1, this.textureStride, GL_FLOAT, false, this.vertexStride, 12);
        glEnableVertexAttribArray(this.attributePointer1);
        glVertexAttribPointer(this.attributePointer2,this.colorStride, GL_FLOAT, false, this.vertexStride, 20);
        glEnableVertexAttribArray(this.attributePointer2);

        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elements.length);
        elementBuffer.put(elements).flip();
        int eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

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

            glDrawElements(GL_TRIANGLES, this.elementsLength, GL_UNSIGNED_INT, 0);
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
    private float[] createVertexArray(){
        // defining 1 triangle.
        //          *
        //  *               *
        float[] triangleVertices = {
                // positions        // textures     // colors
                0f,0f,0f,           0.0f,1.0f,       1.0f, 1.0f, 1.0f,1.0f,             // 0
                100f,0f,0f,       0.0f,1.0f,       1.0f, 1.0f, 1.0f,1.0f,           // 1
                50f, 50f, 0f,    0.0f,1.0f,        1.0f, 1.0f, 1.0f, 1.0f           // 2
        };
         // generate centerpoints
        float[][][] centerpoints = new float[rows][columns][3];
        float[] vertexes = new float[rows * columns * 4 * 9];
        final float zCoord = 1.0f;
        final float uVMin = 0.0f;
        final float uVMax = 1.0f;
        final float RED = 1.0f;
        final float GREEN = 1.0f;
        final float BLUE = 1.0f;
        final float ALPHA = 1.0f;
        float prospective1, prospective2;
        prospective1 = (1.0f / rows);
        prospective2 = (1.0f) / columns;
        float radius = Math.min(prospective1, prospective2);
        setRADIUS(radius);
        float initialX = -1.0f + radius;
        float initialY = 1.0f - radius;
        float offset = 2.0f * radius;
        for (int row = 0; row < rows; row++){ // generate centerpoint components
            for (int col = 0; col < columns; col++){
                centerpoints[row][col][0] = initialX + (col * offset);
                centerpoints[row][col][1] = initialY - (row * offset);
                centerpoints[row][col][2] = zCoord;
                // generate box vertexes
                //  *2       * 1
                //
                //  *3       * 0
                // positions
                // triangle 1
                float Vertex2Y = centerpoints[row][col][1] - radius;
                float Vertex2X = centerpoints[row][col][0] - radius; // bottom left 0
                float Vertex3X = centerpoints[row][col][0] + radius;
                float Vertex3Y = centerpoints[row][col][1] - radius;// bottom right 1
                float Vertex1X = centerpoints[row][col][0] - radius;
                float Vertex1Y = centerpoints[row][col][1] + radius; // top left 2
                for (int i = 0; i < 3; i++) { // for each vertex in a triangle
                    vertexes[i] = Vertex2X;
                    //vertexes[]
                }
                // triangle 2
                // insert top left 2
                // insert bottom right 1
                float Vertex0X = centerpoints[row][col][0] + radius;
                float Vertex0Y = centerpoints[row][col][1] + radius; // top right 5


                // put vertex data (position, texture, color) into array
                // vertex 0
                vertexes[col] = Vertex0X;
                vertexes[col + 1] = Vertex0Y;
                vertexes[col + 2] = zCoord;
                vertexes[col + 3] = uVMin;
                vertexes[col + 4] = uVMax;
                vertexes[col + 5] = RED;
                vertexes[col + 6] = GREEN;
                vertexes[col + 7] = BLUE;
                vertexes[col + 8] = ALPHA;
                // vertex 1
                vertexes[col+ 10] = Vertex1X;
                vertexes[col + 11] = Vertex1Y;
                vertexes[col + 12] = zCoord;
                vertexes[col + 13] = uVMin;
                vertexes[col + 14] = uVMax;
                vertexes[col + 15] = RED;
                vertexes[col + 16] = GREEN;
                vertexes[col + 17] = BLUE;
                vertexes[col + 18] = ALPHA;
                // vertex 2
                vertexes[col+ 19] = Vertex2X;
                vertexes[col + 20] = Vertex2Y;
                vertexes[col + 21] = zCoord;
                vertexes[col + 22] = uVMin;
                vertexes[col + 23] = uVMax;
                vertexes[col + 24] = RED;
                vertexes[col + 25] = GREEN;
                vertexes[col + 26] = BLUE;
                vertexes[col + 27] = ALPHA;
                // vertex 3
                vertexes[col+ 28] = Vertex3X;
                vertexes[col + 29] = Vertex3Y;
                vertexes[col + 30] = zCoord;
                vertexes[col + 31] = uVMin;
                vertexes[col + 32] = uVMax;
                vertexes[col + 33] = RED;
                vertexes[col + 34] = GREEN;
                vertexes[col + 35] = BLUE;
                vertexes[col + 36] = ALPHA;
            }
        }
        return triangleVertices;
    }
    private int[] elementArray(){ // counterclockwise
        int[] elementArray = {
                1,2,0
        };
        return elementArray;
    }
/*
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
        generatePolygons();
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
*/
private void setRADIUS(float radius) {
        this.radius = radius;
    }
}

