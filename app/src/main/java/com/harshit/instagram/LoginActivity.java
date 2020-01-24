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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
   // private EditText fullname;
    private Button btnLogin;
    private TextView jumpReg;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=findViewById(R.id.login_email);
        password=findViewById(R.id.login_password);
        btnLogin=findViewById(R.id.btn_login);
        jumpReg=findViewById(R.id.jump_reg);
       // fullname=findViewById(R.id.login_fullname);
        fAuth=FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eMail=email.getText().toString().trim();
                String pass=password.getText().toString().trim();
                loginTask(eMail,pass);

            }
        });
        jumpReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
    }

    void loginTask(String username,String password)
    {
        if(TextUtils.isEmpty(username)||TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),"All fields are required",Toast.LENGTH_SHORT).show();
        }
        else
        {
            fAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users").child(fAuth.getCurrentUser().getUid());
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    else{
                        Toast.makeText(LoginActivity.this,"Can't Login",Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

    }
}
