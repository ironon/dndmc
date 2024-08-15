package dnd.craft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;


public class ShrunkenFactory implements EntityType.EntityFactory {
    @Override
    public Entity create(EntityType type, World world) {
        return new Shrunken(type, world);
    }
}
