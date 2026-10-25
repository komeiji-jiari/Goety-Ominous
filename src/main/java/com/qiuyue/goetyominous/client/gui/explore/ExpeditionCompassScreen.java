package com.qiuyue.goetyominous.client.gui.explore;

import com.qiuyue.goetyominous.common.explore.ExpeditionKind;
import com.qiuyue.goetyominous.common.explore.ExpeditionSnapshot;
import com.qiuyue.goetyominous.common.explore.ExpeditionTuning;
import com.qiuyue.goetyominous.common.explore.TravelerStatus;
import com.qiuyue.goetyominous.common.explore.network.ExpeditionClaimPacket;
import com.qiuyue.goetyominous.common.explore.network.ExpeditionGatherPacket;
import com.qiuyue.goetyominous.common.explore.network.ExpeditionRecallPacket;
import com.qiuyue.goetyominous.common.explore.network.ExpeditionRequestPacket;
import com.qiuyue.goetyominous.common.explore.network.ExpeditionStartPacket;
import com.qiuyue.goetyominous.common.explore.network.ExpeditionViewPacket;
import com.qiuyue.goetyominous.common.items.explore.ExpeditionCompassItem;
import com.qiuyue.goetyominous.common.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class ExpeditionCompassScreen extends Screen {
    private static final int TEXT = -1777938;
    private static final int MUTED = -5593924;
    private static final int ACCENT = -4677142;
    private static final int PANEL = -266857439;
    private static final int CARD = -14278607;
    private static final int BORDER = -15725033;
    private static final int TRACK = -15264993;
    private static final int HEARTBEAT_TICKS = 40;
    private static final int TIMEOUT_TICKS = 100;

    private ExpeditionSnapshot snapshot;
    private List<ExpeditionSnapshot.TravelerEntry> travelers;
    private List<ExpeditionSnapshot.ExpeditionInfo> expeditions;
    private final Set<UUID> selectedTravelers = new LinkedHashSet<>();
    private ResourceLocation dimension;
    private ResourceLocation biome;
    private ResourceLocation ruin;
    private ExpeditionKind kind = ExpeditionKind.BIOME;
    private boolean management;
    private boolean dimensionsOpen;
    private boolean pending;
    private int teamFilter;
    private String query = "";
    private int age;
    private int progressAge;
    private int pendingSince;
    private Component status = Component.empty();
    private int x;
    private int y;
    private int w;
    private int h;
    private int left;
    private int leftW;
    private int right;
    private int rightW;
    private int listTop;
    private int summaryY;
    private int buttonY;
    private ScrollList<ExpeditionSnapshot.DimensionEntry> dimensionList;
    private ScrollList<ExpeditionSnapshot.BiomeEntry> biomeList;
    private ScrollList<ExpeditionSnapshot.RuinEntry> ruinList;
    private ScrollList<ExpeditionSnapshot.TravelerEntry> travelerList;
    private ScrollList<ExpeditionSnapshot.ExpeditionInfo> teamList;
    private EditBox search;
    private Button dispatchTab;
    private Button teamsTab;
    private Button dimensionButton;
    private Button modeButton;
    private Button selectButton;
    private Button clearButton;
    private Button startButton;
    private Button filterButton;
    private Button gatherButton;
    private Button doneButton;

    private ExpeditionCompassScreen(ExpeditionSnapshot snapshot) {
        super(Component.translatable("screen.goetyominous.expedition.title"));
        this.snapshot = snapshot;
        this.travelers = snapshot.travelers();
        this.expeditions = snapshot.expeditions();
        this.dimension = snapshot.dimension();
    }

    public static void open(ExpeditionSnapshot snapshot) {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        if (screen instanceof ExpeditionCompassScreen compassScreen) {
            compassScreen.update(snapshot);
            return;
        }
        if (snapshot.authoritative()) {
            minecraft.setScreen(new ExpeditionCompassScreen(snapshot));
        }
    }

    public static void progress(List<ExpeditionSnapshot.TravelerEntry> travelers,
                                List<ExpeditionSnapshot.ExpeditionInfo> expeditions) {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof ExpeditionCompassScreen compassScreen) {
            compassScreen.updateDynamic(travelers, expeditions);
        }
    }

    @Override
    protected void init() {
        this.w = Math.min(this.width - 12, 740);
        this.h = Math.min(this.height - 12, 430);
        this.x = (this.width - this.w) / 2;
        this.y = (this.height - this.h) / 2;
        this.left = x + 8;
        this.leftW = (w - 24) / 2;
        this.right = left + leftW + 8;
        this.rightW = w - 16 - leftW - 8;
        this.listTop = y + 94;
        this.summaryY = y + h - 58;
        this.buttonY = y + h - 24;
        clearWidgets();
        int tabW = (w - 24) / 2;
        dispatchTab = button(Component.translatable("screen.goetyominous.expedition.tab.dispatch"),
                left, y + 22, tabW, 18, b -> switchTab(false));
        teamsTab = button(Component.translatable("screen.goetyominous.expedition.tab.teams"),
                left + tabW + 8, y + 22, tabW, 18, b -> switchTab(true));
        dimensionButton = button(Component.empty(), left, y + 48, leftW, 18, b -> {
            if (!pending) {
                dimensionsOpen = !dimensionsOpen;
                refreshLists();
                updateWidgets();
            }
        });
        int modeW = Math.min(66, Math.max(20, leftW / 2));
        modeButton = button(Component.empty(), left, y + 70, modeW, 18, b -> {
            kind = kind == ExpeditionKind.BIOME ? ExpeditionKind.RUIN : ExpeditionKind.BIOME;
            dimensionsOpen = false;
            refreshLists();
            updateWidgets();
        });
        search = new EditBox(this.font, left + modeW + 4, y + 70, Math.max(20, leftW - modeW - 4), 18,
                Component.translatable("screen.goetyominous.expedition.search"));
        search.setMaxLength(80);
        search.setValue(query);
        search.setHint(Component.translatable("screen.goetyominous.expedition.search"));
        search.setResponder(value -> {
            query = value;
            dimensionsOpen = false;
            refreshLists();
            updateWidgets();
        });
        addRenderableWidget(search);
        int half = (rightW - 4) / 2;
        selectButton = button(Component.translatable("screen.goetyominous.expedition.selectFiltered"),
                right, y + 70, half, 18, b -> selectVisible());
        clearButton = button(Component.translatable("screen.goetyominous.expedition.clear"),
                right + half + 4, y + 70, rightW - half - 4, 18, b -> {
                    selectedTravelers.clear();
                    refreshLists();
                    updateWidgets();
                });
        startButton = button(Component.translatable("screen.goetyominous.expedition.start"),
                left, buttonY, 100, 18, b -> start());
        int teamButtonW = Math.min(150, Math.max(90, (w - 40) / 3));
        filterButton = button(Component.empty(), left, y + 48, teamButtonW, 18, b -> {
            teamFilter = (teamFilter + 1) % 3;
            refreshLists();
            updateWidgets();
        });
        gatherButton = button(Component.translatable("screen.goetyominous.expedition.gather"),
                left + teamButtonW + 4, y + 48, teamButtonW, 18, b -> gatherTravelers());
        doneButton = button(Component.translatable("gui.done"), x + w - 72, buttonY, 64, 18, b -> onClose());
        refreshLists();
        updateWidgets();
    }

    private Button button(Component label, int bx, int by, int bw, int bh, Button.OnPress press) {
        Button button = Button.builder(label, press).bounds(bx, by, bw, bh).build();
        addRenderableWidget(button);
        return button;
    }

    private void updateWidgets() {
        if (dispatchTab == null) {
            return;
        }
        dispatchTab.setMessage(Component.translatable("screen.goetyominous.expedition.tab.dispatch"));
        teamsTab.setMessage(Component.translatable("screen.goetyominous.expedition.tab.teams"));
        dimensionButton.visible = !management;
        dimensionButton.active = !pending && unlockedDimensions().size() > 1;
        dimensionButton.setMessage(fit(Component.translatable("screen.goetyominous.expedition.destination",
                dimensionName(dimension)), leftW - 8));
        modeButton.visible = !management;
        modeButton.active = !pending;
        modeButton.setMessage(Component.translatable(kind == ExpeditionKind.BIOME
                ? "screen.goetyominous.expedition.mode.biome" : "screen.goetyominous.expedition.mode.ruin"));
        search.visible = !management;
        search.setEditable(!pending);
        if (management) {
            search.setFocused(false);
        }
        selectButton.visible = !management;
        selectButton.active = !pending && hasReadyTraveler();
        selectButton.setMessage(Component.translatable("screen.goetyominous.expedition.selectFiltered"));
        clearButton.visible = !management;
        clearButton.active = !pending && !selectedTravelers.isEmpty();
        clearButton.setMessage(Component.translatable("screen.goetyominous.expedition.clear"));
        startButton.visible = !management;
        startButton.active = !pending && validate() == null;
        startButton.setMessage(Component.translatable("screen.goetyominous.expedition.start"));
        filterButton.visible = management;
        filterButton.active = !pending;
        filterButton.setMessage(Component.translatable("screen.goetyominous.expedition.filter." + teamFilter));
        gatherButton.visible = management;
        gatherButton.active = !pending;
        gatherButton.setMessage(Component.translatable("screen.goetyominous.expedition.gather"));
        doneButton.active = true;
    }

    private void switchTab(boolean value) {
        management = value;
        dimensionsOpen = false;
        setFocused(null);
        refreshLists();
        updateWidgets();
    }

    private List<ResourceLocation> unlockedDimensions() {
        return snapshot.dimensions().stream().filter(ExpeditionSnapshot.DimensionEntry::unlocked)
                .map(ExpeditionSnapshot.DimensionEntry::id).toList();
    }

    private void update(ExpeditionSnapshot fresh) {
        if (dimension != null && !dimension.equals(fresh.dimension())) {
            return;
        }
        this.snapshot = fresh;
        this.dimension = fresh.dimension();
        this.pending = false;
        this.status = fresh.feedback();
        if (biome != null && fresh.biomes().stream().noneMatch(entry -> entry.id().equals(biome))) {
            biome = null;
        }
        if (ruin != null && fresh.ruins().stream().noneMatch(entry -> entry.id().equals(ruin) && entry.visited())) {
            ruin = null;
        }
        updateDynamic(fresh.travelers(), fresh.expeditions());
    }

    private void updateDynamic(List<ExpeditionSnapshot.TravelerEntry> freshTravelers,
                               List<ExpeditionSnapshot.ExpeditionInfo> freshExpeditions) {
        this.travelers = freshTravelers;
        this.expeditions = freshExpeditions;
        this.progressAge = age;
        selectedTravelers.removeIf(id -> freshTravelers.stream()
                .noneMatch(entry -> entry.id().equals(id) && entry.status() == TravelerStatus.READY));
        refreshLists();
        updateWidgets();
    }

    private void refreshLists() {
        if (this.font == null || snapshot == null) {
            return;
        }
        double dimensionScroll = dimensionList == null ? 0.0D : dimensionList.scroll();
        double biomeScroll = biomeList == null ? 0.0D : biomeList.scroll();
        double ruinScroll = ruinList == null ? 0.0D : ruinList.scroll();
        double travelerScroll = travelerList == null ? 0.0D : travelerList.scroll();
        double teamScroll = teamList == null ? 0.0D : teamList.scroll();
        String find = query.strip().toLowerCase(Locale.ROOT);
        List<ExpeditionSnapshot.BiomeEntry> shownBiomes = snapshot.biomes().stream()
                .filter(entry -> matches(name(entry.id(), "biome"), entry.id(), find)).toList();
        List<ExpeditionSnapshot.RuinEntry> shownRuins = snapshot.ruins().stream()
                .filter(entry -> matches(name(entry.id(), "structure"), entry.id(), find)).toList();
        List<ExpeditionSnapshot.ExpeditionInfo> shownTeams = filteredTeams();
        int top = management ? y + 72 : listTop;
        int bottom = management ? y + h - 36 : summaryY - 4;
        int listH = Math.max(10, bottom - top);
        dimensionList = new ScrollList<>(font, snapshot.dimensions(),
                entry -> dimensionName(entry.id()),
                entry -> entry.unlocked() ? TEXT : MUTED,
                entry -> entry.id().equals(dimension),
                flags(snapshot.dimensions(), entry -> entry.unlocked() && !pending),
                index -> pickDimension(snapshot.dimensions().get(index).id()),
                Component.translatable("screen.goetyominous.expedition.empty.dimension"), 14);
        dimensionList.setBounds(left, top, leftW, listH);
        dimensionList.setScroll(dimensionScroll);
        biomeList = new ScrollList<>(font, shownBiomes,
                entry -> targetLabel(name(entry.id(), "biome"), entry.hasLoot(), true),
                entry -> TEXT,
                entry -> entry.id().equals(biome),
                flags(shownBiomes, entry -> !pending),
                index -> {
                    biome = shownBiomes.get(index).id();
                    kind = ExpeditionKind.BIOME;
                    dimensionsOpen = false;
                    refreshLists();
                    updateWidgets();
                },
                Component.translatable("screen.goetyominous.expedition.empty.biome"), 14);
        biomeList.setBounds(left, top, leftW, listH);
        biomeList.setScroll(biomeScroll);
        ruinList = new ScrollList<>(font, shownRuins,
                entry -> targetLabel(name(entry.id(), "structure"), entry.visited(), false),
                entry -> entry.visited() ? TEXT : MUTED,
                entry -> entry.id().equals(ruin),
                flags(shownRuins, entry -> entry.visited() && !pending),
                index -> {
                    ruin = shownRuins.get(index).id();
                    kind = ExpeditionKind.RUIN;
                    dimensionsOpen = false;
                    refreshLists();
                    updateWidgets();
                },
                Component.translatable("screen.goetyominous.expedition.empty.ruin"), 14);
        ruinList.setBounds(left, top, leftW, listH);
        ruinList.setScroll(ruinScroll);
        travelerList = new ScrollList<>(font, travelers,
                this::travelerLabel,
                entry -> entry.status() == TravelerStatus.READY ? TEXT : MUTED,
                entry -> selectedTravelers.contains(entry.id()),
                flags(travelers, entry -> entry.status() == TravelerStatus.READY && !pending),
                index -> toggleTraveler(travelers.get(index)),
                Component.translatable("screen.goetyominous.expedition.empty.traveler"), 14);
        travelerList.setBounds(right, listTop, rightW, Math.max(10, summaryY - 4 - listTop));
        travelerList.setScroll(travelerScroll);
        boolean[] teamFlags = new boolean[shownTeams.size()];
        for (int i = 0; i < teamFlags.length; i++) {
            teamFlags[i] = !pending;
        }
        teamList = new ScrollList<>(font, shownTeams, entry -> Component.empty(), null, null, teamFlags,
                null,
                Component.translatable("screen.goetyominous.expedition.empty.teams"), 48,
                this::renderTeamCard);
        teamList.setBounds(left, y + 72, w - 16, Math.max(10, h - 108));
        teamList.setScroll(teamScroll);
    }

    private <T> boolean[] flags(List<T> items, java.util.function.Predicate<T> allowed) {
        boolean[] flags = new boolean[items.size()];
        for (int i = 0; i < flags.length; i++) {
            flags[i] = allowed.test(items.get(i));
        }
        return flags;
    }

    private boolean matches(Component name, ResourceLocation id, String find) {
        return find.isEmpty() || name.getString().toLowerCase(Locale.ROOT).contains(find)
                || id.toString().toLowerCase(Locale.ROOT).contains(find);
    }

    private List<ExpeditionSnapshot.ExpeditionInfo> filteredTeams() {
        return expeditions.stream().filter(team -> switch (teamFilter) {
            case 1 -> team.settled() || team.delivering();
            case 2 -> !team.settled() && !team.delivering();
            default -> true;
        }).toList();
    }

    private boolean hasReadyTraveler() {
        return travelers.stream().anyMatch(entry -> entry.status() == TravelerStatus.READY);
    }

    private void pickDimension(ResourceLocation selected) {
        if (pending) {
            return;
        }
        dimensionsOpen = false;
        dimension = selected;
        biome = null;
        ruin = null;
        pending = true;
        pendingSince = age;
        status = Component.translatable("screen.goetyominous.expedition.loading");
        ModNetwork.CHANNEL.sendToServer(new ExpeditionRequestPacket(dimension));
        refreshLists();
        updateWidgets();
    }

    private void toggleTraveler(ExpeditionSnapshot.TravelerEntry entry) {
        if (pending) {
            return;
        }
        if (!selectedTravelers.remove(entry.id())) {
            if (selectedTravelers.size() >= snapshot.tuning().effectiveMaxTravelers()) {
                status = Component.translatable("screen.goetyominous.expedition.tooMany",
                        snapshot.tuning().effectiveMaxTravelers());
            } else if (entry.status() == TravelerStatus.READY) {
                selectedTravelers.add(entry.id());
            }
        }
        refreshLists();
        updateWidgets();
    }

    private void selectVisible() {
        for (ExpeditionSnapshot.TravelerEntry entry : travelers) {
            if (selectedTravelers.size() >= snapshot.tuning().effectiveMaxTravelers()) {
                break;
            }
            if (entry.status() == TravelerStatus.READY) {
                selectedTravelers.add(entry.id());
            }
        }
        refreshLists();
        updateWidgets();
    }

    private Component validate() {
        if (snapshot == null || dimension == null || !dimension.equals(snapshot.dimension())) {
            return Component.translatable("screen.goetyominous.expedition.loading");
        }
        if (kind == ExpeditionKind.BIOME && biome == null) {
            return Component.translatable("screen.goetyominous.expedition.needBiome");
        }
        if (kind == ExpeditionKind.RUIN && ruin == null) {
            return Component.translatable("screen.goetyominous.expedition.needRuin");
        }
        ExpeditionTuning tuning = snapshot.tuning();
        if (selectedTravelers.size() < tuning.minTravelers()) {
            return Component.translatable("screen.goetyominous.expedition.needTravelers", tuning.minTravelers());
        }
        if (selectedTravelers.size() > tuning.effectiveMaxTravelers()) {
            return Component.translatable("screen.goetyominous.expedition.tooMany", tuning.effectiveMaxTravelers());
        }
        if (minecraft == null || minecraft.player == null
                || !(minecraft.player.getItemInHand(snapshot.hand()).getItem() instanceof ExpeditionCompassItem)) {
            return Component.translatable("screen.goetyominous.expedition.needCompass");
        }
        return null;
    }

    private void start() {
        Component problem = validate();
        if (pending) {
            return;
        }
        if (problem != null) {
            status = problem;
            updateWidgets();
            return;
        }
        pending = true;
        pendingSince = age;
        status = Component.translatable("screen.goetyominous.expedition.loading");
        ModNetwork.CHANNEL.sendToServer(new ExpeditionStartPacket(
                kind,
                dimension,
                kind == ExpeditionKind.BIOME ? biome : null,
                kind == ExpeditionKind.RUIN ? ruin : null,
                snapshot.hand(),
                new ArrayList<>(selectedTravelers)
        ));
        refreshLists();
        updateWidgets();
    }

    private void gatherTravelers() {
        if (pending) {
            return;
        }
        pending = true;
        pendingSince = age;
        status = Component.translatable("screen.goetyominous.expedition.loading");
        ModNetwork.CHANNEL.sendToServer(new ExpeditionGatherPacket());
        refreshLists();
        updateWidgets();
    }

    private void actOnTeam(ExpeditionSnapshot.ExpeditionInfo team) {
        if (pending || team == null) {
            return;
        }
        pending = true;
        pendingSince = age;
        status = Component.translatable("screen.goetyominous.expedition.loading");
        if (team.delivering() || team.settled()) {
            ModNetwork.CHANNEL.sendToServer(new ExpeditionClaimPacket(team.expeditionId()));
        } else {
            ModNetwork.CHANNEL.sendToServer(new ExpeditionRecallPacket(team.expeditionId()));
        }
        refreshLists();
        updateWidgets();
    }

    private ScrollList<?> activeTarget() {
        if (dimensionsOpen) {
            return dimensionList;
        }
        return kind == ExpeditionKind.BIOME ? biomeList : ruinList;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!management) {
            if (activeTarget() != null && activeTarget().mouseClicked(mouseX, mouseY, button)
                    || travelerList != null && travelerList.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        } else if (teamList != null) {
            if (!pending) {
                int row = teamList.rowAt(mouseX, mouseY);
                if (row >= 0 && row < teamList.items().size()) {
                    int rowTop = teamList.rowTop(row);
                    int actionX = teamList.contentRight() - 73;
                    if (mouseX >= (double) actionX && mouseX < (double) (actionX + 66)
                            && mouseY >= (double) (rowTop + 12) && mouseY < (double) (rowTop + 32)) {
                        actOnTeam(teamList.items().get(row));
                        return true;
                    }
                }
            }
            if (teamList.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!management) {
            if (activeTarget() != null && activeTarget().mouseDragged(mouseX, mouseY, button)
                    || travelerList != null && travelerList.mouseDragged(mouseX, mouseY, button)) {
                return true;
            }
        } else if (teamList != null && teamList.mouseDragged(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!management) {
            if (dimensionList != null) dimensionList.mouseReleased(mouseX, mouseY, button);
            if (biomeList != null) biomeList.mouseReleased(mouseX, mouseY, button);
            if (ruinList != null) ruinList.mouseReleased(mouseX, mouseY, button);
            if (travelerList != null) travelerList.mouseReleased(mouseX, mouseY, button);
        } else if (teamList != null) {
            teamList.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!management) {
            if (activeTarget() != null && activeTarget().mouseScrolled(mouseX, mouseY, delta)
                    || travelerList != null && travelerList.mouseScrolled(mouseX, mouseY, delta)) {
                return true;
            }
        } else if (teamList != null && teamList.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        graphics.fill(x, y, x + w, y + h, PANEL);
        graphics.fill(x, y, x + w, y + 2, ACCENT);
        graphics.drawCenteredString(this.font, this.title, x + w / 2, y + 8, TEXT);
        int tabW = (w - 24) / 2;
        int activeX = management ? left + tabW + 8 : left;
        graphics.fill(activeX, y + 40, activeX + tabW, y + 42, ACCENT);
        if (management) {
            graphics.drawString(this.font, Component.translatable("screen.goetyominous.expedition.teams",
                    expeditions.size()), x + w - 8 - this.font.width(Component.translatable(
                    "screen.goetyominous.expedition.teams", expeditions.size())), y + 50, MUTED, false);
            if (teamList != null) {
                teamList.updateHover(mouseX, mouseY);
                teamList.render(graphics, mouseX, mouseY);
            }
        } else {
            graphics.drawString(this.font, Component.translatable("screen.goetyominous.expedition.travelers",
                    selectedTravelers.size(), snapshot.tuning().effectiveMaxTravelers()), right, y + 50, MUTED, false);
            ScrollList<?> target = activeTarget();
            if (target != null) {
                target.updateHover(mouseX, mouseY);
                target.render(graphics, mouseX, mouseY);
            }
            if (travelerList != null) {
                travelerList.updateHover(mouseX, mouseY);
                travelerList.render(graphics, mouseX, mouseY);
            }
            List<Component> summary = summary();
            for (int i = 0; i < summary.size(); i++) {
                graphics.drawString(this.font, fit(summary.get(i), w - 16), left, summaryY + i * 10,
                        i == 0 ? TEXT : MUTED, false);
            }
        }
        super.render(graphics, mouseX, mouseY, partialTick);
        Component bottom = status;
        if (bottom.getString().isEmpty() && !management) {
            Component warning = warning();
            bottom = warning.getString().isEmpty() ? validate() : warning;
        }
        if (bottom != null && !bottom.getString().isEmpty()) {
            graphics.drawString(this.font, fit(bottom, w - 190), left + 108, y + h - 18, MUTED, false);
        }
        if (!management && travelerList != null && travelerList.hovered() >= 0 && travelerList.hovered() < travelers.size()) {
            ExpeditionSnapshot.TravelerEntry entry = travelers.get(travelerList.hovered());
            List<Component> tip = new ArrayList<>();
            tip.add(Component.literal(entry.name()));
            tip.add(statusText(entry.status()));
            if (entry.maxHealth() > 0.0F) {
                tip.add(Component.translatable("screen.goetyominous.expedition.health",
                        Math.round(entry.health()), Math.round(entry.maxHealth())));
            }
            graphics.renderTooltip(this.font, tip, Optional.empty(), mouseX, mouseY);
        } else if (!management && mouseX >= left && mouseX < left + w - 16
                && mouseY >= summaryY && mouseY < summaryY + 30) {
            graphics.renderTooltip(this.font, bonusSummary(), Optional.empty(), mouseX, mouseY);
        }
    }

    private List<Component> summary() {
        ExpeditionTuning tuning = snapshot.tuning();
        int count = selectedTravelers.size();
        Component target = kind == ExpeditionKind.BIOME ? name(biome, "biome") : name(ruin, "structure");
        double death = tuning.deathChanceFor(Math.max(1, count));
        double draws = (double) tuning.baseDraws()
                + (double) Math.max(0, count - tuning.minTravelers()) * tuning.extraDrawChance();
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable("screen.goetyominous.expedition.summary.target", target));
        lines.add(Component.translatable("screen.goetyominous.expedition.summary.timing",
                duration(tuning.ticks(kind)), Math.round(death * 100.0D)));
        lines.add(Component.translatable("screen.goetyominous.expedition.summary.expected",
                String.format(Locale.ROOT, "%.1f", draws), count, tuning.minTravelers()));
        return lines;
    }

    private List<Component> bonusSummary() {
        ExpeditionTuning tuning = snapshot.tuning();
        int pickaxes = 0;
        int crossbows = 0;
        for (ExpeditionSnapshot.TravelerEntry entry : travelers) {
            if (!selectedTravelers.contains(entry.id())) {
                continue;
            }
            if (entry.pickaxe()) {
                pickaxes++;
            }
            if (entry.crossbow()) {
                crossbows++;
            }
        }
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable("screen.goetyominous.expedition.summary.bonus.ore",
                tuning.baseOres() + pickaxes * tuning.oresPerPickaxe()));
        lines.add(Component.translatable("screen.goetyominous.expedition.summary.bonus.mob",
                tuning.baseMobDraws() + crossbows * tuning.mobDrawsPerCrossbow()));
        return lines;
    }

    private Component warning() {
        if (kind == ExpeditionKind.BIOME && biome != null) {
            boolean loot = snapshot.biomes().stream()
                    .anyMatch(entry -> entry.id().equals(biome) && entry.hasLoot());
            if (!loot) {
                return Component.translatable("screen.goetyominous.expedition.noStructureLoot");
            }
        }
        if (!snapshot.oresAvailable() && !snapshot.mobsAvailable()) {
            return Component.translatable("screen.goetyominous.expedition.noEnvironment");
        }
        return Component.empty();
    }

    private void renderTeamCard(GuiGraphics graphics, ExpeditionSnapshot.ExpeditionInfo team,
                                int cx, int cy, int cw, int ch, boolean selected, boolean hovered) {
        graphics.fill(cx, cy + 1, cx + cw, cy + ch - 2, CARD);
        graphics.fill(cx, cy + 1, cx + cw, cy + 2, hovered ? ACCENT : BORDER);
        Component target = team.kind() == ExpeditionKind.BIOME ? name(team.biome(), "biome")
                : name(team.structure(), "structure");
        graphics.drawString(this.font, fit(target, cw - 82), cx + 5, cy + 5, TEXT, false);
        Component info = Component.translatable("screen.goetyominous.expedition.teamInfo",
                dimensionName(team.dimension()), team.totalCount(), teamState(team));
        graphics.drawString(this.font, fit(info, cw - 82), cx + 5, cy + 16, MUTED, false);
        long remaining = Math.max(0L, team.remainingTicks() - (long) (age - progressAge));
        double progress = team.settled() || team.delivering() ? 1.0D
                : 1.0D - (double) remaining / (double) Math.max(1L, team.durationTicks());
        int barWidth = Math.max(10, cw - 82);
        graphics.fill(cx + 5, cy + 30, cx + 5 + barWidth, cy + 33, TRACK);
        graphics.fill(cx + 5, cy + 30,
                cx + 5 + (int) (barWidth * Math.max(0.0D, Math.min(1.0D, progress))), cy + 33, ACCENT);
        int actionX = cx + cw - 72;
        graphics.fill(actionX, cy + 12, actionX + 66, cy + 32, pending ? -13356996 : (hovered ? -9350252 : -11650202));
        Component action = teamActionLabel(team);
        graphics.drawCenteredString(this.font, fit(action, 60), actionX + 33, cy + 18, pending ? MUTED : TEXT);
    }

    private Component teamActionLabel(ExpeditionSnapshot.ExpeditionInfo team) {
        if (team == null) {
            return Component.translatable("screen.goetyominous.expedition.retry");
        }
        if (team.delivering()) {
            return Component.translatable("screen.goetyominous.expedition.retry");
        }
        if (team.settled()) {
            return Component.translatable("screen.goetyominous.expedition.claim");
        }
        return Component.translatable("screen.goetyominous.expedition.recall");
    }

    private Component teamState(ExpeditionSnapshot.ExpeditionInfo info) {
        if (info.delivering()) {
            return Component.translatable("screen.goetyominous.expedition.deliveryPending");
        }
        if (info.settled()) {
            return Component.translatable("screen.goetyominous.expedition.ready");
        }
        long remaining = Math.max(0L, info.remainingTicks() - (long) (age - progressAge));
        if (remaining == 0L) {
            return Component.translatable("screen.goetyominous.expedition.settling");
        }
        return duration(remaining);
    }

    private Component travelerLabel(ExpeditionSnapshot.TravelerEntry entry) {
        StringBuilder label = new StringBuilder(entry.name()).append(" · ").append(statusName(entry.status()));
        if (entry.pickaxe()) {
            label.append(" · ").append(marker("pickaxe"));
        }
        if (entry.crossbow()) {
            label.append(" · ").append(marker("crossbow"));
        }
        return Component.literal(label.toString());
    }

    private Component targetLabel(Component name, boolean positive, boolean biome) {
        return Component.literal(name.getString() + (positive ? "" : " · "
                + marker(biome ? "noStructureLoot" : "unvisited")));
    }

    private String marker(String key) {
        return Component.translatable("screen.goetyominous.expedition.marker." + key).getString();
    }

    private Component statusText(TravelerStatus status) {
        return Component.translatable("screen.goetyominous.expedition.status."
                + status.name().toLowerCase(Locale.ROOT));
    }

    private String statusName(TravelerStatus status) {
        return statusText(status).getString();
    }

    private Component name(ResourceLocation id, String prefix) {
        if (id == null) {
            return Component.literal("-");
        }
        String key = id.toLanguageKey(prefix);
        if (Language.getInstance().has(key)) {
            return Component.translatable(key);
        }
        String path = id.getPath();
        int slash = path.lastIndexOf('/');
        if (slash >= 0) {
            path = path.substring(slash + 1);
        }
        StringBuilder out = new StringBuilder();
        for (String word : path.split("_")) {
            if (word.isEmpty()) {
                continue;
            }
            if (out.length() > 0) {
                out.append(' ');
            }
            out.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return Component.literal(out.length() == 0 ? id.toString() : out.toString());
    }

    private Component dimensionName(ResourceLocation id) {
        return name(id, "dimension");
    }

    private Component duration(long ticks) {
        long seconds = Math.max(0L, (ticks + 19L) / 20L);
        return Component.translatable("screen.goetyominous.expedition.duration",
                seconds / 60L, String.format(Locale.ROOT, "%02d", seconds % 60L));
    }

    private Component fit(Component text, int maxWidth) {
        if (text == null) {
            return Component.empty();
        }
        if (this.font.width(text) <= maxWidth) {
            return text;
        }
        String cut = this.font.plainSubstrByWidth(text.getString(), Math.max(0, maxWidth - this.font.width("…")));
        return Component.literal(cut + "…");
    }

    public void tick() {
        super.tick();
        age++;
        if (minecraft == null || minecraft.player == null || !minecraft.player.isAlive()) {
            onClose();
            return;
        }
        if (age % HEARTBEAT_TICKS == 0 && minecraft.getConnection() != null) {
            ModNetwork.CHANNEL.sendToServer(new ExpeditionViewPacket(true));
        }
        if (pending && age - pendingSince > TIMEOUT_TICKS) {
            pending = false;
            status = Component.translatable("screen.goetyominous.expedition.timeout");
            refreshLists();
            updateWidgets();
        }
    }

    public void onClose() {
        if (minecraft != null && minecraft.getConnection() != null) {
            ModNetwork.CHANNEL.sendToServer(new ExpeditionViewPacket(false));
        }
        super.onClose();
    }

    public boolean isPauseScreen() {
        return false;
    }
}
