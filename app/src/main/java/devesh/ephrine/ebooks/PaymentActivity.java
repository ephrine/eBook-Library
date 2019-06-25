/*
 * Copyright (c) 2019. Ephrine Apps
 * Code written by Devesh Chaudhari
 * Website: https://www.ephrine.in
 */

package devesh.ephrine.ebooks;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

public class PaymentActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    public String TAG = "Payment";
    BillingProcessor bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        bp = new BillingProcessor(this, null, this);
        bp.initialize();
        // or bp = BillingProcessor.newBillingProcessor(this, "YOUR LICENSE KEY FROM GOOGLE PLAY CONSOLE HERE", this);
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


        if (bp.isPurchased("android.test.purchased")) {
            Log.d(TAG, "onBillingInitialized: Already Purchased");
        } else {
            Log.d(TAG, "onBillingInitialized: Not Purchased");
        }

    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        /*
         * Called when requested PRODUCT ID was successfully purchased
         */
        Log.d(TAG, "---------------------\nonProductPurchased: \n Product id:" + productId + "\nDetails: " + details.toString() + "\n ");

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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            Log.d(TAG, "---------------------\nonActivityResult: \nData: " + data.toString() + "\n");
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    public void Pay(View v) {
        // bp.subscribe(this, "android.test.purchased");
        bp.purchase(this, "android.test.purchased");

    }

    void checkIfUserIsSusbcribed() {
        boolean purchaseResult = bp.loadOwnedPurchasesFromGoogle();
        if (purchaseResult) {
            TransactionDetails subscriptionTransactionDetails = bp.getSubscriptionTransactionDetails("SUBSCRIPTION_ID");
            if (subscriptionTransactionDetails != null) {
                //User is still subscribed
                //    showToast("User is still subscribed");
                Log.d(TAG, "checkIfUserIsSusbcribed: SUBSCRIBED");
            } else {
                //Not subscribed
                //  showToast("Not subscribed");
                Log.d(TAG, "checkIfUserIsSusbcribed: NOT SUBSCRIBED");
            }
        }
    }


}
