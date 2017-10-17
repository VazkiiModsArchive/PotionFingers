package vazkii.potionfingers;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = PotionFingers.MOD_ID, name = PotionFingers.MOD_NAME, version = PotionFingers.VERSION, dependencies = PotionFingers.DEPENDENCIES)
public class PotionFingers {

	public static final String MOD_ID = "potionfingers";
	public static final String MOD_NAME = "Potion Fingers";
	public static final String BUILD = "GRADLE:BUILD";
	public static final String VERSION = "GRADLE:VERSION-" + BUILD;
	public static final String DEPENDENCIES = "required-before:autoreglib";
	
	public static Item ring;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ring = new ItemRing();
	}
	
}
