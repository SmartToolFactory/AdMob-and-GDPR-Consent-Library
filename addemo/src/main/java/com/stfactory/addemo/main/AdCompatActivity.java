package com.stfactory.addemo.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;
import com.stfactory.addemo.PrefManager;
import com.stfactory.addemo.R;
import com.stfactory.addemo.config.Constants;
import com.stfactory.admanager.AdManager;
import com.stfactory.admanager.AdManagerAdMob;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class AdCompatActivity extends AppCompatActivity {

    private ConsentForm form = null;
    public AdManager mAdManager;
    public PrefManager mPrefManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefManager = new PrefManager(getApplicationContext());
        mAdManager = new AdManagerAdMob(this);
        getLifecycle().addObserver(mAdManager);

    }


    /**
     * Checks if user is not in EAA and if not sets up the ads.
     * This method is useful if you wish to show ads only to users located outside of EEA
     */
    public void checkConsentOnlyForNonEEA() {

        ConsentInformation consentInformation = ConsentInformation.getInstance(getApplicationContext());
/*
        // TODO DEBUG
        consentInformation.addTestDevice("02878E3A2E829DD8152AFE05E84F57CC");
        consentInformation.setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
        */

        boolean isInEEA = mPrefManager.getBoolean(getString(R.string.key_consent_in_eea), true);

        // User is not in EEA and got it once via requestConsentInfoUpdate method,
        // and saved it to sharedPreferences, no need to get update from server again
        if (!isInEEA) {

            setUpAds(true);

            mPrefManager.putString(getString(R.string.key_consent_status), String.valueOf(ConsentStatus.PERSONALIZED.ordinal()));
            mPrefManager.putBoolean(getString(R.string.key_consent_in_eea), false);
            consentInformation.setConsentStatus(ConsentStatus.PERSONALIZED);

            return;
        }

        String[] publisherIds = {Constants.PUBLISHER_ID};

        // Called after inherited Activity's onResume method
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {

                // !!! Attempt to get user location info outside or before onConsentInfoUpdated
                // method returns false incorrectly sometimes

                // User's consent status successfully updated.
                boolean isRequestLocationInEeaOrUnknown = consentInformation.isRequestLocationInEeaOrUnknown();
                mPrefManager.putBoolean(getString(R.string.key_consent_in_eea), isRequestLocationInEeaOrUnknown);

                // User is not in EEA
                if (!isRequestLocationInEeaOrUnknown) {

                    consentInformation.setConsentStatus(ConsentStatus.PERSONALIZED);
                    mPrefManager.putString(getString(R.string.key_consent_status), String.valueOf(ConsentStatus.PERSONALIZED.ordinal()));
                    mPrefManager.putBoolean(getString(R.string.key_consent_in_eea), false);

                    setUpAds(true);
                    if (mAdManager != null) mAdManager.onResume();

                }

            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
            }
        });
    }


    /**
     * Check user consent and if consent has given set up ads with exception control
     */
    public void checkUserConsentSafe() {
        try {
            checkUserConsent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check user location and consent. First time this method is called, requestConsentInfoUpdate method
     * is called to get if user is located in EEA region and saved as a boolean to preferences
     */
    public void checkUserConsent() {

        ConsentInformation consentInformation = ConsentInformation.getInstance(getApplicationContext());

        boolean isInEEA = mPrefManager.getBoolean(getString(R.string.key_consent_in_eea), true);

        // User is not in EEA and got it once using requestConsentInfoUpdate method
        // and saved it to sharedPreferences, no need to process further
        if (!isInEEA) {

            setUpAds(true);

            mPrefManager.putString(getString(R.string.key_consent_status), String.valueOf(ConsentStatus.PERSONALIZED.ordinal()));
            consentInformation.setConsentStatus(ConsentStatus.PERSONALIZED);

            return;
        }

        // TODO DEBUG
        consentInformation.addTestDevice("02878E3A2E829DD8152AFE05E84F57CC");
        consentInformation.setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);

        ConsentStatus consentStatus = consentInformation.getConsentStatus();


        // User in EEA but consent given for ads
        if (consentStatus == ConsentStatus.PERSONALIZED) {

            setUpAds(true);

            mPrefManager.putString(getString(R.string.key_consent_status), String.valueOf(ConsentStatus.PERSONALIZED.ordinal()));

        } else if (consentStatus == ConsentStatus.NON_PERSONALIZED) {

            setUpAds(false);

            mPrefManager.putString(getString(R.string.key_consent_status), String.valueOf(ConsentStatus.NON_PERSONALIZED.ordinal()));

        } else {
            // Request Consent Information Online
            requestConsentInfoUpdate();
        }

    }


    private void requestConsentInfoUpdate() {

        ConsentInformation consentInformation = ConsentInformation.getInstance(getApplicationContext());


        String[] publisherIds = {Constants.PUBLISHER_ID};

        if (publisherIds[0].equals("")) {
            Toast.makeText(this, "Set Publisher Ids in Config.java", Toast.LENGTH_SHORT).show();
        }

        // Called after inherited Activity's onResume method
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {

                System.out.println("AdCompatActivity requestConsentInfoUpdate() consentStatus: " + consentStatus);


                // User's consent status successfully updated.
                boolean isRequestLocationInEeaOrUnknown = consentInformation.isRequestLocationInEeaOrUnknown();
                mPrefManager.putBoolean(getString(R.string.key_consent_in_eea), isRequestLocationInEeaOrUnknown);

                // !!! Trying to get user location info outside or before onConsentInfoUpdated
                // method returns false incorrectly sometimes

                // User is not in EEA
                if (!isRequestLocationInEeaOrUnknown) {
                    consentStatus = ConsentStatus.PERSONALIZED;
                    consentInformation.setConsentStatus(consentStatus);
                }

                switch (consentStatus) {
                    case UNKNOWN:
                        showConsentDialog();
                        break;

                    case PERSONALIZED:
                        mPrefManager.putString(getString(R.string.key_consent_status), String.valueOf(consentStatus.ordinal()));
                        setUpAds(true);
                        if (mAdManager != null) mAdManager.onResume();
                        break;

                    case NON_PERSONALIZED:
                        mPrefManager.putString(getString(R.string.key_consent_status), String.valueOf(consentStatus.ordinal()));
                        setUpAds(false);
                        if (mAdManager != null) mAdManager.onResume();
                        break;
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
            }
        });
    }


    private void showConsentDialog() {
        URL privacyUrl = null;
        try {
            privacyUrl = new URL(Constants.URI_PRIVACY_POLICY);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Handle error.
        }


        form = new ConsentForm.Builder(AdCompatActivity.this, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        // Consent form loaded successfully.
                        try {
                            if (!isFinishing()) form.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                    }

                    @Override
                    public void onConsentFormClosed(
                            ConsentStatus consentStatus, Boolean userPrefersAdFree) {

                        // Save Consent Status to SharedPreferences
                        mPrefManager.putString(getString(R.string.key_consent_status), String.valueOf(consentStatus.ordinal()));


                        // User prefers to use ad free version, open link for paid version and finish app
                        if (userPrefersAdFree) {
                            openPaidVersionLink();
                            finish();
                        } else {

                            if (consentStatus == ConsentStatus.PERSONALIZED) {
                                setUpAds(true);
                            } else if (consentStatus == ConsentStatus.NON_PERSONALIZED) {
                                setUpAds(false);

                            }

                            // Call onResume method of ad manager to reset banner ads
                            if (mAdManager != null) mAdManager.onResume();

                        }

                        // Consent form was closed.
                        form = null;

                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        // Consent form error.
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .withAdFreeOption()
                .build();

        form.load();

    }


    public int getConsentStatus() {
        ConsentInformation consentInformation = ConsentInformation.getInstance(getApplicationContext());
        return consentInformation.getConsentStatus().ordinal();
    }


    /**
     * This method sets up ads if user has already has given consent via dialog.
     * This method is good for secondary Activities and does not set up and display ads unless consent is
     * given prior to calling this method
     */
    public void initAdsOnConsent() {

        ConsentInformation consentInformation = ConsentInformation.getInstance(getApplicationContext());

        ConsentStatus consentStatus = consentInformation.getConsentStatus();

        // User in EEA but consent given for ads
        if (consentStatus == ConsentStatus.PERSONALIZED) {
            setUpAds(true);
        } else if (consentStatus == ConsentStatus.NON_PERSONALIZED) {
            setUpAds(false);
        }

        mPrefManager.putString(getString(R.string.key_consent_status), String.valueOf(consentStatus.ordinal()));

    }


    public void setUpAds(boolean personalAds) {

        if (mAdManager == null) {
            mAdManager = new AdManagerAdMob(this);
            getLifecycle().addObserver(mAdManager);
        }

        // TODO DEBUG
        mAdManager.setDebugMode(true);
        mAdManager.setBannerAdSize(AdManager.SMART_BANNER);
        mAdManager.setBannerAdUnitId(Constants.BANNER_AD_UNIT_ID);
        mAdManager.setInterstitialAdUnitId(Constants.INTERSTITIAL_AD_UNIT_ID);
        mAdManager.setPersonalizedAds(personalAds);

    }

    public void showInterstitialAd() {
        if (mAdManager != null) mAdManager.showInterstitialAd();
    }


    /**
     * This method opens link to paid version of this app. This method could be abstract, but if you need to
     * use ads in activities other than main it's unnecessary to override.
     */
    public void openPaidVersionLink() {

    }

    public void resetConsent() {
        ConsentInformation consentInformation = ConsentInformation.getInstance(getApplicationContext());

        consentInformation.setConsentStatus(ConsentStatus.UNKNOWN);

        mPrefManager.putString(getString(R.string.key_consent_status), String.valueOf(ConsentStatus.UNKNOWN));

        Toast.makeText(this, "Consents is now: " + consentInformation.getConsentStatus() + ", restart app to see effects", Toast.LENGTH_SHORT).show();

    }
}