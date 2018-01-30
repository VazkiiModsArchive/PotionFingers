package vazkii.potionfingers;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import vazkii.arl.recipe.ModRecipe;

import java.util.ArrayList;
import java.util.List;

public class CombinationRecipe extends ModRecipe {

    public CombinationRecipe() {
        super(new ResourceLocation("ring"));
    }

    @Override
    public boolean matches(InventoryCrafting craftingMaterials, World world) {
        int foundRings = 0;
        boolean foundNetherStar = false;

        for(int i = 0; i < craftingMaterials.getSizeInventory(); i++) {
            ItemStack stack = craftingMaterials.getStackInSlot(i);
            if(!stack.isEmpty()) {
                Item item = stack.getItem();
                if(item instanceof ItemRing && stack.getMetadata() > 0 && foundRings < 2)
                    foundRings++;
                else if(item == Items.NETHER_STAR && !foundNetherStar)
                    foundNetherStar = true;
                else
                    return false;
            }
        }

        return foundNetherStar && foundRings == 2;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting craftingMaterials) {
        List<String> effects = new ArrayList<>();

        for(int i = 0; i < craftingMaterials.getSizeInventory(); i++) {
            ItemStack stack = craftingMaterials.getStackInSlot(i);
            if(!stack.isEmpty() && stack.getItem() != Items.NETHER_STAR)
                effects.addAll(ItemRing.getEffects(stack));
        }

        ItemStack finalRing = ItemRing.getAdvancedPotionRing();
        NBTTagList finalRingEffects = ItemRing.getEmptyNBTEffectList();
        for(String effect : effects) {
            NBTTagCompound effectNbt = new NBTTagCompound();
            effectNbt.setString(ItemRing.TAG_POTION_EFFECT, effect);
            finalRingEffects.appendTag(effectNbt);
        }
        finalRing.setTagInfo(ItemRing.TAG_ADVANCED_POTION_EFFECT, finalRingEffects);

        System.out.println(String.format("Final Ring tag info: %s", finalRingEffects.toString()));

        return finalRing;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemRing.getAdvancedPotionRing();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    }

    @Override
    public boolean canFit(int width, int height) {
        return (width >= 2 && height >= 2);
    }

}
