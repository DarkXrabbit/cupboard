package com.mics.spigotPlugin.cupboard.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import com.mics.spigotPlugin.cupboard.Cupboard;
import com.mics.spigotPlugin.cupboard.Data;

public class CupboardBlockProtectListener implements Listener {
	private Cupboard plugin;
	public Data data;

	public CupboardBlockProtectListener(Cupboard instance)
	{
	    this.plugin = instance;
	    this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	    this.plugin.logDebug("CupboardBlockProtectListener Registed.");
	    this.data = this.plugin.data;
	}
    
    
	//�����L���a�}�a���
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent e){
    	Player p = e.getPlayer();
    	Block b = e.getBlock();
        if( p != null){
        	if(data.checkIsLimit(b, p)){
        		if(this.plugin.isOP(p))return;
        		e.setCancelled(true);
    			p.sendMessage("��4�S���v�� ��7(�Q����F? �ո� /esc)");
        	}
        } else {
        	if(data.checkIsLimit(b)){
        		e.setCancelled(true);
        	}
        }
    }
    //�����L���a��m���
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent e){
    	Player p = e.getPlayer();
    	Block b = e.getBlock();
        if( p != null){
        	if(data.checkIsLimit(b, p)){
            	if(this.plugin.isOP(p))return;
        		e.setCancelled(true);
    			p.sendMessage("��4�S���v�� ��7(�Q����F? �ո� /esc)");
        	}
        } else {
        	if(data.checkIsLimit(b))
        		e.setCancelled(true);
        }
    }
    //����a�ϥΤ���
    @EventHandler(priority = EventPriority.HIGH)
    public void onBucketEmpty(PlayerBucketEmptyEvent e){
    	Player p = e.getPlayer();
    	Block b = e.getBlockClicked().getLocation()
    			.add(e.getBlockFace().getModX(),e.getBlockFace().getModY(),e.getBlockFace().getModZ())
    			.getBlock();
        if( p != null){
        	if(data.checkIsLimit(b, p)){
        		e.setCancelled(true);
        		p.updateInventory();
        	}
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onBucketFill(PlayerBucketFillEvent e){
    	Player p = e.getPlayer();
    	Block b = e.getBlockClicked();
        if( p != null){
        	if(data.checkIsLimit(b, p)){
        		e.setCancelled(true);
        		p.updateInventory();
        	}
        }
    }
    
 // �������Q�N�a
    @EventHandler
    void onBlockBurnDamage(BlockBurnEvent e){
    	Block b = e.getBlock();
    	if(data.checkIsLimit(b)){
    		e.setCancelled(true);
    		Location l = e.getBlock().getLocation();
    		if(Math.random() < 0.1){ //���~�n�l�a��10%���v���K����
    			if(l.add(1,0,0).getBlock().getType().equals(Material.FIRE))
    				l.getBlock().setType(Material.AIR);
    			if(l.add(-2,0,0).getBlock().getType().equals(Material.FIRE))
    				l.getBlock().setType(Material.AIR);
    			if(l.add(1,1,0).getBlock().getType().equals(Material.FIRE))
    				l.getBlock().setType(Material.AIR);
    			if(l.add(0,-2,0).getBlock().getType().equals(Material.FIRE))
    				l.getBlock().setType(Material.AIR);
    			if(l.add(0,1,1).getBlock().getType().equals(Material.FIRE))
    				l.getBlock().setType(Material.AIR);
    			if(l.add(0,0,-2).getBlock().getType().equals(Material.FIRE))
    				l.getBlock().setType(Material.AIR);
    		}
    	}
    }

    @EventHandler
    void onFireSpread(BlockSpreadEvent e){
    	if(e.getSource().getType() != Material.FIRE) return;
    	if(data.checkIsLimit(e.getBlock())){
    		e.getSource().setType(Material.AIR);
    	}
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    void onCupboardPiston(BlockPistonExtendEvent e){
    	for(Block block : e.getBlocks()){
    		if(block.getType().equals(Material.GOLD_BLOCK)){
    			e.setCancelled(true);
    		}
    	}
    }

}
