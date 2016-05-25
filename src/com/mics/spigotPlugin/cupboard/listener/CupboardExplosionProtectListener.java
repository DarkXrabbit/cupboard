package com.mics.spigotPlugin.cupboard.listener;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;

import com.mics.spigotPlugin.cupboard.Cupboard;
import com.mics.spigotPlugin.cupboard.Data;
import com.mics.spigotPlugin.cupboard.utils.Config;

public class CupboardExplosionProtectListener implements Listener {
	private Cupboard plugin;
	public Data data;

	public CupboardExplosionProtectListener(Cupboard instance)
	{
	    this.plugin = instance;
	    this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	    this.plugin.logDebug("CupbloardExplosionProtectListener Registed.");
	    this.data = this.plugin.data;
	}
	
    //TNT or Creeper�z��
    @EventHandler(priority = EventPriority.LOWEST)
    public void onExplode(EntityExplodeEvent event){
    	if(
			(
				Config.ANTI_TNT_EXPLOSION.getBoolean() &&
				event.getEntity().getType().equals(EntityType.PRIMED_TNT)
			) || (
				Config.ANTI_CREEPER_EXPLOSION.getBoolean() &&
				event.getEntity().getType().equals(EntityType.CREEPER)
			)
		){
	    	for (Block block : new ArrayList<Block>(event.blockList())){
	    		if(data.checkIsLimit(block)){
					event.blockList().remove(block);
	    		}
	    	}
    	}
    }
    
    //�����z��
    @EventHandler(priority = EventPriority.LOWEST)
    public void onExplode(BlockExplodeEvent event){
    	for (Block block : new ArrayList<Block>(event.blockList())){
    		if(data.checkIsLimit(block)){
    			event.blockList().remove(block);
    		}
    	}
    }

    //����Armor stand�Q����
    @EventHandler
    public void onArmorStandExplosion(EntityDamageEvent e){
    	//TODO �����z���K�� �n�אּ�i�ѰѼƱ���
    	if(
    		e.getEntity().getType() == EntityType.ARMOR_STAND &&
    		( 
				e.getCause() == DamageCause.BLOCK_EXPLOSION ||
				e.getCause() == DamageCause.ENTITY_EXPLOSION
    		) &&
    		this.plugin.data.checkIsLimit(e.getEntity().getLocation().getBlock())
		){
    		e.setCancelled(true);
    	}
    }
    
    //����Hanging�����~�Q�QCreeper���� / �QTNT����
    @EventHandler
    public void onHangingBreak(HangingBreakByEntityEvent e) {
    	//NEEDFIX -- TNT LIGHT BY ALLOW USER WILL DESTORY HANGING ITEM
    	if(!Config.ANTI_TNT_EXPLOSION.getBoolean())return;
		Location bl = e.getEntity().getLocation().getBlock().getLocation();
    	if (!(e.getRemover() instanceof Player)) {
    		if(e.getCause() == RemoveCause.ENTITY){ //by creeper
    			if(this.plugin.data.checkIsLimit(bl)) e.setCancelled(true);
    		}
    	}
	}
    
    @EventHandler
    public void onHangingBreak(HangingBreakEvent e) {
    	if(!Config.ANTI_CREEPER_EXPLOSION.getBoolean())return;
    	Location bl = e.getEntity().getLocation().getBlock().getLocation();
		if(e.getCause() == RemoveCause.EXPLOSION){ //by TNT
			if(this.plugin.data.checkIsLimit(bl)) e.setCancelled(true);
		}
    }

}
