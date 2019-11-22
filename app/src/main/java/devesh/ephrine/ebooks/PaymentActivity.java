/*
 * Copyright (c) 2019. Ephrine Apps
 * Code written by Devesh Chaudhari
 * Website: https://www.ephrine.in
 */

package devesh.ephrine.ebooks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

public class PaymentActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    //public String TAG = "Payment";
    public String TAG = String.valueOf(R.string.app_name);
    BillingProcessor bp;
    SharedPreferences sharedPreferences;


    String ProductId = "001";
    String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnFGhosMBk28VLfMXhRDe4ZXpqCYGxSmGYSLmpjJxjNlvubEecRnhbEGghCXs/tIPaNRSR7Oow2J1HUAjmd04AZRI3FxKsdTmpwa2rJUu9BaR7yrqvNzMchfLieKPkiyVnTa7QrUHOX4S168KUSf6lbgPkqspgoRCwT446pzYpKlRFkQGIH/ZGd0Wblpze+vIjhctzYcrzmzaTisRCyK0QJRt5n6fQeCCS+3iVeqpHctD5gSe6Fqkz7q2lmbyEXkKXlYmcNrvtHm6eonolVi1rqmEZBGLOeI3DoBTuvn71QMChKx+Y9DRwVynARG1VhFhvmuOpr8hjDMUknNj7aSAdwIDAQAB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);


        bp = new BillingProcessor(this, LICENSE_KEY, this);
        bp.initialize();

        sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);    // or bp = BillingProcessor.newBillingProcessor(this, "YOUR LICENSE KEY FROM GOOGLE PLAY CONSOLE HERE", this);
        // See below on why this is a useful alternative
        checkIfUserIsSusbcribed();

    }

    @Override
    public void onBillingInitialized() {
        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
        Log.d(TAG, "-----------------------\nonBillingInitialized: READY !");
        // bp.getPurchaseTransactionDetails("android.test.purchased");

        checkIfUserIsSusbcribed();

        boolean isSubscribes = bp.isSubscribed(ProductId);
        Log.d(TAG, "---------\nonBillingInitialized: isSubscribed: " + isSubscribes);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getString(R.string.pref_purchase), isSubscribes);
        editor.apply();

    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        /*
         * Called when requested PRODUCT ID was successfully purchased
         */
        Log.d(TAG, "---------------------\nonProductPurchased: \n Product id:" + productId + "\nDetails: " + details.toString() + "\n ");

        boolean isSubscribes = bp.isSubscribed(productId);
        Log.d(TAG, "---------\nonProductPurchased: isSubscribed: " + isSubscribes);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getString(R.string.pref_purchase), isSubscribes);
        editor.apply();
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        /*
         * Called when some error occurred. See Constants class for more details
         *
         * Note - this includes handling the case where the user canceled the buy dialog:
         * errorCode = Constants.BILLING_RESPONSE_RESULT_USER_CANCELED
         */
        if (error != null) {
            Log.e(TAG, "---------------------\nonBillingError: \nError: " + error.toString() + "\n");
        } else {
            Log.e(TAG, "---------------------\nonBillingError: Error ");
        }


    }

    @Override
    public void onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
        Log.d(TAG, "---------------------\nonPurchaseHistoryRestored: Already Purchased");
        checkIfUserIsSusbcribed();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            Log.d(TAG, "-------\nonActivityResult: requestCode:" + requestCode + "\n resultCode:" + resultCode + "\n data:" + data);

            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void subscribe(View v) {
        bp.subscribe(this, ProductId);

    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    // Not Working checkIfUserIsSusbcribed() code
    void checkIfUserIsSusbcribed() {
        boolean purchaseResult = bp.loadOwnedPurchasesFromGoogle();
        if (purchaseResult) {
            TransactionDetails subscriptionTransactionDetails = bp.getSubscriptionTransactionDetails(ProductId);
            if (subscriptionTransactionDetails != null) {
                //User is still subscribed
                Log.d(TAG, "-----------\n checkIfUserIsSusbcribed:  subscribed");

            } else {
                //Not subscribed
                Log.d(TAG, "-----------\ncheckIfUserIsSusbcribed: Not subscribed");

            }
        }
    }


}
