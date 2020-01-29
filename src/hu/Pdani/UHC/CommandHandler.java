package hu.Pdani.UHC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 0){
            if(sender.hasPermission("uhc.admin")){
                sender.sendMessage("/uhc reload - Reload config file");
                sender.sendMessage("/uhc start - Force start the game");
                sender.sendMessage("/uhc seed [new] - Generates or sets a new seed for the Randomizer");
            }
        } else if(args[0].equalsIgnoreCase("reload")){
            if(sender.hasPermission("uhc.admin")){
                Main.getPlugin().reloadConfig();
                Main.setupConfig();
                Main.MIN_PLAYERS = Main.getPlugin().getConfig().getInt("MIN_PLAYERS");
                Main.BORDER_SPEED = Main.getPlugin().getConfig().getInt("BORDER_SPEED");
                Main.RANDOMIZE_ITEMS = Main.getPlugin().getConfig().getBoolean("RANDOMIZE_ITEMS");
                Main.SERVER_RESTART = Main.getPlugin().getConfig().getBoolean("SERVER_RESTART");
                Randomizer.RANDOMIZE_DURABILITY = Main.getPlugin().getConfig().getBoolean("RANDOMIZE_DURABILITY",false);
                Randomizer.RANDOMIZE_DURABILITY_OF_CRAFTED_ITEMS = Main.getPlugin().getConfig().getBoolean("RANDOMIZE_DURABILITY_OF_CRAFTED_ITEMS",false);
                Randomizer.RANDOMIZE_CRAFT = Main.getPlugin().getConfig().getBoolean("RANDOMIZE_CRAFT",false);

                if(!Main.gameStarted())
                    Main.setStart(Main.getPlugin().getConfig().getInt("START"));

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
                if (Main.getPlugin().getServer().getOnlinePlayers().size() < 2) {
                    sender.sendMessage("Not enough players!");
                    return true;
                }
                Main.FORCE_START = true;
                Main.setStart(10);
                sender.sendMessage("Game started!");
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
