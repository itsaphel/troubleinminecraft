package io.indices.troubleinminecraft.util;

public class InventoryUtil {

    /**
     * Returns an exact Minecraft inventory size (multiple of 9) from the size of the contents
     *
     * @param unroundedSize size of contents
     * @return Minecraft inventory size
     */
    public static int getExactInventorySize(int unroundedSize) {
        return Math.min(9, (int) (Math.ceil((double) unroundedSize / 9)) * 9);
    }
}
