package app.rishabh.firebase.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.rishabh.firebase.R;
import app.rishabh.firebase.utils.AppUtils;
import app.rishabh.firebase.utils.MakeImageCircular;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class StorageActivity extends BaseActivity {


    @BindView(R.id.iv_toolbar_back)
    ImageView ivToolbarBack;
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_logout)
    TextView tvLogout;
    @BindView(R.id.iv_profile)
    ImageView ivProfile;
    @BindView(R.id.tv_db_data)
    TextView tvDbData;
    @BindView(R.id.btn_post_db)
    Button btnPostDb;
    private static final String TAG = RealtimeDbActivity.class.getName() ;
    File mFile;
    @BindView(R.id.layout_parent)
    RelativeLayout mLayoutParent;
    private String mCurrentPhotoPath;

    private Uri mImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_IMAGE_REQUEST = 2;
    private static final int REQUEST_CODE_CAMERA_WRITE = 3;
    private static final int REQUEST_TAKE_PHOTO = 9;


    private StorageReference mStorageRef;
    private DatabaseReference mDbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        ButterKnife.bind(this);
        mDbReference = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        initViews();
        initVariables();
        addValueEventListener();
    }

    /**
     * Method to initialize variables
     */
    private void initVariables() {
        mImageUri = Uri.EMPTY;
    }


    /**
     * Method to initialize views
     */
    private void initViews() {
        ivToolbarBack.setVisibility(View.VISIBLE);
        tvLogout.setVisibility(View.GONE);
        tvToolbarTitle.setText("Storage");
    }


    /**
     * To add value event listener on the database node to listen event changes
     * instantly.
     */
    private void addValueEventListener() {
        mDbReference.child("storage_links").child("data_posted").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.getValue(String.class));
                tvDbData.setText(dataSnapshot.getValue(String.class));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @OnClick({R.id.iv_toolbar_back, R.id.iv_image_select, R.id.btn_post_db})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                onBackPressed();
                break;
            case R.id.iv_image_select:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_WRITE);
                    return;
                }
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_WRITE);
                    return;
                }
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_CAMERA_WRITE);
                    return;
                } else {
                    selectImage();
                }


                break;
            case R.id.btn_post_db:
                if(mImageUri!=Uri.EMPTY)
                hitStorage();
                else
                    AppUtils.getInstance().showToast(this,"Please select image for uploading" );
                break;
        }
    }

    /**
     * Method to open image dialog.
     */
    private void selectImage() {

        try {
            final CharSequence[] options = {"Choose From Gallery","Take Photo","Remove","Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(StorageActivity.this);
            builder.setTitle(R.string.select_from_options);
            builder.setItems(options, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals("Take Photo")) {
                        dialog.dismiss();
                        dispatchTakePictureIntent();
                    } else if (options[item].equals("Choose From Gallery")) {
                        dialog.dismiss();
                        pickImageFromGallery();
                    } else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    } else if (options[item].equals("Remove")) {
                        ivProfile.setImageResource(R.drawable.ic_signup_img_holder);
                        mImageUri = Uri.EMPTY;
                        dialog.dismiss();
                    }
                }
            });
            builder.show();

        } catch (Exception e) {
            AppUtils.getInstance().showToast(this, "Camera Permission error");
            e.printStackTrace();
        }
    }

    /**
     * Method to perform action for picking up image from gallery.
     */
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Method to open camera to click image and get image uri from it.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                mFile = photoFile;
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mImageUri = FileProvider.getUriForFile(this,
                        "app.rishabh.firebase.fileprovider",
                        mFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * Method for creating image file for camera clicked picture.
     * @return generated file
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Broadcast to android that some image is being added to the storage.
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            mImageUri = data.getData();
            String[] projection = {MediaStore.MediaColumns.DATA};
            CursorLoader cursorLoader = new CursorLoader(this, uri, projection, null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            String selectedImagePath = cursor.getString(column_index);
            MakeImageCircular.makeImageCircularWithUri(this, selectedImagePath, ivProfile);

        } else if (requestCode == REQUEST_TAKE_PHOTO) {
            if (mCurrentPhotoPath != null) {
                MakeImageCircular.makeImageCircularWithUri(StorageActivity.this, mImageUri.toString(), ivProfile);
                galleryAddPic();
            }
        }
    }

    /**
     * Method to upload data to firebase storage.
     */
    public void hitStorage() {

        AppUtils.getInstance().showProgressDialog(this);
        tvDbData.setText("");
        StorageReference riversRef = mStorageRef.child("images/test-image");
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        assert bitmap != null;
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = riversRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                AppUtils.getInstance().hideProgressDialog();
                Uri profileUrl = taskSnapshot.getDownloadUrl();
                hitDB(profileUrl.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                AppUtils.getInstance().hideProgressDialog();
                AppUtils.getInstance().showToast(StorageActivity.this,exception.getMessage());
            }
        });

    }

    /**
     * Method to upload image url to the database.
     * @param imageUrl
     */
    private void hitDB(final String imageUrl) {
        mDbReference.child("storage_links").child("data_posted").setValue(imageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    tvDbData.setText(imageUrl);
                    resetViewAndData();
                } else if(task.getException()!=null)
                    AppUtils.getInstance().showToast(StorageActivity.this, task.getException().getMessage());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AppUtils.getInstance().showToast(StorageActivity.this, e.getMessage() );

            }
        });

    }

    /**
     * Method to reset view and data.
     */
    private void resetViewAndData() {
        mImageUri=Uri.EMPTY;
        ivProfile.setImageResource(R.drawable.ic_signup_img_holder);
    }


}

