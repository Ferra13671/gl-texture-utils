package com.ferra13671.TextureUtils.texture;

import com.ferra13671.TextureUtils.Controller.GlController;
import com.ferra13671.TextureUtils.GLTextureSystem;
import com.ferra13671.TextureUtils.GlTex;
import com.ferra13671.TextureUtils.builder.GLTextureInfo;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.stb.STBImage.nstbi_image_free;

/**
 * @author Ferra13671
 * @LastUpdate 1.6
 */

public class GLTexture implements GlTex {
    protected int texId;

    protected String name;
    protected int width;
    protected int height;

    protected GLTexture(String name) {
        this.name = name;
        GLTextureSystem.addTexture(this);
    }

    private GLTexture create(GLTextureInfo glTextureInfo) {
        GlController controller = GLTextureSystem.getGlController();

        texId = controller.genTexId();

        long bufferAddress = MemoryUtil.memAddress(glTextureInfo.getPixels());

        this.width = glTextureInfo.getWidth();
        this.height = glTextureInfo.getHeight();

        controller.bindTexture(texId);
        controller.texParameter(GL11.GL_TEXTURE_2D, 33085, 0);
        controller.texParameter(GL11.GL_TEXTURE_2D, 33082, 0);
        controller.texParameter(GL11.GL_TEXTURE_2D, 33083, 0);
        controller.texParameter(GL11.GL_TEXTURE_2D, 34049, 0.0F);
        applyFiltering(controller, glTextureInfo.getFiltering());
        applyWrapping(controller, glTextureInfo.getWrapping());

        controller.texImage2D(GL11.GL_TEXTURE_2D, 0, glTextureInfo.getColorMode().glId, width, height, 0, glTextureInfo.getColorMode().glId, 5121, null);
        controller.pixelStore(GL11.GL_UNPACK_ROW_LENGTH, 0);
        controller.pixelStore(GL11.GL_UNPACK_SKIP_PIXELS, 0);
        controller.pixelStore(GL11.GL_UNPACK_SKIP_ROWS, 0);
        controller.pixelStore(GL11.GL_UNPACK_ALIGNMENT, 4);
        controller.texSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, width, height, glTextureInfo.getColorMode().glId, 5121, bufferAddress);

        controller.bindTexture(0);

        if (glTextureInfo.isUsingStb())
            nstbi_image_free(bufferAddress);
        else
            MemoryUtil.memFree(glTextureInfo.getPixels());

        return this;
    }

    private void applyFiltering(GlController controller, TextureFiltering textureFiltering) {
        controller.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, textureFiltering.id);
        controller.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, textureFiltering.id);
    }

    private void applyWrapping(GlController controller, TextureWrapping textureWrapping) {
        controller.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, textureWrapping.id);
        controller.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, textureWrapping.id);
    }

    /**
     * Removes the com.ferra13671.TextureUtils.texture from video memory and returns null.
     * Use this for times when the com.ferra13671.TextureUtils.texture is no longer needed to free up some video memory.
     */
    @Override
    public void delete() {
        GLTextureSystem.getGlController().deleteTexture(texId);
        GLTextureSystem.removeTexture(this);
    }

    @Override
    public void bind() {
        GLTextureSystem.getGlController().bindTexture(getTexId());
    }

    @Override
    public void unBind() {
        GLTextureSystem.getGlController().bindTexture(0);
    }

    public String getName() {
        return name;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getTexId() {
        return this.texId;
    }

    @Override
    public int getWidth() {
        return width;
    }

    public static GLTexture of(String name, GLTextureInfo glTextureInfo) {
        return new GLTexture(name).create(glTextureInfo);
    }
}
