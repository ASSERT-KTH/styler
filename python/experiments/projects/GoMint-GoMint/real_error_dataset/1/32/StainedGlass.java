package io.gomint.server.world.block;

import io.gomint.inventory.item.ItemStack;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.server.world.block.state.EnumBlockState;
import io.gomint.world.block.BlockType;
import io.gomint.world.block.data.BlockColor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( sId = "minecraft:stained_glass" )
public class StainedGlass extends Block implements io.gomint.world.block.BlockStainedGlass {

    private final EnumBlockState<BlockColor, String> color = new EnumBlockState<>( this, v -> new String[]{"color"}, BlockColor.values(), e -> e.name().toLowerCase(), v -> BlockColor.valueOf(v.toUpperCase()) );

    @Override
    public String getBlockId() {
        return "minecraft:stained_glass";
    }

    @Override
    public long getBreakTime() {
        return 450;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public boolean canBeBrokenWithHand() {
        return true;
    }

    @Override
    public List<ItemStack> getDrops( ItemStack itemInHand ) {
        return new ArrayList<>();
    }

    @Override
    public float getBlastResistance() {
        return 1.5f;
    }

    @Override
    public BlockType getType() {
        return BlockType.STAINED_GLASS;
    }

    @Override
    public BlockColor getColor() {
        return this.color.getState();
    }

    @Override
    public void setColor( BlockColor color ) {
        this.color.setState( color );
    }

}
