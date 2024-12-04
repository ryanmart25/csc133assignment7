package org.martinez.driver;

import org.martinez.cameras.Camera;
import org.martinez.managers.WindowManager;
import org.martinez.renderers.RenderEngine;
import org.martinez.shaders.ShaderObject;
import org.martinez.utils.Spot;

public class Driver {
    public static void main(String[] args) {
        System.out.println("starting");
        WindowManager manager = WindowManager.getInstance();
        Camera camera = new Camera();
        ShaderObject shaderObject = new ShaderObject("vs_texture_1.glsl",
                "fs_texture_1.glsl");
        RenderEngine engine = new RenderEngine(manager,shaderObject, camera);
        engine.render(Spot.FRAME_DELAY);
    }
}
