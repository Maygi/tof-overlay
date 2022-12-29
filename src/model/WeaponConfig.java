package model;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Handles the configuration of Weapon Data.
 */
public class WeaponConfig {

    private static final Map<String, WeaponData> data = new HashMap<>();

    private static Properties props;

    private static String claudia4PcHolder;
    private static int claudia4PcStars;

    static {
        props = new Properties();
        try {
            props.load(new FileReader("weapon.properties"));
            for (Map.Entry<Object, Object> e : props.entrySet()) {
                String name = (String) e.getKey();
                if (name.equals("Claudia4pc")) {
                    claudia4PcHolder = (String) e.getValue();
                    System.out.println("Claudia 4-piece holder: " + claudia4PcHolder);
                } else if (name.equals("Claudia4pcStars")) {
                    claudia4PcStars = Integer.parseInt((String) e.getValue());
                    System.out.println("Claudia 4-piece stars: " + claudia4PcStars);
                } else {
                    int advancement = Integer.parseInt((String) e.getValue());
                    System.out.println("Adding " + name + " A" + advancement);
                    WeaponData weaponData = new WeaponData(name, advancement);
                    data.put(name, weaponData);
                }
            }
        } catch (IOException e) {
            System.err.println("Unable to load properties for WeaponConfig");
            e.printStackTrace();
        }
    }

    public static Map<String, WeaponData> getData() {
        return data;
    }

    public static boolean hasClaudia4pc(String weapon) {
        return claudia4PcHolder.equalsIgnoreCase(weapon);
    }
    public static int getClaudia4PcReduction() {
        return 1500 + 500 * claudia4PcStars;
    }
}
