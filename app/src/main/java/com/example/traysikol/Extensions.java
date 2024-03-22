package com.example.traysikol;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Extensions {
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    public static List<List<Double>> convertToList(double[][] array) {
        List<List<Double>> list = new ArrayList<>();

        for (double[] row : array) {
            List<Double> sublist = new ArrayList<>();
            for (double value : row) {
                sublist.add(value);
            }
            list.add(sublist);
        }

        return list;
    }
    public static void ChooseActions(Activity activity){
        String options[] = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    try {
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        takePicture.resolveActivity(activity.getPackageManager());
                        File photo = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "capture.jpg");
                        Uri imageUri = FileProvider.getUriForFile(Objects.requireNonNull(activity),
                                BuildConfig.APPLICATION_ID + ".provider", photo);
                        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(activity, takePicture, 1223, null);
                    } catch (Exception ee)
                    {
                        System.out.println("321355sx" + ee.getMessage());
                    }
                } else if (which == 1) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(activity);
                }
            }
        });
        builder.create().show();
    }
    public static void UploadProfilePicture(Activity context, Uri imageUri){
        StorageReference storageProfilePicture = FirebaseStorage.getInstance().getReference().child("ProfilePicture");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle("Setting your profile");
        dialog.setMessage("Please wait, while we are saving your profile.");
        dialog.show();
        if(imageUri != null){
            StorageReference ref = storageProfilePicture.child(FirebaseAuth.getInstance().getCurrentUser().getUid()+".jpg");
            UploadTask uploadTask  = ref.putFile(imageUri);
            uploadTask.continueWithTask(task -> {
                if(!task.isSuccessful()){
                    Toast.makeText(context,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
                return ref.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Uri downloadUri = (Uri) task.getResult();
                    String url = downloadUri.toString();
                    HashMap<String,Object> userMap = new HashMap<>();
                    userMap.put("profilePicture",url);
                    reference
                            .child("Accounts")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        GlobalClass.UserAccount.setProfilePicture(url);
                                        dialog.cancel();
                                        Toast.makeText(context, "Profile picture has been save.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                }
                            });

                }
            });
        }else{
            dialog.dismiss();
            Toast.makeText(context,"No image selected!",Toast.LENGTH_LONG).show();
        }
    }
    public static void CreateNotification (Context activity, Class<?> goTo, String message) {
        Intent resultIntent = new Intent(activity, goTo);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        resultIntent.setAction(Intent.ACTION_MAIN);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService( NOTIFICATION_SERVICE ) ;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity , default_notification_channel_id ) ;
        mBuilder.setContentTitle( "Traysikol" ) ;
        mBuilder.setContentText( message ) ;
        mBuilder.setSmallIcon(R.drawable.app_logo);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel( true ) ;

        long[] vibrationPattern = {0,3000}; // Vibrate for 1 second, then pause for 1 second, repeat
        mBuilder.setVibrate(vibrationPattern);

        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel) ;
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(( int ) System. currentTimeMillis () , mBuilder.build()) ;
    }
}
