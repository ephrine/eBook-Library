/*
 * Copyright (c) 2019. Ephrine Apps
 * Code written by Devesh Chaudhari
 * Website: https://www.ephrine.in
 */

package devesh.ephrine.ebooks.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import devesh.ephrine.ebooks.R;
import devesh.ephrine.ebooks.UserProfileManager;
import devesh.ephrine.ebooks.mRecycleView.MyLibraryAdapter;

public class MyLibraryFragment extends Fragment {


    public String TAG = String.valueOf(R.string.app_name);
    //  public UserProfileManager mUser;
    RecyclerView recyclerView;
    RecyclerView myLibraryBooksrecyclerView;
    MyLibraryAdapter MyLibraryAdapter;

    FirebaseDatabase database;

    ArrayList<HashMap<String, String>> MyLibraryBookHashmap = new ArrayList();
    String UserUniqueID;
    ProgressBar HomeLoading;

    DataSnapshot BookLibraryDB;
    UserProfileManager mUser;
    String UserPhno;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        database = FirebaseDatabase.getInstance();
        mUser = new UserProfileManager(getContext());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in


            UserPhno = auth.getCurrentUser().getPhoneNumber();

            UserUniqueID = UserPhno.replace("+", "x");
            mUser = new UserProfileManager(getContext());


        } else {

            // not signed in

        }


        myLibraryBooksrecyclerView = container.findViewById(R.id.myBookLibraryRecycleView);
        // myLibraryBooksrecyclerView.removeAllViews();
        // myLibraryBooksrecyclerView.removeAllViewsInLayout();
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        myLibraryBooksrecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        //layoutManager = new LinearLayoutManager(this);
//            myLibraryBooksrecyclerView.setLayoutManager(layoutManager);
        myLibraryBooksrecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
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

                                    MyLibraryAdapter = new MyLibraryAdapter(getContext(), MyLibraryBookHashmap);
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


        return inflater.inflate(R.layout.my_book_library, container, false);
    }

}
