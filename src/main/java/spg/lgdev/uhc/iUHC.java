package spg.lgdev.uhc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import spg.lgdev.uhc.command.permission.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import lombok.Getter;
import lombok.Setter;
import spg.lgdev.uhc.announce.AnnounceManager;
import spg.lgdev.uhc.board.BoardManager;
import spg.lgdev.uhc.board.impl.UHCAdapter;
import spg.lgdev.uhc.board.placeholders.ScoreboardPlaceholders;
import spg.lgdev.uhc.border.BorderStyle;
import spg.lgdev.uhc.command.CommandManager;
import spg.lgdev.uhc.command.abstracton.UHCCommand;
import spg.lgdev.uhc.command.player.BackpackCommand;
import spg.lgdev.uhc.command.player.ExtraInventoryCommand;
import spg.lgdev.uhc.command.player.HealthCommand;
import spg.lgdev.uhc.command.player.InventoryCommand;
import spg.lgdev.uhc.command.player.KillCountCommand;
import spg.lgdev.uhc.command.player.MLGCommand;
import spg.lgdev.uhc.command.player.PingCommand;
import spg.lgdev.uhc.command.player.PracticeCommand;
import spg.lgdev.uhc.command.player.SendCoordsCommand;
import spg.lgdev.uhc.command.player.TeamChatCommand;
import spg.lgdev.uhc.command.player.TeamCommands;
import spg.lgdev.uhc.command.vip.LateScatterCommand;
import spg.lgdev.uhc.config.CachedConfig;
import spg.lgdev.uhc.enums.DMArenaType;
import spg.lgdev.uhc.enums.GameStatus;
import spg.lgdev.uhc.gui.gameconfig.AntiCraftingGUI;
import spg.lgdev.uhc.gui.gameconfig.PreConfigGUI;
import spg.lgdev.uhc.handler.game.KitsHandler;
import spg.lgdev.uhc.handler.game.Loggers;
import spg.lgdev.uhc.handler.game.UHCGame;
import spg.lgdev.uhc.handler.impl.CombatTimer;
import spg.lgdev.uhc.handler.impl.DisconnectTimer;
import spg.lgdev.uhc.handler.impl.NocleanTimer;
import spg.lgdev.uhc.listener.BlockListener;
import spg.lgdev.uhc.listener.ChatListener;
import spg.lgdev.uhc.listener.DamageListener;
import spg.lgdev.uhc.listener.DeathListener;
import spg.lgdev.uhc.listener.FreezeListener;
import spg.lgdev.uhc.listener.GameListener;
import spg.lgdev.uhc.listener.InventoryListener;
import spg.lgdev.uhc.listener.JoinListener;
import spg.lgdev.uhc.listener.MainListener;
import spg.lgdev.uhc.listener.QuitListener;
import spg.lgdev.uhc.listener.ScenariosHandler;
import spg.lgdev.uhc.listener.Ver1_8Listener;
import spg.lgdev.uhc.manager.ArenaManager;
import spg.lgdev.uhc.manager.ChunkManager;
import spg.lgdev.uhc.manager.FileManager;
import spg.lgdev.uhc.manager.InventoryManager;
import spg.lgdev.uhc.manager.ItemManager;
import spg.lgdev.uhc.manager.ProfileManager;
import spg.lgdev.uhc.nms.NMSHandler;
import spg.lgdev.uhc.nms.packetlistener.SignGUIPacketListener;
import spg.lgdev.uhc.nms.packetlistener.SoundPacketListener;
import spg.lgdev.uhc.player.database.UHCMySQL;
import spg.lgdev.uhc.task.InviteCleanupTask;
import spg.lgdev.uhc.util.StringUtil;
import spg.lgdev.uhc.util.UHCSound;
import spg.lgdev.uhc.util.Utils;
import spg.lgdev.uhc.util.VaultUtil;
import spg.lgdev.uhc.util.bossbar.BossBarAPI;
import spg.lgdev.uhc.util.inventory.UIListener;
import spg.lgdev.uhc.visualise.VisualiseHandler;
import spg.lgdev.uhc.world.Biomes;
import spg.lgdev.uhc.world.UHCWorldCreator;
import spg.lgdev.uhc.world.generator.GeneratorCommand;
import net.development.mitw.packetlistener.PacketHandler;
import net.development.mitw.utils.FastRandom;

