/*
 * Copyright (c) 2019. Ephrine Apps
 * Code written by Devesh Chaudhari
 * Website: https://www.ephrine.in
 */

package devesh.ephrine.ebooks;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import devesh.ephrine.ebooks.mRecycleView.MyAdapter;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class BookReadActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "ReadBookID";
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;
    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    public String TAG = String.valueOf(R.string.app_name);
    public String ReadBookID;
    public HashMap<String, String> StoreBook = new HashMap<String, String>();
    public File localFile;
    public PDFView pdfView;
    public TextView PageNo;
    public ImageView BookMarkImageView;
    public int NightMode;
    public FirebaseDatabase database;
    public UserProfileManager mUser;
    public ArrayList<String> BookmarksList = new ArrayList<>();
    public View BookmarkView;
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            int CurrentPg = pdfView.getCurrentPage();
            int Totalpg = pdfView.getPageCount();
            PageNo.setText("Page no: " + CurrentPg + "/" + Totalpg);

            if (BookmarksList != null) {
                if (BookmarksList.contains(String.valueOf(CurrentPg))) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        BookMarkImageView.setImageDrawable(getResources().getDrawable(R.drawable.baseline_bookmark_black_48dp, getApplicationContext().getTheme()));
                    } else {
                        BookMarkImageView.setImageDrawable(getResources().getDrawable(R.drawable.baseline_bookmark_black_48dp));
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        BookMarkImageView.setImageDrawable(getResources().getDrawable(R.drawable.baseline_bookmark_border_black_48dp, getApplicationContext().getTheme()));
                    } else {
                        BookMarkImageView.setImageDrawable(getResources().getDrawable(R.drawable.baseline_bookmark_border_black_48dp));
                    }

                }

            }

            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }


            return false;
        }
    };
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_read);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        database = FirebaseDatabase.getInstance();
        mUser = new UserProfileManager(this);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        ReadBookID = intent.getStringExtra(MyAdapter.EXTRA_MESSAGE);
        BookMarkImageView = (ImageView) findViewById(R.id.BookmarkIMG);
        BookmarkView = (View) findViewById(R.id.includeBookmark);
        BookmarkView.setVisibility(View.GONE);
        NightMode = 0;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        PageNo = (TextView) findViewById(R.id.PageNotextView461);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        DownloadBooks();

        //Sync Book Details
        DatabaseReference BookSync = database.getReference("ebooksapp/users/" + mUser.UserUniqueID + "/mylibrary/" + ReadBookID);
        BookSync.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //  This method is called once with the initial value and again
                //  whenever data at this location is updated.
                //  String value = dataSnapshot.getValue(String.class);
                //  Log.d(TAG, "Value is: " + value);
                //  String BookName = bookmarksnap.child("1").getValue(String.class);

                if (dataSnapshot.child("bookmarks") != null && dataSnapshot != null) {
                    DataSnapshot dp = dataSnapshot.child("bookmarks");
                    BookmarksList.clear();
                    for (DataSnapshot ps : dp.getChildren()) {
                        String key = ps.getKey().toString();
                        Log.d(TAG, "-------------------onDataChange:\n Book Sync:\n" + key + "\n" + ps.child("1").getValue(Integer.class));
                        if (BookmarksList.contains(key)) {
                            Log.e(TAG, "onDataChange: Bookmark already exist = " + key);
                        } else {
                            BookmarksList.add(key);
                        }

                    }
                    Log.d(TAG, "-------------------onDataChange:\n Book Sync:\n" + dp.toString());

                } else {
                    Log.e(TAG, "Bookmarks Empty !!");
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        BookSync.keepSynced(true);

        LoadAds();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void DownloadBooks() {

        // Write a message to the database
        DatabaseReference DownloadBook = database.getReference("ebooksapp/library/books/" + ReadBookID);
        DownloadBook.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //  This method is called once with the initial value and again
                //  whenever data at this location is updated.
                //  String value = dataSnapshot.getValue(String.class);
                //  Log.d(TAG, "Value is: " + value);

                String BookName = dataSnapshot.child("bookname").getValue(String.class);
                String BookAuthor = dataSnapshot.child("bookauthor").getValue(String.class);
                String BookYear = dataSnapshot.child("bookyear").getValue(String.class);
                String BookCategory = dataSnapshot.child("bookcategory").getValue(String.class);
                String BookCover = dataSnapshot.child("bookcover").getValue(String.class);
                String BookURL = dataSnapshot.child("bookurl").getValue(String.class);
                String BookID = String.valueOf(dataSnapshot.child("bookid").getValue(Integer.class));

                StoreBook.put("bookname", BookName);
                StoreBook.put("bookauthor", BookAuthor);
                StoreBook.put("bookyear", BookYear);
                StoreBook.put("bookcategory", BookCategory);
                StoreBook.put("bookcover", BookCover);
                StoreBook.put("bookurl", BookURL);
                StoreBook.put("bookid", BookID);


                Log.d(TAG, "-------------------onDataChange:\n Book Details:\n" + StoreBook);
                LoadPDF();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }

    public void LoadPDF() {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference mStorageRef = storage.getInstance().getReference();

        //  Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
        //StorageReference riversRef = mStorageRef.child("doc/file.pdf");
        StorageReference riversRef = storage.getReferenceFromUrl(StoreBook.get("bookurl"));
        //StorageReference httpsReference = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/b/bucket/o/images%20stars.jpg");
        pdfView = (PDFView) findViewById(R.id.fullscreen_content);


        try {
            Log.d(TAG, "LoadPDF: Trying to Load PDF");

            localFile = new File(this.getFilesDir(), "eBook" + ReadBookID + ".pdf");

            if (localFile.exists()) {
                pdfView.fromFile(localFile).load();
                Log.d(TAG, "LoadPDF: Loading Local Cache");

            } else {
                Log.d(TAG, "LoadPDF: Downloading");
                // localFile = File.createTempFile("eBook"+ReadBookID, "pdf");
                riversRef.getFile(localFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                // Successfully downloaded data to local file
                                // ...

                                Log.d(TAG, "onSuccess: Downloaded File");
                                pdfView.fromFile(localFile).load();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle failed download
                        // ...
                    }
                });

            }


        } catch (Exception e) {
            Log.d(TAG, "LoadPDF: " + e);
        }

    }

    public void SetBookMark(View v) {
        int CurrentPg = pdfView.getCurrentPage();
        // BookmarksList.add(String.valueOf(CurrentPg));
        DatabaseReference addBM = database.getReference("ebooksapp/users/" + mUser.UserUniqueID + "/mylibrary/" + ReadBookID + "/bookmarks/" + String.valueOf(CurrentPg));

        if (BookmarksList.contains(String.valueOf(CurrentPg))) {
            addBM.removeValue();
            Log.d(TAG, "SetBookMark: Already Exist: Delete Bookmark " + CurrentPg);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BookMarkImageView.setImageDrawable(getResources().getDrawable(R.drawable.baseline_bookmark_border_black_48dp, getApplicationContext().getTheme()));
            } else {
                BookMarkImageView.setImageDrawable(getResources().getDrawable(R.drawable.baseline_bookmark_border_black_48dp));
            }


        } else {
            addBM.setValue(String.valueOf(CurrentPg));
            Log.d(TAG, "--------------------\nSetBookMark:\n " + "Curent page:" + CurrentPg + "\n Bookmark list: " + BookmarksList);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BookMarkImageView.setImageDrawable(getResources().getDrawable(R.drawable.baseline_bookmark_black_48dp, getApplicationContext().getTheme()));
            } else {
                BookMarkImageView.setImageDrawable(getResources().getDrawable(R.drawable.baseline_bookmark_black_48dp));
            }

        }


    }

    public void BookMarkButton(View v) {
        //    View BookmarkView=(View)findViewById(R.id.includeBookmark);
        String TAG = v.getTag().toString();
        if (TAG.equals("OPEN")) {
            BookmarkView.setVisibility(View.VISIBLE);
            LoadBookmarkUIList();
        } else {
            BookmarkView.setVisibility(View.GONE);
        }
    }

    public void LoadBookmarkUIList() {
        ListView LLBookmarks = (ListView) findViewById(R.id.BookmarksListView);
        // Create a ArrayAdapter from List
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, BookmarksList);

        // Populate ListView with items from ArrayAdapter
        LLBookmarks.setAdapter(arrayAdapter);

        // Set an item click listener for ListView
        LLBookmarks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                String selectedItem = (String) parent.getItemAtPosition(position);

                int j = Integer.parseInt(BookmarksList.get(position));
                pdfView.jumpTo(j);
                Log.d(TAG, "onItemClick: Bookmark selected: " + position + "\n " + j);
                //   View BookmarkView=(View)findViewById(R.id.includeBookmark);
                BookmarkView.setVisibility(View.GONE);

            }
        });

    }

    public void ViewBookInfo(View v) {
/*
        Intent intent = new Intent(this, BookViewActivity.class);

        intent.putExtra(EXTRA_MESSAGE, ReadBookID);

        startActivity(intent);
        BookReadActivity.this.finish(); */
    }

    public void DarkModeToggle(View v) {
//    if(pdfView.)pdfView.setNightMode(true);
        if (NightMode == 1) {
            pdfView.setNightMode(false);
            NightMode = 0;
            Toast.makeText(this, "Dark Mode Disable", Toast.LENGTH_SHORT).show();
        } else if (NightMode == 0) {
            pdfView.setNightMode(true);
            NightMode = 1;
            Toast.makeText(this, "Dark Mode Enable", Toast.LENGTH_SHORT).show();
        }


    }

    public void nulll(View v) {


    }

    public void Demo() {
/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myImgView.setImageDrawable(getResources().getDrawable(R.drawable.monkey, getApplicationContext().getTheme()));
        } else {
            myImgView.setImageDrawable(getResources().getDrawable(R.drawable.monkey));
        }
        */

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            BookReadActivity.this.finish();
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }

    void LoadAds() {

        MobileAds.initialize(this, getString(R.string.AdMob_App_Id));

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.AdMob_Int_Id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
            }
        });


    }
}

