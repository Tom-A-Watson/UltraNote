package com.example.ultranote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void openHome(View view) {
        Intent homePage = new Intent(this, Home.class);
        startActivity(homePage);
    }

    public void openMyNotes(View view) {
        Intent myNotesPage = new Intent(this, MyNotes.class);
        startActivity(myNotesPage);
    }

    public void openCreateNote(View view) {
        Intent createNotePage = new Intent(this, CreateNote.class);
        startActivity(createNotePage);
    }

    public void openCategories(View view) {
        Intent categoriesPage = new Intent(this, Categories.class);
        startActivity(categoriesPage);
    }

    public void openSettings(View view) {
        Intent settingsPage = new Intent(this, Settings.class);
        startActivity(settingsPage);
    }
}