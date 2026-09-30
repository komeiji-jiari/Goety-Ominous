package com.qiuyue.goetyominous.common.events;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XRayHandler {

    private static final long CLIENT_RAY_TIMEOUT = 200L;

    public record ClientRay(int casterId, Vec3 prevTo, Vec3 to, boolean gamma, long time) {

        public Vec3 lerpTo(float partialTick) {
            return this.prevTo.add(this.to.subtract(this.prevTo).scale(partialTick));
        }
    }

    private static final Map<Integer, ClientRay> CLIENT_RAYS = new HashMap<>();

    public static void acceptClientRay(int casterId, Vec3 to, boolean gamma) {
        synchronized (CLIENT_RAYS) {
            ClientRay prev = CLIENT_RAYS.get(casterId);
            CLIENT_RAYS.put(casterId, new ClientRay(casterId,
                    prev == null ? to : prev.to(), to, gamma, System.currentTimeMillis()));
        }
    }

    public static List<ClientRay> clientRays() {
        long now = System.currentTimeMillis();
        synchronized (CLIENT_RAYS) {
            CLIENT_RAYS.values().removeIf(ray -> now - ray.time() > CLIENT_RAY_TIMEOUT);
            return new ArrayList<>(CLIENT_RAYS.values());
        }
    }

    public static void clear() {
        synchronized (CLIENT_RAYS) {
            CLIENT_RAYS.clear();
        }
    }
}
