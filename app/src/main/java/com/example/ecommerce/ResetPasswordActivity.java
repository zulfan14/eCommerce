package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Privalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity
{
    private String check ="";
    private TextView rpTitle, rpIntruction;
    private EditText rpPhoneNumber, rpQuestion1, rpQuestion2;
    private Button rpVerivyButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        check = getIntent().getStringExtra("check");

        rpTitle = findViewById(R.id.rp_title);
        rpIntruction= findViewById(R.id.rp_intruction);
        rpPhoneNumber = findViewById(R.id.rp_phone_number);
        rpQuestion1 = findViewById(R.id.rp_Question1);
        rpQuestion2 = findViewById(R.id.rp_Question2);
        rpVerivyButton = findViewById(R.id.rp_verify_btn);

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        rpPhoneNumber.setVisibility(View.GONE);


        if (check.equals("settings"))
        {
            rpTitle.setText("Set Question");
            rpIntruction.setText("Tolong Buat Jawaban Untuk Pertanyaan Keamanan Anda");
            rpVerivyButton.setText("Buat Jawaban");

            displayAnswer();

            rpVerivyButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    setAnswers();

                }
            });
        }
        else if (check.equals("login"))
        {
            rpPhoneNumber.setVisibility(View.VISIBLE);

            rpVerivyButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    veryfyUser();
                }
            });
        }
    }

    private void setAnswers()
    {

        String answer1 = rpQuestion1.getText().toString().toLowerCase();
        String answer2 = rpQuestion2.getText().toString().toLowerCase();

        if (rpQuestion1.equals("") && rpQuestion2.equals(""))
        {
            Toast.makeText(ResetPasswordActivity.this, "Tolong Jawab Pertanyaan", Toast.LENGTH_SHORT).show();
        }
        else
        {
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Users")
                    .child(Prevalent.currentOnlineUser.getPhone());


            HashMap<String, Object> userdataMap = new HashMap<>();
            userdataMap.put("answer1", answer1);
            userdataMap.put("answer2", answer2);

            ref.child("Security Question").updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(ResetPasswordActivity.this, "Pembuatan Scurity Password Anda Sukse", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ResetPasswordActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                }
            });

        }
    }

    private void displayAnswer()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child(Prevalent.currentOnlineUser.getPhone());


        ref.child("Security Question").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String ans1 =dataSnapshot.child("answer1").getValue().toString();
                String ans2 = dataSnapshot.child("answer2").getValue().toString();

                rpQuestion1.setText(ans1);
                rpQuestion2.setText(ans2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void veryfyUser()
    {
        final String phone = rpPhoneNumber.getText().toString();
        final String answer1 = rpQuestion1.getText().toString().toLowerCase();
        final String answer2 = rpQuestion2.getText().toString().toLowerCase();

        if (!phone.equals("") && !answer1.equals("") && !answer2.equals(""))
        {
            final DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Users")
                    .child(phone);

            ref.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.exists())
                    {
                        String mPhone = dataSnapshot.child("phone").getValue().toString();

                        if (dataSnapshot.hasChild("Security Question"))
                        {
                            String ans1 =dataSnapshot.child("Security Question").child("answer1").getValue().toString();
                            String ans2 = dataSnapshot.child("Security Question").child("answer2").getValue().toString();

                            if (!ans1.equals(answer1))
                            {
                                Toast.makeText(ResetPasswordActivity.this, "Maaf Jawaban No 1 Yang Anda Masukkan Salah", Toast.LENGTH_SHORT).show();
                            }
                            else if (!ans2.equals(answer2))
                            {
                                Toast.makeText(ResetPasswordActivity.this, "Maaf Jawaban No 2 Yang Anda Masukkan Salah", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                builder.setTitle("Password Baru");

                                final EditText newPassword = new EditText(ResetPasswordActivity.this);
                                newPassword.setHint("Tolong Masukkan Password Baru Anda Disini");
                                builder.setView(newPassword);

                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        if (!newPassword.getText().toString().equals(""))
                                        {
                                            ref.child("password")
                                                    .setValue((newPassword.getText().toString()))
                                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                                    {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if (task.isSuccessful())
                                                            {
                                                                Toast.makeText(ResetPasswordActivity.this, "Password Berhasil diUbah", Toast.LENGTH_SHORT).show();

                                                                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });

                                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        dialog.cancel();
                                    }
                                });

                                builder.show();
                            }
                        }
                        else
                        {
                            Toast.makeText(ResetPasswordActivity.this, "Anda Tidak Mempunyai Pertanyaan keamanan", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(ResetPasswordActivity.this, "Maaf No Telpon Tidak Terdaftar", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
            });
        }

        else
        {
            Toast.makeText(this, "Tolong Lengkapi Form Ini", Toast.LENGTH_SHORT).show();
        }

    }
}
