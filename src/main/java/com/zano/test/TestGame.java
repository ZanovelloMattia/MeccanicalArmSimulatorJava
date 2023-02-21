package com.zano.test;

import com.zano.core.*;
import com.zano.core.entity.Entity;
import com.zano.core.entity.Model;
import com.zano.core.entity.Texture;
import com.zano.core.lighting.SunLight;
import com.zano.core.render.MasterRender;
//import com.zano.core.RenderManager;
import com.zano.core.terrains.Terrain;
import com.zano.core.utils.Consts;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class TestGame implements ILogic {

    //private final RenderManager render;
    private final MasterRender render;
    private final ObjectLoader loader;
    private final WindowsManager window;

    private List<Entity> entities;
    private Camera camera;

    Vector3f cameraInc;

    private float lightAngle;
    private List<SunLight> sunLights = new ArrayList<>();

    private Terrain terrain;

    public TestGame() {
        render = new MasterRender();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        cameraInc = new Vector3f(0,0,0);
        lightAngle = -90;
    }

    @Override
    public void init() throws Exception {
        render.init();

        Model model = loader.loadOBJModel("/models/arm1.obj");
        model.setTexture(new Texture(loader.loadTexture("/textures/verdeAcqua.png")) , 1f);

        Model terrainModel = new Terrain(0,0, loader).getModel();
        terrainModel.setTexture(new Texture(loader.loadTexture("/textures/white.png")), 1f);

        Model modelArmBase = loader.loadOBJModel("/models/ArmBase.obj");
        modelArmBase.setTexture(new Texture(loader.loadTexture("/textures/verdeAcqua.png")));

        Model modelRotationArmBase = loader.loadOBJModel("/models/ArmRotationBase.obj");
        modelRotationArmBase.setTexture(new Texture(loader.loadTexture("/textures/verdeAcqua.png")));

        Model modelArm1 = loader.loadOBJModel("/models/Arm1.obj");
        modelArm1.setTexture(new Texture(loader.loadTexture("/textures/verdeAcqua.png")));

        Model modelArm2 = loader.loadOBJModel("/models/Arm2.obj");
        modelArm2.setTexture(new Texture(loader.loadTexture("/textures/verdeAcqua.png")));

        Model modelArm3 = loader.loadOBJModel("/models/Arm3.obj");
        modelArm3.setTexture(new Texture(loader.loadTexture("/textures/verdeAcqua.png")));

        Model modelArm4 = loader.loadOBJModel("/models/Arm4.obj");
        modelArm4.setTexture(new Texture(loader.loadTexture("/textures/verdeAcqua.png")));

        Model dragon = loader.loadOBJModel("/models/dragon.obj");
        dragon.setTexture(new Texture(loader.loadTexture("/textures/verdeAcqua.png")));

        entities = new ArrayList<>();

        entities.add(new Entity(terrainModel, new Vector3f(200, 0, 200), new Vector3f(0,0,0), 1));
        entities.get(0).getModel().getMaterial().setReflectance(0);
        entities.add(new Entity(modelArmBase, new Vector3f(0, -.5f, 2), new Vector3f(0,0,0), 1));
        entities.add(new Entity(modelRotationArmBase, new Vector3f(0, .25f, 2), new Vector3f(0,0,0), 1));
        entities.add(new Entity(modelArm1, new Vector3f(0, 0.47f, 2), new Vector3f(0,0,0), 1));
        entities.add(new Entity(modelArm2, new Vector3f(.51f, 1.40f, 2  ), new Vector3f(0,0,0), 1));
        entities.add(new Entity(modelArm3, new Vector3f(-.85f, 1.915f, 1.92f), new Vector3f(0,0,0), 1));
        entities.add(new Entity(modelArm4, new Vector3f(-2.5f, 1.915f, 1.92f), new Vector3f(0,0,0), 1));

        sunLights.add(new SunLight(new Vector3f(1f, 0.9f, 1f), new Vector3f(100, 100, 100), 0.5f));
        sunLights.add(new SunLight(new Vector3f(1f, 1f, 0.9f), new Vector3f(-100, 100, -100), 0.2f));
        sunLights.add(new SunLight(new Vector3f(0.9f, 1f, 1f), new Vector3f(-100, 100, 100), 0.3f));
        entities.get(2).setRotOfset(0f,0f,1f);
        entities.get(3).setRotOfset(0f,0.5f,1f);
        entities.get(4).setRotOfset(-0.5f,-0.43f,1f);
        entities.get(5).setRotOfset(0.85f,-0.94f,1.08f);
        entities.get(6).setRotOfset(2.5f,-0.94f,1.08f);

        entities.get(4).addEntityTransforms(new Vector3f(0,0,0), new Vector3f(0,0,0), new Vector3f(0f,0f,0f));
        entities.get(5).addEntityTransforms(new Vector3f(0,0,0), new Vector3f(0,0,0), new Vector3f(2.75f,-1.0325f,0f));
        entities.get(6).addEntityTransforms(new Vector3f(0,0,0), new Vector3f(0,0,0), new Vector3f(6.05f,-1.03f,0f));

        entities.get(5).addEntityTransforms(new Vector3f(0,0,0), new Vector3f(0,0,0), new Vector3f(0f,0f,0f));
        entities.get(6).addEntityTransforms(new Vector3f(0,0,0), new Vector3f(0,0,0), new Vector3f(3.3f,0f,0f));

        entities.get(6).addEntityTransforms(new Vector3f(0,0,0), new Vector3f(0,0,0), new Vector3f(0,0,-1f));
    }

    @Override
    public void input() {
        cameraInc.set(0,0,0);
        if (window.isKeyPressed(GLFW.GLFW_KEY_W))
            cameraInc.z = -1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_S))
            cameraInc.z = 1;

        if (window.isKeyPressed(GLFW.GLFW_KEY_A))
            cameraInc.x = -1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_D))
            cameraInc.x = 1;

        if (window.isKeyPressed(GLFW.GLFW_KEY_Q))
            cameraInc.y = -1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_E))
            cameraInc.y = 1;

        if(window.isKeyPressed(GLFW.GLFW_KEY_T)){
            entities.get(2).incRotation(0,0.2f,0);
            entities.get(3).incRotation(0,0.2f,0);
            entities.get(4).incRotation(0,0.2f,0);
            entities.get(5).incRotation(0,0.2f,0);
            entities.get(6).incRotation(0,0.2f,0);
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_Y)){
            entities.get(2).incRotation(0,-0.2f,0);
            entities.get(3).incRotation(0,-0.2f,0);
            entities.get(4).incRotation(0,-0.2f,0);
            entities.get(5).incRotation(0,-0.2f,0);
            entities.get(6).incRotation(0,-0.2f,0);
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_U ) && entities.get(3).getRotation().z < 90){
            entities.get(3).incRotation(0,0,0.2f);
            entities.get(4).incRotation(0,0,0.2f);
            entities.get(5).incRotation(0,0,0.2f);
            entities.get(6).incRotation(0,0,0.2f);
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_I) && entities.get(3).getRotation().z > -20){
            entities.get(3).incRotation(0,0,-0.2f);
            entities.get(4).incRotation(0,0,-0.2f);
            entities.get(5).incRotation(0,0,-0.2f);
            entities.get(6).incRotation(0,0,-0.2f);
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_G )){// && entities.get(3).getRotation().z < 90){
            entities.get(4).incEntityTransformsRot(1,0,0,0.2f);
            entities.get(5).incEntityTransformsRot(1,0,0,0.2f);
            entities.get(6).incEntityTransformsRot(1,0,0,0.2f);
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_H)){// && entities.get(3).getRotation().z > -20){
            entities.get(4).incEntityTransformsRot(1,0,0,-0.2f);
            entities.get(5).incEntityTransformsRot(1,0,0,-0.2f);
            entities.get(6).incEntityTransformsRot(1,0,0,-0.2f);
        }

        if(window.isKeyPressed(GLFW.GLFW_KEY_J )){// && entities.get(3).getRotation().z < 90){
            entities.get(5).incEntityTransformsRot(2,0,0,0.2f);
            entities.get(6).incEntityTransformsRot(2,0,0,0.2f);
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_K)){// && entities.get(3).getRotation().z > -20){
            entities.get(5).incEntityTransformsRot(2,0,0,-0.2f);
            entities.get(6).incEntityTransformsRot(2,0,0,-0.2f);
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_V)){// && entities.get(3).getRotation().z > -20){
            entities.get(6).incEntityTransformsRot(3,0,0.2f,0);
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_B)){// && entities.get(3).getRotation().z > -20){
            entities.get(6).incEntityTransformsRot(3,0,-0.2f,0);
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_N)){// && entities.get(3).getRotation().z > -20){
            entities.get(6).incEntityTransformsRot(3,0,0,0.2f);
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_M)){// && entities.get(3).getRotation().z > -20){
            entities.get(6).incEntityTransformsRot(3,0,0,-0.2f);
        }
    }

    @Override
    public void update(MouseInput mouseInput) {
        camera.movePosition(cameraInc.x * Consts.CAMERA_MOVE_SPEED, cameraInc.y * Consts.CAMERA_MOVE_SPEED, cameraInc.z * Consts.CAMERA_MOVE_SPEED);

        if(mouseInput.isRightButtonPress()){
            Vector2f rotVex = mouseInput.getDisplVec();
            camera.moveRotation(rotVex.x * Consts.MOUSE_SENSITIVITY, rotVex.y * Consts.MOUSE_SENSITIVITY, 0);
        }

        for(Entity entity : entities){
            render.processEntity(entity);
        }

    }

    @Override
    public void render() throws Exception{
        if(window.isResize()){
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResize(true);
        }

        render.render(camera, sunLights);
    }

    @Override
    public void cleanup() {
        render.cleanup();
        loader.cleanup();
    }
}
