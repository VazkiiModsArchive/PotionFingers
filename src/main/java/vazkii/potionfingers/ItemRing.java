package vazkii.potionfingers;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.interf.IItemColorProvider;
import vazkii.arl.item.ItemMod;
import vazkii.arl.util.ItemNBTHelper;

public class ItemRing extends ItemMod implements IBauble, IItemColorProvider {

	private static final String TAG_POTION_EFFECT = "effect";
	
	private static final String[] VARIANTS = new String[] {
		"ring_disabled",
		"ring_enabled"
	};
	
	public ItemRing() {
		super("ring", VARIANTS);
		setCreativeTab(CreativeTabs.BREWING);
		setMaxStackSize(1);
		setHasSubtypes(true);
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
		return getPotion(stack) != null;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String name = super.getItemStackDisplayName(stack);
		Potion p = getPotion(stack);
		String potionName = "N/A";
		if(p != null)
			potionName = I18n.translateToLocal(p.getName());
		
		return String.format(name, potionName);
	}
	
	public static ItemStack getRingForPotion(Potion potion) {
		String id = potion.getRegistryName().toString();
		ItemStack stack = new ItemStack(PotionFingers.ring, 1, 1);
		ItemNBTHelper.setString(stack, TAG_POTION_EFFECT, id);
		return stack;
	}
	
	public static Potion getPotion(ItemStack stack) {
		if(stack == null)
			return null;
		
		String effect = ItemNBTHelper.getString(stack, TAG_POTION_EFFECT, "");
		if(effect.isEmpty())
			return null;
		
		return Potion.REGISTRY.getObject(new ResourceLocation(effect));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IItemColor getItemColor() {
		return new IItemColor() {
			public int getColorFromItemstack(ItemStack stack, int i) {
				if(i != 0) {
					Potion p = getPotion(stack);
					if(p != null)
						return p.getLiquidColor();
				}
				
				return 0xFFFFFF;
			}
		};
	}

	@Override
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
		updatePotionStatus(player, itemstack, false);
	}
	
	@Override
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
		updatePotionStatus(player, itemstack, true);
	}
	
	public void updatePotionStatus(EntityLivingBase player, ItemStack ring, boolean unequipping) {
	    Potion potion = getPotion(ring);
		if(potion == null || !(player instanceof EntityPlayer))
			return;
		
		IInventory inv = BaublesApi.getBaubles((EntityPlayer) player);
		ItemStack ring1 = inv.getStackInSlot(1);
		ItemStack ring2 = inv.getStackInSlot(2);
		
		Potion potion1 = unequipping && ring == ring1 ? null : getPotion(ring1);
		Potion potion2 = unequipping && ring == ring2 ? null : getPotion(ring2);
		
		int level = -1;
		if(potion1 == potion)
			level++;
		if(potion2 == potion)
			level++;
		
		PotionEffect currentEffect = player.getActivePotionEffect(potion);
		int currentLevel = currentEffect != null ? currentEffect.getAmplifier() : -1;
		if(currentLevel != level) {
			player.removeActivePotionEffect(potion);
			if(level != -1 && !player.world.isRemote)
				player.addPotionEffect(new PotionEffect(potion, Integer.MAX_VALUE, level, true, false));
		}
	}
	
	@Override
	public BaubleType getBaubleType(ItemStack arg0) {
		return BaubleType.RING;
	}

}
