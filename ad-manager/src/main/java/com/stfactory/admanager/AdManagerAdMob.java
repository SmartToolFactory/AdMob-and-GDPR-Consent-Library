package com.stfactory.admanager;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.lang.ref.WeakReference;

public class AdManagerAdMob implements AdManager {
/*
   Sample ad units

    Banner	ca-app-pub-3940256099942544/6300978111
    Interstitial	ca-app-pub-3940256099942544/1033173712
    Interstitial Video	ca-app-pub-3940256099942544/8691691433
    Rewarded Video	ca-app-pub-3940256099942544/5224354917
    Native Advanced	ca-app-pub-3940256099942544/2247696110
    Native Advanced Video	ca-app-pub-3940256099942544/1044960115

 */

/*
     ERROR CODES

    (0)ERROR_CODE_INTERNAL_ERROR - Something happened internally; for instance, an invalid response was received from the ad server.
    (1)ERROR_CODE_INVALID_REQUEST - The ad request was invalid; for instance, the ad unit ID was incorrect.
    (2)ERROR_CODE_NETWORK_ERROR - The ad request was unsuccessful due to network connectivity.
    (3)ERROR_CODE_NO_FILL - The ad request was successful, but no ad was returned due to lack of ad inventory.

  */


    private Activity mActivity;


    private ViewGroup mAdViewContainer;
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;

    // Ad Units are should be set. AdMob debug ids are set by default
    private String mBannerAdUnit = "ca-app-pub-3940256099942544/6300978111";
    private String mInterstitialAdUnit = "ca-app-pub-3940256099942544/1033173712";


    /*
     *** AdMob properties ***
     */
    /**
     * Show personalized ads to user
     */
    private boolean mPersonalizedAds = true;
    /**
     * In debug mode display ads with AdMob id to prevent invalid transactions to account
     */
    private boolean mDebugMode = false;

    /**
     * Enable displaying ads
     */
    private boolean mAdsEnabled = true;


    /**
     * Events like showing consent dialog might cause
     * ads to miss onResume method if ads are not set up in prior
     */
    // private boolean mMissedDisplayCycle = false;

    /**
     * If onResume is missed due to a dialog event invoke onResume manually to show ads
     */
    // private boolean rePlayAdsIfMissed = true;


    private AdSize mBannerAdSize = AdSize.SMART_BANNER;


    /*
     Internal state variables
    */

    /**
     * check if Banner AdView is added to parent
     */

    private boolean mBannerViewAdded = false;
    /**
     * Banner View is visible to user
     */
    private boolean mBannerAdVisible = false;


    public AdManagerAdMob(Activity activity, boolean isPersonalizedAds) {
        this(activity);
        mPersonalizedAds = isPersonalizedAds;
    }

    public AdManagerAdMob(Activity activity) {
        mActivity = new WeakReference<>(activity).get();
    }

    // ***************  AD PARAMS *************** //

    @Override
    public void enableAds(boolean enable) {
        mAdsEnabled = enable;
    }

    @Override
    public void setPersonalizedAds(boolean personalizedAds) {
        mPersonalizedAds = personalizedAds;
    }


    @Override
    public void setDebugMode(boolean debugMode) {
        mDebugMode = debugMode;
    }


    @Override
    public void setBannerAdSize(int size) {
        switch (size) {
            case BANNER:
                mBannerAdSize = AdSize.BANNER;
                break;
            case FULL_BANNER:
                mBannerAdSize = AdSize.FULL_BANNER;
                break;
            case LARGE_BANNER:
                mBannerAdSize = AdSize.LARGE_BANNER;
                break;
            case LEADERBOARD:
                mBannerAdSize = AdSize.LEADERBOARD;
                break;
            case MEDIUM_RECTANGLE:
                mBannerAdSize = AdSize.MEDIUM_RECTANGLE;
                break;
            case WIDE_SKYSCRAPER:
                mBannerAdSize = AdSize.WIDE_SKYSCRAPER;
                break;
            case SMART_BANNER:
                mBannerAdSize = AdSize.SMART_BANNER;
                break;
            case FLUID:
                mBannerAdSize = AdSize.FLUID;
                break;
            case SEARCH:
                mBannerAdSize = AdSize.SEARCH;
                break;
        }

        try {
            if (mAdView != null) {
                mAdView.setAdSize(mBannerAdSize);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void setBannerAdUnitId(String bannerAdId) {
        mBannerAdUnit = bannerAdId;
    }

    @Override
    public void setInterstitialAdUnitId(String interstitialAdId) {
        mInterstitialAdUnit = interstitialAdId;

    }


    // ***************  SET UP ADS *************** //


    /**
     * @param layout   RelativeLayout for adding ads
     * @param position position of AdView on RelativeLayout
     */
    public void setUpAds(ViewGroup layout, int position) {

        mAdViewContainer = layout;
        setUpBannerAds(layout, position);
        // Set up interstitial ad and request ad from network
        setUpInterstitialAds();
    }


    public void setUpBannerAds(final ViewGroup layout, final int position) {
        if (mActivity != null) {
            mActivity.runOnUiThread(() -> {

                mAdViewContainer = layout;

                mAdView = new AdView(mActivity);
                mAdView.setVisibility(View.VISIBLE);

                if (mDebugMode) {
                    mAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
                } else {
                    mAdView.setAdUnitId(mBannerAdUnit);
                }

                mAdView.setAdSize(mBannerAdSize);

                if (mAdViewContainer == null) return;

                // Set up parameters for ad width, height and position
                if (mAdViewContainer instanceof RelativeLayout) {
                    RelativeLayout.LayoutParams params = new
                            RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    // Set up ad position on parent. Top, Bottom, etc.
                    params.addRule(position);
                    // Set up ad width, height and position
                    mAdView.setLayoutParams(params);
                } else if (mAdViewContainer instanceof FrameLayout) {
                    FrameLayout.LayoutParams params = new
                            FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

                    mAdView.setLayoutParams(params);
                }
            });
        }
    }


    public void setBannerListener(final CustomAdListener customAdListener) {
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                customAdListener.onAdLoaded();

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                customAdListener.onAdFailedToLoad(errorCode);

            }
        });

    }

