package org.martinez.shaders;

import org.martinez.utils.EZFileRead;

public class ShaderObject {
    private String vertexShaderFilename;
    private String fragmentShaderFilename;
    private String vertexShaderSourceString;
    private String fragmentShaderSourceString;
    public ShaderObject(String s, String l){
        this.fragmentShaderFilename = l;
        this.vertexShaderFilename = s;
    }
    public void setupShaders(){

    }
    private String readShaderSourceCode(String filename){
        EZFileRead reader = new EZFileRead(filename);
        int lines = reader.getNumLines();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines; i++) {
            builder.append(reader.getLine(i));
        }
        return null;
    }
    private void compileShader(){

    }
    private void setShader(){

    }
}
