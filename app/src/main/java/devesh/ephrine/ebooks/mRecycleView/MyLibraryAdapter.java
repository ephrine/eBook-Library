/*
 * Copyright (c) 2019. Ephrine Apps
 * Code written by Devesh Chaudhari
 * Website: https://www.ephrine.in
 */

package devesh.ephrine.ebooks.mRecycleView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import devesh.ephrine.ebooks.BookDownloadWorker;
import devesh.ephrine.ebooks.BookReadActivity;
import devesh.ephrine.ebooks.R;
import devesh.ephrine.ebooks.UserProfileManager;
import io.fabric.sdk.android.Fabric;

public class MyLibraryAdapter extends RecyclerView.Adapter<MyLibraryAdapter.MyViewHolder> {
    public static final String EXTRA_MESSAGE = "ReadBookID";
    public String TAG = String.valueOf(R.string.app_name);
    public Context mContext;

    public UserProfileManager mUser;

    ArrayList<HashMap<String, String>> StoreBooksList;
    private ArrayList<HashMap<String, String>> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyLibraryAdapter(Context mContext, ArrayList<HashMap<String, String>> myDataset) {
        Fabric.with(mContext, new Crashlytics());

        mDataset = myDataset;
        this.mContext = mContext;
        mUser = new UserProfileManager(mContext);
        // mUser.Download();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyLibraryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        // TextView v = (TextView) LayoutInflater.from(parent.getContext())
        //       .inflate(R.layout.recycleview_books_list, parent, false);


        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycleview_mybooklibrary_book, parent, false);


        // Give the view as it is
        MyViewHolder vh = new MyViewHolder(v);


        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.textView.setText(mDataset.get(position).get("cs"));
        Log.d(TAG, "------------\n onBindViewHolder: " + mDataset.get(position).toString());

        final String ReadBookName = mDataset.get(position).get("bookname");
        final String ReadBookAuthor = mDataset.get(position).get("bookauthor");
        final String ReadBookID = mDataset.get(position).get("bookid");
        final String ReadBookURL = mDataset.get(position).get("bookurl");

        holder.LLBook.setTag(mDataset.get(position).get("bookid"));
        //  holder.AddToLibraryChip.setTag(mDataset.get(position).get("bookid"));

        //  holder.BookTitleTx.setText(ReadBookName);
        //  holder.BookAuthorTx.setText("By " + ReadBookAuthor);

        Glide.with(holder.BookCoverPic)
                .load(mDataset.get(position).get("bookcover"))
                .into(holder.BookCoverPic);

        File localFile = new File(mContext.getFilesDir(), "eBook" + ReadBookID + ".pdf");
        final Toast toastMSG = new Toast(mContext);


        final MyViewHolder H = holder;
        holder.LLBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //view.getTag();
                Log.d(TAG, "onClick: " + view.getTag());
                File localFile = new File(mContext.getFilesDir(), "eBook" + ReadBookID + ".pdf");


                if (localFile.exists()) {
                    //  pdfView.fromFile(localFile).load();
                    Log.d(TAG, "Already Loaded PDF in Local Cache");

                    String tag = view.getTag().toString();
                    Intent intent = new Intent(mContext, BookReadActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, tag);
                    //  String message = editText.getText().toString();
                    //intent.putExtra(EXTRA_MESSAGE, message);
                    mContext.startActivity(intent);
                } else {
                    if (WorkManager.getInstance().getWorkInfosByTag(ReadBookID).isDone()) {
                        Log.d(TAG, "Already Loaded PDF in Local Cache");
                        H.CloudDownloadIMG.setVisibility(View.GONE);
                        H.CheckIMG.setVisibility(View.VISIBLE);
                        H.DownloadingSyncIMG.setVisibility(View.GONE);

                        String tag = view.getTag().toString();
                        Intent intent = new Intent(mContext, BookReadActivity.class);
                        intent.putExtra(EXTRA_MESSAGE, tag);
                        //  String message = editText.getText().toString();
                        //intent.putExtra(EXTRA_MESSAGE, message);
                        mContext.startActivity(intent);
                    } else {
                        toastMSG.makeText(mContext, "Downloading Book", Toast.LENGTH_SHORT).show();
                        // Download book
                        Log.d(TAG, "PDF not in Local Cache");
                        Data FileData = new Data.Builder()
                                .putString("bookid", ReadBookID)
                                .putString("bookurl", ReadBookURL)
                                .putString("bookname", ReadBookName)
                                .build();

                        OneTimeWorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(BookDownloadWorker.class)
                                .setInputData(FileData)
                                .addTag(ReadBookID)
                                .build();

                        WorkManager.getInstance().enqueue(uploadWorkRequest);

                    }


                }

            }
        });

/*
       holder.AddToLibraryChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //view.getTag();
                Log.d(TAG, "onClick: Added to Library " + view.getTag());
                mUser.AddBookLibrary(view.getTag().toString());
                /*String tag = view.getTag().toString();
                Intent intent = new Intent(mContext, BookReadActivity.class);

                intent.putExtra(EXTRA_MESSAGE, tag);

                //  String message = editText.getText().toString();
                //intent.putExtra(EXTRA_MESSAGE, message);
                mContext.startActivity(intent); */
       /*     }
        }); */


        if (localFile.exists()) {
            //  pdfView.fromFile(localFile).load();
            Log.d(TAG, "Already Loaded PDF in Local Cache");
            holder.CloudDownloadIMG.setVisibility(View.GONE);
            holder.CheckIMG.setVisibility(View.VISIBLE);
            holder.DownloadingSyncIMG.setVisibility(View.GONE);

        } else {
            holder.CloudDownloadIMG.setVisibility(View.VISIBLE);
            holder.CheckIMG.setVisibility(View.GONE);
            holder.DownloadingSyncIMG.setVisibility(View.GONE);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public TextView BookTitleTx;
        public TextView BookAuthorTx;
        public LinearLayout LLBook;
        public ImageView BookCoverPic;
        public ImageView CloudDownloadIMG;
        public ImageView CheckIMG;
        public ImageView DownloadingSyncIMG;


        public MyViewHolder(View v) {
            super(v);
            //     textView = v.findViewById(R.id.textView3);
            BookTitleTx = v.findViewById(R.id.BookTitleMyLibrarytextView);
            BookAuthorTx = v.findViewById(R.id.BookAuthorMyLibrarytextView3);
            LLBook = v.findViewById(R.id.LLMyLibraryBook12);
            BookCoverPic = v.findViewById(R.id.BookCoverMyLibraryImageView);

            CloudDownloadIMG = v.findViewById(R.id.CloudDownloadIMGimageView2);
            CheckIMG = v.findViewById(R.id.CheckIMGimageView3);
            DownloadingSyncIMG = v.findViewById(R.id.DownloadingSyncBookimageView4);

        }
    }


}
