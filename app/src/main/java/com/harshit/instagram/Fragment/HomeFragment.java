package com.harshit.instagram.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harshit.instagram.Adapter.PostAdapter;
import com.harshit.instagram.Model.Post;
import com.harshit.instagram.R;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Post> postlists;
    private PostAdapter postAdapter;
    private List<String> followingList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView=view.findViewById(R.id.recycler_view_home);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        postlists=new ArrayList<>();
        postAdapter=new PostAdapter(getContext(),postlists);
        recyclerView.setAdapter(postAdapter);
        checkFollowing();
        return view;
    }

    private void  checkFollowing(){
        //Toast.makeText(getContext(),"Checkfollowing",Toast.LENGTH_SHORT).show();
        followingList=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                followingList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());

          //          Toast.makeText(getContext(),"Followig List Population",Toast.LENGTH_SHORT).show();

                }
                readPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readPosts(){
       // Toast.makeText(getContext(),"ReadPost",Toast.LENGTH_SHORT).show();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                postlists.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){

                    Post post=snapshot.getValue(Post.class);

                    for(String id:followingList){

                            if (post.getPublisher().equals(id)) {
                                postlists.add(post);

                            }
                                          //         Toast.makeText(getContext(),"Adding Posts",Toast.LENGTH_SHORT).show();

                    }
                        postAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
