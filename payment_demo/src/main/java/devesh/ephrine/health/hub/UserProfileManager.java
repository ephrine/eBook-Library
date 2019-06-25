/*
 * Copyright (c) 2019. Ephrine Apps
 * Code written by Devesh Chaudhari
 * Website: https://www.ephrine.in
 */

package devesh.ephrine.health.hub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseInfo;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class UserProfileManager implements BillingProcessor.IBillingHandler{


    public String TAG = String.valueOf(R.string.app_name);
    public String UserUID;
    public String UserPhno;
    public String UserName;
    public String UserBio;
    public String UserGender;
    public String UserEmail;
    public String UserAge;
    public String UserProfilePic;
    public String UserUniqueID;
    public boolean isSubscribed;

    public String AccStatus;

    public Context mContext;
    SharedPreferences SP;

    HashMap<String, String> aBooks;
    ArrayList<String> MyLibraryBookList;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference GetUserProfile;
    ArrayList<HashMap<String, String>> MyLibraryBookHashmap = new ArrayList();

    public UserProfileManager(Context mContext) {

        this.mContext = mContext;
        Download();

        bp = new BillingProcessor(mContext, LicenceKey, this);
        bp.initialize();


    }

    public void Download() {

      FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in
            HashMap<String, String> User = new HashMap<String, String>();

            UserUID = auth.getUid();
            User.put("UserUID", UserUID);

            UserPhno = auth.getCurrentUser().getPhoneNumber();
            User.put("UserPhno", UserPhno);

            UserName = auth.getCurrentUser().getDisplayName();
            User.put("UserName", "Devesh Chaudhari");

            UserUniqueID = UserPhno.replace("+", "x");
            User.put("UserUniqueID", UserUniqueID);

            Log.i(TAG, "-----------\n onCreate: \n UserUID:" + UserUID + "\n User Phone no:" + UserPhno + "\n Unique ID: " + UserUniqueID + "\n name:" + UserName + "\n -------------");

            SP = PreferenceManager.getDefaultSharedPreferences(mContext);

            SP.edit().putString("pref_phoneno", UserPhno).apply();
            SP.edit().putString("pref_UserUID", UserUniqueID).apply();


            String ss = SP.getString("pref_phoneno", "na");
            Log.i(TAG, "onCreate: ---------------\n " + ss);
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference UserDB = database.getInstance().getReference("ebooksapp/users/" + UserUniqueID + "/profile");
            //   UserDB.setValue(User);

            ReadUserData();


            Gson gson;// = new Gson();
            // convert map to JSON String
            GsonBuilder builder = new GsonBuilder();
            gson = builder.enableComplexMapKeySerialization().setPrettyPrinting().create();
            Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            String j = gson.toJson(User, type);
            Log.d(TAG, "------------------\n HASHMAP to JSON\n" + j);

            // JSON to Hashmap
            HashMap<String, String> User1 = new HashMap<String, String>();
            User1 = gson.fromJson(j, type);
            Log.d(TAG, "------------------\n JSON to hashmap \n" + User1);


        } else {

            // not signed in

        }
    }

    public void ReadUserData() {
        SP = PreferenceManager.getDefaultSharedPreferences(mContext);
//        final SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

//        database.setPersistenceEnabled(true);
        GetUserProfile = database.getReference("ebooksapp/users/" + UserUniqueID + "/profile");
        GetUserProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //   String value = dataSnapshot.getValue(String.class);
                if (dataSnapshot != null) {
                    if (dataSnapshot.child("UserName").getValue(String.class) != null) {
                        UserName = dataSnapshot.child("UserName").getValue(String.class);
                    } else {
                        UserName = null;
                    }

                    if (dataSnapshot.child("Gender").getValue(String.class) != null) {
                        UserGender = dataSnapshot.child("Gender").getValue(String.class);
                        //   SP.edit().putString("pref_user_gender", UserGender).apply();
                    } else {
                        UserGender = null;
                    }

                    if (dataSnapshot.child("UserPhoneNo").getValue(String.class) != null) {
                        UserPhno = dataSnapshot.child("UserPhoneNo").getValue(String.class);
                    }

                    if (dataSnapshot.child("ProfilePic").getValue(String.class) != null) {
                        UserProfilePic = dataSnapshot.child("ProfilePic").getValue(String.class);
                    }

                    if (dataSnapshot.child("UserAge").getValue(String.class) != null) {
                        UserAge = dataSnapshot.child("UserAge").getValue(String.class);
                    } else {
                        UserAge = null;
                    }

                    if (dataSnapshot.child("UserEmail").getValue(String.class) != null) {
                        UserEmail = dataSnapshot.child("UserEmail").getValue(String.class);
                    } else {
                        UserEmail = null;
                    }

                    Log.i(TAG, "onDataChange: ------------------\n Get USer Profile\n" + UserName + "\n" + UserGender + "\n" + UserBio + "\n");

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        GetUserProfile.keepSynced(true);


        // Read from the database
        DatabaseReference MyLibraryBooksDB = database.getReference("ebooksapp/users/" + UserUniqueID + "/mylibrary");
        MyLibraryBooksDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //   String value = dataSnapshot.getValue(String.class);
                if (dataSnapshot != null) {
                    MyLibraryBookHashmap.clear();
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
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
                                    Log.w(TAG, "Failed to read value.", error.toException());
                                }
                            });

                        }
                        Log.i(TAG, "onDataChange: ------------------\n Get User Library\n" + BookID + "\n");
                        Log.i(TAG, "onDataChange: ------------------\n My Book Library\n" + MyLibraryBookList);

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


    }

    public void AddBookLibrary(String BookId) {
        Download();
        Toast.makeText(mContext, "Adding Book to Your Library", Toast.LENGTH_SHORT).show();

        aBooks = new HashMap<String, String>();
        aBooks.put("bookid", BookId);
        aBooks.put("bookAddedDate", "Date Added");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference Add2MyAcc = database.getReference("ebooksapp/users/" + UserUniqueID + "/mylibrary/" + BookId);
        Add2MyAcc.setValue(aBooks);
        ReadUserData();

    }

    public void DeleteBookLibrary(String BookId) {
        Download();
        Toast.makeText(mContext, "Book Removed from Library", Toast.LENGTH_SHORT).show();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference Add2MyAcc = database.getReference("ebooksapp/users/" + UserUniqueID + "/mylibrary/" + BookId);
        Add2MyAcc.removeValue();
        ReadUserData();

       File localFile = new File(mContext.getFilesDir(), "eBook" + BookId + ".pdf");
        if (localFile.exists()) {
            localFile.delete();
            Log.d(TAG, "DeleteBookLibrary: Deleted Local File");
        }
         
    }

    public ArrayList<HashMap<String, String>> GetMyBooksLibrary() {
        return MyLibraryBookHashmap;
    }

    public void UpdateProfile(HashMap<String, String> ph) {
        ph.put("UserUID", UserUID);
        ph.put("UserPhno", UserPhno);
        ph.put("UserUniqueID", UserUniqueID);

        GetUserProfile = database.getReference("ebooksapp/users/" + UserUniqueID + "/profile");
        GetUserProfile.setValue(ph);

    }


    // Get Billing Info
    BillingProcessor bp;
    String ProductId="demo_sub";
    String LicenceKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgVhaeCsYFGJZAXP6qW7SLiCEn2PxP0R35naAq7LiNouHngz+wZ5xicmNA+9yqMh5FlcWyWc87lDQOLwMbFLv03nlh/b/bcTuz16I/ispF71u9o+b4tWuBLo21LNZBYMD0eXcbNBT/fN7KVkYm/ac/p02L3J1yQET/GWjSbqronnps0HnrA6C+DR4vQStM20/Oi7mIUCq0ktmxKUCQ9kwceWijIFVDGV6UwfAXT0edtGhokzrTA9eD9TpQWSCYvUjDNwKNBPEMeiAR6DuUoRzkw+Y2FKidFqJ4iFX4u8901SQKbeS9pcJ+b2FXOHlPvwBqtMy12+3YolKJdrIFVHHAQIDAQAB";


    @Override
    public void onBillingInitialized() {
        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
        bp.loadOwnedPurchasesFromGoogle();

        bp.getSubscriptionTransactionDetails(ProductId);
        TransactionDetails f=bp.getSubscriptionTransactionDetails(ProductId);
        if(f!=null){
            PurchaseInfo fd=f.purchaseInfo;
            String d=f.purchaseInfo.purchaseData.purchaseTime.toString();  // Purchase Date
            String x=f.purchaseInfo.responseData;
            Log.d(TAG, "--------------------- Trans details: \n" +d+"\n\n\n"+x);
            Log.d(TAG, "onBillingInitialized: Trans details:" +f.toString());
        }else {
            Log.d(TAG, "onBillingInitialized: Trans details: NULL");
        }


        checkIfUserIsSusbcribed();




    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        /*
         * Called when requested PRODUCT ID was successfully purchased
         */

        Log.d(TAG, "onProductPurchased: "+ProductId+"\n\n"+details.toString());

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        /*
         * Called when some error occurred. See Constants class for more details
         *
         * Note - this includes handling the case where the user canceled the buy dialog:
         * errorCode = Constants.BILLING_RESPONSE_RESULT_USER_CANCELED
         */
        Log.d(TAG, "onBillingError: "+errorCode);

    }

    @Override
    public void onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */


    }




    void checkIfUserIsSusbcribed(){
        boolean purchaseResult = bp.loadOwnedPurchasesFromGoogle();
        if(purchaseResult){
            TransactionDetails subscriptionTransactionDetails = bp.getSubscriptionTransactionDetails(ProductId);
            if(subscriptionTransactionDetails!=null) {
                //User is still subscribed
                Log.d(TAG, "checkIfUserIsSusbcribed: User is still subscribed");
            } else {
                //Not subscribed
                Log.d(TAG, "checkIfUserIsSusbcribed: Not subscribed");
            }
        }
    }




}
