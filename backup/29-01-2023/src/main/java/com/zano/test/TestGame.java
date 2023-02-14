package com.zano.test;

import com.zano.core.*;
import com.zano.core.entity.Entity;
import com.zano.core.entity.Model;
import com.zano.core.entity.Texture;
import com.zano.core.lighting.SunLight;
import com.zano.core.render.MasterRender;
import com.zano.core.RenderManager;
import com.zano.core.terrains.Terrain;
import com.zano.core.utils.Consts;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestGame implements ILogic {

    //private final RenderManager render;
    private final MasterRender render;
    private final ObjectLoader loader;
    private final WindowsManager window;

    private List<Entity> entities;
    private Camera camera;

    Vector3f cameraInc;

    private float lightAngle;
    private SunLight sunLight;

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
        model.setTexture(new Texture(loader.loadTexture("textures/verdeAcqua.png")) , 1f);
        Model terrainModel = new Terrain(0,0, loader).getModel();
        terrainModel.setTexture(new Texture(loader.loadTexture("textures/verdeAcqua.png")), 1f);
        entities = new ArrayList<>();
        float x = 0;
        float y = 0;
        float z = -5;
        for(int i = 0; i < 1; i++){
            x += 3;
            entities.add(new Entity(model, new Vector3f(x, y, z), new Vector3f(0,0,0), 1));
        }

        entities.add(new Entity(terrainModel, new Vector3f(0, -1, 0), new Vector3f(0,0,0), 1));

        float lightIntensity = 0.2f;
        Vector3f lightPosition = new Vector3f(100, 100, 100);
        Vector3f lightColour = new Vector3f(1f, 1f, 1f);
        sunLight = new SunLight(lightColour, lightPosition, lightIntensity);

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
    public void render() {
        if(window.isResize()){
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResize(true);
        }
        entities.get(0).incRotation(0,0,0.2f);

        render.render(camera, sunLight);
    }

    @Override
    public void cleanup() {
        render.cleanup();
        loader.cleanup();
    }
}
