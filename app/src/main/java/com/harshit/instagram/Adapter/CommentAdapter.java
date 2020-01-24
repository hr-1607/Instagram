package com.harshit.instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harshit.instagram.CommentActivity;
import com.harshit.instagram.HomeActivity;
import com.harshit.instagram.Model.Comment;
import com.harshit.instagram.Model.User;
import com.harshit.instagram.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    Context hContext;
    List<Comment> commentList;
    FirebaseUser firebaseUser;

    public CommentAdapter(Context hContext, List<Comment> commentList) {
        this.hContext = hContext;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(hContext).inflate(R.layout.comment_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment=commentList.get(i);

        viewHolder.comment.setText(comment.getComment());
        userInfo(viewHolder.img_profile,viewHolder.username,comment.getPublisher());

        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(hContext, HomeActivity.class);
                intent.putExtra("publisherid",comment.getPublisher());
                hContext.startActivity(intent);
            }
        });


        viewHolder.img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(hContext, HomeActivity.class);
                intent.putExtra("publisherid",comment.getPublisher());
                hContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView img_profile;
        public TextView username,comment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_profile=itemView.findViewById(R.id.img_profile_cmt);
            username=itemView.findViewById(R.id.username_cmtitem);
            comment=itemView.findViewById(R.id.cmt_cmtitem);
        }
    }
    private void userInfo(final ImageView imageP,final TextView username,String pubisherid){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(pubisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                Glide.with(hContext).load(user.getImageurl()).into(imageP);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
