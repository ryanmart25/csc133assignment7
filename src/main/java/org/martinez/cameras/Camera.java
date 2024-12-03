package org.martinez.cameras;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.martinez.utils.Spot;

public class Camera {

    public Camera(){

    }
    public Matrix4f getprojectionMatrix(){
        Matrix4f projmatrix = new Matrix4f();
        projmatrix.identity();
        projmatrix.ortho(0.0f, (float)Spot.win_width, 0.0f, (float)Spot.win_height, 0.0f, 100.0f);
        return projmatrix;
    }
    public Matrix4f getViewingMatrix(){
        Matrix4f viewmatrix = new Matrix4f();
        viewmatrix.identity();
        Vector3f lookfrom = new Vector3f(0f, 0f, 100f);
        Vector3f lookat = new Vector3f(0f, 0f, -1f);
        Vector3f upVector = new Vector3f(0f, 1f, 0f);
        viewmatrix.lookAt( lookfrom,  lookat.add(lookfrom), upVector);
        return viewmatrix;
    }
}
