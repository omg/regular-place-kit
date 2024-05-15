package games.omg.security;

import java.util.Iterator;
import java.util.List;

import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;

public class SafeExplosions implements Listener {

  /**
   * Protects blocks from block explosions
   */
  @EventHandler
  public void onBlockExplode(BlockExplodeEvent event) {
    // Only protect blocks in the Overworld
    if (event.getBlock().getWorld().getEnvironment() != Environment.NORMAL) {
      return;
    }

    protectBlocks(event.blockList());
  }

  /**
   * Protects blocks from entity explosions
   */
  @EventHandler
  public void onEntityExplode(EntityExplodeEvent event) {
    // Only protect blocks in the Overworld
    if (event.getLocation().getWorld().getEnvironment() != Environment.NORMAL) {
      return;
    }

    protectBlocks(event.blockList());
  }

  /**
   * Removes protected blocks from a block list
   * 
   * @param blocks The list of blocks to protect
   */
  private void protectBlocks(List<Block> blocks) {
    Iterator<Block> iterator = blocks.iterator();

    while (iterator.hasNext()) {
      Block block = iterator.next();

      BlockState blockState = block.getState();

      if (blockState instanceof Container) {
        iterator.remove();
        continue;
      }
    }
  }

  /**
   * Prevent explosions from harming certain entities
   */
  @EventHandler
  public void onExplosionDamage(EntityDamageEvent event) {
    // Check if the damage was caused by an explosion
    if (
      event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
      event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
    ) {
      Entity entity = event.getEntity();

      if (
        entity instanceof Tameable ||
        entity instanceof ArmorStand ||
        // entity instanceof Hanging ||
        event.getEntity().customName() != null
      ) {
        event.setCancelled(true);
      }
    }
  }

  /**
   * Prevent hanging entities from exploding in the Overworld
   */
  @EventHandler
  public void onBreakHangingEntity(HangingBreakEvent event) {
    // Only protect hanging entities in the Overworld
    if (event.getEntity().getLocation().getWorld().getEnvironment() != Environment.NORMAL) {
      return;
    }

    // Cancel explosions
    if (event.getCause() == HangingBreakEvent.RemoveCause.EXPLOSION) {
      event.setCancelled(true);
    }
  }
}