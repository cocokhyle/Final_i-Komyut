package com.example.ecabs.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ecabs.DirectionHelper.TaskLoadedCallback;
import com.example.ecabs.Fragments.Maps_Fragment;
import com.example.ecabs.Fragments.TransitFragment;
import com.example.ecabs.R;
import com.example.ecabs.Utils.ConnectionManager;
import com.example.ecabs.Utils.NetworkUtils;
import com.example.ecabs.Utils.SQLHelper;
import com.example.ecabs.databinding.ActivityMainBinding;
import com.google.android.gms.maps.model.PolylineOptions;


public class MainActivity extends AppCompatActivity implements TaskLoadedCallback {

    ActivityMainBinding binding;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public static final long NETWORK_CHECK_INTERVAL = 5000; // 5 seconds
    private static final String SHARED_PREF_NAME= "MyPreferences";
    public static final String userLoc = "userLoc";
    public static final String userDes = "userDes";
    public static final String cost = "cost";
    public static final String connection = "con";
    public static final String fareDiscount = "fareDiscount";
    public static final String KEY_USERNAME =  "save_username";
    public static final String DEMO =  "demo";
    String con;
    String getFareDiscount;
    int demoPage = 0;
    String demo;
    private boolean doubleBackToExitPressedOnce = false;
    private ConnectionManager connectionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new Maps_Fragment());
        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        editor = preferences.edit();
        setBottomNavigationSelectedItem(R.id.map);

        // create DB
        SQLHelper dbHelper = new SQLHelper(this);
        demo = preferences.getString(DEMO, null);

        try {
            dbHelper.createDB();
        } catch (Exception ioe) {
            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
        }

        con = preferences.getString(connection, null);
        getFareDiscount = preferences.getString(fareDiscount, null);

        setFareDiscount();

        if (con == null){
            checkInternetConnection();
        }
        if (getFareDiscount != null){
            if (demo == null){
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        demoTutorial();
                    }
                }, 5000);

            }
        }
            //Activity Redirect


            if (getIntent().hasExtra("MAPS")) {
                boolean closeClicked = getIntent().getBooleanExtra("MAPS", false);
                if (closeClicked) {
                    replaceFragment(new Maps_Fragment());
                }
            }
            if (getIntent().hasExtra("TRANSIT")) {
                boolean closeClicked = getIntent().getBooleanExtra("TRANSIT", false);
                if (closeClicked) {
                    replaceFragment(new TransitFragment());
                }
            }


            if (getIntent().hasExtra("PROFILE")) {
                boolean closeClicked = getIntent().getBooleanExtra("PROFILE", false);
                if (closeClicked) {
                    // Perform the function to open the Home Fragment
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
            }
            if (getIntent().hasExtra("LOG_IN")) {
                String  clicked = getIntent().getStringExtra("LOG_IN");
                if (clicked != null) {
                    editor.putString(KEY_USERNAME, clicked);
                    editor.commit();
                    // Perform the function to open the Home Fragment
                    Intent intent = new Intent(MainActivity.this, Once_Login.class);
                    startActivity(intent);
                }
            }


            // Set animation for BottomNavigationView
            binding.bottomNavigationView.setItemHorizontalTranslationEnabled(false);
            binding.bottomNavigationView.setAnimationCacheEnabled(true);
            binding.bottomNavigationView.setItemTextAppearanceActive(R.style.BottomNavigationTextActive);
            binding.bottomNavigationView.setItemTextAppearanceInactive(R.style.BottomNavigationTextInactive);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*editor.remove(userLoc);
        editor.remove(userDes);
        editor.remove(cost);*/
        editor.remove(connection);
        editor.apply();

    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void setBottomNavigationSelectedItem(int itemId) {
            binding.bottomNavigationView.setSelectedItemId(itemId);
        }

    @Override
    public void onTaskDone(Object... values) {
        if(Maps_Fragment.currentPolyline != null) {
            Maps_Fragment.currentPolyline.remove();
        }
        Maps_Fragment.currentPolyline = Maps_Fragment.mapAPI.addPolyline((PolylineOptions) values[0]);
    }

    private void setFareDiscount(){
        if (getFareDiscount != null){

        }else {
            View alertCustomDialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_fare_discount, null);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setView(alertCustomDialog);
            Button submitFareBtn = (Button) alertCustomDialog.findViewById(R.id.submitFareBtn);
            CheckBox fareDiscounted1 = (CheckBox) alertCustomDialog.findViewById(R.id.checkBox1);
            CheckBox none = (CheckBox) alertCustomDialog.findViewById(R.id.fareDNone);
            final AlertDialog dialog = alertDialog.create();

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            fareDiscounted1.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                if (isChecked) {
                    none.setChecked(false);
                }

            });
            none.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                if (isChecked) {
                    fareDiscounted1.setChecked(false);
                }

            });



            submitFareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (none.isChecked() || fareDiscounted1.isChecked()){
                        if (none.isChecked()){
                            editor.putString(fareDiscount, "none");
                        }else if (fareDiscounted1.isChecked()){
                            editor.putString(fareDiscount, "discounted");
                        }
                        editor.apply();
                        dialog.cancel();
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }else {
                        Toast.makeText(MainActivity.this, "Choose one for discount!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!doubleBackToExitPressedOnce) {
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }

    }
    public void checkInternetConnection(){
        if (NetworkUtils.isWifiConnected(getApplicationContext())) {

        }else {
            connectionManager = new ConnectionManager(MainActivity.this, editor);
            connectionManager.lostConnectionDialog(MainActivity.this);
        }
    }
    private void demoTutorial(){

        Drawable pindrawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_pin);
        Drawable searchdrawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_search);

        //System demo for new user
        View alertCustomDialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_tutorial, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setView(alertCustomDialog);
        //the whole container
        LinearLayout container1 = (LinearLayout) alertCustomDialog.findViewById(R.id.container);
        //eastback
        LinearLayout container2 = (LinearLayout) alertCustomDialog.findViewById(R.id.container1);
        //search field
        LinearLayout savedClearBtn = (LinearLayout) alertCustomDialog.findViewById(R.id.show5);
        LinearLayout nextPageContainer = (LinearLayout) alertCustomDialog.findViewById(R.id.show7);
        LinearLayout mainContainer = (LinearLayout) alertCustomDialog.findViewById(R.id.mainContainer);
        LinearLayout lastContainer = (LinearLayout) alertCustomDialog.findViewById(R.id.show8);
        AutoCompleteTextView demoSearchLocationField = (AutoCompleteTextView) alertCustomDialog.findViewById(R.id.demoSearchBar);
        AutoCompleteTextView demoDestinationField = (AutoCompleteTextView) alertCustomDialog.findViewById(R.id.show4);
        Button nextBtn  = (Button) alertCustomDialog.findViewById(R.id.nextBtn);
        TextView demoMessage = (TextView) alertCustomDialog.findViewById(R.id.demoMessage);
        TextView demoCurrentLocBtn = (TextView) alertCustomDialog.findViewById(R.id.show3);
        TextView infoTxt = (TextView) alertCustomDialog.findViewById(R.id.infoTxt);
        View demoArrow = (View) alertCustomDialog.findViewById(R.id.demoArrow1);
        View demoArro2 = (View) alertCustomDialog.findViewById(R.id.demoArrow2);
        View demoArrow3 = (View) alertCustomDialog.findViewById(R.id.demoArrow3);
        Button demoNextBtn = (Button) alertCustomDialog.findViewById(R.id.show6);
        Button continueBtn = (Button) alertCustomDialog.findViewById(R.id.continueBtn);
        final AlertDialog dialog = alertDialog.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        demoPage = 1;

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString(DEMO, "demoSearc");
                editor.commit();
                dialog.cancel();
            }
        });

        demoSearchLocationField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                container1.setBackgroundResource(R.drawable.search_container);
                container2.setVisibility(View.VISIBLE);
                demoSearchLocationField.setHint("Type your Location");
                demoSearchLocationField.setCompoundDrawablesWithIntrinsicBounds(pindrawable, null, null, null);
                demoMessage.setText("Put your location here.");
                nextBtn.setVisibility(View.VISIBLE);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if (demoPage == 1){
                    demoCurrentLocBtn.setVisibility(View.VISIBLE);
                    demoMessage.setText("Click this if you want to put your exact location.");
                    hideKeyboard(view);
                    demoPage = 2;
                } else if (demoPage ==2) {
                    demoDestinationField.setVisibility(View.VISIBLE);
                    demoMessage.setText("Put your destination here.");
                    demoPage = 3;
                } else if (demoPage ==3) {
                    savedClearBtn.setVisibility(View.VISIBLE);
                    demoMessage.setText("Check this if you want to save this search.\n\nNote: You can access this if you logged in.");
                    demoArrow.setVisibility(View.GONE);
                    demoArro2.setVisibility(View.VISIBLE);
                    demoPage = 4;
                } else if (demoPage == 4) {
                    demoMessage.setText("Click this if you want to clear\nboth location and destination.");
                    demoArro2.setVisibility(View.GONE);
                    demoArrow3.setVisibility(View.VISIBLE);
                    demoPage = 5;
                } else if (demoPage ==5 ) {
                    demoMessage.setText("Click Next button to go to the next process.");
                    demoNextBtn.setVisibility(View.VISIBLE);
                    demoArrow.setVisibility(View.VISIBLE);
                    demoArrow3.setVisibility(View.GONE);
                    demoPage = 6;
                } else if (demoPage ==6) {
                    demoSearchLocationField.setVisibility(View.GONE);
                    demoCurrentLocBtn.setVisibility(View.GONE);
                    demoDestinationField.setVisibility(View.GONE);
                    savedClearBtn.setVisibility(View.GONE);
                    demoNextBtn.setVisibility(View.GONE);
                    nextPageContainer.setVisibility(View.VISIBLE);
                    infoTxt.setText("What do you prefer?");
                    demoMessage.setText("Choose which one do you prefer.");

                    demoPage = 7;
                } else if (demoPage == 7) {

                    demoNextBtn.setVisibility(View.VISIBLE);
                    demoNextBtn.setText("Search");
                    demoMessage.setText("Click Search button to show the search location and destination to the maps.");
                    demoPage = 8;
                } else if (demoPage == 8) {
                    mainContainer.setVisibility(View.GONE);
                    lastContainer.setVisibility(View.VISIBLE);

                }

            }
        });

    }
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
