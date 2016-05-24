package com.mics.spigotPlugin.cupboard.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.mics.spigotPlugin.cupboard.Cupboard;
import com.mics.spigotPlugin.cupboard.Data;

public class CupboardUseProtectListener implements Listener {
	private Cupboard plugin;
	public Data data;

	public CupboardUseProtectListener(Cupboard instance)
	{
	    this.plugin = instance;
	    this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	    this.plugin.logDebug("CupboardUseProtectListener Registed.");
	    this.data = this.plugin.data;
	}
	
	

    //�T��ϥΥۻs�}�� �H�� �ۻs��O
      @EventHandler
      public void onUseStoneButton(PlayerInteractEvent event){
      	Block b = event.getClickedBlock();
      	Player p = event.getPlayer();
      	if (
      			event.getAction() == Action.RIGHT_CLICK_BLOCK && 
  				b.getType() == Material.STONE_BUTTON
		) 
      		if(data.checkIsLimit(b, p)){
    			if(this.plugin.isOP(p)) return;
      			event.setCancelled(true);
      		}
      	
      }
      
      //�T��a�ϥΥۻs��O
        @EventHandler
        public void onUseStonePlate(PlayerInteractEvent event){
        	Block b = event.getClickedBlock();
        	Player p = event.getPlayer();
        	if (
        			event.getAction() == Action.PHYSICAL &&
        			b.getType() == Material.STONE_PLATE
			){
        		if(data.checkIsLimit(b, p)){
        			if(this.plugin.isOP(p)) return;
        			event.setCancelled(true);
        		}
        	}
        }

    //�T��ʪ�/�Ǫ��ϥΥۻs��O (���a�M�b�ʪ��W�h�W�[���a�v���P�_
    @EventHandler
    public void onEntryUseStonePlate(EntityInteractEvent event){
    	Block b = event.getBlock();
    	if( b.getType() == Material.STONE_PLATE ){
        	Entity e = event.getEntity();
        	if (e.getPassenger() instanceof Player){
        		Player p = (Player) e.getPassenger();
        		if(data.checkIsLimit(b, p)){
        			if(this.plugin.isOP(p)) return;
        			event.setCancelled(true);
        		}
        	} else {
        		if(data.checkIsLimit(b)) event.setCancelled(true);
        	}
    	}
    }
}