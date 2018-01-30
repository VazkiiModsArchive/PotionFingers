package vazkii.potionfingers;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.interf.IItemColorProvider;
import vazkii.arl.item.ItemMod;
import vazkii.arl.util.ItemNBTHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemRing extends ItemMod implements IBauble, IItemColorProvider {

	static final String TAG_POTION_EFFECT = "effect";
	static final String TAG_ADVANCED_POTION_EFFECT = "advanced_effect";

	private static final String[] VARIANTS = new String[] {
		"ring_disabled",
		"ring_enabled",
		"ring_advanced"
	};
	
	public ItemRing() {
		super("ring", VARIANTS);
		setCreativeTab(CreativeTabs.BREWING);
		setMaxStackSize(1);
		setHasSubtypes(true);

		new CombinationRecipe();
	}
	
	@Override
	public String getModNamespace() {
		return PotionFingers.MOD_ID;
	}
	
	@Override
	public String getUniqueModel() {
		return "ring";
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if(tab == getCreativeTab()) {
			subItems.add(new ItemStack(this));
			for(Potion p : PotionFingers.DEFAULT_EFFECTS)
				subItems.add(getRingForPotion(p));
		}
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return getEffects(stack).size() > 0;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		if(stack.getMetadata() == 2)
			return super.getItemStackDisplayName(stack);

		String name = super.getItemStackDisplayName(stack);
		Potion p = getPotion(stack, 0);
		String potionName = "N/A";
		if(p != null)
			potionName = I18n.translateToLocal(p.getName());
		
		return String.format(name, potionName);
	}

	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
		if(!ItemRing.isAdvancedRing(stack))
			return;

		List<Potion> potions = ItemRing.getPotions(stack);
		if(potions.isEmpty())
			return;

		Map<String, Integer> potionLevels = new HashMap<>();
		for(Potion p : potions) {
			String name = p.getRegistryName().toString();
			Integer current = potionLevels.getOrDefault(name, 0);
			potionLevels.put(name, current + 1);
		}

		tooltipIfShift(tooltip, () -> potionLevels.forEach((name, level) -> {
			Potion p = Potion.REGISTRY.getObject(new ResourceLocation(name));
			String effectName = I18n.translateToLocalFormatted(p.getName());
			if(level > 1) {
				String levelName = String.format("enchantment.level.%d", level);
				effectName = effectName.concat(String.format(" %s", I18n.translateToLocal(levelName)));
			}
			tooltip.add(effectName);
		}));
	}

	static boolean isAdvancedRing(ItemStack stack) {
		return stack.getMetadata() == 2;
	}

	public static ItemStack getGenericPotionRing() {
		return new ItemStack(PotionFingers.ring, 1, 1);
	}

	public static ItemStack getAdvancedPotionRing() {
		return new ItemStack(PotionFingers.ring, 1, 2);
	}

	public static ItemStack getRingForPotion(Potion potion) {
		String id = potion.getRegistryName().toString();
		ItemStack stack = getGenericPotionRing();
		ItemNBTHelper.setString(stack, TAG_POTION_EFFECT, id);
		return stack;
	}

	static List<String> getEffects(ItemStack stack) {
		List<String> effects = new ArrayList<>();

		if(isAdvancedRing(stack)) {
			NBTTagList effectList = ItemNBTHelper.getList(stack, TAG_ADVANCED_POTION_EFFECT, 10, false);
			for(int j = 0; j < effectList.tagCount(); j++)
				effects.add(effectList.getCompoundTagAt(j).getString(TAG_POTION_EFFECT));
		} else {
			String effect = ItemNBTHelper.getString(stack, TAG_POTION_EFFECT, "");
			if(!effect.isEmpty())
				effects.add(effect);
		}

		return effects;
	}

	static NBTTagList getEmptyNBTEffectList() {
		return ItemNBTHelper.getList(ItemStack.EMPTY, TAG_POTION_EFFECT, 8, false);
	}

	public static List<Potion> getPotions(ItemStack stack) {
		List<Potion> potions = new ArrayList<>();
		List<String> effectList = getEffects(stack);
		for(String effect : effectList)
			potions.add(Potion.REGISTRY.getObject(new ResourceLocation(effect)));

		return potions;
	}

	public static Potion getPotion(ItemStack stack, int idx) {
		if(stack == null)
			return null;
		
		List<String> effectList = getEffects(stack);
		if(effectList.isEmpty() || idx >= effectList.size())
			return null;
		
		return Potion.REGISTRY.getObject(new ResourceLocation(effectList.get(idx)));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IItemColor getItemColor() {
		return new IItemColor() {
			public int getColorFromItemstack(ItemStack stack, int i) {
				if(i != 0) {
					Potion p = getPotion(stack, 0);
					if(p != null)
						return p.getLiquidColor();
				}
				return 0xFFFFFF;
			}
		};
	}

	@Override
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
		int effectCount = getEffects(itemstack).size();
		for(int i = 0; i < effectCount; i++)
			updatePotionStatus(player, getPotion(itemstack, i));
	}
	
	@Override
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
		int effectCount = getEffects(itemstack).size();
		for(int i = 0; i < effectCount; i++)
			updatePotionStatus(player, getPotion(itemstack, i));
	}
	
	public void updatePotionStatus(EntityLivingBase player, Potion potion) {
		if(potion == null || !(player instanceof EntityPlayer))
			return;
		
		IInventory inv = BaublesApi.getBaubles((EntityPlayer) player);
		ItemStack ring1 = inv.getStackInSlot(1);
		ItemStack ring2 = inv.getStackInSlot(2);

		List<String> ring1Effects = getEffects(ring1);
		List<String> ring2Effects = getEffects(ring2);

		List<String> effects = new ArrayList<>();
		effects.addAll(ring1Effects);
		for(String effect : ring2Effects)
			if(!ring1Effects.contains(effect))
				effects.add(effect);

		List<Potion> potions1 = getPotions(ring1);
		List<Potion> potions2 = getPotions(ring2);
		
		int level = -1;
		for(Potion p : potions1)
			if(p == potion)
				level++;
		for(Potion p : potions2)
			if(p == potion)
				level++;

		PotionEffect currentEffect = player.getActivePotionEffect(potion);
		int currentLevel = currentEffect != null ? currentEffect.getAmplifier() : -1;
		if(currentLevel != level) {
			player.removePotionEffect(potion);
			if(level != -1 && !player.world.isRemote)
				player.addPotionEffect(new PotionEffect(potion, Integer.MAX_VALUE, level, true, false));
		}
	}
	
	@Override
	public BaubleType getBaubleType(ItemStack arg0) {
		return BaubleType.RING;
	}

}
