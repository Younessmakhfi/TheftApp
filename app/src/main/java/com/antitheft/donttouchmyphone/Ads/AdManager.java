package com.antitheft.donttouchmyphone.Ads;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdkUtils;

public class AdManager {
    public static MaxAdView adView;
    public static MaxInterstitialAd interstitialAd;
    public static int retryAttempt;
    public static String MAX_BANNER_AD = "133503430f0510b7";
    public static String MAX_INTERSTITIAL_AD = "a4e0f0d19ce60b40";
    public static String max_banner_ad_logger = "MAX_BANNER_AD_LOGGER";
    public static String max_interstitial_ad_logger = "MAX_INTERSTITIAL_AD_LOGGER";
    public static GetBackPointer getBackPointer_Local;

    public static void showInter(Activity activity, final GetBackPointer getBackPointer) {
        getBackPointer_Local = getBackPointer;
        if (interstitialAd.isReady()) {
            interstitialAd.showAd();
        } else {
            if (getBackPointer != null) {
                getBackPointer.returnAction();
            }

        }
    }

    //
//    }
    public static void loadBanner(Activity activity, LinearLayout bannerContainer) {
        adView = new MaxAdView(MAX_BANNER_AD, activity);
        MaxAdViewAdListener maxAdViewAdListener = new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd ad) {
                Log.d(max_banner_ad_logger, "onAdExpanded");
            }

            @Override
            public void onAdCollapsed(MaxAd ad) {
                Log.d(max_banner_ad_logger, "onAdCollapsed");
            }

            @Override
            public void onAdLoaded(MaxAd ad) {
                Log.d(max_banner_ad_logger, "onAdLoaded");
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
                Log.d(max_banner_ad_logger, "onAdDisplayed");
            }

            @Override
            public void onAdHidden(MaxAd ad) {
                Log.d(max_banner_ad_logger, "onAdHidden");
            }

            @Override
            public void onAdClicked(MaxAd ad) {
                Log.d(max_banner_ad_logger, "onAdClicked");
            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                Log.d(max_banner_ad_logger, "onAdLoadFailed");
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                Log.d(max_banner_ad_logger, "onAdDisplayFailed");
            }
        };
        adView.setListener(maxAdViewAdListener);

        final boolean isTablet = AppLovinSdkUtils.isTablet(activity);
        final int heightPx = AppLovinSdkUtils.dpToPx(activity, isTablet ? 90 : 50);
        adView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightPx));
        adView.setBackgroundColor(Color.BLACK);
        bannerContainer.addView(adView);
        adView.loadAd();
    }

    public static void initVideoAds(Activity activity) {
        interstitialAd = new MaxInterstitialAd(MAX_INTERSTITIAL_AD, activity);
        MaxAdListener maxAdListener = new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                Log.d(max_interstitial_ad_logger, "onAdLoaded");
            }
            @Override
            public void onAdDisplayed(MaxAd ad) {
                Log.d(max_interstitial_ad_logger, "onAdDisplayed");
            }
            @Override
            public void onAdHidden(MaxAd ad) {
                Log.d(max_interstitial_ad_logger, "onAdHidden");
                if (getBackPointer_Local != null) {
                    getBackPointer_Local.returnAction();
                }
            }
            @Override
            public void onAdClicked(MaxAd ad) {
                Log.d(max_interstitial_ad_logger, "onAdClicked");
            }
            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                Log.d(max_interstitial_ad_logger, "onAdLoadFailed");
            }
            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                Log.d(max_interstitial_ad_logger, "onAdDisplayFailed");
            }
        };
        interstitialAd.setListener(maxAdListener);

        // Load the first ad
        interstitialAd.loadAd();
    }


    public interface GetBackPointer {
        public void returnAction();
    }
}


