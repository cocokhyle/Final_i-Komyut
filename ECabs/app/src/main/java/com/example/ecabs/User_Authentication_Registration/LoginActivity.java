package com.example.ecabs.User_Authentication_Registration;

import static com.example.ecabs.Activity.MainActivity.KEY_USERNAME;
import static com.example.ecabs.Activity.ProfileActivity.KEY_ADD;
import static com.example.ecabs.Activity.ProfileActivity.KEY_AGE;
import static com.example.ecabs.Activity.ProfileActivity.KEY_EMAIL;
import static com.example.ecabs.Activity.ProfileActivity.KEY_NAME;
import static com.example.ecabs.Activity.ProfileActivity.KEY_NO;
import static com.example.ecabs.Activity.RateUsActivity.SHARED_PREF_NAME;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecabs.Activity.Once_Login;
import com.example.ecabs.Activity.SettingActivity;
import com.example.ecabs.R;
import com.example.ecabs.Utils.ConnectionManager;
import com.example.ecabs.Utils.DatabaseHelper;
import com.example.ecabs.Utils.EncryptionUtil;
import com.example.ecabs.Utils.NetworkUtils;
import com.example.ecabs.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.KeyPair;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private DatabaseHelper databaseHelper;
    private Handler networkCheckHandler = new Handler();
    private Runnable networkCheckRunnable;
    private ConnectionManager connectionManager;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private String email;
    private String password;

    private String getNewPass1;
    private String getNewPass2;
    private String getUserName;
    private String getContactNo;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);

        firebaseAuth = FirebaseAuth.getInstance();

        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        editor = preferences.edit();

        String text = "Sign Up";
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new UnderlineSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.signupRedirectText.setText(spannableString);

        databaseHelper = new DatabaseHelper(this);

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.progressBar.setVisibility(View.VISIBLE);
                email = binding.loginEmail.getText().toString();
                password = binding.loginPassword.getText().toString();


                if (NetworkUtils.isWifiConnected(getApplicationContext())) {
                    if (email.equals("") || password.equals("")) {
                        Toast.makeText(LoginActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                        binding.progressBar.setVisibility(View.GONE);
                    } else {
                        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                if (firebaseAuth.getCurrentUser().isEmailVerified()){
                                    checkUser();
                                    binding.progressBar.setVisibility(View.GONE);
                                }else {
                                    Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                                    binding.progressBar.setVisibility(View.GONE);
                                }
                            }
                            else {
                                // Handle the sign-in failure, including invalid credentials
                                Exception exception = task.getException();
                                if (exception instanceof FirebaseAuthInvalidUserException) {
                                    // This exception is thrown if the user does not exist or is disabled
                                    Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                                } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                    // This exception is thrown if the password is incorrect
                                    Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Handle other exceptions
                                    Toast.makeText(LoginActivity.this, "Sign-in failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                binding.progressBar.setVisibility(View.GONE);
                            }

                        });

                    }
                }else {
                    connectionManager = new ConnectionManager(LoginActivity.this, editor);
                    connectionManager.lostConnectionDialog(LoginActivity.this);
                }
            }
        });

        binding.signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        binding.eastBack1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean closeClicked = true;
                // Rest of your code here...
                Intent intent = new Intent(LoginActivity.this, SettingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });
        binding.forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getEmail = binding.loginEmail.getText().toString().trim();
                if (NetworkUtils.isWifiConnected(getApplicationContext())) {
                    if (!getEmail.equals("")){
                       firebaseAuth.sendPasswordResetEmail(getEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful()) {
                                   // Password reset email sent successfully
                                   Toast.makeText(LoginActivity.this, "Password reset email sent, check your email!", Toast.LENGTH_LONG).show();
                               } else {
                                   // If the task is not successful, show an error message
                                   Toast.makeText(LoginActivity.this, "Failed to send password reset email!", Toast.LENGTH_LONG).show();
                               }
                           }
                       });
                    }else {
                        Toast.makeText(LoginActivity.this, "Enter your email!", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    connectionManager = new ConnectionManager(LoginActivity.this, editor);
                    connectionManager.lostConnectionDialog(LoginActivity.this);
                }

            }
        });



    }
    public void checkUser(){
        String userUserName = binding.loginEmail.getText().toString().trim();
        String filteredEmail = userUserName.replaceAll("[@,.]", "").trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("email").equalTo(filteredEmail);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    try {
                        String emailFromDB = snapshot.child(filteredEmail).child("email").getValue(String.class);
                        String nameFromDB = snapshot.child(filteredEmail).child("name").getValue(String.class);
                        String ageFromDB = snapshot.child(filteredEmail).child("age").getValue(String.class);
                        String contactFromDB = snapshot.child(filteredEmail).child("contact").getValue(String.class);
                        String addressFromDB = snapshot.child(filteredEmail).child("address").getValue(String.class);

                        //store the above to sharedPref
                        if (emailFromDB != null && ageFromDB != null && contactFromDB != null && addressFromDB != null){
                            editor.putString(KEY_EMAIL, filteredEmail);
                            editor.putString(KEY_NAME, nameFromDB);
                            editor.putString(KEY_AGE, ageFromDB);
                            editor.putString(KEY_ADD, addressFromDB);
                            editor.putString(KEY_NO, contactFromDB);
                            editor.putString(KEY_USERNAME, "save_username");
                            editor.commit();

                            Toast.makeText(LoginActivity.this, "Login successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, Once_Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }else {
                            Toast.makeText(LoginActivity.this, "Empty!", Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
