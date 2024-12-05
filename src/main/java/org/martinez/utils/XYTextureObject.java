package org.martinez.utils;
/*
TODO:
Make this a generic class with separate init_texture() for 2D textures.
 */

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

public class XYTextureObject {
    private String texFilepath;
    private int texID;
    ByteBuffer texImage = null;

    public XYTextureObject(String filepath) {
        this.texFilepath = filepath;

        texID = glGenTextures();
        // If we don't enable blending, the transparent pixels in the texture
        // will render as dark pixels:
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        loadImageToTexture();
    }
        // are these two methods my interface for setting which texture to display?
        // initially: set texture to undiscovered texture // might not work
        // on click, check tile @ (row, column).
        //      if discovered:
        //          do nothing?
        //      if undiscovered & mine:
        //          set texture to mine
        //          render tile
        //      if undiscovered & gold:
        //          set texture to gold
        //          render tile
    public void bind_texture() {
        glBindTexture(GL_TEXTURE_2D, texID);
    }

    public void unbind_texture() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void loadImageToTexture() {
        IntBuffer texWidth    = BufferUtils.createIntBuffer(1);
        IntBuffer texHeight   = BufferUtils.createIntBuffer(1);
        IntBuffer texChannels = BufferUtils.createIntBuffer(1);

        texImage = stbi_load(texFilepath, texWidth, texHeight, texChannels, 0);
        if (texImage != null) {
            if (texChannels.get(0) == 4 ) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texWidth.get(0), texHeight.get(0),
                        0, GL_RGBA, GL_UNSIGNED_BYTE, texImage);
            } else if (texChannels.get(0) == 3) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, texWidth.get(0), texHeight.get(0),
                        0, GL_RGB, GL_UNSIGNED_BYTE, texImage);
            } else {
                assert false: "Error loading texture: images with " + texChannels.get(0) +
                        " channels is not supported";
            }
        }
        else {
            assert false : "Error loading the texture image \"" + texFilepath + "\" ";
        }
        // Now that the texture is loaded, the image memory can be released to OS:
        stbi_image_free(texImage);
    }  //  public void loadImageToTexture(...)

}
