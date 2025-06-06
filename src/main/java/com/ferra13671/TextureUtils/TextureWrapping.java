package com.ferra13671.TextureUtils;

import org.lwjgl.opengl.GL11;

/**
 * @author Ferra13671
 * @LastUpdate 1.5
 */

public enum TextureWrapping {
    DEFAULT(GL11.GL_CLAMP),
    REPEAT(GL11.GL_REPEAT);

    public final int id;

    TextureWrapping(int id) {
        this.id = id;
    }
}
