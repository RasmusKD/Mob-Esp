package com.rasmus.mobesp.client.gui;

import com.rasmus.mobesp.config.MobespConfig;
import com.rasmus.mobesp.util.MobTypes;
import com.rasmus.mobesp.util.SpawnEggRenderer;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MobEspConfigScreen extends Screen {
    private final Screen parent;
    private final MobespConfig config;
    private final List<ButtonWidget> mobButtons = new ArrayList<>();
    private TextFieldWidget searchField;
    private int scrollOffset = 0;
    private int maxScrollOffset = 0;
    private String currentSearchTerm = "";
    private static final int BUTTONS_PER_ROW = 3;
    private static final int BUTTON_WIDTH = 140;
    private static final int BUTTON_HEIGHT = 30;
    private static final int BUTTON_SPACING = 5;

    public MobEspConfigScreen(Screen parent) {
        super(Text.literal("Mob ESP Configuration"));
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
        ButtonWidget masterToggleButton = ButtonWidget.builder(
                Text.literal("Master Toggle: " + (config.masterToggle ? "ON" : "OFF"))
                        .formatted(config.masterToggle ? Formatting.GREEN : Formatting.RED),
                button -> {
                    config.masterToggle = !config.masterToggle;
                    button.setMessage(Text.literal("Master Toggle: " + (config.masterToggle ? "ON" : "OFF"))
                            .formatted(config.masterToggle ? Formatting.GREEN : Formatting.RED));
                    saveConfig();
                }
        ).dimensions(startX, topRowY, masterToggleWidth, 20).build();
        this.addDrawableChild(masterToggleButton);

        // Search field (middle)
        int searchX = startX + masterToggleWidth + spacing;
        searchField = new TextFieldWidget(this.textRenderer, searchX, topRowY, searchWidth, 20, Text.literal("Search..."));
        searchField.setPlaceholder(Text.literal("Search...").formatted(Formatting.GRAY));
        searchField.setText(currentSearchTerm);
        searchField.setChangedListener(this::onSearchChanged);
        this.addDrawableChild(searchField);

        // Clear button (right)
        int clearX = searchX + searchWidth + spacing;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Clear"),
                button -> {
                    searchField.setText("");
                    onSearchChanged("");
                    searchField.setFocused(false); // Remove focus after clearing
                }
        ).dimensions(clearX, topRowY, clearWidth, 20).build());

        // Calculate max scroll offset based on filtered results
        calculateMaxScroll();

        // Create mob toggle buttons
        createMobButtons();

        // Bottom buttons
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Done"),
                button -> this.close()
        ).dimensions(this.width / 2 - 50, this.height - 30, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("All ON"),
                button -> {
                    for (String mobType : getFilteredMobs()) {
                        config.setMobGlowEnabled(mobType, true);
                    }
                    updateMobButtons();
                    saveConfig();
                }
        ).dimensions(this.width / 2 - 160, this.height - 30, 50, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("All OFF"),
                button -> {
                    for (String mobType : getFilteredMobs()) {
                        config.setMobGlowEnabled(mobType, false);
                    }
                    updateMobButtons();
                    saveConfig();
                }
        ).dimensions(this.width / 2 + 110, this.height - 30, 50, 20).build());
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
        int visibleArea = this.height - 120; // Space between controls and bottom buttons
        int visibleRows = visibleArea / (BUTTON_HEIGHT + BUTTON_SPACING);
        maxScrollOffset = Math.max(0, (totalRows - visibleRows) * (BUTTON_HEIGHT + BUTTON_SPACING));
    }

    private void createMobButtons() {
        mobButtons.clear();
        List<String> filteredMobs = getFilteredMobs();

        int startY = 70; // Start buttons below the top controls
        int currentRow = 0;
        int currentCol = 0;

        for (String mobType : filteredMobs) {
            boolean isEnabled = config.isMobGlowEnabled(mobType);

            int x = (this.width / 2) - (BUTTONS_PER_ROW * (BUTTON_WIDTH + BUTTON_SPACING)) / 2 +
                    currentCol * (BUTTON_WIDTH + BUTTON_SPACING);
            int y = startY + currentRow * (BUTTON_HEIGHT + BUTTON_SPACING) - scrollOffset;

            // Only add visible buttons
            if (y > 60 && y < this.height - 60) {
                String displayName = SpawnEggRenderer.getFormattedName(mobType);

                ButtonWidget button = ButtonWidget.builder(
                        Text.literal("   " + displayName)
                                .formatted(isEnabled ? Formatting.GREEN : Formatting.RED),
                        btn -> {
                            boolean newState = !config.isMobGlowEnabled(mobType);
                            config.setMobGlowEnabled(mobType, newState);
                            btn.setMessage(Text.literal("   " + displayName)
                                    .formatted(newState ? Formatting.GREEN : Formatting.RED));
                            saveConfig();
                        }
                ).dimensions(x, y, BUTTON_WIDTH, BUTTON_HEIGHT).build();

                mobButtons.add(button);
                this.addDrawableChild(button);
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
        for (ButtonWidget button : mobButtons) {
            this.remove(button);
        }
        mobButtons.clear();

        // Recreate mob buttons
        createMobButtons();
    }

    private void updateMobButtons() {
        // Remove all mob buttons and recreate them
        this.clearChildren();
        this.init();
    }

    private void saveConfig() {
        AutoConfig.getConfigHolder(MobespConfig.class).save();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render a simple dark background
        context.fill(0, 0, this.width, this.height, 0x88000000);

        // Render widgets first
        super.render(context, mouseX, mouseY, delta);

        // Title
        context.drawCenteredTextWithShadow(this.textRenderer, this.title,
                this.width / 2, 10, 0xFFFFFF);

        // Enhanced search field visual feedback
        renderSearchFieldFeedback(context);

        // Search results count
        List<String> filteredMobs = getFilteredMobs();
        String resultText = filteredMobs.size() + " mobs";
        if (!currentSearchTerm.isEmpty()) {
            resultText = filteredMobs.size() + " of " + MobTypes.getAllMobTypes().size() + " mobs found";
        }

        // Color the result text based on search activity
        Formatting resultColor = !currentSearchTerm.isEmpty() ? Formatting.YELLOW : Formatting.GRAY;
        if (!currentSearchTerm.isEmpty() && filteredMobs.isEmpty()) {
            resultColor = Formatting.RED; // No results found
        }

        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal(resultText).formatted(resultColor),
                this.width / 2, 55, 0xFFFFFF);

        // Render spawn egg icons on buttons
        renderMobIcons(context);

        // Render scrollbar
        renderScrollbar(context);
    }

    private void renderSearchFieldFeedback(DrawContext context) {
        // Get search field position
        int searchX = searchField.getX();
        int searchY = searchField.getY();
        int searchWidth = searchField.getWidth();
        int searchHeight = searchField.getHeight();

        // Enhanced border when focused
        if (searchField.isFocused()) {
            // Bright border around search field when focused
            int borderColor = 0xFF4A9EFF; // Light blue
            context.fill(searchX - 2, searchY - 2, searchX + searchWidth + 2, searchY, borderColor);
            context.fill(searchX - 2, searchY + searchHeight, searchX + searchWidth + 2, searchY + searchHeight + 2, borderColor);
            context.fill(searchX - 2, searchY, searchX, searchY + searchHeight, borderColor);
            context.fill(searchX + searchWidth, searchY, searchX + searchWidth + 2, searchY + searchHeight, borderColor);

            // Status indicator
            String statusText = "Search active";
            int statusX = searchX + searchWidth + 10;
            int statusY = searchY + 5;
            context.drawTextWithShadow(this.textRenderer,
                    Text.literal(statusText).formatted(Formatting.AQUA),
                    statusX, statusY, 0xFFFFFF);
        }

        // Show search term feedback
        if (!currentSearchTerm.isEmpty()) {
            String searchInfo = "Searching for: \"" + currentSearchTerm + "\"";
            int infoY = searchY - 12;
            context.drawTextWithShadow(this.textRenderer,
                    Text.literal(searchInfo).formatted(Formatting.YELLOW),
                    searchX, infoY, 0xFFFFFF);
        }


    }

    private void renderMobIcons(DrawContext context) {
        List<String> filteredMobs = getFilteredMobs();

        int startY = 70;
        int currentRow = 0;
        int currentCol = 0;

        for (String mobType : filteredMobs) {
            int x = (this.width / 2) - (BUTTONS_PER_ROW * (BUTTON_WIDTH + BUTTON_SPACING)) / 2 +
                    currentCol * (BUTTON_WIDTH + BUTTON_SPACING);
            int y = startY + currentRow * (BUTTON_HEIGHT + BUTTON_SPACING) - scrollOffset;

            // Only render visible icons
            if (y > 60 && y < this.height - 60) {
                // Render spawn egg icon inline with button
                SpawnEggRenderer.renderMobIcon(context, mobType, x, y);
            }

            currentCol++;
            if (currentCol >= BUTTONS_PER_ROW) {
                currentCol = 0;
                currentRow++;
            }
        }
    }

    private void renderScrollbar(DrawContext context) {
        if (maxScrollOffset <= 0) return; // No scrollbar needed if everything fits

        int scrollbarX = this.width - 15;
        int scrollbarY = 70;
        int scrollbarHeight = this.height - 140;
        int scrollbarWidth = 8;

        // Background track
        context.fill(scrollbarX, scrollbarY, scrollbarX + scrollbarWidth, scrollbarY + scrollbarHeight, 0x88444444);

        // Calculate thumb position and size
        float scrollPercent = (float) scrollOffset / maxScrollOffset;
        int thumbHeight = Math.max(20, scrollbarHeight / 4);
        int thumbY = scrollbarY + (int) (scrollPercent * (scrollbarHeight - thumbHeight));

        // Scrollbar thumb
        context.fill(scrollbarX + 1, thumbY, scrollbarX + scrollbarWidth - 1, thumbY + thumbHeight, 0xFFAAAAAA);
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Check if clicking on search field
        if (searchField.isMouseOver(mouseX, mouseY)) {
            searchField.setFocused(true);
            return true;
        } else {
            // Click outside search field - remove focus
            searchField.setFocused(false);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ESC key handling
        if (keyCode == 256) { // ESC key
            if (searchField.isFocused() && !currentSearchTerm.isEmpty()) {
                // If search field is focused and has text, clear it first
                searchField.setText("");
                onSearchChanged("");
                return true;
            } else if (searchField.isFocused()) {
                // If search field is focused but empty, unfocus it
                searchField.setFocused(false);
                return true;
            }
            // Otherwise, close the screen
            this.close();
            return true;
        }

        // Tab key to focus search field
        if (keyCode == 258) { // TAB key
            searchField.setFocused(!searchField.isFocused());
            return true;
        }

        // Make sure search field gets focus and input
        if (searchField.isFocused()) {
            return searchField.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (searchField.isFocused()) {
            return searchField.charTyped(chr, modifiers);
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }
}