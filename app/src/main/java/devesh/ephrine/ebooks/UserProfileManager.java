/*
 * Copyright (c) 2019. Ephrine Apps
 * Code written by Devesh Chaudhari
 * Website: https://www.ephrine.in
 */

package devesh.ephrine.ebooks;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class UserProfileManager {


    public String TAG = "UserProfileManager.java\n" + String.valueOf(R.string.app_name);
    public String UserUID;
    public String UserPhno;
    public String UserName;
    public String UserBio;
    public String UserGender;
    public String UserEmail;
    public String UserAge;
    public String UserProfilePic;
    public String UserUniqueID;

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


            //        Gson gson;// = new Gson();
            // convert map to JSON String
   /*         GsonBuilder builder = new GsonBuilder();
            gson = builder.enableComplexMapKeySerialization().setPrettyPrinting().create();
            Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            String j = gson.toJson(User, type);
            Log.d(TAG, "------------------\n HASHMAP to JSON\n" + j);
*/
            // JSON to Hashmap
    /*        HashMap<String, String> User1 = new HashMap<String, String>();
            User1 = gson.fromJson(j, type);
            Log.d(TAG, "------------------\n JSON to hashmap \n" + User1);
*/

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
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(mContext /* Activity context */);
                    if (dataSnapshot.child("UserName").getValue(String.class) != null) {
                        UserName = dataSnapshot.child("UserName").getValue(String.class);
                        sharedPreferences.edit().putString("pref_username", UserName).apply();
                    } else {
                        UserName = null;
                    }

                    if (dataSnapshot.child("Gender").getValue(String.class) != null) {
                        UserGender = dataSnapshot.child("Gender").getValue(String.class);
                        sharedPreferences.edit().putString("pref_usergender", "").apply();

                    } else {
                        UserGender = null;
                    }

                    if (dataSnapshot.child("UserPhoneNo").getValue(String.class) != null) {
                        UserPhno = dataSnapshot.child("UserPhoneNo").getValue(String.class);
                        sharedPreferences.edit().putString("pref_userphno", UserEmail).apply();

                    }

                    if (dataSnapshot.child("ProfilePic").getValue(String.class) != null) {
                        UserProfilePic = dataSnapshot.child("ProfilePic").getValue(String.class);
                    }

                    if (dataSnapshot.child("UserAge").getValue(String.class) != null) {
                        UserAge = dataSnapshot.child("UserAge").getValue(String.class);
                        sharedPreferences.edit().putString("pref_userage", UserAge).apply();

                    } else {
                        UserAge = null;
                    }

                    if (dataSnapshot.child("UserEmail").getValue(String.class) != null) {
                        UserEmail = dataSnapshot.child("UserEmail").getValue(String.class);
                        sharedPreferences.edit().putString("pref_useremail", UserEmail).apply();

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

    public void UpdateProfileX() {
        HashMap<String, String> ph = new HashMap<String, String>();

        ph.put("UserUID", UserUID);
        ph.put("UserPhno", UserPhno);
        ph.put("UserUniqueID", UserUniqueID);

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(mContext /* Activity context */);
        String name = sharedPreferences.getString("UserName", "");
        ph.put("UserName", sharedPreferences.getString("pref_username", ""));
        ph.put("UserAge", sharedPreferences.getString("pref_userage", ""));
        ph.put("UserEmail", sharedPreferences.getString("pref_useremail", ""));
        ph.put("Gender", sharedPreferences.getString("pref_usergender", ""));
        GetUserProfile = database.getReference("ebooksapp/users/" + UserUniqueID + "/profile");
        GetUserProfile.setValue(ph);

    }

}
