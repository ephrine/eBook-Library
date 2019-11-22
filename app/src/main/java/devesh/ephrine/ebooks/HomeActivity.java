/*
 * Copyright (c) 2019. Ephrine Apps
 * Code written by Devesh Chaudhari
 * Website: https://www.ephrine.in
 */

package devesh.ephrine.ebooks;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import devesh.ephrine.ebooks.mRecycleView.MyAdapter;
import devesh.ephrine.ebooks.mRecycleView.MyLibraryAdapter;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class HomeActivity extends AppCompatActivity {

    public String TAG = String.valueOf(R.string.app_name);
    //  public UserProfileManager mUser;
    ArrayList<HashMap<String, String>> StoreBooksList = new ArrayList();
    RecyclerView recyclerView;
    RecyclerView myLibraryBooksrecyclerView;
    RecyclerView.LayoutManager layoutManager;
    MyAdapter mAdapter;
    MyLibraryAdapter MyLibraryAdapter;
    View includeHomeView;
    View includeAccountView;
    View includeMyLibraryBooks;
    View includeAbout;

    FirebaseDatabase database;

    ArrayList<HashMap<String, String>> MyLibraryBookHashmap = new ArrayList();
    String UserUniqueID;
    String UserPhno;
    ProgressBar HomeLoading;

    UserProfileManager mUser;

    DataSnapshot BookLibraryDB;
    BottomNavigationView navigation;

    AdRequest adRequest;
    SmoothProgressBar smoothProgressBar;
    AdView mAdView;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.MyLibraryMenu:

                    getSupportFragmentManager().popBackStack();

                    includeHomeView.setVisibility(View.GONE);
                    includeAccountView.setVisibility(View.GONE);
                    includeMyLibraryBooks.setVisibility(View.VISIBLE);
                    includeAbout.setVisibility(View.GONE);

                    LoadMyLibrary();
                    //     navigation.setSelectedItemId(R.id.MyLibraryMenu);


                    break;

                case R.id.BrowseStoreMenu:

                    getSupportFragmentManager().popBackStack();

                    includeHomeView.setVisibility(View.VISIBLE);
                    includeAccountView.setVisibility(View.GONE);
                    includeMyLibraryBooks.setVisibility(View.GONE);
                    includeAbout.setVisibility(View.GONE);

                    loadbooks();


                    break;

                case R.id.settingMenu:

           /*         includeHomeView.setVisibility(View.GONE);
                    includeAccountView.setVisibility(View.GONE);
                    includeMyLibraryBooks.setVisibility(View.GONE);
                    includeAbout.setVisibility(View.GONE);
*/
                    getSupportFragmentManager()
                            .beginTransaction()

                            .replace(R.id.settings_container, new SettingsFragment())
                            .addToBackStack(null)
                            .commit();

                    break;


            }
            /*
            if (item.getItemId() == R.id.MyLibraryMenu) {

            }
            else if (item.getItemId() == R.id.myAccountMenu) {
                getSupportFragmentManager().popBackStack();

                includeHomeView.setVisibility(View.GONE);
                includeAccountView.setVisibility(View.VISIBLE);
                includeMyLibraryBooks.setVisibility(View.GONE);
                includeAbout.setVisibility(View.GONE);

                LoadMyAccount();
                navigation.setSelectedItemId(R.id.myAccountMenu);


            } else if (item.getItemId() == R.id.BrowseStoreMenu) {
                navigation.setSelectedItemId(R.id.BrowseStoreMenu);



            } else if (item.getItemId() == R.id.settingMenu) {
                 navigation.setSelectedItemId(R.id.settingMenu);

            } else if (item.getItemId() == R.id.includeMyAccount) {

                includeHomeView.setVisibility(View.GONE);
                includeAccountView.setVisibility(View.VISIBLE);
                includeMyLibraryBooks.setVisibility(View.GONE);
                includeAbout.setVisibility(View.GONE);
          //      navigation.setSelectedItemId(R.id.includeMyAccount);

            }
            else if (item.getItemId() == R.id.AboutMenu) {
                getSupportFragmentManager().popBackStack();

                includeHomeView.setVisibility(View.GONE);
                includeAccountView.setVisibility(View.GONE);
                includeMyLibraryBooks.setVisibility(View.GONE);
                includeAbout.setVisibility(View.VISIBLE);

            }*/

            return true;
        }
    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        adRequest = new AdRequest.Builder().build();

        //     Toolbar toolbar = findViewById(R.id.toolbar);
        //    setSupportActionBar(toolbar);
        MobileAds.initialize(this, getString(R.string.AdMob_App_Id));
        loadAds();


        database = FirebaseDatabase.getInstance();


        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {

            UserPhno = auth.getCurrentUser().getPhoneNumber();

            UserUniqueID = UserPhno.replace("+", "x");
            mUser = new UserProfileManager(this);

        } else {

            // not signed in

        }


        includeHomeView = (View) findViewById(R.id.includeHome);
        includeAccountView = (View) findViewById(R.id.includeMyAccount);
        includeMyLibraryBooks = (View) findViewById(R.id.includeMyLibrary);
        includeAbout = (View) findViewById(R.id.includeAbout);

        includeAccountView.setVisibility(View.GONE);
        includeMyLibraryBooks.setVisibility(View.VISIBLE);
        includeAbout.setVisibility(View.GONE);
        includeHomeView.setVisibility(View.GONE);
        if (includeAccountView.getVisibility() != View.GONE) {

        }
        HomeLoading = (ProgressBar) findViewById(R.id.progressBarHome);
        HomeLoading.setVisibility(View.VISIBLE);

  /*      DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);
*/
        // loadbooks();
        //   myLibraryBooksrecyclerView.removeAllViews();

        navigation = (BottomNavigationView) findViewById(R.id.BottomNavBar);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        LoadMyLibrary();


    }

  /*  @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.MyLibraryMenu) {
            getSupportFragmentManager().popBackStack();

            includeHomeView.setVisibility(View.GONE);
            includeAccountView.setVisibility(View.GONE);
            includeMyLibraryBooks.setVisibility(View.VISIBLE);
            includeAbout.setVisibility(View.GONE);

            LoadMyLibrary();

        } else if (id == R.id.myAccountMenu) {
            getSupportFragmentManager().popBackStack();

            includeHomeView.setVisibility(View.GONE);
            includeAccountView.setVisibility(View.VISIBLE);
            includeMyLibraryBooks.setVisibility(View.GONE);
            includeAbout.setVisibility(View.GONE);

            LoadMyAccount();

        } else if (id == R.id.BrowseStoreMenu) {
            getSupportFragmentManager().popBackStack();

            includeHomeView.setVisibility(View.VISIBLE);
            includeAccountView.setVisibility(View.GONE);
            includeMyLibraryBooks.setVisibility(View.GONE);
            includeAbout.setVisibility(View.GONE);

            loadbooks();



        } else if (id == R.id.settingMenu) {
            includeHomeView.setVisibility(View.GONE);
            includeAccountView.setVisibility(View.GONE);
            includeMyLibraryBooks.setVisibility(View.GONE);
            includeAbout.setVisibility(View.GONE);

            getSupportFragmentManager()
                    .beginTransaction()

                    .replace(R.id.settings_container, new SettingsFragment())
                    .addToBackStack(null)
                    .commit();

        } else if (id == R.id.includeMyAccount) {

            includeHomeView.setVisibility(View.GONE);
            includeAccountView.setVisibility(View.VISIBLE);
            includeMyLibraryBooks.setVisibility(View.GONE);
            includeAbout.setVisibility(View.GONE);

        }
        else if (id == R.id.AboutMenu) {
            getSupportFragmentManager().popBackStack();

            includeHomeView.setVisibility(View.GONE);
            includeAccountView.setVisibility(View.GONE);
            includeMyLibraryBooks.setVisibility(View.GONE);
            includeAbout.setVisibility(View.VISIBLE);

        }

        /* else if (id == R.id.nav_send) {
        }*/

    /*  @Override
      public void onBackPressed() {
          DrawerLayout drawer = findViewById(R.id.drawer_layout);
          if (drawer.isDrawerOpen(GravityCompat.START)) {
              drawer.closeDrawer(GravityCompat.START);
          } else {
              super.onBackPressed();
          }
      }
  */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_refresh) {
            Toast.makeText(this, "Refreshing....", Toast.LENGTH_SHORT).show();

            includeMyLibraryBooks = (View) findViewById(R.id.includeMyLibrary);
            if (includeHomeView.getVisibility() != View.GONE) {
                // recyclerView.removeAllViews();
                loadbooks();
            }
            if (includeAccountView.getVisibility() != View.GONE) {
                LoadMyAccount();
            }
            if (includeMyLibraryBooks.getVisibility() != View.GONE) {
                //  myLibraryBooksrecyclerView.removeAllViews();
                LoadMyLibrary();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*      DrawerLayout drawer = findViewById(R.id.drawer_layout);
          drawer.closeDrawer(GravityCompat.START);
          return true;
      }

  */
    public void LoadMyAccount() {

        if (includeAccountView.getVisibility() == View.VISIBLE) {
            LinearLayout myAccountView = (LinearLayout) findViewById(R.id.LLAccountView);
            LinearLayout myAccountEdit = (LinearLayout) findViewById(R.id.LLAccountEdit);
            myAccountEdit.setVisibility(View.GONE);
            myAccountView.setVisibility(View.VISIBLE);

            TextView UserPhoneTx = (TextView) findViewById(R.id.myAccountPhoneNotextView7);
            TextView UserNameTX = (TextView) findViewById(R.id.myAccountUserNametextView6);
            TextView UserEmailTx = (TextView) findViewById(R.id.myAccountEmailIdtextView9);
            TextView UserAgeTx = (TextView) findViewById(R.id.myAccountAgetextView8);

            if (mUser.UserName != null) {
                UserNameTX.setText(mUser.UserName);
            } else {
                UserNameTX.setText(" ");
            }

            if (mUser.UserPhno != null) {
                UserPhoneTx.setText(mUser.UserPhno);
            } else {
                UserPhoneTx.setText(" ");
            }

            if (mUser.UserEmail != null) {
                UserEmailTx.setText(mUser.UserEmail);
            } else {
                UserEmailTx.setText(" ");
            }

            if (mUser.UserAge != null) {
                UserAgeTx.setText(mUser.UserAge);
            } else {
                UserAgeTx.setText(" ");
            }


        }


    }

  /*  private BillingClient billingClient;

public void billing(){
    billingClient = BillingClient.newBuilder(HomeActivity.this).setListener(this).build();
    billingClient.startConnection(new BillingClientStateListener() {
        @Override
        public void onBillingSetupFinished(BillingResult billingResult) {
            if (billingResult.getResponseCode() == BillingResponse.OK) {
                // The BillingClient is ready. You can query purchases here.
            }
        }
        @Override
        public void onBillingServiceDisconnected() {
            // Try to restart the connection on the next request to
            // Google Play by calling the startConnection() method.
        }
    });



    List<String> skuList = new ArrayList<> ();
    skuList.add("premium_upgrade");
    skuList.add("gas");
    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
    billingClient.querySkuDetailsAsync(params.build(),
            new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(BillingResult billingResult,
                                                 List<SkuDetails> skuDetailsList) {
                    // Process the result.
                }
            });


    if (result.getResponseCode() == BillingResponse.OK && skuDetailsList != null) {
        for (SkuDetails skuDetails : skuDetailsList) {
            String sku = skuDetails.getSku();
            String price = skuDetails.getPrice();
            if ("premium_upgrade".equals(sku)) {
                premiumUpgradePrice = price;
            } else if ("gas".equals(sku)) {
                gasPrice = price;
            }
        }


        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();
        int responseCode = billingClient.launchBillingFlow(flowParams);
    }

}

    @Override
    void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingResponse.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingResponse.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
        }
    }

*/

    public void SaveData(View v) {
        if (includeAccountView.getVisibility() == View.VISIBLE) {
            LinearLayout myAccountView = (LinearLayout) findViewById(R.id.LLAccountView);
            LinearLayout myAccountEdit = (LinearLayout) findViewById(R.id.LLAccountEdit);

            HashMap<String, String> ph = new HashMap<String, String>();

            TextInputEditText UserNameET = (TextInputEditText) findViewById(R.id.UserNameTextInput);
            TextInputEditText UserEmailET = (TextInputEditText) findViewById(R.id.UserEmailTextInput);
            TextInputEditText UserAgeET = (TextInputEditText) findViewById(R.id.UserAgeTextInput);

            if (UserNameET.getText() != null) {
                ph.put("UserName", UserNameET.getText().toString());
            }
            if (UserEmailET.getText() != null) {
                ph.put("UserEmail", UserEmailET.getText().toString());
            }
            if (UserAgeET.getText() != null) {
                ph.put("UserAge", UserAgeET.getText().toString());
            }
            ph.put("UserPhoneNo", mUser.UserPhno);

            mUser.UpdateProfile(ph);

            myAccountEdit.setVisibility(View.GONE);
            myAccountView.setVisibility(View.VISIBLE);
            LoadMyAccount();

            TextView UserNameTX = (TextView) findViewById(R.id.myAccountUserNametextView6);
            TextView UserEmailTx = (TextView) findViewById(R.id.myAccountEmailIdtextView9);
            TextView UserAgeTx = (TextView) findViewById(R.id.myAccountAgetextView8);

            if (ph.get("UserName") != null) {
                UserNameTX.setText(ph.get("UserName"));
            }
            if (ph.get("UserEmail") != null) {
                UserEmailTx.setText(ph.get("UserEmail"));
            }
            if (ph.get("UserAge") != null) {
                UserAgeTx.setText(ph.get("UserAge"));
            }

        }

    }

    public void EditAccount(View v) {
        LinearLayout myAccountView = (LinearLayout) findViewById(R.id.LLAccountView);
        LinearLayout myAccountEdit = (LinearLayout) findViewById(R.id.LLAccountEdit);
        myAccountEdit.setVisibility(View.VISIBLE);
        myAccountView.setVisibility(View.GONE);

        if (myAccountEdit.getVisibility() != View.GONE) {

            TextInputEditText UserNameET = (TextInputEditText) findViewById(R.id.UserNameTextInput);
            TextInputEditText UserEmailET = (TextInputEditText) findViewById(R.id.UserEmailTextInput);
            TextInputEditText UserAgeET = (TextInputEditText) findViewById(R.id.UserAgeTextInput);

            if (mUser.UserName != null) {
                UserNameET.setText(mUser.UserName);
            } else {
                UserNameET.setText("");
            }


            if (mUser.UserEmail != null) {
                UserEmailET.setText(mUser.UserEmail);
            } else {
                UserEmailET.setText("");
            }

            if (mUser.UserAge != null) {
                UserAgeET.setText(mUser.UserAge);
            } else {
                UserAgeET.setText("");
            }


        }

    }

    private void loadbooks() {

        if (includeHomeView.getVisibility() != View.GONE) {

            LoadWebViewContent(getString(R.string.Book_Store_url) + "?uid=" + UserUniqueID);


      /*     recyclerView = (RecyclerView) findViewById(R.id.mRecycleView);
            recyclerView.removeAllViews();
            recyclerView.removeAllViewsInLayout();
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true);

            // use a linear layout manager
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            //  recyclerView.setLayoutManager(new GridLayoutManager(this, 2));


            // Write a message to the database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("ebooksapp/library/books");
            // Read from the database
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    //   String value = dataSnapshot.getValue(String.class);
                    //   Log.d(TAG, "Value is: " + value);
                    StoreBooksList.clear();


                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        HashMap<String, String> StoreBook = new HashMap<String, String>();

                        String BookName = postSnapshot.child("bookname").getValue(String.class);
                        String BookAuthor = postSnapshot.child("bookauthor").getValue(String.class);
                        String BookYear = postSnapshot.child("bookyear").getValue(String.class);
                        String BookCategory = postSnapshot.child("bookcategory").getValue(String.class);
                        String BookCover = postSnapshot.child("bookcover").getValue(String.class);
                        String BookURL = postSnapshot.child("bookurl").getValue(String.class);
                        String BookID = String.valueOf(postSnapshot.child("bookid").getValue(Integer.class));


                        StoreBook.put("bookname", BookName);
                        StoreBook.put("bookauthor", BookAuthor);
                        StoreBook.put("bookyear", BookYear);
                        StoreBook.put("bookcategory", BookCategory);
                        StoreBook.put("bookcover", BookCover);
                        StoreBook.put("bookurl", BookURL);
                        StoreBook.put("bookid", BookID);

                        StoreBooksList.add(StoreBook);

                        Log.d(TAG, "-------------------onDataChange:\n Book Details:\n" + StoreBook);

                        if (StoreBooksList != null) {
                            if (includeHomeView.getVisibility() != View.GONE) {

                                // specify an adapter (see also next example)
                                mAdapter = new MyAdapter(HomeActivity.this, StoreBooksList);
                                recyclerView.setAdapter(mAdapter);
                            }
                        }

                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });    */


        }

    }

    public void LoadMyLibrary() {


        if (includeMyLibraryBooks.getVisibility() != View.GONE) {
            myLibraryBooksrecyclerView = (RecyclerView) findViewById(R.id.myBookLibraryRecycleView);
            // myLibraryBooksrecyclerView.removeAllViews();
            // myLibraryBooksrecyclerView.removeAllViewsInLayout();
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            myLibraryBooksrecyclerView.setHasFixedSize(true);
            // use a linear layout manager
            //layoutManager = new LinearLayoutManager(this);
//            myLibraryBooksrecyclerView.setLayoutManager(layoutManager);
            myLibraryBooksrecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            // specify an adapter (see also next example)

            DatabaseReference MyLibraryBooksDB = database.getReference("ebooksapp/users/" + UserUniqueID + "/mylibrary");
            MyLibraryBooksDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    //   String value = dataSnapshot.getValue(String.class);
                    if (dataSnapshot != null) {
                        MyLibraryBookHashmap.clear();
                        BookLibraryDB = dataSnapshot;
                        //      GetDirFiles();

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            String BookID = "null";
                            if (postSnapshot.child("bookid").getValue(String.class) != null) {
                                BookID = postSnapshot.child("bookid").getValue(String.class);
                                //MyLibraryBookList.add(BookID);

                                DatabaseReference GetBookDB;
                                GetBookDB = FirebaseDatabase.getInstance().getReference("ebooksapp/library/books/" + BookID);
                                GetBookDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // This method is called once with the initial value and again
                                        // whenever data at this location is updated.
                                        //   String value = dataSnapshot.getValue(String.class);
                                        //   Log.d(TAG, "Value is: " + value);

                                        HashMap<String, String> bk = new HashMap<String, String>();

                                        String BookName = dataSnapshot.child("bookname").getValue(String.class);
                                        String BookAuthor = dataSnapshot.child("bookauthor").getValue(String.class);
                                        String BookYear = dataSnapshot.child("bookyear").getValue(String.class);
                                        String BookCategory = dataSnapshot.child("bookcategory").getValue(String.class);
                                        String BookCover = dataSnapshot.child("bookcover").getValue(String.class);
                                        String BookURL = dataSnapshot.child("bookurl").getValue(String.class);
                                        String BookID = String.valueOf(dataSnapshot.child("bookid").getValue(Integer.class));

                                        bk.put("bookid", BookID);
                                        bk.put("bookname", BookName);
                                        bk.put("bookauthor", BookAuthor);
                                        bk.put("bookyear", BookYear);
                                        bk.put("bookcategory", BookCategory);
                                        bk.put("bookcover", BookCover);
                                        bk.put("bookurl", BookURL);

                                        MyLibraryBookHashmap.add(bk);

                                        Log.d(TAG, "-------------------onDataChange:\n Book Details:\nBookID:" + BookID + "\nMyLibraryBookHashmap\n" + MyLibraryBookHashmap);

                                        myLibraryBooksrecyclerView.removeAllViews();
                                        myLibraryBooksrecyclerView.removeAllViewsInLayout();

                                        MyLibraryAdapter = new MyLibraryAdapter(HomeActivity.this, MyLibraryBookHashmap);
                                        myLibraryBooksrecyclerView.setAdapter(MyLibraryAdapter);
                                        HomeLoading.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        // Failed to read value
                                        Log.w(TAG, "Failed to read value.", error.toException());
                                    }
                                });

                            }
                            Log.i(TAG, "onDataChange: ------------------\n Get User Library\n" + BookID + "\n");
                            //Log.i(TAG, "onDataChange: ------------------\n My Book Library\n" + MyLibraryBookList);

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
            MyLibraryBooksDB.keepSynced(true);

            loadAds();

        } else {
            HomeLoading.setVisibility(View.GONE);

        }

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    void LoadWebViewContent(String url) {

        String URL = url;

        WebView myWebView = (WebView) findViewById(R.id.webView1);
        smoothProgressBar = (SmoothProgressBar) findViewById(R.id.progressBarhorizontal1);
        myWebView.loadUrl("about:blank");
        myWebView.loadUrl(URL);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        myWebView.setWebViewClient(new MyWebViewClient());

    }

    public void pay(View v) {
        Intent intent = new Intent(this, PaymentActivity.class);

        //  String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    void loadAds() {

        if (findViewById(R.id.adView) != null) {

            mAdView = findViewById(R.id.adView);
            mAdView.loadAd(adRequest);

        }


    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        public static final String UserName = "pref_username";
        public static final String UserEmail = "pref_useremail";
        public String TAG = String.valueOf(R.string.app_name);
        UserProfileManager mUser;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            //  setPreferencesFromResource(R.xml.root_preferences, rootKey);
            addPreferencesFromResource(R.xml.root_preferences);

            mUser = new UserProfileManager(getContext());
            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
            Preference pref = findPreference("pref_userphno");
            pref.setSummary(mUser.UserPhno);


            Preference PrefSignout = (Preference) findPreference("signout");
            PrefSignout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Do you want to Sign-out ?");
// Add the buttons
                    builder.setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            FirebaseAuth.getInstance().signOut();
                            deleteAppData();
                            Toast.makeText(getContext(), "Signed out !", Toast.LENGTH_SHORT).show();

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
// Set other dialog properties

// Create the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();


                    return true;
                }
            });


            Preference PrefDeleteBooks = (Preference) findPreference("deletebooks");
            PrefDeleteBooks.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Do you want to remove all offline book cache ?");
