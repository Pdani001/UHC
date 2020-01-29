package hu.Pdani.UHC;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemEvent implements Listener {
    @EventHandler
    public void itemDrop(PlayerDropItemEvent event){
        Item drop = event.getItemDrop();
        ItemStack i = drop.getItemStack();
        ItemMeta meta = i.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        i.setItemMeta(meta);
        drop.setItemStack(i);
    }

    @EventHandler
    public void itemClickDrop(InventoryClickEvent event){
        if(event.getClickedInventory() == null) {
            ItemStack i = event.getCurrentItem();
            if(i == null) return;
            ItemMeta meta = i.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            i.setItemMeta(meta);
            event.setCurrentItem(i);
        }
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        if(Randomizer.isEnabled()) {
            Item item = event.getEntity();
            ItemStack itemstack = item.getItemStack();
            ItemMeta meta = itemstack.getItemMeta();
            if(meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)){
                meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
                itemstack.setItemMeta(meta);
                item.setItemStack(itemstack);
            } else {
                item.setItemStack(Randomizer.randomizeItemStack(itemstack, Randomizer.RANDOMIZE_DURABILITY));
            }
        }
    }
    @EventHandler
    public void onItemCraft(PrepareItemCraftEvent event) {
        if(Randomizer.isEnabled()) {
            CraftingInventory result = event.getInventory();
            ItemStack itemstack_result = result.getResult();
            if (itemstack_result == null) return;

            if(itemstack_result.getType() == Material.ENCHANTED_GOLDEN_APPLE){
                return;
            }

            ItemMeta itemmeta_result;
            if (itemstack_result.hasItemMeta() == false) {
                itemmeta_result = Main.getPlugin().getServer().getItemFactory().getItemMeta(itemstack_result.getType());
            } else {
                itemmeta_result = itemstack_result.getItemMeta();
            }
            if (Randomizer.RANDOMIZE_CRAFT == true) {
                result.setResult(Randomizer.randomizeItemStack(itemstack_result, Randomizer.RANDOMIZE_DURABILITY_OF_CRAFTED_ITEMS));
            }
            if (Randomizer.RANDOMIZE_DURABILITY_OF_CRAFTED_ITEMS == true && Randomizer.RANDOMIZE_CRAFT == false) {
                itemstack_result.setItemMeta(Randomizer.randomizeDurability(true, itemstack_result, itemmeta_result));
            }
        }
    }
}
