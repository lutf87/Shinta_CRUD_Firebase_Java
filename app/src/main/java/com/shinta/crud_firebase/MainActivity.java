package com.shinta.crud_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shinta.crud_firebase.databinding.ActivityMainBinding;

import java.util.Collections;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    private EditText NIM, Nama, Jurusan;
    private FirebaseAuth auth;
    private Button Logout, Simpan, Login, ShowData;

    private ActivityMainBinding binding;

    private int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        Logout = findViewById(R.id.btn_logout);
        Logout.setOnClickListener(this);

        Simpan = findViewById(R.id.btn_simpan);
        Simpan.setOnClickListener(this);

        Login = findViewById(R.id.btn_login);
        Login.setOnClickListener(this);

        ShowData = findViewById(R.id.btn_showdata);
        ShowData.setOnClickListener(this);

        NIM = findViewById(R.id.Ed_NIM);
        Nama = findViewById(R.id.Ed_Nama);
        Jurusan = findViewById(R.id.Ed_Jurusan);

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() == null) {
            defaultUI();
        } else {
            updateUI();
        }

    }

    private void updateUI() {
        Logout.setEnabled(true);
        Simpan.setEnabled(true);
        ShowData.setEnabled(true);
        Login.setEnabled(false);
        NIM.setEnabled(true);
        Nama.setEnabled(true);
        Jurusan.setEnabled(true);
    }

    private void defaultUI() {
        Logout.setEnabled(false);
        Simpan.setEnabled(false);
        ShowData.setEnabled(false);
        Login.setEnabled(true);
        NIM.setEnabled(false);
        Nama.setEnabled(false);
        Jurusan.setEnabled(false);
    }

    private  boolean isEmpty(String s) {
        return TextUtils.isEmpty(s);
    }

    protected void  onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {
            //Success Login
            if(resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                updateUI();
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Login Dibatalkan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_login:
                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.GoogleBuilder().build()))
                .setIsSmartLockEnabled(false).build(),RC_SIGN_IN);
                progressBar.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_simpan:
                //follow in the next step
                String getUserID = auth.getCurrentUser().getUid();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference getReference;

                String getNim = NIM.getText().toString();
                String getNama = Nama.getText().toString();
                String getJurusan = Jurusan.getText().toString();

                getReference = database.getReference();

                if (isEmpty(getNim) && isEmpty(getNama) && isEmpty(getJurusan)) {
                    Toast.makeText(MainActivity.this, "Data tidak boleh kosong!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    getReference.child("Admin").child(getUserID).child("Mahasiswa").push()
                            .setValue(new data_mahasiswa(getNim, getNama, getJurusan))
                            .addOnSuccessListener(this, new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    NIM.setText("");
                                    Nama.setText("");
                                    Jurusan.setText("");
                                    Toast.makeText(MainActivity.this, "Data Tersimpan",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                break;
            case R.id.btn_logout:
                AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "Logout Berhasil", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                break;
            case R.id.btn_showdata:
                //used to follow in the next step

        }

    }
}