package com.examplebett.block;

import com.examplebett.AiObjectMod;
import com.examplebett.compat.ModRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
//? if >=1.20 {
import net.minecraft.world.level.material.MapColor;
//?} else
/*import net.minecraft.world.level.material.MaterialColor;*/

public class AiObjectModBlocks {

    private static BlockBehaviour.Properties withId(BlockBehaviour.Properties properties, ResourceKey<Block> key) {
        //? if >=1.21.2 {
        return properties.setId(key);
        //?} else
        /*return properties;*/
    }

    public static final ResourceKey<Block> CAR_BREAKABLE_GLASS_KEY =
            ResourceKey.create(ModRegistries.BLOCK_KEY, AiObjectMod.id("car_breakable_glass"));

    //? if >=1.20.3 {
    public static final Block CAR_BREAKABLE_GLASS =
            new Block(withId(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS), CAR_BREAKABLE_GLASS_KEY));
    //?} else
    /*public static final Block CAR_BREAKABLE_GLASS =
            new Block(withId(BlockBehaviour.Properties.copy(Blocks.GLASS), CAR_BREAKABLE_GLASS_KEY));*/

    public static final ResourceKey<Block> WATER_PUDDLE_KEY =
            ResourceKey.create(ModRegistries.BLOCK_KEY, AiObjectMod.id("water_puddle"));

    //? if >=1.19 {
    //? if >=1.20 {
    //? if >=1.21.9 {
    public static final Block WATER_PUDDLE = new Block(withId(BlockBehaviour.Properties.of()
            .mapColor(MapColor.WATER).replaceable().noCollision().noOcclusion().strength(0.1F)
            .sound(SoundType.MUD), WATER_PUDDLE_KEY));
    //?} else {
    /*public static final Block WATER_PUDDLE = new Block(withId(BlockBehaviour.Properties.of()
            .mapColor(MapColor.WATER).replaceable().noCollission().noOcclusion().strength(0.1F)
            .sound(SoundType.MUD), WATER_PUDDLE_KEY));*/
    //?}
    //?} else {
    /*public static final Block WATER_PUDDLE = new Block(withId(BlockBehaviour.Properties.of(net.minecraft.world.level.material.Material.WATER, MaterialColor.WATER)
            .noCollission().noOcclusion().strength(0.1F)
            .sound(SoundType.MUD), WATER_PUDDLE_KEY));*/
    //?}
    //?} else {
    /*public static final Block WATER_PUDDLE = new Block(withId(BlockBehaviour.Properties.of(net.minecraft.world.level.material.Material.WATER, MaterialColor.WATER)
            .noCollission().noOcclusion().strength(0.1F)
            .sound(SoundType.SLIME_BLOCK), WATER_PUDDLE_KEY));*/
    //?}

    public static void register() {
        ModRegistries.register(ModRegistries.BLOCK, CAR_BREAKABLE_GLASS_KEY, CAR_BREAKABLE_GLASS);
        ModRegistries.register(ModRegistries.BLOCK, WATER_PUDDLE_KEY, WATER_PUDDLE);
    }

    private AiObjectModBlocks() {
    }
}
