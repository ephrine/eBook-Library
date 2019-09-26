package ephrine.elibrary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseInfo;
import com.anjlab.android.iab.v3.TransactionDetails;

public class PaymentActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    public String TAG = "Payment";
    BillingProcessor bp;


    // public String ProductId;
    //   public String LicenceKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgVhaeCsYFGJZAXP6qW7SLiCEn2PxP0R35naAq7LiNouHngz+wZ5xicmNA+9yqMh5FlcWyWc87lDQOLwMbFLv03nlh/b/bcTuz16I/ispF71u9o+b4tWuBLo21LNZBYMD0eXcbNBT/fN7KVkYm/ac/p02L3J1yQET/GWjSbqronnps0HnrA6C+DR4vQStM20/Oi7mIUCq0ktmxKUCQ9kwceWijIFVDGV6UwfAXT0edtGhokzrTA9eD9TpQWSCYvUjDNwKNBPEMeiAR6DuUoRzkw+Y2FKidFqJ4iFX4u8901SQKbeS9pcJ+b2FXOHlPvwBqtMy12+3YolKJdrIFVHHAQIDAQAB";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        SecKeys SecKeys = new SecKeys();

        bp = new BillingProcessor(this, SecKeys.LICENSE_KEY, this);
        bp.initialize();
        // or bp = BillingProcessor.newBillingProcessor(this, "YOUR LICENSE KEY FROM GOOGLE PLAY CONSOLE HERE", this);
        // See below on why this is a useful alternative
        checkIfUserIsSusbcribed();

    }

    // IBillingHandler implementation

    @Override
    public void onBillingInitialized() {
        SecKeys SecKeys = new SecKeys();

        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
        bp.loadOwnedPurchasesFromGoogle();

        bp.getSubscriptionTransactionDetails(SecKeys.SUBSCRIPTION_ID);
        TransactionDetails f = bp.getSubscriptionTransactionDetails(SecKeys.SUBSCRIPTION_ID);
        if (f != null) {
            PurchaseInfo fd = f.purchaseInfo;
            String d = f.purchaseInfo.purchaseData.purchaseTime.toString();  // Purchase Date
            String x = f.purchaseInfo.responseData;
            Log.d(TAG, "--------------------- Trans details: \n" + d + "\n\n\n" + x);
            Log.d(TAG, "onBillingInitialized: Trans details:" + f.toString());
        } else {
            Log.d(TAG, "onBillingInitialized: Trans details: NULL");
        }


        checkIfUserIsSusbcribed();


    }

    public void Pay(View v) {
        SecKeys SecKeys = new SecKeys();

        bp.subscribe(this, SecKeys.SUBSCRIPTION_ID);

    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        /*
         * Called when requested PRODUCT ID was successfully purchased
         */
        SecKeys SecKeys = new SecKeys();

        Log.d(TAG, "onProductPurchased: " + SecKeys.SUBSCRIPTION_ID + "\n\n" + details.toString());

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        /*
         * Called when some error occurred. See Constants class for more details
         *
         * Note - this includes handling the case where the user canceled the buy dialog:
         * errorCode = Constants.BILLING_RESPONSE_RESULT_USER_CANCELED
         */
        Log.d(TAG, "onBillingError: " + errorCode);

    }

    @Override
    public void onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            Log.d(TAG, "onActivityResult: " + data.toString() + "\n" + resultCode);
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


    void checkIfUserIsSusbcribed() {
        SecKeys SecKeys = new SecKeys();

        boolean purchaseResult = bp.loadOwnedPurchasesFromGoogle();
        if (purchaseResult) {
            TransactionDetails subscriptionTransactionDetails = bp.getSubscriptionTransactionDetails(SecKeys.SUBSCRIPTION_ID);
            if (subscriptionTransactionDetails != null) {
                //User is still subscribed
                Log.d(TAG, "checkIfUserIsSusbcribed: User is still subscribed");
            } else {
                //Not subscribed
                Log.d(TAG, "checkIfUserIsSusbcribed: Not subscribed");
            }
        }
    }


}
