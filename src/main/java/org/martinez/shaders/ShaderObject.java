package org.martinez.shaders;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.martinez.utils.EZFileRead;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL20.*;

public class ShaderObject {

    private int shader_program = 0;
    private String vsSource;
    private String fsSource;
    private String vs_filename;
    private String fs_filename;

    public ShaderObject(String s, String l){ // this will still allocate memory no matter what
        this.vs_filename = s;
        this.fs_filename = l;
        setupShaders(s,l);
        //if(EZFileRead.doesFileExist(s) && EZFileRead.doesFileExist(l)){
        //
        //}else{
        //    throw new RuntimeException("ShaderObject.java: One or both files do not exist.");
        //}
    }
    private void setupShaders(String vertexShaderFilename, String fragmentShaderFilename){
        readShaderSourceCode(vertexShaderFilename);
        shader_program = glCreateProgram();
        int vs = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vs, vsSource);
        glCompileShader(vs);
        glAttachShader(shader_program, vs);
        int fs = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fs, fsSource);
        glCompileShader(fs);
        glAttachShader(shader_program, fs);
        glLinkProgram(shader_program);
    }
    private void readShaderSourceCode(String filename){ // todo remove parameter to fit purpose
        //EZFileRead reader = new EZFileRead(filename);
        //int lines = reader.getNumLines();
        //StringBuilder builder = new StringBuilder();
        //for (int i = 0; i < lines; i++) {
        //    builder.append(reader.getLine(i));
        //}
        //System.out.println(builder);
        //String out = builder.toString();
        //return builder.toString();

        vs_filename = System.getProperty("user.dir") + "\\src\\main\\resources\\shaders\\" + vs_filename;
         fs_filename = System.getProperty("user.dir") + "\\src\\main\\resources\\shaders\\" + fs_filename;
        try {
            vsSource = new String(Files.readAllBytes(Paths.get(vs_filename)));
            fsSource = new String(Files.readAllBytes(Paths.get(fs_filename)));
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error opening shader files: " + vs_filename + ", " + fs_filename;
        }
    }
    public void useProgram(){
        glUseProgram(this.shader_program);
    }
    public void loadMatrix4f(String strMatrixName, Matrix4f my_mat4) {
        int var_location = glGetUniformLocation(this.shader_program, strMatrixName);
        final int OGL_MATRIX_SIZE = 16;
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(OGL_MATRIX_SIZE);
        my_mat4.get(matrixBuffer);
        glUniformMatrix4fv(var_location, false, matrixBuffer);
    } // public void loadMatrix4f(...)
    public void loadVector4f(String strVec4Name, Vector4f my_vec4) {
        int var_location = glGetUniformLocation(this.shader_program, strVec4Name);
        int OGL_VEC4_SIZE = 4;
        FloatBuffer vec4Buffer = BufferUtils.createFloatBuffer(OGL_VEC4_SIZE);
        my_vec4.get(vec4Buffer);
        glUniform4fv(var_location, vec4Buffer);
    } // public void loadVec4f(...)
}
