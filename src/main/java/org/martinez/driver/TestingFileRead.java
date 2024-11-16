package org.martinez.driver;

import org.martinez.utils.EZFileRead;

public class TestingFileRead {

    public static void main(String[] args) {
        EZFileRead reader = new EZFileRead("C:\\Users\\timef\\Documents\\Workspaces\\Java\\CSC133Assignment7\\src\\main\\resources\\shaders\\fs_0.glsl");
        System.out.println(reader.getNumLines());
        for (int i = 0; i < reader.getNumLines(); i++) {
            System.out.println("line: " + reader.getLine(i));
        }

    }

}
