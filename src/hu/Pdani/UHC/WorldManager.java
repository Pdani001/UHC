package hu.Pdani.UHC;

import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class WorldManager {
    private static JavaPlugin plugin;

    public static boolean createWorld(){
        if(plugin == null) plugin = Main.getPlugin();
        World world = plugin.getServer().getWorld("UltraHardcore");
        if(world != null){
            if(plugin.getServer().unloadWorld("UltraHardcore",false)){
                if(!delete(world.getWorldFolder())) return false;
            } else {
                return false;
            }
        }
        WorldCreator c = new WorldCreator("UltraHardcore");
        c.type(WorldType.NORMAL);
        c.generateStructures(true);
        World uhc = c.createWorld();
        if(uhc == null)return false;
        uhc.setGameRule(GameRule.NATURAL_REGENERATION,false);
        uhc.setGameRule(GameRule.DISABLE_RAIDS,true);
        uhc.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS,false);
        uhc.setGameRule(GameRule.SPAWN_RADIUS,Main.BORDER_SIZE/4);
        uhc.setDifficulty(Difficulty.EASY);

        return true;
    }

    public static boolean deleteWorld(){
        if(plugin == null) plugin = Main.getPlugin();
        World world = plugin.getServer().getWorld("UltraHardcore");
        plugin.getServer().unloadWorld("UltraHardcore",false);
        if(world == null){
            return delete(new File(Bukkit.getWorldContainer(),"UltraHardcore"));
        }
        return delete(world.getWorldFolder());
    }

    public static World getWorld() throws NullPointerException {
        World world = Main.getPlugin().getServer().getWorld("UltraHardcore");
        if(world == null) throw new NullPointerException("World is null!");
        return world;
    }

    private static boolean delete(File path) {
        if(path.exists()) {
            File files[] = path.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    delete(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return(path.delete());
    }
}
