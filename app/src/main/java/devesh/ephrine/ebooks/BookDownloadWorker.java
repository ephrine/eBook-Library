/*
 * Copyright (c) 2019. Ephrine Apps
 * Code written by Devesh Chaudhari
 * Website: https://www.ephrine.in
 */

package devesh.ephrine.ebooks;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;

public class BookDownloadWorker extends Worker {
    public String TAG = String.valueOf(R.string.app_name);
    public String ReadBookID;
    public String BookURL;
    public String BookName;

    public HashMap<String, String> StoreBook = new HashMap<String, String>();
    public File localFile;
    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManager;
    private Context mContext;

    public BookDownloadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        mContext = context;
    }

    @Override
    public Result doWork() {
        // Do the work here--in this case, upload the images.

        ReadBookID = getInputData().getString("bookid");
        BookURL = getInputData().getString("bookurl");
        BookName = getInputData().getString("bookname");

  /*      NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "0")
                .setSmallIcon(R.drawable.sync_baseline_cached_black_48dp)
                .setContentTitle("Ebook")
                .setContentText("Much longer text that cannot fit one line...")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        notificationManager.notify(0, builder.build());
*/


        String CHANNEL_ID = "1995";
        notificationManager = NotificationManagerCompat.from(mContext);
        builder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
        builder.setContentTitle(BookName)
                .setContentText("Downloading")
                .setSmallIcon(R.drawable.book_baseline_book_black_48dp)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

// Issue the initial notification with zero progress
        int PROGRESS_MAX = 100;
        int PROGRESS_CURRENT = 0;
        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
        notificationManager.notify(1995, builder.build());


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference mStorageRef = storage.getInstance().getReference();

        //  Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
        //StorageReference riversRef = mStorageRef.child("doc/file.pdf");
        StorageReference riversRef = storage.getReferenceFromUrl(BookURL);
        //StorageReference httpsReference = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/b/bucket/o/images%20stars.jpg");
        //  final PDFView pdfView = (PDFView) findViewById(R.id.fullscreen_content);


        try {
            Log.d(TAG, "LoadPDF: Trying to Load PDF");

            localFile = new File(mContext.getFilesDir(), "eBook" + ReadBookID + ".pdf");


            Log.d(TAG, "LoadPDF: Downloading");
            // localFile = File.createTempFile("eBook"+ReadBookID, "pdf");
            riversRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            // ...

                            builder.setContentText("Download complete")
                                    .setProgress(0, 0, false);
                            notificationManager.notify(1995, builder.build());

                            Log.d(TAG, "onSuccess: Downloaded File");
                            //   pdfView.fromFile(localFile).load();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    // ...
                    builder.setContentText("Download Failed !")
                            .setProgress(0, 0, false);
                    notificationManager.notify(1995, builder.build());
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //calculating progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    //displaying percentage in progress dialog
                    //   yourProgressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    int PROGRESS_MAX = 100;
                    int PROGRESS_CURRENT = ((int) progress);
                    builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
                    notificationManager.notify(1995, builder.build());


                }
            });


        } catch (Exception e) {
            Log.d(TAG, "LoadPDF: " + e);

        }


        // Indicate whether the task finished successfully with the Result
        return Result.success();
    }
}
