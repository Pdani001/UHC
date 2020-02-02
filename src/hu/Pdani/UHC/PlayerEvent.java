package hu.Pdani.UHC;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class PlayerEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(Main.SCOREBOARD) player.setScoreboard(Main.getBoard());
        if(Main.BOSSBAR_MESSAGES) Main.bar.addPlayer(event.getPlayer());
        if(!Main.gameStarted()){
            player.setGameMode(GameMode.ADVENTURE);
            double y = event.getPlayer().getWorld().getHighestBlockYAt(event.getPlayer().getLocation());
            player.teleport(new Location(event.getPlayer().getWorld(),event.getPlayer().getLocation().getX(),y,event.getPlayer().getLocation().getZ()));
            player.getInventory().clear();
            player.setHealth(20);
            player.sendMessage(c(Main.getPlugin().getConfig().getString("Messages.Game.Join.Server").replace("{0}", event.getPlayer().getName())));
        } else {
            World w = WorldManager.getWorld();
            boolean isSafe = false;
            Location loc = null;
            while(!isSafe) {
                loc = Main.getRandomLocation(w);
                try {
                    loc = LocationUtil.getSafeDestination(loc);
                    isSafe = true;
                } catch (Exception ignored) {}
            }
            player.getInventory().clear();
            player.setHealth(20);
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(loc);
            player.sendMessage(c(Main.getPlugin().getConfig().getString("Messages.Game.Join.Late")));
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Main.leave(event.getPlayer());
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event){
        if((!Main.gameStarted() || Main.isEnded()) && !event.getPlayer().isOp()) event.setCancelled(true);
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event){
        if((!Main.gameStarted() || Main.isEnded()) && !event.getPlayer().isOp()) event.setCancelled(true);
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event){
        event.getProjectile().setMetadata("shooter",new FixedMetadataValue(Main.getPlugin(),event.getEntity()));
    }

    @EventHandler
    public void onHungerDeplete(FoodLevelChangeEvent e) {
        if(!Main.gameStarted()) {
            e.setCancelled(true);
            ((Player) e.getEntity()).setFoodLevel(20);
        }
    }

    @EventHandler
    public void PlayerDeath(EntityDamageEvent ev){
        if(!(ev.getEntity() instanceof LivingEntity))
            return;
        LivingEntity entity = (LivingEntity)ev.getEntity();
        if(!Main.gameStarted()){
            ev.setCancelled(true);
            return;
        }
        if (((entity.getHealth() - ev.getFinalDamage()) <= 0) //Checks if the entity will die and if entity is player
                && entity instanceof Player)
        {
            if(Main.isEnded()){
                ev.setCancelled(true);
                return;
            }
            Player player = (Player) entity;
            PlayerInventory pi = player.getInventory();
            if(pi.getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING || pi.getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING){
                return;
            }
            if(Main.gameStarted()) ev.setCancelled(true);
            DamageCause c = ev.getCause();
            if(c == DamageCause.ENTITY_ATTACK){
                EntityDamageByEntityEvent edbev = (EntityDamageByEntityEvent) ev;
                Entity damager = edbev.getDamager();
                if(damager instanceof Player){
                    Player killer = (Player) damager;
                    //Main.announce("&6"+killer.getName()+" killed &f"+player.getName());
                    Main.announce(Main.getPlugin().getConfig().getString("Messages.Death.Player").replace("{0}",killer.getName()).replace("{1}",player.getName()));
                    if(Main.SCOREBOARD) Main.addScore(killer,1);
                } else {
                    //Main.announce("&c"+player.getName()+" died by &f"+damager.getName());
                    Main.announce(Main.getPlugin().getConfig().getString("Messages.Death.Mob").replace("{0}",player.getName()).replace("{1}",damager.getName()));
                }
            } else if(c == DamageCause.CONTACT){
                Main.announce(Main.getPlugin().getConfig().getString("Messages.Death.Cactus").replace("{0}",player.getName()));
            } else if(c == DamageCause.BLOCK_EXPLOSION){
                Main.announce(Main.getPlugin().getConfig().getString("Messages.Death.TNT").replace("{0}",player.getName()));
            } else if(c == DamageCause.DROWNING){
                Main.announce(Main.getPlugin().getConfig().getString("Messages.Death.Drown").replace("{0}",player.getName()));
            } else if(c == DamageCause.FALL){
                Main.announce(Main.getPlugin().getConfig().getString("Messages.Death.Fall").replace("{0}",player.getName()));
            } else if(c == DamageCause.FIRE){
                Main.announce(Main.getPlugin().getConfig().getString("Messages.Death.Fire").replace("{0}",player.getName()));
            } else if(c == DamageCause.SUFFOCATION){
                Main.announce(Main.getPlugin().getConfig().getString("Messages.Death.Suffocation").replace("{0}",player.getName()));
            } else if(c == DamageCause.STARVATION){
                Main.announce(Main.getPlugin().getConfig().getString("Messages.Death.Starve").replace("{0}",player.getName()));
            } else if(c == DamageCause.SUICIDE){
                Main.announce(Main.getPlugin().getConfig().getString("Messages.Death.Suicide").replace("{0}",player.getName()));
            } else if(c == DamageCause.MAGIC){
                Main.announce(Main.getPlugin().getConfig().getString("Messages.Death.Potion").replace("{0}",player.getName()));
            } else if(c == DamageCause.PROJECTILE){
                EntityDamageByEntityEvent edbev = (EntityDamageByEntityEvent) ev;
                Entity damager = edbev.getDamager();
                Entity shooter = (Entity) damager.getMetadata("shooter").get(0).value();
                if(shooter instanceof Player){
                    Player killer = (Player) shooter;
                    Main.announce(Main.getPlugin().getConfig().getString("Messages.Death.Player").replace("{0}",killer.getName()).replace("{1}",player.getName()));
                    if(Main.SCOREBOARD)  Main.addScore(killer,1);
                } else {
                    Main.announce(Main.getPlugin().getConfig().getString("Messages.Death.Arrow").replace("{0}",player.getName()).replace("{1}",shooter.getName()));
                }
            } else if(c == DamageCause.THORNS){
                EntityDamageByEntityEvent edbev = (EntityDamageByEntityEvent) ev;
                Entity damager = edbev.getDamager();
                if(damager instanceof Player){
                    Player killer = (Player) damager;
                    Main.announce(Main.getPlugin().getConfig().getString("Messages.Death.Thorns.Player").replace("{0}",killer.getName()).replace("{1}",player.getName()));
                    if(Main.SCOREBOARD) Main.addScore(killer,1);
                } else {
                    Main.announce(Main.getPlugin().getConfig().getString("Messages.Death.Thorns.Mob").replace("{0}",player.getName()).replace("{1}",damager.getName()));
                }
            } else {
                Main.announce(Main.getPlugin().getConfig().getString("Messages.Death.Unknown").replace("{0}",player.getName()));
            }
            PlayerInventory inv = player.getInventory();
            for(ItemStack i : inv.getContents()){
                if(i == null)
                    continue;
                ItemMeta meta = i.getItemMeta();
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                i.setItemMeta(meta);
                player.getWorld().dropItemNaturally(player.getLocation(), i);
            }
            for(ItemStack i : inv.getArmorContents()){
                if(i == null)
                    continue;
                ItemMeta meta = i.getItemMeta();
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                i.setItemMeta(meta);
                player.getWorld().dropItemNaturally(player.getLocation(), i);
            }
            inv.clear();
            if(Main.gameStarted()) {
                player.setHealth(20);
                player.setGameMode(GameMode.SPECTATOR);
                Main.playerDeath(player);
            }
        } else {
            if(!Main.isPvP() || Main.isEnded()){
                DamageCause c = ev.getCause();
                if(c == DamageCause.ENTITY_ATTACK){
                    EntityDamageByEntityEvent edbev = (EntityDamageByEntityEvent) ev;
                    Entity damager = edbev.getDamager();
                    if((damager instanceof Player) && (entity instanceof Player)){
                        ev.setCancelled(true);
                    }
                    if((damager instanceof Firework) && (entity instanceof Player)){
                        ev.setCancelled(true);
                    }
                }
            }
        }
    }
    private String c(String m){
        return ChatColor.translateAlternateColorCodes('&',m);
    }
}
