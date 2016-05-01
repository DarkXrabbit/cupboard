package com.mics.spigotPlugin.cupboard;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.mics.spigotPlugin.cupboard.command.EscCommand;

import net.milkbowl.vault.permission.Permission;


public class Cupboard extends JavaPlugin implements Listener {
	public Data data;
	@Override
	public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        
        //�B�z��Ʈw
        data = new Data(getDataFolder(),this);

        //�]�w���\Ĳ�o���v�����פ��
        setUpAllowFaceBlock();
        //�]�w�O�@��entity
        setUpProtectEntity();
        
        setUpVipProtect();
        
        setupPermissions();
        
        //register command
        this.getCommand("esc").setExecutor(new EscCommand(this));
    }
	
    @Override
    public void onDisable() {

    }

    //�������j
    @EventHandler(priority = EventPriority.MONITOR)
    public void onGoldBlockBreak(BlockBreakEvent event){
    	if(event.isCancelled())return;
    	if(event.getBlock().getType() == Material.GOLD_BLOCK){
    		Player p = event.getPlayer();
    		data.removeCupboard(event.getBlock());
    		Util.msgToPlayer(p, "���j�w�");
    	}
    }
    
    //��m���j
    @EventHandler(priority = EventPriority.MONITOR)
    public void onGoldBlockPlace(BlockPlaceEvent event){
    	if(event.isCancelled())return;
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

    ArrayList<Material> protect_vehicle;
    private void setUpProtectEntity(){
    	protect_vehicle = new ArrayList<Material>();
    	protect_vehicle.add(Material.ARMOR_STAND);
    	protect_vehicle.add(Material.BOAT);
    	protect_vehicle.add(Material.MINECART );
    	protect_vehicle.add(Material.COMMAND_MINECART );
    	protect_vehicle.add(Material.EXPLOSIVE_MINECART );
    	protect_vehicle.add(Material.HOPPER_MINECART );
    	protect_vehicle.add(Material.POWERED_MINECART );
    	protect_vehicle.add(Material.STORAGE_MINECART );
    }
    
    ArrayList<Material> vip_protect_block;
    private void setUpVipProtect(){
    	vip_protect_block = new ArrayList<Material>();
    	vip_protect_block.add(Material.CHEST);
    	vip_protect_block.add(Material.TRAPPED_CHEST);
    	vip_protect_block.add(Material.FURNACE);
    	vip_protect_block.add(Material.BURNING_FURNACE);
    	vip_protect_block.add(Material.JUKEBOX);
    	vip_protect_block.add(Material.BREWING_STAND);
    	vip_protect_block.add(Material.ANVIL);
    	vip_protect_block.add(Material.DROPPER);
    	vip_protect_block.add(Material.DISPENSER);
    	vip_protect_block.add(Material.HOPPER);
    }

    public Permission perms;
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
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
			p.sendMessage("��71. �Ǫ�/�ʪ��L�kĲ�o�ۻs���O��");
			p.sendMessage("��73. �����������z�������O�@�ϡC");
			p.sendMessage("��73. ��������j�Q������ʡC");
			p.sendMessage("��c�S�Oĵ�i�H�U���i����ƥ�: ");
			p.sendMessage("��72. �ϥΤ�s��/���s/���O��/�c�l/�����/����/�]���");
			p.sendMessage("��73. ��������ϤW��|�������������");
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
		p.updateInventory();
		Util.msgToPlayer(p, str);
    }
    
    //==========�H�U���O�@���I=========
    
    
    //���� VIP �c�l/���l/�|��/�s�Ĥ��˸m�Q�}��
    @EventHandler
    public void onVipBlockUsed(PlayerInteractEvent e){
    	if(
			e.getAction() == Action.RIGHT_CLICK_BLOCK &&
			vip_protect_block.contains(e.getClickedBlock().getType())
		){
        	if (data.checkIsLimitOffline(e.getClickedBlock(), e.getPlayer())){
        		if(this.isOP(e.getPlayer()))return;
        		e.getPlayer().sendMessage("��4�S���v�� ��7(VIP���u�O�@)");
        		e.setCancelled(true);
        	}
    	}
    }
    
    //���� VIP �˳Ƭ[�Q�ϥ�
    @EventHandler
    public void onVipArmorStandUsed(PlayerInteractAtEntityEvent e){
    	if(
			e.getRightClicked().getType() == EntityType.ARMOR_STAND
		){
        	if (data.checkIsLimitOffline(e.getRightClicked().getLocation().getBlock(), e.getPlayer())){
        		if(this.isOP(e.getPlayer()))return;
        		e.getPlayer().sendMessage("��4�S���v�� ��7(VIP���u�O�@)");
        		e.setCancelled(true);
        	}
    	}
    }
    
    //���� VIP ���~�i�ܮسQ��m���~
    @EventHandler
    public void onVipFrameUsed(PlayerInteractEntityEvent e){
    	if(
    			e.getRightClicked().getType() == EntityType.ITEM_FRAME
		){
        	if (data.checkIsLimitOffline(e.getRightClicked().getLocation().getBlock(), e.getPlayer())){
        		if(this.isOP(e.getPlayer()))return;
        		e.getPlayer().sendMessage("��4�S���v�� ��7(VIP���u�O�@)");
        		e.setCancelled(true);
        	}
    	}
    }
    
    //���� VIP ���~�i�ܮسQ�������~
    @EventHandler
    public void onVipFrameRemove(EntityDamageByEntityEvent e){
    	if(
			e.getEntity().getType() == EntityType.ITEM_FRAME &&
			e.getDamager() instanceof Player
		){
    		Player p = (Player) e.getDamager();
        	if (data.checkIsLimitOffline(e.getEntity().getLocation().getBlock(), p)){
        		if(this.isOP(p))return;
        		p.sendMessage("��4�S���v�� ��7(VIP���u�O�@)");
        		e.setCancelled(true);
        	}
    	}
    }

    //����/�q��/���Ҭ[�Q��m
    @EventHandler
    public void onBoatPlace(PlayerInteractEvent e){
    	if (
			e.getAction() == Action.RIGHT_CLICK_BLOCK &&
			e.getItem() != null &&
			protect_vehicle.contains(e.getItem().getType())
		){
    	Player p = e.getPlayer();
	    	if (data.checkIsLimit(e.getClickedBlock(), p)){
	    		if(this.isOP(p))return;
	    		e.setCancelled(true);
	    		e.getPlayer().updateInventory();
	    	}
    	}
    }
    
    //����Ҭ[�Q����
    @EventHandler
    public void onArmorStandDamage(EntityDamageByEntityEvent e){
    	if (e.getEntity().getType() != EntityType.ARMOR_STAND) return;
    	if (e.getDamager() instanceof Player){
    		Player p = (Player) e.getDamager();
        	if (data.checkIsLimit(e.getEntity().getLocation().getBlock(), p)){
        		if(this.isOP(p))return;
        		e.setCancelled(true);
        	}
    	}
    }
    
    //����Armor stand�Q����
    @EventHandler
    public void onArmorStandExplosion(EntityDamageEvent e){
    	if(
    		e.getEntity().getType() == EntityType.ARMOR_STAND &&
    		( 
				e.getCause() == DamageCause.BLOCK_EXPLOSION ||
				e.getCause() == DamageCause.ENTITY_EXPLOSION
    		) &&
    		data.checkIsLimit(e.getEntity().getLocation().getBlock())
		){
    		e.setCancelled(true);
    	}
    }
    
    //����Hanging�����~�Q�����v���a���� / �QCreeper���� / �QTNT����
    @EventHandler
    public void onHangingBreak(HangingBreakByEntityEvent e) {
    	//NEEDFIX -- TNT LIGHT BY ALLOW USER WILL DESTORY HANGING ITEM
		Location bl = e.getEntity().getLocation().getBlock().getLocation();
    	if (e.getRemover() instanceof Player){
    		Player p = (Player) e.getRemover();
    		if(data.checkIsLimit(bl, p)){
    			if(this.isOP(p)) return;
    			e.setCancelled(true);
    		}
    	} else {
    		if(e.getCause() == RemoveCause.ENTITY){ //by creeper
    			if(data.checkIsLimit(bl)) e.setCancelled(true);
    		}
    	}
	}
    
    @EventHandler
    public void onHangingBreak(HangingBreakEvent e) {
    	Location bl = e.getEntity().getLocation().getBlock().getLocation();
		if(e.getCause() == RemoveCause.EXPLOSION){ //by TNT
			if(data.checkIsLimit(bl)) e.setCancelled(true);
		}
    }
    

    //����Hanging�����~�Q�����v���a��m
    @EventHandler
    public void onHangingPlace(HangingPlaceEvent e) {
		Location bl = e.getEntity().getLocation().getBlock().getLocation();
		Player p = e.getPlayer();
		if(data.checkIsLimit(bl, p)){
			if(this.isOP(p)) return;
			e.setCancelled(true);
			e.getPlayer().updateInventory();
		}
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
    			if(this.isOP(p)) return;
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
        			if(this.isOP(p)) return;
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
        			if(this.isOP(p)) return;
        			event.setCancelled(true);
        		}
        	} else {
        		if(data.checkIsLimit(b)) event.setCancelled(true);
        	}
    	}
    }
    //�����L���a�}�a���
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent e){
    	Player p = e.getPlayer();
    	Block b = e.getBlock();
        if( p != null){
        	if(data.checkIsLimit(b, p)){
        		if(this.isOP(p))return;
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
            	if(this.isOP(p))return;
        		e.setCancelled(true);
    			p.sendMessage("��4�S���v�� ��7(�Q����F? �ո� /esc)");
        	}
        } else {
        	if(data.checkIsLimit(b))
        		e.setCancelled(true);
        }
    }
    
    //������a���~������z�L�a�����ǰe
    @EventHandler
    public void onEntityPortal(EntityPortalEvent e){
		e.setCancelled(true);
    }
    
    
    //����a�צ�a����
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockNetherDoor(BlockPlaceEvent e){
    	Player p = e.getPlayer();
    	Block b = e.getBlock();
    	if(
    			b.getLocation().add(1,0,0).getBlock().getType() == Material.PORTAL ||
    			b.getLocation().add(-1,0,0).getBlock().getType() == Material.PORTAL ||
    			b.getLocation().add(0,0,1).getBlock().getType() == Material.PORTAL ||
    			b.getLocation().add(0,0,-1).getBlock().getType() == Material.PORTAL
    			){
	        if( p != null && b.getType().isSolid()){
	        	if(b.getType() == Material.WOOD_PLATE) return;
	        	if(b.getType() == Material.STONE_PLATE) return;
	        	if(b.getType() == Material.IRON_PLATE) return;
	        	if(b.getType() == Material.GOLD_PLATE) return;
        		p.sendMessage("��7�ФűN�a��������");
        		e.setCancelled(true);
	    	}
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
    
    // �������Q�N�a
    // TODO ���K���|����
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
    
    boolean isOP(Player p){
    	if(p.isOp() && p.getGameMode() == GameMode.CREATIVE){
    		p.sendMessage("��cĵ�i: �޲z���v���w�����O�@��");
    		return true;
    	}
		return false;
    }
    
    
}
