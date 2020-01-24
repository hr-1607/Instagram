package com.harshit.instagram;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harshit.instagram.Adapter.CommentAdapter;
import com.harshit.instagram.Model.Comment;
import com.harshit.instagram.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Comment> commentList;
    private CommentAdapter commentAdapter;

    Toolbar toolbar;
    EditText addComment;
    TextView post;
    ImageView img_profile;
    String postid;
    String publisherid;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        toolbar =findViewById(R.id.toolbar_cmt);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comment");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addComment=findViewById(R.id.et_add_cmt);
        post=findViewById(R.id.post_cmt);
        img_profile=findViewById(R.id.img_profile_cmt);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        recyclerView=findViewById(R.id.recycler_cmt);
        commentList=new ArrayList<>();
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        commentAdapter=new CommentAdapter(this,commentList);
        recyclerView.setAdapter(commentAdapter);

        Intent intent=getIntent();

        postid= intent.getStringExtra("postid");
        publisherid= intent.getStringExtra("publisherid");

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (addComment.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Can't add empty comment",Toast.LENGTH_SHORT).show();
                }
                else{
                    AddComment();

                }
            }
        });
        getImage();
        readComments();
    }
    private void AddComment(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("comment",addComment.getText().toString());
        hashMap.put("publisher",firebaseUser.getUid());

        reference.push().setValue(hashMap);
        addComment.setText("");
    }
    private void getImage(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(img_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readComments(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Comment cmt=snapshot.getValue(Comment.class);
                    commentList.add(cmt);

                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
 