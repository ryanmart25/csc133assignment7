package org.martinez.renderers;


import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import org.martinez.cameras.Camera;
import org.martinez.helper.ArrayWrapper;
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
        so.useProgram();
        // todo remove color component of vertexes

        so.loadMatrix4f("uProjMatrix", camera.getprojectionMatrix());
        so.loadMatrix4f("uViewMatrix", camera.getViewingMatrix());
         // allocate NIO array for Vertex data
         this.positionStride = 3;
         this.textureStride = 2;
         //this.colorStride = 4;
         this.vertexStride = (this.positionStride + this.textureStride) * 4;
        createVertexArray();

        this.VertexArrayObjectHandle = glGenVertexArrays();
        glBindVertexArray(this.VertexArrayObjectHandle);
        this.VertexBufferObjectHandle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VertexBufferObjectHandle);
        // setting vertex attribute pointers
        this.attributePointer0 = 0;
        this.attributePointer1 = 1;
        glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(this.attributePointer0, this.positionStride, GL_FLOAT, false,this.vertexStride, 0 );
        glEnableVertexAttribArray(this.attributePointer0);
        glVertexAttribPointer(this.attributePointer1, this.textureStride, GL_FLOAT, false, this.vertexStride, 12);
        glEnableVertexAttribArray(this.attributePointer1);
        // this argument represents the offset, in bytes. it should be set the
        // number of bytes of data for position coordinates. They are floats, 4 bytes, 3 coordinates, 4 * 3 = 12. If this doesn't work, you need to figure out what the correct offset is

    }
    private void initOpenGL(){
        //GL.createCapabilities();
        setupPGP();
    }

    public void render(int framedelay) {
        Vector4f COLOR_FACTOR = new Vector4f(0.2f, 0.4f, 0.6f, 1.0f);
        so.loadMatrix4f("uProjMatrix", this.camera.getprojectionMatrix());
        so.loadMatrix4f("UViewMatrix", this.camera.getViewingMatrix());
        while(!manager.isGlfwWindowClosed()){
            glfwPollEvents();
            glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);
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
        this.eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, VertexIndicesBuffer, GL_STATIC_DRAW);
        glDrawElements(GL_TRIANGLES, rgVertexIndices.length, GL_UNSIGNED_INT, 0);
    }

    private int getVAVIndex(int row, int col) {
        return (row * columns + col) * 4; // todo figure out what vpt is
    }

    private void createVertexArray(){ // todo, put vertexes in a standard array, then insert the array into the floatbuffer
        ArrayWrapper wrapper = new ArrayWrapper(rows * columns * 4 * 5); // todo refactor magic number
        final float square_length = 0.1f;
        final float z = 0.0f;

        float x, y;
        final float[] textureCoordinates = {
                0.0f, 1.0f
        };

        this.floatBuffer = BufferUtils.createFloatBuffer(rows * columns * 4 * 5); // todo refactor magic number
        // defining vertices for square.
        //          *
        //  *               *
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                x = col * square_length; // translate x and y based on which row, col we are one, in addition to the length of the square. TODO figure out how to add padding
                y = row * square_length;
                float[] vertexPositions = { // define a generic square and then translate it
                        // triangle 1
                        // bottom left
                        x, y , z,
                        // bottom right
                        x + square_length, y, z,
                        // top left
                        x, y + square_length, z,
                        // triangle 2
                        // top left
                        x, y + square_length, z,
                        // bottom right
                        x + square_length, y, z,
                        // top right
                        x + square_length, y + square_length, z
                };
                for (int i = 0; i < 18; i+=3) {   // put 4 vertices, data is interleaved
                    // insert a vertex
                        // position data. input i -> i + 2: position components of one vertex. i is used as an offset to look into vertexPositions
                        //
                    wrapper.append(vertexPositions[i]);
                    wrapper.append(vertexPositions[i + 1]);
                    wrapper.append(vertexPositions[ i + 2]);
                        //texture data
                    wrapper.append(textureCoordinates[0]);
                    wrapper.append(textureCoordinates[1]);
                }
            }
        }
        this.floatBuffer.put(wrapper.getArray());

        this.floatBuffer.flip();
    }
    private void elementArray(){ // counterclockwise order
        this.elementBuffer = BufferUtils.createIntBuffer(rows * columns * 6 * 9);

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) { // for each square
                int bottomLeft = row * (columns + 1) + column;
                int bottomRight = bottomLeft + 1;
                int topLeft = bottomLeft + (columns + 1);
                int topRight = topLeft + 1;
                elementBuffer.put(bottomLeft);
                elementBuffer.put(bottomRight);
                elementBuffer.put(topRight);
                elementBuffer.put(bottomLeft);
                elementBuffer.put(topRight);
                elementBuffer.put(topLeft);
            }
        }

        elementBuffer.flip();
        // 2  * 3   * 5
        //
        //  0 *   1 * 4
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

