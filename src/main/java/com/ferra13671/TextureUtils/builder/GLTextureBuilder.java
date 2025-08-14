package com.ferra13671.TextureUtils.builder;

import com.ferra13671.TextureUtils.texture.ColorMode;
import com.ferra13671.TextureUtils.texture.GLTexture;
import com.ferra13671.TextureUtils.texture.TextureFiltering;
import com.ferra13671.TextureUtils.texture.TextureWrapping;
import com.ferra13671.TextureUtils.loader.TextureLoader;

/**
 * @author Ferra13671
 * @LastUpdate 1.6
 */

public class GLTextureBuilder {
    private String name = null;
    private TextureLoader loader = null;
    private ColorMode colorMode = ColorMode.RGBA;
    private TextureFiltering filtering = null;
    private TextureWrapping wrapping = null;

    private GLTextureBuilder() {}

    public GLTextureBuilder name(String name) {
        this.name = name;
        return this;
    }

    public GLTextureBuilder loader(TextureLoader loader) {
        this.loader = loader;
        return this;
    }

    public GLTextureBuilder colorMode(ColorMode colorMode) {
        this.colorMode = colorMode;
        return this;
    }

    public GLTextureBuilder filtering(TextureFiltering filtering) {
        this.filtering = filtering;
        return this;
    }

    public GLTextureBuilder wrapping(TextureWrapping wrapping) {
        this.wrapping = wrapping;
        return this;
    }

    public GLTexture build() {
        try {
            checkArguments();
            return GLTexture.of(name, loader.load(colorMode, filtering, wrapping));
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private void checkArguments() {
        if (name == null)
            throw new IllegalArgumentException("Name cannot be null.");
        if (loader == null)
            throw new IllegalArgumentException(String.format("Loader in texture '%s' cannot be null.", name));
        if (colorMode == null)
            throw new IllegalArgumentException(String.format("ColorMode in texture '%s' cannot be null.", name));
        if (filtering == null)
            throw new IllegalArgumentException(String.format("TextureFiltering in texture '%s' cannot be null.", name));
        if (wrapping == null)
            throw new IllegalArgumentException(String.format("TextureWrapping in texture '%s' cannot be null.", name));
    }

    public static GLTextureBuilder builder() {
        return new GLTextureBuilder();
    }
}
