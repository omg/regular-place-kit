package games.omg.behavior;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Repairable;

/**
 * This class handles anvil repair costs, and most notably prevents anvils from
 * displaying "Too Expensive!".
 */
public class AnvilCostFix implements Listener {

  final private static int RENAME_COST = 2;
  final private static int REPAIR_COST = 1;

  final private static int MAX_COST = 38;

  /**
   * Sets the maximum repair cost of any opened AnvilInventory to be the integer
   * limit.
   * 
   * AnvilInventories only produce result items when the cost is less than their
   * stored maximum repair cost.
   * Setting the maximum repair cost to the integer limit will allow the game to
   * always produce a result if it can.
   */
  @EventHandler
  public void onAnvilOpen(InventoryOpenEvent event) {
    if (event.getInventory() instanceof AnvilInventory) {
      AnvilInventory inventory = ((AnvilInventory) event.getInventory());
      inventory.setMaximumRepairCost(Integer.MAX_VALUE);
    }
  }

  /**
   * Sets the repair cost of the resulting item in the anvil to be, at most, the
   * maximum repair cost.
   * This is done to prevent any item from acquiring an internal repair cost of 39
   * or more, which would cause the anvil to display "Too Expensive!"
   * and not allow the repair. This "Too Expensive!" message is client-side, and
   * is not a server-side restriction, so it is not possible to
   * bypass it.
   */
  @EventHandler
  public void onAnvilInventoryClick(InventoryClickEvent event) {
    if (!(event.getInventory() instanceof AnvilInventory)) {
      return;
    }

    AnvilInventory inventory = ((AnvilInventory) event.getInventory());
    ItemStack clickedItem = event.getCurrentItem();

    if (clickedItem == null) {
      return;
    }

    if (clickedItem == inventory.getResult()) {
      Repairable itemMeta = (Repairable) clickedItem.getItemMeta();
      // Note: Repair cost is stored as a 0-based value, so an item with a repair cost
      // of 0 will actually cost 1 level to repair.
      itemMeta.setRepairCost(clamp(itemMeta.getRepairCost(), 0, MAX_COST - 1));
      clickedItem.setItemMeta(itemMeta);
      event.setCurrentItem(clickedItem);
    }
  }

  /**
   * Sets the repair cost of the resulting item in the anvil to the combined
   * repair cost of the two items.
   */
  @EventHandler
  public void onAnvilPrepare(PrepareAnvilEvent event) {
    AnvilInventory anvil = event.getInventory();
    ItemStack result = anvil.getResult();

    // We don't need to do anything if there's no resulting item
    if (result == null) {
      return;
    }

    ItemStack firstItem = anvil.getFirstItem();
    ItemStack secondItem = anvil.getSecondItem();

    int anvilCost;

    if (secondItem == null) {
      // We're renaming, set the cost to the rename cost
      anvilCost = RENAME_COST;
    } else {
      if (isRepair(firstItem, secondItem)) {
        // We're repairing, set the cost to the repair cost
        anvilCost = REPAIR_COST;
      } else {
        anvilCost = clamp(anvil.getRepairCost(), 1, MAX_COST);
      }
    }

    anvil.setRepairCost(anvilCost);
  }

  private int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(max, value));
  }

  /**
   * Checks if the two items could combine for a repair.
   * 
   * Repairs can be completed with a repairable item and a material that can
   * repair it, such as a Diamond Pickaxe
   * and a Diamond.
   * 
   * They can also be completed with two items of the same type, with one of them
   * having no enchantments.
   * If any of the items have enchantments, it is considered an upgrade, and this
   * method will return false.
   * 
   * @param item1 The first item
   * @param item2 The second item
   * @return Whether or not the two items could combine for a repair
   */
  private boolean isRepair(ItemStack item1, ItemStack item2) {
    // If the first item is not repairable, then this cannot be a repair
    if (!(item1.getItemMeta() instanceof Repairable)) {
      return false;
    }
    // If there is no second item, then this cannot be a repair
    if (item2 == null) {
      return false;
    }
    // Diamond Pickaxes are "repairable by" only Diamonds, isRepairableBy doesn't
    // return true with another Diamond Pickaxe
    if (item1.isRepairableBy(item2)) {
      return true;
    }
    // Here we're checking if the first item and the second item are the same type
    // for a repair
    if (item1.getType() == item2.getType()) {
      // One of the items must not have any enchantments for this to be a simple
      // repair
      // Otherwise, that would be an upgrade, since the enchantments would be combined
      return item1.getEnchantments().isEmpty() || item2.getEnchantments().isEmpty();
    }
    return false;
  }
}
