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

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import devesh.ephrine.ebooks.R;
import devesh.ephrine.ebooks.UserProfileManager;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    public static final String EXTRA_MESSAGE = "ReadBookID";
    public String TAG = String.valueOf(R.string.app_name);
    public Context mContext;

    public UserProfileManager mUser;

    ArrayList<HashMap<String, String>> StoreBooksList;
    private ArrayList<HashMap<String, String>> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context mContext, ArrayList<HashMap<String, String>> myDataset) {
        mDataset = myDataset;
        this.mContext = mContext;
        mUser = new UserProfileManager(mContext);
        // mUser.Download();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        // TextView v = (TextView) LayoutInflater.from(parent.getContext())
        //       .inflate(R.layout.recycleview_books_list, parent, false);


        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycleview_books_list, parent, false);


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
        holder.LLBook.setTag(mDataset.get(position).get("bookid"));
        //    holder.AddToLibraryChip.setTag(mDataset.get(position).get("bookid"));

        holder.BookTitleTx.setText(mDataset.get(position).get("bookname"));
        holder.BookAuthorTx.setText("By " + mDataset.get(position).get("bookauthor"));

        Glide.with(holder.BookCoverPic)
                .load(mDataset.get(position).get("bookcover"))
                .into(holder.BookCoverPic);


    /*    holder.LLBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //view.getTag();
                Log.d(TAG, "onClick: " + view.getTag());
                String tag = view.getTag().toString();
            //    Intent intent = new Intent(mContext, BookViewActivity.class);
             //   intent.putExtra(EXTRA_MESSAGE, tag);

               // mContext.startActivity(intent);
            }
        });

    holder.AddToLibraryChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //view.getTag();
                Log.d(TAG, "onClick: Added to Library " + view.getTag());
                mUser.AddBookLibrary(view.getTag().toString());

            }
        });  */

        String ReadBookID = mDataset.get(position).get("bookid");
        File localFile = new File(mContext.getFilesDir(), "eBook" + ReadBookID + ".pdf");

        if (localFile.exists()) {
            //  pdfView.fromFile(localFile).load();
            Log.d(TAG, "Already Loaded PDF in Local Cache");
            //   holder.CloudDownloadIMG.setVisibility(View.GONE);
            holder.CheckIMG.setVisibility(View.VISIBLE);
        } else {
            //     holder.CloudDownloadIMG.setVisibility(View.VISIBLE);
            holder.CheckIMG.setVisibility(View.GONE);

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
        //     public ImageView CloudDownloadIMG;
        public ImageView CheckIMG;
        //  public Chip AddToLibraryChip;

        public MyViewHolder(View v) {
            super(v);
            //     textView = v.findViewById(R.id.textView3);
            BookTitleTx = v.findViewById(R.id.BookTitletextView4);
            BookAuthorTx = v.findViewById(R.id.BookAuthortextView5);
            LLBook = v.findViewById(R.id.LLBookListStore1);
            BookCoverPic = v.findViewById(R.id.BookCoverimageView2);

            //   CloudDownloadIMG= v.findViewById(R.id.cloudDownloadimageView3);
            CheckIMG = v.findViewById(R.id.CheckICOimageView4);
            //    AddToLibraryChip=v.findViewById(R.id.AddToLibraryChipchip4);

        }
    }


}
