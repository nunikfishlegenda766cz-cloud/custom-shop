package cz.tvojeserver.customshop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class CustomShop extends JavaPlugin implements CommandExecutor, Listener {

    private final String MAIN_TITLE = ChatColor.DARK_GREEN + "Obchod";
    private final Map<String, List<TradeItem>> categories = new LinkedHashMap<>();

    private static class TradeItem {
        Material reward;
        int rewardAmount;
        String displayName;
        Material currency;
        int cost;

        TradeItem(Material reward, int rewardAmount, String displayName, Material currency, int cost) {
            this.reward = reward;
            this.rewardAmount = rewardAmount;
            this.displayName = displayName;
            this.currency = currency;
            this.cost = cost;
        }
    }

    @Override
    public void onEnable() {
        setupShop();
        getCommand("shop").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("CustomShop byl uspesne zapnut!");
    }

    private void setupShop() {
        categories.put("Bloky", Arrays.asList(
                new TradeItem(Material.OAK_LOG, 32, "Dubové dřevo (32x)", Material.EMERALD, 1),
                new TradeItem(Material.COBBLESTONE, 64, "Kamení (64x)", Material.EMERALD, 1),
                new TradeItem(Material.GLASS, 32, "Sklo (32x)", Material.EMERALD, 1),
                new TradeItem(Material.QUARTZ_BLOCK, 16, "Křemenné bloky (16x)", Material.EMERALD, 2),
                new TradeItem(Material.OBSIDIAN, 8, "Obsidián (8x)", Material.DIAMOND, 1),
                new TradeItem(Material.SEA_LANTERN, 8, "Mořské lucerny (8x)", Material.EMERALD, 3)
        ));

        categories.put("Jídlo", Arrays.asList(
                new TradeItem(Material.COOKED_BEEF, 16, "Pečený steak (16x)", Material.EMERALD, 1),
                new TradeItem(Material.GOLDEN_CARROT, 16, "Zlatá mrkev (16x)", Material.EMERALD, 2),
                new TradeItem(Material.GOLDEN_APPLE, 2, "Zlaté jablko (2x)", Material.DIAMOND, 1),
                new TradeItem(Material.CAKE, 1, "Dort (1x)", Material.EMERALD, 2)
        ));

        categories.put("Suroviny", Arrays.asList(
                new TradeItem(Material.IRON_INGOT, 16, "Železné ingoty (16x)", Material.EMERALD, 2),
                new TradeItem(Material.GOLD_INGOT, 12, "Zlaté ingoty (12x)", Material.EMERALD, 2),
                new TradeItem(Material.DIAMOND, 1, "Diamant (1x)", Material.EMERALD, 5),
                new TradeItem(Material.EMERALD, 5, "Emeraldy (5x)", Material.DIAMOND, 1),
                new TradeItem(Material.ENDER_PEARL, 4, "Ender perly (4x)", Material.EMERALD, 3),
                new TradeItem(Material.BLAZE_ROD, 2, "Blaze tyčky (2x)", Material.DIAMOND, 1)
        ));

        categories.put("Výbava", Arrays.asList(
                new TradeItem(Material.DIAMOND_PICKAXE, 1, "Diamantový krumpáč", Material.DIAMOND, 3),
                new TradeItem(Material.DIAMOND_SWORD, 1, "Diamantový meč", Material.DIAMOND, 2),
                new TradeItem(Material.EXPERIENCE_BOTTLE, 16, "Lahvičky zkušeností (16x)", Material.EMERALD, 3),
                new TradeItem(Material.NAME_TAG, 1, "Jmenovka (Name Tag)", Material.EMERALD, 4),
                new TradeItem(Material.SADDLE, 1, "Sedlo", Material.EMERALD, 3),
                new TradeItem(Material.ELYTRA, 1, "Elytra", Material.DIAMOND, 16)
        ));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;
        openMainMenu(player);
        return true;
    }

    private void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, MAIN_TITLE);

        inv.setItem(10, createIcon(Material.OAK_LOG, ChatColor.GREEN + "" + ChatColor.BOLD + "Stavební bloky",
                Collections.singletonList(ChatColor.GRAY + "Dřevo, kámen, sklo, obsidián")));

        inv.setItem(12, createIcon(Material.COOKED_BEEF, ChatColor.GOLD + "" + ChatColor.BOLD + "Jídlo",
                Collections.singletonList(ChatColor.GRAY + "Steaky, zlaté mrkve, jablka")));

        inv.setItem(14, createIcon(Material.DIAMOND, ChatColor.AQUA + "" + ChatColor.BOLD + "Suroviny a výměna",
                Collections.singletonList(ChatColor.GRAY + "Železo, zlato, výměna diamantů a emeraldů")));

        inv.setItem(16, createIcon(Material.DIAMOND_PICKAXE, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Nástroje a vzácné věci",
                Collections.singletonList(ChatColor.GRAY + "Diamantová výbava, XP, Elytra")));

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.7f, 1.2f);
    }

    private void openCategory(Player player, String category) {
        List<TradeItem> items = categories.get(category);
        if (items == null) return;

        Inventory inv = Bukkit.createInventory(null, 36, ChatColor.DARK_GREEN + "Obchod » " + category);

        for (int i = 0; i < items.size() && i < 27; i++) {
            TradeItem item = items.get(i);
            String currencyName = (item.currency == Material.DIAMOND) ? ChatColor.AQUA + "Diamantů" : ChatColor.GREEN + "Emeraldů";

            ItemStack display = new ItemStack(item.reward, Math.min(64, item.rewardAmount));
            ItemMeta meta = display.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + item.displayName);
                meta.setLore(Arrays.asList(
                        ChatColor.GRAY + "Cena: " + ChatColor.WHITE + item.cost + "x " + currencyName,
                        "",
                        ChatColor.GREEN + "▸ Klikni pro nákup"
                ));
                display.setItemMeta(meta);
            }
            inv.setItem(i, display);
        }

        inv.setItem(31, createIcon(Material.ARROW, ChatColor.RED + "« Zpět", Collections.emptyList()));
        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.7f, 1.2f);
    }

    private ItemStack createIcon(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();

        if (title.equals(MAIN_TITLE)) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            if (slot == 10) openCategory(player, "Bloky");
            else if (slot == 12) openCategory(player, "Jídlo");
            else if (slot == 14) openCategory(player, "Suroviny");
            else if (slot == 16) openCategory(player, "Výbava");
            return;
        }

        String prefix = ChatColor.DARK_GREEN + "Obchod » ";
        if (title.startsWith(prefix)) {
            event.setCancelled(true);
            int slot = event.getRawSlot();

            if (slot == 31) {
                openMainMenu(player);
                return;
            }

            String cat = title.substring(prefix.length());
            List<TradeItem> items = categories.get(cat);
            if (items == null || slot < 0 || slot >= items.size()) return;

            TradeItem trade = items.get(slot);

            if (!player.getInventory().containsAtLeast(new ItemStack(trade.currency), trade.cost)) {
                String chybí = (trade.currency == Material.DIAMOND) ? "diamanty" : "emeraldy";
                player.sendMessage(ChatColor.RED + "Nemáš u sebe dostatek surovin (" + trade.cost + "x " + chybí + ")!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                return;
            }

            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(ChatColor.RED + "Máš plný inventář!");
                return;
            }

            player.getInventory().removeItem(new ItemStack(trade.currency, trade.cost));
            player.getInventory().addItem(new ItemStack(trade.reward, trade.rewardAmount));
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.3f);
            player.sendMessage(ChatColor.GREEN + "Koupil jsi " + trade.displayName + "!");
        }
    }
}
