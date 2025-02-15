package com.gregtechceu.gtceu.integration;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.gui.WidgetUtils;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeCondition;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.misc.FluidStorage;
import com.lowdragmc.lowdraglib.side.fluid.IFluidStorage;
import com.lowdragmc.lowdraglib.utils.CycleItemStackHandler;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import com.lowdragmc.lowdraglib.utils.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author KilaBash
 * @date 2023/2/25
 * @implNote GTRecipeWidget
 */
public class GTRecipeWidget extends WidgetGroup {
    public GTRecipeWidget(GTRecipe recipe) {
        super(0, 0, recipe.recipeType.getJEISize().width, recipe.recipeType.getJEISize().height);
        setClientSideWidget();
        List<Content> inputStackContents = new ArrayList<>();
        inputStackContents.addAll(recipe.getInputContents(ItemRecipeCapability.CAP));
        inputStackContents.addAll(recipe.getTickInputContents(ItemRecipeCapability.CAP));
        List<List<ItemStack>> inputStacks = inputStackContents.stream().map(content -> content.content)
                .map(ItemRecipeCapability.CAP::of)
                .map(Ingredient::getItems)
                .map(Arrays::stream)
                .map(Stream::toList)
                .collect(Collectors.toList());
        while (inputStacks.size() < recipe.recipeType.getMaxInputs(ItemRecipeCapability.CAP)) inputStacks.add(null);

        List<Content> outputStackContents = new ArrayList<>();
        outputStackContents.addAll(recipe.getOutputContents(ItemRecipeCapability.CAP));
        outputStackContents.addAll(recipe.getTickOutputContents(ItemRecipeCapability.CAP));
        List<List<ItemStack>> outputStacks = outputStackContents.stream().map(content -> content.content)
                .map(ItemRecipeCapability.CAP::of)
                .map(Ingredient::getItems)
                .map(Arrays::stream)
                .map(Stream::toList)
                .collect(Collectors.toList());
        while (outputStacks.size() < recipe.recipeType.getMaxOutputs(ItemRecipeCapability.CAP)) outputStacks.add(null);

        IFluidStorage[] inputFluids = new IFluidStorage[recipe.recipeType.getMaxInputs(FluidRecipeCapability.CAP)];
        List<Content> inputFluidContents = new ArrayList<>(recipe.getInputContents(FluidRecipeCapability.CAP));
        inputFluidContents.addAll(recipe.getTickInputContents(FluidRecipeCapability.CAP));
        for (int i = 0; i < inputFluidContents.size(); i++) {
            inputFluids[i] = new FluidStorage(FluidRecipeCapability.CAP.of(inputFluidContents.get(i).content));
        }

        IFluidStorage[] outputFluids = new IFluidStorage[recipe.recipeType.getMaxOutputs(FluidRecipeCapability.CAP)];
        List<Content> outputFluidContents = new ArrayList<>( recipe.getOutputContents(FluidRecipeCapability.CAP));
        outputFluidContents.addAll(recipe.getTickOutputContents(FluidRecipeCapability.CAP));
        for (int i = 0; i < outputFluidContents.size(); i++) {
            outputFluids[i] = new FluidStorage(FluidRecipeCapability.CAP.of(outputFluidContents.get(i).content));
        }

        var group = recipe.recipeType.createUITemplate(ProgressWidget.JEIProgress,
                new CycleItemStackHandler(inputStacks),
                new CycleItemStackHandler(outputStacks),
                inputFluids,
                outputFluids
        );
        // bind item in overlay
        WidgetUtils.widgetByIdForEach(group, "^%s_[0-9]+$".formatted(ItemRecipeCapability.CAP.slotName(IO.IN)), SlotWidget.class, slot -> {
            var index = WidgetUtils.widgetIdIndex(slot);
            if (index >= 0 && index < inputStackContents.size()) {
                var content = inputStackContents.get(index);
                slot.setOverlay(content.createOverlay(index >= recipe.getInputContents(ItemRecipeCapability.CAP).size()));
                slot.setOnAddedTooltips((w, tooltips) -> {
                    var chance = content.chance;
                    if (chance < 1) {
                        tooltips.add(chance == 0 ?
                                Component.translatable("gtceu.gui.content.chance_0") :
                                Component.translatable("gtceu.gui.content.chance_1", String.format("%.1f", chance * 100) + "%"));
                        if (content.tierChanceBoost > 0) {
                            tooltips.add(Component.translatable("gtceu.gui.content.tier_boost", String.format("%.1f", content.tierChanceBoost * 100) + "%"));
                        }
                    }
                    if (index >= recipe.getInputContents(ItemRecipeCapability.CAP).size()) {
                        tooltips.add(Component.translatable("gtceu.gui.content.per_tick"));
                    }
                });
            }
        });
        // bind item out overlay
        WidgetUtils.widgetByIdForEach(group, "^%s_[0-9]+$".formatted(ItemRecipeCapability.CAP.slotName(IO.OUT)), SlotWidget.class, slot -> {
            var index = WidgetUtils.widgetIdIndex(slot);
            if (index >= 0 && index < outputStackContents.size()) {
                var content = outputStackContents.get(index);
                slot.setOverlay(content.createOverlay(index >= recipe.getOutputContents(ItemRecipeCapability.CAP).size()));
                slot.setOnAddedTooltips((w, tooltips) -> {
                    var chance = content.chance;
                    if (chance < 1) {
                        tooltips.add(chance == 0 ?
                                Component.translatable("gtceu.gui.content.chance_0") :
                                Component.translatable("gtceu.gui.content.chance_1", String.format("%.1f", chance * 100) + "%"));
                        if (content.tierChanceBoost > 0) {
                            tooltips.add(Component.translatable("gtceu.gui.content.tier_boost", String.format("%.1f", content.tierChanceBoost * 100) + "%"));
                        }
                    }
                    if (index >= recipe.getOutputContents(ItemRecipeCapability.CAP).size()) {
                        tooltips.add(Component.translatable("gtceu.gui.content.per_tick"));
                    }
                });
            }
        });
        // bind fluid in overlay
        WidgetUtils.widgetByIdForEach(group, "^%s_[0-9]+$".formatted(FluidRecipeCapability.CAP.slotName(IO.IN)), TankWidget.class, tank -> {
            var index = WidgetUtils.widgetIdIndex(tank);
            if (index >= 0 && index < inputFluidContents.size()) {
                var content = inputFluidContents.get(index);
                tank.setOverlay(content.createOverlay(index >= recipe.getInputContents(FluidRecipeCapability.CAP).size()));
                tank.setOnAddedTooltips((w, tooltips) -> {
                    var chance = content.chance;
                    if (chance < 1) {
                        tooltips.add(chance == 0 ?
                                Component.translatable("gtceu.gui.content.chance_0") :
                                Component.translatable("gtceu.gui.content.chance_1", String.format("%.1f", chance * 100) + "%"));
                        if (content.tierChanceBoost > 0) {
                            tooltips.add(Component.translatable("gtceu.gui.content.tier_boost", String.format("%.1f", content.tierChanceBoost * 100) + "%"));
                        }
                    }
                    if (index >= recipe.getInputContents(FluidRecipeCapability.CAP).size()) {
                        tooltips.add(Component.translatable("gtceu.gui.content.per_tick"));
                    }
                });
            }
        });
        // bind fluid out overlay
        WidgetUtils.widgetByIdForEach(group, "^%s_[0-9]+$".formatted(FluidRecipeCapability.CAP.slotName(IO.OUT)), TankWidget.class, tank -> {
            var index = WidgetUtils.widgetIdIndex(tank);
            if (index >= 0 && index < outputFluidContents.size()) {
                var content = outputFluidContents.get(index);
                tank.setOverlay(content.createOverlay(index >= recipe.getOutputContents(FluidRecipeCapability.CAP).size()));
                tank.setOnAddedTooltips((w, tooltips) -> {
                    var chance = content.chance;
                    if (chance < 1) {
                        tooltips.add(chance == 0 ?
                                Component.translatable("gtceu.gui.content.chance_0") :
                                Component.translatable("gtceu.gui.content.chance_1", String.format("%.1f", chance * 100) + "%"));
                        if (content.tierChanceBoost > 0) {
                            tooltips.add(Component.translatable("gtceu.gui.content.tier_boost", String.format("%.1f", content.tierChanceBoost * 100) + "%"));
                        }
                    }
                    if (index >= recipe.getOutputContents(FluidRecipeCapability.CAP).size()) {
                        tooltips.add(Component.translatable("gtceu.gui.content.per_tick"));
                    }
                });
            }
        });
        var size = group.getSize();
        group.setSelfPosition(new Position((176 - size.width) / 2, 0));
        addWidget(group);

        int yOffset = 5 + size.height;
        addWidget(new LabelWidget(3, yOffset,
                LocalizationUtils.format("gtceu.recipe.duration", recipe.duration / 20f)));
        var EUt = RecipeHelper.getInputEUt(recipe);
        boolean isOutput = false;
        if (EUt == 0) {
            EUt = RecipeHelper.getOutputEUt(recipe);
            isOutput = true;
        }
        if (EUt > 0) {
            addWidget(new LabelWidget(3, yOffset += 10,
                    LocalizationUtils.format("gtceu.recipe.total", EUt * recipe.duration)));
            addWidget(new LabelWidget(3, yOffset += 10,
                    LocalizationUtils.format(!isOutput ? "gtceu.recipe.eu" : "gtceu.recipe.eu_inverted", EUt, GTValues.VN[GTUtil.getTierByVoltage(EUt)])));
        }
        for (RecipeCondition condition : recipe.conditions) {
            if (condition.getTooltips() == null) continue;
            addWidget(new LabelWidget(3, yOffset += 10, condition.getTooltips().getString()));
        }
        for (Function<CompoundTag, String> dataInfo : recipe.recipeType.getDataInfos()) {
            addWidget(new LabelWidget(3, yOffset += 10, dataInfo.apply(recipe.data)));
        }
        recipe.recipeType.appendJEIUI(recipe, this);
    }
}
