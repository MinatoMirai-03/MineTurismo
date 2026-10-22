package com.examplebett.entity;

import com.examplebett.AiObjectMod;
import com.examplebett.compat.ModRegistries;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class AiObjectModEntities {

    //? if >=26.2 {
    /*public static final EntityType<CarEntity> CAR_ENTITY = EntityType.Builder
            .<CarEntity>of(CarEntity::new, MobCategory.MISC)
            .sized(2.0f, 1.5f)
            .fireImmune()
            .updateInterval(1)
            .build(ResourceKey.create(ModRegistries.ENTITY_TYPE_KEY, AiObjectMod.id("car")));*/
    //?}
    //? if >=1.21.2 && <26.2 {
    public static final EntityType<CarEntity> CAR_ENTITY = FabricEntityTypeBuilder
            .<CarEntity>create(MobCategory.MISC, CarEntity::new)
            .dimensions(EntityDimensions.fixed(2.0f, 1.5f))
            .fireImmune()

            .trackedUpdateRate(1)
            .build(ResourceKey.create(ModRegistries.ENTITY_TYPE_KEY, AiObjectMod.id("car")));
    //?}
    //? if <1.16 {
    /*public static final EntityType<CarEntity> CAR_ENTITY = FabricEntityTypeBuilder
            .<CarEntity>create(MobCategory.MISC, CarEntity::new)
            .dimensions(EntityDimensions.fixed(2.0f, 1.5f))
            .fireImmune()
            .trackedUpdateRate(1)
            .build();*/
    //?}

    //? if >=1.16 && <1.16.2 {
    /*public static final EntityType<CarEntity> CAR_ENTITY = FabricEntityTypeBuilder
            .<CarEntity>create(MobCategory.MISC, CarEntity::new)
            .dimensions(EntityDimensions.fixed(2.0f, 1.5f))
            .fireImmune()
            .trackable(5, 1, true)
            .build();*/
    //?}
    //? if >=1.16.2 && <1.21.2 {
    /*public static final EntityType<CarEntity> CAR_ENTITY = FabricEntityTypeBuilder
            .<CarEntity>create(MobCategory.MISC, CarEntity::new)
            .dimensions(EntityDimensions.fixed(2.0f, 1.5f))
            .fireImmune()
            .trackedUpdateRate(1)
            .build();*/
    //?}

    //? if >=26.2 {
    /*public static final EntityType<BreakableTireStackEntity> BREAKABLE_TIRE_STACK = EntityType.Builder
            .<BreakableTireStackEntity>of(BreakableTireEntity::new, MobCategory.MISC)
            .sized(0.75f, 0.21f)
            .build(ResourceKey.create(ModRegistries.ENTITY_TYPE_KEY, AiObjectMod.id("breakable_tire_stack")));*/
    //?}
    //? if >=1.21.2 && <26.2 {
    public static final EntityType<BreakableTireStackEntity> BREAKABLE_TIRE_STACK = FabricEntityTypeBuilder
            .<BreakableTireStackEntity>create(MobCategory.MISC, BreakableTireEntity::new)
            .dimensions(EntityDimensions.fixed(0.75f, 0.21f))
            .build(ResourceKey.create(ModRegistries.ENTITY_TYPE_KEY, AiObjectMod.id("breakable_tire_stack")));
    //?}
    //? if <1.21.2 {
    /*public static final EntityType<BreakableTireStackEntity> BREAKABLE_TIRE_STACK = FabricEntityTypeBuilder
            .<BreakableTireStackEntity>create(MobCategory.MISC, BreakableTireEntity::new)
            .dimensions(EntityDimensions.fixed(0.75f, 0.21f))
            .build();*/
    //?}

    //? if >=26.2 {
    /*public static final EntityType<BreakableBoxEntity> BREAKABLE_BOX = EntityType.Builder
            .<BreakableBoxEntity>of((type, level) -> new BreakableBoxEntity(type, level, "breakable_cardboard_box"), MobCategory.MISC)
            .sized(1.6f, 0.75f)
            .build(ResourceKey.create(ModRegistries.ENTITY_TYPE_KEY, AiObjectMod.id("breakable_cardboard_box")));*/
    //?}
    //? if >=1.21.2 && <26.2 {
    public static final EntityType<BreakableBoxEntity> BREAKABLE_BOX = FabricEntityTypeBuilder
            .<BreakableBoxEntity>create(MobCategory.MISC,
                    (type, level) -> new BreakableBoxEntity(type, level, "breakable_cardboard_box"))
            .dimensions(EntityDimensions.fixed(1.6f, 0.75f))
            .build(ResourceKey.create(ModRegistries.ENTITY_TYPE_KEY, AiObjectMod.id("breakable_cardboard_box")));
    //?}
    //? if <1.21.2 {
    /*public static final EntityType<BreakableBoxEntity> BREAKABLE_BOX = FabricEntityTypeBuilder
            .<BreakableBoxEntity>create(MobCategory.MISC,
                    (type, level) -> new BreakableBoxEntity(type, level, "breakable_cardboard_box"))
            .dimensions(EntityDimensions.fixed(1.6f, 0.75f))
            .build();*/
    //?}

    //? if >=26.2 {
    /*public static final EntityType<BreakableBoxEntity> BREAKABLE_BOX_2 = EntityType.Builder
            .<BreakableBoxEntity>of((type, level) -> new BreakableBoxEntity(type, level, "breakable_cardboard_box_2"), MobCategory.MISC)
            .sized(1.6f, 0.75f)
            .build(ResourceKey.create(ModRegistries.ENTITY_TYPE_KEY, AiObjectMod.id("breakable_cardboard_box_2")));*/
    //?}
    //? if >=1.21.2 && <26.2 {
    public static final EntityType<BreakableBoxEntity> BREAKABLE_BOX_2 = FabricEntityTypeBuilder
            .<BreakableBoxEntity>create(MobCategory.MISC,
                    (type, level) -> new BreakableBoxEntity(type, level, "breakable_cardboard_box_2"))
            .dimensions(EntityDimensions.fixed(1.6f, 0.75f))
            .build(ResourceKey.create(ModRegistries.ENTITY_TYPE_KEY, AiObjectMod.id("breakable_cardboard_box_2")));
    //?}
    //? if <1.21.2 {
    /*public static final EntityType<BreakableBoxEntity> BREAKABLE_BOX_2 = FabricEntityTypeBuilder
            .<BreakableBoxEntity>create(MobCategory.MISC,
                    (type, level) -> new BreakableBoxEntity(type, level, "breakable_cardboard_box_2"))
            .dimensions(EntityDimensions.fixed(1.6f, 0.75f))
            .build();*/
    //?}

    //? if >=26.2 {
    /*public static final EntityType<BreakableConeEntity> TRAFFIC_CONE = EntityType.Builder
            .<BreakableConeEntity>of((type, level) -> new BreakableConeEntity(type, level, "traffic_cone"), MobCategory.MISC)
            .sized(0.5f, 0.85f)
            .build(ResourceKey.create(ModRegistries.ENTITY_TYPE_KEY, AiObjectMod.id("traffic_cone")));*/
    //?}
    //? if >=1.21.2 && <26.2 {
    public static final EntityType<BreakableConeEntity> TRAFFIC_CONE = FabricEntityTypeBuilder
            .<BreakableConeEntity>create(MobCategory.MISC,
                    (type, level) -> new BreakableConeEntity(type, level, "traffic_cone"))
            .dimensions(EntityDimensions.fixed(0.5f, 0.85f))
            .build(ResourceKey.create(ModRegistries.ENTITY_TYPE_KEY, AiObjectMod.id("traffic_cone")));
    //?}
    //? if <1.21.2 {
    /*public static final EntityType<BreakableConeEntity> TRAFFIC_CONE = FabricEntityTypeBuilder
            .<BreakableConeEntity>create(MobCategory.MISC,
                    (type, level) -> new BreakableConeEntity(type, level, "traffic_cone"))
            .dimensions(EntityDimensions.fixed(0.5f, 0.85f))
            .build();*/
    //?}

    //? if >=26.2 {
    /*public static final EntityType<BreakableBarricadeEntity> BARRICADE = EntityType.Builder
            .<BreakableBarricadeEntity>of((type, level) -> new BreakableBarricadeEntity(type, level, "barricade"), MobCategory.MISC)
            .sized(1.2f, 1.05f)
            .build(ResourceKey.create(ModRegistries.ENTITY_TYPE_KEY, AiObjectMod.id("barricade")));*/
    //?}
    //? if >=1.21.2 && <26.2 {
    public static final EntityType<BreakableBarricadeEntity> BARRICADE = FabricEntityTypeBuilder
            .<BreakableBarricadeEntity>create(MobCategory.MISC,
                    (type, level) -> new BreakableBarricadeEntity(type, level, "barricade"))
            .dimensions(EntityDimensions.fixed(1.2f, 1.05f))
            .build(ResourceKey.create(ModRegistries.ENTITY_TYPE_KEY, AiObjectMod.id("barricade")));
    //?}
    //? if <1.21.2 {
    /*public static final EntityType<BreakableBarricadeEntity> BARRICADE = FabricEntityTypeBuilder
            .<BreakableBarricadeEntity>create(MobCategory.MISC,
                    (type, level) -> new BreakableBarricadeEntity(type, level, "barricade"))
            .dimensions(EntityDimensions.fixed(1.2f, 1.05f))
            .build();*/
    //?}

    public static void register() {
        Registry.register(ModRegistries.ENTITY_TYPE, AiObjectMod.id("car"), CAR_ENTITY);

        //? if >=1.16 {
        FabricDefaultAttributeRegistry.register(CAR_ENTITY, CarEntity.createAttributes());
        //?}
        Registry.register(ModRegistries.ENTITY_TYPE, AiObjectMod.id("breakable_tire_stack"), BREAKABLE_TIRE_STACK);
        Registry.register(ModRegistries.ENTITY_TYPE, AiObjectMod.id("breakable_cardboard_box"), BREAKABLE_BOX);
        Registry.register(ModRegistries.ENTITY_TYPE, AiObjectMod.id("breakable_cardboard_box_2"), BREAKABLE_BOX_2);
        Registry.register(ModRegistries.ENTITY_TYPE, AiObjectMod.id("traffic_cone"), TRAFFIC_CONE);
        Registry.register(ModRegistries.ENTITY_TYPE, AiObjectMod.id("barricade"), BARRICADE);
    }
}
