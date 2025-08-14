package com.ferra13671.TextureUtils.gif;

import com.ferra13671.TextureUtils.GifUtils.GifUtils;

/**
 * @author Ferra13671
 * @LastUpdate 1.6
 */

public enum DecompileMode {
    DELTAS(GifUtils::decompileDeltas),
    FULL(GifUtils::decompileFull);

    public final GifDecompiler gifDecompiler;

    DecompileMode(GifDecompiler gifDecompiler) {
        this.gifDecompiler = gifDecompiler;
    }
}