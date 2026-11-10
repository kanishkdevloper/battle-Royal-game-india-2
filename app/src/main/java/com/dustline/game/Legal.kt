package com.dustline.game

import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

/**
 * Privacy / ads / data-deletion screens used by Settings > ACCOUNT.
 * Play Store needs the privacy policy to be reachable INSIDE the app as well as in the Play Console.
 */
object Legal {
    /**
     * Public link of docs/privacy-policy.html (GitHub Pages or your own site), e.g. https://yourname.github.io/Dustline/privacy-policy.html
     * TODO before release: paste YOUR link here. While it is empty the in-app text below is shown without the "open online" button.
     * The same link must be entered in Play Console > App content > Privacy policy.
     */
    const val PRIVACY_POLICY_URL = ""

    private const val TEXT = """PRIVACY SUMMARY

Dustline is a single-player game. Your name, ID, progress, settings and photo-album pictures are stored only on your device. We do not run a server and we do not receive this data.

ADS (AdMob)
The game shows optional rewarded video ads (the WATCH VIDEO button). Ads are provided by Google AdMob. To show and measure ads, and to prevent fraud, Google may collect your device's advertising ID, IP address, device and app information and ad interaction data. In the EEA, UK and Switzerland you are asked for consent first, and you can change it later in Settings > ACCOUNT > AD PRIVACY CHOICES. Without internet or without consent no ad is shown and the rest of the game works normally.
More: https://policies.google.com/technologies/partner-sites

PERMISSIONS
Internet and network state (only for ads), advertising ID (only for ads), vibration.

DELETE YOUR DATA
Settings > ACCOUNT > DELETE ALL MY DATA erases everything the game stored on this device. Uninstalling the game does the same.

The game is for players aged 13 and over."""

    fun showPrivacy(act: Activity) {
        val tv = TextView(act).apply { text = TEXT; textSize = 13f; setPadding(40, 30, 40, 30) }
        val sv = ScrollView(act).apply { addView(tv) }
        val b = AlertDialog.Builder(act).setTitle("Privacy Policy").setView(sv).setPositiveButton("OK", null)
        if (PRIVACY_POLICY_URL.startsWith("https://")) {
            b.setNeutralButton("OPEN ONLINE VERSION") { _, _ ->
                try { act.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))) } catch (_: Throwable) { }
            }
        }
        b.show()
    }

    fun adPrivacyChoices(act: Activity) {
        if (ConsentManager.privacyOptionsRequired(act)) {
            ConsentManager.showPrivacyOptions(act)
        } else {
            AlertDialog.Builder(act).setTitle("Ad privacy choices")
                .setMessage("No ad-consent choice is required in your region right now. If it becomes required, this button opens the Google consent settings.")
                .setPositiveButton("OK", null).show()
        }
    }

    /** Wipes ALL app data (progress, name, settings, album) and closes the game - works like Android's "Clear storage". */
    fun confirmDeleteAll(act: Activity) {
        AlertDialog.Builder(act).setTitle("Delete all my data?")
            .setMessage("This erases your name, ID, coins, diamonds, outfits, settings and photos from this device. It cannot be undone.")
            .setPositiveButton("DELETE") { _, _ ->
                try {
                    val am = act.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                    if (!am.clearApplicationUserData()) Toast.makeText(act, "Could not delete. Use Android Settings > Apps > Dustline > Storage > Clear storage.", Toast.LENGTH_LONG).show()
                } catch (_: Throwable) {
                    Toast.makeText(act, "Use Android Settings > Apps > Dustline > Storage > Clear storage.", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("CANCEL", null).show()
    }
}
