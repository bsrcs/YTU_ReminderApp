package com.busra.reminder.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.busra.reminder.R;
import com.busra.reminder.constant.ReminderAppConstants;
import com.busra.reminder.reminder.AlarmScheduler;
import com.busra.reminder.reminder.ReminderAlarmService;
import com.busra.reminder.utils.DateTimeUtils;
import com.busra.reminder.utils.FirebaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

import static com.busra.reminder.constant.ReminderAppConstants.*;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.busra.reminder.reminder.AlarmScheduler;
import com.busra.reminder.reminder.ReminderAlarmService;
import com.busra.reminder.utils.DateTimeUtils;
import com.busra.reminder.utils.FirebaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import com.busra.reminder.R;

import java.util.Calendar;
import java.util.UUID;

public class NewTaskActivity extends AppCompatActivity implements View.OnClickListener {
    public Calendar c;
    private EditText editTextTitle, editTextDesc, editTextDate,editTextCategory;
    private Spinner spinnerFrequencyOptions;
    private DatabaseReference reference;
    private String newTaskId = UUID.randomUUID().toString();
    private Uri mCurrentReminderUri;
    String startTime,startDate ;
    int h,m,dd,mm,yyyy;
    private String[] FREQUENCY_OPTIONS = {
            ReminderAppConstants.REMINDER_TYPE_ONCE,
            ReminderAppConstants.REMINDER_TYPE_MONTHLY,
            ReminderAppConstants.REMINDER_TYPE_WEEKLY,
            ReminderAppConstants.REMINDER_TYPE_YEARLY
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        //initializing views and the calender
        c = Calendar.getInstance();
        editTextTitle = (EditText) findViewById(R.id.titleTodoTask);
        editTextDesc = (EditText) findViewById(R.id.descTodoTask);
        editTextDate = (EditText) findViewById(R.id.dateTodoTask);
        editTextDate.setEnabled(false);
        editTextCategory = (EditText) findViewById(R.id.editTextCategory);
        spinnerFrequencyOptions = findViewById(R.id.spinnerFrequencyOptions);
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,FREQUENCY_OPTIONS);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinnerFrequencyOptions.setAdapter(spinnerAdapter);
        // initialise firebase to insert new Task
        reference = FirebaseHelper.initFirebase(this).child(newTaskId);

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
                dataSnapshot.getRef().child(TITLE).setValue(editTextTitle.getText().toString());
                dataSnapshot.getRef().child(DESC).setValue(editTextDesc.getText().toString());
                dataSnapshot.getRef().child(DATE).setValue(editTextDate.getText().toString());
                dataSnapshot.getRef().child(CATEGORY).setValue(editTextCategory.getText().toString());
                dataSnapshot.getRef().child(FREQUENCY).setValue((String)spinnerFrequencyOptions.getSelectedItem());
                dataSnapshot.getRef().child(ID).setValue(newTaskId);
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

         if(editTextDesc.getText().toString().isEmpty()){
             ReminderAlarmService.notificationContent="Tap to view more !";
         }
         else
         {
             ReminderAlarmService.notificationContent= editTextDesc.getText().toString();
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
                        editTextDate.setText(DateTimeUtils.dateManager(resultTime));
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
                        editTextDate.setText(DateTimeUtils.dateManager(resultDate));
                        startDate= resultDate;
                      yyyy=year;
                      mm=monthOfYear;
                      dd=dayOfMonth;

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    private void shareTask()
    {
        String titleShare,deskShare;
        titleShare= editTextTitle.getText().toString();
        deskShare= editTextDesc.getText().toString();
        if(titleShare.isEmpty() && deskShare.isEmpty()) {
            Toast.makeText(this, "First create a task !", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_SUBJECT, "Your task is " + titleShare);
            email.putExtra(Intent.EXTRA_TEXT, "Task Description is " + deskShare);
            email.setType("message/rfc822");
            startActivity(Intent.createChooser(email, "Choose an Email client :"));
        }

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