package vazkii.potionfingers;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import vazkii.arl.item.ItemMod;
import vazkii.arl.util.ItemNBTHelper;

public class ItemRing extends ItemMod {

	private static final String TAG_POTION_EFFECT = "effect";
	
	public ItemRing() {
		super("ring");
		setCreativeTab(CreativeTabs.BREWING);
	}
	
	@Override
	public String getModNamespace() {
		return PotionFingers.MOD_ID;
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		super.getSubItems(tab, subItems);
		subItems.add(getRingForPotion(MobEffects.HASTE));
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		Potion potion = getPotion(stack);
		if(potion == null)
			tooltip.add(I18n.translateToLocal("potionfingers.noEffect"));
		else tooltip.add(I18n.translateToLocal(potion.getName()));
	}
	
	public static ItemStack getRingForPotion(Potion potion) {
		String id = potion.getRegistryName().toString();
		ItemStack stack = new ItemStack(PotionFingers.ring);
		ItemNBTHelper.setString(stack, TAG_POTION_EFFECT, id);
		return stack;
	}
	
	public static Potion getPotion(ItemStack stack) {
		String effect = ItemNBTHelper.getString(stack, TAG_POTION_EFFECT, "");
		if(effect.isEmpty())
			return null;
		
		return Potion.REGISTRY.getObject(new ResourceLocation(effect));
	}

}
