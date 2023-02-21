package com.zano.core.render;

import com.zano.core.Camera;
import com.zano.core.ShaderManager;
import com.zano.core.WindowsManager;
import com.zano.core.entity.Entity;
import com.zano.core.entity.Material;
import com.zano.core.entity.Model;
import com.zano.core.lighting.SunLight;
import com.zano.core.skybox.SkyboxRender;
import com.zano.core.utils.Consts;
import com.zano.core.utils.Transformation;
import com.zano.core.utils.Utils;
import com.zano.test.Launcher;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRender {

    private Map<Model, List<Entity>> entities = new HashMap<>();

    private ShaderManager shader;
    private WindowsManager window;

    private Material material;

    private SkyboxRender skybox;

    public MasterRender(){
        window = Launcher.getWindow();
    }

    public void init() throws Exception{
        shader = new ShaderManager();
        shader.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
        shader.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
        shader.link();
        shader.createUniform("textureSampler");
        shader.createUniform("transformationMatrix");
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");
        shader.createUniform("lightPosition");
        shader.createSunLightUniform("sunlight");
        shader.createMaterialUniform("material");
        for(int i = 0; i < Consts.MAX_LIGHTS; i++) {
            shader.createUniform("lightPosition" + "[" + i + "]");
        }
        skybox = new SkyboxRender();
    }

    public void renderMaterial(Material material){
        shader.setUniform("material", material);
    }

    public void renderLight(List<SunLight> sunLights){
        shader.setUniform("sunlight", sunLights);
        for(int i = 0; i < Consts.MAX_LIGHTS; i++) {
            if(i < sunLights.size()) {
                shader.setUniform("lightPosition" + "[" + i + "]", sunLights.get(i).getDirection());
            }
            else
                shader.setUniform("lightPosition" + "[" + i + "]", 0);
        }
    }

    public void bind(Model model){
        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getId());
    }

    public void unbind(){
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    public void prepare(Entity entity, Camera camera){
        shader.setUniform("textureSampler", 0);
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
        entity.updateTransformMatrixes();
        Matrix4f trasformationMatrix = Transformation.mulMatrix4f(entity.getTransformMatrixes());
        shader.setUniform("transformationMatrix", trasformationMatrix);
    }

    public void render(Camera camera, List<SunLight> sunLights) throws Exception {
        clear();
        shader.bind();
        //GL11.glViewport(0,0,window.getWidth()/2, window.getHeight());
        shader.setUniform("projectionMatrix", window.updateProjectionMatrix());
        renderLight(sunLights);


        material = new Material();
        for(Model model : entities.keySet()){
            bind(model);
            List<Entity> entityList = entities.get(model);
            for(Entity entity : entityList){
                prepare(entity, camera);
                renderMaterial(entity.getModel().getMaterial());
                GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            unbind();
        }
        skybox.render(camera, window);
        entities.clear();
        shader.unbind();
    }

    public void processEntity(Entity entity) {
        List<Entity> entityList = entities.get(entity.getModel());
        if (entityList != null){
            entityList.add(entity);
        }
        else{
            List<Entity> newEntityList = new ArrayList<>();
            newEntityList.add(entity);
            entities.put(entity.getModel(), newEntityList);
        }
    }

    public void clear(){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup(){
        shader.cleanup();
    }

}
