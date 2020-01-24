package com.harshit.instagram;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    Uri imageUri;
    String myUrl="";
    StorageTask uploadTask;
    StorageReference storageReference;
    ImageView close,imageAdded;
    TextView post;
    EditText decription;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        close=findViewById(R.id.close_post);
        imageAdded=findViewById(R.id.image_added_post);
        post=findViewById(R.id.tv_post);
        decription=findViewById(R.id.image_description);

        //setting progress dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_dialog);
        dialog=builder.create();

        storageReference= FirebaseStorage.getInstance().getReference("posts");

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this,HomeActivity.class));
                finish();
            }
        });


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        CropImage.activity().setAspectRatio(1,1).start(PostActivity.this);

    }

    private String getFileExtention(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        dialog.show();

        if(imageUri!=null){
            final StorageReference filereference=storageReference.child(System.currentTimeMillis()+"."+getFileExtention(imageUri));
            uploadTask =filereference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri=task.getResult();
                        myUrl=downloadUri.toString();

                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts");
                        String postid=reference.push().getKey();

                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("postid",postid);
                        hashMap.put("postimage",myUrl);
                        hashMap.put("description",decription.getText().toString());
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());


                        reference.child(postid).setValue(hashMap);

                        dialog.dismiss();

                        startActivity(new Intent(PostActivity.this,HomeActivity.class));
                        finish();
                    }
                    else{
                        Toast.makeText(PostActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(PostActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri =result.getUri();
            imageAdded.setImageURI(imageUri);
        }

        else{
            Toast.makeText(PostActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this,HomeActivity.class));
            finish();
        }
    }
}
