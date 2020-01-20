/*
 * Copyright (c) 2019. Ephrine Apps
 * Code written by Devesh Chaudhari
 * Website: https://www.ephrine.in
 */

package devesh.ephrine.ebooks;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import io.fabric.sdk.android.Fabric;


public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;

    public String TAG = String.valueOf(R.string.app_name);
    VideoView videoview;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        videoview = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.loginvideo);
        videoview.setVideoURI(uri);
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        videoview.start();

//FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Fabric.with(this, new Crashlytics());

        if (currentUser != null) {
            Crashlytics.setUserIdentifier(currentUser.getUid());

            Intent intent = new Intent(this, HomeActivity.class);
            finish();
            //  String message = editText.getText().toString();
            //intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        } else {
            //login();
        }


    }

    @Override
    protected void onPause() {
        videoview.stopPlayback();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoview = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.loginvideo);
        videoview.setVideoURI(uri);
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        videoview.start();
    }

    public void login() {

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                      //  .setIsSmartLockEnabled(false)
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
        LoginActivity.this.finish();
    }

    public void PPClick(View v) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://ephrine.in/privacy-policy"));
        startActivity(intent);


    }

    public void LoginNow(View v) {

        login();
    }
}
