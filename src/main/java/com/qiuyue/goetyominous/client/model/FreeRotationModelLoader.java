package com.qiuyue.goetyominous.client.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.math.Transformation;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class FreeRotationModelLoader implements IGeometryLoader<FreeRotationModelLoader.Geometry> {

    @Override
    public Geometry read(JsonObject json, JsonDeserializationContext context) throws JsonParseException {
        JsonArray elements = null;
        if (json.has("template")) {
            JsonObject template = this.loadTemplate(new ResourceLocation(json.get("template").getAsString()));
            elements = template.getAsJsonArray("elements");
        } else if (json.has("elements")) {
            elements = json.getAsJsonArray("elements");
        }
        if (elements == null) {
            throw new JsonParseException("Missing elements");
        }
        return new Geometry(this.parseElements(elements));
    }

    private JsonObject loadTemplate(ResourceLocation location) {
        ResourceLocation file = new ResourceLocation(location.getNamespace(), "models/" + location.getPath() + ".json");
        Resource resource = Minecraft.getInstance().getResourceManager().getResource(file)
                .orElseThrow(() -> new JsonParseException("Missing template: " + file));
        try (Reader reader = new InputStreamReader(resource.open(), StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception exception) {
            throw new JsonParseException("Failed to read template: " + file, exception);
        }
    }

    private List<Face> parseElements(JsonArray elements) {
        List<Face> faces = new ArrayList<>();
        for (JsonElement elementElement : elements) {
            JsonObject element = elementElement.getAsJsonObject();
            Vector3f from = readVector(element, "from").mul(0.0625F);
            Vector3f to = readVector(element, "to").mul(0.0625F);
            Vector3f origin = new Vector3f(8.0F, 8.0F, 8.0F).mul(0.0625F);
            Quaternionf rotation = new Quaternionf();
            boolean rotated = false;
            if (element.has("rotation")) {
                JsonObject rotationJson = element.getAsJsonObject("rotation");
                if (rotationJson.has("origin")) {
                    origin = readVector(rotationJson, "origin").mul(0.0625F);
                }
                if (rotationJson.has("x") || rotationJson.has("y") || rotationJson.has("z")) {
                    float x = getFloat(rotationJson, "x");
                    float y = getFloat(rotationJson, "y");
                    float z = getFloat(rotationJson, "z");
                    rotation.rotateY((float) Math.toRadians(y));
                    rotation.rotateX((float) Math.toRadians(x));
                    rotation.rotateZ((float) Math.toRadians(z));
                } else if (rotationJson.has("axis") && rotationJson.has("angle")) {
                    String axis = rotationJson.get("axis").getAsString();
                    float angle = rotationJson.get("angle").getAsFloat();
                    float ax = axis.equals("x") ? 1.0F : 0.0F;
                    float ay = axis.equals("y") ? 1.0F : 0.0F;
                    float az = axis.equals("z") ? 1.0F : 0.0F;
                    rotation.rotateAxis((float) Math.toRadians(angle), ax, ay, az);
                }
                rotated = true;
            }
            boolean shade = !element.has("shade") || element.get("shade").getAsBoolean();
            if (!element.has("faces")) {
                continue;
            }
            JsonObject facesJson = element.getAsJsonObject("faces");
            for (Map.Entry<String, JsonElement> entry : facesJson.entrySet()) {
                Direction direction = Direction.byName(entry.getKey());
                if (direction == null) {
                    continue;
                }
                JsonObject faceJson = entry.getValue().getAsJsonObject();
                if (!faceJson.has("uv")) {
                    continue;
                }
                JsonArray uvJson = faceJson.getAsJsonArray("uv");
                float[] uv = new float[4];
                for (int i = 0; i < 4; i++) {
                    uv[i] = uvJson.get(i).getAsFloat();
                }
                int uvRotation = faceJson.has("rotation") ? faceJson.get("rotation").getAsInt() : 0;
                BlockFaceUV blockFaceUV = new BlockFaceUV(uv, uvRotation);
                String texture = faceJson.has("texture") ? faceJson.get("texture").getAsString() : "#0";
                int tint = faceJson.has("tintindex") ? faceJson.get("tintindex").getAsInt() : -1;
                List<Vertex> vertices = new ArrayList<>(4);
                FaceInfo faceInfo = FaceInfo.fromFacing(direction);
                for (int i = 0; i < 4; i++) {
                    FaceInfo.VertexInfo info = faceInfo.getVertexInfo(i);
                    float x = info.xFace == FaceInfo.Constants.MAX_X ? to.x : from.x;
                    float y = info.yFace == FaceInfo.Constants.MAX_Y ? to.y : from.y;
                    float z = info.zFace == FaceInfo.Constants.MAX_Z ? to.z : from.z;
                    Vector3f position = new Vector3f(x, y, z);
                    if (rotated) {
                        position.sub(origin).rotate(rotation).add(origin);
                    }
                    float u = blockFaceUV.getU(i);
                    float v = blockFaceUV.getV(i);
                    float u2 = blockFaceUV.getU((i + 2) % 4);
                    float v2 = blockFaceUV.getV((i + 2) % 4);
                    vertices.add(new Vertex(position, u * 0.999F + u2 * 0.001F, v * 0.999F + v2 * 0.001F));
                }
                Vector3f normal = new Vector3f(direction.getStepX(), direction.getStepY(), direction.getStepZ());
                if (rotated) {
                    normal.rotate(rotation);
                }
                faces.add(new Face(direction, texture.startsWith("#") ? texture.substring(1) : texture,
                        tint, shade, vertices, normal));
            }
        }
        return faces;
    }

    private static Vector3f readVector(JsonObject json, String key) {
        JsonArray array = json.getAsJsonArray(key);
        return new Vector3f(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
    }

    private static float getFloat(JsonObject json, String key) {
        return json.has(key) ? json.get(key).getAsFloat() : 0.0F;
    }

    public record Vertex(Vector3f position, float u, float v) {
    }

    public record Face(Direction direction, String texture, int tintIndex, boolean shade, List<Vertex> vertices,
                       Vector3f normal) {
    }

    @OnlyIn(Dist.CLIENT)
    public static class Geometry implements IUnbakedGeometry<Geometry> {
        private final List<Face> faces;

        public Geometry(List<Face> faces) {
            this.faces = faces;
        }

        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBaker baker,
                               Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState,
                               ItemOverrides overrides, ResourceLocation modelLocation) {
            Transformation modelTransform = modelState.getRotation();
            Matrix4f matrix = modelTransform.getMatrix();
            Matrix3f normalMatrix = modelTransform.getNormalMatrix();
            List<BakedQuad> quads = new ArrayList<>();
            for (Face face : this.faces) {
                Material material = context.hasMaterial(face.texture())
                        ? context.getMaterial(face.texture()) : context.getMaterial("0");
                TextureAtlasSprite sprite = spriteGetter.apply(material);
                Vector3f normal = new Vector3f(face.normal());
                normalMatrix.transform(normal);
                if (normal.lengthSquared() > 1.0E-5F) {
                    normal.normalize();
                }
                Direction direction = Direction.getNearest(normal.x, normal.y, normal.z);
                QuadBakingVertexConsumer builder = new QuadBakingVertexConsumer(quads::add);
                builder.setSprite(sprite);
                builder.setTintIndex(face.tintIndex());
                builder.setDirection(direction);
                builder.setShade(face.shade());
                builder.setHasAmbientOcclusion(context.useAmbientOcclusion());
                for (Vertex vertex : face.vertices()) {
                    Vector3f position = new Vector3f(vertex.position());
                    position.sub(0.5F, 0.5F, 0.5F);
                    matrix.transformPosition(position);
                    position.add(0.5F, 0.5F, 0.5F);
                    builder.vertex(position.x, position.y, position.z);
                    builder.color(255, 255, 255, 255);
                    builder.uv(sprite.getU(vertex.u()), sprite.getV(vertex.v()));
                    builder.normal(normal.x, normal.y, normal.z);
                    builder.endVertex();
                }
            }
            Map<Direction, List<BakedQuad>> culled = new EnumMap<>(Direction.class);
            for (Direction direction : Direction.values()) {
                culled.put(direction, List.of());
            }
            Material particleMaterial = context.hasMaterial("particle")
                    ? context.getMaterial("particle") : context.getMaterial("0");
            TextureAtlasSprite particle = spriteGetter.apply(particleMaterial);
            return new SimpleBakedModel(quads, culled, context.useAmbientOcclusion(), context.useBlockLight(),
                    context.isGui3d(), particle, context.getTransforms(), overrides);
        }
    }
}
