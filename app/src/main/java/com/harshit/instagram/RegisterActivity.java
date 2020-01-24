package com.harshit.instagram;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button btnRegister;
    private TextView jumpLogin;
    private EditText usrname;
    FirebaseAuth fAuth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email=findViewById(R.id.reg_email);
        password=findViewById(R.id.reg_password);
        btnRegister=findViewById(R.id.btn_register);
        jumpLogin=findViewById(R.id.jump_login);
        usrname=findViewById(R.id.reg_name);
        fAuth=FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eMail=email.getText().toString().trim();
                String pass=password.getText().toString().trim();
                String uname=usrname.getText().toString().trim();
                if(TextUtils.isEmpty(uname)|| TextUtils.isEmpty(pass)){
                    Toast.makeText(getApplicationContext(),"All fields ae required",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(RegisterActivity.this,"Please Wait..",Toast.LENGTH_SHORT).show();
                    registerTask(eMail,pass,uname);
                }
            }
        });

        jumpLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
    }

    void registerTask(final String email,final  String password, final String username){
       // Toast.makeText(getApplicationContext(),email,Toast.LENGTH_SHORT).show();
        fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user=fAuth.getCurrentUser();
                    String uid=user.getUid();


                    reference= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("id",uid);
                    hashMap.put("username",username);
                    hashMap.put("fullname","");
                    hashMap.put("bio","");

                    hashMap.put("imageurl","https://firebasestorage.googleapis.com/v0/b/instagram-5c0c0.appspot.com/o/profile_pic.png?alt=media&token=c80cd63c-effd-48a8-89e7-3f47ed29d983");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Intent intent =new Intent(RegisterActivity.this,HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(RegisterActivity.this,"Cannot Register",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {

                    FirebaseAuthException e=(FirebaseAuthException)task.getException();
                    Toast.makeText(RegisterActivity.this,"Cannot Register\n"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    Log.e("RegisterError","message"+e.getMessage());
                }
            }
        });
    }
}
