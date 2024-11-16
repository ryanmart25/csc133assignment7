package org.martinez.driver;

import org.martinez.managers.WindowManager;
import org.martinez.renderers.RenderEngine;
import org.martinez.shaders.ShaderObject;
import org.martinez.utils.Spot;

public class Driver {
    public static void main(String[] args) {
        System.out.println("starting");
        WindowManager manager = WindowManager.getInstance();
        ShaderObject shaderObject = new ShaderObject("C:\\Users\\timef\\Documents\\Workspaces\\Java\\CSC133Assignment7\\src\\main\\resources\\shaders\\fs_0.glsl",
                "C:\\Users\\timef\\Documents\\Workspaces\\Java\\CSC133Assignment7\\src\\main\\resources\\shaders\\vs_0.glsl");
        RenderEngine engine = new RenderEngine(manager,shaderObject);
        engine.render(Spot.FRAME_DELAY);
    }
}
