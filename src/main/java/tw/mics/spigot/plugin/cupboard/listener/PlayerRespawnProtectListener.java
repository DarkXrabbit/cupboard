package tw.mics.spigot.plugin.cupboard.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import tw.mics.spigot.plugin.cupboard.Cupboard;
import tw.mics.spigot.plugin.cupboard.config.Config;
import tw.mics.spigot.plugin.cupboard.config.Locales;
import tw.mics.spigot.plugin.cupboard.utils.SpawnLocationManager;

public class PlayerRespawnProtectListener extends MyListener {
	private Map<String, List<ItemStack>> saveinv;
	public PlayerRespawnProtectListener(Cupboard instance)
	{
		super(instance);
		saveinv = new HashMap<String, List<ItemStack>>();
	}

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
    	Player p = event.getEntity();
    	List<ItemStack> keepInv = new ArrayList<ItemStack>();
    	if(Config.PP_PLAYER_INVENTORY_RECOVERY_PERCENT.getDouble() != 0){
	    	for(ItemStack i: Arrays.asList(event.getEntity().getInventory().getContents())){
	    		if(i == null) continue;
	    		if(new Random().nextDouble() < Config.PP_PLAYER_INVENTORY_RECOVERY_PERCENT.getDouble() ){
	    			keepInv.add(i);
	    			event.getDrops().remove(i);
	    		}
	    	}
	    	saveinv.put(p.getUniqueId().toString(), keepInv);
    	}
    }
    
    @EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event){
		Location l = event.getRespawnLocation();
		Player p = event.getPlayer();
		
		//return your inventory
		if(saveinv.containsKey(p.getUniqueId().toString())){
			for(ItemStack i: saveinv.get(p.getUniqueId().toString())){
				event.getPlayer().getInventory().addItem(i);
			}
			saveinv.remove(p.getUniqueId().toString());
		}
		
		//Spawn location check
		if(event.isBedSpawn()){
			if(this.plugin.cupboards.checkIsLimit(l, p)){
				p.setBedSpawnLocation(null);
				p.sendMessage(Locales.SPAWN_WITHOUT_ACCESS.getString());
			}
		}
		WorldBorder border = l.getWorld().getWorldBorder();
		int max_x = border.getCenter().add(border.getSize()/2, 0, 0).getBlockX();
		int min_x = border.getCenter().add(-border.getSize()/2, 0, 0).getBlockX();
		int max_z = border.getCenter().add(0, 0, border.getSize()/2).getBlockZ();
		int min_z = border.getCenter().add(0, 0, -border.getSize()/2).getBlockZ();
		if(
				l.getBlockX() > max_x ||
				l.getBlockX() < min_x ||
				l.getBlockZ() > max_z ||
				l.getBlockZ() < min_z
			){
			p.setBedSpawnLocation(null);
			p.sendMessage(Locales.SPAWN_OUTSIDE_BORDER.getString());
		}
		
		//spawn lava check
		if(event.isBedSpawn() && event.getPlayer().getBedSpawnLocation() != null){
            if(
                    event.getRespawnLocation().getBlock().getType() == Material.LAVA ||
                    event.getRespawnLocation().getBlock().getType() == Material.STATIONARY_LAVA ||
                    event.getRespawnLocation().clone().add(0,1,0).getBlock().getType() == Material.LAVA ||
                    event.getRespawnLocation().clone().add(0,1,0).getBlock().getType() == Material.STATIONARY_LAVA
            ){
                event.getPlayer().setBedSpawnLocation(null);
                event.getPlayer().sendMessage("床被放置了岩漿，已自動恢復隨機重生。");
            } else {
                return;
            }
        }
		
		//隨機重生
        if(Config.PP_PLAYER_RANDOM_SPAWN_ENABLE.getBoolean()){
            if(SpawnLocationManager.checkNewSpawnLocation()){
                String msg = "世界的重生點已經更新。";
                event.getPlayer().sendMessage(msg);
                for( Player pl : plugin.getServer().getOnlinePlayers() ){
                    if(pl.getBedSpawnLocation() == null)
                        pl.sendMessage(msg);
                }
            } else {
                event.getPlayer().sendMessage(String.format("還有 %.0f 秒世界的重生點就會更新", SpawnLocationManager.getTimeLeft()));
            }
            event.setRespawnLocation(SpawnLocationManager.getSpawnLocation());
        }
	}

}