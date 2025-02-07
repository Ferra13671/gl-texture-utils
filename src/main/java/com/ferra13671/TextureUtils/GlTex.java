package com.ferra13671.TextureUtils;

/**
 * @author Ferra13671
 * @LastUpdate 1.3
 */

public interface GlTex {

    void delete();

    void bind();

    void unBind();

    int getWidth();

    int getHeight();

    int getTexId();
}
