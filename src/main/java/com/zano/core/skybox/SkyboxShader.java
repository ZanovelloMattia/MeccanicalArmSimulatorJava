package com.zano.core.skybox;

import com.zano.core.ShaderManager;
import com.zano.core.utils.Consts;
import com.zano.core.utils.Utils;

public class SkyboxShader {

    ShaderManager shader;

    public void init() throws Exception{
        shader = new ShaderManager();
        shader.createVertexShader(Utils.loadResource("/shaders/skyboxVertexShader.vs"));
        shader.createFragmentShader(Utils.loadResource("/shaders/skyboxFragmentShader.fs"));
        shader.link();
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");
    }

}
