package com.harshit.instagram.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harshit.instagram.Fragment.ProfileFragment;
import com.harshit.instagram.Model.User;
import com.harshit.instagram.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>
{
    private Context hContext;
    private List<User> hUsers;
    private FirebaseUser firebaseUser;

    public UserAdapter(Context hcontext,List<User> hUsers){
        this.hContext=hcontext;
        this.hUsers=hUsers;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(hContext).inflate(R.layout.user_row_theme,viewGroup,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final User user=hUsers.get(i);
        viewHolder.btnFollow.setVisibility(View.VISIBLE);
        viewHolder.username.setText(user.getUsername());
        viewHolder.fullname.setText(user.getFullname());
        Glide.with(hContext).load(user.getImageurl()).into(viewHolder.profileImg);

        isFollowing(user.getId(),viewHolder.btnFollow);
        if(user.getId().equals(firebaseUser.getUid())){
            viewHolder.btnFollow.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor= hContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid", user.getId());
                editor.apply();

                ((FragmentActivity)hContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
            }
        });
        viewHolder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.btnFollow.getText().toString().equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following").child(user.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).child("Followers").child(firebaseUser.getUid()).setValue(true);

                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following").child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following").child(user.getId()).removeValue();

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return hUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public TextView fullname;
        public Button btnFollow;
        public CircleImageView profileImg;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            username=itemView.findViewById(R.id.follow_username);
            fullname=itemView.findViewById(R.id.follow_fullname);
            profileImg=itemView.findViewById(R.id.profile_img);
            btnFollow=itemView.findViewById(R.id.btn_follow);
        }
    }

    private void isFollowing(final String userid, final Button button){
        DatabaseReference dReference= FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following");
        dReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userid).exists())
                    button.setText("Following");
                else
                    button.setText("Follow");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}


