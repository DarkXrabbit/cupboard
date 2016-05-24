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
import com.mics.spigotPlugin.cupboard.Util;

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
    		Util.msgToPlayer(p, "���j�w�");
    	}
    }
    
    //��m���j
    @EventHandler(priority = EventPriority.MONITOR)
    public void onGoldBlockPlace(BlockPlaceEvent event){
    	if(event.isCancelled())return;
    	if(event.getBlockPlaced().getType() == Material.GOLD_BLOCK){
    		Player p = event.getPlayer();
    		if(!plugin.data.putCupboard(event.getBlockPlaced(), p)){
    			Util.msgToPlayer(p, "�Z����L���j�Ӫ�C");
    			event.setCancelled(true);
    			return;
    		}
    		Util.msgToPlayer(p, "���j�w��m�è��o���v�C(���k����o����)");
    		//Util.msgToPlayer(p, "*** �K�ߴ���: ���j�ä�����Q������ʡC ***");
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
    		p.sendMessage("�Q�צ�F�A�L�k���v/�������v�C");
    		return;
    	}
    	
		if (p.isSneaking()){
			//����
			p.sendMessage("��a���j��m�Უ��19x19x19����ΫO�@�ϡA���j�b�������C");
			p.sendMessage("��a�O�@�ϥu����U�C�ƶ�: ");
			p.sendMessage("��71. �����v�̵L�k��m/��������C");
			p.sendMessage("��72. �����v�̵L�k�ϥΥۻs���O��/�ۻs���s�C");
			p.sendMessage("��73. �Ǫ�/�ʪ��L�kĲ�o�ۻs���O��");
			p.sendMessage("��74. �����������z�������O�@�ϡC");
			p.sendMessage("��75. ������j�Q������ʡC");
			p.sendMessage("��c�S�Oĵ�i�H�U���i����ƥ�: ");
			p.sendMessage("��72. �ϥΤ�s��/���s/���O��/�c�l/�����/����/�]���");
			p.sendMessage("��73. ��������ϤW��|�������������");
			return;
		}
		String str;
		if(!plugin.data.checkCupboardExist(event.getClickedBlock())){
			str="�����j�D�Ѫ��a��m�θ�ƿ򥢡A�Щ�᭫�s��m";
		} else if(plugin.data.toggleBoardAccess(p, event.getClickedBlock())){
			str="�w���v";
		} else {
			str="�w�������v";
		}
		event.setCancelled(true);
		p.updateInventory();
		Util.msgToPlayer(p, str);
    }
}
