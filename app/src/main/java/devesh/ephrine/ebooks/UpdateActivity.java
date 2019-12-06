/*
 * Copyright (c) 2019. Ephrine Apps
 * Code written by Devesh Chaudhari
 * Website: https://www.ephrine.in
 */

package devesh.ephrine.ebooks;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

public class UpdateActivity extends AppCompatActivity {

    String TAG="Notification";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "FCM Data: \n" + "Key: " + key + " Value: " + value);
                //   Toast.makeText(this, "Key: " + key + " Value: " + value, Toast.LENGTH_LONG).show();

                if (key.equals(getString(R.string.FCM_update_url))) {
                    // Open Play Store for Update
                    String url = value.toString();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);

                }

                if (key.equals(getString(R.string.FCM_browser_url))) {
                    String url = value.toString();
                    OpenChromeTab(url);
                    /*Intent intent = new Intent(this, BrowserActivity.class);
                    intent.putExtra("URL2Load", url);
                    startActivity(intent);
                    */

                }
                if (key.equals(getString(R.string.FCM_open_app_url))) {

                    String url = value.toString();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }


            }
        }





      //  Intent intent = new Intent(Intent.ACTION_VIEW);
       // intent.setData(Uri.parse(getString(R.string.update_url)));
        //startActivity(intent);

        //UpdateActivity.this.finish();


    }



    void OpenChromeTab(String url){

        CustomTabsIntent.Builder builder1 = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder1.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));


    }

}
