package org.alexanderr193.barrelTrade.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class Serialization {
    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();
    private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();

    private Serialization() {}

    public static String itemStackToBase64(ItemStack itemStack) throws IOException {
        if (itemStack == null) return null;


        try (ByteArrayOutputStream io = new ByteArrayOutputStream();
             BukkitObjectOutputStream os = new BukkitObjectOutputStream(io)) {
            os.writeObject(itemStack);
            os.flush();

            byte[] serializedObject = io.toByteArray();
            return new String(BASE64_ENCODER.encode(serializedObject));
        }
        catch (Exception e) {
            Bukkit.getLogger().warning("Serialization ItemStack error: " + e.getMessage());
            return null;
        }
    }

    public static ItemStack itemStackFromBase64(String base64String) {
        if (base64String == null || base64String.isEmpty()) return new ItemStack(Material.AIR);

        try (ByteArrayInputStream in = new ByteArrayInputStream(BASE64_DECODER.decode(base64String));
             BukkitObjectInputStream is = new BukkitObjectInputStream(in)) {
            return (ItemStack)is.readObject();
        } catch (Exception e) {
            Bukkit.getLogger().warning("Deserialization ItemStack error: " + e.getMessage());
            return new ItemStack(Material.AIR);
        }

    }
}