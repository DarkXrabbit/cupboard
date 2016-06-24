package com.mics.spigotPlugin.cupboard.schedule;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import com.mics.spigotPlugin.cupboard.Cupboard;
import com.mics.spigotPlugin.cupboard.config.Config;
import com.mics.spigotPlugin.cupboard.entity.FallingPackageEntity;
import com.mics.spigotPlugin.cupboard.utils.Util;

public class AirDrop {
	Cupboard plugin;
	Runnable runnable;
	int schedule_id;
	int drop_time_count;
	Location airdrop_location;
	
	public AirDrop(Cupboard i){
		this.plugin = i;
		drop_time_count = getNextDropRandomTime();
		setupRunnable();
		running();
	}
	
	private int getNextDropRandomTime(){
		return new Random().nextInt(Config.AIR_DROP_MAX_TIME.getInt() - Config.AIR_DROP_MIN_TIME.getInt()) + Config.AIR_DROP_MIN_TIME.getInt();
	}
	
	private void everyMin(){
		drop_time_count--;
		if(drop_time_count == Config.AIR_NOTIFY_TIME.getInt()){
			airdrop_location = Util.getRandomLocation(plugin.getServer().getWorld("world"));
			if ( playerMoreThanMinPlayer() ) {
				airdropNotify();
			} else {
				drop_time_count = getNextDropRandomTime();
				plugin.logDebug("Airdrop will not drop, becouse player is less than min player");
			}
		} else if(drop_time_count <= 0){
			if( playerMoreThanMinPlayer() ){
				broadcast("空投物資已經投放。");
				airdrop(airdrop_location);
			} else {
				broadcast("因為玩家過少，空投物資已經取消。");
			}
			drop_time_count = getNextDropRandomTime();
		}
	}
	

	private boolean playerMoreThanMinPlayer(){
		return plugin.getServer().getOnlinePlayers().size() >= Config.AIR_MIN_PLAYER.getInt();
	}
	
	private void airdropNotify() {
		String str = String.format("空投物資即將在 %d 分鐘後投放在 x:%d z:%d 附近", drop_time_count, airdrop_location.getBlockX(), airdrop_location.getBlockZ());
		broadcast(str);
	}
	
	private void broadcast(String str){
		for (Player p : plugin.getServer().getOnlinePlayers()){
			p.sendMessage(str);
		}
	}
	
	//TODO this is have same one at Airdropcommand
	private void airdrop(Location l){
		Location drop_loc = l.clone();
		
		drop_loc.setY(l.getWorld().getMaxHeight());
		new FallingPackageEntity(drop_loc, Material.NOTE_BLOCK, Config.AIR_DROP_OFFSET.getInt());
	}
	
	
	private void running(){
		schedule_id = this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, runnable, 1200); //have to change 1200
	}
	
	private void setupRunnable(){
		runnable = new Runnable(){
			public void run() {
				everyMin();
				running();
			}
		};
	}
}
