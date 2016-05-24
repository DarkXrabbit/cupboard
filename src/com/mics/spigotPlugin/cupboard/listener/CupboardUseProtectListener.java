package com.mics.spigotPlugin.cupboard.listener;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.mics.spigotPlugin.cupboard.Cupboard;
import com.mics.spigotPlugin.cupboard.Data;

public class CupboardUseProtectListener implements Listener {
	private Cupboard plugin;
	ArrayList<Material> vip_protect_block;
	public Data data;

	public CupboardUseProtectListener(Cupboard instance)
	{
	    this.plugin = instance;
	    this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	    this.plugin.logDebug("CupboardUseProtectListener Registed.");
	    this.vip_protect_block = this.plugin.vip_protect_block;
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
    
	//���� VIP �c�l/���l/�|��/�s�Ĥ��˸m�Q�}��
    @EventHandler
    public void onVipBlockUsed(PlayerInteractEvent e){
    	if(!this.plugin.CFG_PROTECT_EVERYTHING_WEHN_OFFLINE) return;
    	if(
			e.getAction() == Action.RIGHT_CLICK_BLOCK &&
			vip_protect_block.contains(e.getClickedBlock().getType())
		){
        	if (this.plugin.data.checkIsLimitOffline(e.getClickedBlock(), e.getPlayer())){
        		if(this.plugin.isOP(e.getPlayer()))return;
        		e.getPlayer().sendMessage("��4�S���v�� ��7(���u�O�@)");
        		e.setCancelled(true);
        	}
    	}
    }
    
    //���� VIP �˳Ƭ[�Q�ϥ�
    @EventHandler
    public void onVipArmorStandUsed(PlayerInteractAtEntityEvent e){
    	if(!this.plugin.CFG_PROTECT_EVERYTHING_WEHN_OFFLINE) return;
    	if(
			e.getRightClicked().getType() == EntityType.ARMOR_STAND
		){
        	if (this.plugin.data.checkIsLimitOffline(e.getRightClicked().getLocation().getBlock(), e.getPlayer())){
        		if(this.plugin.isOP(e.getPlayer()))return;
        		e.getPlayer().sendMessage("��4�S���v�� ��7(���u�O�@)");
        		e.setCancelled(true);
        	}
    	}
    }
    
    //���� VIP ���~�i�ܮسQ��m���~
    @EventHandler
    public void onVipFrameUsed(PlayerInteractEntityEvent e){
    	if(!this.plugin.CFG_PROTECT_EVERYTHING_WEHN_OFFLINE) return;
    	if(
    			e.getRightClicked().getType() == EntityType.ITEM_FRAME
		){
        	if(this.plugin.data.checkIsLimitOffline(e.getRightClicked().getLocation().getBlock(), e.getPlayer())){
        		if(this.plugin.isOP(e.getPlayer()))return;
        		e.getPlayer().sendMessage("��4�S���v�� ��7(���u�O�@)");
        		e.setCancelled(true);
        	}
    	}
    }
    
    //���� VIP ���~�i�ܮسQ�������~
    @EventHandler
    public void onVipFrameRemove(EntityDamageByEntityEvent e){
    	if(!this.plugin.CFG_PROTECT_EVERYTHING_WEHN_OFFLINE) return;
    	if(
			e.getEntity().getType() == EntityType.ITEM_FRAME &&
			e.getDamager() instanceof Player
		){
    		Player p = (Player) e.getDamager();
        	if(this.plugin.data.checkIsLimitOffline(e.getEntity().getLocation().getBlock(), p)){
        		if(this.plugin.isOP(p))return;
        		p.sendMessage("��4�S���v�� ��7(VIP���u�O�@)");
        		e.setCancelled(true);
        	}
    	}
    }

}
