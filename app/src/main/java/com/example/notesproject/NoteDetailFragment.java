package com.example.notesproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteDetailFragment extends Fragment {

    EditText noteTitle, noteDescription, noteCategory;
    Button saveButton;
    private NoteClass notes;
    private String category;
    private DatabaseHelper mDatabase;
    boolean temp = false;
    public static final int REQUEST_CODE = 1;
    LocationManager locationManager;
    LocationListener locationListener;
    Location currentLocation;


    public NoteDetailFragment(NoteClass notes) {

        this.notes = notes;
    }

    public NoteDetailFragment(String category) {

        this.category = category;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.note_detail_frag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mDatabase = new DatabaseHelper(getContext());
        noteTitle = view.findViewById(R.id.note_title);
        noteDescription = view.findViewById(R.id.note_description);
        noteCategory = view.findViewById(R.id.note_category);
        saveButton = view.findViewById(R.id.save_button);

        if (notes != null) {


            noteTitle.setText(notes.getTitle());
            noteCategory.setText(notes.getCategory());
            noteDescription.setText(notes.getDescription());
            temp = true;

        }

        else if (category != null)
        {
            noteCategory.setText(category);
        }

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {


                if (location != null) {
                    currentLocation = location;

                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (!checkPermission()) {
            requestPermission();
        } else {

            loc();
        }


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = noteTitle.getText().toString().trim();
                String desc = noteDescription.getText().toString().trim();
                String cat = noteCategory.getText().toString().trim();

                if (temp) {

                    if (mDatabase.updateNotes(notes.getId(), title, desc, cat))
                        Toast.makeText(getContext(), "Note Updated", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getContext(), "Note Not Updated", Toast.LENGTH_SHORT).show();

                    updation(cat);
                } else {
                    SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss");

                    if (mDatabase.addNotes(title, desc, cat, date.format(new Date()), currentLocation.getLatitude(), currentLocation.getLongitude()))

                        Toast.makeText(getContext(), "Note saved", Toast.LENGTH_SHORT).show();

                    else
                        Toast.makeText(getContext(), "Note Not Saved", Toast.LENGTH_SHORT).show();

                    updation(cat);
                }


                if (!MainActivity.categoryList.contains(cat)) {

                    MainActivity.categoryList.add(cat);

                    try {

                        MainActivity.sharedPreferences.edit().putString("list", ObjectSerializer.serialize(MainActivity.categoryList)).apply();

                    }
                    catch (Exception e) {

                        e.printStackTrace();
                    }
                }


            }
        });
    }



    @SuppressLint("MissingPermission")
    private void loc() {
        requestLocationUpdate();
        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

    }

    private boolean checkPermission() {
        int permissionState = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loc();

            }else{
                Toast.makeText(getContext(), "Requires permission to access location.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @SuppressLint("MissingPermission")
    private void requestLocationUpdate() {

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 100, locationListener);
    }

    private void updation(String categoryName) {

        Cursor cursor = mDatabase.getAllNotes(categoryName);

        if (cursor.moveToLast()) {

            notes= new NoteClass(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getDouble(5),
                    cursor.getDouble(6)
            );
        }
    }


}
