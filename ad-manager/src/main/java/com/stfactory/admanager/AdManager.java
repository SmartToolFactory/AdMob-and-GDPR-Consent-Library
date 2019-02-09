package com.stfactory.admanager;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.view.ViewGroup;

public interface AdManager extends LifecycleObserver {


    int BANNER = 0;
    int FULL_BANNER = 1;
    int LARGE_BANNER = 2;
    int LEADERBOARD = 3;
    int MEDIUM_RECTANGLE = 4;
    int WIDE_SKYSCRAPER = 5;
    int SMART_BANNER = 6;
    int FLUID = 7;
    int SEARCH = 8;


    // TODO Ad other ad types and set initial ad parameters for other ad types
    // TODO Try using Builder Pattern

    /*
     *** Set Up Ad Parameters ***
     */

    /**
     * Use this method to disable ads before getting GDPR permission
     *
     * @param enable if enabled ads can be displayed to user
     */
    void enableAds(boolean enable);

    void setPersonalizedAds(boolean personalizedAds);

    void setDebugMode(boolean debugMode);

    /**
     * Set size of banner ad. Caution: The ad size can only be set once on AdViewç
     *
     * @param size of the AdView
     */
    void setBannerAdSize(int size);

    void setBannerAdUnitId(String bannerAdId);

    void setInterstitialAdUnitId(String interstitialAdId);


    /*
     *** Set Up Ads ***
     */

    /**
     * Set up more than one ads
     *
     * @param layout   container layout for AdView
     * @param position is depends on type of layout used. RelativeLayout can display ads
     *                 on top, bottom or any other position
     */
    void setUpAds(ViewGroup layout, int position);

    void setUpBannerAds(final ViewGroup layout, final int position);

    void setUpInterstitialAds();

    // Banner Ads

    /**
     * Add AdView to a container layout
     */
    void addBanner();

    /**
     * Removes banner from AdView container
     */
    void removeBanner();

    /**
     * Shows banner view to user
     */
    void showBanner();

    /**
     * Hides banner view
     */
    void hideBanner();


    // Interstitial Ads

    void loadInterstitialAd();

    void showInterstitialAd();

    /*
     *** Life Cycle Methods ***
     */

    // Register life cycle with  getLifecycle().addObserver() inside an AppcompatActivity class

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume();

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause();

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy();


    /*
     ***  Ad Listener Methods ***
     */

    void addBannerAdListener(CustomAdListener adListener);

    void addInterstitialAdListener(CustomAdListener adListener);

    /**
     * Method to check internet connection
     *
     * @return true if internet connection is established
     */
    boolean isOnline(Context context);


}
