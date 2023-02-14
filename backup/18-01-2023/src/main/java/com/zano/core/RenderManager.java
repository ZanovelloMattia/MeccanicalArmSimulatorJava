package com.zano.core;

import com.zano.core.entity.Entity;
import com.zano.core.entity.Model;
import com.zano.core.lighting.DirectionalLight;
import com.zano.core.utils.Consts;
import com.zano.core.utils.Transformation;
import com.zano.core.utils.Utils;
import com.zano.test.Launcher;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenderManager {

    private final WindowsManager window;
    private ShaderManager shader;

    private Map<Model, List<Entity>> entities = new HashMap<>();

    public RenderManager(){
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
        shader.createUniform("ambientLight");
        shader.createUniform("lightPosition");
        shader.createUniform("colourLight");
        shader.createUniform("reflectivity");
        shader.createUniform("reflectancePow");
        //shader.createUniform("fragToCameraVector");
        //shader.createMaterialUniform("material");
    }

    public void bind(Model model){
        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        //shader.setUniform("material", model.getMaterial());
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
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(entity));
    }

    public void renderLight(Camera camera, DirectionalLight directionalLight){
        shader.setUniform("ambientLight", Consts.AMBIENT_LIGHT);
        shader.setUniform("colourLight", directionalLight.getColour());
        shader.setUniform("lightPosition", directionalLight.getDirection());
        //shader.setUniform("fragToCameraVector", camera.getPosition());
    }

    public void render(Camera camera, DirectionalLight directionalLight){
        clear();
        shader.bind();

        shader.setUniform("projectionMatrix", window.updateProjectionMatrix());
        shader.setUniform("reflectancePow", Consts.SPECULAR_POWER);
        shader.setUniform("reflectivity", Consts.reflectivity);
        renderLight(camera, directionalLight);
        for(Model model : entities.keySet()){
            bind(model);
            List<Entity> entityList = entities.get(model);
            for(Entity entity : entityList){
                prepare(entity, camera);
                GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            unbind();
        }
        entities.clear();
        shader.unbind();
    }

    public void processEntity(Entity entity){
        List<Entity> entityList = entities.get(entity.getModel());
        if(entityList != null)
            entityList.add(entity);
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