/**
 * @author Mirez & Mitw
 * @version 1.0.3
 */
public class iUHC extends iUHCEngine {

	@Getter
	private static final FastRandom random = new FastRandom();

	@Getter
	private static iUHC instance;

	@Getter
	public final String version = "1.0-D-SNAPSHOT";
	public final String name = "UHC";

	public boolean development = false;
	public boolean vault = false;
	public boolean placeholderAPI = false;
	public boolean multiverse = false;

	@Getter
	@Setter
	private boolean disabling;

	@Getter
	private UHCWorldCreator worldCreator;
	@Getter
	private FileManager fileManager;
	@Getter
	private BoardManager sidebarManager;
	@Getter
	private ChunkManager chunkManager;
	@Getter
	private ItemManager itemManager;
	@Getter
	private VisualiseHandler barrierManager;
	@Getter
	private ProfileManager profileManager;
	@Getter
	@Setter
	private BorderStyle borderStyle;
	@Getter
	private AnnounceManager announceManager;
	@Getter
	private CommandManager commandManager;

	//-- Timers
	@Getter
	@Setter
	private CombatTimer combatTimer;
	@Getter
	@Setter
	private DisconnectTimer disconnectTimer;
	@Getter
	@Setter
	private NocleanTimer nocleanTimer;


	@Override
	public void onEnable() {
		instance = this;

		this.fileManager = new FileManager();
		this.fileManager.checkUpdater();
		CachedConfig.reloadConfig();

		new NMSHandler(this);
		new UHCGame(this);

		GameStatus.set(GameStatus.LOADING);

		this.chunkManager = new ChunkManager("UHCArena");
		this.profileManager = new ProfileManager(this);
		this.itemManager = new ItemManager(this);
		this.borderStyle = new BorderStyle(getConfig());
		this.barrierManager = new VisualiseHandler(this);
		this.worldCreator = new UHCWorldCreator(this);
		this.announceManager = new AnnounceManager(this);
		this.commandManager = new CommandManager();
		this.commandManager.registerSimpleCommands(this);

		this.setBoardManager(new BoardManager(this, new UHCAdapter(this), new ScoreboardPlaceholders(this)));

		if (CachedConfig.SQL) {
			UHCMySQL.getInstance().openConnection();
		}
		if (CachedConfig.BIOMESWAP) {
			Biomes.setupBiomes(this);
		}

		this.registerDependency();
		this.registerCommands();
		this.registerEvents();
		UHCSound.load();
		UHCGame.getInstance().setWhitelisted(true);

		new Loggers(this);
		new ArenaManager();
		new KitsHandler();

		new InviteCleanupTask(this);
		this.getServer().getScheduler().runTaskTimerAsynchronously(this, this, 20L, 20L);

		PacketHandler.getInstance().register(new SignGUIPacketListener());
		PacketHandler.getInstance().register(new SoundPacketListener());

		ScenariosHandler.setupGoldenHeads();

		this.getServer().setSpawnRadius(0);
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		log(true, "iUHC enabled");

		if (development = fileManager.getCache().getBoolean("testmode")) {
			log(true, "Test mode enabled");
			CachedConfig.setIsGenerated();
		}

		if (CachedConfig.PerformanceMode) {
			log(true, "Performance mode enabled");
		}

		if (CachedConfig.AVOID_ASYNC_CATCHER) {
			Utils.closeAsyncCatcher();
		}

		clearArena();

		if (CachedConfig.IsGenerated() || CachedConfig.IsGenerating()) {
			Bukkit.getScheduler().runTaskLater(this, () -> {

				worldCreator.importWorlds("UHCArena");
				if (CachedConfig.IsGenerating()) {
					chunkManager.load(iUHC.this, "UHCArena");
					chunkManager.continueLoading(iUHC.this, CachedConfig.getWorldIsInGen());
					chunkManager.moveToNext(iUHC.this);
				}

			}, 20L);
		}
	}

	@Override
	public void onDisable() {

		setDisabling(true);

		if (CachedConfig.SQL) {
			UHCMySQL.getInstance().closeConnection();
		}

		getServer().getScheduler().cancelTasks(this);
		UHCCommand.unregisterAll();

	}

	public Set<Player> getNotInGame() {
		return getServer().getOnlinePlayers().stream().filter(player -> !getProfileManager().isAlive(player)).collect(Collectors.toSet());
	}

