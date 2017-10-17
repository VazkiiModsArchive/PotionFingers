package vazkii.potionfingers;

import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = PotionFingers.MOD_ID, name = PotionFingers.MOD_NAME, version = PotionFingers.VERSION, dependencies = PotionFingers.DEPENDENCIES)
public class PotionFingers {

	public static final String MOD_ID = "potionfingers";
	public static final String MOD_NAME = "Potion Fingers";
	public static final String BUILD = "GRADLE:BUILD";
	public static final String VERSION = "GRADLE:VERSION-" + BUILD;
	public static final String DEPENDENCIES = "required-before:autoreglib;required-after:baubles";
	
	public static final Potion[] DEFAULT_EFFECTS = {
			MobEffects.SPEED, MobEffects.HASTE,
			MobEffects.RESISTANCE, MobEffects.JUMP_BOOST,	
			MobEffects.STRENGTH, MobEffects.REGENERATION
	};
	
	public static Item ring;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ring = new ItemRing();
	}
	
}
