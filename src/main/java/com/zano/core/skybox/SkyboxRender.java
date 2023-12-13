package com.zano.core.skybox;

import com.zano.core.Camera;
import com.zano.core.ObjectLoader;
import com.zano.core.ShaderManager;
import com.zano.core.WindowsManager;
import com.zano.core.entity.Model;
import com.zano.core.entity.Texture;
import com.zano.core.utils.Transformation;
import com.zano.core.utils.Utils;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class SkyboxRender {

    private ShaderManager shader;

    private final float SIZE = 500f;

    private final float[] VERTICES = {
            -SIZE,  SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            -SIZE,  SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE,  SIZE
    };

    private String[] TEXTURE_FILES = {"/resources/textures/SkyBox/positiveX.png",
            "/resources/textures/SkyBox/negativeX.png",
            "/resources/textures/SkyBox/positiveY.png",
            "/resources/textures/SkyBox/negativeY.png",
            "/resources/textures/SkyBox/positiveZ.png",
            "/resources/textures/SkyBox/negativeZ.png"};

    private Model cube;
    private ObjectLoader loader;

    public void init() throws Exception{
        shader = new ShaderManager();
        shader.createVertexShader(Utils.loadResource("/resources/shaders/skyboxVertexShader.vs"));
        shader.createFragmentShader(Utils.loadResource("/resources/shaders/skyboxFragmentShader.fs"));
        shader.link();
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");
    }

    public void bind(Model model){
        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, model.getTexture().getId());
    }

    public void unbind(){
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }


    public SkyboxRender() throws Exception {
        loader = new ObjectLoader();
        cube = loader.loadModel(VERTICES);
        cube.setTexture(new Texture(loader.loadTextureCube(TEXTURE_FILES)));
        init();
    }

    public void render(Camera camera, WindowsManager window){
        shader.bind();

        shader.setUniform("projectionMatrix", window.getProjectionMatrix());
        Matrix4f viewMatrix = Transformation.getViewMatrix(camera);
        viewMatrix.m30(0);
        viewMatrix.m31(0);
        viewMatrix.m32(0);
        shader.setUniform("viewMatrix", viewMatrix);
        bind(cube);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
        unbind();
        shader.unbind();
    }

}
