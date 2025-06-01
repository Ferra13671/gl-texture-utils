package com.ferra13671.TextureUtils;

import org.lwjgl.opengl.GL11;

/**
 * @author Ferra13671
 * @LastUpdate 1.5
 */

public enum TextureFiltering {
    DEFAULT(GL11.GL_NEAREST),
    SMOOTH(GL11.GL_LINEAR);

    public final int id;

    TextureFiltering(int id) {
        this.id = id;
    }
}
