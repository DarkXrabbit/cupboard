package com.mics.spigotPlugin.cupboard;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
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
    			Util.msgToPlayer(p, "�Z����L�u���d�Ӫ�C");
    			event.setCancelled(true);
    			return;
    		}
    		Util.msgToPlayer(p, "�u���d�w�g��m�è��o���v�C");
    	}
    }

    
    //���v/�������v
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onRightClick(PlayerInteractEvent event){
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;                    		// off hand packet, ignore.
    	if (event.getClickedBlock().getType() != Material.GOLD_BLOCK) return;       	// �D�����j�h�L��
    	if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return; 						// �D�k�����h�L��
        if (event.getItem() != null && event.getItem().getType().isBlock()) return;   	// �D�Ť�h�L��
    	
		Player p = event.getPlayer();
		String str;
		if(!data.checkCupboardExist(event.getClickedBlock())){
			str="������ëD�Ѫ��a��m�θ�ƿ򥢡A�Щ�᭫�s��m";
		}else if(data.toggleBoardAccess(p, event.getClickedBlock())){
			str="�u���d�w���v";
		} else {
			str="�u���d�w�������v";
		}
		
    	GameMode p_gamemode = p.getGameMode();
		boolean limit = data.checkIsLimit(p.getLocation(), p);
    	
    	if(p_gamemode == GameMode.SURVIVAL && limit){
    		p.setGameMode(GameMode.ADVENTURE);
    	}

    	if(p_gamemode == GameMode.ADVENTURE && !limit){
    		p.setGameMode(GameMode.SURVIVAL);
    	}
		Util.msgToPlayer(p, str);
    }
    
    //==========�H�U���O�@���I=========
    
    //�i�J�d�򤺤����a�|�]�w���_�I�Ҧ�
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent e){
    	for(int i=0; i<2000; i++){
    		//TODO maybe need improve speed
	    	Player p = e.getPlayer();
	    	GameMode p_gamemode = p.getGameMode();
	    	boolean limit = data.checkIsLimit(p.getLocation(), p);
	    	
	    	if(p_gamemode == GameMode.SURVIVAL && limit){
	    		p.setGameMode(GameMode.ADVENTURE);
	    	}
	
	    	if(p_gamemode == GameMode.ADVENTURE && !limit){
	    		p.setGameMode(GameMode.SURVIVAL);
	    	}
    	}
    	
    }
    
  //�T��ϥΥۻs�}�� �H�� �ۻs��O
    @EventHandler
    public void onRightClickDoor(PlayerInteractEvent event){
    	Block b = event.getClickedBlock();
    	Player p = event.getPlayer();
    	if ((
    			event.getAction() == Action.RIGHT_CLICK_BLOCK && 
				b.getType() == Material.STONE_BUTTON
			) || (
    			event.getAction() == Action.PHYSICAL &&
    			b.getType() == Material.STONE_PLATE
			))
    			if(data.checkIsLimit(b, p)) event.setCancelled(true);
    	
    }
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
    
    //�������Q�N�a
    // TODO ���K���|����
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    void onBlockBurnDamage(BlockBurnEvent e){
    	Block b = e.getBlock();
    	if(data.checkIsLimit(b))
    		e.setCancelled(true);
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
