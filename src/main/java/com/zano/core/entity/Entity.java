package com.zano.core.entity;

import com.zano.core.utils.Transformation;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Entity {

    private Model model;
    private Vector3f pos, rotation, rotOffSet;

    private HashMap<String, List<Vector3f>> entityTransforms;

    private List<Matrix4f> transformMatrixes;

    private float scale;

    public Entity(Model model, Vector3f pos, Vector3f rotation, float scale) {
        this.model = model;
        this.pos = pos;
        this.rotation = rotation;
        this.scale = scale;
        this.rotOffSet = new Vector3f(0,0,0);
        initEntityTransform();
        initTransformMatrixes();
    }

    private void initTransformMatrixes(){
        transformMatrixes = new ArrayList<>();
        transformMatrixes.add(Transformation.createTransformationMatrix(
                entityTransforms.get("pos").get(0),
                entityTransforms.get("rot").get(0),
                entityTransforms.get("rotOff").get(0),
                this.scale));
    }

    private void initEntityTransform(){
        entityTransforms = new HashMap<>();
        List<Vector3f> pos = new ArrayList<>();
        pos.add(this.pos);
        List<Vector3f> rot = new ArrayList<>();
        rot.add(this.rotation);
        List<Vector3f> rotOff = new ArrayList<>();
        rotOff.add(this.rotOffSet);

        entityTransforms.put("pos", pos);
        entityTransforms.put("rot", rot);
        entityTransforms.put("rotOff", rotOff);
    }

    public void incPos(float x, float y, float z){
        this.pos.x += x;
        this.pos.y += y;
        this.pos.z += z;
    }

    public void setPos(float x, float y, float z){
        this.pos.x = x;
        this.pos.y = y;
        this.pos.z = z;
    }

    public void incRotation(float x, float y, float z){
        this.rotation.x += x;
        this.rotation.y += y;
        this.rotation.z += z;
    }

    public void setRotation(float x, float y, float z){
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public void setRotOfset(float x, float y, float z) {
        this.rotOffSet.x = x;
        this.rotOffSet.y = y;
        this.rotOffSet.z = z;
    }

    public List<Matrix4f> getTransformMatrixes() {
        return transformMatrixes;
    }

    public void updateTransformMatrixes() {
        for(int i = 0; i < entityTransforms.get("pos").size(); i++){
            if(i >= transformMatrixes.size()){
                transformMatrixes.add(i, Transformation.createTransformationMatrix(
                        entityTransforms.get("pos").get(i),
                        entityTransforms.get("rot").get(i),
                        entityTransforms.get("rotOff").get(i),
                        this.scale));
            }else {
                transformMatrixes.set(i, Transformation.createTransformationMatrix(
                        entityTransforms.get("pos").get(i),
                        entityTransforms.get("rot").get(i),
                        entityTransforms.get("rotOff").get(i),
                        this.scale));
            }
        }
    }

    public void setTransformMatrixes(List<Matrix4f> transformMatrixes) {
        this.transformMatrixes = transformMatrixes;
    }

    public HashMap<String, List<Vector3f>> getEntityTransforms() {
        return entityTransforms;
    }

    public void addEntityTransforms(Vector3f pos, Vector3f rot, Vector3f rotOff) {
        entityTransforms.get("pos").add(pos);
        entityTransforms.get("rot").add(rot);
        entityTransforms.get("rotOff").add(rotOff);
    }

    public void setEntityTransforms(HashMap<String, List<Vector3f>> entityTransforms) {
        this.entityTransforms = entityTransforms;
    }

    public void incEntityTransformsRot(int index, float x, float y, float z) {

        Vector3f rot = entityTransforms.get("rot").get(index);
        rot.x += x;
        rot.y += y;
        rot.z += z;
        entityTransforms.get("rot").set(index, rot);
    }

    public Model getModel() {
        return model;
    }

    public Vector3f getPos() {
        return pos;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    public Vector3f getRotOffSet() {
        return rotOffSet;
    }
}
