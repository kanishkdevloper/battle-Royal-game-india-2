# Ads policy (AdMob) + Privacy - checklist before every upload

## Done in the code (verified by reading the source)
- [x] AdMob SDK 25.5.0 (supported) + UMP consent SDK 4.0.0 (both versions confirmed on Google's release-notes pages)
- [x] UMP consent form runs before any ad request (`ConsentManager.kt`); Settings > ACCOUNT > AD PRIVACY CHOICES re-opens it
- [x] Debug builds use Google's TEST ad unit; the real unit is used only in release builds (own-click / invalid-traffic protection)
- [x] Max ad content rating = Teen (13+ audience, no Families / child-directed settings)
- [x] Ads only after the player taps WATCH VIDEO (no forced / surprise ads, no ads during a match, no ads on app open)
- [x] Reward matches the button: "+5 FREE DIAMONDS" = exactly 5 diamonds; the button is labelled "AD"
- [x] 30 s cooldown after a watched ad; closing the ad early gives no reward and says so
- [x] Manifest: INTERNET, ACCESS_NETWORK_STATE, AD_ID (declared), VIBRATE only
- [x] Privacy policy page rewritten for AdMob + in-app copy (Settings > ACCOUNT > PRIVACY POLICY)
- [x] In-app "DELETE ALL MY DATA"
- [x] Licence notices: credits.txt (in-game) has author, licence, link and the CC-BY "modified" notice

## YOU must do (cannot be done in code)
- [ ] Fill YOUR NAME OR STUDIO NAME and YOUR_EMAIL in `docs/privacy-policy.html` (1 name + 4 email places), publish it (GitHub Pages) and paste the link in `Legal.kt` (`PRIVACY_POLICY_URL`) AND in Play Console
- [ ] AdMob console > Privacy & messaging: create the **GDPR message** (EEA/UK) and the **US states** message, set the app to publish. Without a message UMP shows nothing (the app still works, but EEA users are then not asked)
- [ ] AdMob console: app linked to the Play listing after the first release; add `app-ads.txt` on your developer website (Play Console > Store settings > developer website) if you have one
- [ ] Play Console: Ads = Yes, Advertising ID = Yes, Data safety as in `DATA_SAFETY_ANSWERS.md`, Target audience 13+
- [ ] Test the **release build** once on a phone: WATCH VIDEO shows an ad, closing it early gives no diamonds, finishing it gives +5
- [ ] NEVER tap your own real ads. Test on the debug APK (test ads) only
- [ ] Keep: ChatGPT chats for lobby_bg + HUD icons, CGTrader download record (#4323274), the CC-BY .glb files

## Known limits / watch-outs
- Google calls the "legacy" Mobile Ads SDK maintenance-mode; plan a move to the GMA Next-Gen SDK before 2027-06-30 (see AdMob docs).
- Realistic firearms: allowed as a game, but answer the IARC rating honestly (Violence). Do not describe the game as realistic military training or link to weapon shops.
- The HUD airdrop crate looks like a common battle-royale crate: redraw it if you want zero resemblance.
