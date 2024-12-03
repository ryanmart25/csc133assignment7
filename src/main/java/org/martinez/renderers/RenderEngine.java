package org.martinez.renderers;


import org.joml.Vector4f;
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
import java.util.ArrayList;

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
    private IntBuffer elementBuffer;
    private Camera camera;
    private IntBuffer VertexIndicesBuffer;
    private int eboID;

    // constructors
    public RenderEngine(WindowManager manager, ShaderObject so, Camera camera){
        this.so = so;
        this.camera = camera;
        this.manager = manager;
        this.rows = Spot.ROWS;
        this.columns = Spot.COLUMNS;
        initOpenGL();
    }
    // methods
    private void setupPGP(){

         // allocate NIO array for Vertex data
         this.positionStride = 3;
         this.textureStride = 2;
         this.vertexStride = (this.positionStride + this.textureStride ) * 4;
        createVertexArray();
//        mockvertexarray();
        this.VertexArrayObjectHandle = glGenVertexArrays();
        glBindVertexArray(this.VertexArrayObjectHandle);
        this.VertexBufferObjectHandle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VertexBufferObjectHandle);
        // setting vertex attribute pointers
        this.attributePointer0 = 0;
        this.attributePointer1 = 1;
        //this.attributePointer2 = 2;


        glVertexAttribPointer(this.attributePointer0, this.positionStride, GL_FLOAT, false,this.vertexStride, 0 );
        glEnableVertexAttribArray(this.attributePointer0);
        glVertexAttribPointer(this.attributePointer1, this.textureStride, GL_FLOAT, false, this.vertexStride, (long)positionStride * Float.BYTES);
        glEnableVertexAttribArray(this.attributePointer1);glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
        so.useProgram();
        so.loadMatrix4f("uProjMatrix", camera.getprojectionMatrix());
        so.loadMatrix4f("uViewMatrix", camera.getViewingMatrix());

//        glVertexAttribPointer(this.attributePointer2,this.colorStride, GL_FLOAT, false, this.vertexStride, 20);
//        glEnableVertexAttribArray(this.attributePointer2);

        // this argument represents the offset, in bytes. it should be set the
        // number of bytes of data for position coordinates. They are floats, 4 bytes, 3 coordinates, 4 * 3 = 12. If this doesn't work, you need to figure out what the correct offset is

    }
    private void initOpenGL(){
        setupPGP();
    }

    public void render(int framedelay) {
        Vector4f COLOR_FACTOR = new Vector4f(1.0f, 0.5f, 0.6f, 1.0f);

        while(!manager.isGlfwWindowClosed()){

            glfwPollEvents();
            glClearColor(1.0f, 1.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);
            so.loadMatrix4f("uProjMatrix", this.camera.getprojectionMatrix());
            so.loadMatrix4f("uViewMatrix", this.camera.getViewingMatrix());
            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) {
                    so.loadVector4f("COLOR_FACTOR", COLOR_FACTOR);
                    renderTile(row, column);
                }
            }
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
    public void renderTile(int row, int col) {
// Compute the vertexArray offset
        int va_offset = getVAVIndex(row, col); // vertex array offset of tile
        int[] rgVertexIndices = new int[] {va_offset, va_offset+1, va_offset+2,
                va_offset, va_offset+2, va_offset+3};
        VertexIndicesBuffer = BufferUtils.createIntBuffer(rgVertexIndices.length);
        VertexIndicesBuffer.put(rgVertexIndices).flip();
//        mockelementArray();
        this.eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, VertexIndicesBuffer, GL_STATIC_DRAW);
        glDrawElements(GL_TRIANGLES, rgVertexIndices.length, GL_UNSIGNED_INT, 0);
    }

    private int getVAVIndex(int row, int col) {
        return (row * columns + col) * 4; // todo figure out what vpt is
    }

    private void createVertexArray(){
        final float square_length = 0.09f;
        final float z = 0.0f;
        float x, y;
        final int verticespertile = 6;
        final int floatspervertex = 5;
        final float[] textureCoordinates = { // todo this can be shrunken to 2 entries, with .put(textureCoordinates,i, 2) replacing line 194
                0.0f, 1.0f,
                0.0f, 1.0f,
                0.0f, 1.0f,
                0.0f, 1.0f,
                0.0f, 1.0f,
                0.0f, 1.0f
        };
        this.floatBuffer = BufferUtils.createFloatBuffer(rows * columns * verticespertile * floatspervertex);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                float padding = 0.5f;
                x =    (col * square_length); // translate x and y based on which row, col we are one, in addition to the length of the square. TODO figure out how to add padding
                y =    (row * square_length);
                float[] vertexPositions = { // define a generic square and then translate it
                        // triangle 1
                        // bottom left
                        x, y , z,
//                        / bottom right
                        (x + square_length), y, z,
//                        / top left
                        x, y + square_length, z,
//                        / triangle 2
//                        / top left
                        x, y + square_length, z,
//                        / bottom right
                        (x + square_length), y, z,
//                        / top right
                        (x + square_length), y + square_length, z
                };
                System.out.println("====Box: ====");
                System.out.print("Triangle 1:\nVertex 0, bottom left:"+ vertexPositions[0] + ","+ vertexPositions[1]+ ","+ vertexPositions[2] +"\n" +
                        " Vertex 1, bottom right: "+ vertexPositions[3] + ","+ vertexPositions[4]+ ","+ vertexPositions[5] +"\n" +
                        " Vertex 2, top left: "+ vertexPositions[6] + ","+ vertexPositions[7]+ ","+ vertexPositions[8] +"\n" +
                        " Triangle 2:\nVertex 3, top left: "+ vertexPositions[9] + ","+ vertexPositions[10]+ ","+ vertexPositions[11] +"\n" +
                        " Vertex 4, bottom right: "+ vertexPositions[12] + ","+ vertexPositions[13]+ ","+ vertexPositions[14] +"\n" +
                        " Vertex 5, top right:"+ vertexPositions[15] + ","+ vertexPositions[16]+ ","+ vertexPositions[17]+"\n");
                for (int i = 0; i < 6; i++) {   // put vertex data, interleaved

                    this.floatBuffer.put(vertexPositions, i * 3, 3);
                    this.floatBuffer.put(textureCoordinates, i * 2, 2);
                }
            }
        }
        this.floatBuffer.flip();
    }
    private void mockelementArray(){ // counterclockwise order
        int[] elements = { 0, 2, 1};
        this.VertexIndicesBuffer = BufferUtils.createIntBuffer(elements.length);

        this.VertexIndicesBuffer.put(elements);
        VertexIndicesBuffer.flip();
        // 2  * 3   * 5
        //
        //  0 *   1 * 4
    }
    private void mockvertexarray(){
        float[] vertexes = {
                0,0,0,0,1f,

                0,0.5f,0,0,1,
                0.5f,0,0,0,1,
        };
        this.floatBuffer = BufferUtils.createFloatBuffer(vertexes.length);
        this.floatBuffer.put(vertexes);
        this.floatBuffer.flip();
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
    private void generatepositions(){
           final float z = 0f;
           final float square_length = 0.1f;
           for (int x = 0; x < Spot.win_width; x++) {
            for (int y = 0; y < Spot.win_height; y++) {

            }
        }
    }
}

