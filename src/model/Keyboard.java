package model;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class Keyboard implements NativeKeyListener {

    private static final String SKILL_KEY = "Q";

    private static final Map<Integer, Boolean> pressedKeys = new HashMap<>();

    private static long lastKeyPress = 0;

    private static final int INTERVAL = 200;

    static {
        /*try {
            GlobalScreen.registerNativeHook();
        } catch (Exception e) {
            System.err.println("Could not register the native hook.");
            e.printStackTrace();
        }
        GlobalScreen.addNativeKeyListener(new Keyboard());*/
    }

    public void nativeKeyPressed(NativeKeyEvent e) {
        System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
        if (NativeKeyEvent.getKeyText(e.getKeyCode()).equalsIgnoreCase(SKILL_KEY)) {
            System.out.println("SKILL ON");
            lastKeyPress = System.currentTimeMillis();
        }
    }
    public static boolean pressedSkill() {
        //System.out.println("Last pressed: " + (System.currentTimeMillis() - lastKeyPress));
        return (System.currentTimeMillis() - lastKeyPress <= INTERVAL);
    }

}