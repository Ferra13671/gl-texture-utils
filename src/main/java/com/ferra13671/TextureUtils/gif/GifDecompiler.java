package com.ferra13671.TextureUtils.gif;

import com.ferra13671.TextureUtils.GifUtils.GifData;

import javax.imageio.stream.ImageInputStream;

/**
 * @author Ferra13671
 * @LastUpdate 1.6
 */

public interface GifDecompiler {

    GifData decompile(ImageInputStream iis) throws Exception;
}