	public void clearArena() {
		if (!CachedConfig.IsGenerated() && !CachedConfig.IsGenerating()) {
			if (Bukkit.getWorld("UHCArena") != null) {
				if (multiverse) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv delete UHCArena");
				}
				Bukkit.unloadWorld(Bukkit.getWorld("UHCArena"), false);
			}
			if (Bukkit.getWorld("UHCArena_nether") != null) {
				if (multiverse) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv delete UHCArena_nether");
				}
				Bukkit.unloadWorld(Bukkit.getWorld("UHCArena_nether"), false);
			}
			if (Bukkit.getWorld("UHCArena_deathmatch") != null && !UHCGame.getInstance().getDeathmatchArenaType().equals(DMArenaType.CUSTOM)) {
				if (multiverse) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv delete UHCArena_deathmatch");
				}
				Bukkit.unloadWorld(Bukkit.getWorld("UHCArena_deathmatch"), false);
			}
			if (Utils.fileExists("UHCArena")) {
				worldCreator.deleteWorld("UHCArena");
			}
			if (Utils.fileExists("UHCArena_nether")) {
				worldCreator.deleteWorld("UHCArena_nether");
			}
			if (Utils.fileExists("UHCArena_deathmatch")) {
				worldCreator.deleteWorld("UHCArena_deathmatch");
			}
			log(false, "[DEBUG] Some of the worlds weren't removed previously, system has force-delete them!");
		}
	}

	private void registerDependency() {
		final PluginManager pm = this.getServer().getPluginManager();

		if (pm.getPlugin("Multiverse-Core") != null) {

			log(true, "&aMultiverse-Core has been hooked");
			this.multiverse = true;

		}

		if (pm.getPlugin("PlaceholderAPI") != null) {

			log(true, "&aPlaceholderAPI has been hooked");
			this.placeholderAPI = true;

		}

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb shape square");

		if (pm.getPlugin("Vault") != null) {

			log(true, "&aVault has been hooked");
			VaultUtil.setupChat();
			this.vault = true;

		}

	}

	private void registerEvents() {

		final List<Listener> list = new ArrayList<>(Arrays.asList(new DamageListener(this),
				new JoinListener(this),
				new QuitListener(this),
				new AntiCraftingGUI(),
				new PreConfigGUI(),
				itemManager,
				chunkManager,
				new MainListener(this),
				new DeathListener(this),
				new ScenariosHandler(),
				new FreezeListener(this),
				new InventoryManager(this),
				new InventoryListener(),
				new UIListener(),
				new GameListener(),
				new BossBarAPI(),
				new BlockListener(),
				new Ver1_8Listener()));

		if (getConfig().getBoolean("Chat.Enabled")) {
			list.add(new ChatListener(this));
		}

		list.forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));

	}

	public void registerCommands() {
		Arrays.asList(
				new BroadRulesCommand(),
				new ChatSpectateCommand(),
				new FreezeCommand(),
				new GiveAllCommand(),
				new HealCommand(),
				new HostSetCommand(),
				new KickSpectatorsCommand(),
				new MuteChatCommand(),
				new PregameCommand(),
				new RemoveHostCommand(),
				new RemoveModCommand(),
				new RescatterCommand(),
				new RespawnCommand(),
				new ScatterPlayerCommand(),
				new SpectatorSetCommand(),
				new ModSetCommand(),
				new StaffCommand(),
				new WhitelistCommand(),
				new BackpackCommand(),
				new ExtraInventoryCommand(),
				new HealthCommand(),
				new InventoryCommand(),
				new KillCountCommand(),
				new MLGCommand(),
				new PingCommand(),
				new PracticeCommand(),
				new SendCoordsCommand(),
				new TeamChatCommand(),
				new TeamCommands(),
				new LateScatterCommand(),
				new MainCommand(),
				new GeneratorCommand(this),
				new PostWinnierCommand()
				).forEach(Utils::registerCommand);
	}

	public void log(final boolean isPrefix, final String message) {
		Bukkit.getConsoleSender().sendMessage((isPrefix ? "§f[§6UHC§f] §6" : "") + StringUtil.cc(message));
	}

	public void setBoardManager(final BoardManager manager) {
		this.sidebarManager = manager;
		this.sidebarManager.runTaskTimerAsynchronously(this, manager.getAdapter().getInterval(), manager.getAdapter().getInterval());
	}

}
