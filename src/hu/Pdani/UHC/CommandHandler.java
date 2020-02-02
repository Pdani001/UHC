package hu.Pdani.UHC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {

    public String c(String m){
        return ChatColor.translateAlternateColorCodes('&',m);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 0){
            sender.sendMessage(c(Main.getPlugin().getConfig().getString("Messages.Command.Help.Join")));
            sender.sendMessage(c(Main.getPlugin().getConfig().getString("Messages.Command.Help.Leave")));
            if(sender.hasPermission("uhc.admin")){
                sender.sendMessage(c(Main.getPlugin().getConfig().getString("Messages.Command.Help.Reload")));
                sender.sendMessage(c(Main.getPlugin().getConfig().getString("Messages.Command.Help.Start")));
                sender.sendMessage(c(Main.getPlugin().getConfig().getString("Messages.Command.Help.Seed")));
            }
        } else if(args[0].equalsIgnoreCase("leave")){
            if(!(sender instanceof Player))
                return true;
            Player player = (Player) sender;
            if(Main.gameCheck(player)) {
                sender.sendMessage(c(Main.getPlugin().getConfig().getString("Messages.Game.Leave.Player")));
                Main.leave(player);
            } else {
                sender.sendMessage(c(Main.getPlugin().getConfig().getString("Messages.Command.LeaveError")));
            }
        } else if(args[0].equalsIgnoreCase("join")){
            if(!(sender instanceof Player))
                return true;
            Player player = (Player) sender;
            if(!Main.gameCheck(player)) {
                sender.sendMessage(c(Main.getPlugin().getConfig().getString("Messages.Game.Join.Game")));
                Main.gameJoin(player);
            } else {
                sender.sendMessage(c(Main.getPlugin().getConfig().getString("Messages.Command.JoinError")));
            }
        } else if(args[0].equalsIgnoreCase("reload")){
            if(sender.hasPermission("uhc.admin")){
                Main.getPlugin().reloadConfig();
                Main.setupConfig();
                Main.MIN_PLAYERS = Main.getPlugin().getConfig().getInt("MIN_PLAYERS");
                Main.BORDER_SPEED = Main.getPlugin().getConfig().getInt("BORDER_SPEED");
                Main.setBorderSpeed(Main.BORDER_SPEED);
                Main.RANDOMIZE_ITEMS = Main.getPlugin().getConfig().getBoolean("RANDOMIZE_ITEMS");
                Main.SERVER_RESTART = Main.getPlugin().getConfig().getBoolean("SERVER_RESTART");
                Randomizer.RANDOMIZE_DURABILITY = Main.getPlugin().getConfig().getBoolean("RANDOMIZE_DURABILITY",false);
                Randomizer.RANDOMIZE_DURABILITY_OF_CRAFTED_ITEMS = Main.getPlugin().getConfig().getBoolean("RANDOMIZE_DURABILITY_OF_CRAFTED_ITEMS",false);
                Randomizer.RANDOMIZE_CRAFT = Main.getPlugin().getConfig().getBoolean("RANDOMIZE_CRAFT",false);

                if(!Main.gameStarted())
                    Main.setGameStart(Main.getPlugin().getConfig().getInt("START"));

                if(!Main.RANDOMIZE_ITEMS && Randomizer.isEnabled()){
                    Randomizer.disable();
                } else if(Main.RANDOMIZE_ITEMS && !Randomizer.isEnabled()){
                    Randomizer.init();
                }

                sender.sendMessage("Reloaded!");
            }
        } else if(args[0].equalsIgnoreCase("seed")){
            if(sender.hasPermission("uhc.admin")){
                if(args.length > 1 && args[1] != null){
                    if(!isInt(args[1])){
                        sender.sendMessage("Value must be number!");
                        return true;
                    }
                    Randomizer.newSeed(Integer.parseInt(args[1]));
                } else {
                    Randomizer.newSeed();
                }
                sender.sendMessage("Seed updated!");
            }
        } else if(args[0].equalsIgnoreCase("start")){
            if(sender.hasPermission("uhc.admin")) {
                if (Main.join.size() < 2) {
                    sender.sendMessage("Not enough players!");
                    return true;
                }
                if(!Main.gameStarted()) {
                    Main.FORCE_START = true;
                    Main.setGameStart(10);
                    sender.sendMessage("Game started!");
                } else {
                    if(!Main.isPvP()) {
                        Main.setPvP(10);
                        sender.sendMessage("PVP started!");
                    } else {
                        Main.setBorderStart(10);
                        sender.sendMessage("Border started!");
                    }
                }
            }
        }
        return true;
    }

    public boolean isInt(String s)
    {
        try
        {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex)
        {
            return false;
        }
    }
}
