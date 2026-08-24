/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.model.HierarchicalModel
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.ItemStack
 */
package com.vivideru.masteryofmagic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public final class TimeFreezeRenderAnimationState {
    private static final Set<Integer> CLIENT_FROZEN_ENTITIES = ConcurrentHashMap.newKeySet();
    private static final Set<Integer> CLIENT_ANIMATION_FROZEN_ENTITIES = ConcurrentHashMap.newKeySet();
    private static final Map<Integer, RenderState> RENDER_STATES = new ConcurrentHashMap<Integer, RenderState>();
    private static final ThreadLocal<Integer> CURRENT_RENDERING_ENTITY_ID = new ThreadLocal();
    private static final Set<Integer> CLIENT_TIME_FREEZE_IMMUNE_ENTITIES = ConcurrentHashMap.newKeySet();

    private TimeFreezeRenderAnimationState() {
    }

    public static void setFrozen(int entityId, boolean frozen) {
        if (frozen) {
            if (CLIENT_TIME_FREEZE_IMMUNE_ENTITIES.contains(entityId)) {
                CLIENT_FROZEN_ENTITIES.remove(entityId);
                if (!CLIENT_ANIMATION_FROZEN_ENTITIES.contains(entityId)) {
                    RENDER_STATES.remove(entityId);
                }
                return;
            }
            CLIENT_FROZEN_ENTITIES.add(entityId);
        } else {
            CLIENT_FROZEN_ENTITIES.remove(entityId);
            if (!CLIENT_ANIMATION_FROZEN_ENTITIES.contains(entityId)) {
                RENDER_STATES.remove(entityId);
            }
        }
    }

    public static void setAnimationFrozen(int entityId, boolean frozen) {
        if (frozen) {
            CLIENT_ANIMATION_FROZEN_ENTITIES.add(entityId);
        } else {
            CLIENT_ANIMATION_FROZEN_ENTITIES.remove(entityId);
            if (!CLIENT_FROZEN_ENTITIES.contains(entityId)) {
                RENDER_STATES.remove(entityId);
            }
        }
    }

    public static void setTimeFreezeImmune(int entityId, boolean immune) {
        if (immune) {
            CLIENT_TIME_FREEZE_IMMUNE_ENTITIES.add(entityId);
            CLIENT_FROZEN_ENTITIES.remove(entityId);
            if (!CLIENT_ANIMATION_FROZEN_ENTITIES.contains(entityId)) {
                RENDER_STATES.remove(entityId);
            }
        } else {
            CLIENT_TIME_FREEZE_IMMUNE_ENTITIES.remove(entityId);
        }
    }

    public static boolean isFrozen(int entityId) {
        return CLIENT_FROZEN_ENTITIES.contains(entityId);
    }

    public static boolean isAnimationFrozen(int entityId) {
        return CLIENT_FROZEN_ENTITIES.contains(entityId) || CLIENT_ANIMATION_FROZEN_ENTITIES.contains(entityId);
    }

    public static void beginRenderEntity(Entity entity, float partialTick) {
        RenderState state = TimeFreezeRenderAnimationState.getState(entity.m_19879_());
        CURRENT_RENDERING_ENTITY_ID.set(entity.m_19879_());
        if (!TimeFreezeRenderAnimationState.isAnimationFrozen(entity.m_19879_())) {
            state.entityFieldStates.clear();
            state.frozenTickCount = entity.f_19797_;
            state.renderPartialTick = partialTick;
            state.hasRenderPartialTick = true;
            TimeFreezeRenderAnimationState.captureEntityFields(entity, state.entityFieldStates);
            state.hasEntityFieldState = true;
            return;
        }
        if (!state.hasEntityFieldState) {
            state.entityFieldStates.clear();
            TimeFreezeRenderAnimationState.captureEntityFields(entity, state.entityFieldStates);
            state.frozenTickCount = entity.f_19797_;
            state.renderPartialTick = partialTick;
            state.hasRenderPartialTick = true;
            state.hasEntityFieldState = true;
        }
        state.runtimeEntityFieldStates.clear();
        TimeFreezeRenderAnimationState.captureEntityFields(entity, state.runtimeEntityFieldStates);
        entity.f_19797_ = state.frozenTickCount;
        TimeFreezeRenderAnimationState.applyEntityFields(entity, state.entityFieldStates);
    }

    public static void endRenderEntity(Entity entity) {
        RenderState state = TimeFreezeRenderAnimationState.getState(entity.m_19879_());
        if (TimeFreezeRenderAnimationState.isAnimationFrozen(entity.m_19879_())) {
            TimeFreezeRenderAnimationState.applyEntityFields(entity, state.runtimeEntityFieldStates);
        }
        CURRENT_RENDERING_ENTITY_ID.remove();
    }

    public static float getFrozenPartialTick(float currentPartialTick) {
        Integer entityId = CURRENT_RENDERING_ENTITY_ID.get();
        if (entityId == null) {
            return currentPartialTick;
        }
        RenderState state = TimeFreezeRenderAnimationState.getState(entityId);
        if (!TimeFreezeRenderAnimationState.isAnimationFrozen(entityId)) {
            state.renderPartialTick = currentPartialTick;
            state.hasRenderPartialTick = true;
            return currentPartialTick;
        }
        if (!state.hasRenderPartialTick) {
            state.renderPartialTick = currentPartialTick;
            state.hasRenderPartialTick = true;
        }
        return state.renderPartialTick;
    }

    public static RenderState getPrepareState(int entityId, float limbSwing, float limbSwingAmount, float partialTick) {
        RenderState state = TimeFreezeRenderAnimationState.getState(entityId);
        if (!TimeFreezeRenderAnimationState.isAnimationFrozen(entityId) || !state.hasPrepareState) {
            state.prepareLimbSwing = limbSwing;
            state.prepareLimbSwingAmount = limbSwingAmount;
            state.preparePartialTick = partialTick;
            state.hasPrepareState = true;
        }
        return state;
    }

    public static RenderState getSetupState(int entityId, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        RenderState state = TimeFreezeRenderAnimationState.getState(entityId);
        if (!TimeFreezeRenderAnimationState.isAnimationFrozen(entityId) || !state.hasSetupState) {
            state.setupLimbSwing = limbSwing;
            state.setupLimbSwingAmount = limbSwingAmount;
            state.ageInTicks = ageInTicks;
            state.netHeadYaw = netHeadYaw;
            state.headPitch = headPitch;
            state.hasSetupState = true;
        }
        return state;
    }

    public static void captureLivingState(int entityId, float attackAnim, float oAttackAnim, boolean usingItem, int useItemRemainingTicks, int ticksUsingItem, ItemStack useItem) {
        RenderState state = TimeFreezeRenderAnimationState.getState(entityId);
        if (!TimeFreezeRenderAnimationState.isAnimationFrozen(entityId) || !state.hasLivingState) {
            state.attackAnim = attackAnim;
            state.oAttackAnim = oAttackAnim;
            state.usingItem = usingItem;
            state.useItemRemainingTicks = useItemRemainingTicks;
            state.ticksUsingItem = ticksUsingItem;
            state.useItem = useItem.m_41777_();
            state.hasLivingState = true;
        }
    }

    public static float getFrozenAgeInTicks(int entityId, float currentAgeInTicks) {
        RenderState state = TimeFreezeRenderAnimationState.getState(entityId);
        if (!TimeFreezeRenderAnimationState.isAnimationFrozen(entityId) || !state.hasAgeState) {
            state.ageInTicks = currentAgeInTicks;
            state.hasAgeState = true;
            return currentAgeInTicks;
        }
        return state.ageInTicks;
    }

    public static void captureOrApplyModelPose(int entityId, EntityModel<?> model) {
        RenderState state = TimeFreezeRenderAnimationState.getState(entityId);
        List<ModelPart> modelParts = TimeFreezeRenderAnimationState.collectModelParts(model);
        List<Object> advancedParts = TimeFreezeRenderAnimationState.collectAdvancedModelParts(model);
        if (!TimeFreezeRenderAnimationState.isAnimationFrozen(entityId)) {
            state.modelPartStates.clear();
            state.advancedPartStates.clear();
            for (ModelPart modelPart : modelParts) {
                state.modelPartStates.put(modelPart, ModelPartState.capture(modelPart));
            }
            for (Object advancedPart : advancedParts) {
                state.advancedPartStates.put(advancedPart, ReflectivePartState.capture(advancedPart));
            }
            return;
        }
        if (state.modelPartStates.isEmpty() && state.advancedPartStates.isEmpty()) {
            for (ModelPart modelPart : modelParts) {
                state.modelPartStates.put(modelPart, ModelPartState.capture(modelPart));
            }
            for (Object advancedPart : advancedParts) {
                state.advancedPartStates.put(advancedPart, ReflectivePartState.capture(advancedPart));
            }
            return;
        }
        for (ModelPart modelPart : modelParts) {
            ModelPartState modelPartState = state.modelPartStates.get(modelPart);
            if (modelPartState == null) continue;
            modelPartState.apply(modelPart);
        }
        for (Object advancedPart : advancedParts) {
            ReflectivePartState advancedPartState = state.advancedPartStates.get(advancedPart);
            if (advancedPartState == null) continue;
            advancedPartState.apply(advancedPart);
        }
    }

    private static List<Object> collectAdvancedModelParts(EntityModel<?> model) {
        ArrayList<Object> parts = new ArrayList<Object>();
        try {
            Method method = TimeFreezeRenderAnimationState.findNoArgMethod(model.getClass(), "getAllParts");
            method.setAccessible(true);
            Object value = method.invoke(model, new Object[0]);
            if (value instanceof Iterable) {
                Iterable iterable = (Iterable)value;
                for (Object part : iterable) {
                    if (part == null || !TimeFreezeRenderAnimationState.isAdvancedModelPart(part) || parts.contains(part)) continue;
                    parts.add(part);
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return parts;
    }

    private static boolean isAdvancedModelPart(Object object) {
        for (Class<?> currentClass = object.getClass(); currentClass != null && currentClass != Object.class; currentClass = currentClass.getSuperclass()) {
            String className = currentClass.getName();
            if (!className.contains("AdvancedModelBox") && !className.contains("BasicModelPart")) continue;
            return true;
        }
        return false;
    }

    private static Method findNoArgMethod(Class<?> clazz, String methodName) throws NoSuchMethodException {
        for (Class<?> currentClass = clazz; currentClass != null && currentClass != Object.class; currentClass = currentClass.getSuperclass()) {
            for (Method method : currentClass.getDeclaredMethods()) {
                if (!method.getName().equals(methodName) || method.getParameterCount() != 0) continue;
                return method;
            }
        }
        throw new NoSuchMethodException(methodName);
    }

    private static void captureEntityFields(Entity entity, Map<Field, Object> output) {
        for (Class<?> currentClass = entity.getClass(); currentClass != null && currentClass != Object.class; currentClass = currentClass.getSuperclass()) {
            for (Field field : currentClass.getDeclaredFields()) {
                if (!TimeFreezeRenderAnimationState.shouldFreezeEntityField(field)) continue;
                try {
                    field.setAccessible(true);
                    output.put(field, field.get(entity));
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
    }

    private static void applyEntityFields(Entity entity, Map<Field, Object> states) {
        for (Map.Entry<Field, Object> entry : states.entrySet()) {
            try {
                entry.getKey().setAccessible(true);
                entry.getKey().set(entity, entry.getValue());
            }
            catch (Exception exception) {}
        }
    }

    private static boolean shouldFreezeEntityField(Field field) {
        int modifiers = field.getModifiers();
        if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
            return false;
        }
        Class<?> type = field.getType();
        return type.isPrimitive();
    }

    private static List<ModelPart> collectModelParts(EntityModel<?> model) {
        ArrayList<ModelPart> modelParts = new ArrayList<ModelPart>();
        if (model instanceof HierarchicalModel) {
            HierarchicalModel hierarchicalModel = (HierarchicalModel)model;
            hierarchicalModel.m_142109_().m_171331_().forEach(modelPart -> TimeFreezeRenderAnimationState.addModelPart(modelParts, modelPart));
        }
        for (Class<?> currentClass = model.getClass(); currentClass != null && currentClass != Object.class; currentClass = currentClass.getSuperclass()) {
            for (Field field : currentClass.getDeclaredFields()) {
                if (!ModelPart.class.isAssignableFrom(field.getType())) continue;
                try {
                    field.setAccessible(true);
                    Object value = field.get(model);
                    if (!(value instanceof ModelPart)) continue;
                    ModelPart modelPart2 = (ModelPart)value;
                    modelPart2.m_171331_().forEach(part -> TimeFreezeRenderAnimationState.addModelPart(modelParts, part));
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
        return modelParts;
    }

    private static void addModelPart(List<ModelPart> modelParts, ModelPart modelPart) {
        if (!modelParts.contains(modelPart)) {
            modelParts.add(modelPart);
        }
    }

    public static RenderState getState(int entityId) {
        return RENDER_STATES.computeIfAbsent(entityId, unused -> new RenderState());
    }

    public static void clear() {
        CLIENT_FROZEN_ENTITIES.clear();
        CLIENT_ANIMATION_FROZEN_ENTITIES.clear();
        CLIENT_TIME_FREEZE_IMMUNE_ENTITIES.clear();
        RENDER_STATES.clear();
    }

    public static final class RenderState {
        public boolean hasPrepareState;
        public boolean hasSetupState;
        public boolean hasAgeState;
        public boolean hasLivingState;
        public boolean hasEntityFieldState;
        public float prepareLimbSwing;
        public float prepareLimbSwingAmount;
        public float preparePartialTick;
        public float setupLimbSwing;
        public float setupLimbSwingAmount;
        public float ageInTicks;
        public float netHeadYaw;
        public float headPitch;
        public float attackAnim;
        public float oAttackAnim;
        public boolean usingItem;
        public int useItemRemainingTicks;
        public int ticksUsingItem;
        public ItemStack useItem = ItemStack.f_41583_;
        public int frozenTickCount;
        public boolean hasRenderPartialTick;
        public float renderPartialTick;
        public final Map<Object, ReflectivePartState> advancedPartStates = new ConcurrentHashMap<Object, ReflectivePartState>();
        public final Map<Field, Object> entityFieldStates = new ConcurrentHashMap<Field, Object>();
        public final Map<Field, Object> runtimeEntityFieldStates = new ConcurrentHashMap<Field, Object>();
        public final Map<ModelPart, ModelPartState> modelPartStates = new IdentityHashMap<ModelPart, ModelPartState>();
    }

    private static final class ModelPartState {
        private final float x;
        private final float y;
        private final float z;
        private final float xRot;
        private final float yRot;
        private final float zRot;
        private final float xScale;
        private final float yScale;
        private final float zScale;
        private final boolean visible;
        private final boolean skipDraw;

        private ModelPartState(float x, float y, float z, float xRot, float yRot, float zRot, float xScale, float yScale, float zScale, boolean visible, boolean skipDraw) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.xRot = xRot;
            this.yRot = yRot;
            this.zRot = zRot;
            this.xScale = xScale;
            this.yScale = yScale;
            this.zScale = zScale;
            this.visible = visible;
            this.skipDraw = skipDraw;
        }

        private static ModelPartState capture(ModelPart modelPart) {
            return new ModelPartState(modelPart.f_104200_, modelPart.f_104201_, modelPart.f_104202_, modelPart.f_104203_, modelPart.f_104204_, modelPart.f_104205_, modelPart.f_233553_, modelPart.f_233554_, modelPart.f_233555_, modelPart.f_104207_, modelPart.f_233556_);
        }

        private void apply(ModelPart modelPart) {
            modelPart.f_104200_ = this.x;
            modelPart.f_104201_ = this.y;
            modelPart.f_104202_ = this.z;
            modelPart.f_104203_ = this.xRot;
            modelPart.f_104204_ = this.yRot;
            modelPart.f_104205_ = this.zRot;
            modelPart.f_233553_ = this.xScale;
            modelPart.f_233554_ = this.yScale;
            modelPart.f_233555_ = this.zScale;
            modelPart.f_104207_ = this.visible;
            modelPart.f_233556_ = this.skipDraw;
        }
    }

    private static final class ReflectivePartState {
        private final Map<Field, Object> values;

        private ReflectivePartState(Map<Field, Object> values) {
            this.values = values;
        }

        private static ReflectivePartState capture(Object part) {
            HashMap<Field, Object> values = new HashMap<Field, Object>();
            for (Class<?> currentClass = part.getClass(); currentClass != null && currentClass != Object.class; currentClass = currentClass.getSuperclass()) {
                for (Field field : currentClass.getDeclaredFields()) {
                    if (!ReflectivePartState.shouldCaptureField(field)) continue;
                    try {
                        field.setAccessible(true);
                        values.put(field, field.get(part));
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
            return new ReflectivePartState(values);
        }

        private void apply(Object part) {
            for (Map.Entry<Field, Object> entry : this.values.entrySet()) {
                try {
                    entry.getKey().setAccessible(true);
                    entry.getKey().set(part, entry.getValue());
                }
                catch (Exception exception) {}
            }
        }

        private static boolean shouldCaptureField(Field field) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                return false;
            }
            Class<?> type = field.getType();
            return type == Float.TYPE || type == Double.TYPE || type == Integer.TYPE || type == Boolean.TYPE;
        }
    }
}

