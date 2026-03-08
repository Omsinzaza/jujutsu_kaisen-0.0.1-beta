package net.jujutsukaisen.capability;

import net.jujutsukaisen.Config;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public enum TechniqueMove {
    CURSED_BLAST("Cursed Blast", "CB", null),

    LIMITLESS_INFINITY_TOGGLE("Infinity", "INF",
            "jujutsukaisen:textures/gui/icons/icon_infinity.png"),
    LIMITLESS_INFINITY_CRUSH("Infinity Crush", "IC",
            "jujutsukaisen:textures/gui/icons/icon_infinity.png"),
    LIMITLESS_BLUE("Blue", "BLU",
            "jujutsukaisen:textures/gui/icons/icon_blue.png"),
    LIMITLESS_RED("Red", "RED",
            "jujutsukaisen:textures/gui/icons/icon_red.png"),
    LIMITLESS_HOLLOW_PURPLE("Hollow Purple", "HP",
            "jujutsukaisen:textures/gui/icons/icon_purple.png"),

    TEN_SHADOWS_DIVINE_DOGS("Divine Dogs", "DOG",
            "jujutsukaisen:textures/gui/icons/icon_divine_dogs.png");

    private final String displayName;
    private final String shortLabel;
    private final ResourceLocation icon;

    TechniqueMove(String displayName, String shortLabel, String iconPath) {
        this.displayName = displayName;
        this.shortLabel = shortLabel;
        this.icon = iconPath == null ? null : ResourceLocation.parse(iconPath);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getShortLabel() {
        return shortLabel;
    }

    public int getEnergyCost() {
        return Config.getMoveCost(this);
    }

    public int getCooldownTicks() {
        return Config.getMoveCooldownTicks(this);
    }

    public ResourceLocation getIcon() {
        return icon;
    }

    public static List<TechniqueMove> getMovesForTechnique(InnateTechnique technique) {
        List<TechniqueMove> moves = new ArrayList<>();

        moves.add(CURSED_BLAST);

        if (technique == InnateTechnique.LIMITLESS) {
            moves.add(LIMITLESS_INFINITY_TOGGLE);
            moves.add(LIMITLESS_INFINITY_CRUSH);
            moves.add(LIMITLESS_BLUE);
            moves.add(LIMITLESS_RED);
            moves.add(LIMITLESS_HOLLOW_PURPLE);
        } else if (technique == InnateTechnique.TEN_SHADOWS) {
            moves.add(TEN_SHADOWS_DIVINE_DOGS);
        }

        return moves;
    }
}
