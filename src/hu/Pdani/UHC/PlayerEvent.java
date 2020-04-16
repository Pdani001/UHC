package hu.Pdani.UHC;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerEvent implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        if(!Main.gameCheck(event.getPlayer())){
            return;
        }
        Main.leave(event.getPlayer());
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event){
        if(!Main.gameCheck(event.getPlayer())){
            return;
        }
        if((!Main.gameStarted() || Main.isEnded()) && !event.getPlayer().isOp()) event.setCancelled(true);
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event){
        if(!Main.gameCheck(event.getPlayer())){
            return;
        }
        if((!Main.gameStarted() || Main.isEnded()) && !event.getPlayer().isOp()) event.setCancelled(true);
    }

    @EventHandler
    public void onHungerDeplete(FoodLevelChangeEvent e) {
        if(!Main.gameCheck((Player)e.getEntity())){
            return;
        }
        if(!Main.gameStarted()) {
            e.setCancelled(true);
            ((Player) e.getEntity()).setFoodLevel(20);
        }
    }

    @EventHandler
    public void PlayerRespawn(PlayerRespawnEvent e){
        if(Main.gameCheck(e.getPlayer())){
            e.setRespawnLocation(Main.death.get(e.getPlayer()));
            e.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
    }

    @EventHandler
    public void PlayerDied(PlayerDeathEvent e){
        Player player = e.getEntity();
        if(!Main.gameCheck(player)){
            return;
        }
        e.setDeathMessage(null);
        EntityDamageEvent ede = player.getLastDamageCause();
        DamageCause c = ede.getCause();
        if(ede instanceof EntityDamageByEntityEvent){
            EntityDamageByEntityEvent edbev = (EntityDamageByEntityEvent) ede;
            if(edbev instanceof Arrow){
                Arrow arrow = (Arrow) edbev.getDamager();
                Entity shooter = (Entity)arrow.getShooter();
                if (shooter instanceof Player) {
                    Player killer = (Player) shooter;
                    Main.announce(Main.getPlugin().getConfig().getString("Messages.Death.Player").replace("{0}",killer.getName()).replace("{1}",player.getName()));
                    if(Main.SCOREBOARD)  Main.addScore(killer,1);
                } else {
                    Main.announce(Main.getPlugin().getConfig().getString("Messages.Death.Arrow").replace("{0}",player.getName()).replace("{1}",shooter.getName()));
                }
                return;
            }
        }
        if(c == DamageCause.ENTITY_ATTACK){
            EntityDamageByEntityEvent edbev = (EntityDamageByEntityEvent) ede;
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
        } else if(c == DamageCause.THORNS){
            EntityDamageByEntityEvent edbev = (EntityDamageByEntityEvent) ede;
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
        Main.death.put(player,player.getLocation());
        Main.playerDeath(player);
    }

    @EventHandler
    public void PlayerHit(EntityDamageEvent ev){
        if(!(ev.getEntity() instanceof LivingEntity))
            return;
        LivingEntity entity = (LivingEntity)ev.getEntity();
        if(!(entity instanceof Player)){
            return;
        }
        if(!Main.gameCheck((Player)entity)){
            return;
        }
        if(!Main.gameStarted()){
            ev.setCancelled(true);
            return;
        }
        if(!Main.isPvP() || Main.isEnded()){
            DamageCause c = ev.getCause();
            if(c == DamageCause.ENTITY_ATTACK){
                EntityDamageByEntityEvent edbev = (EntityDamageByEntityEvent) ev;
                Entity damager = edbev.getDamager();
                if(damager instanceof Player){
                    ev.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void ChatEvent(AsyncPlayerChatEvent e){
        if(!Main.gameCheck(e.getPlayer())) {
            for (Player p : Main.join) {
                e.getRecipients().remove(p);
            }
        } else {
            for(Player p : Main.getPlugin().getServer().getOnlinePlayers()){
                if(!Main.gameCheck(p)) e.getRecipients().remove(p);
            }
        }
    }
}
