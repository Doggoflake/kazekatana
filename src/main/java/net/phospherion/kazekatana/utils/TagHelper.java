package net.phospherion.kazekatana.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Reflection-based helper to get/set/remove the CompoundTag on ItemStack across mappings.
 * It caches discovered methods for performance.
 */
public final class TagHelper {
    private static Method GET_TAG_METHOD = null;
    private static Method SET_TAG_METHOD = null;
    private static Method HAS_TAG_METHOD = null;

    private TagHelper() {}

    private static void discoverMethods() {
        if (GET_TAG_METHOD != null) return; // already discovered

        Class<?> cls = ItemStack.class;
        Method foundGet = null;
        Method foundSet = null;
        Method foundHas = null;

        // Find a method that returns CompoundTag and takes no args (getTag / getOrCreateTag)
        for (Method m : cls.getMethods()) {
            if (m.getReturnType() == CompoundTag.class && m.getParameterCount() == 0) {
                String name = m.getName().toLowerCase();
                if (name.contains("get") && name.contains("tag")) {
                    foundGet = m;
                    break;
                }
            }
        }

        // If not found, try methods that return CompoundTag but may take a boolean (some mappings)
        if (foundGet == null) {
            for (Method m : cls.getMethods()) {
                if (m.getReturnType() == CompoundTag.class && m.getParameterCount() == 1 &&
                        (m.getParameterTypes()[0] == boolean.class || m.getParameterTypes()[0] == Boolean.class)) {
                    String name = m.getName().toLowerCase();
                    if (name.contains("get") && name.contains("tag")) {
                        foundGet = m;
                        break;
                    }
                }
            }
        }

        // Find a setter: takes CompoundTag and returns void or ItemStack
        for (Method m : cls.getMethods()) {
            if (m.getParameterCount() == 1 && m.getParameterTypes()[0] == CompoundTag.class) {
                String name = m.getName().toLowerCase();
                if (name.contains("set") && name.contains("tag")) {
                    foundSet = m;
                    break;
                }
            }
        }

        // Find a hasTag-like method returning boolean
        for (Method m : cls.getMethods()) {
            if (m.getReturnType() == boolean.class && m.getParameterCount() == 0) {
                String name = m.getName().toLowerCase();
                if (name.contains("has") && name.contains("tag")) {
                    foundHas = m;
                    break;
                }
            }
        }

        GET_TAG_METHOD = foundGet;
        SET_TAG_METHOD = foundSet;
        HAS_TAG_METHOD = foundHas;

        // make accessible
        try {
            if (GET_TAG_METHOD != null) GET_TAG_METHOD.setAccessible(true);
            if (SET_TAG_METHOD != null) SET_TAG_METHOD.setAccessible(true);
            if (HAS_TAG_METHOD != null) HAS_TAG_METHOD.setAccessible(true);
        } catch (Exception ignored) {}
    }

    /**
     * Returns the CompoundTag for the stack, or null if none.
     */
    public static CompoundTag getTag(ItemStack stack) {
        discoverMethods();
        if (GET_TAG_METHOD != null) {
            try {
                Object res = GET_TAG_METHOD.invoke(stack);
                return (CompoundTag) res;
            } catch (Exception ignored) {}
        }

        // Fallback: try to access private field 'tag' via reflection (last resort)
        try {
            var f = ItemStack.class.getDeclaredField("tag");
            f.setAccessible(true);
            Object val = f.get(stack);
            return (CompoundTag) val;
        } catch (Exception ignored) {}

        return null;
    }

    /**
     * Sets the CompoundTag on the ItemStack. If tag is null, attempts to remove it.
     */
    public static void setTag(ItemStack stack, CompoundTag tag) {
        discoverMethods();
        if (SET_TAG_METHOD != null) {
            try {
                SET_TAG_METHOD.invoke(stack, tag);
                return;
            } catch (Exception ignored) {}
        }

        // Fallback: try to set private field 'tag'
        try {
            var f = ItemStack.class.getDeclaredField("tag");
            f.setAccessible(true);
            f.set(stack, tag);
            return;
        } catch (Exception ignored) {}
    }

    /**
     * Returns true if the stack has a tag (best-effort).
     */
    public static boolean hasTag(ItemStack stack) {
        discoverMethods();
        if (HAS_TAG_METHOD != null) {
            try {
                Object res = HAS_TAG_METHOD.invoke(stack);
                return res instanceof Boolean && (Boolean) res;
            } catch (Exception ignored) {}
        }

        // Fallback: check getTag() != null
        return getTag(stack) != null;
    }

    /**
     * Convenience: get or create a CompoundTag (if none exists, create and attach).
     */
    public static CompoundTag getOrCreateTag(ItemStack stack) {
        CompoundTag tag = getTag(stack);
        if (tag == null) {
            tag = new CompoundTag();
            setTag(stack, tag);
        }
        return tag;
    }
}
