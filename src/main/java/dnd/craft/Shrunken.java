package dnd.craft;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MarkerEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class Shrunken extends MarkerEntity {

    public Shrunken(EntityType<?> entityType, World world) {
        super(entityType, world);

    }
    public Text getDisplayName() {
        return Text.literal("Shrunken");
    }



}