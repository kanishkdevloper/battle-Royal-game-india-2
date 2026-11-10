package com.dustline.game

import android.app.Activity
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

/**
 * Google UMP (User Messaging Platform) consent flow, required by AdMob for users in the EEA / UK / Switzerland
 * (and shown for US-state rules if you configure a message in AdMob > Privacy & messaging).
 *
 * Usage: ConsentManager.gather(activity) { AdManager.start() }  - the callback runs on the UI thread only when ads may be requested.
 * If you have NOT created a message in AdMob > Privacy & messaging, UMP reports "no form needed" and ads simply start.
 */
object ConsentManager {
    private var started = false

    fun gather(act: Activity, onCanRequestAds: () -> Unit) {
        try {
            val info = UserMessagingPlatform.getConsentInformation(act)
            // players of this game are 13+ (Play Console target audience), so the "under age of consent" flag is false
            val params = ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(false).build()
            info.requestConsentInfoUpdate(act, params, {
                try {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(act) { _ ->
                        if (info.canRequestAds()) fire(onCanRequestAds)
                    }
                } catch (_: Throwable) { if (info.canRequestAds()) fire(onCanRequestAds) }
            }, { _ ->
                // update failed (offline etc.): the SDK may still hold a consent from an earlier run
                if (info.canRequestAds()) fire(onCanRequestAds)
            })
            // returning user who already consented: ads can start at once, without waiting for the network
            if (info.canRequestAds()) fire(onCanRequestAds)
        } catch (_: Throwable) { }
    }

    private fun fire(cb: () -> Unit) {
        if (started) return
        started = true
        cb()
    }

    /** true when the player must be able to re-open the consent choices (Settings button). */
    fun privacyOptionsRequired(act: Activity): Boolean = try {
        UserMessagingPlatform.getConsentInformation(act).privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    } catch (_: Throwable) { false }

    fun showPrivacyOptions(act: Activity, after: () -> Unit = {}) {
        try {
            UserMessagingPlatform.showPrivacyOptionsForm(act) { _ -> after() }
        } catch (_: Throwable) { after() }
    }
}
