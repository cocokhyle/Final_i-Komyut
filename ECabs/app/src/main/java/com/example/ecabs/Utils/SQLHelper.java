package com.example.ecabs.Utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecabs.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SQLHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "routeplan_graph.db";
    private static final int DATABASE_VERSION = 1;
    private final Context myContext;

    public SQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // This method will not be used, as we're executing SQL script instead

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This method will not be used, as we're executing SQL script instead
    }

    public void createDB() throws IOException {
        getJson();
        Log.e("Start", "error here");
        if (isDatabaseExist()){
            SQLiteDatabase db = getWritableDatabase();
            try {
                String createGraphTableQuery  = "CREATE TABLE IF NOT EXISTS graph (" +
                        "id INTEGER," +
                        "starting INTEGER," +
                        "ending INTEGER," +
                        "route TEXT," +
                        "weight_distance DOUBLE," +
                        "weight_fare DOUBLE" +
                        ")";
                db.execSQL(createGraphTableQuery );
                String createPublicTransitTableQuery = "CREATE TABLE IF NOT EXISTS public_transit (" +
                        "id INTEGER," +
                        "no_route VARCHAR," +
                        "string VARCHAR" +
                        ")";
                db.execSQL(createPublicTransitTableQuery);
                getReadableDatabase();

            }finally {
                db.close();
            }
        }

    }

    private boolean isDatabaseExist() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(
                    myContext.getDatabasePath(DATABASE_NAME).getPath(),
                    null,
                    SQLiteDatabase.OPEN_READONLY);
        } catch (SQLException e) {
            // Database does not exist yet
        } finally {
            if (checkDB != null) {
                checkDB.close();
            }
        }
        return checkDB != null;
    }
    private void createGraphTable(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS graph (" +
                "id INTEGER," +
                "starting INTEGER," +
                "ending INTEGER," +
                "route TEXT," +
                "weight_distance DOUBLE," +
                "weight_fare DOUBLE" +
                ")";
        db.execSQL(createTableQuery);
    }

    private void createPublicTransitTable(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS public_transit (" +
                "id INTEGER," +
                "no_route VARCHAR," +
                "string VARCHAR" +
                ")";
        db.execSQL(createTableQuery);
    }


    public void insertGraphData(int id, int starting, int ending, String route, double weightDistance, double weightFare) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the record with the given id already exists
        Cursor cursor = db.rawQuery("SELECT * FROM graph WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            // Record with the given id exists, check if weight_fare matches
            @SuppressLint("Range") double existingWeightFare = cursor.getDouble(cursor.getColumnIndex("weight_fare"));

            if (existingWeightFare != weightFare) {
                // Weight_fare does not match, update the record with new values
                ContentValues updateValues = new ContentValues();
                updateValues.put("starting", starting);
                updateValues.put("ending", ending);
                updateValues.put("route", route);
                updateValues.put("weight_distance", weightDistance);
                updateValues.put("weight_fare", weightFare);

                db.update("graph", updateValues, "id = ?", new String[]{String.valueOf(id)});
            }
            // Close the cursor and database and return
            cursor.close();
            db.close();
            return;
        }

        // If execution reaches here, the record with the given id does not exist, insert a new record
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("starting", starting);
        values.put("ending", ending);
        values.put("route", route);
        values.put("weight_distance", weightDistance);
        values.put("weight_fare", weightFare);

        // Insert the data into the 'graph' table
        db.insert("graph", null, values);

        // Close the cursor and database
        cursor.close();
        db.close();
    }

    public void insertPublicTransitData(int id, String noRoute, String string) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the record with the given id already exists
        Cursor cursor = db.rawQuery("SELECT * FROM public_transit WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            // Record with the given id exists, check if 'no_route' and 'string' match
            @SuppressLint("Range") String existingNoRoute = cursor.getString(cursor.getColumnIndex("no_route"));
            @SuppressLint("Range") String existingString = cursor.getString(cursor.getColumnIndex("string"));

            if (!existingNoRoute.equals(noRoute) || !existingString.equals(string)) {
                // 'no_route' or 'string' does not match, update the record with new values
                ContentValues updateValues = new ContentValues();
                updateValues.put("no_route", noRoute);
                updateValues.put("string", string);

                db.update("public_transit", updateValues, "id = ?", new String[]{String.valueOf(id)});
            }
            // Close the cursor and database and return
            cursor.close();
            db.close();
            return;
        }

        // If execution reaches here, the record with the given id does not exist, insert a new record
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("no_route", noRoute);
        values.put("string", string);

        // Insert the data into the 'public_transit' table
        db.insert("public_transit", null, values);

        // Close the cursor and database
        cursor.close();
        db.close();
    }
    public void getJson(){
        String apiUrl = "http://192.168.100.4:5000/api/getData";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray graphArray = response.getJSONArray("graph");
                    JSONArray transitArray = response.getJSONArray("public_transit");

                    // Now you can iterate through the array and access individual objects
                    for (int i = 0; i < graphArray.length(); i++) {
                        JSONObject graphObject = graphArray.getJSONObject(i);

                        // Access individual properties of each graphObject
                        int id = graphObject.getInt("id");
                        int starting = graphObject.getInt("starting");
                        int ending = graphObject.getInt("ending");
                        String route = graphObject.getString("route");

                        // Access other properties like "weight_distance" and "weight_fare"
                        double weightDistance = graphObject.getDouble("weight_distance");
                        double weightFare = graphObject.getDouble("weight_fare");

                       /* Log.d("ImHere", "" + route);*/

                        insertGraphData(id, starting, ending, route, weightDistance, weightFare);


                    }
                    for (int i = 0; i < transitArray.length(); i++) {
                        JSONObject transitObject = transitArray.getJSONObject(i);
                        int id = transitObject.getInt("id");
                        String no_route = transitObject.getString("no_route");
                        String string = transitObject.getString("string");

                        insertPublicTransitData(id, no_route, string);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON parsing error
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error response
            }
        });

        Volley.newRequestQueue(myContext.getApplicationContext()).add(request);
    }
}
