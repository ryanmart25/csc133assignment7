package org.martinez.driver;

import org.martinez.backend.Board;
import org.martinez.backend.BoardTwo;
import org.martinez.cameras.Camera;
import org.martinez.listeners.XYMouseListener;
import org.martinez.managers.WindowManager;
import org.martinez.renderers.RenderEngine;
import org.martinez.shaders.ShaderObject;
import org.martinez.utils.SpotTwo;

import java.util.Random;

public class Driver {
    public static void main(String[] args) {
        System.out.println("starting");
        WindowManager manager = WindowManager.getInstance();
        Camera camera = new Camera();
        BoardTwo board = new BoardTwo(SpotTwo.ROWS, SpotTwo.COLUMNS, SpotTwo.NUMMINES);
        XYMouseListener listener = XYMouseListener.getInstance();
        ShaderObject shaderObject = new ShaderObject("vs_texture_1.glsl",
                "fs_texture_1.glsl");
        RenderEngine engine = new RenderEngine(manager,shaderObject, camera, listener, board);


        engine.render(SpotTwo.FRAME_DELAY);
    }
}
