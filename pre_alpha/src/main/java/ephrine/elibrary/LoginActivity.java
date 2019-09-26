package ephrine.elibrary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;


public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;

    public String TAG = String.valueOf(R.string.app_name);
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
        //    Intent intent = new Intent(this, HomeActivity.class);
          //  finish();
            //  String message = editText.getText().toString();
            //intent.putExtra(EXTRA_MESSAGE, message);
          //  startActivity(intent);
        } else {
          //  login();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            finish();
            //  String message = editText.getText().toString();
            //intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        } else {
            login();
        }
    }

    public void login() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                //    new AuthUI.IdpConfig.GoogleBuilder().build(),
                                //  new AuthUI.IdpConfig.FacebookBuilder().build(),
                                //     new AuthUI.IdpConfig.TwitterBuilder().build(),
                                //     new AuthUI.IdpConfig.GitHubBuilder().build(),
                                //   new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.PhoneBuilder().build()
                                //        new AuthUI.IdpConfig.AnonymousBuilder().build()
                        ))
                        .build(),
                RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
        }
    }

    private void handleSignInResponse(int resultCode, @Nullable Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        // Successfully signed in
        if (resultCode == RESULT_OK) {
            finish();

            startSignedInActivity(response);
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
//                showSnackbar(R.string.sign_in_cancelled);
                return;
            }

            if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                //              showSnackbar(R.string.no_internet_connection);
                return;
            }

            if (response.getError().getErrorCode() == ErrorCodes.ANONYMOUS_UPGRADE_MERGE_CONFLICT) {
                //              Intent intent = new Intent(this, AnonymousUpgradeActivity.class).putExtra
                //                    (ExtraConstants.IDP_RESPONSE, response);
                //          startActivity(intent);
            }

            if (response.getError().getErrorCode() == ErrorCodes.ERROR_USER_DISABLED) {
                //        showSnackbar(R.string.account_disabled);
                return;
            }

            //  showSnackbar(R.string.unknown_error);
            Log.e(TAG, "Sign-in error: ", response.getError());
        }
    }

    private void startSignedInActivity(@Nullable IdpResponse response) {
        //    startActivity(SignedInActivity.createIntent(this, response));
        Intent intent = new Intent(this, HomeActivity.class);

        //  String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
