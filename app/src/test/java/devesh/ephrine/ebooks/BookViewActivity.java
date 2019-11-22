/*
 * Copyright (c) 2019. Ephrine Apps
 * Code written by Devesh Chaudhari
 * Website: https://www.ephrine.in
 */

package devesh.ephrine.ebooks;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import devesh.ephrine.ebooks.mRecycleView.MyAdapter;


public class BookViewActivity extends AppCompatActivity {
    public String TAG = String.valueOf(R.string.app_name);

    public String ViewBookID;
    public UserProfileManager mUser;

    String BookName;
    String BookAuthor;
    String BookYear;
    String BookCategory;
    String BookCover;
    String BookURL;
    String BookID;
    String BookDesc;
    String BookLanguage;
    //    Button Add2LibraryBT;
    Chip Add2LibraryChip;
    boolean isAddedtoLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_view);
        isAddedtoLibrary = false;
        mUser = new UserProfileManager(this);

        Intent intent = getIntent();
        ViewBookID = intent.getStringExtra(MyAdapter.EXTRA_MESSAGE);

        final ImageView BookCoverIMG = (ImageView) findViewById(R.id.BookCoverPicimageView2);
        final TextView BookTitleTXT = (TextView) findViewById(R.id.BookTitletextView45);
        final TextView BookAuthorTXT = (TextView) findViewById(R.id.BookAuthortextView58);
        final TextView BookDescTXT = (TextView) findViewById(R.id.BookDesctextView647);
        final Chip BookLanguageChip = (Chip) findViewById(R.id.BookLanguageChip22);
        Add2LibraryChip = (Chip) findViewById(R.id.Add2Libchip2);
        //    Add2LibraryBT = (Button) findViewById(R.id.add2libbutton2);
        //  BookLanguageChip22
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference DownloadBookInfo = database.getReference("ebooksapp/library/books/" + ViewBookID);

        DownloadBookInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Dowmloaded Book Info");

                if (dataSnapshot != null) {

                    BookName = dataSnapshot.child("bookname").getValue(String.class);
                    BookAuthor = dataSnapshot.child("bookauthor").getValue(String.class);
                    BookYear = dataSnapshot.child("bookyear").getValue(String.class);
                    BookCategory = dataSnapshot.child("bookcategory").getValue(String.class);
                    BookCover = dataSnapshot.child("bookcover").getValue(String.class);
                    BookURL = dataSnapshot.child("bookurl").getValue(String.class);
                    BookID = String.valueOf(dataSnapshot.child("bookid").getValue(Integer.class));
                    BookDesc = dataSnapshot.child("bookdesc").getValue(String.class);
                    BookLanguage = dataSnapshot.child("booklang").getValue(String.class);


                    BookTitleTXT.setText(BookName);
                    BookAuthorTXT.setText(BookAuthor);
                    BookDescTXT.setText(BookDesc);

                    Glide.with(BookViewActivity.this)
                            .load(BookCover)
                            .into(BookCoverIMG);


                    BookLanguageChip.setText(BookLanguage);

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        final DatabaseReference isBookinLib;
        isBookinLib = FirebaseDatabase.getInstance().getReference("ebooksapp/users/" + mUser.UserUniqueID + "/mylibrary/" + ViewBookID);
        isBookinLib.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //   String value = dataSnapshot.getValue(String.class);
                //   Log.d(TAG, "Value is: " + value);

                if (dataSnapshot != null && dataSnapshot.child("bookid").getValue(String.class) != null) {

                    String mBookid = dataSnapshot.child("bookid").getValue(String.class);
                    if (mBookid.equals(BookID)) {
                        //Add2LibraryBT.setText("Added to Library");
                        Add2LibraryChip.setText("Added to Library");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Add2LibraryChip.setChipIcon(getDrawable(R.drawable.baseline_favorite_black_48dp));
                        }
                        isAddedtoLibrary = true;
                    } else {
                        isAddedtoLibrary = false;
                    }

                } else {
                    isAddedtoLibrary = false;
                }


                Log.d(TAG, "-------------------onDataChange:");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }

    public void Add2Library(View v) {
        if (isAddedtoLibrary) {
            mUser.DeleteBookLibrary(BookID);
            Add2LibraryChip.setText("Add to Library");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Add2LibraryChip.setChipIcon(getDrawable(R.drawable.baseline_favorite_border_black_48dp));
            }

        } else {
            Toast.makeText(this, "Adding to Library", Toast.LENGTH_SHORT).show();
            mUser.AddBookLibrary(BookID);
            Add2LibraryChip.setText("Added to Library");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Add2LibraryChip.setChipIcon(getDrawable(R.drawable.baseline_favorite_black_48dp));
            }

        }
    }
}
