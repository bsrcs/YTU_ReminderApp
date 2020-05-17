package com.busra.reminder.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.busra.reminder.reminder.AlarmScheduler;
import com.busra.reminder.reminder.ReminderAlarmService;
import com.busra.reminder.utils.DateTimeUtils;
import com.busra.reminder.utils.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.busra.reminder.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class NewTaskActivity extends AppCompatActivity implements View.OnClickListener {
    public Calendar c;
    private EditText titleTask, descTask, dateTask;

    private DatabaseReference reference;
    private String firebaseKey;

    private String keyTodo = UUID.randomUUID().toString();
    private Uri mCurrentReminderUri;
    String startTime,startDate ;
    int h,m,dd,mm,yyyy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        //initializing views and the calender
        c = Calendar.getInstance();
        titleTask = (EditText) findViewById(R.id.titleTodoTask);
        descTask = (EditText) findViewById(R.id.descTodoTask);
        dateTask = (EditText) findViewById(R.id.dateTodoTask);
        dateTask.setEnabled(false);

        // Firebase
        reference = FirebaseHelper.initFirebase(this).child("Task" + keyTodo);
        firebaseKey = FirebaseHelper.getFirebaseKey();

        // Init buttons
        findViewById(R.id.btnCreateTask).setOnClickListener((View.OnClickListener) this);
        findViewById(R.id.btnCancel).setOnClickListener((View.OnClickListener) this);
        findViewById(R.id.btnSetTime).setOnClickListener((View.OnClickListener) this);
        findViewById(R.id.btnSetDate).setOnClickListener((View.OnClickListener) this);
    }
    //This method inserts data to the firebase server
    private void insertData() {
        // insert data to database
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                dataSnapshot.getRef().child("titleTask").setValue(titleTask.getText().toString());
                dataSnapshot.getRef().child("descTask").setValue(descTask.getText().toString());
                dataSnapshot.getRef().child("dateTask").setValue(dateTask.getText().toString());
                dataSnapshot.getRef().child("keyTask").setValue(keyTodo);
                dataSnapshot.getRef().child("keyFirebase").setValue(firebaseKey);

                Intent intent = new Intent(NewTaskActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DB","Invalid db update= " +databaseError);

            }
        });



        //starting alarm and calling notification service
        Intent myIntent=getIntent();
        mCurrentReminderUri=myIntent.getData();
        Log.e("VALUE TIME"+this.getClass().getName(),"" +startTime);
        Log.e("VALUE DATE" +this.getClass().getName(),"" +startDate);
        Calendar calendar= Calendar.getInstance();
        if(startDate==null){
            int yyyy = c.get(Calendar.YEAR);
            int mm = c.get(Calendar.MONTH);
            int dd = c.get(Calendar.DAY_OF_MONTH);
            Log.e("DATE TIME" ,"" +yyyy+""+mm + " "+ dd);
            calendar.set(yyyy,mm,dd,h,m,0);
        }else {
            calendar.set(yyyy, mm, dd, h, m, 0);
        }

        long selectedTimestamp =  calendar.getTimeInMillis();
        Log.e("NOTIFICATION","Timestamp =" + " "+selectedTimestamp);

         if(descTask.getText().toString().isEmpty()){
             ReminderAlarmService.notificationContent="Tap to view more !";
         }
         else
         {
             ReminderAlarmService.notificationContent=descTask.getText().toString();
         }

        //starting the alarm manager
        new AlarmScheduler().setAlarm(getApplicationContext(),selectedTimestamp,mCurrentReminderUri );


    }



    private void setTime() {
  // c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(NewTaskActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String resultTime = DateTimeUtils.validTime(hourOfDay, minute);
                        Log.e("TIME VALUE,Timepicker"," " +resultTime);
                        dateTask.setText(DateTimeUtils.dateManager(resultTime));
                        startTime= resultTime;
                      h=hourOfDay;
                      m=minute;
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    private void setDate() {
      //  c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(NewTaskActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String resultDate = DateTimeUtils.validDate(year, monthOfYear, dayOfMonth);
                        Log.e("DATE VALUE,Datepicker"," " +resultDate);
                        dateTask.setText(DateTimeUtils.dateManager(resultDate));
                        startDate= resultDate;
                      yyyy=year;
                      mm=monthOfYear;
                      dd=dayOfMonth;

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnCreateTask) {
            insertData();
        } else if (i == R.id.btnCancel) {
            Intent intent = new Intent(NewTaskActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
           // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

         //   recreate();
        }
        if (i == R.id.btnSetTime) {
            setTime();
        }
        if (i == R.id.btnSetDate) {
            setDate();
        }
    }
}