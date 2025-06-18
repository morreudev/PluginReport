package com.morreudev.discord.reports.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

public class HeadUtil {
    
    public static ItemStack getCustomHead(String texture) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        if (texture == null || texture.isEmpty()) return head;

        SkullMeta meta = (SkullMeta) head.getItemMeta();
        
        try {

            Class<?> gameProfileClass = Class.forName("com.mojang.authlib.GameProfile");
            Class<?> propertyClass = Class.forName("com.mojang.authlib.properties.Property");
            

            Constructor<?> profileConstructor = gameProfileClass.getConstructor(UUID.class, String.class);
            Object profile = profileConstructor.newInstance(UUID.randomUUID(), null);
            

            Method getProperties = gameProfileClass.getMethod("getProperties");
            Object properties = getProperties.invoke(profile);
            
            Constructor<?> propertyConstructor = propertyClass.getConstructor(String.class, String.class);
            Object property = propertyConstructor.newInstance("textures", texture);
            
            Method put = properties.getClass().getMethod("put", Object.class, Object.class);
            put.invoke(properties, "textures", property);
            

            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        head.setItemMeta(meta);
        return head;
    }
} 