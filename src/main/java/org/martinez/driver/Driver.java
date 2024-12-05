package org.martinez.driver;

import org.martinez.backend.Board;
import org.martinez.cameras.Camera;
import org.martinez.listeners.XYMouseListener;
import org.martinez.managers.WindowManager;
import org.martinez.renderers.RenderEngine;
import org.martinez.shaders.ShaderObject;
import org.martinez.utils.Spot;
import org.martinez.utils.XYTextureObject;

import java.util.Random;

public class Driver {
    public static void main(String[] args) {
        System.out.println("starting");
        WindowManager manager = WindowManager.getInstance();
        Camera camera = new Camera();
        Board board = new Board(new Random());
        XYMouseListener listener = XYMouseListener.get();
        ShaderObject shaderObject = new ShaderObject("vs_texture_1.glsl",
                "fs_texture_1.glsl");
        RenderEngine engine = new RenderEngine(manager,shaderObject, camera, listener, board);
        XYTextureObject undiscoveredTextureObject = new XYTextureObject("texture.png"); // todo resolve filepath
        XYTextureObject mineTextureObject = new XYTextureObject("texture.png");
        XYTextureObject goldTextureObject = new XYTextureObject("texture.png");
        undiscoveredTextureObject.loadImageToTexture();
        mineTextureObject.loadImageToTexture();
        goldTextureObject.loadImageToTexture();

        engine.render(Spot.FRAME_DELAY);
    }
}
