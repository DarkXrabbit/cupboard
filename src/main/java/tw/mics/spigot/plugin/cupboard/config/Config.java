package tw.mics.spigot.plugin.cupboard.config;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import tw.mics.spigot.plugin.cupboard.Cupboard;
import tw.mics.spigot.plugin.cupboard.utils.Util;

public enum Config {

	DEBUG("debug", false, "is plugin show debug message?"),
	CUPBOARD_PROTECT_DIST("cupboard.protect_dist", 9, "this is cupboard protect area size (ex 9 is 9+9+1 -> 19*19*19)"),
    CUPBOARD_BETWEEN_DIST("cupboard.between_dist", 18, "this is how many block between cupboard can put another cupboard"),
	ANTI_TNT_EXPLOSION("cupboard.anti-tnt-explosion", false, "is cupboard protect explosion from TNT?"),
	ANTI_OTHERS_EXPLOSION("cupboard.anti-creeper-explosion", true, "is cupboard protect explosion from CREEPER?"),
    OP_BYPASS("cupboard.is-op-creative-bypass", true, "is OP user can bypass block protect when in creative mode?"),
    ENABLE_WORLD("cupboard.enable-world", new String[]{
            "world"
    }, "witch world is enable cupboard?"),

	TNT_SP_ENABLE("tnt.enable", true, "let TNT event handle by plugin."),
    TNT_EXPLOSION_RADIUS("tnt.radius", 1, "TNT power radius (int only)"),
    TNT_BREAKCHANCE("tnt.breakchance", new String[]{
            "BARRIER:0:AIR",
            "BEDROCK:0:AIR",
            "STRUCTURE_BLOCK:0:AIR",
            "STRUCTURE_VOID:0:AIR",
            "COMMAND:0:AIR",
            "COMMAND_REPEATING:0:AIR",
            "COMMAND_CHAIN:0:AIR",
            "ENDER_PORTAL:0:AIR",
            "ENDER_PORTAL_FRAME:0:AIR",
            "END_GATEWAY:0:AIR",
            "GOLD_BLOCK:0:AIR",
            "ANVIL:0.25:AIR",
            "ENCHANTMENT_TABLE:0.25:AIR",
            "ENDER_CHEST:0.25:AIR",
            "CHEST:0.5:DROP",
            "TRAPPED_CHEST:0.5:DROP",
            "OBSIDIAN:0.5:COBBLESTONE"
    }, "set block destory chance Material:chance:turn_to_Material (turn_to_Material set to \"DROP\" will destory like normal TNT)"),
    TNT_FUSETICK("tnt.fusetick", 100, "TNT Fuse tick (int only)"),
    
	LOCALE("locale", "en-EN", "language file name");
	
	private final Object value;
	private final String path;
	private final String description;
	private static YamlConfiguration cfg;
	private static final File f = new File(Cupboard.getInstance().getDataFolder(), "config.yml");
	
	private Config(String path, Object val, String description) {
	    this.path = path;
	    this.value = val;
	    this.description = description;
	}
	
	public String getPath() {
	    return path;
	}
	
	public String getDescription() {
	    return description;
	}
	
	public Object getDefaultValue() {
	    return value;
	}

	public boolean getBoolean() {
	    return cfg.getBoolean(path);
	}
	
	public int getInt() {
	    return cfg.getInt(path);
	}
	
	public double getDouble() {
	    return cfg.getDouble(path);
	}
	
	public String getString() {
	    return Util.replaceColors(cfg.getString(path));
	}
	
	public List<String> getStringList() {
	    return cfg.getStringList(path);
	}
	
	public static void load() {
		boolean save_flag = false;
		
        Cupboard.getInstance().getDataFolder().mkdirs();
        String header = "";
		cfg = YamlConfiguration.loadConfiguration(f);

        for (Config c : values()) {
            if(c.getDescription().toLowerCase().equals("removed")){
                if(cfg.contains(c.getPath())){
                    save_flag = true;
                    cfg.set(c.getPath(), null);
                }
                continue;
            }
        	header += c.getPath() + ": " + c.getDescription() + System.lineSeparator();
            if (!cfg.contains(c.getPath())) {
            	save_flag = true;
                c.set(c.getDefaultValue(), false);
            }
        }
        cfg.options().header(header);
        
        if(save_flag){
        	save();
    		cfg = YamlConfiguration.loadConfiguration(f);
        }
	}
	
	public static void save(){
		try {
			cfg.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void set(Object value, boolean save) {
	    cfg.set(path, value);
	    if (save) {
            save();
	    }
	}
}
