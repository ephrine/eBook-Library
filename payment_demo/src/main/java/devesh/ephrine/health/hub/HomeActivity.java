/*
 * Copyright (c) 2019. Ephrine Apps
 * Code written by Devesh Chaudhari
 * Website: https://www.ephrine.in
 */

package devesh.ephrine.health.hub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import devesh.ephrine.health.hub.mRecycleView.MyAdapter;
import devesh.ephrine.health.hub.mRecycleView.MyLibraryAdapter;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public String TAG = String.valueOf(R.string.app_name);
    public UserProfileManager mUser;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUser = new UserProfileManager(this);
        //  mUser.Download();

        includeHomeView = (View) findViewById(R.id.includeHome);
        includeAccountView = (View) findViewById(R.id.includeMyAccount);
        includeMyLibraryBooks = (View) findViewById(R.id.includeMyLibrary);
        includeAbout = (View) findViewById(R.id.includeAbout);

        includeAccountView.setVisibility(View.GONE);
        includeMyLibraryBooks.setVisibility(View.GONE);
        includeAbout.setVisibility(View.GONE);

        if (includeAccountView.getVisibility() != View.GONE) {

        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        loadbooks();




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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
            if(includeHomeView.getVisibility()!=View.GONE){
                recyclerView.removeAllViews();
                loadbooks();
            }
            if(includeAccountView.getVisibility()!=View.GONE){
                LoadMyAccount();
            }
            if(includeMyLibraryBooks.getVisibility()!=View.GONE){
                myLibraryBooksrecyclerView.removeAllViews();
                LoadMyLibrary();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.MyLibraryMenu) {
            // Handle the camera action
            includeHomeView.setVisibility(View.GONE);
            includeAccountView.setVisibility(View.GONE);
            includeMyLibraryBooks.setVisibility(View.VISIBLE);
            includeAbout.setVisibility(View.GONE);

            LoadMyLibrary();

        } else if (id == R.id.myAccountMenu) {

            includeHomeView.setVisibility(View.GONE);
            includeAccountView.setVisibility(View.VISIBLE);
            includeMyLibraryBooks.setVisibility(View.GONE);
            includeAbout.setVisibility(View.GONE);

            LoadMyAccount();

        } else if (id == R.id.BrowseStoreMenu) {
            includeHomeView.setVisibility(View.VISIBLE);
            includeAccountView.setVisibility(View.GONE);
            includeMyLibraryBooks.setVisibility(View.GONE);
            includeAbout.setVisibility(View.GONE);

            loadbooks();

        } else if (id == R.id.paymentMenu) {
            Intent intent = new Intent(this, PaymentActivity.class);

            //  String message = editText.getText().toString();
            //intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);

        } else if (id == R.id.includeMyAccount) {
            includeHomeView.setVisibility(View.GONE);
            includeAccountView.setVisibility(View.VISIBLE);
            includeMyLibraryBooks.setVisibility(View.GONE);
            includeAbout.setVisibility(View.GONE);

        }
        else if (id == R.id.AboutMenu) {
            includeHomeView.setVisibility(View.GONE);
            includeAccountView.setVisibility(View.GONE);
            includeMyLibraryBooks.setVisibility(View.GONE);
            includeAbout.setVisibility(View.VISIBLE);

        }

        /* else if (id == R.id.nav_send) {
        }*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

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

    private void loadbooks() {

        if (includeHomeView.getVisibility() != View.GONE) {

            recyclerView = (RecyclerView) findViewById(R.id.mRecycleView);
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
            });

        }

    }


    public void LoadMyLibrary() {


        if (includeMyLibraryBooks.getVisibility() != View.GONE) {

            myLibraryBooksrecyclerView = (RecyclerView) findViewById(R.id.myBookLibraryRecycleView);
            myLibraryBooksrecyclerView.removeAllViews();
            myLibraryBooksrecyclerView.removeAllViewsInLayout();
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            myLibraryBooksrecyclerView.setHasFixedSize(true);
            // use a linear layout manager
            //layoutManager = new LinearLayoutManager(this);
//            myLibraryBooksrecyclerView.setLayoutManager(layoutManager);
            myLibraryBooksrecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            // specify an adapter (see also next example)
            MyLibraryAdapter = new MyLibraryAdapter(HomeActivity.this, mUser.GetMyBooksLibrary());
            myLibraryBooksrecyclerView.setAdapter(MyLibraryAdapter);


        }

    }




    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onStart() {


        super.onStart();
    }


}
