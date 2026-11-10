# Dustline - Google Play store listing (copy / paste)

## Main store listing
**App name** (max 30): `Dustline`

**Short description** (max 80 - this one is 63):
```
Offline 3D battle royale. Drop in, loot up and outlast 35 bots.
```

**Full description** (max 4000):
```
DUSTLINE is a solo 3D battle royale you can play offline. No account, no waiting for a lobby - just you, 35 bots and a shrinking island.

DROP IN
Ride the plane, choose your moment and jump. Dive head-first, glide in a skate stance, then open your parachute and land next to the loot you want.

LOOT AND FIGHT
- Search houses, supply drops and loot boxes dropped by fallen bots
- 13 firearms across SMG, shotgun, marksman and sniper classes, plus scopes
- Helmets, medkits, armor plates and grenades
- Sprint, crouch, swim and take cover - then out-aim the bots

SURVIVE THE ZONE
The safe zone keeps shrinking. Stay inside it and be the last fighter standing.

YOUR GAME, YOUR RULES
- Pick Easy, Normal or Hard bots
- Move and resize every HUD button with the built-in HUD editor
- Customize your fighter and outfit in the in-game store
- Track your profile and stats, and keep a photo album of your best moments

PLAYS OFFLINE
All matches work without internet and the game asks for no account. The game contains optional rewarded video ads (Google AdMob): watch a video to get 5 free diamonds. Ads need an internet connection. No in-app purchases. Your progress is saved on your device.

Dustline is a game with simulated firearm combat against computer-controlled opponents.

CREDITS
Survivor character: "FREE Survival Character" by Arberry (CGTrader, Royalty Free License). Parachute: "Parachute Simple" by sxnneh, CC BY 4.0. Character animation: "Character Jump" by SamSilcox, CC BY 4.0. Jump animation: "Human character with jumping animation" by LukeDale65, CC BY 4.0. Reload animation: "M4 - FPS Weapon Animations Pack" by BarcodeGames, CC BY 4.0. Clothes: "T-shirt" by Sirenko, "T-shirt LP" by Robin Bad, "T-SHIRT 3D Model Free" by Mohammad Rajabloo, "Sweater Pack" by MadeByYeshe, all CC BY 4.0 (Sketchfab). Models marked CC BY 4.0 were modified for this game. Full list in Settings > Account > Credits & Licenses.
```

**App category:** Game > Action  
**Contact email:** `YOUR_EMAIL` (required, shown publicly)  
**Privacy policy URL:** the public link of `docs/privacy-policy.html` (see BUILD_AND_UPLOAD.md, step "Privacy policy")

## Graphics (already made - `PLAY_STORE/assets/`)
| Play Console field | File | Size |
|---|---|---|
| App icon | `icon-512.png` | 512 x 512 |
| Feature graphic | `feature-graphic-1024x500.png` | 1024 x 500 |
| Phone screenshots | **you take these** (min. 2, best 4-8) | landscape 1920 x 1080 (16:9), JPEG or 24-bit PNG, max 8 MB each |

Screenshots must be real pictures of the running game: play a match on your phone, press power + volume-down in the
lobby, in the plane, in a firefight and at the safe zone. Do not add text claims that are not in the game.

## App content declarations (Play Console > Policy > App content)
| Section | Answer |
|---|---|
| Privacy policy | the URL above |
| Ads | **Yes**, the app contains ads (AdMob rewarded video, optional, shown only when the player taps WATCH VIDEO) |
| App access | All functionality is available without login / special access |
| Content rating (IARC questionnaire) | Answer honestly: *Violence* - yes, simulated shooting with realistic firearms against computer-controlled characters; no blood/gore setting, no online interaction, no purchases, no gambling. Expect roughly Teen / 12+ - 16+ |
| Target audience and content | **13+** age groups only. Do NOT tick "under 13" (that triggers the Families policy). |
| Data safety | See `PLAY_STORE/DATA_SAFETY_ANSWERS.md` (the AdMob SDK collects and shares data: **Yes**). |
| Advertising ID | **Yes** - purposes: **Advertising or marketing** (+ Fraud prevention). The manifest declares `com.google.android.gms.permission.AD_ID` |
| Government app / Financial features / Health / News | **No** to all |
| In-app purchases | **None** (the TOP-UP button only shows "not available offline") |
| Account creation / data deletion | The player only types a local name; no server account exists. In-app deletion is available: Settings > ACCOUNT > DELETE ALL MY DATA |

## Release details
- **Package name:** `com.dustline.game`  (cannot be changed after the first upload)
- **Version:** versionCode 39, versionName 1.0.2 (raise `versionCode` in `app/build.gradle.kts` for every new upload)
- **Permissions:** `VIBRATE`, `INTERNET`, `ACCESS_NETWORK_STATE`, `AD_ID` (all normal permissions; AD_ID needs the Advertising ID declaration above)
- **Supported devices:** Android 7.0+ (API 24), OpenGL ES 3.0, landscape only

## Credits paragraph for the full description (add the lines extract_credits.py prints, plus this)
Credits: Parachute Simple by sxnneh (https://sketchfab.com/Sxnneh), CC-BY-4.0 (http://creativecommons.org/licenses/by/4.0/).
Island map, menu icons and sounds are original or generated by the developer. [Add one line per verified Sketchfab model here.]
