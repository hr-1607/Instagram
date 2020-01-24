package com.harshit.instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
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
import com.harshit.instagram.Model.Post;
import com.harshit.instagram.Model.User;
import com.harshit.instagram.R;

import org.w3c.dom.Text;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    Context hContext;
    List<Post> hpost;

    public PostAdapter(Context hContext, List<Post> hpost) {
        this.hContext = hContext;
        this.hpost = hpost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(hContext).inflate(R.layout.post_layout,viewGroup,false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
       final  Post post=hpost.get(i);

        Glide.with(hContext).load(post.getPostimage()).into(viewHolder.image_post);
        if(post.getDescription()=="") {
            viewHolder.description.setVisibility(View.GONE);
        }
        else{
            viewHolder.description.setVisibility(View.VISIBLE);
            viewHolder.description.setText(post.getDescription());
        }


        publisher_info(viewHolder.image_profile,viewHolder.username,viewHolder.publisher,post.getPublisher());
        isLiked(post.getPostid(),viewHolder.like);
        nrlikes(post.getPostid(),viewHolder.likes);
        getComments(post.getPostid(),viewHolder.comments);



        viewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(viewHolder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference("Likes").child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);
                }

                else{
                    FirebaseDatabase.getInstance().getReference("Likes").child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
                }

            }
        });

        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(hContext, CommentActivity.class);
                intent.putExtra("postid",post.getPostid());
                intent.putExtra("publisherid",post.getPublisher());
                hContext.startActivity(intent);
            }
        });

        viewHolder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return hpost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile,image_post,like,comment;
        public TextView username,publisher,likes,comments,description;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile=itemView.findViewById(R.id.image_profile_pi);
            image_post=itemView.findViewById(R.id.post_image_pi);
            like=itemView.findViewById(R.id.ic_like_pi);
            comment=itemView.findViewById(R.id.ic_comment_pi);
            username=itemView.findViewById(R.id.username_pi);
            publisher=itemView.findViewById(R.id.publisher_pi);
            likes=itemView.findViewById(R.id.likes_pi);
            comments=itemView.findViewById(R.id.view_comments_pi);
            description=itemView.findViewById(R.id.description_pi);

        }
    }

    private void getComments(String postid,final TextView comments){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                comments.setText("View all "+dataSnapshot.getChildrenCount()+" Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void isLiked(String postid,final ImageView imageView){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Likes").child(postid);
        final String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(uid).exists()){
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                }
                else{
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void nrlikes(String postid,final TextView likes){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void publisher_info(final ImageView img_profile, final TextView username,final TextView publisher,final String userid){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                Glide.with(hContext).load(user.getImageurl()).into(img_profile);
                username.setText(user.getUsername());
                publisher.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
