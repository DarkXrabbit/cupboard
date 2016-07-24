package tw.mics.spigot.plugin.cupboard.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import tw.mics.spigot.plugin.cupboard.Cupboard;
import tw.mics.spigot.plugin.cupboard.utils.Util;

public enum Locales {

	OP_BYPASS("gold-block.op-bypass", "&cWARNING: YOU ARE BYPASS PROTECT AREA."),
	GOLD_PLACE("gold-block.place-gold-block", "Block of Gold is placed and got access. (Shift + Right Click can get help)"),
	GOLD_REMOVE("gold-block.remove-gold-block", "Block of Gold is removed."),
	GOLD_TOO_CLOSE("gold-block.gold-block-too-close-to-place", "Block of Gold is too close to place."),
	GOLD_ACCESS_BLOCKED("gold-block.access-blocked-gold-block", "It is blocked, can't access it."),
	GOLD_DATA_NOT_FOUND("gold-block.gold-data-not-found", "This Block of Gold is not place by player or data missing."),
	GOLD_GRANT_ACCESS("gold-block.grant-access", "Grant access."),
	GOLD_REVOKE_ACCESS("gold-block.revoke-access", "Revoke access."),
	NO_ACCESS("gold-block.no-access", "&4No access."),
	SPAWN_WITHOUT_ACCESS("gold-block.spawn-without-access", "&cYour bed is in non-access area, you are spawn in world spawn."),
	
	//WORLD PROTECT
	//DO_NOT_BLOCK_NETHER_DOOR("world-protect.do-not-block-nether-door", "&7Please do not block nether door."),
	NETHER_PORTEL_TELEPORT_BACK("world-protect.nether-portel-teleport-back", "&7已將您傳送回原本位置防止卡門。"),
	
	//WORLD BORDER
	SPAWN_OUTSIDE_BORDER("world-border.spawn-outside-border", "&cYour bed is outside world border, you are spawn in world spawn."),
	
	//TELEPORT
	TELEPORT_FAIL("teleport.teleport-fail", "&4Teleport fail."),
	TELEPORT_NOW("teleport.teleport-now", "Will teleport after 10 sec, please do not move."),
	TELEPORT_NOT_FOUND("teleport.teleport-not-found", "Can't find good place to teleport, please try to close non-protect area."),
	NOT_IN_NO_ACCESS_AREA("teleport.not-in-no-access-area", "You are not in no access area."),
	
	//TNT
	TNT_EXPLOTION_NAME("tnt.explotion-name", "&4Explosion"),
	TNT_EXPLOTION_LORE("tnt.explotion-lore", new String[] {
			"&r&aPlease follow this to craft TNT",
	    	"&r&6E E E   &bE is &4Explosion",
	    	"&r&6E G E   &bG is Block of Gold",
	    	"&r&6E E E"
	}),
	
	TNT_TNT_LORE("tnt.tnt-lore", new String[] {
			"&r&6Put in portect area, will Auto Ignite.",
			"&r&6Can destory protect area.",
			"&r&6Can destory water, lava and obsidian.",
	}),
	
	//HELP
	HELP("Help", new String[] {
		"&aBlock of Gold can protect 19x19x19 area, Block of Gold is at center。",
		"&aIt Protect Following: ",
		"&71. No access player can't place/remove block",
		"&72. No access player can't use stone plate/button",
		"&73. Mobs can't trigger stone plate.",
		"&74. Creeper can't destory block in protect area."
	}),

    //AIRDROP
    AIRDROP_DROPED("airdrop.droped", "空投物資已經投放"),
    AIRDROP_WILL_DROP("airdrop.will_drop", "空投物資即將在 %d 分鐘後投放在 x:%d z:%d 附近"),
    
    //BED
    BED_SPAWN_SET("spawn.spawn-set", "重生點已紀錄。"),
    BED_HAVE_LAVA("spawn.bed-have-lava", "床被放置了岩漿, 回到世界重生點。"),
    BED_WORLD_SPAWN_UPDATED("spawn.world-spawn-updated", "世界的重生點已經更新。"),
    BED_WORLD_SPAWN_UPDATE_TIME("spawn.world-spawn-update-time", "還有 %.0f 秒世界的重生點就會更新。");
	
	
	private final Object value;
	private final String path;
	private static YamlConfiguration cfg;
	private static final File localeFolder = new File(Cupboard.getInstance().getDataFolder().getAbsolutePath() + File.separator + "locales");
	private static final File f = new File(localeFolder, Config.LOCALE.getString() + ".yml");
	
	private Locales(String path, Object val) {
	    this.path = path;
	    this.value = val;
	}
	
	public String getPath() {
	    return path;
	}
	
	public Object getDefaultValue() {
	    return value;
	}

	public String getString() {
	    return Util.replaceColors(cfg.getString(path));
	}
	
	public List<String> getStringList() {
		List<String> strlist = new ArrayList<String>();
		for( String str : cfg.getStringList(path)){
			strlist.add(Util.replaceColors(str));
		}
	    return strlist;
	}
	
	public void send(CommandSender s) {
	    s.sendMessage(getString());
	}
	
	public void send(CommandSender s, Map<String, String> map) {
	    String msg = getString();
	    for (String string : map.keySet()) {
	        msg = msg.replaceAll(string, map.get(string));
	    }
	    s.sendMessage(msg);
	}
	
	public static void load() {
		boolean save_flag = false;
	    localeFolder.mkdirs();
	    if (!f.exists()) {
	    	try{
	    		Cupboard.getInstance().saveResource("locales" + File.separator + Config.LOCALE.getString() + ".yml", true);
	    	} catch (IllegalArgumentException ex) {
	    		Cupboard.getInstance().log("Can't find this locales file, create new file for it");
    	    }
	    } 

		cfg = YamlConfiguration.loadConfiguration(f);

        for (Locales c : values()) {
            if (!cfg.contains(c.getPath())) {
            	save_flag = true;
                c.set(c.getDefaultValue(), false);
            }
        }
        
        if(save_flag){
        	save();
    		cfg = YamlConfiguration.loadConfiguration(f);
        }
	}

	public void set(Object value, boolean save){
	    cfg.set(path, value);
	    if (save) {
            Locales.save();
	    }
	}
	
	public static void save(){
	    localeFolder.mkdirs();
		try {
			cfg.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void set(Object value) throws IOException {
	    this.set(value, true);
	}
	
	public static Locales fromPath(String path) {
	    for (Locales loc : values()) {
	        if (loc.getPath().equalsIgnoreCase(path)) return loc;
	    }
	    return null;
	}
}
