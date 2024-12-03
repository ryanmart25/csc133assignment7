package org.martinez.shaders;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.martinez.utils.EZFileRead;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class ShaderObject {

    private int shader_program = 0;

    public ShaderObject(String s, String l){ // this will still allocate memory no matter what

        if(EZFileRead.doesFileExist(s) && EZFileRead.doesFileExist(l)){
            setupShaders(s,l);
        }else{
            throw new RuntimeException("ShaderObject.java: One or both files do not exist.");
        }
    }
    private void setupShaders(String vertexShaderFilename, String fragmentShaderFilename){

        shader_program = glCreateProgram();
        int vs = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vs, readShaderSourceCode(vertexShaderFilename));
        glCompileShader(vs);
        glAttachShader(shader_program, vs);
        int fs = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fs, readShaderSourceCode(fragmentShaderFilename));
        glCompileShader(fs);
        glAttachShader(shader_program, fs);
        glLinkProgram(shader_program);
    }
    private String readShaderSourceCode(String filename){
        EZFileRead reader = new EZFileRead(filename);
        int lines = reader.getNumLines();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines; i++) {
            builder.append(reader.getLine(i));
        }
        return builder.toString();
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
