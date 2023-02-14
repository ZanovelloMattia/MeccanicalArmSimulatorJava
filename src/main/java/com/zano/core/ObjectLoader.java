package com.zano.core;

import com.zano.core.entity.Model;
import com.zano.core.utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class ObjectLoader {
    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

    public Model loadOBJModel(String filename){
        List<String> lines = Utils.readAllLine(filename);

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3i> faces = new ArrayList<>();

        for(String line : lines){
            String[] tokens = line.split("\\s+");
            switch (tokens[0]){
                case "v":
                    //vertices
                    Vector3f verticesVec = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    vertices.add(verticesVec);
                    break;
                case "vt":
                    //vertex textures
                    Vector2f textureVec = new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2])
                    );
                    textures.add(textureVec);
                    break;
                case "vn":
                    //vertex normals
                    Vector3f normalVec = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    normals.add(normalVec);
                    break;
                case "f":
                    //faces
                    for(int i = 1; i < tokens.length; i++) {
                        processFace(tokens[i], faces);
                    }
                    break;
                default:
                    break;
            }
        }
        List<Integer> indices = new ArrayList<>();
        float[] verticesArr = new float[vertices.size() * 3];
        int i = 0;
        for(Vector3f pos : vertices){
            verticesArr[i * 3] = pos.x;
            verticesArr[i * 3 + 1] = pos.y;
            verticesArr[i * 3 + 2] = pos.z;
            i++;
        }
        float[] texCoordArr = new float[vertices.size() * 2];
        float[] normalArr = new float[vertices.size() * 3];

        for (Vector3i face : faces){
            processVertex(face.x, face.y, face.z, textures, normals, indices, texCoordArr, normalArr);
        }

        int[] indicesArr = indices.stream().mapToInt((Integer v) -> v).toArray();

        return loadModel(verticesArr, texCoordArr, normalArr,indicesArr);
    }

    private static void processVertex(int pos, int texCoord, int normal, List<Vector2f> texCoordList,
                                      List<Vector3f> normalList, List<Integer> indicesList,
                                      float[] texCoordArr, float[] normalArr){
        indicesList.add(pos);

        if(texCoord >= 0){
            Vector2f texCoordVec = texCoordList.get(texCoord);
            texCoordArr[pos * 2] = texCoordVec.x;
            texCoordArr[pos * 2 + 1] = 1 - texCoordVec.y;
        }

        if(normal >= 0){
            Vector3f normalVec = normalList.get(normal);
            normalArr[pos * 3] = normalVec.x;
            normalArr[pos * 3 + 1] = normalVec.y;
            normalArr[pos * 3 + 2] = normalVec.z;
        }

    }

    private static void processFace(String token, List<Vector3i> faces){
        String[] lineToken = token.split("/");
        int length = lineToken.length;
        int pos = -1, coords = -1, normal = -1;
        pos = Integer.parseInt(lineToken[0]) - 1;
        if(length > 1){
            String textCoord = lineToken[1];
            coords = textCoord.length() > 0 ? Integer.parseInt(textCoord) - 1 : -1;
            if(length > 2)
                normal = Integer.parseInt(lineToken[2]) - 1;
        }
        Vector3i facesVec = new Vector3i(pos, coords, normal);
        faces.add(facesVec);
    }

    public Model loadModel(float[] vertices,float[] textureCoords, float[] normals, int[] indices){
        int id = createVAO();
        storeIndicesBudder(indices);
        storeDataInAtribList(0, 3, vertices);
        storeDataInAtribList(1,2,textureCoords);
        storeDataInAtribList(2,3,normals);
        unbind();
        return new Model(id, indices.length);
    }

    public Model loadModel(float[] vertices){
        int id = createVAO();
        storeDataInAtribList(0, 3, vertices);
        unbind();
        return new Model(id, vertices.length);
    }

    public int loadTexture(String filename) throws Exception{
        /*int width, height;
        ByteBuffer buffer;

        try(MemoryStack stack = MemoryStack.stackPush()){
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            buffer = STBImage.stbi_load(filename, w, h, c, 4);
            if(buffer == null)
                throw  new Exception("Image File " + filename + " not loaded" + STBImage.stbi_failure_reason());

            width = w.get();
            height = h.get();

        }
        int id = GL11.glGenTextures();
        textures.add(id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);
        return id;*/

        int[] pixels = null;
        int width = 0;
        int height = 0;
        try (InputStream in = Utils.class.getResourceAsStream(filename)){
            BufferedImage image = ImageIO.read(in);
            if(image == null)
                throw  new Exception("Image File " + filename + " not loaded");
            width = image.getWidth();
            height = image.getHeight();
            pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int[] data = new int[width * height];
        for (int i = 0; i < width * height; i++) {
            int a = (pixels[i] & 0xff000000) >> 24;
            int r = (pixels[i] & 0xff0000) >> 16;
            int g = (pixels[i] & 0xff00) >> 8;
            int b = (pixels[i] & 0xff);

            data[i] = a << 24 | b << 16 | g << 8 | r;
        }

        int result = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, result);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

        IntBuffer buffer = ByteBuffer.allocateDirect(data.length << 2).order(ByteOrder.nativeOrder()).asIntBuffer();
        buffer.put(data).flip();

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        textures.add(result);
        return result;

    }

    public int loadTextureCube(String[] filename) throws Exception{

        int result = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, result);
        for(int j = 0; j < filename.length; j++) {
            int[] pixels = null;
            int width = 0;
            int height = 0;
            try (InputStream in = Utils.class.getResourceAsStream(filename[j])) {
                BufferedImage image = ImageIO.read(in);
                if (image == null)
                    throw new Exception("Image File " + filename + " not loaded");
                width = image.getWidth();
                height = image.getHeight();
                pixels = new int[width * height];
                image.getRGB(0, 0, width, height, pixels, 0, width);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int[] data = new int[width * height];
            for (int i = 0; i < width * height; i++) {
                int a = (pixels[i] & 0xff000000) >> 24;
                int r = (pixels[i] & 0xff0000) >> 16;
                int g = (pixels[i] & 0xff00) >> 8;
                int b = (pixels[i] & 0xff);

                data[i] = a << 24 | b << 16 | g << 8 | r;
            }
            IntBuffer buffer = ByteBuffer.allocateDirect(data.length << 2).order(ByteOrder.nativeOrder()).asIntBuffer();
            buffer.put(data).flip();
            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + j, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        }

        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

        textures.add(result);
        return result;
    }

    private int createVAO(){
        int id = GL30.glGenVertexArrays();
        vaos.add(id);
        GL30.glBindVertexArray(id);
        return id;
    }

    private void storeIndicesBudder(int[] indices){
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private void storeDataInAtribList(int attribNo, int vertexCount, float[] data){
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attribNo, vertexCount, GL11.GL_FLOAT, false, 0, 0);
    }

    private void unbind(){
        GL30.glBindVertexArray(0);
    }

    public void cleanup(){
        for (int vao : vaos)
            GL30.glDeleteVertexArrays(vao);
        for (int vbo : vbos)
            GL30.glDeleteVertexArrays(vbo);
        for (int texture : textures)
            GL11.glDeleteTextures(texture);
    }
}
