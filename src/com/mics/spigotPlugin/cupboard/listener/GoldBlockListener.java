package com.mics.spigotPlugin.cupboard.listener;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.mics.spigotPlugin.cupboard.Cupboard;
import com.mics.spigotPlugin.cupboard.utils.Locales;
import com.mics.spigotPlugin.cupboard.utils.Util;

public class GoldBlockListener implements Listener {
	private Cupboard plugin;

	public GoldBlockListener(Cupboard instance)
	{
	    this.plugin = instance;
	    this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	    this.plugin.logDebug("GoldBlockListener Registed.");
	}
	//�������j
    @EventHandler(priority = EventPriority.MONITOR)
    public void onGoldBlockBreak(BlockBreakEvent event){
    	if(event.isCancelled())return;
    	if(event.getBlock().getType() == Material.GOLD_BLOCK){
    		Player p = event.getPlayer();
    		plugin.data.removeCupboard(event.getBlock());
    		Util.msgToPlayer(p, Locales.GOLD_REMOVE.getString());
    	}
    }
    
    //��m���j
    @EventHandler(priority = EventPriority.MONITOR)
    public void onGoldBlockPlace(BlockPlaceEvent event){
    	if(event.isCancelled())return;
    	if(event.getBlockPlaced().getType() == Material.GOLD_BLOCK){
    		Player p = event.getPlayer();
    		if(!plugin.data.putCupboard(event.getBlockPlaced(), p)){
    			Util.msgToPlayer(p,Locales.GOLD_TOO_CLOSE.getString());
    			event.setCancelled(true);
    			return;
    		}
    		Util.msgToPlayer(p, Locales.GOLD_PLACE.getString());
    	}
    }
    
  //�k����j ���v/�������v
    @EventHandler(priority = EventPriority.HIGH)
    public void onRightClick(PlayerInteractEvent event){
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;                    		// off hand packet, ignore.
    	if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.GOLD_BLOCK) return;       	// �D�����j�h�L��
    	if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return; 						// �D�k�����h�L��
        //if (event.getItem() != null && event.getItem().getType().isBlock()) return;   	// �D�Ť�h�L��
    	Location front_block_loc = event.getClickedBlock().getLocation().clone();
		Player p = event.getPlayer();
    	switch(event.getBlockFace()){
    	case EAST:
    		front_block_loc.add(1,0,0);
    		break;
    	case WEST:
    		front_block_loc.add(-1,0,0);
    		break;
    	case SOUTH:
    		front_block_loc.add(0,0,1);
    		break;
    	case NORTH:
    		front_block_loc.add(0,0,-1);
    		break;
    	case UP:
    		front_block_loc.add(0,1,0);
    		break;
    	case DOWN:
    		front_block_loc.add(0,-1,0);
    		break;
		default:
			break;
    	}
    	if(front_block_loc.getBlock().getType().isSolid()){
    		p.sendMessage(Locales.GOLD_ACCESS_BLOCKED.getString());
    		return;
    	}
    	
		if (p.isSneaking()){
			for(String str : Locales.HELP.getStringList()){
				p.sendMessage(str);
			}
			return;
		}
		String str;
		if(!plugin.data.checkCupboardExist(event.getClickedBlock())){
			str=Locales.GOLD_DATA_NOT_FOUND.getString();
		} else if(plugin.data.toggleBoardAccess(p, event.getClickedBlock())){
			str=Locales.GOLD_GRANT_ACCESS.getString();
		} else {
			str=Locales.GOLD_REVOKE_ACCESS.getString();
		}
		event.setCancelled(true);
		p.updateInventory();
		Util.msgToPlayer(p, str);
    }
}
