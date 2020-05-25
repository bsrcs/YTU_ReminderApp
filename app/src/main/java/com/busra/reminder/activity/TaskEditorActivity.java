package com.busra.reminder.activity;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.TimePicker;

import com.busra.reminder.reminder.AlarmScheduler;
import com.busra.reminder.reminder.ReminderAlarmService;
import com.busra.reminder.utils.DateTimeUtils;
import com.busra.reminder.utils.FirebaseHelper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.busra.reminder.R;

import java.util.Calendar;

public class TaskEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText titleEdit, descEdit, dateEdit;

    private DatabaseReference reference;
    private String firebaseKey;
    private String keyTodo;
    private Uri newReminderUri;
    String startTime,startDate ;
    int h,m,dd,mm,yyyy;
    public Calendar c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_editor);
        c = Calendar.getInstance();
        titleEdit = (EditText) findViewById(R.id.titleEdit);
        descEdit = (EditText) findViewById(R.id.descEdit);
        dateEdit = (EditText) findViewById(R.id.dateEdit);

        // get a value from prev page
        titleEdit.setText(getIntent().getStringExtra("titleTask"));
        descEdit.setText(getIntent().getStringExtra("descTask"));
        dateEdit.setText(getIntent().getStringExtra("dateTask"));
        keyTodo = getIntent().getStringExtra("keyTask");

        // Firebase
        reference = FirebaseHelper.initFirebase(this).child("Task" + keyTodo);
        firebaseKey = FirebaseHelper.getFirebaseKey();

        // Init buttons
        findViewById(R.id.btnEdit).setOnClickListener((View.OnClickListener) this);
        findViewById(R.id.btnDelete).setOnClickListener((View.OnClickListener) this);
        findViewById(R.id.btnEditTime).setOnClickListener((View.OnClickListener) this);
        findViewById(R.id.btnEditDate).setOnClickListener((View.OnClickListener) this);
        findViewById(R.id.btnShare).setOnClickListener((View.OnClickListener) this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnEdit)
            insertData();
        else if (i == R.id.btnDelete)
            deleteData();
        else if (i == R.id.btnEditTime)
            editTime();
        else if (i == R.id.btnEditDate)
            editDate();
        else if (i == R.id.btnShare)
            shareTask();
    }

    private void insertData() {
        // Edit task in database
        reference.child("titleTask").setValue(titleEdit.getText().toString());
        reference.child("descTask").setValue(descEdit.getText().toString());
        reference.child("dateTask").setValue(dateEdit.getText().toString());
        reference.child("keyTask").setValue(keyTodo);
        reference.child("keyFirebase").setValue(firebaseKey);

        Intent intent = new Intent(TaskEditorActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

        //starting alarm and calling notification service
        Intent myIntent=getIntent();
        newReminderUri=myIntent.getData();
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

        if(descEdit.getText().toString().isEmpty()){
            ReminderAlarmService.notificationContent="Tap to view more !";
        }
        else
        {
            ReminderAlarmService.notificationContent=descEdit.getText().toString();
        }


        new AlarmScheduler().setAlarm(getApplicationContext(),selectedTimestamp,newReminderUri );
    }

    private void deleteData() {
        // delete task in database
        reference.removeValue();

        Intent intent = new Intent(TaskEditorActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void editTime() {

        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(TaskEditorActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String resultTime = DateTimeUtils.validTime(hourOfDay, minute);
                        dateEdit.setText(DateTimeUtils.dateManager(resultTime));
                        startTime= resultTime;
                        h=hourOfDay;
                        m=minute;
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    private void editDate() {
        // Get Current Date

        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(TaskEditorActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String resultDate = DateTimeUtils.validDate(year, monthOfYear, dayOfMonth);
                        dateEdit.setText(DateTimeUtils.dateManager(resultDate));
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
        titleShare=titleEdit.getText().toString();
        deskShare=descEdit.getText().toString();

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_SUBJECT,"Your task is " +titleShare );
        email.putExtra(Intent.EXTRA_TEXT,"Task Description is " +deskShare  );
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Choose an Email client :"));

    }
}