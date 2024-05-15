package games.omg.behavior;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class NoEggTeleport implements Listener {
  
  /**
   * Prevent the Dragon Egg from being teleported by right clicking it
   */
  @EventHandler
  public void onEggTeleport(PlayerInteractEvent event) {
    if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.DRAGON_EGG) {
      event.setCancelled(true);
    }
  }
}
