package io.gomint.server.world.block;

import io.gomint.inventory.item.*;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.server.world.block.state.EnumBlockState;
import io.gomint.world.block.BlockType;
import io.gomint.world.block.data.BlockColor;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( sId = "minecraft:stained_hardened_clay" )
public class StainedHardenedClay extends Block implements io.gomint.world.block.BlockStainedHardenedClay {

    private final EnumBlockState<BlockColor, String> color = new EnumBlockState<>( this, v -> new String[]{"color"}, BlockColor.values(), e -> e.name().toLowerCase(), v -> BlockColor.valueOf(v.toUpperCase()) );

    @Override
    public String getBlockId() {
        return "minecraft:stained_hardened_clay";
    }

    @Override
    public long getBreakTime() {
        return 1875;
    }

    @Override
    public Class<? extends ItemStack>[] getToolInterfaces() {
        return new Class[]{
            ItemWoodenPickaxe.class,
            ItemIronPickaxe.class,
            ItemDiamondPickaxe.class,
            ItemGoldenPickaxe.class,
            ItemStonePickaxe.class
        };
    }

    @Override
    public float getBlastResistance() {
        return 0.75f;
    }

    @Override
    public BlockType getType() {
        return BlockType.STAINED_HARDENED_CLAY;
    }

    @Override
    public BlockColor getColor() {
        return this.color.getState();
    }

    @Override
    public void setColor( BlockColor color ) {
        this.color.setState( color );
    }

    @Override
    public boolean canBeBrokenWithHand() {
        return true;
    }

}
