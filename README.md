# ms2combatanalyzer
This is an overlay designed to run with **Maplestory2** in the background, that parses useful combat information using the [Sikuli API](http://doc.sikuli.org/). It collects data by essentially taking screenshots and looking for text or images - no intrusive memory reading or packet sniffing included!

![The MSCA Overlay](https://media.giphy.com/media/RkHuicEN5HldWz6sYO/giphy.gif)
![An example parse](https://i.imgur.com/eEpsxFN.png)

# Recent Updates
Check the [releases](https://github.com/Maygi/ms2combatanalyzer/releases) page to see a list of releases and the relevant update notes.

# How to use
Currently, MSCA requires that you play in fullscreen mode. Due to how it collects data, note that obstructing your UI at any time may lead to inaccurate results.

# Is it legal?
It should be fine (keyword: should). This doesn't do anything more than analyze some pixels on the screen, it's no more intrusive than software such as OBS.

To run MSCA, just follow these steps:

1. Go to the [releases](https://github.com/Maygi/ms2combatanalyzer/releases) and download the latest version at the bottom. It will be a .zip or .gz file.
2. Extract the MSCA zip folder.
3. Run the **MSCA.jar** file.
4. That's it!

*Not working?*
Scroll to the bottom for some common mistakes that people make~

# Features
* Parses total party DPS
* Estimates clear time based on average party DPS in and out of Holy Symbol, taking into consideration how many Holy Symbol casts are remaining in the fight
* User can reset / pause the parse
* Calculates uptime
  * Personal buffs (Celestial Guardian, Iron Defense, etc)
  * Personal debuffs (Celestial Light, Shadow Chaser, etc)
  * Party buffs (Celestial Blessings, Focus Seal, etc)
  * Party debuffs (Smiting Aura, Shield Toss, etc)
* Estimates damage contribution
  * Smiting Aura / Purifying Light
  * Shield Toss / Cyclone Shield
  * Static Flash / Soul Flock
  * Mark of Death
  * Holy Symbol
* Sound triggers
  * Reaching max stacks of Flame Arrow, Wind Draw
  * Resetting cooldown of BBQ Party
  * Gaining instant 3rd hit of Skull Splitter
  * Varrekant's wings
  * Weapon proc
  * Blue bomb debuff in Wrath of Infernog

# Notes
* All calculations are estimates! They may not be 100% accurate.
* As of 5/29/19, debuff calculations follow a complex formula and are much more accurate than before.
* Holy Symbol contribution calculation is a work in progress and may be even more inaccurate. As such, I've provided a metric for the total damage dealt under Holy Symbol as well
* If you cast Holy Symbol within the first minute or so, the Holy Symbol damage contribution won't appear for another minute. This is intended; the program needs to see around one minute of combat without Holy Symbol to estimate how much damage the party is doing normally
* Pausing when in combat with a boss won't do anything, because it'll automatically start up instantly. Pause is only useful for when the encounter is finished, or you leave early.

# Upcoming Features
* Add an option pane for customizing and toggling sound triggers

# Upcoming (soon TM) Features
* Networking with other MSCA users in your party to come up with even more advanced information (e.g. "That last blessings only hit 7/10 party members!")
* Add an option to change the UI color (I'm sure not everyone likes pink as much as I do...)

# HELP! IT'S NOT WORKING!

*If the overlay is collecting no data...*
* Are you in fullscreen? It only works in fullscreen :c
* Is your interface size 50%? You can change this in the game settings.
* Are you fighting a boss monster? It only parses bosses!

*If the overlay looks like this...*
![Broken overlay](https://i.imgur.com/sitE9Q0.png)
* Did you download the entire folder? You'll need the resource folders - namely, MSCA_lib, fonts, images, and sound - to get the overlay to work properly. MSCA.jar should be in a folder outside those.
* Did you open it with Java? Some people have jar folders to open with WinZip by default. Right-click the .jar file and make sure to open with Java. If you don't have it, you can [download it from the Oracle website.](https://www.java.com/en/download/)
