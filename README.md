# tof-overlay
This is an overlay designed to run with **Tower of Fantasy** in the background, that parses useful combat information using the [Sikuli API](http://doc.sikuli.org/). It collects data by essentially taking screenshots and looking for text or images - no intrusive memory reading or packet sniffing included! It is a [repurpose for a parser I wrote for Maplestory2](https://github.com/Maygi/ms2combatanalyzer).

![The TOF Overlay](https://cdn.discordapp.com/attachments/549708292423811074/1047454023881592912/image.png)

# Recent Updates
Check the [releases](https://github.com/Maygi/tof-overlay/releases) page to see a list of releases and the relevant update notes.

# Is it legal?
It should be fine (keyword: should). This doesn't do anything more than attempt to recognize images/text on the screen, it's no more intrusive than software such as OBS.

# How to use
Currently, the TOF Overlay requires that you play in fullscreen mode. Due to how it collects data, note that obstructing your UI at any time may lead to inaccurate results.

To run the TOF Overlay, just follow these steps:

1. Go to the [releases](https://github.com/Maygi/tof-overlay/releases) and download the latest version at the bottom. It will be a .zip or .gz file.
2. Extract the tof-overlay zip folder.
3. Open **weapon.properties** with any text editor of choice and update it with the advancements you have. 
4. Run the **tof-overlay.jar** file, or the **run.bat** file (better for debugging).
5. Drag the window to wherever you would like, and leave it running.
6. Turn on Weapon Shortcut Hints. (see below)

**Calibration**

If you don't have weapon shortcut hints showing, you'll need to turn them on.

![How to turn on Shortcut Hints](https://cdn.discordapp.com/attachments/549708292423811074/1047725194892021850/image.png)

To help calibrate your weapon setup, it's recommended to stand somewhere where the bottom-right corner of your screen is in a dark area. Swap to each of your three weapons, and they should appear on the overlay. If an incorrect weapon shows up, click the reset button to reset the overlay.

![The weapon UI in-game](https://cdn.discordapp.com/attachments/549708292423811074/1047454158850117672/image.png)

If you are standing in an overly bright area, there's a good chance you'll run into false positives (most commonly Ruby and Frigg). If you see the red warning triangle button, that means that the calibration is a bit shaky, and you should consider resetting.

![When you see this, consider resetting!](https://cdn.discordapp.com/attachments/549708292423811074/1047643392949108816/image.png)

*Not working?*

Scroll to the bottom for some common mistakes that people make~

# Features
* Keeps track of cooldowns on all weapons (except Huma)
* Displays weapon-charge effects (Electrocute, Frostbite, Grievous, Burn, Aberration Mark)

![Physical](https://cdn.discordapp.com/attachments/549708292423811074/1047449489230807091/grievous.png)
* Claudia - keeps track of A1 buff timer (refreshed upon Skill or Discharge)
* Shiro - keeps track of Full Bloom timer
* Lyra (DPS) - keeps track of Enlightenment mark timer
* Lyra (Benediction) - keeps track of Enlightenment mark, blade cooldown including charges, total blades on-field, and the time before the first blade expires

![Volt](https://cdn.discordapp.com/attachments/549708292423811074/1047449490610720848/electrocute.png)
* Nemesis - keeps track of Electrodes - added through Skill (A1+) or Discharge, as well as Electrode count (A6)
* Crow - keeps track of Discharge timer 
* Samir - A6 cooldown is updated constantly by checking on-screen values
* Tian Lang - keeps track of Volt sense timer

![Flame](https://cdn.discordapp.com/attachments/549708292423811074/1047449490187104267/burn.png)
* Ruby - keeps track of estimated detonation timing
* Cobalt-B - keeps track of Ionic Burn timer (A3+)
* King - keeps track of Discharge timer
* Zero - keeps track of shield duration

![Frost](https://cdn.discordapp.com/attachments/549708292423811074/1047449491101462558/frostbite.png)
* Saki Fuwa - keeps tracks of estimated Surge cooldown (assumes you use it instantly on-swap), and counts your skill usages for the reset (A1+)
* Tsubasa - keeps track of buff timer (on dodge, A1+)
* Frigg - keeps track of Frost Domain timer
* Meryl - keeps track of shield timer, as well as the shield cooldown

![Aberration](https://cdn.discordapp.com/attachments/549708292423811074/1047450104111575061/aberration.png)
* Lin - keeps track of Moonlight Realm timer, as well as Discharge counter for extra skill use (A6)

**Misc**

* Sound triggers
  * When any cooldown is ready
  * When the window is focused and your queue pops

# Notes
* All values are estimates! They may not be 100% accurate.
* False positives in weapon cooldown usage, or weapons existing (Frigg pop up out of nowhere? Woops), may occur due to limitations in current tech (trying to match images on transparent UI is really difficult).

# SoonTM
* Different visual themes
* UI for initialization

# HELP! IT'S NOT WORKING!

*If the overlay is collecting no data...*
* Are you in fullscreen? It only works in fullscreen, and is supported in 1080p/1440p.

* Did you download the entire folder? You'll need the resource folders - namely, lib, fonts, images, and sound - to get the overlay to work properly. tof-overlay.jar should be in a folder outside those.
* Did you open it with Java? Some people have jar folders to open with WinZip by default. Right-click the .jar file and make sure to open with Java. If you don't have it, you can [download it from the Oracle website.](https://www.java.com/en/download/)

*Are you using a controller?*
* The parser works by reading your current skill key configuration. You can do a workaround by making a controller layout on Steam and assign the controller buttons to the KBM inputs.

*If it doesn't open...*
 * If the command prompt from Run.bat closes instantly upon opening, it's probably because you don't have [Java](https://www.java.com/en/download/)!
 * Check the config.properties file and reset the x and y coordinates to 0 if the app runs but you can't see it.