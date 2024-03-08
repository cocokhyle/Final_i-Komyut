package com.example.ecabs.Activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecabs.DirectionHelper.TaskLoadedCallback;
import com.example.ecabs.Fragments.Maps_Fragment;
import com.example.ecabs.Fragments.TransitFragment;
import com.example.ecabs.R;
import com.example.ecabs.Utils.ConnectionManager;
import com.example.ecabs.Utils.NetworkUtils;
import com.example.ecabs.Utils.SQLHelper;
import com.example.ecabs.databinding.ActivityMainBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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

    //This is the current version
    private String ver = "ver2.0";
    String verUpdate;
    String description;
    private String url;
    private boolean doubleBackToExitPressedOnce = false;
    private ConnectionManager connectionManager;

    int idArraySize;
    SQLHelper dbHelper;



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
        dbHelper = new SQLHelper(this);
        demo = preferences.getString(DEMO, null);



        try {
            dbHelper.createDB();
            Log.d("Error", "pass");

        } catch (Exception ioe) {
            Log.d("Error", "try_catch");
            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("update");
        Query checkUpdate = reference.orderByChild("version");

        checkUpdate.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    verUpdate = snapshot.child("version").getValue(String.class);
                    url = snapshot.child("url").getValue(String.class);
                    description = snapshot.child("description").getValue(String.class);

                   if (verUpdate != null){

                       if (!ver.equals(verUpdate)) {
                           newUpdate();
                       }else{

                       }
                   }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



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
      /*  editor.remove(userLoc);
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
    public void newUpdate(){
        View alertCustomDialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_update, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setView(alertCustomDialog);
        Button updateBtn = (Button) alertCustomDialog.findViewById(R.id.updateBtn);
        TextView textView = (TextView) alertCustomDialog.findViewById(R.id.versionTxt);
        TextView desTxt = (TextView) alertCustomDialog.findViewById(R.id.descriptionTxt);
        final AlertDialog dialog = alertDialog.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        if (verUpdate != null ){
            textView.setText("Update to " + verUpdate);
        }
        if (description !=null){
            desTxt.setText(description);
        }


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(View view) {
                dialog.cancel();
                goToUrl(url);
            }
        });

    }

    private void goToUrl(String url) {
        Uri uri = Uri.parse(url);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void executeSQLScriptFromApi() {
        String apiUrl = "http://192.168.100.4:5000/api/getData";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    dbHelper = new SQLHelper(MainActivity.this);
                    JSONArray graphArray = response.getJSONArray("graph");
                    SQLiteDatabase db = dbHelper.getWritableDatabase();  // Assuming dbHelper is an instance of SQLHelper

                    for (int i = 0; i < graphArray.length(); i++) {
                        JSONObject jsonData = graphArray.getJSONObject(i);
                        String tableName = "graph";

                        int id = jsonData.getInt("id");
                        double weightFare = jsonData.getDouble("weight_fare");

                        if (isRecordExists(db, tableName, id, weightFare)) {
                            updateWeightFare(db, tableName, id, weightFare);
                            Log.d("SQLHelper", "Record with id " + id + " and weight_fare " + weightFare + " already exists. Updated.");
                        } else {
                            ContentValues values = getContentValuesForInsert(tableName, jsonData);

                            long insertResult = db.insert(tableName, null, values);

                            if (insertResult != -1) {
                                Log.d("SQLHelper", "Record with id " + id + " and weight_fare " + weightFare + " inserted.");
                            } else {
                                Log.e("SQLHelper", "Error inserting record with id " + id + " and weight_fare " + weightFare);
                            }
                        }
                    }

                    db.close();

                } catch (JSONException e) {
                    Log.e("SQLHelper", "Error parsing JSON response: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SQLHelper", "Volley error: " + error.getMessage());
            }
        });

        Volley.newRequestQueue(getApplicationContext()).add(request);

    }
    private boolean isRecordExists(SQLiteDatabase db, String tableName, int id, double weightFare) {
        String query = "SELECT 1 FROM " + tableName + " WHERE id = " + id + " AND weight_fare = " + weightFare;
        Cursor cursor = db.rawQuery(query, null);

        boolean exists = cursor.getCount() > 0;

        if (exists) {
            Log.d("SQLHelper", "Record with id " + id + " and weight_fare " + weightFare + " already exists.");
        } else {
            Log.d("SQLHelper", "Record with id " + id + " and weight_fare " + weightFare + " does not exist.");
        }

        cursor.close();
        return exists;
    }

    private void updateWeightFare(SQLiteDatabase db, String tableName, int id, double weightFare) {
        ContentValues values = new ContentValues();
        values.put("weight_fare", weightFare);

        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(id)};

        db.update(tableName, values, whereClause, whereArgs);
    }

    private ContentValues getContentValuesForInsert(String tableName, JSONObject jsonData) throws JSONException {
        ContentValues values = new ContentValues();

        JSONArray columns = jsonData.getJSONArray("columns");
        for (int i = 0; i < columns.length(); i++) {
            String columnName = columns.getString(i);

            // Check the type of the value and insert it accordingly
            if (jsonData.get(columnName) instanceof String) {
                values.put(columnName, jsonData.getString(columnName));
            } else if (jsonData.get(columnName) instanceof Integer) {
                values.put(columnName, jsonData.getInt(columnName));
            } else if (jsonData.get(columnName) instanceof Double) {
                values.put(columnName, jsonData.getDouble(columnName));
            } else {
                // Handle other types as needed
                Log.e("SQLHelper", "Unsupported data type for column " + columnName);
            }
        }

        return values;
    }

}
