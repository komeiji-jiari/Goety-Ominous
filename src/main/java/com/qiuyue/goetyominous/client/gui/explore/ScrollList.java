package com.qiuyue.goetyominous.client.gui.explore;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ScrollList<T> {
    private final Font font;
    private final List<T> items;
    private final Function<T, Component> label;
    @Nullable
    private final Function<T, Integer> color;
    @Nullable
    private final Function<T, Boolean> selected;
    private final boolean[] enabledFlags;
    @Nullable
    private final Consumer<Integer> onPick;
    private final Component emptyText;
    private final int rowHeight;
    @Nullable
    private final RowRenderer<T> renderer;
    private int x;
    private int y;
    private int width;
    private int height;
    private double scroll;
    private int hovered = -1;
    private boolean draggingThumb;
    private double dragOffset;

    public ScrollList(Font font, List<T> items, Function<T, Component> label, @Nullable Function<T, Integer> color,
                      @Nullable Function<T, Boolean> selected, boolean[] enabledFlags,
                      @Nullable Consumer<Integer> onPick, Component emptyText, int rowHeight) {
        this(font, items, label, color, selected, enabledFlags, onPick, emptyText, rowHeight, null);
    }

    public ScrollList(Font font, List<T> items, Function<T, Component> label, @Nullable Function<T, Integer> color,
                      @Nullable Function<T, Boolean> selected, boolean[] enabledFlags,
                      @Nullable Consumer<Integer> onPick, Component emptyText, int rowHeight,
                      @Nullable RowRenderer<T> renderer) {
        this.font = font;
        this.items = items;
        this.label = label;
        this.color = color;
        this.selected = selected;
        this.enabledFlags = enabledFlags;
        this.onPick = onPick;
        this.emptyText = emptyText;
        this.rowHeight = rowHeight;
        this.renderer = renderer;
    }

    public void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.scroll = this.clampScroll(this.scroll);
    }

    public List<T> items() {
        return this.items;
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public int rowHeight() {
        return this.rowHeight;
    }

    public int contentRight() {
        return this.needBar() ? this.trackLeft() : this.x + this.width;
    }

    public int rowTop(int index) {
        return this.y + 2 + (int) ((double) (index * this.rowHeight) - this.scroll);
    }

    public double scroll() {
        return this.scroll;
    }

    public void setScroll(double value) {
        this.scroll = this.clampScroll(value);
    }

    public int hovered() {
        return this.hovered;
    }

    private double contentHeight() {
        return (double) this.items.size() * (double) this.rowHeight + 2.0D;
    }

    private double maxScroll() {
        return Math.max(0.0D, this.contentHeight() - (double) this.height);
    }

    private double clampScroll(double value) {
        return Mth.clamp(value, 0.0D, this.maxScroll());
    }

    private boolean needBar() {
        return this.maxScroll() > 0.0D;
    }

    private int trackRight() {
        return this.x + this.width;
    }

    private int trackLeft() {
        return this.trackRight() - 6;
    }

    private int thumbHeight() {
        if (!this.needBar()) {
            return 0;
        }
        double ratio = (double) this.height / this.contentHeight();
        return Math.min(this.height, Math.max(12, (int) ((double) this.height * ratio)));
    }

    private int thumbTop() {
        if (!this.needBar()) {
            return this.y;
        }
        double travel = (double) this.height - (double) this.thumbHeight();
        double ratio = this.maxScroll() <= 0.0D ? 0.0D : this.scroll / this.maxScroll();
        return this.y + (int) Math.round(travel * ratio);
    }

    private boolean inside(double mouseX, double mouseY) {
        return mouseX >= (double) this.x && mouseX < (double) (this.x + this.width)
                && mouseY >= (double) this.y && mouseY < (double) (this.y + this.height);
    }

    public int rowAt(double mouseX, double mouseY) {
        if (!this.inside(mouseX, mouseY) || this.needBar() && mouseX >= (double) this.trackLeft()) {
            return -1;
        }
        int index = (int) Math.floor((mouseY - (double) this.y - 2.0D + this.scroll) / (double) this.rowHeight);
        return index >= 0 && index < this.items.size() ? index : -1;
    }

    private boolean rowEnabled(int index) {
        return this.enabledFlags != null && index >= 0 && index < this.enabledFlags.length && this.enabledFlags[index];
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || !this.inside(mouseX, mouseY)) {
            return false;
        }
        if (this.needBar() && mouseX >= (double) this.trackLeft()) {
            int top = this.thumbTop();
            if (mouseY >= (double) top && mouseY < (double) (top + this.thumbHeight())) {
                this.draggingThumb = true;
                this.dragOffset = mouseY - (double) top;
            } else {
                double travel = (double) this.height - (double) this.thumbHeight();
                double ratio = travel <= 0.0D ? 0.0D
                        : (mouseY - (double) this.y - (double) this.thumbHeight() / 2.0D) / travel;
                this.scroll = this.clampScroll(ratio * this.maxScroll());
            }
            return true;
        }
        int index = this.rowAt(mouseX, mouseY);
        if (index >= 0) {
            if (this.rowEnabled(index) && this.onPick != null) {
                this.onPick.accept(index);
            }
            return true;
        }
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button) {
        if (this.draggingThumb && button == 0) {
            double travel = (double) this.height - (double) this.thumbHeight();
            if (travel <= 0.0D) {
                return true;
            }
            double ratio = (mouseY - (double) this.y - this.dragOffset) / travel;
            this.scroll = this.clampScroll(ratio * this.maxScroll());
            return true;
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.draggingThumb && button == 0) {
            this.draggingThumb = false;
            return true;
        }
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!this.inside(mouseX, mouseY)) {
            return false;
        }
        this.scroll = this.clampScroll(this.scroll - delta * 3.0D * (double) this.rowHeight);
        return true;
    }

    public void updateHover(double mouseX, double mouseY) {
        this.hovered = this.rowAt(mouseX, mouseY);
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, 0x80000000);
        graphics.enableScissor(this.x, this.y, this.x + this.width, this.y + this.height);
        try {
            int contentRight = this.needBar() ? this.trackLeft() : this.x + this.width;
            if (this.items.isEmpty()) {
                graphics.drawString(this.font, this.fitText(this.emptyText, Math.max(10, contentRight - this.x - 6)),
                        this.x + 4, this.y + 4, -8355712, false);
            } else {
                int first = Math.max(0, (int) Math.floor(this.scroll / (double) this.rowHeight));
                int last = Math.min(this.items.size() - 1,
                        (int) Math.floor((this.scroll + (double) this.height) / (double) this.rowHeight));
                for (int i = first; i <= last; i++) {
                    int rowTop = this.y + 2 + (int) ((double) (i * this.rowHeight) - this.scroll);
                    int rowBottom = rowTop + this.rowHeight;
                    if (rowBottom <= this.y || rowTop >= this.y + this.height) {
                        continue;
                    }
                    T item = this.items.get(i);
                    boolean isSelected = this.selected != null && Boolean.TRUE.equals(this.selected.apply(item));
                    boolean isHovered = i == this.hovered && this.rowEnabled(i);
                    if (this.renderer != null) {
                        this.renderer.render(graphics, item, this.x + 1, rowTop, contentRight - this.x - 2,
                                this.rowHeight, isSelected, isHovered);
                        continue;
                    }
                    if (isHovered || isSelected) {
                        graphics.fill(this.x + 1, Math.max(rowTop, this.y), contentRight - 1,
                                Math.min(rowBottom, this.y + this.height), isSelected ? 0x8040A040 : 0x30FFFFFF);
                    }
                    Component text = Component.literal(isSelected ? "✔ " : "  ").append(this.label.apply(item));
                    int textColor = this.color != null ? this.color.apply(item) : -1777938;
                    graphics.drawString(this.font, this.fitText(text, contentRight - this.x - 6),
                            this.x + 4, rowTop + 3, textColor, false);
                }
            }
        } finally {
            graphics.disableScissor();
        }
        this.drawScrollBar(graphics);
    }

    private void drawScrollBar(GuiGraphics graphics) {
        if (!this.needBar()) {
            return;
        }
        int top = this.thumbTop();
        int thumbH = this.thumbHeight();
        graphics.fill(this.trackLeft(), this.y, this.trackRight(), this.y + this.height, 0x60000000);
        int thumbColor = this.draggingThumb ? -5209857 : -7708720;
        graphics.fill(this.trackLeft(), top, this.trackRight(), top + thumbH, thumbColor);
    }

    private Component fitText(Component text, int maxWidth) {
        if (maxWidth <= 0) {
            return Component.empty();
        }
        if (this.font.width(text) <= maxWidth) {
            return text;
        }
        String full = text.getString();
        int ellipsisWidth = this.font.width("..");
        StringBuilder trimmed = new StringBuilder();
        int used = 0;
        for (int i = 0; i < full.length() && used + this.font.width(String.valueOf(full.charAt(i))) <= maxWidth - ellipsisWidth; i++) {
            trimmed.append(full.charAt(i));
            used += this.font.width(String.valueOf(full.charAt(i)));
        }
        return Component.literal(trimmed + "..");
    }

    @FunctionalInterface
    public interface RowRenderer<T> {
        void render(GuiGraphics graphics, T item, int x, int y, int width, int height, boolean selected, boolean hovered);
    }
}
