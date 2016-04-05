package com.mics.spigotPlugin.cupboard;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Cupboard extends JavaPlugin implements Listener {
	Data data;
	@Override
	public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        
        //�B�z��Ʈw
        data = new Data(getDataFolder());
    }
	
    @Override
    public void onDisable() {

    }

    //�������j
    @EventHandler
    public void onGoldBlockBreak(BlockBreakEvent event){
    	if(event.getBlock().getType() == Material.GOLD_BLOCK){
    		//Player p = event.getPlayer();
    		data.removeCupboard(event.getBlock());
    		//Util.msgToPlayer(p, "�w�����u���d");
    	}
    }
    
    //��m���j
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onGoldBlockPlace(BlockPlaceEvent event){
    	if(event.getBlockPlaced().getType() == Material.GOLD_BLOCK){
    		Player p = event.getPlayer();
    		if(!data.putCupboard(event.getBlockPlaced(), p)){
    			//Util.msgToPlayer(p, "�u���d��m����");
    			event.setCancelled(true);
    			return;
    		}
    		//Util.msgToPlayer(p, "�w��m�u���d");
    	}
    }

    
    //���v/�������v
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onRightClick(PlayerInteractEvent event){
    	if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
	    	if(event.getClickedBlock().getType() == Material.GOLD_BLOCK){
	    		Player p = event.getPlayer();
    			String str;
    			if(!data.checkCupboardExist(event.getClickedBlock())){
    				str="������ëD�Ѫ��a��m�θ�ƿ򥢡A�Щ�᭫�s��m";
    			}else if(data.toggleBoardAccess(p, event.getClickedBlock())){
    				str="�u���d�w���v";
    			} else {
    				str="�w�������v";
    			}
	    		Util.msgToPlayer(p, str);
	    	}
    	}
    }
    
    //==========�H�U���O�@���I=========
    
    //�����L���a�}�a���
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e){
    	Player p = e.getPlayer();
    	Block b = e.getBlock();
        if( p != null){
        	if(data.checkIsLimit(b, p))
        		e.setCancelled(true);
        } else {
        	if(data.checkIsLimit(b))
        		e.setCancelled(true);
        }
    }
    //�����L���a��m���
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e){
    	Player p = e.getPlayer();
    	Block b = e.getBlock();
        if( p != null){
        	if(data.checkIsLimit(b, p))
        		e.setCancelled(true);
        } else {
        	if(data.checkIsLimit(b))
        		e.setCancelled(true);
        }
    }
    
    //����a�ϥΤ���
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
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
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
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
    
    //TNT or Creeper�z��
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event){
    	for (Block block : new ArrayList<Block>(event.blockList())){
    		if(data.checkIsLimit(block)){
    			event.blockList().remove(block);
    		}
    	}
    }

    //������K�U�N
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    void onBlockIgnite(BlockIgniteEvent e)
    {
    	Player p = e.getPlayer();
    	Block b = e.getBlock();
        if( p != null){
        	if(data.checkIsLimit(b, p))
        		e.setCancelled(true);
        } else {
        	if(data.checkIsLimit(b))
        		e.setCancelled(true);
        }
    }
    
    //�������Q�N�a
    // TODO ���K���|����
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    void onBlockBurnDamage(BlockBurnEvent e){
    	Block b = e.getBlock();
    	if(data.checkIsLimit(b))
    		e.setCancelled(true);
    }
    
    
}
