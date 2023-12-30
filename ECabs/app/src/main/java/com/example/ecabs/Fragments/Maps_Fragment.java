package com.example.ecabs.Fragments;

import static android.content.Context.MODE_PRIVATE;

import static com.example.ecabs.Activity.MainActivity.connection;
import static com.example.ecabs.Activity.MainActivity.fareDiscount;
import static com.example.ecabs.Activity.ProfileActivity.KEY_EMAIL;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecabs.Activity.MainActivity;
import com.example.ecabs.Activity.SettingActivity;
import com.example.ecabs.Dijkstra.AddNode;
import com.example.ecabs.Dijkstra.Algo;
import com.example.ecabs.Dijkstra.GetInitialAndFinalCoordinates;
import com.example.ecabs.Dijkstra.GraphToArray;
import com.example.ecabs.R;
import com.example.ecabs.Utils.ConnectionManager;
import com.example.ecabs.Utils.InfoWindow;
import com.example.ecabs.Utils.MapMarkerUtils;
import com.example.ecabs.Utils.MapUtils;
import com.example.ecabs.Utils.MyAdapter;
import com.example.ecabs.Utils.NetworkUtils;
import com.example.ecabs.Utils.SQLHelper;
import com.example.ecabs.Utils.TODAUtils;
import com.example.ecabs.Utils.TODAUtils_2;
import com.example.ecabs.Utils.TodaItem;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Maps_Fragment extends Fragment implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    public static GoogleMap mapAPI;
    private boolean clicked;
    
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ConnectionManager connectionManager;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public static final String SHARED_PREF_NAME= "MyPreferences";
    private static final String userLoc = "userLoc";
    private static final String userDes = "userDes";
    private static final String cost = "cost";
    FirebaseDatabase database;
    DatabaseReference reference;
    private String location;
    private String destination;
    private String Cost;
    public static Polyline currentPolyline;
    public String TODA;
    private int zoom = 13;
    Cursor cursor = null;
    public int __global_start_node;
    public int __global_end_node;
    public String __global_old_start_node;
    public String __global_old_end_node;
    public int __global_maxRow0;
    public int __global_maxRow1;
    private String[][] __global_graphArray;
    SQLHelper dbHelper;
    SQLiteDatabase db;
    double totalAmountOfFare;
    double totalDistance;
    String con;
    private BottomSheetBehavior bottomSheetBehavior;
    TextView infoTxt, clearTxtBtn, currentLocBtn, timeTxt, kmTxt, bestModes;
    TextView bestInMode;
    TextView totalFare;
    String modeCount;
    String getEmail;
    String getFareDiscountStatus;
    Boolean pageCount = false;
    private List<TodaItem> todaItemList;
    public MyAdapter adapter;

    boolean isDistance = false;
    int Id = 1;


    String destination1;
    String location1;

    int hour;
    int minutes;

    String stringValueOfTotalAmountFare ;
    String getFareDiscount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps_, container, false);
        preferences = getContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        editor = preferences.edit();
        todaItemList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minutes = calendar.get(Calendar.MINUTE);


        String currentDate = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());

       /* Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (hour == 15 && minutes == 57){
                    showLocationNotFoundErrorDialog();

                }
            }
        };*/
       /* handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (hour == 15 && minutes == 51){
                    showLocationNotFoundErrorDialog();
                }
            }
        }, 10);*/

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        stringValueOfTotalAmountFare = String.valueOf(totalAmountOfFare).replace(".", "");


        location = preferences.getString(userLoc, null);
        destination = preferences.getString(userDes, null);
        Cost = preferences.getString(cost, null);

        getFareDiscount = preferences.getString(fareDiscount, null);


        int white = ContextCompat.getColor(requireContext(), R.color.white);
        int blue = ContextCompat.getColor(requireContext(), R.color.blue);

        con = preferences.getString(connection, null);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        getEmail = preferences.getString(KEY_EMAIL, null);
        getFareDiscountStatus = preferences.getString(fareDiscount, null);

        LinearLayout bottomSheetContainer = view.findViewById(R.id.bottomSheetContainer);
        RecyclerView recyclerView = view.findViewById(R.id.listTodaContainer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        TextView discountFareTxt = view.findViewById(R.id.discountedFareTxt);

        if (getFareDiscountStatus != null){
            if (getFareDiscountStatus.equals("none")){
                discountFareTxt.setText("Regular Fare");
            } else if (getFareDiscountStatus.equals("discounted")) {
                discountFareTxt.setText("Discounted Fare");
            }

        }






        adapter = new MyAdapter(todaItemList);
        recyclerView.setAdapter(adapter);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer);
       bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback);


        View btn = view.findViewById(R.id.bottomBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                adapter.notifyDataSetChanged();
            }
        });

        if (location != null || destination != null){
            bottomSheetContainer.setVisibility(View.VISIBLE);
            bottomSheetBehavior.setPeekHeight(650);
            adapter.notifyDataSetChanged();

        }else {
            bottomSheetContainer.setVisibility(View.GONE);
        }
        requestLocationPermission();


        //Bago to pre
        //Eto kung Best in Fare ba or Time and Distane
        LinearLayout searchContainer = view.findViewById(R.id.searchContainer);
        searchContainer.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        LinearLayout container1 = view.findViewById(R.id.container1);
        LinearLayout container2 = view.findViewById(R.id.container2);
        LinearLayout container3 = view.findViewById(R.id.container3);
        LinearLayout container4 = view.findViewById(R.id.container4);
        AutoCompleteTextView locationTxtField = view.findViewById(R.id.locationTxtField);
        AutoCompleteTextView destinationTxtField = view.findViewById(R.id.destinationTxtField);
        Button settingBtn = view.findViewById(R.id.settingBtn);
        Button searchBtn = view.findViewById(R.id.searchBtn);
        Button distanceWiseBtn = view.findViewById(R.id.distanceWiseBtn);
        Button fareWiseBtn = view.findViewById(R.id.fareWiseBtn);
        Button fareDiscountBtn = view.findViewById(R.id.fareDiscountBtn);



        ImageView backBtn = view.findViewById(R.id.eastBack);
        CheckBox saveSearchBtn = view.findViewById(R.id.savedSearchCbox);
        infoTxt = view.findViewById(R.id.infoTxt);
        clearTxtBtn = view.findViewById(R.id.clearText);
        currentLocBtn = view.findViewById(R.id.currentLocBtn);
        timeTxt = view.findViewById(R.id.timeTxt);
        kmTxt = view.findViewById(R.id.kmTxt);
        bestModes = view.findViewById(R.id.bestModes);
        totalFare = view.findViewById(R.id.totalFare);

        fareDiscountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFareDiscount();
            }
        });



        String[] itemArray = getResources().getStringArray(R.array.LocationList);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireContext(), R.layout.custom_recent_item_layout, itemArray);
        destinationTxtField.setAdapter(arrayAdapter);
        locationTxtField.setAdapter(arrayAdapter);

        String text = currentLocBtn.getText().toString();
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new UnderlineSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        currentLocBtn.setText(spannableString);

        if (getEmail != null){
            saveSearchBtn.setEnabled(true);
            saveSearchBtn.setVisibility(View.VISIBLE);

            Query checkUserDatabase = reference.orderByChild("users");
            checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        String userIDFromDB = snapshot.child(getEmail).child("history_id").getValue(String.class);
                        if (!userIDFromDB.equals(1)){
                            Id = Integer.valueOf(userIDFromDB) + 1;

                        }else {

                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }else {
            saveSearchBtn.setEnabled(false);
            saveSearchBtn.setVisibility(View.GONE);
        }


        currentLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call getLocationNameAsync to fetch the location name
                getLocationNameAsync(new LocationCallback() {
                    @Override
                    public void onLocationNameReceived(String locationName) {
                        // Inside this callback, you can use the locationName
                        if (locationName != null) {
                            // Set the locationName in the EditText when touched
                            locationTxtField.setText(locationName);
                        }
                    }
                });
            }
        });


        clearTxtBtn.setOnClickListener(v -> {
            locationTxtField.setText(""); // Clear the text in editText1
            destinationTxtField.setText(""); // Clear the text in editText2
            Toast.makeText(getActivity(), "Text cleared", Toast.LENGTH_SHORT).show();
        });

        if (location != null){
            locationTxtField.setText(location);
            destinationTxtField.setText(destination);
        }else {

        }


        View.OnClickListener clickListener = v ->{
            if (v == distanceWiseBtn){
                distanceWiseBtn.setTextColor(white);
                distanceWiseBtn.setBackgroundResource(R.drawable.fragment_search_search_button);
                fareWiseBtn.setTextColor(blue);
                fareWiseBtn.setBackgroundResource(R.drawable.fragment_setting_button_box);
                isDistance = true;
                modeCount = "FAST";
            }
            if (v == fareWiseBtn){
                fareWiseBtn.setTextColor(white);
                fareWiseBtn.setBackgroundResource(R.drawable.fragment_search_search_button);
                distanceWiseBtn.setTextColor(blue);
                distanceWiseBtn.setBackgroundResource(R.drawable.fragment_setting_button_box);
                isDistance = false;
                modeCount = "LOW";
            }

        };
        distanceWiseBtn.setOnClickListener(clickListener);
        fareWiseBtn.setOnClickListener(clickListener);


        Drawable pindrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_pin);
        Drawable searchdrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_search);


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchBtnTxt = searchBtn.getText().toString();
                location1 = locationTxtField.getText().toString();
                destination1 = destinationTxtField.getText().toString();

                if (searchBtnTxt.equals("Next")){

                    if (!location1.isEmpty() && !destination1.isEmpty()){
                        //Hide the first page
                        container2.setVisibility(View.GONE);
                        container3.setVisibility(View.GONE);
                        infoTxt.setText("What do you prefer?");
                        searchBtn.setText("Search");
                        pageCount = true;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                container4.setVisibility(View.VISIBLE);
                            }
                        }, 100);
                    }else {
                        Toast.makeText(requireContext(), "All fields required!", Toast.LENGTH_SHORT).show();
                    }
                } else if (searchBtnTxt.equals("Search")) {
                    //proceed to search
                    if (modeCount != null){
                        container4.setVisibility(View.GONE);
                        container1.setVisibility(View.GONE);
                        searchBtn.setVisibility(View.GONE);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                container2.setVisibility(View.VISIBLE);
                                settingBtn.setVisibility(View.VISIBLE);
                                searchContainer.setBackgroundResource(0);
                                infoTxt.setText("Where would you like to go?");
                            }
                        }, 100);
                        if (!location1.isEmpty() && !destination1.isEmpty() && !modeCount.equals(null)){


                            if (NetworkUtils.isWifiConnected(requireContext().getApplicationContext())) {
                                editor.putString(userLoc, location1);
                                editor.putString(userDes, destination1);
                                editor.putString(cost, modeCount);
                                editor.commit();
                                Intent intent = new Intent(requireContext(), MainActivity.class);
                                intent.putExtra("MAPS", true);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                requireActivity().overridePendingTransition(0, 0);

                                String userID = String.valueOf(Id);
                                if (saveSearchBtn.isChecked()){
                                    if (getEmail != null){
                                        reference.child(getEmail).child("histories").child(userID).child("location").setValue(location1);
                                        reference.child(getEmail).child("histories").child(userID).child("destination").setValue(destination1);
                                        reference.child(getEmail).child("histories").child(userID).child("date").setValue(currentDate);
                                        reference.child(getEmail).child("history_id").setValue(userID);

                                    }

                                }

                            }else{
                                connectionManager = new ConnectionManager(requireContext(), editor);
                                connectionManager.lostConnectionDialog(requireActivity());
                                container3.setVisibility(View.GONE);
                                searchBtn.setText("Next");
                            }



                        }else {
                            Toast.makeText(requireContext(), "Error occured!", Toast.LENGTH_SHORT).show();
                        }


                    }else {
                        Toast.makeText(requireContext(), "Choose your preferred mode!", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        locationTxtField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus){

                    searchContainer.setBackgroundResource(R.drawable.search_container);
                    /*timedDisCon.setVisibility(View.GONE);*/
                    settingBtn.setVisibility(View.GONE);
                    settingBtn.setVisibility(View.GONE);
                    container1.setVisibility(View.VISIBLE);
                    bottomSheetBehavior.setPeekHeight(100);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            locationTxtField.setHint("Type your Location");
                            locationTxtField.setCompoundDrawablesWithIntrinsicBounds(pindrawable, null, null, null);
                            container3.setVisibility(View.VISIBLE);
                            searchBtn.setVisibility(View.VISIBLE);

                        }
                    }, 10);
                }
            }
        });



        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pageCount == false){
                    //first page
                    /*timedDisCon.setVisibility(View.GONE);*/
                    container1.setVisibility(View.GONE);
                    settingBtn.setVisibility(View.GONE);
                    locationTxtField.setHint("Search");
                    locationTxtField.setCompoundDrawablesWithIntrinsicBounds(searchdrawable, null, null, null);
                    container3.setVisibility(View.GONE);
                    searchBtn.setVisibility(View.GONE);
                    settingBtn.setVisibility(View.VISIBLE);

                    hideKeyboard(view);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            searchContainer.setBackgroundResource(0);
                            bottomSheetBehavior.setPeekHeight(650);
                        }
                    }, 200);

                }else {
                    //second page
                    container4.setVisibility(View.GONE);
                    searchBtn.setText("Next");
                    pageCount = false;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            container1.setVisibility(View.VISIBLE);
                            container2.setVisibility(View.VISIBLE);
                            container3.setVisibility(View.VISIBLE);
                            infoTxt.setText("Where would you like to go?");
                        }
                    }, 100);

                }
            }
        });
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), SettingActivity.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(0, 0);
            }
        });


        //Eto yung time na bago makarating sa destination
        /*time = view.findViewById(R.id.timeAndDistance);*/
        //Eto yung distance from location to destination
        /*distance = view.findViewById(R.id.distance);*/
        totalFare = view.findViewById(R.id.totalFare);
        // Dito mo lalagay yung total fare pri setText mo lang

        //Eto yung para lumabas yung details
        //Sa BottomSheetFragment mo maseset yung nakakalagay dun sa see details
      /*  Button seeDetailsBtn = view.findViewById(R.id.seeDetailsBtn);
        seeDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
            }
        });*/
        //hanggang dito lang

        return view;
    }
    private void setFareDiscount(){

            View alertCustomDialog = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_fare_discount, null);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());
            alertDialog.setView(alertCustomDialog);
            Button submitFareBtn = (Button) alertCustomDialog.findViewById(R.id.submitFareBtn);
            CheckBox fareDiscounted1 = (CheckBox) alertCustomDialog.findViewById(R.id.checkBox1);
            CheckBox none = (CheckBox) alertCustomDialog.findViewById(R.id.fareDNone);
            final AlertDialog dialog = alertDialog.create();

        int grey = ContextCompat.getColor(requireContext(), R.color.greyish);
        ColorStateList greyList = ColorStateList.valueOf(grey);
        int white = ContextCompat.getColor(requireContext(), R.color.white);
        ColorStateList whiteList = ColorStateList.valueOf(white);


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

                if (getFareDiscountStatus.equals("none")){
                    none.setEnabled(false);
                    none.setButtonTintList(greyList);
                    none.setTextColor(grey);
                } else if (getFareDiscountStatus.equals("discounted")) {
                    fareDiscounted1.setEnabled(false);
                    fareDiscounted1.setButtonTintList(greyList);
                    fareDiscounted1.setTextColor(grey);
                }



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
                        Intent intent = new Intent(requireContext(), MainActivity.class);
                        startActivity(intent);
                        requireActivity().overridePendingTransition(0, 0);
                    }else {
                        Toast.makeText(requireContext(), "Choose one for discount!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    }
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            // This method is called when the state of the BottomSheet changes
            // You can check the state to see if it's being dragged or settled
            // For example, newState == BottomSheetBehavior.STATE_DRAGGING

            switch (newState) {
                case BottomSheetBehavior.STATE_DRAGGING:
                    adapter.notifyDataSetChanged();
                    // The bottom sheet is being dragged
                    // You can perform actions when the bottom sheet is being swiped up
                    break;
                case BottomSheetBehavior.STATE_SETTLING:
                    adapter.notifyDataSetChanged();
                    // The bottom sheet is settling after being dragged
                    break;
                case BottomSheetBehavior.STATE_EXPANDED:
                    adapter.notifyDataSetChanged();
                    // The bottom sheet is fully expanded
                    break;
                case BottomSheetBehavior.STATE_COLLAPSED:
                    adapter.notifyDataSetChanged();
                    // The bottom sheet is collapsed
                    break;
                // Handle other states as needed
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            // This method is called when the bottom sheet is being slid
            // You can use the slideOffset if needed
            adapter.notifyDataSetChanged();
        }
    };
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

/*    private void showBottomSheet() {
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment(todaItemList);
        bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
    }*/
    public static String getLocationName(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String locationName = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                locationName = address.getAddressLine(0); // You can customize the address format as needed
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return locationName;
    }

    public void getLocationNameAsync(Maps_Fragment.LocationCallback callback) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Handle the case where permissions are not granted.
            // You might want to request permissions here if needed.
            callback.onLocationNameReceived(null);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        // Once you have the location, call getLocationName
                        String locationName = getLocationName(requireContext(), latitude, longitude);

                        // Use the callback to pass the locationName to the caller
                        callback.onLocationNameReceived(locationName);
                    }
                }
            }
        });
    }

    // Define a callback interface in your class:
    public interface LocationCallback {
        void onLocationNameReceived(String locationName);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapAPI = googleMap;
        MarkerOptions markerOptions = new MarkerOptions();

        try {
        //Search result here

        if (Cost != null){
            if (!location.isEmpty() && !destination.isEmpty()){
                if (Cost.equals("FAST")){
                    searchLocation(requireContext(), location);
                    searchLocation(requireContext(), destination);
                    isDistance = true;

                    try {
                        startingScript(getLatitude(requireContext(), location), getLongitude(requireContext(), location), getLatitude(requireContext(), destination), getLongitude(requireContext(), destination));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    int totalFareRound = (int) Math.round(totalAmountOfFare);

                    totalFare.setText("₱ " + String.valueOf(totalFareRound).replace(".", ""));
                    timeTxt.setText(estimatedTimeDuration(totalDistance) + " min");
                    kmTxt.setText("( " + String.valueOf(convertToKm(totalDistance)) + " km" + " )");
                    bestModes.setText("Fastest Route");

                    closedatabase();

                } else if (Cost.equals("LOW")) {
                    searchLocation(requireContext(), location);
                    searchLocation(requireContext(), destination);
                    isDistance= false;

                    try {
                        startingScript(getLatitude(requireContext(), location), getLongitude(requireContext(), location), getLatitude(requireContext(), destination), getLongitude(requireContext(), destination));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    closedatabase();
                    int totalFareRound = (int) Math.round(totalAmountOfFare);

                    totalFare.setText("₱ " + String.valueOf(totalFareRound).replace(".", ""));
                    timeTxt.setText(estimatedTimeDuration(totalDistance) + " min");
                    kmTxt.setText("( " + String.valueOf(convertToKm(totalDistance)) + " km" + " )");
                    bestModes.setText("Low-Cost Route");
                }
            }else {
               /* showLocationNotFoundErrorDialog();*/
            }
        }else {
           /* showLocationNotFoundErrorDialog();*/
        }

        if (getArguments() != null) {
            clicked = getArguments().getBoolean("JEEP", false);
            if (clicked) {
                try {
                    MapUtils.addCustomMarkerToMap(
                            mapAPI,
                            requireContext(),
                            R.drawable.jeep_pin_icon,
                            new LatLng(14.2797559245088, 121.12251837737215),
                            "JEEPNEY",
                            "Cabuyao Bayan"

                    );
                    MapUtils.addCustomMarkerToMap(
                            mapAPI,
                            requireContext(),
                            R.drawable.jeep_pin_icon,
                            new LatLng(14.228073059381586, 121.13908828787508),
                            "JEEPNEY",
                            "Banlic Cabuyao"

                    );

                    List<LatLng> JeepTodaLL = TODAUtils.getInstance().getJeepLatLngList();

                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(JeepTodaLL) // Add the list of LatLng coordinates
                            .width(15) // Set the width of the polyline in pixels
                            .color(ContextCompat.getColor(requireContext(), R.color.blue));

                    Polyline polyline = googleMap.addPolyline(polylineOptions);
                } catch (Exception e) {
                e.printStackTrace();
                // Handle the exception or log it to understand the problem better.
            }
            setInfoView();

            }

        }
        if (getArguments() != null) {
            clicked = getArguments().getBoolean("POSATODA", false);
            if (clicked) {
                try {
                    MapMarkerUtils.POSATODA(mapAPI, requireContext());
                    //Bago to pre, meron sa lahat ng TODA nito
                    List<LatLng> PosaTodaLL = TODAUtils_2.getInstance().getPosaLatLngList();

                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(PosaTodaLL) // Add the list of LatLng coordinates
                            .width(15) // Set the width of the polyline in pixels
                            .color(ContextCompat.getColor(requireContext(), R.color.red));

                    Polyline polyline = googleMap.addPolyline(polylineOptions);

                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle the exception or log it to understand the problem better.
                }

                setInfoView();

            }

        }
        if (getArguments() != null) {
            clicked = getArguments().getBoolean("BBTODA", false);
            if (clicked) {
                try {
                    MapMarkerUtils.BBTODA(mapAPI, requireContext());
                    List<LatLng> bbTodaLL = TODAUtils.getInstance().getBbtodaLL();

                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(bbTodaLL) // Add the list of LatLng coordinates
                            .width(15) // Set the width of the polyline in pixels
                            .color(ContextCompat.getColor(requireContext(), R.color.bbtoda));

                    Polyline polyline = googleMap.addPolyline(polylineOptions);

                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle the exception or log it to understand the problem better.
                }

                //setting the infoview

                TODA = "BBTODA";

                setInfoView();

            }

        }
        if (getArguments() != null) {
            clicked = getArguments().getBoolean("BTATODA", false);
            if (clicked) {
                try {
                    MapMarkerUtils.BTATODA(mapAPI, requireContext());
                    List<LatLng> btaTodaLL = TODAUtils.getInstance().getBtatodaLL();

                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(btaTodaLL) // Add the list of LatLng coordinates
                            .width(15) // Set the width of the polyline in pixels
                            .color(ContextCompat.getColor(requireContext(), R.color.btatoda));

                    Polyline polyline = googleMap.addPolyline(polylineOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle the exception or log it to understand the problem better.
                }


                //setting the infoview
                setInfoView();

            }

        }

        if (getArguments() != null) {
            clicked = getArguments().getBoolean("OSPOTODA", false);
            if (clicked) {
                try {
                    MapMarkerUtils.OSPOTODA(mapAPI, requireContext());
                    List<LatLng> OspoTodaLL = TODAUtils_2.getInstance().getOspotodaLL();
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(OspoTodaLL) // Add the list of LatLng coordinates
                            .width(15) // Set the width of the polyline in pixels
                            .color(ContextCompat.getColor(requireContext(), R.color.ospotoda));
                    Polyline polyline = googleMap.addPolyline(polylineOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle the exception or log it to understand the problem better.
                }
                //setting the infoview
                setInfoView();
            }
        }
        if (getArguments() != null) {
            clicked = getArguments().getBoolean("CMS", false);
            if (clicked) {
                try {
                    MapMarkerUtils.CMSTODA(mapAPI, requireContext());
                    List<LatLng> cmsatodaLL = TODAUtils_2.getInstance().getCmsatodaLL();

                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(cmsatodaLL) // Add the list of LatLng coordinates
                            .width(15) // Set the width of the polyline in pixels
                            .color(ContextCompat.getColor(requireContext(), R.color.cmsatoda));

                    Polyline polyline = googleMap.addPolyline(polylineOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle the exception or log it to understand the problem better.
                }
                //setting the infoview
                setInfoView();
            }

        }
        if (getArguments() != null) {
            clicked = getArguments().getBoolean("MACA", false);
            if (clicked) {
                try {
                    MapMarkerUtils.MACATODA(mapAPI, requireContext());
                    List<LatLng> MAcatodaLL = TODAUtils.getInstance().getMacaLatLngList();
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(MAcatodaLL) // Add the list of LatLng coordinates
                            .width(15) // Set the width of the polyline in pixels
                            .color(ContextCompat.getColor(requireContext(), R.color.macatoda));

                    Polyline polyline = googleMap.addPolyline(polylineOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle the exception or log it to understand the problem better.
                }
                //setting the infoview
                setInfoView();
            }
        }
        if (getArguments() != null) {
            clicked = getArguments().getBoolean("BMBG", false);
            if (clicked) {
                try {
                    MapMarkerUtils.BMBGTODA(mapAPI, requireContext());
                    List<LatLng> bmbgTodaLL = TODAUtils.getInstance().getBmbgtodaLL();

                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(bmbgTodaLL) // Add the list of LatLng coordinates
                            .width(15) // Set the width of the polyline in pixels
                            .color(ContextCompat.getColor(requireContext(), R.color.bmbgtoda));

                    Polyline polyline = googleMap.addPolyline(polylineOptions);


                }catch (Exception e) {
                    e.printStackTrace();
                    // Handle the exception or log it to understand the problem better.
                }


                //setting the infoview
                setInfoView();
            }

        }
        if (getArguments() != null) {
            clicked = getArguments().getBoolean("CSV", false);
            if (clicked) {
                try {
                    MapMarkerUtils.CSVTODA(mapAPI, requireContext());
                    List<LatLng> csvTodaLL = TODAUtils_2.getInstance().getCsvtodaLL();
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(csvTodaLL) // Add the list of LatLng coordinates
                            .width(15) // Set the width of the polyline in pixels
                            .color(ContextCompat.getColor(requireContext(), R.color.csvtoda));
                    Polyline polyline = googleMap.addPolyline(polylineOptions);
                }catch (Exception e) {
                    e.printStackTrace();
                    // Handle the exception or log it to understand the problem better.
                }
                //setting the infoview
                setInfoView();
            }
        }
        if (getArguments() != null) {
            clicked = getArguments().getBoolean("SJV7", false);
            if (clicked) {
                try {
                    MapMarkerUtils.SJV7TODA(mapAPI, requireContext());
                    List<LatLng> sjv7TodaLL = TODAUtils.getInstance().getSjv7todaLL();

                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(sjv7TodaLL) // Add the list of LatLng coordinates
                            .width(15) // Set the width of the polyline in pixels
                            .color(ContextCompat.getColor(requireContext(), R.color.sjv7toda));

                    Polyline polyline = googleMap.addPolyline(polylineOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //setting the infoview
                setInfoView();
            }

        }
        if (getArguments() != null) {
            clicked = getArguments().getBoolean("SJB", false);
            if (clicked) {
                try {
                    MapMarkerUtils.SJBTODA(mapAPI, requireContext());
                    List<LatLng> sjbTodaLL = TODAUtils.getInstance().getSjbtodaLL();

                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(sjbTodaLL) // Add the list of LatLng coordinates
                            .width(15) // Set the width of the polyline in pixels
                            .color(ContextCompat.getColor(requireContext(), R.color.sjbtoda));

                    Polyline polyline = googleMap.addPolyline(polylineOptions);
                }catch (Exception e) {
                    e.printStackTrace();
                }


                //setting the infoview
                setInfoView();
            }

        }
        if (getArguments() != null) {
            clicked = getArguments().getBoolean("SICALA", false);
            if (clicked) {
                try {
                    MapMarkerUtils.SICALATODA(mapAPI, requireContext());
                    List<LatLng> sicalaTodaLL = TODAUtils.getInstance().getSicalatodaLL();

                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(sicalaTodaLL) // Add the list of LatLng coordinates
                            .width(15) // Set the width of the polyline in pixels
                            .color(ContextCompat.getColor(requireContext(), R.color.sicalatoda));

                    Polyline polyline = googleMap.addPolyline(polylineOptions);

                }catch (Exception e) {
                    e.printStackTrace();
                }


                //setting the infoview
                setInfoView();
            }

        }
        if (getArguments() != null) {
            clicked = getArguments().getBoolean("PUD", false);
            if (clicked) {
                try {
                    MapMarkerUtils.PUDTODA(mapAPI, requireContext());
                    List<LatLng> pudTodaLL = TODAUtils.getInstance().getPudtodaLL();

                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(pudTodaLL) // Add the list of LatLng coordinates
                            .width(15) // Set the width of the polyline in pixels
                            .color(ContextCompat.getColor(requireContext(), R.color.pudtoda));

                    Polyline polyline = googleMap.addPolyline(polylineOptions);

                }catch (Exception e) {
                    e.printStackTrace();
                }


                //setting the infoview
                setInfoView();
            }

        }
        if (getArguments() != null) {
            clicked = getArguments().getBoolean("HV", false);
            if (clicked) {
                try {
                    MapMarkerUtils.HVTODA(mapAPI, requireContext());
                    List<LatLng> hvtodaLL = TODAUtils.getInstance().getHvLatLngList();

                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(hvtodaLL) // Add the list of LatLng coordinates
                            .width(15) // Set the width of the polyline in pixels
                            .color(ContextCompat.getColor(requireContext(), R.color.hvtoda));

                    Polyline polyline = googleMap.addPolyline(polylineOptions);
                }catch (Exception e) {
                    e.printStackTrace();
                }


                //setting the infoview
                setInfoView();
            }

        }
        if (getArguments() != null) {
            clicked = getArguments().getBoolean("DOV", false);
            if (clicked) {
                try {
                    MapMarkerUtils.DOVTODA(mapAPI, requireContext());
                    List<LatLng> dovTodaLL = TODAUtils.getInstance().getDovtodaLL();

                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(dovTodaLL) // Add the list of LatLng coordinates
                            .width(15) // Set the width of the polyline in pixels
                            .color(ContextCompat.getColor(requireContext(), R.color.dovtoda));

                    Polyline polyline = googleMap.addPolyline(polylineOptions);
                }catch (Exception e) {
                    e.printStackTrace();
                }


                //setting the infoview
                setInfoView();
            }

        }
        if (getArguments() != null) {
            clicked = getArguments().getBoolean("KA", false);
            if (clicked) {
                try {
                    MapMarkerUtils.KATODA(mapAPI, requireContext());

                    List<LatLng> katodaLL = TODAUtils_2.getInstance().getKatodaLL();

                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(katodaLL) // Add the list of LatLng coordinates
                            .width(15) // Set the width of the polyline in pixels
                            .color(ContextCompat.getColor(requireContext(), R.color.katoda));

                    Polyline polyline = googleMap.addPolyline(polylineOptions);

                }catch (Exception e) {
                    e.printStackTrace();
                }


                //setting the infoview
                setInfoView();
            }

        }
        if (getArguments() != null) {
            clicked = getArguments().getBoolean("MCCH", false);
            if (clicked) {
                try {
                    MapMarkerUtils.MCCHTODA(mapAPI, requireContext());
                    List<LatLng> mcchTodaLL = TODAUtils.getInstance().getMcchtodaLL();

                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(mcchTodaLL) // Add the list of LatLng coordinates
                            .width(15) // Set the width of the polyline in pixels
                            .color(ContextCompat.getColor(requireContext(), R.color.mcchtoda));

                    Polyline polyline = googleMap.addPolyline(polylineOptions);
                }catch (Exception e) {
                    e.printStackTrace();
                }


                //setting the infoview
                setInfoView();
            }

        }
        if (getArguments() != null) {
            clicked = getArguments().getBoolean("BO", false);
            if (clicked) {
                try {
                    MapMarkerUtils.BOTODA(mapAPI, requireContext());
                    List<LatLng> boTodaLL = TODAUtils.getInstance().getBotodaLL();

                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(boTodaLL) // Add the list of LatLng coordinates
                            .width(15) // Set the width of the polyline in pixels
                            .color(ContextCompat.getColor(requireContext(), R.color.botoda));

                    Polyline polyline = googleMap.addPolyline(polylineOptions);
                }catch (Exception e) {
                    e.printStackTrace();
                }


                //setting the infoview
                setInfoView();
            }

        }
        if (getArguments() != null) {
            clicked = getArguments().getBoolean("MACOPA", false);
            if (clicked) {
                try {
                    MapMarkerUtils.MACOPASTR(mapAPI, requireContext());
                    List<LatLng> macopaTodaLL = TODAUtils.getInstance().getMacopastrtodaLL();

                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(macopaTodaLL) // Add the list of LatLng coordinates
                            .width(15) // Set the width of the polyline in pixels
                            .color(ContextCompat.getColor(requireContext(), R.color.macopastr));

                    Polyline polyline = googleMap.addPolyline(polylineOptions);
                }catch (Exception e) {
                    e.printStackTrace();
                }


                //setting the infoview
                setInfoView();
            }

        }
        if (getArguments() != null) {
            clicked = getArguments().getBoolean("LNS", false);
            if (clicked) {
                try {
                    MapMarkerUtils.LNSTODA(mapAPI, requireContext());
                    List<LatLng> lnsTodaLL = TODAUtils.getInstance().getLnsLatLngList();

                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(lnsTodaLL) // Add the list of LatLng coordinates
                            .width(15) // Set the width of the polyline in pixels
                            .color(ContextCompat.getColor(requireContext(), R.color.lnstoda));

                    Polyline polyline = googleMap.addPolyline(polylineOptions);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                //setting the infoview
                setInfoView();
            }

        }
        } catch (Exception e) {
            // Handle exceptions here
            e.printStackTrace();
          Toast.makeText(requireActivity(), "Error", Toast.LENGTH_SHORT);
        }
    }
    public void closedatabase() {
        // Close the database after all operations
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Close the database
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    public void startingScript(double latUser, double lngUser, double lat_endposition, double lng_endposition) throws JSONException {

        try {
            // delete temporary record DB
            deleteTemporaryRecord();

            // reset google map
            mapAPI.clear();

            // convert graph from DB to an Array; graph[][]
            GraphToArray DBGraph = new GraphToArray();
            __global_graphArray = DBGraph.convertToArray(requireContext()); // return graph[][] Array

            // get the maximum row in the temporary DB
            maxRowDB();

            // GET THE STARTING COORDINATE AROUND THE NODE
            // the starting coordinate is then converted to the starting node
            // return __global_simpul_awal, __global_graphArray[][]
            // ==========================================
            GetInitialAndFinalCoordinates start_coordinate_route = new GetInitialAndFinalCoordinates();
            getStartEndNodesRoute(start_coordinate_route, latUser, lngUser, "start");

            // GET THE ENDING COORDINATE AROUND THE NODE
            // the ending coordinate is then converted to the ending node
            // return __global_simpul_akhir, __global_graphArray[][]
            // ==========================================
            GetInitialAndFinalCoordinates destination_coordinate_route = new GetInitialAndFinalCoordinates();
            getStartEndNodesRoute(destination_coordinate_route, lat_endposition, lng_endposition, "end");

            // DIJKSTRA'S ALGORITHM
            // ==========================================
            Algo algo = new Algo();
            algo.shortestPath(requireContext(), __global_start_node, __global_end_node, isDistance);

            // no result for Dijkstra's algorithm
            if (algo.status == "die") {
                showLocationNotFoundErrorDialog();
            } else {
                // return the shortest path; example 1->5->6->7
                String[] exp = algo.shortestPath1.split("->");

                // DRAW PUBLIC TRANSPORTATION ROUTE
                // =========================================
                drawRoute(algo.shortestPath1, exp);
                for (int i = 0; i < exp.length; i++){
                    System.out.println(exp[i]);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            // Check if the exception is an ANR issue
            if (isANRIssue(e)) {
                // Show the location not found dialog or handle the ANR issue
                showLocationNotFoundErrorDialog();
            }
        }
    }
    public void drawRoute(String algo, String[] exp) throws JSONException {

        int start = 0;

        // DRAW THE ROUTE
        db = dbHelper.getReadableDatabase();
        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);

        try {
            for (int i = 0; i < exp.length - 1; i++) {

                ArrayList<LatLng> lat_lng = new ArrayList<LatLng>();

                cursor = db.rawQuery("SELECT route, starting FROM graph where starting =" + exp[start] + " and ending =" + exp[(++start)], null);
                cursor.moveToFirst();

                // get Lat, Lng coordinates from the coordinate field (3)
                String json = cursor.getString(0).toString();

                // System.out.println(json);

                // get JSON
                JSONObject jObject = new JSONObject(json);
                JSONArray jArrCoordinates = jObject.getJSONArray("coordinates");

                // get coordinate JSON
                for (int w = 0; w < jArrCoordinates.length(); w++) {

                    JSONArray latlngs = jArrCoordinates.getJSONArray(w);
                    Double lats = latlngs.getDouble(0);
                    Double lngs = latlngs.getDouble(1);

                    lat_lng.add(new LatLng(lats, lngs));
                }

                PolylineOptions normalRoute = new PolylineOptions();
                normalRoute.addAll(lat_lng)
                        .width(13);

                // Check if exp is in the range of 17 to 18 or 18 to 17 PULO
                if ((exp[start].equals("17") && exp[start + 1].equals("18")) || (exp[start].equals("18") && exp[start + 1].equals("17") || (exp[start].equals("68") && exp[start + 1].equals("17") || (exp[start].equals("17") && exp[start + 1].equals("68") || exp[start].equals("68") )))) {
                    // Check if the current time is between 5:30 AM and 8:00 AM
                    if ((currentHour == 5 && currentMinute >= 30) || (currentHour > 5 && currentHour < 8) || (currentHour == 8 && currentMinute == 0)) {
                        // Set the color to red
                        normalRoute.color(Color.YELLOW);
                    } else {
                        normalRoute.color(Color.parseColor("#69a1ff"));
                    }
                    //Banay-Banay to Sala
                } else if ((exp[start].equals("69") && exp[start + 1].equals("70")) || (exp[start].equals("70") && exp[start + 1].equals("69") || (exp[start].equals("70") && exp[start + 1].equals("71")
                        || (exp[start].equals("71") && exp[start + 1].equals("70") || (exp[start].equals("71") && exp[start + 1].equals("72") || (exp[start].equals("72") && exp[start + 1].equals("71"))))))) {
                    if ((currentHour == 6 && currentMinute >= 0) || (currentHour > 6 && currentHour < 8) || (currentHour == 8 && currentMinute == 0)) {
                        // Set the color to red
                        normalRoute.color(Color.RED);
                    }
                    else if ((currentHour == 17 && currentMinute >= 0) || (currentHour > 17 && currentHour < 20) || (currentHour == 20 && currentMinute == 0)) {
                        //orange
                        normalRoute.color(Color.parseColor("#FFA500"));
                    }
                    else {
                        normalRoute.color(Color.parseColor("#69a1ff"));
                    }
                    //San Isidro
                } else if ((exp[start].equals("67") && exp[start + 1].equals("21") || (exp[start].equals("21") && exp[start + 1].equals("67") || (exp[start].equals("67") && exp[start + 1].equals("23")
                        || (exp[start].equals("23") && exp[start + 1].equals("67") || (exp[start].equals("67") && exp[start + 1].equals("66") || (exp[start].equals("66") && exp[start + 1].equals("67")
                        || (exp[start].equals("67") && exp[start + 1].equals("68")|| (exp[start].equals("68") && exp[start + 1].equals("67") || (exp[start].equals("67"))))))))))) {
                    if ((currentHour == 6 && currentMinute >= 0) || (currentHour > 6 && currentHour < 8) || (currentHour == 8 && currentMinute == 0)) {
                        //orange
                        normalRoute.color(Color.parseColor("#FFA500"));
                    }
                    else if ((currentHour == 4 && currentMinute >= 0) || (currentHour > 4 && currentHour < 8) || (currentHour == 8 && currentMinute == 0)) {
                        normalRoute.color(Color.RED);
                    }
                    else {
                        normalRoute.color(Color.parseColor("#69a1ff"));
                    }

                } else if ((exp[start].equals("66") && exp[start + 1].equals("28") || (exp[start].equals("28") && exp[start + 1].equals("66") || (exp[start].equals("28") && exp[start + 1].equals("30") || (exp[start].equals("30") && exp[start + 1].equals("28") || (exp[start].equals("66") || (exp[start].equals("30") || (exp[start].equals("28"))))))))) {
                    if ((currentHour == 18 && currentMinute >= 0) || (currentHour > 18 && currentHour < 20) || (currentHour == 20 && currentMinute == 0)) {
                        normalRoute.color(Color.RED);
                    }
                    else if ((currentHour == 6 && currentMinute >= 0) || (currentHour > 6 && currentHour < 8) || (currentHour == 8 && currentMinute == 0)) {
                        normalRoute.color(Color.RED);
                    }
                    else {
                        normalRoute.color(Color.parseColor("#69a1ff"));
                    }
                } else {
                    // Set the default color
                    normalRoute.color(Color.parseColor("#69a1ff"));
                }

                // Add the polyline to the map
                mapAPI.addPolyline(normalRoute);
            }

            // CREATE MARKERS FOR YOUR POSITION AND DESTINATION POSITION
            // ======================
            // your position

            LatLng pos = new LatLng(getLatitude(requireContext(), location), getLongitude(requireContext(), location));
            mapAPI.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(location)
                    .snippet("Your position")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            LatLng endx = new LatLng(getLatitude(requireContext(), destination), getLongitude(requireContext(), destination));

            // destination position
            mapAPI.addMarker(new MarkerOptions()
                    .position(endx)
                    .title(destination)
                    .snippet("Destination position")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            // DETERMINE THE TYPE OF PUBLIC TRANSPORTATION PASSING THROUGH THE ROUTE
            // ==========================================================
            // for example, exp[] = 1->5->6->7
            int m = 0;

            String[] start1 = __global_old_start_node.split("-"); // for example, 4-5
            String[] end = __global_old_end_node.split("-"); // for example, 8-7

            int change_a = 0;
            int change_b = 0;
            int dijkstra_start_node = Integer.parseInt(exp[0]); // Default value if parsing fails

            String mergedAllNodes = "";
            Map<String, ArrayList> publicTransportList = new HashMap<String, ArrayList>();
            ArrayList<Integer> transportNodesList = new ArrayList<Integer>();
            ArrayList<Integer> starting = new ArrayList<Integer>();
            ArrayList<Integer> ending = new ArrayList<Integer>();

            // find the old nodes before coordinates are split
            // for example, 4-5 is split into 4-6-5, so the old starting node = 5, old ending node = 4
            for (int e = 0; e < (exp.length - 1); e++) {

                if (e == 0) { // starting

                    // executed if the algo result has only 2 nodes, example: 4->5
                    if (exp.length == 2 /* 2 nodes (4-5)*/) {

                        // there are new nodes at the beginning (10) and at the end (11), example 10->11
                        if (exp[0].equals(String.valueOf(__global_maxRow0)) && exp[1].equals(String.valueOf(__global_maxRow1))) {

                            if (String.valueOf(__global_maxRow0).equals(end[0])) {
                                change_b = Integer.parseInt(end[1]);
                            } else {
                                change_b = Integer.parseInt(end[0]);
                            }

                            if (String.valueOf(change_b).equals(start1[0])) {
                                change_a = Integer.parseInt(start1[1]);
                            } else {
                                change_a = Integer.parseInt(start1[0]);
                            }
                        } else {
                            // there are new nodes at the beginning (10), example 10->5
                            // then find the old starting node
                            if (exp[0].equals(String.valueOf(__global_maxRow0))) {

                                if (exp[1].equals(start1[1])) {
                                    change_a = Integer.parseInt(start1[0]);
                                } else {
                                    change_a = Integer.parseInt(start1[1]);
                                }
                                change_b = Integer.parseInt(exp[1]);
                            }
                            // there are new nodes at the end (10), example 5->10
                            // then find the old ending node
                            else if (exp[1].equals(String.valueOf(__global_maxRow0))) {

                                if (exp[0].equals(end[0])) {
                                    change_b = Integer.parseInt(end[1]);
                                } else {
                                    change_b = Integer.parseInt(end[0]);
                                }
                                change_a = Integer.parseInt(exp[0]);
                            }
                            // no addition of nodes at all
                            else {
                                change_a = Integer.parseInt(exp[0]);
                                change_b = Integer.parseInt(exp[1]);
                            }
                        }
                    }
                    // algo result has more than 2 nodes: 4->5->8->7-> etc ..
                    else {
                        if (exp[1].equals(start1[1])) { // 5 == 5
                            change_a = Integer.parseInt(start1[0]); // result 4
                        } else {
                            change_a = Integer.parseInt(start1[1]); // result 5
                        }

                        change_b = Integer.parseInt(exp[++m]);
                    }
                } else if (e == (exp.length - 2)) { // ending

                    if (exp[(exp.length - 2)].equals(end[1])) { // 7 == 7
                        change_b = Integer.parseInt(end[0]); // result 8
                    } else {
                        change_b = Integer.parseInt(end[1]); // result 7
                    }

                    change_a = Integer.parseInt(exp[m]);

                } else { // middle nodes
                    change_a = Integer.parseInt(exp[m]);
                    change_b = Integer.parseInt(exp[++m]);
                }

                mergedAllNodes += "," + change_a + "-" + change_b + ","; // ,1-5,
                String mergedNodes = "," + change_a + "-" + change_b + ","; // ,1-5,

                starting.add(change_a);
                ending.add(change_b);

                cursor = db.rawQuery("SELECT no_route FROM public_transit where id = '" + change_a + "'", null);
                cursor.moveToFirst();

                ArrayList<String> transportList = new ArrayList<String>();

                for (int ae = 0; ae < cursor.getCount(); ae++) {
                    cursor.moveToPosition(ae);
                    transportList.add(cursor.getString(0).toString());
                }

                publicTransportList.put("transport" + e, transportList);

                // add transport nodes
                transportNodesList.add(Integer.parseInt(exp[e]));

            }

            String replace_route = mergedAllNodes.replace(",,", ",");

            try (Cursor cursor1 = db.rawQuery("SELECT * FROM public_transit where string like '%" + replace_route + "%'", null)) {
                if (cursor1 != null && cursor1.moveToFirst()) {
                    if (cursor1.getCount() > 0) {
                        String transport = cursor1.getString(1);

                        // Get coordinates
                        try (Cursor coordinatesCursor = db.rawQuery("SELECT route, weight_distance, weight_fare FROM graph where starting = '" + dijkstra_start_node + "'", null)) {
                            if (coordinatesCursor != null && coordinatesCursor.moveToFirst()) {
                                String json_coordinates = coordinatesCursor.getString(0);

                                // Manipulate JSON
                                try {
                                    JSONObject jObject = new JSONObject(json_coordinates);
                                    JSONArray jArrCoordinates = jObject.getJSONArray("coordinates");
                                    JSONArray latlngs = jArrCoordinates.getJSONArray(0);

                                    // First coordinates
                                    Double lats = latlngs.getDouble(0);
                                    Double lngs = latlngs.getDouble(1);

                                    // Add marker to the map
                                    MapUtils.addCustomMarkerToMap(
                                            mapAPI,
                                            requireContext(),
                                            getMarkerIcon(String.valueOf(starting.get(0))),
                                            new LatLng(lats, lngs),
                                            getTodaName(String.valueOf(starting.get(0))),
                                            transport.replace("[", "").replace("]", "")
                                    );
                                    TodaItem item = new TodaItem(getTodaName(String.valueOf(starting.get(0))), transport.replace("[", "").replace("]", ""), coordinatesCursor.getDouble(2));
                                    todaItemList.add(item);

                                    return;
                                } catch (JSONException e) {
                                    e.printStackTrace(); // Handle JSON parsing exception
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace(); // Handle cursor exception for coordinates
                        }
                    } else {
                        // Handle the case where there are no rows in the cursor
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(); // Handle cursor exception for public transit
            }

            // there are 2 or more public transports passing through the route from start to end
            int transportCount = 0;
            int indexOrder = 0;
            int transportNodesIndex = 1;
            int transportListLength = publicTransportList.size();
            Map<String, ArrayList> fixedTransport = new HashMap<String, ArrayList>();

            for (int en = 0; en < transportListLength; en++) {

                // temporary before retainAll()
                ArrayList<String> temps = new ArrayList<String>();
                for (int u = 0; u < publicTransportList.get("transport0").size(); u++) {
                    temps.add(publicTransportList.get("transport0").get(u).toString());
                }

                if (en > 0) {
                    ArrayList currentList1 = publicTransportList.get("transport0");
                    ArrayList nextList1 = publicTransportList.get("transport" + en);

                    // intersection
                    currentList1.retainAll(nextList1);

                    if (currentList1.size() > 0) {

                        transportNodesList.remove(transportNodesIndex);
                        --transportNodesIndex;

                        publicTransportList.remove("transport" + en);

                        if (en == (transportListLength - 1)) {

                            ArrayList<String> tempInside = new ArrayList<String>();
                            for (int es = 0; es < currentList1.size(); es++) {
                                tempInside.add(currentList1.get(es).toString());
                            }

                            fixedTransport.put("transportFix" + indexOrder, tempInside);
                            ++indexOrder;
                        }
                    } else if (currentList1.size() == 0) {

                        fixedTransport.put("transportFix" + indexOrder, temps);

                        ArrayList<String> tempInside = new ArrayList<String>();
                        for (int es = 0; es < nextList1.size(); es++) {
                            tempInside.add(nextList1.get(es).toString());
                        }

                        publicTransportList.get("transport0").clear();
                        publicTransportList.put("transport0", tempInside);

                        publicTransportList.remove("transport" + en);

                        ++indexOrder;

                        if (en == (transportListLength - 1)) {

                            ArrayList<String> tempInside2 = new ArrayList<String>();
                            for (int es = 0; es < nextList1.size(); es++) {
                                tempInside2.add(nextList1.get(es).toString());
                            }

                            fixedTransport.put("transportFix" + indexOrder, tempInside2);
                            ++indexOrder;
                        }
                    }

                    ++transportNodesIndex;
                }
            }
            System.out.println("starting :" + starting);
            List<Integer> visibleNodeMarker = new ArrayList<>();
            List<Integer> visibleNodeFare = new ArrayList<>();
            boolean firstIdInRangeProcessed = false;
            for (int i = 0; i < starting.size(); i++) {
                Cursor coordinatesCursor = db.rawQuery("SELECT route, weight_distance, weight_fare FROM graph where starting = '" + starting.get(i) + "' and ending = '" + ending.get(i) + "'", null);
                coordinatesCursor.moveToPosition(0);
                totalDistance += coordinatesCursor.getDouble(1);

                int idNum = starting.get(i);
                if (idNum >= 66 && idNum <= 80 && !firstIdInRangeProcessed) {
                    visibleNodeMarker.add(idNum);
                    visibleNodeFare.add(idNum);
                    firstIdInRangeProcessed = true;
                } else if (firstIdInRangeProcessed && idNum >= 66 && idNum <= 80) {
                    visibleNodeMarker.add(null);
                    visibleNodeFare.add(null);
                } else if (idNum < 66 || idNum > 80) {
                    visibleNodeMarker.add(idNum);
                    visibleNodeFare.add(idNum);
                }
            }
            System.out.println(visibleNodeMarker);

            //visibleNodeFare.set(visibleNodeFare.size() - 1, null);
            System.out.println(visibleNodeFare);

            for (int r = 0; r < visibleNodeMarker.size(); r++) {
                Cursor transitCursorMarker = db.rawQuery("SELECT no_route FROM public_transit where id = '" + visibleNodeMarker.get(r) + "'", null);
                Cursor transitCursorFare = db.rawQuery("SELECT no_route FROM public_transit where id = '" + visibleNodeFare.get(r) + "'", null);

                if (transitCursorMarker.moveToFirst() && transitCursorFare.moveToFirst()) {
                    // Check if the cursor has at least one row
                    // Assuming no_route is a String column
                    String noRouteMarker = transitCursorMarker.getString(0);
                    String noRouteFare = transitCursorFare.getString(0);
                    // get coordinates of transport nodes
                    Cursor coordinatesCursorMarker = db.rawQuery("SELECT route, weight_fare FROM graph where starting = '" + visibleNodeMarker.get(r) + "' and ending = '" + ending.get(r) + "'", null);
                    Cursor coordinatesCursorFare = db.rawQuery("SELECT route, weight_fare FROM graph where starting = '" + visibleNodeFare.get(r) + "' and ending = '" + ending.get(r) + "'", null);

                    if (coordinatesCursorMarker.moveToFirst() && coordinatesCursorFare.moveToFirst()) {
                        // Check if the cursor has at least one row
                        // Assuming route is a String column
                        String json = coordinatesCursorMarker.getString(0);

                        // get JSON
                        JSONObject jObject = new JSONObject(json);
                        JSONArray jArrCoordinates = jObject.getJSONArray("coordinates");

                        // Check if the JSON array is not empty
                        if (jArrCoordinates.length() > 0) {
                            // get first coordinate from JSON
                            JSONArray latlngs = jArrCoordinates.getJSONArray(0);
                            Double lats = latlngs.getDouble(0);
                            Double lngs = latlngs.getDouble(1);

                            LatLng transportNode = new LatLng(lats, lngs);
                            MapUtils.addCustomMarkerToMap(
                                    mapAPI,
                                    requireContext(),
                                    getMarkerIcon(String.valueOf(visibleNodeMarker.get(r))),
                                    transportNode,
                                    getTodaName(String.valueOf(visibleNodeMarker.get(r))),
                                    noRouteMarker
                            );

                            if(coordinatesCursorFare.getDouble(1) != 0.0) {
                                TodaItem item = new TodaItem(getTodaName(String.valueOf(visibleNodeFare.get(r))), noRouteFare, discountFare(coordinatesCursorFare.getDouble(1)));
                                todaItemList.add(item);
                            }
                            totalAmountOfFare += discountFare(coordinatesCursorFare.getDouble(1));
                        } else {
                            // Handle the case when the coordinates JSON array is empty
                            Log.w("Empty Coordinates", "Coordinates JSON array is empty.");

                        }
                    } else {
                        // Handle the case when the coordinatesCursor is empty
                        Log.w("Empty Cursor", "Coordinates cursor is empty.");
                    }
                    //coordinatesCursor.close(); // Close the coordinatesCursor when you're done with it
                } else {
                    // Handle the case when the transitCursor is empty
                    Log.w("Empty Cursor", "Transit cursor is empty.");
                }
                //transitCursor.close(); // Close the transitCursor when you're done with it
            }
            setInfoView();
        }catch (Exception e) {
            e.printStackTrace();
            showLocationNotFoundErrorDialog();
            // Check if the exception is an ANR issue
            if (isANRIssue(e)) {
                // Show the location not found dialog or handle the ANR issue
                showLocationNotFoundErrorDialog();
            }
        }finally {
            // Close the Cursor in a finally block to ensure it gets closed
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }
    public void getStartEndNodesRoute(GetInitialAndFinalCoordinates objects, double latx, double lngx, String statusObject) throws JSONException{

        // return JSON index of coordinate position, nodes0, nodes1
        JSONObject jStart = objects.GetNodes(latx, lngx, requireContext());

        // JSON index
        String status = jStart.getString("status");
        int node_start0 = jStart.getInt("node_start0");
        int node_start1 = jStart.getInt("node_start1");
        int coordinate_json_index = jStart.getInt("index_coordinate_json");


        int fixed_start_node = 0;

        // if the coordinates are right above the node position
        // then there is no need to add new nodes
        if(status.equals("no_path")){

            // determine the starting or ending node closest to the user's position
            if(coordinate_json_index == 0){ // starting
                fixed_start_node = node_start0;
            }else{ // ending
                fixed_start_node = node_start1;
            }

            if(statusObject == "start"){

                // return
                __global_old_start_node = node_start0 + "-" + node_start1;
                __global_start_node = fixed_start_node; // for example, 0
            }else{

                // return
                __global_old_end_node = node_start0 + "-" + node_start1;
                __global_end_node = fixed_start_node; // for example, 0
            }

        }
        // if the coordinates are between node 5 and node 4 or node 4 and node 5
        // then new nodes need to be added
        else if(status.equals("double_path")){

            // return
            if(statusObject.equals("start")){

                // find nodes (5,4) and (4-5) in Add_node.java
                AddNode obj_add = new AddNode();
                obj_add.doubleNode(node_start0, node_start1, coordinate_json_index,
                        requireContext(), __global_graphArray, 401
                ); // 401: new row id

                // return
                __global_old_start_node = obj_add.old_node;
                __global_start_node = obj_add.new_node; // for example, 6
                __global_graphArray = obj_add.modified_graph; // graph[][]

            }else{

                // find nodes (5,4) and (4-5) in Add_node.java
                AddNode obj_add = new AddNode();
                obj_add.doubleNode(node_start0, node_start1, coordinate_json_index,
                        requireContext(), __global_graphArray, 501
                ); // 501: new row id

                // return
                __global_old_end_node = obj_add.old_node;
                __global_end_node = obj_add.new_node; // for example, 4
                __global_graphArray = obj_add.modified_graph; // graph[][]

            }

        }
        // if the coordinates are only between node 5 and node 4
        // then new nodes need to be added
        else if(status.equals("single_path")){

            if(statusObject.equals("start")){

                // find nodes (5,4) in Add_node.java
                AddNode obj_add1 = new AddNode();
                obj_add1.singleNode(node_start0, node_start1, coordinate_json_index,
                        requireContext(), __global_graphArray, 401
                ); // 401: new row id

                // return
                __global_old_start_node = obj_add1.old_node;
                __global_start_node = obj_add1.new_node; // for example, 6
                __global_graphArray = obj_add1.modified_graph; // graph[][]

            }else{

                // find nodes (5,4) in Add_node.java
                AddNode obj_add1 = new AddNode();
                obj_add1.singleNode(node_start0, node_start1, coordinate_json_index,
                        requireContext(), __global_graphArray, 501
                ); // 501: new row id

                // return
                __global_old_end_node = obj_add1.old_node;
                __global_end_node = obj_add1.new_node; // for example, 4
                __global_graphArray = obj_add1.modified_graph; // graph[][]
            }

        }
    }
    public void deleteTemporaryRecord(){

        //Check whether the variable dbHelper is null
        if (dbHelper == null) {
            dbHelper = new SQLHelper(requireContext()); // Initialize dbHelper if not already done
        }

        final SQLiteDatabase dbDelete = dbHelper.getWritableDatabase();

        // Delete temporary record from DB
        for (int i = 0; i < 4; i++) {
            // Delete additional starting node, starting from id 401,402,403,404
            String deleteQueryStart = "DELETE FROM graph WHERE id = '" + (401 + i) + "'";
            dbDelete.execSQL(deleteQueryStart);

            // Delete additional destination node, starting from id 501,502,503,504
            String deleteQueryDestination = "DELETE FROM graph WHERE id = '" + (501 + i) + "'";
            dbDelete.execSQL(deleteQueryDestination);
        }
    }
    public void maxRowDB(){
        dbHelper = new SQLHelper(requireContext());
        SQLiteDatabase dbRead = dbHelper.getReadableDatabase();


        cursor = dbRead.rawQuery("SELECT max(starting), max(ending) FROM graph", null);
        cursor.moveToFirst();
        int max_node_db = 0;
        int max_start_node_db = Integer.parseInt(cursor.getString(0).toString());
        int max_destination_node_db = Integer.parseInt(cursor.getString(1).toString());

        if (max_start_node_db >= max_destination_node_db) {
            max_node_db = max_start_node_db;
        } else {
            max_node_db = max_destination_node_db;
        }

        // return
        __global_maxRow0 = (max_node_db + 1);
        __global_maxRow1 = (max_node_db + 2);
    }
    public int getColor(String id) {
        int todaColor = 0;

        if("0".equals(id) || "1".equals(id) || "2".equals(id)) {
            todaColor = Color.parseColor("#FF993c1d");
        }
        if("3".equals(id) || "4".equals(id) || "5".equals(id)) {
            todaColor = Color.parseColor("#9dd089");
        }
        if("6".equals(id) || "7".equals(id) || "8".equals(id) || "9".equals(id) || "10".equals(id) || "11".equals(id)) {
            todaColor = Color.parseColor("#a3d393");
        }
        if("12".equals(id) || "13".equals(id)) {
            todaColor = Color.parseColor("#ef5da2");
        }
        if("14".equals(id) || "15".equals(id)) {
            todaColor = Color.parseColor("#640723");
        }
        if("16".equals(id)) {
            todaColor = Color.parseColor("#d1c0a8");
        }
        if("17".equals(id) || "18".equals(id) || "19".equals(id) || "20".equals(id)) {
            todaColor = Color.parseColor("#ababab");
        }
        if("21".equals(id) || "22".equals(id) || "23".equals(id) || "24".equals(id) || "27".equals(id)) {
            todaColor = Color.parseColor("#073b3a");
        }
        if("28".equals(id) || "29".equals(id) || "30".equals(id) || "32".equals(id) || "33".equals(id)) {
            todaColor = Color.parseColor("#252466");
        }
        if("33".equals(id) || "34".equals(id) || "35".equals(id) || "36".equals(id) || "37".equals(id) || "38".equals(id)) {
            todaColor = Color.parseColor("#fed700");
        }
        if("39".equals(id) || "40".equals(id) || "41".equals(id) || "42".equals(id)) {
            todaColor = Color.parseColor("#cd3a7b");
        }
        if("43".equals(id) || "44".equals(id) || "45".equals(id) || "46".equals(id)) {
            todaColor = Color.parseColor("#592e90");
        }
        if("47".equals(id) || "48".equals(id) || "49".equals(id) || "50".equals(id) || "51".equals(id) || "52".equals(id)) {
            todaColor = Color.parseColor("#81c9ef");
        }
        if("53".equals(id) || "54".equals(id) || "55".equals(id)) {
            todaColor = Color.parseColor("#3953a4");
        }
        if("56".equals(id) || "57".equals(id) || "58".equals(id) || "59".equals(id)) {
            todaColor = Color.parseColor("#b9529f");
        }
        if("60".equals(id) || "61".equals(id)) {
            todaColor = Color.parseColor("#f58220");
        }
        if("62".equals(id)) {
            todaColor = Color.parseColor("#fa0202");
        }
        if("63".equals(id) || "64".equals(id) || "65".equals(id)) {
            todaColor = Color.parseColor("#f7ec13");
        }
        if("66".equals(id) || "67".equals(id) || "68".equals(id) || "69".equals(id) || "70".equals(id) || "71".equals(id) || "72".equals(id) || "73".equals(id) || "74".equals(id) || "75".equals(id) || "76".equals(id) || "77".equals(id) || "78".equals(id) || "79".equals(id) || "80".equals(id)) {
            todaColor = Color.parseColor("#69a1ff");
        }
        return todaColor;
    }
    public String getTodaName(String id) {
        String todaName = null;

        if("0".equals(id) || "1".equals(id) || "2".equals(id)) {
            todaName = "KATODA";
        }
        if("3".equals(id) || "4".equals(id) || "5".equals(id)) {
            todaName = "LNSTODA";
        }
        if("6".equals(id) || "7".equals(id) || "8".equals(id) || "9".equals(id) || "10".equals(id) || "11".equals(id)) {
            todaName = "CSVTODA";
        }
        if("12".equals(id) || "13".equals(id)) {
            todaName = "DOVTODA";
        }
        if("14".equals(id) || "15".equals(id)) {
            todaName = "HVTODA";
        }
        if("16".equals(id)) {
            todaName = "BOTODA";
        }
        if("17".equals(id) || "18".equals(id) || "19".equals(id) || "20".equals(id)) {
            todaName = "PUDTODA";
        }
        if("21".equals(id) || "22".equals(id) || "23".equals(id) || "24".equals(id) || "27".equals(id)) {
            todaName = "SICALATODA";
        }
        if("28".equals(id) || "29".equals(id) || "30".equals(id) || "32".equals(id) || "33".equals(id)) {
            todaName = "BMBGTODA";
        }
        if("33".equals(id) || "34".equals(id) || "35".equals(id) || "36".equals(id) || "37".equals(id) || "38".equals(id)) {
            todaName = "MCCHTODAI";
        }
        if("39".equals(id) || "40".equals(id) || "41".equals(id) || "42".equals(id)) {
            todaName = "SJV7TODA";
        }
        if("43".equals(id) || "44".equals(id) || "45".equals(id) || "46".equals(id)) {
            todaName = "MACATODA";
        }
        if("47".equals(id) || "48".equals(id) || "49".equals(id) || "50".equals(id) || "51".equals(id) || "52".equals(id)) {
            todaName = "BBTODA";
        }
        if("53".equals(id) || "54".equals(id) || "55".equals(id)) {
            todaName = "BTATODA";
        }
        if("56".equals(id) || "57".equals(id) || "58".equals(id) || "59".equals(id)) {
            todaName = "SJBTODA";
        }
        if("60".equals(id) || "61".equals(id)) {
            todaName = "MACOPASTRTODA";
        }
        if("62".equals(id)) {
            todaName = "POSATODA";
        }
        if("63".equals(id) || "64".equals(id) || "65".equals(id)) {
            todaName = "OSPOTODA";;
        }
        if("66".equals(id) || "67".equals(id) || "68".equals(id) || "69".equals(id) || "70".equals(id) || "71".equals(id) || "72".equals(id) || "73".equals(id) || "74".equals(id) || "75".equals(id) || "76".equals(id) || "77".equals(id) || "78".equals(id) || "79".equals(id) || "80".equals(id)) {
            todaName = "JEEPNEY";
        }
        return todaName;
    }
    public int getMarkerIcon(String id) {
        int markerIcon = 0;

        if("0".equals(id) || "1".equals(id) || "2".equals(id)) {
            markerIcon = R.drawable.ka_toda;
        }
        if("3".equals(id) || "4".equals(id) || "5".equals(id)) {
            markerIcon = R.drawable.lns_toda;
        }
        if("6".equals(id) || "7".equals(id) || "8".equals(id) || "9".equals(id) || "10".equals(id) || "11".equals(id)) {
            markerIcon = R.drawable.csv_toda;
        }
        if("6".equals(id) || "7".equals(id) || "8".equals(id) || "9".equals(id) || "10".equals(id) || "11".equals(id)) {
            markerIcon = R.drawable.csv_toda;
        }
        if("12".equals(id) || "13".equals(id)) {
            markerIcon = R.drawable.dov_toda;
        }
        if("14".equals(id) || "15".equals(id)) {
            markerIcon = R.drawable.hv_toda;
        }
        if("16".equals(id)) {
            markerIcon = R.drawable.bo_toda;
        }
        if("17".equals(id) || "18".equals(id) || "19".equals(id) || "20".equals(id)) {
            markerIcon = R.drawable.pud_toda;
        }
        if("21".equals(id) || "22".equals(id) || "23".equals(id) || "24".equals(id) || "27".equals(id)) {
            markerIcon = R.drawable.sicala_toda;
        }
        if("28".equals(id) || "29".equals(id) || "30".equals(id) || "32".equals(id) || "33".equals(id)) {
            markerIcon = R.drawable.bmbg_toda;
        }
        if("33".equals(id) || "34".equals(id) || "35".equals(id) || "36".equals(id) || "37".equals(id) || "38".equals(id)) {
            markerIcon = R.drawable.mcch_toda;
        }
        if("39".equals(id) || "40".equals(id) || "41".equals(id) || "42".equals(id)) {
            markerIcon = R.drawable.sjv7_toda;
        }
        if("43".equals(id) || "44".equals(id) || "45".equals(id) || "46".equals(id)) {
            markerIcon = R.drawable.maca_toda;
        }
        if("47".equals(id) || "48".equals(id) || "49".equals(id) || "50".equals(id) || "51".equals(id) || "52".equals(id)) {
            markerIcon = R.drawable.bb_toda;
        }
        if("53".equals(id) || "54".equals(id) || "55".equals(id)) {
            markerIcon = R.drawable.bta_toda;
        }
        if("56".equals(id) || "57".equals(id) || "58".equals(id) || "59".equals(id)) {
            markerIcon = R.drawable.sjb_toda;
        }
        if("60".equals(id) || "61".equals(id)) {
            markerIcon = R.drawable.macopastar;
        }
        if("62".equals(id)) {
            markerIcon = R.drawable.posa_toda;
        }
        if("63".equals(id) || "64".equals(id) || "65".equals(id)) {
            markerIcon = R.drawable.ospo_toda;
        }
        if("66".equals(id) || "67".equals(id) || "68".equals(id) || "69".equals(id) || "70".equals(id) || "71".equals(id) || "72".equals(id) || "73".equals(id) || "74".equals(id) || "75".equals(id) || "76".equals(id) || "77".equals(id) || "78".equals(id) || "769".equals(id) || "80".equals(id)) {
            markerIcon = R.drawable.jeep_pin_icon;
        }
        return markerIcon;
    }
    public double convertToKm(double meters){
        double km = meters/1000;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        double format = Double.parseDouble(decimalFormat.format(km));
        return format;
    }
    public String estimatedTimeDuration(double totalDistance) {
        String time;
        DecimalFormat decimalFormat = new DecimalFormat("#");
        double cal = (convertToKm(totalDistance)/30) * 60;
        time = String.valueOf(decimalFormat.format(cal));

        return time;
    }
    public int discountFare(double fare) {
        double discountedFare = 0;

        if(getFareDiscountStatus.equals("none")) {
            discountedFare = fare;
        }

        if (getFareDiscountStatus.equals("discounted")) {
            discountedFare = fare - (fare * 0.20);
        }

        DecimalFormat decimalFormat = new DecimalFormat("#0");

        int format = Integer.parseInt(decimalFormat.format(discountedFare));

        return format;
    }
    private void showLocationNotFoundErrorDialog() {
        View alertCustomDialog = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_location_not_found, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());
        alertDialog.setView(alertCustomDialog);
        Button goBackBtn = alertCustomDialog.findViewById(R.id.locatioNotFoundBtn);
        final AlertDialog dialog = alertDialog.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.remove(userLoc);
                editor.remove(userDes);
                editor.remove(cost);
                editor.commit();
                dialog.cancel();
                Intent intent = new Intent(requireContext(), MainActivity.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(0,0);
            }
        });
    }
    // Helper method to check if an exception is an ANR issue
    private boolean isANRIssue(Exception e) {
        // Add your logic to check if the exception is related to ANR
        // You may inspect the exception type or other characteristics

        // For example, checking if the exception message contains "ANR"
        return e.getMessage() != null && e.getMessage().toLowerCase().contains("anr");
    }
    public double getLatitude(Context context, String loc) {
        Geocoder geocoder = new Geocoder(context);
        double latitude = 0;

        try {
            List<Address> addresses = geocoder.getFromLocationName(loc, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                latitude = address.getLatitude();
            }
        } catch (IOException e) {
            // Handle the exception more gracefully in a production environment
            e.printStackTrace();
            // Log the error for debugging purposes
            Log.e("Geocoding", "Error getting latitude for location: " + loc, e);
        }

        return latitude;
    }

    public double getLongitude(Context context, String loc) {
        Geocoder geocoder = new Geocoder(context);
        double longitude = 0;

        try {
            List<Address> addresses = geocoder.getFromLocationName(loc, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                longitude = address.getLongitude();
            }
        } catch (IOException e) {
            // Handle the exception more gracefully in a production environment
            e.printStackTrace();
            // Log the error for debugging purposes
            Log.e("Geocoding", "Error getting longitude for location: " + loc, e);
        }

        return longitude;
    }

    public void searchLocation(Context context, String searchLocation) {
        Geocoder geocoder = new Geocoder(context);

        try {
            List<Address> addresses = geocoder.getFromLocationName(searchLocation, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng location = new LatLng(address.getLatitude(), address.getLongitude());
                mapAPI.addMarker(new MarkerOptions().position(location).title(searchLocation));
                mapAPI.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            }
        } catch (IOException e) {
            // Handle the exception more gracefully in a production environment
            e.printStackTrace();
            // Log the error for debugging purposes
            Log.e("Geocoding", "Error searching location: " + searchLocation, e);
            showLocationNotFoundErrorDialog();
        }
    }

    private void pinLocationOnMap(double latitude, double longitude, String title) {
        LatLng location = new LatLng(latitude, longitude);
        mapAPI.addMarker(new MarkerOptions().position(location).title(title));
        mapAPI.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
    }
    private void requestLastKnownLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mapAPI.addMarker(new MarkerOptions().position(latLng).title("My Location"));
                        mapAPI.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
                    }
                }
            }
        });
    }
    private void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    // ... other methods ...

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            double bottom =  14.217955 - 0.1f;
            double left = 121.093565 - 0.1f;
            double top = 14.297272 + 0.1f;
            double right = 121.130053 + 0.1f;


            // Define the LatLngBounds for Cabuyao, Laguna
            LatLngBounds cabuyaoBounds = new LatLngBounds(
                    new LatLng(bottom, left), // Minimum latitude and longitude for Cabuyao
                    new LatLng(top, right)  // Maximum latitude and longitude for Cabuyao
            );

            // Set the LatLngBounds for camera target
            mapAPI.setLatLngBoundsForCameraTarget(cabuyaoBounds);

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(cabuyaoBounds.getCenter(), zoom); // Adjust the zoom level as needed
            mapAPI.animateCamera(cameraUpdate);
        } else {
            // Permission denied, show custom explanation and prompt to grant permission again
            // ...
        }
    }
    
    //This is to get the users' current location

    public void setInfoView(){
        mapAPI.setInfoWindowAdapter(new InfoWindow(requireContext()));
    }

}

