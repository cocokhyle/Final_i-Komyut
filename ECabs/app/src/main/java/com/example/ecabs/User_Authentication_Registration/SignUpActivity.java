package com.example.ecabs.User_Authentication_Registration;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.ecabs.Activity.Once_Signup;
import com.example.ecabs.Activity.SettingActivity;
import com.example.ecabs.R;
import com.example.ecabs.Utils.ConnectionManager;
import com.example.ecabs.Utils.EncryptionUtil;
import com.example.ecabs.Utils.NetworkUtils;
import com.example.ecabs.Utils.UserHelper;
import com.example.ecabs.databinding.ActivitySignUpBinding;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.KeyPair;


public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private ConnectionManager connectionManager;

    FirebaseDatabase database;
    DatabaseReference reference;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private static final String SHARED_PREF_NAME= "MyPreferences";
   /* CheckBox TermsConditionCbox;
    CheckBox PrivacyPolicyCbox;*/

    EditText AgeTxt;
    int getAge;
    Boolean termsCondition = false;
    Boolean privacyPolicy = false;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        editor = preferences.edit();

        String text =binding.loginRedirectText.getText().toString();
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new UnderlineSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.loginRedirectText.setText(spannableString);

        String text2 =binding.termsConditionRedirect.getText().toString();
        SpannableString spannableString2 = new SpannableString(text2);
        spannableString2.setSpan(new UnderlineSpan(), 0, text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.termsConditionRedirect.setText(spannableString2);

        String text3 =binding.privacyPolicyRedirect.getText().toString();
        SpannableString spannableString3 = new SpannableString(text3);
        spannableString3.setSpan(new UnderlineSpan(), 0, text3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.privacyPolicyRedirect.setText(spannableString3);

        CheckBox TermsConditionCbox = binding.TermsConditionCbox;
        CheckBox PrivacyPolicyCbox = binding.privacyPolicyCbox;
        AgeTxt = binding.signupAge;

        int grey = ContextCompat.getColor(SignUpActivity.this, R.color.greyish);
        ColorStateList greyList = ColorStateList.valueOf(grey);
        int white = ContextCompat.getColor(SignUpActivity.this, R.color.white);
        ColorStateList whiteList = ColorStateList.valueOf(white);

        TermsConditionCbox.setEnabled(false);
        PrivacyPolicyCbox.setEnabled(false);


        binding.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.signupEmail.getText().toString();
                String filteredEmail = email.replaceAll("[@,.]", "").trim();
                String name = binding.signupName.getText().toString();
                String age = binding.signupAge.getText().toString();
                String contact = binding.signupConNo.getText().toString();
                String address = binding.signupAdd.getText().toString();
                String password = binding.signupPassword.getText().toString();
                String confirmPassword = binding.signupConfirm.getText().toString();

                if (NetworkUtils.isWifiConnected(getApplicationContext())) {

                    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    database = FirebaseDatabase.getInstance();
                    reference = database.getReference("users");



                        if ( email.equals("") ||name.equals("") || age.equals("") || contact.equals("") || address.equals("") || password.equals("") || confirmPassword.equals("")) {
                            Toast.makeText(SignUpActivity.this, "All fields are mandatory!", Toast.LENGTH_SHORT).show();
                        }
                        else if (!confirmPassword.equals(password)) {
                            Toast.makeText(SignUpActivity.this, "Password not match!", Toast.LENGTH_SHORT).show();
                        }
                        else if (password.length() < 8) {
                            Toast.makeText(SignUpActivity.this, "Password too short!", Toast.LENGTH_SHORT).show();
                        }
                        else if (getAge >= 18) {
                            Toast.makeText(SignUpActivity.this, "You must be at least 18 years old!", Toast.LENGTH_SHORT).show();
                        }
                        else if (!TermsConditionCbox.isChecked() || !PrivacyPolicyCbox.isChecked()) {
                            Toast.makeText(SignUpActivity.this, "PLease Read T&C and Privacy Policy!", Toast.LENGTH_SHORT).show();
                        }
                        else if (password.equals(email)) {
                            Toast.makeText(SignUpActivity.this, "Password should not equal to username!", Toast.LENGTH_SHORT).show();
                        } else {
                            Query checkUserDatabase = reference.orderByChild("email").equalTo(filteredEmail);

                            checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    // if the email is already exists
                                    if (snapshot.exists()){
                                        Toast.makeText(SignUpActivity.this, "This email address is already used!", Toast.LENGTH_SHORT).show();
                                    }else {
                                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                                            firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        try {
                                                            String encryptedPass = EncryptionUtil.encrypt(password);
                                                            getAge = Integer.parseInt(age);
                                                            String filteredEmail = email.replaceAll("[@,.]", "");
                                                            UserHelper userHelper = new UserHelper(filteredEmail, name, age, contact, address, encryptedPass);
                                                            reference.child(filteredEmail).setValue(userHelper);
                                                            reference.child(filteredEmail).child("history_id").setValue("0");

                                                            Toast.makeText(SignUpActivity.this, "Signup successfully, please verify your email!", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(SignUpActivity.this, Once_Signup.class);
                                                            startActivity(intent);

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                            Toast.makeText(SignUpActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                                                            throw new RuntimeException(e);
                                                        }

                                                    }
                                                }
                                            });
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                }else {
                    connectionManager = new ConnectionManager(SignUpActivity.this, editor);
                    connectionManager.lostConnectionDialog(SignUpActivity.this);
                }

            }

        });

        binding.loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        binding.eastBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean closeClicked = true;
                Intent intent = new Intent(SignUpActivity.this, SettingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);


            }
        });
        binding.termsConditionRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View alertCustomDialog = LayoutInflater.from(SignUpActivity.this).inflate(R.layout.dialog_terms_condition, null);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivity.this);
                alertDialog.setView(alertCustomDialog);
                Button closeBtn = (Button) alertCustomDialog.findViewById(R.id.agreeBtn);
                final AlertDialog dialog = alertDialog.create();

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                        termsCondition = true;
                        TermsConditionCbox.setEnabled(true);
                        TermsConditionCbox.setTextColor(white);
                        TermsConditionCbox.setButtonTintList(whiteList);
                        TermsConditionCbox.setChecked(true);
                    }
                });
            }
        });
        binding.privacyPolicyRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View alertCustomDialog = LayoutInflater.from(SignUpActivity.this).inflate(R.layout.dialog_privacy_policy, null);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivity.this);
                alertDialog.setView(alertCustomDialog);
                Button closeBtn = (Button) alertCustomDialog.findViewById(R.id.agreeBtn);
                final AlertDialog dialog = alertDialog.create();

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                        privacyPolicy = true;
                        PrivacyPolicyCbox.setEnabled(true);
                        PrivacyPolicyCbox.setTextColor(white);
                        PrivacyPolicyCbox.setButtonTintList(whiteList);
                        PrivacyPolicyCbox.setChecked(true);
                    }
                });
            }
        });

    }

}
