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

    static {
        props = new Properties();
        try {
            props.load(new FileReader("weapon.properties"));
            for (Map.Entry<Object, Object> e : props.entrySet()) {
                String name = (String) e.getKey();
                int advancement = Integer.parseInt((String) e.getValue());
                System.out.println("Adding " + name + " A" + advancement);
                WeaponData weaponData = new WeaponData(name, advancement);
                data.put(name, weaponData);
            }
        } catch (IOException e) {
            System.err.println("Unable to load properties for WeaponConfig");
            e.printStackTrace();
        }
    }

    public static Map<String, WeaponData> getData() {
        return data;
    }
}
