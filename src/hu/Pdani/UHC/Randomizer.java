package hu.Pdani.UHC;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

public class Randomizer {
    public static boolean RANDOMIZE_DURABILITY;
    public static boolean RANDOMIZE_DURABILITY_OF_CRAFTED_ITEMS;
    public static int SEED;
    public static boolean RANDOMIZE_CRAFT;
    private static boolean INIT = false;

    public static boolean isEnabled(){
        return INIT;
    }

    public static void disable(){
        INIT = false;
    }

    public static void newSeed(){
        SEED = (int)System.currentTimeMillis();
        SEED = (int)(SEED * Math.random());
    }

    public static void newSeed(int seed){
        SEED = seed;
    }

    public static void init(){
        if(INIT)
            return;

        RANDOMIZE_DURABILITY = Main.getPlugin().getConfig().getBoolean("RANDOMIZE_DURABILITY",false);
        RANDOMIZE_DURABILITY_OF_CRAFTED_ITEMS = Main.getPlugin().getConfig().getBoolean("RANDOMIZE_DURABILITY_OF_CRAFTED_ITEMS",false);
        RANDOMIZE_CRAFT = Main.getPlugin().getConfig().getBoolean("RANDOMIZE_CRAFT",false);
        newSeed();

        INIT = true;
    }

    public static Material getRandomItemFromItemWithSeed(Material material, int fallback) {
        if(!INIT)
            return null;
        int count = 0;
        int block_nbr = 0;
        ArrayList<Material> names = new ArrayList<>();
        for (Material material_loop : Material.values()) {
            if(isAir(material_loop)){
                continue;
            }
            if(material_loop == material) {
                block_nbr = count;
            }
            names.add(material_loop);
            count++;
        }
        int random_int = pseudoRandom(SEED, count, block_nbr, fallback);

        return names.get(random_int);
    }
    public static ItemStack randomizeItemStack(ItemStack itemstack, boolean randomize_durability) {
        if(!INIT)
            return null;
        Material material = itemstack.getType();
        int itemstack_amount = itemstack.getAmount();

        int loop = 0;
        Material item_to_drop;
        while (true) {
            item_to_drop = getRandomItemFromItemWithSeed(material, loop);
            try {
                itemstack.setType(item_to_drop);

                ItemMeta itemmeta_drop;
                if(!itemstack.hasItemMeta()) {
                    itemmeta_drop = Main.getPlugin().getServer().getItemFactory().getItemMeta(itemstack.getType());
                }
                else {
                    itemmeta_drop = itemstack.getItemMeta();
                }


                itemmeta_drop = randomizeDurability(randomize_durability, itemstack, itemmeta_drop);

                itemstack.setAmount(itemstack_amount);
                itemstack.setItemMeta(itemmeta_drop);

                return itemstack;
            }
            catch (java.lang.IllegalArgumentException | java.lang.NullPointerException ex) {
                loop ++;
            }
        }
    }

    public static ItemMeta randomizeDurability(boolean randomize_durability, ItemStack itemstack, ItemMeta itemmeta) {
        if(!INIT)
            return null;
        int item_max_durability = itemstack.getType().getMaxDurability();

        if(item_max_durability != 0) {
            try {
                // 1.13+
                if(itemmeta instanceof Damageable) {
                    if(!randomize_durability) {
                        ((Damageable) itemmeta).setDamage(0);
                    }
                    else {
                        int random = (int) (Math.random() * ((item_max_durability) + 1));
                        ((Damageable) itemmeta).setDamage(random);
                    }
                }
            }
            catch (java.lang.NoClassDefFoundError e) {
                Main.getPlugin().getServer().getLogger().log(Level.SEVERE, "Item durability randomization is not supported on this version!");
            }
        }
        return itemmeta;
    }
    public static int pseudoRandom(int seed, int i, int add, int fallback) {
        Random randnum = new Random();
        randnum.setSeed(seed*add+fallback);
        return randnum.nextInt(i);
    }
    public static boolean isAir(Material m){
        return (m == Material.AIR
                || m.name().contains("POTTED")
                || m.name().endsWith("_AIR")
                || m.name().endsWith("_STEM")
                || m.name().endsWith("WALL_BANNER")
                || m.name().endsWith("WALL_FAN")
                || m.name().endsWith("WALL_HEAD")
                || m.name().endsWith("WALL_SIGN")
                || m.name().endsWith("WALL_TORCH")
                || m.name().endsWith("_GLASS")
                || m.name().endsWith("_GLASS_PANE")
                || m.name().startsWith("LEGACY_")
                || m == Material.BEETROOTS
                || m == Material.BAMBOO_SAPLING
                || m == Material.COCOA
                || m == Material.CARROTS
                || m == Material.END_GATEWAY
                || m == Material.END_PORTAL
                || m == Material.FIRE
                || m == Material.FROSTED_ICE
                || m == Material.KELP_PLANT
                || m == Material.LAVA
                || m == Material.MOVING_PISTON
                || m == Material.NETHER_PORTAL
                || m == Material.PISTON_HEAD
                || m == Material.POTATOES
                || m == Material.REDSTONE_WIRE
                || m == Material.SWEET_BERRY_BUSH
                || m == Material.TALL_SEAGRASS
                || m == Material.TRIPWIRE
                || m == Material.WATER
        );
    }
}
