package com.busra.reminder.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.busra.reminder.utils.FirebaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import com.busra.reminder.AuthActivity;
import com.busra.reminder.R;
import com.busra.reminder.adapter.ReminderAdapter;
import com.busra.reminder.model.Task;

import java.util.ArrayList;


//TODO fix onBackPressed.
public class MainActivity extends AppCompatActivity {


    private ImageButton btnLogOut;
    private ProgressBar progressBar;

    private DatabaseReference reference;

    private ArrayList<Task> tasks;
    private RecyclerView recyclerView;
    private ReminderAdapter reminderAdapter;
    FloatingActionButton btnAddNewTask;
    public static final String NIGHT_MODE = "NIGHT_MODE";
    private boolean isNightModeEnabled = false;
    SharedPreferences mPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //checking if nightmode enabled? if yes then turn on nightmode. we are doing this before loading the layout
        Log.e("isNightModeEnabled " ," "+isNightModeEnabled());
        if ( isNightModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnAddNewTask =  findViewById(R.id.btnAddNewTask);
        btnLogOut = (ImageButton) findViewById(R.id.btnLogOut);
        //opening add new task activity
        btnAddNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewTaskActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //right-top icon which opens the popup menu list
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PopupMenu popup = new PopupMenu(MainActivity.this, v);
                popup.inflate(R.menu.menu_home);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            //if night mode toggle switch
                            case R.id.dayNightMenu:

                                Log.e("isNightModeEnabled menu" ," "+isNightModeEnabled());
                                //if nightmode is not enabled then turn on the night mode and set the flag to true  else turn off the night mode and set the flag to false
                              if( isNightModeEnabled()==false){
                                  setIsNightModeEnabled(true);
                                  AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                  Toast.makeText(MainActivity.this, "Turned on nightmode !", Toast.LENGTH_SHORT).show();
                              }
                              else{
                                  setIsNightModeEnabled(false);
                                  AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                  Toast.makeText(MainActivity.this, "Turned off nightmode !", Toast.LENGTH_SHORT).show();
                              }


                                return true;
                            case R.id.logout:
                                logout();
                                return true;
                                // About me menu and openning profile page
                            case R.id.item3:
                                Intent toProfileActivity= new Intent(MainActivity.this,ProfileActivity.class);
                                startActivity(toProfileActivity);
                                return true;
                            default:
                                return false;
                        }
                    }

                });
                //showing the popup menu
                popup.show();

            }
        });

        // initializing  Firebase connection and loading data from internet
        reference = FirebaseHelper.initFirebase(this);
        getDataFromFirebase();
    }

    //checking if night mode is enabled   isNightModeEnabled  is a shared pref value which stores the information in true/false value
    public boolean isNightModeEnabled() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        isNightModeEnabled = mPrefs.getBoolean(NIGHT_MODE, false);
        return isNightModeEnabled;
    }
    //This method is to turn on night mode and set the isNightModeEnabled  shared pref value to true
    public void setIsNightModeEnabled(boolean isNightModeEnabled) {
        this.isNightModeEnabled = isNightModeEnabled;

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(NIGHT_MODE, isNightModeEnabled);
        editor.apply();
    }
    // logout from firebase server and get back to the registration page
    public void logout(){
    FirebaseAuth.getInstance().signOut();
    Intent intent = new Intent(MainActivity.this, AuthActivity.class);
    startActivity(intent);
}

    //fetch data from the firebase server
    private void getDataFromFirebase() {
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = (RecyclerView) findViewById(R.id.taskList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasks = new ArrayList<>();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Task task = childSnapshot.getValue(Task.class);
                    tasks.add(task);
                }
                reminderAdapter = new ReminderAdapter(MainActivity.this, tasks);
                recyclerView.setAdapter(reminderAdapter);
                reminderAdapter.notifyDataSetChanged();
                //calling swipe to delete function on lisitems
                swipeToDelete ();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });

    }
    //This method deletes list items from the reminderList and also from the firebase server
    private void swipeToDelete () {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder
                            target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        tasks.remove(viewHolder.getAdapterPosition());
                        reminderAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        reference.removeValue();
                        Toast.makeText(MainActivity.this, "Task Deleted !", Toast.LENGTH_SHORT).show();
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    //if user presses the back button on Mainactivity
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}