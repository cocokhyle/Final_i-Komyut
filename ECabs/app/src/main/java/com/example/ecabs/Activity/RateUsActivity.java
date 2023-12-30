package com.example.ecabs.Activity;

import static android.view.View.OnClickListener;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecabs.R;
import com.example.ecabs.Utils.ConnectionManager;
import com.example.ecabs.Utils.NetworkUtils;
import com.example.ecabs.databinding.ActivityRateUsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RateUsActivity extends AppCompatActivity {

    private ActivityRateUsBinding binding;
    public static final String SHARED_PREF_NAME= "MyPreferences";

    private int rate = 1;
    private int Drate = 1;
    int userID;
    private ConnectionManager connectionManager;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    EditText commentTxt;
    String userIdKey;

    public static final String UserIDKey = "userIdKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRateUsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        editor = preferences.edit();

        userIdKey = preferences.getString(UserIDKey, null);

        ImageView close = view.findViewById(R.id.close);
        TextView skip = view.findViewById(R.id.skip);
        Button submitBtn = view.findViewById(R.id.submitBtn);

        ImageView rate1 = view.findViewById(R.id.rate1);
        ImageView rate2 = view.findViewById(R.id.rate2);
        ImageView rate3 = view.findViewById(R.id.rate3);
        ImageView rate4 = view.findViewById(R.id.rate4);
        ImageView rate5 = view.findViewById(R.id.rate5);

        ImageView Drate1 = view.findViewById(R.id.Drate1);
        ImageView Drate2 = view.findViewById(R.id.Drate2);
        ImageView Drate3 = view.findViewById(R.id.Drate3);
        ImageView Drate4 = view.findViewById(R.id.Drate4);
        ImageView Drate5 = view.findViewById(R.id.Drate5);
        commentTxt = view.findViewById(R.id.commentTxt);

        View.OnClickListener clickListener = v ->{

            if (v == rate1 ){
                rate1.setImageResource(R.drawable.ic_star);
                rate2.setImageResource(R.drawable.ic_star_white);
                rate3.setImageResource(R.drawable.ic_star_white);
                rate4.setImageResource(R.drawable.ic_star_white);
                rate5.setImageResource(R.drawable.ic_star_white);
                rate = 1;

            }
            if (v == rate2){
                rate1.setImageResource(R.drawable.ic_star);
                rate2.setImageResource(R.drawable.ic_star);
                rate3.setImageResource(R.drawable.ic_star_white);
                rate4.setImageResource(R.drawable.ic_star_white);
                rate5.setImageResource(R.drawable.ic_star_white);
                rate = 2;

            }
            if (v == rate3){
                rate1.setImageResource(R.drawable.ic_star);
                rate2.setImageResource(R.drawable.ic_star);
                rate3.setImageResource(R.drawable.ic_star);
                rate4.setImageResource(R.drawable.ic_star_white);
                rate5.setImageResource(R.drawable.ic_star_white);
                rate = 3;

            }
            if (v == rate4 ){
                rate1.setImageResource(R.drawable.ic_star);
                rate2.setImageResource(R.drawable.ic_star);
                rate3.setImageResource(R.drawable.ic_star);
                rate4.setImageResource(R.drawable.ic_star);
                rate5.setImageResource(R.drawable.ic_star_white);
                rate = 4;
            }
            if (v == rate5){
                rate1.setImageResource(R.drawable.ic_star);
                rate2.setImageResource(R.drawable.ic_star);
                rate3.setImageResource(R.drawable.ic_star);
                rate4.setImageResource(R.drawable.ic_star);
                rate5.setImageResource(R.drawable.ic_star);
                rate = 5;
            }
            if (v == Drate1){

                Drate1.setImageResource(R.drawable.ic_star);
                Drate2.setImageResource(R.drawable.ic_star_white);
                Drate3.setImageResource(R.drawable.ic_star_white);
                Drate4.setImageResource(R.drawable.ic_star_white);
                Drate5.setImageResource(R.drawable.ic_star_white);
                Drate = 1;
            }
            if (v == Drate2){
                Drate1.setImageResource(R.drawable.ic_star);
                Drate2.setImageResource(R.drawable.ic_star);
                Drate3.setImageResource(R.drawable.ic_star_white);
                Drate4.setImageResource(R.drawable.ic_star_white);
                Drate5.setImageResource(R.drawable.ic_star_white);
                Drate = 2;
            }
            if (v == Drate3){
                Drate1.setImageResource(R.drawable.ic_star);
                Drate2.setImageResource(R.drawable.ic_star);
                Drate3.setImageResource(R.drawable.ic_star);
                Drate4.setImageResource(R.drawable.ic_star_white);
                Drate5.setImageResource(R.drawable.ic_star_white);
                Drate = 3;
            }
            if (v == Drate4){
                Drate1.setImageResource(R.drawable.ic_star);
                Drate2.setImageResource(R.drawable.ic_star);
                Drate3.setImageResource(R.drawable.ic_star);
                Drate4.setImageResource(R.drawable.ic_star);
                Drate5.setImageResource(R.drawable.ic_star_white);
                Drate = 4;
            }
            if (v == Drate5){
                Drate1.setImageResource(R.drawable.ic_star);
                Drate2.setImageResource(R.drawable.ic_star);
                Drate3.setImageResource(R.drawable.ic_star);
                Drate4.setImageResource(R.drawable.ic_star);
                Drate5.setImageResource(R.drawable.ic_star);
                Drate = 5;
            }
        };
        rate1.setOnClickListener(clickListener);
        rate2.setOnClickListener(clickListener);
        rate3.setOnClickListener(clickListener);
        rate4.setOnClickListener(clickListener);
        rate5.setOnClickListener(clickListener);
        Drate1.setOnClickListener(clickListener);
        Drate2.setOnClickListener(clickListener);
        Drate3.setOnClickListener(clickListener);
        Drate4.setOnClickListener(clickListener);
        Drate5.setOnClickListener(clickListener);


        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RateUsActivity.this, SettingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        skip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RateUsActivity.this, SettingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });



        View alertCustomDialog = LayoutInflater.from(RateUsActivity.this).inflate(R.layout.dialog_rateus, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RateUsActivity.this);
        alertDialog.setView(alertCustomDialog);
        Button closeBtn = (Button) alertCustomDialog.findViewById(R.id.successDone);
        final AlertDialog dialog = alertDialog.create();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users_feedbacks");
        Query checkUserDatabase = reference.orderByChild("UserID");

        if (userIdKey != null){
            userID = Integer.valueOf(userIdKey);
        }else{
            checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        String userIDFromDB = snapshot.child("UserID").getValue(String.class);
                        if (!userIDFromDB.equals(-1)){
                            userID = Integer.valueOf(userIDFromDB) + 1;


                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        Handler handler = new Handler();
        View.OnClickListener clickListener1 = v ->{
            if (v == submitBtn){
                submitBtn.setBackgroundResource(R.drawable.button_hover_5radius);
            }
            if (v == closeBtn){
                closeBtn.setBackgroundResource(R.drawable.button_hover);

            }



            handler.postDelayed(new Runnable() {
                @Override
                public void run() {


                    if (v == submitBtn){

                        submitBtn.setBackgroundResource(R.drawable.button_5radius);
                        if (NetworkUtils.isWifiConnected(getApplicationContext())) {
                            String rateValue = String.valueOf(rate);
                            String getUserID = String.valueOf(userID);
                            String comment = commentTxt.getText().toString().trim();
                            reference.child("users_ratings").child("app_ratings").child("UserID").child(getUserID).setValue(rateValue);
                            reference.child("users_ratings").child("drivers_ratings").child("UserID").child(getUserID).setValue(rateValue);
                            reference.child("users_ratings").child("user_comment").child("UserID").child(getUserID).setValue(comment);
                            reference.child("UserID").setValue(getUserID);
                            if (userIdKey != null){

                            }else {
                                editor.putString(UserIDKey, getUserID);
                                editor.apply();
                            }
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();

                        }else {
                            connectionManager = new ConnectionManager(RateUsActivity.this, editor);
                            connectionManager.lostConnectionDialog(RateUsActivity.this);
                        }

                    }
                    if (v == closeBtn){
                        closeBtn.setBackgroundResource(R.drawable.fragment_transit_button_boxs);
                        dialog.cancel();
                        Toast.makeText(RateUsActivity.this, "Thank you!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RateUsActivity.this, SettingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }

                }
            }, 100);
        };
        submitBtn.setOnClickListener(clickListener1);
        closeBtn.setOnClickListener(clickListener1);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RateUsActivity.this, SettingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}