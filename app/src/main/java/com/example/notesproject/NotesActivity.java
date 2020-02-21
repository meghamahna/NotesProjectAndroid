package com.example.notesproject;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NotesActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<NoteClass> notes;
    List<NoteClass> searchList = new ArrayList<>();
    List<String> search_list = new ArrayList<>();
    String categoryName;
    EditText searchText;
    int temp = 0;
    private ArrayAdapter arrayAdapter;
    DatabaseHelper mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = findViewById(R.id.notesList);
        notes = new ArrayList<>();
        mDatabase = new DatabaseHelper(this);
        searchText = findViewById(R.id.searchText);

        //loadNotes();
        //searchList = notes;

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent intent = new Intent(NotesActivity.this, EachNoteSampleActivity.class);
                intent.putExtra("category",categoryName);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(temp == 0){

                    Intent intent = new Intent(NotesActivity.this, EachNoteSampleActivity.class);
                    intent.putExtra("note", notes.get(position));
                    startActivity(intent);
                }
                else if(temp == 1){

                    Intent intent = new Intent(NotesActivity.this, EachNoteSampleActivity.class);
                    intent.putExtra("note", searchList.get(position));
                    startActivity(intent);
                }

            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                temp = 1;
                String searchText = s.toString();
                search_list.clear();
                searchList.clear();
                if(!searchText.isEmpty()){

                    for (NoteClass note:notes) {
                        if(note.getTitle().contains(searchText)){
                            searchList.add(note);
                            search_list.add(note.getTitle());
                        }
                    }
                } else{
                    temp = 0;
                    searchList.addAll(notes);
                    for (NoteClass note:notes) {
                            search_list.add(note.getTitle());
                    }

                }

                arrayAdapter = new ArrayAdapter(NotesActivity.this , android.R.layout.simple_list_item_1 , search_list);
                listView.setAdapter(arrayAdapter);


            }



            @Override
            public void afterTextChanged(Editable s) {





            }
        });
    }



    public void loadNotes(){


        notes.clear();
        Intent intent = getIntent();
        categoryName = intent.getStringExtra("category");

        Cursor cursor = mDatabase.getAllNotes(categoryName);

        if (cursor.moveToFirst()) {
            do {

                notes.add(new NoteClass(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getDouble(5),
                        cursor.getDouble(6)

                ));
            } while (cursor.moveToNext());
            cursor.close();
        }


        String[] titles = new String[notes.size()];

        for(int i = 0; i < notes.size(); i++){

            titles[i] = notes.get(i).getTitle();
            Log.i("Main", titles[i]);
        }


        arrayAdapter = new ArrayAdapter(this , android.R.layout.simple_list_item_1 ,titles );
        listView.setAdapter(arrayAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_title){

            Collections.sort(notes, new Comparator<NoteClass>() {
                @Override
                public int compare(NoteClass o1, NoteClass o2) {
                    if (o1.getTitle() == null || o2.getTitle() == null)
                        return 0;
                    return o1.getTitle().compareTo(o2.getTitle());
                }
            });

            String[] titles = new String[notes.size()];

            for(int i = 0; i < notes.size(); i++){

                titles[i] = notes.get(i).getTitle();
            }

            arrayAdapter = new ArrayAdapter(this , android.R.layout.simple_list_item_1 ,titles);
            listView.setAdapter(arrayAdapter);



        }

        if(item.getItemId() == R.id.action_date){


                Collections.sort(notes, new Comparator<NoteClass>() {
                    @Override
                    public int compare(NoteClass o1, NoteClass o2) {
                        if (o1.getDate() == null || o2.getDate() == null)
                            return 0;
                        return o1.getDate().compareTo(o2.getDate());
                    }
                });

                String[] title = new String[notes.size()];

                for(int i = 0; i < notes.size(); i++){

                    title[i] = notes.get(i).getTitle();
                    Log.i("Main", title[i]);
                }

                arrayAdapter = new ArrayAdapter(this , android.R.layout.simple_list_item_1 ,title);
                listView.setAdapter(arrayAdapter);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadNotes();


    }


}
