package com.rasmus.mobesp.client.gui;

import com.rasmus.mobesp.config.MobespConfig;
import com.rasmus.mobesp.util.MobTypes;
import com.rasmus.mobesp.util.SpawnEggRenderer;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MobEspConfigScreen extends Screen {
    private final Screen parent;
    private final MobespConfig config;
    private final List<Button> mobButtons = new ArrayList<>();
    private EditBox searchField;
    private EditBox colorField;
    private int scrollOffset = 0;
    private int maxScrollOffset = 0;
    private String currentSearchTerm = "";
    private static final int BUTTONS_PER_ROW = 3;
    private static final int BUTTON_WIDTH = 140;
    private static final int BUTTON_HEIGHT = 30;
    private static final int BUTTON_SPACING = 5;

    public MobEspConfigScreen(Screen parent) {
        super(Component.literal("Mob ESP Configuration"));
        this.parent = parent;
        this.config = MobespConfig.get();
    }

    @Override
    protected void init() {
        super.init();

        // ALL ON SAME LINE - Y = 30, CENTERED
        int topRowY = 30;
        int centerX = this.width / 2;

        // Calculate total width needed
        int masterToggleWidth = 140;
        int searchWidth = 140;
        int clearWidth = 50;
        int spacing = 10;
        int totalWidth = masterToggleWidth + spacing + searchWidth + spacing + clearWidth;

        // Start position to center everything
        int startX = centerX - (totalWidth / 2);

        // Master toggle button (left)
        Button masterToggleButton = Button.builder(
                Component.literal("Master Toggle: " + (config.masterToggle ? "ON" : "OFF"))
                        .withStyle(config.masterToggle ? ChatFormatting.GREEN : ChatFormatting.RED),
                button -> {
                    config.masterToggle = !config.masterToggle;
                    button.setMessage(Component.literal("Master Toggle: " + (config.masterToggle ? "ON" : "OFF"))
                            .withStyle(config.masterToggle ? ChatFormatting.GREEN : ChatFormatting.RED));
                    saveConfig();
                }
        ).bounds(startX, topRowY, masterToggleWidth, 20).build();
        this.addRenderableWidget(masterToggleButton);

        // Search field (middle)
        int searchX = startX + masterToggleWidth + spacing;
        searchField = new EditBox(this.font, searchX, topRowY, searchWidth, 20, Component.literal("Search..."));
        searchField.setHint(Component.literal("Search...").withStyle(ChatFormatting.GRAY));
        searchField.setValue(currentSearchTerm);
        searchField.setResponder(this::onSearchChanged);
        this.addRenderableWidget(searchField);

        // Clear button (right)
        int clearX = searchX + searchWidth + spacing;
        this.addRenderableWidget(Button.builder(
                Component.literal("Clear"),
                button -> {
                    searchField.setValue("");
                    onSearchChanged("");
                    searchField.setFocused(false); // Remove focus after clearing
                }
        ).bounds(clearX, topRowY, clearWidth, 20).build());

        // Color row (below the top controls): label + hex field + preview swatch, centered
        int colorRowY = 55;
        int colorLabelWidth = this.font.width("ESP Color:");
        int colorBoxWidth = 70;
        int swatchSize = 20;
        int colorTotal = colorLabelWidth + 6 + colorBoxWidth + 6 + swatchSize;
        int colorStartX = centerX - (colorTotal / 2);

        colorField = new EditBox(this.font, colorStartX + colorLabelWidth + 6, colorRowY, colorBoxWidth, 20,
                Component.literal("#RRGGBB"));
        colorField.setValue(String.format("#%06X", config.espColor));
        colorField.setResponder(this::onColorChanged);
        this.addRenderableWidget(colorField);

        // Calculate max scroll offset based on filtered results
        calculateMaxScroll();

        // Create mob toggle buttons
        createMobButtons();

        // Bottom buttons
        this.addRenderableWidget(Button.builder(
                Component.literal("Done"),
                button -> this.onClose()
        ).bounds(this.width / 2 - 50, this.height - 30, 100, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("All ON"),
                button -> {
                    for (String mobType : getFilteredMobs()) {
                        config.setMobGlowEnabled(mobType, true);
                    }
                    updateMobButtons();
                    saveConfig();
                }
        ).bounds(this.width / 2 - 160, this.height - 30, 50, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("All OFF"),
                button -> {
                    for (String mobType : getFilteredMobs()) {
                        config.setMobGlowEnabled(mobType, false);
                    }
                    updateMobButtons();
                    saveConfig();
                }
        ).bounds(this.width / 2 + 110, this.height - 30, 50, 20).build());
    }

    private void onColorChanged(String value) {
        String hex = value.startsWith("#") ? value.substring(1) : value;
        if (hex.length() == 6 && hex.chars().allMatch(c -> Character.digit(c, 16) >= 0)) {
            config.espColor = Integer.parseInt(hex, 16);
            saveConfig();
        }
    }

    private void onSearchChanged(String searchTerm) {
        currentSearchTerm = searchTerm;
        scrollOffset = 0; // Reset scroll when search changes
        calculateMaxScroll();
        recreateMobButtonsOnly();
    }

    private List<String> getFilteredMobs() {
        List<String> allMobs = MobTypes.getAllMobTypes();
        if (currentSearchTerm.isEmpty()) {
            return allMobs;
        }

        String searchLower = currentSearchTerm.toLowerCase();
        return allMobs.stream()
                .filter(mob -> {
                    String mobName = mob.toLowerCase();
                    String displayName = SpawnEggRenderer.getFormattedName(mob).toLowerCase();

                    // Exact match first
                    if (mobName.contains(searchLower) || displayName.contains(searchLower)) {
                        return true;
                    }

                    // Fuzzy matching for typos
                    return fuzzyMatch(mobName, searchLower) || fuzzyMatch(displayName, searchLower);
                })
                .collect(Collectors.toList());
    }

    private boolean fuzzyMatch(String text, String search) {
        if (search.length() < 2) return false; // Too short for fuzzy matching

        // Simple fuzzy matching - check if most characters are present in order
        int matches = 0;
        int textIndex = 0;

        for (char searchChar : search.toCharArray()) {
            while (textIndex < text.length() && text.charAt(textIndex) != searchChar) {
                textIndex++;
            }
            if (textIndex < text.length()) {
                matches++;
                textIndex++;
            }
        }

        // Require at least 70% of characters to match for fuzzy matching
        double matchRatio = (double) matches / search.length();
        return matchRatio >= 0.7;
    }

    private void calculateMaxScroll() {
        List<String> filteredMobs = getFilteredMobs();
        int totalRows = (int) Math.ceil((double) filteredMobs.size() / BUTTONS_PER_ROW);
        int visibleArea = this.height - 155; // Space between controls and bottom buttons
        int visibleRows = visibleArea / (BUTTON_HEIGHT + BUTTON_SPACING);
        maxScrollOffset = Math.max(0, (totalRows - visibleRows) * (BUTTON_HEIGHT + BUTTON_SPACING));
    }

    private void createMobButtons() {
        mobButtons.clear();
        List<String> filteredMobs = getFilteredMobs();

        int startY = 95; // Start buttons below the top controls and the color row
        int currentRow = 0;
        int currentCol = 0;

        for (String mobType : filteredMobs) {
            boolean isEnabled = config.isMobGlowEnabled(mobType);

            int x = (this.width / 2) - (BUTTONS_PER_ROW * (BUTTON_WIDTH + BUTTON_SPACING)) / 2 +
                    currentCol * (BUTTON_WIDTH + BUTTON_SPACING);
            int y = startY + currentRow * (BUTTON_HEIGHT + BUTTON_SPACING) - scrollOffset;

            // Only add visible buttons
            if (y > 85 && y < this.height - 60) {
                String displayName = SpawnEggRenderer.getFormattedName(mobType);

                Button button = Button.builder(
                        Component.literal("   " + displayName)
                                .withStyle(isEnabled ? ChatFormatting.GREEN : ChatFormatting.RED),
                        btn -> {
                            boolean newState = !config.isMobGlowEnabled(mobType);
                            config.setMobGlowEnabled(mobType, newState);
                            btn.setMessage(Component.literal("   " + displayName)
                                    .withStyle(newState ? ChatFormatting.GREEN : ChatFormatting.RED));
                            saveConfig();
                        }
                ).bounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT).build();

                mobButtons.add(button);
                this.addRenderableWidget(button);
            }

            currentCol++;
            if (currentCol >= BUTTONS_PER_ROW) {
                currentCol = 0;
                currentRow++;
            }
        }
    }

    private void recreateMobButtonsOnly() {
        // Remove only mob buttons, not all children
        for (Button button : mobButtons) {
            this.removeWidget(button);
        }
        mobButtons.clear();

        // Recreate mob buttons
        createMobButtons();
    }

    private void updateMobButtons() {
        // Remove all mob buttons and recreate them
        this.clearWidgets();
        this.init();
    }

    private void saveConfig() {
        AutoConfig.getConfigHolder(MobespConfig.class).save();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        // Render a simple dark background
        extractor.fill(0, 0, this.width, this.height, 0x88000000);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        // Render background + widgets first
        super.extractRenderState(extractor, mouseX, mouseY, delta);

        // Overlays go on top of the widgets
        extractor.nextStratum();

        // Title
        extractor.centeredText(this.font, this.title, this.width / 2, 10, 0xFFFFFFFF);

        // Enhanced search field visual feedback
        renderSearchFieldFeedback(extractor);

        // Search results count
        List<String> filteredMobs = getFilteredMobs();
        String resultText = filteredMobs.size() + " mobs";
        if (!currentSearchTerm.isEmpty()) {
            resultText = filteredMobs.size() + " of " + MobTypes.getAllMobTypes().size() + " mobs found";
        }

        // Color the result text based on search activity
        ChatFormatting resultColor = !currentSearchTerm.isEmpty() ? ChatFormatting.YELLOW : ChatFormatting.GRAY;
        if (!currentSearchTerm.isEmpty() && filteredMobs.isEmpty()) {
            resultColor = ChatFormatting.RED; // No results found
        }

        extractor.centeredText(this.font,
                Component.literal(resultText).withStyle(resultColor),
                this.width / 2, 82, 0xFFFFFFFF);

        // ESP color label and live preview swatch
        int labelX = colorField.getX() - 6 - this.font.width("ESP Color:");
        extractor.text(this.font, Component.literal("ESP Color:"), labelX, colorField.getY() + 6, 0xFFFFFFFF);
        int swatchX = colorField.getX() + colorField.getWidth() + 6;
        int swatchY = colorField.getY();
        extractor.fill(swatchX - 1, swatchY - 1, swatchX + 21, swatchY + 21, 0xFFFFFFFF);
        extractor.fill(swatchX, swatchY, swatchX + 20, swatchY + 20, 0xFF000000 | config.espColor);

        // Render spawn egg icons on buttons
        renderMobIcons(extractor);

        // Render scrollbar
        renderScrollbar(extractor);
    }

    private void renderSearchFieldFeedback(GuiGraphicsExtractor extractor) {
        // Get search field position
        int searchX = searchField.getX();
        int searchY = searchField.getY();
        int searchWidth = searchField.getWidth();
        int searchHeight = searchField.getHeight();

        // Enhanced border when focused
        if (searchField.isFocused()) {
            // Bright border around search field when focused
            int borderColor = 0xFF4A9EFF; // Light blue
            extractor.fill(searchX - 2, searchY - 2, searchX + searchWidth + 2, searchY, borderColor);
            extractor.fill(searchX - 2, searchY + searchHeight, searchX + searchWidth + 2, searchY + searchHeight + 2, borderColor);
            extractor.fill(searchX - 2, searchY, searchX, searchY + searchHeight, borderColor);
            extractor.fill(searchX + searchWidth, searchY, searchX + searchWidth + 2, searchY + searchHeight, borderColor);
        }

        // Show search term feedback
        if (!currentSearchTerm.isEmpty()) {
            String searchInfo = "Searching for: \"" + currentSearchTerm + "\"";
            int infoY = searchField.getY() - 12;
            extractor.text(this.font,
                    Component.literal(searchInfo).withStyle(ChatFormatting.YELLOW),
                    searchX, infoY, 0xFFFFFFFF);
        }
    }

    private void renderMobIcons(GuiGraphicsExtractor extractor) {
        List<String> filteredMobs = getFilteredMobs();

        int startY = 95;
        int currentRow = 0;
        int currentCol = 0;

        for (String mobType : filteredMobs) {
            int x = (this.width / 2) - (BUTTONS_PER_ROW * (BUTTON_WIDTH + BUTTON_SPACING)) / 2 +
                    currentCol * (BUTTON_WIDTH + BUTTON_SPACING);
            int y = startY + currentRow * (BUTTON_HEIGHT + BUTTON_SPACING) - scrollOffset;

            // Only render visible icons
            if (y > 85 && y < this.height - 60) {
                // Render spawn egg icon inline with button
                SpawnEggRenderer.renderMobIcon(extractor, mobType, x, y);
            }

            currentCol++;
            if (currentCol >= BUTTONS_PER_ROW) {
                currentCol = 0;
                currentRow++;
            }
        }
    }

    private void renderScrollbar(GuiGraphicsExtractor extractor) {
        if (maxScrollOffset <= 0) return; // No scrollbar needed if everything fits

        int scrollbarX = this.width - 15;
        int scrollbarY = 95;
        int scrollbarHeight = this.height - 165;
        int scrollbarWidth = 8;

        // Background track
        extractor.fill(scrollbarX, scrollbarY, scrollbarX + scrollbarWidth, scrollbarY + scrollbarHeight, 0x88444444);

        // Calculate thumb position and size
        float scrollPercent = (float) scrollOffset / maxScrollOffset;
        int thumbHeight = Math.max(20, scrollbarHeight / 4);
        int thumbY = scrollbarY + (int) (scrollPercent * (scrollbarHeight - thumbHeight));

        // Scrollbar thumb
        extractor.fill(scrollbarX + 1, thumbY, scrollbarX + scrollbarWidth - 1, thumbY + thumbHeight, 0xFFAAAAAA);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int itemHeight = BUTTON_HEIGHT + BUTTON_SPACING; // Height of one row
        int scrollDirection = verticalAmount > 0 ? -1 : 1; // Invert for natural scrolling

        scrollOffset += scrollDirection * itemHeight;
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset)); // Clamp to valid range

        // Recreate buttons with new positions
        recreateMobButtonsOnly();

        return true;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClicked) {
        // Focus follows the clicked text field; clicking elsewhere unfocuses both
        boolean onSearch = searchField.isMouseOver(event.x(), event.y());
        boolean onColor = colorField.isMouseOver(event.x(), event.y());
        searchField.setFocused(onSearch);
        colorField.setFocused(onColor);
        if (onSearch || onColor) {
            return true;
        }

        return super.mouseClicked(event, doubleClicked);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        // ESC key handling
        if (event.key() == 256) { // ESC key
            if (searchField.isFocused() && !currentSearchTerm.isEmpty()) {
                // If search field is focused and has text, clear it first
                searchField.setValue("");
                onSearchChanged("");
                return true;
            } else if (searchField.isFocused()) {
                // If search field is focused but empty, unfocus it
                searchField.setFocused(false);
                return true;
            } else if (colorField.isFocused()) {
                colorField.setFocused(false);
                return true;
            }
            // Otherwise, close the screen
            this.onClose();
            return true;
        }

        // Tab key to focus search field
        if (event.key() == 258) { // TAB key
            searchField.setFocused(!searchField.isFocused());
            return true;
        }

        // Make sure the focused text field gets the input
        if (searchField.isFocused()) {
            return searchField.keyPressed(event);
        }
        if (colorField.isFocused()) {
            return colorField.keyPressed(event);
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (searchField.isFocused()) {
            return searchField.charTyped(event);
        }
        if (colorField.isFocused()) {
            return colorField.charTyped(event);
        }
        return super.charTyped(event);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}
