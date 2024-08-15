package dnd.craft.mixin;

import dnd.craft.BlockDisplayOverview;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.AffineTransformation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DisplayEntity.class)
public abstract class BlockDisplayEntityInvoker implements BlockDisplayOverview {

    @Invoker("setTransformation")
    public abstract void invokeSetTransformation(AffineTransformation trans);

}
