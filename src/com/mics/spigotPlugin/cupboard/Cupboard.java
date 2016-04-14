package com.mics.spigotPlugin.cupboard;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

import com.mics.spigotPlugin.cupboard.command.EscCommand;


public class Cupboard extends JavaPlugin implements Listener {
	public Data data;
	@Override
	public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        
        //�B�z��Ʈw
        data = new Data(getDataFolder());
        
        //�]�w���\Ĳ�o���v�����פ��
        setUpAllowFaceBlock();
        
        //register command
        this.getCommand("esc").setExecutor(new EscCommand(this));
    }
	
    @Override
    public void onDisable() {

    }

    //�������j
    @EventHandler
    public void onGoldBlockBreak(BlockBreakEvent event){
    	if(event.getBlock().getType() == Material.GOLD_BLOCK){
    		Player p = event.getPlayer();
    		data.removeCupboard(event.getBlock());
    		Util.msgToPlayer(p, "���j�w�");
    	}
    }
    
    //��m���j
    @EventHandler(priority = EventPriority.HIGH)
    public void onGoldBlockPlace(BlockPlaceEvent event){
    	if(event.getBlockPlaced().getType() == Material.GOLD_BLOCK){
    		Player p = event.getPlayer();
    		if(!data.putCupboard(event.getBlockPlaced(), p)){
    			Util.msgToPlayer(p, "�Z����L���j�Ӫ�C");
    			event.setCancelled(true);
    			return;
    		}
    		Util.msgToPlayer(p, "���j�w��m�è��o���v�C(�ۤU�k����o����)");
    	}
    }
    ArrayList<Material> allow_face_block;
    private void setUpAllowFaceBlock(){
    	allow_face_block = new ArrayList<Material>();
    	allow_face_block.add(Material.AIR);
    	allow_face_block.add(Material.TORCH);
    	allow_face_block.add(Material.REDSTONE_TORCH_OFF);
    	allow_face_block.add(Material.REDSTONE_TORCH_ON);
    	allow_face_block.add(Material.LONG_GRASS);
    	allow_face_block.add(Material.LEVER);
    	allow_face_block.add(Material.STONE_BUTTON);
    	allow_face_block.add(Material.WOOD_BUTTON);
    	allow_face_block.add(Material.REDSTONE_WIRE);
    }

    
    //���v/�������v
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
    	if(!allow_face_block.contains(front_block_loc.getBlock().getType())){
    		p.sendMessage("�Q�צ�F�A�L�k���v/�������v�C");
    		return;
    	}
    	
		if (p.isSneaking()){
			//����
			p.sendMessage("��a���j��m�Უ��19x19x19����ΫO�@�ϡA���j�b�������C");
			p.sendMessage("��a�O�@�ϥu����U�C�ƶ�: ");
			p.sendMessage("��71. �����v�̵L�k��m/��������C");
			p.sendMessage("��72. �����v�̵L�k�ϥΥۻs���O��/�ۻs���s�C");
			p.sendMessage("��73. �����������z�������O�@�ϡC");
			p.sendMessage("��73. ��������j�Q������ʡC");
			p.sendMessage("��c�S�Oĵ�i�H�U���i����ƥ�: ");
			p.sendMessage("��71. �Ǫ����iĲ�o�ۻs���O��");
			p.sendMessage("��72. �ϥΤ�s��/���s/��O/�c�l/�����/����");
			p.sendMessage("��73. �����U������Ϸ|�������������");
			return;
		}
		String str;
		if(!data.checkCupboardExist(event.getClickedBlock())){
			str="�����j�D�Ѫ��a��m�θ�ƿ򥢡A�Щ�᭫�s��m";
		} else if(data.toggleBoardAccess(p, event.getClickedBlock())){
			str="�w���v";
		} else {
			str="�w�������v";
		}
		event.setCancelled(true);
		Util.msgToPlayer(p, str);
    }
    
    //==========�H�U���O�@���I=========
    
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
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent e){
    	Player p = e.getPlayer();
    	Block b = e.getBlock();
        if( p != null){
        	if(data.checkIsLimit(b, p)){
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
    
    //TNT or Creeper�z��
    @EventHandler(priority = EventPriority.LOWEST)
    public void onExplode(EntityExplodeEvent event){
    	for (Block block : new ArrayList<Block>(event.blockList())){
    		if(data.checkIsLimit(block)){
				event.blockList().remove(block);
    		}
    	}
    }
    
    //�Ҳ��z��
    @EventHandler(priority = EventPriority.LOWEST)
    public void onExplode(BlockExplodeEvent event){
    	for (Block block : new ArrayList<Block>(event.blockList())){
    		if(data.checkIsLimit(block)){
    			event.blockList().remove(block);
    		}
    	}
    }
    
    //�������Q�N�a
    // TODO ���K���|����
    @EventHandler(priority = EventPriority.HIGH)
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
