package org.martinez.renderers;


import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

import org.martinez.Array;
import org.martinez.backend.Board;
import org.martinez.cameras.Camera;
import org.martinez.listeners.KeyboardListener;
import org.martinez.listeners.MouseListener;
import org.martinez.listeners.XYMouseListener;
import org.martinez.managers.WindowManager;
import org.martinez.shaders.ShaderObject;
import org.martinez.utils.Spot;
import org.martinez.utils.XYTextureObject;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Random;

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
    private XYMouseListener mouseListener;
    private Board board;
    private float radius = Spot.RADIUS;
    private int colorStride;
    private int attributePointer2;
    private IntBuffer elementBuffer;
    private Camera camera;
    private IntBuffer VertexIndicesBuffer;
    private int eboID;
    private float square_length;
    private float padding;
    private boolean firstRender = true;
    // constructors
    public RenderEngine(WindowManager manager, ShaderObject so, Camera camera, XYMouseListener mouseListener, Board board){
        this.so = so;
        this.camera = camera;
        this.manager = manager;
        this.mouseListener = mouseListener;
        this.board = board;
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

        this.VertexArrayObjectHandle = glGenVertexArrays();
        glBindVertexArray(this.VertexArrayObjectHandle);
        this.VertexBufferObjectHandle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VertexBufferObjectHandle);
        // setting vertex attribute pointers
        this.attributePointer0 = 0;
        this.attributePointer1 = 1;

        glVertexAttribPointer(this.attributePointer0, this.positionStride, GL_FLOAT, false,this.vertexStride, 0 );
        glEnableVertexAttribArray(this.attributePointer0);
        glVertexAttribPointer(this.attributePointer1, this.textureStride, GL_FLOAT, false, this.vertexStride, (long)positionStride * Float.BYTES);
        glEnableVertexAttribArray(this.attributePointer1);
        glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
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
        //Random random = new Random();
        int[][] gameBoard = this.board.getGameboard();
        Vector4f COLOR_FACTOR = new Vector4f(0.4f, 0.2f, 0.6f, 1.0f);
        so.loadMatrix4f("uProjMatrix", this.camera.getprojectionMatrix());
        so.loadMatrix4f("uViewMatrix", this.camera.getViewingMatrix());
        // initialize texture objects
        XYTextureObject undiscoveredTextureObject = new XYTextureObject("resources/textures/Bunny_1.PNG"); // todo resolve filepath
        XYTextureObject mineTextureObject = new XYTextureObject("resources/textures/Bunny_2.PNG");
        XYTextureObject goldTextureObject = new XYTextureObject("resources/textures/BunnyB_1.PNG");
        undiscoveredTextureObject.loadImageToTexture();
        mineTextureObject.loadImageToTexture();
        goldTextureObject.loadImageToTexture();
        while(!manager.isGlfwWindowClosed()){

            glfwPollEvents();
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);
            int clickedRow, clickedColumn; // get mouse click row, column
            clickedRow = (int) ((XYMouseListener.getX() - padding) / square_length + padding);
            clickedColumn = (int)(XYMouseListener.getY() - padding / square_length + padding);
            // resolve correct texture
            // if the tile is undiscovered
            if(firstRender){
                // we are rendering for the first time, use the undiscovered texture
                undiscoveredTextureObject.bind_texture();
                firstRender = false;
            }
            if(this.board.getState(clickedRow, clickedColumn) == 0){ // switch texture
                int tiletype = gameBoard[clickedRow][clickedColumn]; // 1 = mine | 0 == gold
                if(tiletype == 1){
                    // set texture to mine texture
                    System.out.printf("Tile at: (%2d, %2d) is a mine", clickedRow, clickedColumn);
                }
                if(tiletype == 0){
                    // set texture to gold
                    System.out.printf("Tile at: (%2d, %2d) is not a mine", clickedRow, clickedColumn);
                }
                this.board.setState(clickedRow, clickedColumn, tiletype); // set the state to discovered: mine or gold
            }
            // render all tiles
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
        return (row * columns + col) * 4;
    }

    private void createVertexArray(){
        final int verticespertile = 4;
        final int floatspervertex = 5;
        Array floatarray = new Array(rows * columns * verticespertile * floatspervertex);
        square_length = 100f;
        padding = 10f;
        final float z = 0.0f;
        float x , y ;

        final float[] textureCoordinates = {
                0.0f, 1.0f
        };
        this.floatBuffer = BufferUtils.createFloatBuffer(rows * columns * verticespertile * floatspervertex);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {

                x =    col * (square_length + padding) + padding; // translate x and y based on which row, col we are on, in addition to the length of the square.
                y =    row * (square_length + padding) + padding;
                if(x + square_length > Spot.win_width || y + square_length > Spot.win_height){
                    continue;
                }
                float[] vertexPositions = { // define a generic square and then translate it

                        // bottom right
                        (x + square_length) , y, z,
                        //top right
                        (x + square_length) , y + square_length, z,
                        // top left
                        x , y + square_length, z,
                        // bottom left
                        x , y , z

                        // top left
                        // x, y + square_length, z,
                        // bottom right
                        //(x + square_length), y, z,

                };
                System.out.println("====Box====");
                                System.out.print(" Vertex 0, bottom right:"+ vertexPositions[0] + ","+ vertexPositions[1]+ ","+ vertexPositions[2] +"\n" +
                                        " Vertex 1, top right: "+ vertexPositions[3] + ","+ vertexPositions[4]+ ","+ vertexPositions[5] +"\n" +
                                        " Vertex 2, top left: "+ vertexPositions[6] + ","+ vertexPositions[7]+ ","+ vertexPositions[8] + "\n" +
                        " Vertex 3, bottom left:"+ vertexPositions[9] + ","+ vertexPositions[10]+ ","+ vertexPositions[11]+"\n");
                for (int i = 0; i < vertexPositions.length; i++) {
                    floatarray.append(vertexPositions[i]); // append positions
                    if( i == 2 || i == 5 || i == 8 || i == 11){ // append textures 
                        floatarray.append(textureCoordinates[0]);
                        floatarray.append(textureCoordinates[1]);
                    }
                }
            }
        }
        System.out.println(floatarray);
        this.floatBuffer.put(floatarray.getArray());
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

