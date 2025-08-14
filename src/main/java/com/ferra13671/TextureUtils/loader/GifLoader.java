package com.ferra13671.TextureUtils.loader;

import com.ferra13671.TextureUtils.builder.GLGifInfo;
import com.ferra13671.TextureUtils.gif.DecompileMode;

/**
 * @author Ferra13671
 * @LastUpdate 1.6
 */

public interface GifLoader {

    GLGifInfo load(DecompileMode decompileMode) throws Exception;
}
