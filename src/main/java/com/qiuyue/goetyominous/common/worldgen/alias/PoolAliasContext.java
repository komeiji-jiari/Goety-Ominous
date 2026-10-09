package com.qiuyue.goetyominous.common.worldgen.alias;

import java.util.function.Supplier;

public final class PoolAliasContext {

    private static final ThreadLocal<PoolAliasLookup> CURRENT = new ThreadLocal<>();

    private PoolAliasContext() {
    }

    public static PoolAliasLookup current() {
        PoolAliasLookup lookup = CURRENT.get();
        return lookup == null ? PoolAliasLookup.EMPTY : lookup;
    }

    public static <R> R with(PoolAliasLookup lookup, Supplier<R> action) {
        PoolAliasLookup previous = CURRENT.get();
        CURRENT.set(lookup);
        try {
            return action.get();
        } finally {
            if (previous == null) {
                CURRENT.remove();
            } else {
                CURRENT.set(previous);
            }
        }
    }
}
