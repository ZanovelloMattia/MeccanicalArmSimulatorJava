package com.zano.core.skybox;

import com.zano.core.ShaderManager;
import com.zano.core.utils.Utils;

public class SkyboxShader {

    ShaderManager shader;

    public void init() throws Exception{
        shader = new ShaderManager();
        shader.createVertexShader(Utils.loadResource("/resources/shaders/skyboxVertexShader.vs"));
        shader.createFragmentShader(Utils.loadResource("/resources/shaders/skyboxFragmentShader.fs"));
        shader.link();
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");
    }

}