// Add the buttons
                    builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            DeleteFiles();
                            Toast.makeText(getContext(), "Removed Successfully !", Toast.LENGTH_SHORT).show();

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
// Set other dialog properties

// Create the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    return true;
                }
            });


            Preference PrefContact = (Preference) findPreference("contact");
            PrefContact.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://www.ephrine.in/contact"));
                    startActivity(intent);


                    return true;
                }
            });

            Preference Prefprivacy = (Preference) findPreference("privacyp");
            Prefprivacy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://www.ephrine.in/privacy-policy"));
                    startActivity(intent);

                    return true;
                }
            });


            Preference Prefwebsite = (Preference) findPreference("epwebsite");
            Prefwebsite.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://www.ephrine.in/"));
                    startActivity(intent);

                    return true;
                }
            });


        }

        private void deleteAppData() {
            try {
                // clearing app data
                String packageName = getContext().getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear " + packageName);
                Log.i(TAG, "App Data Cleared !!");

                Intent intent = new Intent(getContext(), HomeActivity.class);

                //  String message = editText.getText().toString();
                //intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            mUser.UpdateProfileX();
            Log.d(TAG, "onSharedPreferenceChanged: SAVED ");

        }

        void DeleteFiles() {
// Currently Not Using

            File directory = new File(getContext().getFilesDir(), "");

            File[] files = directory.listFiles();
            Log.d("Files", "Size: " + files.length);
            for (int i = 0; i < files.length; i++) {
                File localFile = new File(getContext().getFilesDir(), files[i].getName());
                localFile.delete();
                Log.d("Files", "GetDirFiles: Delete: " + localFile);


  /*          String key="x";

            for (DataSnapshot postSnapshot : BookLibraryDB.getChildren()) {
                String BI="eBook"+postSnapshot.child("bookid").getValue(String.class)+ ".pdf";
                if(files[i].getName().equals(BI)){
                    Log.d(TAG, "GetDirFiles: Same File on Cloud:"+BI);
                }else {
                    key="Deleted Book";
                    File localFile = new File(this.getFilesDir(), files[i].getName());
                    localFile.delete();
                    Log.d("Files", "GetDirFiles: Delete: "+localFile);
                }
            }

            Log.d("Files", "FileName:" + files[i].getName()+"\n key:"+key+"\n----");
*/
            }

        }


    }

    private class MyWebViewClient extends WebViewClient {
        /*@Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if ("bitvedas-13cc3.web.app".equals(Uri.parse(url).getHost())) {
                // This is my website, so do not override; let my WebView load the page
                return false;
            }else if ("ephrine.in".equals(Uri.parse(url).getHost())) {
                // This is my website, so do not override; let my WebView load the page
                return false;
            }


            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }


      */
        public void onPageFinished(WebView view, String url) {
            // do your stuff here
            if (includeHomeView.getVisibility() != View.GONE) {
                smoothProgressBar.setVisibility(View.GONE);
            }
            Log.d(TAG, "onPageFinished: " + url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (includeHomeView.getVisibility() != View.GONE) {
                smoothProgressBar.setVisibility(View.VISIBLE);
            }
            Log.d(TAG, "onPageStarted: " + url);

        }
    }
}
