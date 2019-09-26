/*
 * Copyright (c) 2019. Ephrine Apps
 * Code written by Devesh Chaudhari
 * Website: https://www.ephrine.in
 */

package devesh.ephrine.ebooks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;


import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
     String TAG = String.valueOf(R.string.app_name);

    FirebaseDatabase database;
    ArrayList<HashMap<String, String>> MyLibraryBookHashmap = new ArrayList();
    String UserUniqueID;
    String UserPhno;
    RecyclerView myLibraryBooksrecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in


            UserPhno = auth.getCurrentUser().getPhoneNumber();

            UserUniqueID = UserPhno.replace("+", "x");
AppStart();

        } else {

            // not signed in

        }
        BottomAppBar bar = (BottomAppBar) findViewById(R.id.bar);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the navigation click by showing a BottomDrawer etc.
                Log.d(TAG, "onClick: click");
            }
        });





    }

    void AppStart(){
        // specify an adapter (see also next example)
        DatabaseReference MyLibraryBooksDB = database.getReference("ebooksapp/users/" + UserUniqueID + "/mylibrary");
        MyLibraryBooksDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //   String value = dataSnapshot.getValue(String.class);
                if (dataSnapshot != null) {
                    MyLibraryBookHashmap.clear();

                    long TSize=dataSnapshot.getChildrenCount();
                    long i=0;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String BookID = "null";
                        i=i+1;
                        if (postSnapshot.child("bookid").getValue(String.class) != null) {
                            BookID = postSnapshot.child("bookid").getValue(String.class);
                            //MyLibraryBookList.add(BookID);

                            DataSnapshot bid=postSnapshot.child("bookid");
                            for (DataSnapshot ds:bid.getChildren()){
                                HashMap<String, String> bk = new HashMap<String, String>();

                                String BookName = ds.child("bookname").getValue(String.class);
                                String BookAuthor = ds.child("bookauthor").getValue(String.class);
                                String BookYear = ds.child("bookyear").getValue(String.class);
                                String BookCategory = ds.child("bookcategory").getValue(String.class);
                                String BookCover = ds.child("bookcover").getValue(String.class);
                                String BookURL = ds.child("bookurl").getValue(String.class);
                                //String BookID = String.valueOf(ds.child("bookid").getValue(Integer.class));

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

                            Log.d(TAG, "onDataChange: "+i+"\n"+TSize);
                            if(i==TSize){
                                Log.d(TAG, "onDataChange: at last: "+i+"\n"+TSize);
                                LoadMyLibraryRecycleView();
                            }

                        }
                        Log.i(TAG, "onDataChange: ------------------\n Get User Library\n" + BookID + "\n");




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
    void LoadMyLibraryRecycleView(){

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


        MyLibraryAdapter d= new MyLibraryAdapter(this,MyLibraryBookHashmap );
        myLibraryBooksrecyclerView.setAdapter(d);

    }


}
