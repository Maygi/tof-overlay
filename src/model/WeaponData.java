package model;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class WeaponData {

    public static final int ELEMENT_VOLT = 0;
    public static final int ELEMENT_FIRE = 1;
    public static final int ELEMENT_ICE = 2;
    public static final int ELEMENT_PHYS = 3;
    public static final int ELEMENT_ABER = 4;

    public static final Color COLOR_VOLT = new Color(227, 117, 255);
    public static final Color COLOR_FIRE = new Color(255, 90, 61);
    public static final Color COLOR_ICE = new Color(158, 240, 255);
    public static final Color COLOR_PHYS = new Color(255, 249, 230);
    public static final Color COLOR_ABER = new Color(201, 255, 186);

    public static Color[] elementColors;

    private int element;
    private int advancement;
    private String name;

    private static final Map<String, Integer> elementMap = new HashMap<>();

    /**
     * A map of weapon names to a list of "buff time" per advanacement.
     */
    private static final Map<String, Integer[]> extraMap = new HashMap<>();

    static {

        elementColors = new Color[]{COLOR_VOLT, COLOR_FIRE, COLOR_ICE, COLOR_PHYS, COLOR_ABER};
        elementMap.put("Ruby", ELEMENT_FIRE);
        elementMap.put("King", ELEMENT_FIRE);
        elementMap.put("Cobalt", ELEMENT_FIRE);
        elementMap.put("Zero", ELEMENT_FIRE);
        elementMap.put("Annabella", ELEMENT_FIRE);

        elementMap.put("Saki", ELEMENT_ICE);
        elementMap.put("Frigg", ELEMENT_ICE);
        elementMap.put("Coco", ELEMENT_ICE);
        elementMap.put("Tsubasa", ELEMENT_ICE);
        elementMap.put("Meryl", ELEMENT_ICE);
        elementMap.put("Alyss", ELEMENT_ICE);

        elementMap.put("Nemesis", ELEMENT_VOLT);
        elementMap.put("Samir", ELEMENT_VOLT);
        elementMap.put("Crow", ELEMENT_VOLT);
        elementMap.put("Tian", ELEMENT_VOLT);

        elementMap.put("Claudia", ELEMENT_PHYS);
        elementMap.put("Shiro", ELEMENT_PHYS);
        elementMap.put("Lyra", ELEMENT_PHYS);

        elementMap.put("Lin", ELEMENT_ABER);

        extraMap.put("Lin", new Integer[]{15, 15, 15, 15, 20, 20, 20});
    }

    public static Color getColor(String name) {
        return getColor(elementMap.get(name));
    }

    public WeaponData(String name, int advancement) {
        this.name = name;
        this.advancement = advancement;
        this.element = elementMap.get(name);
    }

    public int getElement() {
        return element;
    }

    public String getName() {
        return name;
    }

    public int getAdvancement() {
        return advancement;
    }

    public int getExtra() {
        return extraMap.get(name)[Math.min(6, advancement)];
    }

    public static String getFullChargeIcon(int element) {
        switch(element) {
            case WeaponData.ELEMENT_ICE:
                return "images/tableicons/frostbite.png";
            case WeaponData.ELEMENT_VOLT:
                return "images/tableicons/electrocute.png";
            case WeaponData.ELEMENT_FIRE:
                return "images/tableicons/burn.png";
            case WeaponData.ELEMENT_PHYS:
                return "images/tableicons/grievous.png";
        }
        return "images/tableicons/aberration.png";
    }
    public static Color getColor(int element) {
        switch(element) {
            case WeaponData.ELEMENT_ICE:
                return COLOR_ICE;
            case WeaponData.ELEMENT_VOLT:
                return COLOR_VOLT;
            case WeaponData.ELEMENT_FIRE:
                return COLOR_FIRE;
            case WeaponData.ELEMENT_PHYS:
                return COLOR_PHYS;
        }
        return COLOR_ABER;
    }
}
