package com.zano.test;

import com.zano.core.*;
import com.zano.core.entity.Entity;
import com.zano.core.entity.Model;
import com.zano.core.entity.Texture;
import com.zano.core.lighting.DirectionalLight;
import com.zano.core.utils.Consts;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class TestGame implements ILogic {



        private int direction = 0;

        private int antiDirection = 0;
        private int antiantiDirection = 0;
        private float colour = 0.0f;

        private float antiColour = 0.0f;

        private float antiantiColour = 0.0f;
    private final RenderManager render;
    private final ObjectLoader loader;
    private final WindowsManager window;

    private Entity entity;
    private Camera camera;

    Vector3f cameraInc;

    private float lightAngle;
    private DirectionalLight directionalLight;

    public TestGame() {
        render = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        cameraInc = new Vector3f(0,0,0);
        lightAngle = -90;
    }

    @Override
    public void init() throws Exception {
        render.init();

        Model model = loader.loadOBJModel("/models/dragon.obj");
        model.setTexture(new Texture(loader.loadTexture("textures/verdeAcqua.png")) , 1f);
        entity = new Entity(model, new Vector3f(0, -4.5f, -15), new Vector3f(0, 0, 0), 1);

        float lightIntensity = 0.0f;
        Vector3f lightPosition = new Vector3f(-1, -10, 0);
        Vector3f lightColour = new Vector3f(1, 1, 1);
        directionalLight = new DirectionalLight(lightColour, lightPosition, lightIntensity);
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

        entity.incRotation(0.0f, 0.25f, 0.0f);
        directionalLight.setIntensity(1);
        directionalLight.getDirection().x = -100;
        directionalLight.getDirection().y = 50;

    }

    @Override
    public void render() {
        if(window.isResize()){
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResize(true);
        }
        render.render(entity, camera, directionalLight);
    }

    @Override
    public void cleanup() {
        render.cleanup();
        loader.cleanup();
    }
}
