package games.omg;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import games.omg.behavior.AnvilCostFix;
import games.omg.behavior.NoEggTeleport;
import games.omg.channeling.TeleportTools;
import games.omg.chat.ChatHandler;
import games.omg.command.CommandManager;
import games.omg.server.MotdService;
import games.omg.security.SafeExplosions;

public class Main extends JavaPlugin implements Listener {

	private static JavaPlugin plugin;

	public static void register(Listener ...listeners) {
		for (Listener listener : listeners) {
			plugin.getServer().getPluginManager().registerEvents(listener, plugin);
		}
	}

	public static void registerCommand(String command, CommandExecutor executor) {
		plugin.getCommand(command).setExecutor(executor);
	}

	@Override
	public void onEnable() {
		plugin = this;

		register(
			new ChatHandler(),
			new TeleportTools(),
			new MotdService(),
			new SafeExplosions(),
			new AnvilCostFix(),
			new NoEggTeleport()
		);

		// registerCommand("tpa", new Tpa());	

		// this is really dumb
		CommandManager commandManager = new CommandManager();
	}

	@Override
	public void onDisable() {
		plugin = null;
	}

	public static JavaPlugin getPlugin() {
		return plugin;
	}
}
