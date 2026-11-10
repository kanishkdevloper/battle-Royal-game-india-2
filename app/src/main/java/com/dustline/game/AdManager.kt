package com.dustline.game

import android.app.Activity
import android.content.pm.ApplicationInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlin.random.Random

/**
 * Lobby "WATCH VIDEO" rewarded ad (AdMob). The ad is preloaded in the background, so a tap shows it at once.
 * The ad is a separate full-screen Activity of the SDK: MainActivity only pauses (never restarts), and when the
 * player closes / back-presses the ad he is simply back in the lobby.
 *
 * Real AdMob IDs: REWARDED_UNIT_ID here + APPLICATION_ID in AndroidManifest.xml.
 * DEBUG builds (installed from Android Studio / app-debug.apk) use Google's TEST rewarded unit instead, so testing on your own
 * phone never sends real ad requests / clicks to your AdMob account (that can get the account suspended).
 * Ads start only after ConsentManager says ads may be requested (UMP consent).
 */
class AdManager(private val act: Activity) {
    companion object {
        /** Real AdMob rewarded ad unit ID. */
        const val REWARDED_UNIT_ID = "ca-app-pub-2366599730777996/2335941063"
        /** Google's public test rewarded unit - used automatically in debug builds. */
        const val TEST_REWARDED_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
        /** The lobby button says "+5 FREE DIAMONDS": the reward is exactly that (Play / AdMob: reward must match what the button promises). */
        const val REWARD_DIAMONDS = 5
        private const val EXPIRE_MS = 50L * 60_000L     // a loaded rewarded ad expires after 1 h
        private const val RETRY_MIN_MS = 15_000L
        private const val RETRY_MAX_MS = 5L * 60_000L
        private const val COOLDOWN_MS = 30_000L         // pause after a watched video, so the button cannot be spammed

        /** Fixed reward = what the button promises (+5). Kept as a function so the call site stays the same. */
        fun rollDiamonds(@Suppress("UNUSED_PARAMETER") rnd: Random = Random.Default): Int = REWARD_DIAMONDS
    }

    interface Listener {
        /** The player watched the video to the end: give the diamonds now (called before onClosed). */
        fun onReward(diamonds: Int)
        /** The ad screen was closed (back / X). rewarded = the reward was earned. */
        fun onClosed(rewarded: Boolean)
        /** Short text for the lobby (not ready / no internet / failed). */
        fun onMessage(text: String)
    }

    private val handler = Handler(Looper.getMainLooper())
    private var ad: RewardedAd? = null
    private var loadedAt = 0L
    private var loading = false
    private var showing = false
    private var sdkReady = false
    private var started = false
    private var retryMs = RETRY_MIN_MS
    private var cooldownUntil = 0L

    private fun unitId(): String =
        if ((act.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0) TEST_REWARDED_UNIT_ID else REWARDED_UNIT_ID

    /** true only when a fresh ad is loaded, none is showing and the cooldown is over -> the lobby shows the WATCH VIDEO button only then. */
    fun isReady(): Boolean = sdkReady && !showing && fresh() && System.currentTimeMillis() >= cooldownUntil

    /** Call once (after boot). Initialises the SDK off the UI thread and preloads the first ad. */
    fun start() {
        if (started) return
        started = true
        Thread {
            try {
                // the game is rated Teen: never request ads above "T" (no mature-only ads in a 13+ game)
                MobileAds.setRequestConfiguration(
                    RequestConfiguration.Builder().setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_T).build()
                )
                MobileAds.initialize(act) { sdkReady = true; act.runOnUiThread { load() } }
            } catch (_: Throwable) { }
        }.start()
    }

    private fun fresh() = ad != null && System.currentTimeMillis() - loadedAt < EXPIRE_MS

    fun load() {
        if (!sdkReady || loading || showing || fresh() || act.isDestroyed || act.isFinishing) return
        ad = null; loading = true
        try {
            RewardedAd.load(act, unitId(), AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
                override fun onAdLoaded(a: RewardedAd) { ad = a; loadedAt = System.currentTimeMillis(); loading = false; retryMs = RETRY_MIN_MS }
                override fun onAdFailedToLoad(e: LoadAdError) {
                    ad = null; loading = false
                    // no fill / no internet: try again later, with growing pauses
                    handler.postDelayed({ load() }, retryMs)
                    retryMs = (retryMs * 2).coerceAtMost(RETRY_MAX_MS)
                }
            })
        } catch (_: Throwable) { loading = false }
    }

    private fun online(): Boolean = try {
        val cm = act.getSystemService(ConnectivityManager::class.java)
        val caps = cm?.getNetworkCapabilities(cm.activeNetwork)
        caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    } catch (_: Throwable) { true }

    fun show(l: Listener) {
        if (showing) return
        val a = ad
        if (a == null || !fresh()) {
            ad = null
            if (!online()) { l.onMessage("NO INTERNET - CONNECT TO WATCH ADS"); return }
            retryMs = RETRY_MIN_MS; load()
            l.onMessage("AD LOADING... TRY AGAIN IN A FEW SECONDS")
            return
        }
        var rewarded = false
        a.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                showing = false; ad = null
                if (rewarded) cooldownUntil = System.currentTimeMillis() + COOLDOWN_MS
                l.onClosed(rewarded)
                load()                       // next ad is ready for the next tap
            }
            override fun onAdFailedToShowFullScreenContent(e: AdError) {
                showing = false; ad = null
                l.onMessage("AD FAILED - TRY AGAIN")
                load()
            }
        }
        showing = true
        try {
            a.show(act, OnUserEarnedRewardListener { _ ->
                if (!rewarded) { rewarded = true; l.onReward(rollDiamonds()) }
            })
        } catch (t: Throwable) {
            showing = false; ad = null
            l.onMessage("AD FAILED - TRY AGAIN"); load()
        }
    }
}
