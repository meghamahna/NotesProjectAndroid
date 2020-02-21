package com.example.notesproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class EachNoteSampleActivity extends AppCompatActivity {

    private ActionBar actionBar;
    NoteClass notes;
    String categoryName;
    Intent intent;

    NoteDetailFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_note_sample);

        intent = getIntent();
        notes = (NoteClass) intent.getSerializableExtra("note");
        fragment = new NoteDetailFragment(notes);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Note Details");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FF9800"));
        actionBar.setBackgroundDrawable(colorDrawable);

        BottomNavigationView navigationView = findViewById(R.id.navigationBarItems);
        navigationView.setOnNavigationItemSelectedListener(mItemSelectedListener);

        if (notes != null) {
            loadFragment(fragment);

        }else{
            categoryName = intent.getStringExtra("category");
            loadFragment(new NoteDetailFragment(categoryName));

        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()){
                case R.id.detail:
                    actionBar.setTitle("Note");
                    if (notes != null) {

                        loadFragment(fragment);

                    }

                    else {
                        categoryName = intent.getStringExtra("category");
                        loadFragment(new NoteDetailFragment(categoryName));

                    }
                    return true;

                case R.id.images:
                     actionBar.setTitle("Images");
                     loadFragment(new NoteImageFragment(notes));
                     return true;

                case R.id.audio:
                    actionBar.setTitle("Audio");
                    if(notes != null){
                        loadFragment(new NoteAudioFragment(notes));
                    }

                    else {
                        loadFragment(new NoteAudioFragment(null));
                    }
                    return true;

                case R.id.maps:
                    actionBar.setTitle("Map");
                    if(notes != null){
                        loadFragment(new NoteMapFragment(notes));
                    }

                    else {

                    }
                    return true;
            }
            return false;
        }
    };


    private void loadFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.commit();
    }
}
