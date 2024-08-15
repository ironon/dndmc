package dnd.craft.mixin;

import dnd.craft.BlockDisplayOverview;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.AffineTransformation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(GameMessageS2CPacket.class)
public abstract class TextMixin {

    @Inject(method = "content()Lnet/minecraft/text", at=@At("HEAD"))
    public Text content() {
        return new Te
    }

}