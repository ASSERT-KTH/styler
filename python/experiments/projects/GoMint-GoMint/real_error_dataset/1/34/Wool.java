package io.gomint.server.world.block;

import io.gomint.inventory.item.ItemShears;
import io.gomint.inventory.item.ItemStack;
import io.gomint.server.registry.RegisterInfo;
import io.gomint.server.world.block.state.EnumBlockState;
import io.gomint.world.block.BlockType;
import io.gomint.world.block.data.BlockColor;

/**
 * @author geNAZt
 * @version 1.0
 */
@RegisterInfo( sId = "minecraft:wool" )
public class Wool extends Block implements io.gomint.world.block.BlockWool {

    private final EnumBlockState<BlockColor, String> color = new EnumBlockState<>( this, v -> new String[]{"color"}, BlockColor.values(), e -> e.name().toLowerCase(), v -> BlockColor.valueOf(v.toUpperCase()) );

    @Override
    public String getBlockId() {
        return "minecraft:wool";
    }

    @Override
    public long getBreakTime() {
        return 1200;
    }

    @Override
    public float getBlastResistance() {
        return 4.0f;
    }

    @Override
    public BlockType getType() {
        return BlockType.WOOL;
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

    @Override
    public Class<? extends ItemStack>[] getToolInterfaces() {
        return new Class[]{
            ItemShears.class
        };
    }

}
