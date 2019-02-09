package com.stfactory.addemo.main;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.stfactory.addemo.R;
import com.stfactory.addemo.config.Constants;
import com.stfactory.admanager.AdManager;
import com.stfactory.admanager.CustomAdListener;

public class MainActivity extends AdCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO Test1 Memory Leak
    /*    AdView adView = findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        System.out.println("MainActivity adView: " + adView);


        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int error) {
                super.onAdFailedToLoad(error);
                System.out.println("MainActivity onAdFailedToLoad() error: " + error);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                System.out.println("MainActivity onAdLoaded()");

            }
        });*/

        checkUserConsentSafe();

        Button button = findViewById(R.id.buttonInterstitial);

        button.setOnClickListener(view -> {
            if (mAdManager != null) mAdManager.showInterstitialAd();

        });


        Button button2 = findViewById(R.id.button);

        button2.setOnClickListener(view -> resetConsent());


    }


    //  @Override
    public void setUpAds(boolean personalAds) {
        super.setUpAds(personalAds);

        if (mAdManager == null) return;

        RelativeLayout layoutContainer = findViewById(R.id.main_container);

        mAdManager.setDebugMode(true);
        mAdManager.setBannerAdSize(AdManager.SMART_BANNER);
        mAdManager.setBannerAdUnitId(Constants.BANNER_AD_UNIT_ID);
        mAdManager.setInterstitialAdUnitId(Constants.INTERSTITIAL_AD_UNIT_ID);
        mAdManager.setPersonalizedAds(personalAds);

        mAdManager.setUpAds(layoutContainer, RelativeLayout.ALIGN_BOTTOM);


        mAdManager.addBannerAdListener(new CustomAdListener() {
            @Override
            public void onAdLoaded() {
                System.out.println("MainActivity onAdLoaded()");

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                System.out.println("MainActivity onAdFailedToLoad() errorCode: " + errorCode);

            }
        });

        mAdManager.addInterstitialAdListener(new CustomAdListener() {

            @Override
            public void onAdClosed() {
                mAdManager.loadInterstitialAd();
            }
        });

    }

    //  @Override
    public void openPaidVersionLink() {
        System.out.println("MainActivity openPaidVersionLink()");

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
