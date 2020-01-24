package com.harshit.instagram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.harshit.instagram.Fragment.HomeFragment;
import com.harshit.instagram.Fragment.NotficationFragment;
import com.harshit.instagram.Fragment.ProfileFragment;
import com.harshit.instagram.Fragment.SearchFragment;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigationView=findViewById(R.id.bottom_nav_view);

        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        Bundle intent=getIntent().getExtras();
        if(intent!=null){
            String publisher=intent.getString("publisherid");
            SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
            editor.putString("profileid",publisher);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
        }
        else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        }


    }

   private BottomNavigationView.OnNavigationItemSelectedListener navListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
       @Override
       public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
           switch (menuItem.getItemId()){

               case R.id.icn_home: selectedFragment=new HomeFragment();
                   break;
               case R.id.icn_search:selectedFragment=new SearchFragment();
                   break;
               case R.id.icn_add:startActivity(new Intent(HomeActivity.this,PostActivity.class));
                   break;
               case R.id.icn_heart:selectedFragment=new NotficationFragment();
                   break;
               case R.id.icn_profile:
                   SharedPreferences.Editor editor=getSharedPreferences("PREF",MODE_PRIVATE).edit();
                   editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                   editor.apply();
                   selectedFragment=new ProfileFragment();
                   break;

           }
           if(selectedFragment!=null){
               getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
           }
           return true;
       }
   };
}
