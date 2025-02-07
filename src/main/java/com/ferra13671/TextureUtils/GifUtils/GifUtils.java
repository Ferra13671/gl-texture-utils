package com.ferra13671.TextureUtils.GifUtils;

import com.ferra13671.TextureUtils.GifUtils.gif.GIFImageReader;
import com.ferra13671.TextureUtils.GifUtils.gif.GIFImageReaderSpi;

import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GifUtils {

    public static List<BufferedImage> decompileFull(ImageInputStream inputStream) throws IOException {
        List<BufferedImage> copies = new ArrayList<>();
        List<BufferedImage> frames = decompileDeltas(inputStream);
        copies.add(frames.remove(0));
        for (BufferedImage frame : frames) {
            BufferedImage img = new BufferedImage(copies.get(0).getWidth(),
                    copies.get(0).getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics g = img.getGraphics();
            g.drawImage(copies.get(copies.size()-1),0,0,null);
            g.drawImage(frame,0,0,null);
            copies.add(img);
        }
        return copies;
    }

    public static List<BufferedImage> decompileDeltas(ImageInputStream inputStream) throws IOException {
        List<BufferedImage> frames = new ArrayList<>();
        ImageReader ir = new GIFImageReader(new GIFImageReaderSpi());
        ir.setInput(inputStream);
        for(int i = 0; i < ir.getNumImages(true); i++)
            frames.add(ir.read(i));
        return frames;
    }
}