    /**
     * Get AdRequest depending on non-personalized or personalized ads preference
     */
    private AdRequest getAdRequest() {

        AdRequest.Builder builder = new AdRequest.Builder();

        if (!mPersonalizedAds) {
            Bundle extras = new Bundle();
            extras.putString("npa", "1");
            builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
        }

        return builder.build();
    }


    /**
     * adds AdView to layout
     */
    @Override
    public void addBanner() {

        if (mActivity != null && mAdView != null && mAdViewContainer != null && !mBannerViewAdded && isOnline(mActivity)) {

            mActivity.runOnUiThread(() -> {
                mAdViewContainer.addView(mAdView);
                mBannerViewAdded = true;

            });
        }
    }

    /**
     * Remove banner ads from container layout
     */
    @Override
    public void removeBanner() {
        if (mActivity != null && mAdView != null && mAdViewContainer != null && mBannerViewAdded) {
            mActivity.runOnUiThread(() -> {
                mAdViewContainer.removeView(mAdView);
                mBannerViewAdded = false;
                mBannerAdVisible = false;
            });
        }
    }

    /**
     * Loads ad from network and show banner ads to user
     */
    @Override
    public void showBanner() {
        if (mAdsEnabled && mActivity != null && mAdView != null && mAdViewContainer != null
                && isOnline(mActivity) && mBannerViewAdded && !mBannerAdVisible) {

            mActivity.runOnUiThread(() -> {
                try {
                    mAdView.loadAd(getAdRequest());
                    mAdView.setVisibility(AdView.VISIBLE);
                    mBannerAdVisible = true;
                    mAdView.bringToFront();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }
    }

    /**
     * Pause AdView and set visibility of AdView to invisible
     */
    @Override
    public void hideBanner() {

        if (mAdView != null && mBannerAdVisible) {
            if (mActivity != null) {
                mActivity.runOnUiThread(() -> {
                    mAdView.pause();
                    mAdView.setVisibility(AdView.INVISIBLE);
                    mBannerAdVisible = false;
                });
            }
        }
    }

    @Override
    public void setUpInterstitialAds() {
        mInterstitialAd = new InterstitialAd(mActivity);
        if (mDebugMode) {
            mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        } else {
            mInterstitialAd.setAdUnitId(mInterstitialAdUnit);
        }
        loadInterstitialAd();
    }


    @Override
    public void loadInterstitialAd() {
        if (mInterstitialAd != null && isOnline(mActivity))
            mInterstitialAd.loadAd(getAdRequest());
    }

    @Override
    public void showInterstitialAd() {
        if (mAdsEnabled && mActivity != null) {
            try {
                mActivity.runOnUiThread(() -> {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else if (!mInterstitialAd.isLoading()) {
                        loadInterstitialAd();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // *************** ANDROID LIFE CYCLE METHODS *************** //

    //  Life Cycle Methods

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    @Override
    public void onResume() {

        if (mAdsEnabled && mAdView != null && isOnline(mActivity)) {
            mAdView.resume();
            addBanner();
            showBanner();

        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    @Override
    public void onPause() {

        if (mAdsEnabled && mAdView != null) {
            mAdView.pause();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    @Override
    public void onDestroy() {

        if (mActivity != null) {
            mActivity.runOnUiThread(() -> {
                if (mAdView != null) {
                    mAdView.setAdListener(null);
                    mAdView.destroy();
                    mBannerViewAdded = false;
                    mAdView = null;
                }

                if (mInterstitialAd != null) {
                    mInterstitialAd.setAdListener(null);
                    mInterstitialAd = null;
                }
            });
        }
    }

    @Override
    public void addBannerAdListener(final CustomAdListener adListener) {

        if (mAdView == null) return;

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adListener.onAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                adListener.onAdFailedToLoad(errorCode);
            }
        });

    }

    @Override
    public void addInterstitialAdListener(final CustomAdListener adListener) {

        if (mInterstitialAd == null) return;

        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                adListener.onAdFailedToLoad(errorCode);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adListener.onAdLoaded();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                adListener.onAdClosed();
            }

        });

    }

    @Override
    public boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) return false;

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    /**
     * This method is internal debugging method
     *
     * @param message  Message to be displayed
     * @param duration of the message display time
     */
    private void showToast(final String message, final int duration) {
        try {
            mActivity.runOnUiThread(() -> Toast.makeText(mActivity, message, duration).show());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}