package com.ferra13671.TextureUtils.loader;

import com.ferra13671.TextureUtils.texture.ColorMode;
import com.ferra13671.TextureUtils.texture.TextureFiltering;
import com.ferra13671.TextureUtils.texture.TextureWrapping;
import com.ferra13671.TextureUtils.builder.GLTextureInfo;

/**
 * @author Ferra13671
 * @LastUpdate 1.6
 */

public interface TextureLoader {

    GLTextureInfo load(ColorMode colorMode, TextureFiltering filtering, TextureWrapping wrapping) throws Exception;

    boolean isUsingStb();
}
