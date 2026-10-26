//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.item.VehicleItemIcons;
import com.examplebett.network.SetCarItemIconPayload;
import com.examplebett.util.AiCarPool;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CarItemIconScreen extends Screen implements CarCustomizationScreen {

    private static final int CHOICE_WIDTH = 80;
    private static final int CHOICE_GAP = 8;
    private static final int ICON_BOX = 40;

    private final CarEntity car;
    private final Screen parent;
    private final List<Choice> choices = new ArrayList<>();
    private int choicesTop;

    private record Choice(int icon, String labelKey, ItemStack preview, int x) {
    }

    public CarItemIconScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.item_icon.title"));
        this.car = car;
        this.parent = parent;
    }

    private boolean hasVehicleIcon(Item spawnItem) {
        if (spawnItem == null) {
            return false;
        }
        var key = com.examplebett.compat.ModRegistries.ITEM.getKey(spawnItem);

        var texture = new net.minecraft.resources.ResourceLocation(key.getNamespace(), "textures/item/vehicle_icons/" + this.car.getModelId() + ".png");
        return this.minecraft.getResourceManager().hasResource(texture);
    }

    private int currentIcon(boolean vehicleAvailable) {
        int icon = this.car.getItemIcon();
        return icon == CarEntity.ITEM_ICON_VEHICLE && !vehicleAvailable ? CarEntity.ITEM_ICON_KEY : icon;
    }

    private void apply(int icon) {
        this.car.setItemIcon(icon);
        SetCarItemIconPayload mineturismo$payload = new SetCarItemIconPayload(this.car.getId(), icon);
        ModClientNetworking.sendToServer(SetCarItemIconPayload.ID, mineturismo$payload, SetCarItemIconPayload::write);
        this.minecraft.setScreen(new CarItemIconScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        Item spawnItem = AiCarPool.spawnItemFor(this.car.getModelId());
        boolean vehicleAvailable = this.hasVehicleIcon(spawnItem);
        int current = this.currentIcon(vehicleAvailable);

        List<Integer> icons = new ArrayList<>();
        if (vehicleAvailable) {
            icons.add(CarEntity.ITEM_ICON_VEHICLE);
        }
        icons.add(CarEntity.ITEM_ICON_KEY);
        icons.add(CarEntity.ITEM_ICON_BOXED);
        icons.add(CarEntity.ITEM_ICON_SMART_KEY);

        int total = icons.size() * CHOICE_WIDTH + (icons.size() - 1) * CHOICE_GAP;
        int x0 = this.width / 2 - total / 2;
        this.choicesTop = this.height / 2 - 50;
        this.choices.clear();
        for (int i = 0; i < icons.size(); i++) {
            int icon = icons.get(i);
            String labelKey = switch (icon) {
                case CarEntity.ITEM_ICON_VEHICLE -> "gui.mineturismo.item_icon.vehicle";
                case CarEntity.ITEM_ICON_KEY -> "gui.mineturismo.item_icon.key";
                case CarEntity.ITEM_ICON_SMART_KEY -> "gui.mineturismo.item_icon.smart_key";
                default -> "gui.mineturismo.item_icon.boxed";
            };
            ItemStack preview = spawnItem == null ? ItemStack.EMPTY : new ItemStack(spawnItem);
            VehicleItemIcons.applyToSpawnStack(preview, icon);
            int x = x0 + i * (CHOICE_WIDTH + CHOICE_GAP);
            this.choices.add(new Choice(icon, labelKey, preview, x));

            Component label = ModText.translatable(labelKey);
            if (icon == current) {
                label = ModText.literal("> ").append(label);
            }
            this.addRenderableWidget(ModWidgets.button(label, x, this.choicesTop + ICON_BOX + 6, CHOICE_WIDTH, 20, b -> this.apply(icon)));
        }

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), this.width / 2 - 100, this.choicesTop + ICON_BOX + 60, 200, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.choicesTop - 30, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, ModText.translatable("gui.mineturismo.item_icon.desc"), this.width / 2, this.choicesTop - 16, 0xFFAAAAAA);

        Item spawnItem = AiCarPool.spawnItemFor(this.car.getModelId());
        int current = this.currentIcon(this.hasVehicleIcon(spawnItem));
        for (Choice choice : this.choices) {
            int bx = choice.x() + (CHOICE_WIDTH - ICON_BOX) / 2;
            int by = this.choicesTop;
            int frame = choice.icon() == current ? 0xFFFFD24A : 0xFF555555;
            guiGraphics.fill(bx - 1, by - 1, bx + ICON_BOX + 1, by + ICON_BOX + 1, frame);
            guiGraphics.fill(bx, by, bx + ICON_BOX, by + ICON_BOX, 0xC0101010);

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate((float) (bx + 4), (float) (by + 4), 0f);
            guiGraphics.pose().scale(2f, 2f, 1f);
            guiGraphics.renderItem(choice.preview(), 0, 0);
            guiGraphics.pose().popPose();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
//? if <1.17 && >=1.16 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.item.VehicleItemIcons;
import com.examplebett.network.SetCarItemIconPayload;
import com.examplebett.util.AiCarPool;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CarItemIconScreen extends Screen implements CarCustomizationScreen {

    private static final int CHOICE_WIDTH = 80;
    private static final int CHOICE_GAP = 8;
    private static final int ICON_BOX = 40;

    private final CarEntity car;
    private final Screen parent;
    private final List<Choice> choices = new ArrayList<>();
    private int choicesTop;

    private record Choice(int icon, String labelKey, ItemStack preview, int x) {
    }

    public CarItemIconScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.item_icon.title"));
        this.car = car;
        this.parent = parent;
    }

    private boolean hasVehicleIcon(Item spawnItem) {
        if (spawnItem == null) {
            return false;
        }
        var key = com.examplebett.compat.ModRegistries.ITEM.getKey(spawnItem);

        var texture = new net.minecraft.resources.ResourceLocation(key.getNamespace(), "textures/item/vehicle_icons/" + this.car.getModelId() + ".png");
        return this.minecraft.getResourceManager().hasResource(texture);
    }

    private int currentIcon(boolean vehicleAvailable) {
        int icon = this.car.getItemIcon();
        return icon == CarEntity.ITEM_ICON_VEHICLE && !vehicleAvailable ? CarEntity.ITEM_ICON_KEY : icon;
    }

    private void apply(int icon) {
        this.car.setItemIcon(icon);
        SetCarItemIconPayload mineturismo$payload = new SetCarItemIconPayload(this.car.getId(), icon);
        ModClientNetworking.sendToServer(SetCarItemIconPayload.ID, mineturismo$payload, SetCarItemIconPayload::write);
        this.minecraft.setScreen(new CarItemIconScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        Item spawnItem = AiCarPool.spawnItemFor(this.car.getModelId());
        boolean vehicleAvailable = this.hasVehicleIcon(spawnItem);
        int current = this.currentIcon(vehicleAvailable);

        List<Integer> icons = new ArrayList<>();
        if (vehicleAvailable) {
            icons.add(CarEntity.ITEM_ICON_VEHICLE);
        }
        icons.add(CarEntity.ITEM_ICON_KEY);
        icons.add(CarEntity.ITEM_ICON_BOXED);
        icons.add(CarEntity.ITEM_ICON_SMART_KEY);

        int total = icons.size() * CHOICE_WIDTH + (icons.size() - 1) * CHOICE_GAP;
        int x0 = this.width / 2 - total / 2;
        this.choicesTop = this.height / 2 - 50;
        this.choices.clear();
        for (int i = 0; i < icons.size(); i++) {
            int icon = icons.get(i);
            String labelKey = switch (icon) {
                case CarEntity.ITEM_ICON_VEHICLE -> "gui.mineturismo.item_icon.vehicle";
                case CarEntity.ITEM_ICON_KEY -> "gui.mineturismo.item_icon.key";
                case CarEntity.ITEM_ICON_SMART_KEY -> "gui.mineturismo.item_icon.smart_key";
                default -> "gui.mineturismo.item_icon.boxed";
            };
            ItemStack preview = spawnItem == null ? ItemStack.EMPTY : new ItemStack(spawnItem);
            VehicleItemIcons.applyToSpawnStack(preview, icon);
            int x = x0 + i * (CHOICE_WIDTH + CHOICE_GAP);
            this.choices.add(new Choice(icon, labelKey, preview, x));

            Component label = ModText.translatable(labelKey);
            if (icon == current) {
                label = ModText.literal("> ").append(label);
            }
            this.addButton(ModWidgets.button(label, x, this.choicesTop + ICON_BOX + 6, CHOICE_WIDTH, 20, b -> this.apply(icon)));
        }

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), this.width / 2 - 100, this.choicesTop + ICON_BOX + 60, 200, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.choicesTop - 30, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, ModText.translatable("gui.mineturismo.item_icon.desc"), this.width / 2, this.choicesTop - 16, 0xFFAAAAAA);

        Item spawnItem = AiCarPool.spawnItemFor(this.car.getModelId());
        int current = this.currentIcon(this.hasVehicleIcon(spawnItem));
        for (Choice choice : this.choices) {
            int bx = choice.x() + (CHOICE_WIDTH - ICON_BOX) / 2;
            int by = this.choicesTop;
            int frame = choice.icon() == current ? 0xFFFFD24A : 0xFF555555;
            guiGraphics.fill(bx - 1, by - 1, bx + ICON_BOX + 1, by + ICON_BOX + 1, frame);
            guiGraphics.fill(bx, by, bx + ICON_BOX, by + ICON_BOX, 0xC0101010);

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate((float) (bx + 4), (float) (by + 4), 0f);
            guiGraphics.pose().scale(2f, 2f, 1f);
            guiGraphics.renderItem(choice.preview(), 0, 0);
            guiGraphics.pose().popPose();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
//? if <1.16 && >=1.15 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.item.VehicleItemIcons;
import com.examplebett.network.SetCarItemIconPayload;
import com.examplebett.util.AiCarPool;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CarItemIconScreen extends Screen implements CarCustomizationScreen {

    private static final int CHOICE_WIDTH = 80;
    private static final int CHOICE_GAP = 8;
    private static final int ICON_BOX = 40;

    private final CarEntity car;
    private final Screen parent;
    private final List<Choice> choices = new ArrayList<>();
    private int choicesTop;

    private record Choice(int icon, String labelKey, ItemStack preview, int x) {
    }

    public CarItemIconScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.item_icon.title"));
        this.car = car;
        this.parent = parent;
    }

    private boolean hasVehicleIcon(Item spawnItem) {
        if (spawnItem == null) {
            return false;
        }
        var key = com.examplebett.compat.ModRegistries.ITEM.getKey(spawnItem);

        var texture = new net.minecraft.resources.ResourceLocation(key.getNamespace(), "textures/item/vehicle_icons/" + this.car.getModelId() + ".png");
        return this.minecraft.getResourceManager().hasResource(texture);
    }

    private int currentIcon(boolean vehicleAvailable) {
        int icon = this.car.getItemIcon();
        return icon == CarEntity.ITEM_ICON_VEHICLE && !vehicleAvailable ? CarEntity.ITEM_ICON_KEY : icon;
    }

    private void apply(int icon) {
        this.car.setItemIcon(icon);
        SetCarItemIconPayload mineturismo$payload = new SetCarItemIconPayload(this.car.getId(), icon);
        ModClientNetworking.sendToServer(SetCarItemIconPayload.ID, mineturismo$payload, SetCarItemIconPayload::write);
        this.minecraft.setScreen(new CarItemIconScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        Item spawnItem = AiCarPool.spawnItemFor(this.car.getModelId());
        boolean vehicleAvailable = this.hasVehicleIcon(spawnItem);
        int current = this.currentIcon(vehicleAvailable);

        List<Integer> icons = new ArrayList<>();
        if (vehicleAvailable) {
            icons.add(CarEntity.ITEM_ICON_VEHICLE);
        }
        icons.add(CarEntity.ITEM_ICON_KEY);
        icons.add(CarEntity.ITEM_ICON_BOXED);
        icons.add(CarEntity.ITEM_ICON_SMART_KEY);

        int total = icons.size() * CHOICE_WIDTH + (icons.size() - 1) * CHOICE_GAP;
        int x0 = this.width / 2 - total / 2;
        this.choicesTop = this.height / 2 - 50;
        this.choices.clear();
        for (int i = 0; i < icons.size(); i++) {
            int icon = icons.get(i);
            String labelKey = switch (icon) {
                case CarEntity.ITEM_ICON_VEHICLE -> "gui.mineturismo.item_icon.vehicle";
                case CarEntity.ITEM_ICON_KEY -> "gui.mineturismo.item_icon.key";
                case CarEntity.ITEM_ICON_SMART_KEY -> "gui.mineturismo.item_icon.smart_key";
                default -> "gui.mineturismo.item_icon.boxed";
            };
            ItemStack preview = spawnItem == null ? ItemStack.EMPTY : new ItemStack(spawnItem);
            VehicleItemIcons.applyToSpawnStack(preview, icon);
            int x = x0 + i * (CHOICE_WIDTH + CHOICE_GAP);
            this.choices.add(new Choice(icon, labelKey, preview, x));

            Component label = ModText.translatable(labelKey);
            if (icon == current) {
                label = ModText.literal("> ").append(label);
            }
            this.addButton(ModWidgets.button(label, x, this.choicesTop + ICON_BOX + 6, CHOICE_WIDTH, 20, b -> this.apply(icon)));
        }

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), this.width / 2 - 100, this.choicesTop + ICON_BOX + 60, 200, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.choicesTop - 30, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, ModText.translatable("gui.mineturismo.item_icon.desc"), this.width / 2, this.choicesTop - 16, 0xFFAAAAAA);

        Item spawnItem = AiCarPool.spawnItemFor(this.car.getModelId());
        int current = this.currentIcon(this.hasVehicleIcon(spawnItem));
        for (Choice choice : this.choices) {
            int bx = choice.x() + (CHOICE_WIDTH - ICON_BOX) / 2;
            int by = this.choicesTop;
            int frame = choice.icon() == current ? 0xFFFFD24A : 0xFF555555;
            guiGraphics.fill(bx - 1, by - 1, bx + ICON_BOX + 1, by + ICON_BOX + 1, frame);
            guiGraphics.fill(bx, by, bx + ICON_BOX, by + ICON_BOX, 0xC0101010);

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate((float) (bx + 4), (float) (by + 4), 0f);
            guiGraphics.pose().scale(2f, 2f, 1f);
            guiGraphics.renderItem(choice.preview(), 0, 0);
            guiGraphics.pose().popPose();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
//? if <1.15 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.item.VehicleItemIcons;
import com.examplebett.network.SetCarItemIconPayload;
import com.examplebett.util.AiCarPool;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CarItemIconScreen extends Screen implements CarCustomizationScreen {

    private static final int CHOICE_WIDTH = 80;
    private static final int CHOICE_GAP = 8;
    private static final int ICON_BOX = 40;

    private final CarEntity car;
    private final Screen parent;
    private final List<Choice> choices = new ArrayList<>();
    private int choicesTop;

    private record Choice(int icon, String labelKey, ItemStack preview, int x) {
    }

    public CarItemIconScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.item_icon.title"));
        this.car = car;
        this.parent = parent;
    }

    private boolean hasVehicleIcon(Item spawnItem) {
        if (spawnItem == null) {
            return false;
        }
        var key = com.examplebett.compat.ModRegistries.ITEM.getKey(spawnItem);

        var texture = new net.minecraft.resources.ResourceLocation(key.getNamespace(), "textures/item/vehicle_icons/" + this.car.getModelId() + ".png");
        return this.minecraft.getResourceManager().hasResource(texture);
    }

    private int currentIcon(boolean vehicleAvailable) {
        int icon = this.car.getItemIcon();
        return icon == CarEntity.ITEM_ICON_VEHICLE && !vehicleAvailable ? CarEntity.ITEM_ICON_KEY : icon;
    }

    private void apply(int icon) {
        this.car.setItemIcon(icon);
        SetCarItemIconPayload mineturismo$payload = new SetCarItemIconPayload(this.car.getId(), icon);
        ModClientNetworking.sendToServer(SetCarItemIconPayload.ID, mineturismo$payload, SetCarItemIconPayload::write);
        this.minecraft.setScreen(new CarItemIconScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        Item spawnItem = AiCarPool.spawnItemFor(this.car.getModelId());
        boolean vehicleAvailable = this.hasVehicleIcon(spawnItem);
        int current = this.currentIcon(vehicleAvailable);

        List<Integer> icons = new ArrayList<>();
        if (vehicleAvailable) {
            icons.add(CarEntity.ITEM_ICON_VEHICLE);
        }
        icons.add(CarEntity.ITEM_ICON_KEY);
        icons.add(CarEntity.ITEM_ICON_BOXED);
        icons.add(CarEntity.ITEM_ICON_SMART_KEY);

        int total = icons.size() * CHOICE_WIDTH + (icons.size() - 1) * CHOICE_GAP;
        int x0 = this.width / 2 - total / 2;
        this.choicesTop = this.height / 2 - 50;
        this.choices.clear();
        for (int i = 0; i < icons.size(); i++) {
            int icon = icons.get(i);
            String labelKey = switch (icon) {
                case CarEntity.ITEM_ICON_VEHICLE -> "gui.mineturismo.item_icon.vehicle";
                case CarEntity.ITEM_ICON_KEY -> "gui.mineturismo.item_icon.key";
                case CarEntity.ITEM_ICON_SMART_KEY -> "gui.mineturismo.item_icon.smart_key";
                default -> "gui.mineturismo.item_icon.boxed";
            };
            ItemStack preview = spawnItem == null ? ItemStack.EMPTY : new ItemStack(spawnItem);
            VehicleItemIcons.applyToSpawnStack(preview, icon);
            int x = x0 + i * (CHOICE_WIDTH + CHOICE_GAP);
            this.choices.add(new Choice(icon, labelKey, preview, x));

            Component label = ModText.translatable(labelKey);
            if (icon == current) {
                label = ModText.literal("> ").append(label);
            }
            this.addButton(ModWidgets.button(label, x, this.choicesTop + ICON_BOX + 6, CHOICE_WIDTH, 20, b -> this.apply(icon)));
        }

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), this.width / 2 - 100, this.choicesTop + ICON_BOX + 60, 200, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.choicesTop - 30, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, ModText.translatable("gui.mineturismo.item_icon.desc"), this.width / 2, this.choicesTop - 16, 0xFFAAAAAA);

        Item spawnItem = AiCarPool.spawnItemFor(this.car.getModelId());
        int current = this.currentIcon(this.hasVehicleIcon(spawnItem));
        for (Choice choice : this.choices) {
            int bx = choice.x() + (CHOICE_WIDTH - ICON_BOX) / 2;
            int by = this.choicesTop;
            int frame = choice.icon() == current ? 0xFFFFD24A : 0xFF555555;
            guiGraphics.fill(bx - 1, by - 1, bx + ICON_BOX + 1, by + ICON_BOX + 1, frame);
            guiGraphics.fill(bx, by, bx + ICON_BOX, by + ICON_BOX, 0xC0101010);

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate((float) (bx + 4), (float) (by + 4), 0f);
            guiGraphics.pose().scale(2f, 2f, 1f);
            guiGraphics.renderItem(choice.preview(), 0, 0);
            guiGraphics.pose().popPose();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
