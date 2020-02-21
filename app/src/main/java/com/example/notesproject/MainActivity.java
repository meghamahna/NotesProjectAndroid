package com.example.notesproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SwipeMenuListView listView;
    DatabaseHelper mDatabase;
    public static ArrayList<String> categoryList = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    public static SharedPreferences sharedPreferences;

    String category;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.listView);
        setSupportActionBar(toolbar);

        mDatabase = new DatabaseHelper(this);
        sharedPreferences = this.getSharedPreferences("com.example.notesproject", Context.MODE_PRIVATE);

        loadCategories();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                addCategory();
            }
        });

        SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {

                SwipeMenuItem delete = new SwipeMenuItem(getApplicationContext());

                delete.setIcon(R.drawable.ic_delete);
                delete.setBackground(new ColorDrawable(Color.parseColor("#FFF71B05")));
                delete.setWidth(250);
                menu.addMenuItem(delete);
            }
        };
        listView.setMenuCreator(swipeMenuCreator);


        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                if (index == 0){

                    categoryList.remove(position);
                    arrayAdapter.notifyDataSetChanged();
                    try {
                        sharedPreferences.edit().putString("list", ObjectSerializer.serialize(categoryList)).apply();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    loadCategories();

                }
                return true;
            }
        });

        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this , NotesActivity.class);
                intent.putExtra("category", categoryList.get(position));
                startActivity(intent);


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadCategories();
    }

    private void loadCategories() {

        categoryList.clear();
        try {
            categoryList = (ArrayList) ObjectSerializer.deserialize(sharedPreferences.getString("list", ObjectSerializer.serialize(new ArrayList<>())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        arrayAdapter = new ArrayAdapter(this , android.R.layout.simple_list_item_1 , categoryList);
        listView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }

    private void addCategory(){

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this );
        alertDialog.setTitle("Add Category");
        alertDialog.setMessage("Enter Category");

        final EditText input= new EditText(MainActivity.this);

        alertDialog.setView(input);

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                category = input.getText().toString();
                categoryList.add(category);

                try {
                    sharedPreferences.edit().putString("list", ObjectSerializer.serialize(categoryList)).apply();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                loadCategories();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }





}
