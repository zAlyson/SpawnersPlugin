package com.alysonsantos.aspect.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.UUID;

/**
 * @author Sasuked
 * https://github.com/SasukeMCHC
 * <p>
 * Simple class to create custom heads.F
 * <p>
 * Based on https://github.com/deanveloper/SkullCreator/
 */
public class HeadGenerator {

   private static HeadGenerator instance;

   public static HeadGenerator getInstance() {

      if (instance == null) {
         instance = new HeadGenerator();
      }

      return instance;
   }
   
   public ItemStack fromEntity(Entity entity) {
      return fromEntityType(entity.getType());
   }
   
   
   public ItemStack fromEntityType(EntityType entityType) {
      return fromName("MHF_" + entityType.getName());
   }

   public ItemStack fromPlayer(OfflinePlayer owner) {
      return fromName(owner.getName());
   }

   public ItemStack fromName(String name) {
      ItemStack item = getPlayerHead();
      return fromName(item, name);
   }


   public ItemStack fromName(ItemStack item, String name) {
      notNull(item, "item");
      notNull(name, "name");

      return Bukkit.getUnsafe().modifyItemStack(item, "{SkullOwner:\"" + name + "\"}");
   }

   public ItemStack fromUUID(UUID id) {
      ItemStack item = getPlayerHead();

      return fromUUID(item, id);
   }


   public ItemStack fromUUID(ItemStack item, UUID id) {
      notNull(item, "item");
      notNull(id, "id");

      SkullMeta meta = (SkullMeta) item.getItemMeta();
      meta.setOwner(Bukkit.getOfflinePlayer(id).getName());
      item.setItemMeta(meta);

      return item;
   }


   public ItemStack fromURL(String url) {
      ItemStack item = getPlayerHead();

      return fromURL(item, url);
   }


   private ItemStack fromURL(ItemStack item, String url) {
      notNull(item, "item");
      notNull(url, "url");

      return itemWithBase64(item, urlToBase64(url));
   }

   public ItemStack itemFromBase64(String base64) {
      ItemStack item = getPlayerHead();
      return itemWithBase64(item, base64);
   }


   private ItemStack itemWithBase64(ItemStack item, String base64) {
      notNull(item, "item");
      notNull(base64, "base64");

      UUID hashAsId = new UUID(base64.hashCode(), base64.hashCode());
      return Bukkit.getUnsafe().modifyItemStack(item,
              "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + base64 + "\"}]}}}"
      );
   }


   public void setHeadWithName(Block block, String name) {
      notNull(block, "block");
      notNull(name, "name");

      if (!(block instanceof Skull)) return;

      applyHead(block);

      Skull skull = (Skull) block;
      skull.setOwner(name);
   }

   public void setBlockUUID(Block block, UUID id) {
      notNull(block, "block");
      notNull(id, "id");

      if (!(block instanceof Skull)) return;

      applyHead(block);

      Skull skull = (Skull) block;
      skull.setOwner(Bukkit.getOfflinePlayer(id).getName());


   }

   public void setBlockURL(Block block, String url) {
      notNull(block, "block");
      notNull(url, "url");

      setBlockBase64(block, urlToBase64(url));
   }


   private void setBlockBase64(Block block, String base64) {
      notNull(block, "block");
      notNull(base64, "base64");

      UUID uuid = new UUID(base64.hashCode(), base64.hashCode());

      String format = String.format("%d %d %d %s",
              block.getX(),
              block.getY(),
              block.getZ(),
              "{Owner:{Id:\"" + uuid + "\",Properties:{textures:[{Value:\"" + base64 + "\"}]}}}"
      );

      if (isUpdatedVersion()) {
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "data merge block " + format);
      } else {
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "blockdata " + format);
      }
   }

   private boolean isUpdatedVersion() {
      try {

         Material.valueOf("PLAYER_HEAD");
         return true;

      } catch (IllegalArgumentException e) {
         return false;
      }
   }

   private ItemStack getPlayerHead() {
      if (isUpdatedVersion()) {
         return new ItemStack(Material.valueOf("PLAYER_HEAD"));
      } else {
         return new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (byte) 3);
      }
   }

   private void applyHead(Block block) {
      try {
         block.setType(Material.valueOf("PLAYER_HEAD"), false);
      } catch (IllegalArgumentException e) {
         block.setType(Material.valueOf("SKULL"), false);
      }
   }

   private void notNull(Object o, String name) {
      if (o == null) {
         throw new NullPointerException(name + " cannot be null");
      }
   }

   private String urlToBase64(String url) {

      URI actualUrl;
      try {
         actualUrl = new URI(url);
      } catch (URISyntaxException e) {
         throw new RuntimeException(e);
      }
      String toEncode = "{\"textures\":{\"SKIN\":{\"url\":\"" + actualUrl.toString() + "\"}}}";
      return Base64.getEncoder().encodeToString(toEncode.getBytes());
   }
}