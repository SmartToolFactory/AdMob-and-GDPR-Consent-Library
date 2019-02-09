package com.stfactory.addemo.config;


public interface Constants {


    /*
     * *** Ad Network Ids ***
     */

    // ADMOB
    // This is the id of the publisher, same for all apps
    // TODO Change Publisher, banner and interstitial ids
    String PUBLISHER_ID = "pub-5656345011444660";
    // App specific info
    String APP_AD_ID = "ca-app-pub-5656345011444660~1085856744";
    String BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";
    String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";

    // Consent
    int CONSENT_PERSONALIZED = 2;
    int CONSENT_NON_PERSONALIZED = 1;
    int CONSENT_UNKNOWN = 0;

    /*
     * *** URIS ***
     */

    // TODO Set a privacy policy URL
    String URI_PRIVACY_POLICY = "http://www.google.com";

}