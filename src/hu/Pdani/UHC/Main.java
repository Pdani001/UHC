package hu.Pdani.UHC;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class Main extends JavaPlugin {
    private static JavaPlugin plugin = null;
    public static boolean RANDOMIZE_ITEMS, SCOREBOARD, BORDER_ALLOW, SERVER_RESTART, FORCE_START, BOSSBAR_MESSAGES;
    private static int PVP;
    public static int BORDER_WAIT,BORDER_START_WAIT,BORDER_SIZE,BORDER_SPEED,MIN_PLAYERS;
    private static Border bc;
    private static PVP pc;
    private static Restart rc;
    private static Scoreboard board;
    private static boolean isStarted = false;
    private static ArrayList<Color> colors = new ArrayList<>();
    public static ArrayList<Player> list = new ArrayList<>();
    public static ArrayList<Player> join = new ArrayList<>();
    private static Player winner = null;
    private static BarColor startcolor,pvpcolor,bordercolor;
    public static BossBar bar;

    public void onLoad(){
        this.getLogger().log(Level.INFO,"Preparing for battle...");
        plugin = this;
        setupConfig();
    }

    public void onEnable(){

        bar = getServer().createBossBar("Test",BarColor.WHITE, BarStyle.SOLID);
        bar.setVisible(false);

        setupColors();
        getCommand("ultrahardcore").setExecutor(new CommandHandler());

        RANDOMIZE_ITEMS = getConfig().getBoolean("RANDOMIZE_ITEMS");
        SCOREBOARD = getConfig().getBoolean("SCOREBOARD");
        BORDER_ALLOW = getConfig().getBoolean("BORDER_ALLOW");
        SERVER_RESTART = getConfig().getBoolean("SERVER_RESTART");
        BOSSBAR_MESSAGES = getConfig().getBoolean("BOSSBAR_MESSAGES");
        PVP = getConfig().getInt("PVP");
        BORDER_WAIT = getConfig().getInt("BORDER_WAIT");
        BORDER_START_WAIT = getConfig().getInt("BORDER_START_WAIT");
        BORDER_SIZE = getConfig().getInt("BORDER_SIZE");
        BORDER_SPEED = getConfig().getInt("BORDER_SPEED");
        MIN_PLAYERS = getConfig().getInt("MIN_PLAYERS");
        FORCE_START = false;

        this.getLogger().log(Level.INFO,"BORDER_SIZE: "+BORDER_SIZE);
        this.getLogger().log(Level.INFO,"BORDER_SPEED: "+BORDER_SPEED);

        if(RANDOMIZE_ITEMS) Randomizer.init();

        getServer().getPluginManager().registerEvents(new PlayerEvent(),this);
        getServer().getPluginManager().registerEvents(new ItemEvent(),this);
        this.getLogger().log(Level.INFO,"Plugin is now enabled!");
        this.getLogger().log(Level.INFO,"UltraHardcore plugin by Pdani001");

        pc = new PVP();
        bc = new Border();
        rc = new Restart();

        start();
        addNotch();

        World n = getServer().getWorld("world");
        n.setDifficulty(Difficulty.PEACEFUL);
    }

    public static void setupConfig() {
        plugin.getConfig().addDefault("RANDOMIZE_DURABILITY", false);
        plugin.getConfig().addDefault("RANDOMIZE_DURABILITY_OF_CRAFTED_ITEMS", false);
        plugin.getConfig().addDefault("RANDOMIZE_CRAFT", false);
        plugin.getConfig().addDefault("RANDOMIZE_ITEMS", false);
        plugin.getConfig().addDefault("SCOREBOARD", true);
        plugin.getConfig().addDefault("PVP", 300);
        plugin.getConfig().addDefault("BORDER_WAIT", 90);
        plugin.getConfig().addDefault("BORDER_START_WAIT", 180);
        plugin.getConfig().addDefault("BORDER_SIZE", 1000);
        plugin.getConfig().addDefault("BORDER_SPEED", 40);
        plugin.getConfig().addDefault("BORDER_ALLOW", true);
        plugin.getConfig().addDefault("MIN_PLAYERS", 2);
        plugin.getConfig().addDefault("START", 30);
        plugin.getConfig().addDefault("SERVER_RESTART", false);
        plugin.getConfig().addDefault("BOSSBAR_MESSAGES", true);
        plugin.getConfig().addDefault("Messages.PVP.Min","&d&lPVP will start in {0} minute(s)");
        plugin.getConfig().addDefault("Messages.PVP.Sec","&d&lPVP will start in {0} second(s)");
        plugin.getConfig().addDefault("Messages.PVP.Start","&4&lPVP starts now!");
        plugin.getConfig().addDefault("Messages.Border.Start.Min","&c&lThe border will start in {0} minute(s)");
        plugin.getConfig().addDefault("Messages.Border.Start.Sec","&c&lThe border will start in {0} second(s)");
        plugin.getConfig().addDefault("Messages.Border.Moving","&6&lThe border is shrinking to {0}!!");
        plugin.getConfig().addDefault("Messages.GameStart","&3&lThe game starts in {0} second(s)");
        plugin.getConfig().addDefault("Messages.Death.Player","&6{0} killed &f{1}");
        plugin.getConfig().addDefault("Messages.Death.Mob","&c{0} died by &f{1}");
        plugin.getConfig().addDefault("Messages.Death.Cactus","&a{0} touched a cactus");
        plugin.getConfig().addDefault("Messages.Death.TNT","&a{0} exploded");
        plugin.getConfig().addDefault("Messages.Death.Drown","&a{0} drowned");
        plugin.getConfig().addDefault("Messages.Death.Fall","&a{0} fell from a high place");
        plugin.getConfig().addDefault("Messages.Death.Fire","&a{0} burnt to death");
        plugin.getConfig().addDefault("Messages.Death.Suffocation","&a{0} suffocated");
        plugin.getConfig().addDefault("Messages.Death.Starve","&a{0} starved to death");
        plugin.getConfig().addDefault("Messages.Death.Suicide","&a{0} commited suicide");
        plugin.getConfig().addDefault("Messages.Death.Potion","&a{0} was posioned");
        plugin.getConfig().addDefault("Messages.Death.Arrow","&a{0} was shot to death by &f{1}");
        plugin.getConfig().addDefault("Messages.Death.Thorns.Player","&a{0} died, while trying to hit &f{1}");
        plugin.getConfig().addDefault("Messages.Death.Thorns.Mob","&a{0} died, while fighting a &f{1}");
        plugin.getConfig().addDefault("Messages.Death.Unkown","&e{0} died");
        plugin.getConfig().addDefault("Messages.GameEnd.Announce","&b&lThe game is over! The winner is &d{0}");
        plugin.getConfig().addDefault("Messages.GameEnd.Restart.Count","&cThe server is restarting in &f{0} second(s)");
        plugin.getConfig().addDefault("Messages.GameEnd.Restart.Now","&4The server is restarting!");
        plugin.getConfig().addDefault("Messages.GameEnd.Restart.InformationForYou","If you want to use the restart feature you need to set things up on your end, as this plugin only shuts down the server!");
        plugin.getConfig().addDefault("Messages.GameEnd.Stop.Count","&cThe server closes in &f{0} second(s)");
        plugin.getConfig().addDefault("Messages.GameEnd.Stop.Now","&4The server is closing!");
        plugin.getConfig().addDefault("Messages.Game.Leave.Announce","&e{0} abandoned the current game!");
        plugin.getConfig().addDefault("Messages.Game.Leave.Player","&4&lGame left.");
        plugin.getConfig().addDefault("Messages.Game.Join.Server","&aWelcome {0}! To be able to play, please enter &7/uhc join&a!");
        plugin.getConfig().addDefault("Messages.Game.Join.Game","&b&lGame joined!");
        plugin.getConfig().addDefault("Messages.Game.Join.Late","&cThe game already started, so you can only spectate!");
        plugin.getConfig().addDefault("Messages.Game.Join.Spectator","&cYou didn't join the game, so you can only spectate!");
        plugin.getConfig().addDefault("Messages.Command.JoinError","&3You are already in the game!");
        plugin.getConfig().addDefault("Messages.Command.LeaveError","&cYou are not in the game!");
        plugin.getConfig().addDefault("Messages.Command.Help.Join","/uhc join - Join the game");
        plugin.getConfig().addDefault("Messages.Command.Help.Leave","/uhc leave - Leave the game");
        plugin.getConfig().addDefault("Messages.Command.Help.Reload","/uhc reload - Reload config file");
        plugin.getConfig().addDefault("Messages.Command.Help.Start","/uhc start - Force start the game/pvp/border");
        plugin.getConfig().addDefault("Messages.Command.Help.Seed","/uhc seed [new] - Generates or sets a new seed for the Randomizer");
        plugin.getConfig().addDefault("Messages.Bar.Start","&6Game starting in {0} second(s)");
        plugin.getConfig().addDefault("Messages.Bar.PVP.Min","&cPVP starts in {0} minute(s)");
        plugin.getConfig().addDefault("Messages.Bar.PVP.Sec","&cPVP starts in {0} second(s)");
        plugin.getConfig().addDefault("Messages.Bar.Border.Min","&dBorder starts in {0} minute(s)");
        plugin.getConfig().addDefault("Messages.Bar.Border.Sec","&dBorder starts in {0} second(s)");
        plugin.getConfig().addDefault("Messages.Bar.Border.Moving","&dBorder moving in {0} second(s)");
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
    }

    public void onDisable() {
        WorldManager.deleteWorld();
        this.getLogger().log(Level.INFO,"Plugin disabled. Thanks for playing!");
    }

    public static void announce(String msg){
        plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&',msg));
    }

    private void addNotch(){
        ItemStack item = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
        NamespacedKey key = new NamespacedKey(this, "notch_apple");
        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape("GGG", "GAG", "GGG");
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        recipe.setIngredient('A', Material.APPLE);
        Bukkit.addRecipe(recipe);
    }

    public static JavaPlugin getPlugin(){
        return plugin;
    }

    public static boolean isPvP(){
        return (PVP == 0);
    }

    public static boolean gameStarted(){
        return isStarted;
    }

    private void start() {
        if (!WorldManager.createWorld()) {
            getServer().getLogger().log(Level.SEVERE, "An error occured while creating the world for the game! Starting aborted.");
            return;
        }
        World world;
        try {
            world = WorldManager.getWorld();
        } catch (NullPointerException e) {
            getServer().getLogger().log(Level.SEVERE, "An error occured while trying to get the world for the game! Starting aborted.");
            return;
        }
        WorldBorder border = world.getWorldBorder();
        border.setCenter(0, 0);
        border.setSize(BORDER_SIZE);
        getServer().setWhitelist(false);
        this.getLogger().log(Level.INFO,"UltraHardcore is now ready for action! Have fun~");
        pc.runTaskTimer(this, 0, 20);
        for (Player online : Bukkit.getOnlinePlayers()) {
            bar.addPlayer(online);
        }
    }

    public static void setPvP(int newValue){
        PVP = newValue;
    }

    public static int getPvP(){
        return PVP;
    }

    public static void startBorder(){
        bc.runTaskTimer(plugin,0,20);
    }

    public static void startShutdown(){
        rc.runTaskTimer(plugin,0,20);
    }

    private static void setupColors(){
        colors.add(Color.BLUE);
        colors.add(Color.LIME);
        colors.add(Color.OLIVE);
        colors.add(Color.ORANGE);
        colors.add(Color.PURPLE);
        colors.add(Color.WHITE);
        colors.add(Color.AQUA);
        colors.add(Color.GREEN);
        colors.add(Color.YELLOW);
        colors.add(Color.RED);
        startcolor = randBarColor();
        pvpcolor = randBarColor();
        bordercolor = randBarColor();
    }

    private static Color randomColor(){
        int count = colors.size();
        int seed = (int)System.currentTimeMillis();
        seed = (int)(seed * Math.random());
        int random_int = random(seed,count);
        return colors.get(random_int);
    }

    private static BarColor randBarColor(){
        ArrayList<BarColor> names = new ArrayList<>(Arrays.asList(BarColor.values()));
        names.remove(BarColor.WHITE);
        int count = names.size();
        int seed = (int)System.currentTimeMillis();
        seed = (int)(seed * Math.random());
        int random_int = random(seed,count);
        return names.get(random_int);
    }

    private static int random(int seed, int i) {
        Random randnum = new Random();
        randnum.setSeed(seed/i);
        return randnum.nextInt(i);
    }

    public static void spawnFireworks(Location location, int amount){
        Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(0);
        Color c = randomColor();
        fwm.addEffect(FireworkEffect.builder().withColor(c).flicker(true).trail(false).build());

        fw.setFireworkMeta(fwm);

        for(int i = 0;i<amount; i++){
            Location newLocation = location.add(new Vector(Math.random()-0.5, 5, Math.random()-0.5).multiply(10));
            Firework fw2 = (Firework) location.getWorld().spawnEntity(newLocation, EntityType.FIREWORK);
            FireworkMeta fw2m = fw.getFireworkMeta();
            Color c2 = randomColor();
            fwm.addEffect(FireworkEffect.builder().withColor(c2).flicker(true).trail(false).build());
            fw2m.setPower(0);
            fw2.setFireworkMeta(fw2m);
        }
    }

    public static WorldBorder getBorder() throws NullPointerException{
        World world = WorldManager.getWorld();
        return world.getWorldBorder();
    }

    private static void setupPlayers(){
        for(Player online : Bukkit.getOnlinePlayers()) {
            if(online.getGameMode() == GameMode.SURVIVAL)
                list.add(online);
        }
    }

    private static void setupBoard(){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective("test", "playerKillCount", "Player score");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        for(Player online : Bukkit.getOnlinePlayers()) {
            Score score = objective.getScore(online.getName());
            score.setScore(0);
        }
        for(Player online : Bukkit.getOnlinePlayers()) {
            list.add(online);
            online.setScoreboard(board);
            online.setGameMode(GameMode.SURVIVAL);
        }
    }

    public static Scoreboard getBoard(){
        Scoreboard b = board;
        if(b == null){
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            b = manager.getNewScoreboard();
        }
        return b;
    }

    public static void addScore(Player player, int amount){
        Objective objective = board.getObjective("test");
        Score score = objective.getScore(player.getName());
        score.setScore(score.getScore()+amount);
        for(Player online : Bukkit.getOnlinePlayers()){
            online.setScoreboard(board);
        }
    }

    public static boolean gameCheck(Player player){
        return join.contains(player);
    }

    public static void gameJoin(Player player){
        join.add(player);
    }

    public static void gameQuit(Player player){
        join.remove(player);
    }

    public static void leave(Player player){
        FORCE_START = false;
        if(list.size() > 0) {
            announce(Main.getPlugin().getConfig().getString("Messages.GameEnd.Announce").replace("{0}", winner.getName()));
            list.remove(player);
            if(list.size() == 1){
                try{
                    if(!pc.isCancelled()) pc.cancel();
                } catch (IllegalStateException e){
                    try{
                        if(!bc.isCancelled()) bc.cancel();
                    } catch (IllegalStateException ignored){}
                }
                winner = list.get(0);
                announce(Main.getPlugin().getConfig().getString("Messages.GameEnd.Announce").replace("{0}", winner.getName()));
                startShutdown();
            }
        } else {
            pc.setStart(getPlugin().getConfig().getInt("START"));
            gameQuit(player);
        }
    }

    public static void playerDeath(Player p){
        list.remove(p);
        if(list.size() == 1){
            try{
                if(!pc.isCancelled()) pc.cancel();
            } catch (IllegalStateException e){
                try{
                    if(!bc.isCancelled()) bc.cancel();
                } catch (IllegalStateException ignored){}
            }
            for(Player online : Bukkit.getOnlinePlayers()) {
                if(online.getGameMode() == GameMode.SURVIVAL){
                    winner = online;
                }
            }
            announce(Main.getPlugin().getConfig().getString("Messages.GameEnd.Announce").replace("{0}", winner.getName()));
            startShutdown();
        }
    }

    private static void teleportPlayers(){
        for(Player online : Bukkit.getOnlinePlayers()){
            boolean isSafe = false;
            World w;
            try{
                w = WorldManager.getWorld();
            } catch (NullPointerException e){
                plugin.getServer().getLogger().log(Level.SEVERE,"Error: "+e.getMessage());
                return;
            }
            Location loc = null;
            while(!isSafe) {
                loc = getRandomLocation(w);
                loc.setY(w.getHighestBlockAt(loc.getBlockX(), loc.getBlockZ()).getY());
                try {
                    loc = LocationUtil.getSafeDestination(loc);
                    isSafe = true;
                } catch (Exception ignored) {}
            }
            online.teleport(loc);
            if(join.contains(online)) {
                online.setGameMode(GameMode.SURVIVAL);
            } else {
                online.setGameMode(GameMode.SPECTATOR);
                online.sendMessage(c(Main.getPlugin().getConfig().getString("Messages.Game.Join.Spectator")));
            }
            online.setFoodLevel(20);
            online.setHealth(20);
            online.getInventory().clear();

        }
    }

    public static Location getRandomLocation(World world) {
        final double randomX = ThreadLocalRandom.current().nextDouble(0, world.getWorldBorder().getSize() / 2);
        final double randomY = ThreadLocalRandom.current().nextDouble(0, world.getWorldBorder().getSize() / 2);
        return world.getWorldBorder().getCenter().add(randomX, 0, randomY);
    }

    public static boolean isEnded() {
        return (list.size() == 1);
    }

    public static void setGameStart(int value){
        pc.setStart(value);
    }

    public static void setBorderStart(int value){
        bc.setStart(value);
    }

    public static void setBorderSpeed(int value){
        bc.setSpeed(value);
    }

    public static String c(String m){
        return ChatColor.translateAlternateColorCodes('&',m);
    }

    public class PVP extends BukkitRunnable {
        private int count,count_def;
        private int start,start_def;
        private boolean isFirst = true;

        public PVP(){
            count = Main.getPvP();
            count_def = Main.getPvP();
            start = Main.getPlugin().getConfig().getInt("START");
            start_def = Main.getPlugin().getConfig().getInt("START");
        }

        public void setStart(int value){
            start = value;
        }

        @Override
        public void run() {
            if(start == 0) {
                if (count >= 60) {
                    int min = (count / 60);
                    if (count % 60 == 0) {
                        announce(Main.getPlugin().getConfig().getString("Messages.PVP.Min").replace("{0}", Integer.toString(min)));
                        isFirst = false;
                        if(BOSSBAR_MESSAGES){
                            bar.setTitle(c(Main.getPlugin().getConfig().getString("Messages.PVP.Min").replace("{0}", Integer.toString(min))));
                        }
                    }
                    if(isFirst){
                        bar.setTitle(c(Main.getPlugin().getConfig().getString("Messages.PVP.Min").replace("{0}", Integer.toString(min))));
                        isFirst = false;
                    }
                    if(BOSSBAR_MESSAGES) {
                        double progress = (double) count / (double) count_def;
                        bar.setVisible(true);
                        bar.setColor(pvpcolor);
                        bar.setProgress(progress);
                    }
                } else {
                    if(isFirst){
                        announce(Main.getPlugin().getConfig().getString("Messages.PVP.Sec").replace("{0}", Integer.toString(count)));
                        if(BOSSBAR_MESSAGES) {
                            double progress = (double) count / (double) count_def;
                            bar.setVisible(true);
                            bar.setColor(pvpcolor);
                            bar.setTitle(c(Main.getPlugin().getConfig().getString("Messages.PVP.Sec").replace("{0}", Integer.toString(count))));
                            bar.setProgress(progress);
                        }
                        isFirst = false;
                    } else {
                        double progress = (double)count/(double)count_def;
                        switch (count) {
                            case 10:
                            case 3:
                            case 2:
                            case 1:
                                announce(Main.getPlugin().getConfig().getString("Messages.PVP.Sec").replace("{0}", Integer.toString(count)));
                                break;
                            case 0:
                                announce(Main.getPlugin().getConfig().getString("Messages.PVP.Start"));
                                this.cancel();
                                if (BORDER_ALLOW) Main.startBorder();
                                break;
                            default:
                                if(BOSSBAR_MESSAGES) {
                                    bar.setVisible(true);
                                    bar.setColor(pvpcolor);
                                    bar.setTitle(c(Main.getPlugin().getConfig().getString("Messages.PVP.Sec").replace("{0}", Integer.toString(count))));
                                    bar.setProgress(progress);
                                }
                                break;
                        }
                    }
                }
                if(count > 0) count--;
                Main.setPvP(count);
            } else {
                if(Main.join.size() < Main.MIN_PLAYERS && !Main.FORCE_START){
                    if(bar.isVisible()) bar.setVisible(false);
                    return;
                }
                double progress = (double)start/(double)start_def;
                switch(start) {
                    case 30:
                    case 10:
                        if(!BOSSBAR_MESSAGES) announce(Main.getPlugin().getConfig().getString("Messages.GameStart").replace("{0}", Integer.toString(start)));
                        if(BOSSBAR_MESSAGES) {
                            bar.setVisible(true);
                            bar.setColor(startcolor);
                            bar.setTitle(c(Main.getPlugin().getConfig().getString("Messages.Bar.Start").replace("{0}", Integer.toString(start))));
                            bar.setProgress(progress);
                        }
                        break;
                    case 3:
                    case 2:
                        announce(Main.getPlugin().getConfig().getString("Messages.GameStart").replace("{0}", Integer.toString(start)));
                        if(BOSSBAR_MESSAGES) {
                            bar.setVisible(true);
                            bar.setColor(startcolor);
                            bar.setTitle(c(Main.getPlugin().getConfig().getString("Messages.Bar.Start").replace("{0}", Integer.toString(start))));
                            bar.setProgress(progress);
                        }
                        break;
                    case 1:
                        isStarted = true;
                        announce(Main.getPlugin().getConfig().getString("Messages.GameStart").replace("{0}", Integer.toString(start)));
                        if(SCOREBOARD) setupBoard();
                        else setupPlayers();
                        getServer().setWhitelist(true);
                        teleportPlayers();
                        if(BOSSBAR_MESSAGES) {
                            bar.setVisible(true);
                            bar.setColor(startcolor);
                            bar.setTitle(c(Main.getPlugin().getConfig().getString("Messages.Bar.Start").replace("{0}", Integer.toString(start))));
                            bar.setProgress(progress);
                        }
                        break;
                    default:
                        if(BOSSBAR_MESSAGES) {
                            bar.setVisible(true);
                            bar.setColor(startcolor);
                            bar.setTitle(c(Main.getPlugin().getConfig().getString("Messages.Bar.Start").replace("{0}", Integer.toString(start))));
                            bar.setProgress(progress);
                        }
                        if(start < 60) break;
                        if(start % 60 == 0){
                            announce(Main.getPlugin().getConfig().getString("Messages.GameStart").replace("{0}", Integer.toString(start)));
                        }
                        break;
                }
                start--;
            }
        }
    }

    public class Border extends BukkitRunnable {
        private int BORDER_WAIT;
        private int BORDER_START_WAIT,BSW_DEF;
        private int BORDER_SPEED;
        private int BORDER_SIZE;
        private int wait;
        private WorldBorder border;
        private boolean isFirst = true;

        public Border(){
            BORDER_SIZE = Main.BORDER_SIZE;
            BORDER_SPEED = Main.BORDER_SPEED;
            BORDER_WAIT = Main.BORDER_WAIT;
            BORDER_START_WAIT = Main.BORDER_START_WAIT;
            BSW_DEF = Main.BORDER_START_WAIT;
            wait = BORDER_WAIT;
        }

        public void setStart(int value){
            if(BORDER_START_WAIT > 0 && value > 0){
                BORDER_START_WAIT = value;
            }
        }

        public void setSpeed(int value){
            if(value > 0){
                BORDER_SPEED = value;
            }
        }

        @Override
        public void run() {
            if(BORDER_START_WAIT == 0) {
                try {
                    border = Main.getBorder();
                } catch (NullPointerException e){
                    getServer().getLogger().log(Level.SEVERE,"Error: "+e.getMessage());
                    return;
                }
                if(bar.isVisible()) bar.setVisible(false);
                if(isFirst){
                    BORDER_SIZE = BORDER_SIZE / 2;
                    border.setSize(BORDER_SIZE, BORDER_SPEED);
                    isFirst = false;
                    String size = (BORDER_SIZE/2)+"x"+(BORDER_SIZE/2);
                    announce(Main.getPlugin().getConfig().getString("Messages.Border.Moving").replace("{0}",size));
                    return;
                }
                if (wait == 0) {
                    BORDER_SIZE = BORDER_SIZE / 2;
                    if (BORDER_SIZE < 4) {
                        BORDER_SIZE = 4;
                        border.setSize(BORDER_SIZE, 4);
                        this.cancel();
                    } else {
                        border.setSize(BORDER_SIZE, BORDER_SPEED);
                        wait--;
                    }
                    String size = (BORDER_SIZE/2)+"x"+(BORDER_SIZE/2);
                    announce(Main.getPlugin().getConfig().getString("Messages.Border.Moving").replace("{0}",size));
                } else if (wait == -1) {
                    if (border.getSize() == BORDER_SIZE) {
                        if (BORDER_WAIT > 10) BORDER_WAIT = (int) (BORDER_WAIT / 1.5);
                        wait = BORDER_WAIT;
                        if(BOSSBAR_MESSAGES) {
                            double progress = (double) wait / (double) BORDER_WAIT;
                            bar.setVisible(true);
                            bar.setColor(bordercolor);
                            bar.setTitle(c(Main.getPlugin().getConfig().getString("Messages.Bar.Border.Moving").replace("{0}", Integer.toString(wait))));
                            bar.setProgress(progress);
                        }
                    }
                } else {
                    if(BOSSBAR_MESSAGES) {
                        double progress = (double) wait / (double) BORDER_WAIT;
                        bar.setVisible(true);
                        bar.setColor(bordercolor);
                        bar.setTitle(c(Main.getPlugin().getConfig().getString("Messages.Bar.Border.Moving").replace("{0}", Integer.toString(wait))));
                        bar.setProgress(progress);
                    }
                    wait--;
                }
            } else {
                int min = (BORDER_START_WAIT / 60);
                if(BORDER_START_WAIT >= 300){
                    if(BORDER_START_WAIT % 300 == 0) {
                        announce(Main.getPlugin().getConfig().getString("Messages.Border.Start.Min").replace("{0}", Integer.toString(min)));
                    }
                    if(BOSSBAR_MESSAGES) {
                        double progress = (double) BORDER_START_WAIT / (double) BSW_DEF;
                        bar.setVisible(true);
                        bar.setColor(bordercolor);
                        bar.setProgress(progress);
                        bar.setTitle(c(Main.getPlugin().getConfig().getString("Messages.Bar.Border.Min").replace("{0}", Integer.toString(min))));
                    }
                } else {
                    switch (BORDER_START_WAIT){
                        case 10:
                        case 3:
                        case 2:
                        case 1:
                            announce(Main.getPlugin().getConfig().getString("Messages.Border.Start.Sec").replace("{0}", Integer.toString(BORDER_START_WAIT)));
                            if(BOSSBAR_MESSAGES) {
                                double progress = (double) BORDER_START_WAIT / (double) BSW_DEF;
                                bar.setVisible(true);
                                bar.setColor(bordercolor);
                                bar.setProgress(progress);
                                bar.setTitle(c(Main.getPlugin().getConfig().getString("Messages.Bar.Border.Sec").replace("{0}", Integer.toString(BORDER_START_WAIT))));
                            }
                            break;
                        default:
                            if(BOSSBAR_MESSAGES){
                                double progress = (double) BORDER_START_WAIT / (double) BSW_DEF;
                                bar.setVisible(true);
                                bar.setColor(bordercolor);
                                bar.setProgress(progress);
                            }
                            if(BORDER_START_WAIT > 60) {
                                if (BORDER_START_WAIT % 60 == 0) {
                                    announce(Main.getPlugin().getConfig().getString("Messages.Border.Start.Min").replace("{0}", Integer.toString(min)));
                                    if (BOSSBAR_MESSAGES) {
                                        bar.setTitle(c(Main.getPlugin().getConfig().getString("Messages.Bar.Border.Min").replace("{0}", Integer.toString(min))));
                                    }
                                }
                            } else {
                                if(BOSSBAR_MESSAGES){
                                    bar.setTitle(c(Main.getPlugin().getConfig().getString("Messages.Bar.Border.Sec").replace("{0}", Integer.toString(BORDER_START_WAIT))));
                                }
                            }
                            break;
                    }
                }
                BORDER_START_WAIT--;
            }
        }
    }

    public class Restart extends BukkitRunnable {
        private int start_wait = 15;
        private int wait = 10;

        public Restart(){

        }


        @Override
        public void run() {
            if(start_wait == 0){
                switch (wait){
                    case 10:
                    case 5:
                    case 4:
                    case 3:
                    case 2:
                    case 1:
                        if(SERVER_RESTART)
                            announce(Main.getPlugin().getConfig().getString("Messages.GameEnd.Restart.Count").replace("{0}", Integer.toString(wait)));
                        else
                            announce(Main.getPlugin().getConfig().getString("Messages.GameEnd.Stop.Count").replace("{0}", Integer.toString(wait)));
                        break;
                    case 0:
                        if(SERVER_RESTART)
                            announce(Main.getPlugin().getConfig().getString("Messages.GameEnd.Restart.Now"));
                        else
                            announce(Main.getPlugin().getConfig().getString("Messages.GameEnd.Stop.Now"));
                        break;
                    case -1:
                        plugin.getServer().shutdown();
                        break;
                    default:
                        break;
                }
                wait--;
            } else {
                spawnFireworks(winner.getLocation(),2);
                start_wait--;
            }
        }
    }
}
