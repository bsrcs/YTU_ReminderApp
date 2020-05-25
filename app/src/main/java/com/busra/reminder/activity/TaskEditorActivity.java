package com.busra.reminder.activity;

import androidx.appcompat.app.AppCompatActivity;

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
import com.busra.reminder.constant.ReminderAppConstants;
import com.busra.reminder.reminder.AlarmScheduler;
import com.busra.reminder.reminder.ReminderAlarmService;
import com.busra.reminder.utils.DateTimeUtils;
import com.busra.reminder.utils.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;

import com.busra.reminder.R;

import java.util.Calendar;

import static com.busra.reminder.constant.ReminderAppConstants.CATEGORY;
import static com.busra.reminder.constant.ReminderAppConstants.DATE;
import static com.busra.reminder.constant.ReminderAppConstants.DESC;
import static com.busra.reminder.constant.ReminderAppConstants.FREQUENCY;
import static com.busra.reminder.constant.ReminderAppConstants.ID;
import static com.busra.reminder.constant.ReminderAppConstants.REMINDER_TYPE_MONTHLY;
import static com.busra.reminder.constant.ReminderAppConstants.REMINDER_TYPE_WEEKLY;
import static com.busra.reminder.constant.ReminderAppConstants.REMINDER_TYPE_YEARLY;
import static com.busra.reminder.constant.ReminderAppConstants.TITLE;

public class TaskEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextTitleEdit, editTextCategoryEdit, editTextDescEdit, editTextDateEdit;
    private Spinner spinnerFrequencyOptionsEdit;
    private DatabaseReference reference;
    private String taskId;
    private Uri newReminderUri;
    String startTime,startDate, frequency;
    int h,m,dd,mm,yyyy;
    public Calendar c;
    private String[] FREQUENCY_OPTIONS = {
            ReminderAppConstants.REMINDER_TYPE_ONCE,
            ReminderAppConstants.REMINDER_TYPE_MONTHLY,
            ReminderAppConstants.REMINDER_TYPE_WEEKLY,
            ReminderAppConstants.REMINDER_TYPE_YEARLY
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_editor);
        c = Calendar.getInstance();
        editTextTitleEdit = (EditText) findViewById(R.id.editTextTitleEdit);
        editTextCategoryEdit =(EditText) findViewById(R.id.editTextCategoryEdit);
        editTextDescEdit = (EditText) findViewById(R.id.editTextDescEdit);
        editTextDateEdit = (EditText) findViewById(R.id.editTextDateEdit);
        spinnerFrequencyOptionsEdit = (Spinner) findViewById(R.id.spinnerFrequencyOptionsEdit);

        // get a value from prev page
        editTextTitleEdit.setText(getIntent().getStringExtra(TITLE));
        editTextCategoryEdit.setText(getIntent().getStringExtra(CATEGORY));
        editTextDescEdit.setText(getIntent().getStringExtra(DESC));
        editTextDateEdit.setText(getIntent().getStringExtra(DATE));
        // get frequency as a string from Intent
        frequency = getIntent().getStringExtra(FREQUENCY);

        taskId = getIntent().getStringExtra(ID);
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,FREQUENCY_OPTIONS);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrequencyOptionsEdit.setAdapter(spinnerAdapter);
        //get the position(0,1,2,3) by frequency arg in func and set it to options in view
        spinnerFrequencyOptionsEdit.setSelection(getSelectedPosition(frequency));
        // get reference from firebase by using the the id of task to be editing
        reference = FirebaseHelper.initFirebase(this).child(taskId);

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
        reference.child(TITLE).setValue(editTextTitleEdit.getText().toString());
        reference.child(DESC).setValue(editTextDescEdit.getText().toString());
        reference.child(DATE).setValue(editTextDateEdit.getText().toString());

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

        if(editTextDescEdit.getText().toString().isEmpty()){
            ReminderAlarmService.notificationContent="Tap to view more !";
        }
        else
        {
            ReminderAlarmService.notificationContent=editTextDescEdit.getText().toString();
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
                        editTextDateEdit.setText(DateTimeUtils.dateManager(resultTime));
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
                        editTextDateEdit.setText(DateTimeUtils.dateManager(resultDate));
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
        titleShare=editTextTitleEdit.getText().toString();
        deskShare=editTextDescEdit.getText().toString();

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_SUBJECT,"Your task is " +titleShare );
        email.putExtra(Intent.EXTRA_TEXT,"Task Description is " +deskShare  );
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Choose an Email client :"));

    }

    private int getSelectedPosition(String selectedFrequency){
        switch(selectedFrequency){
            case REMINDER_TYPE_MONTHLY:
                return 1;
            case REMINDER_TYPE_WEEKLY:
                return 2;
            case REMINDER_TYPE_YEARLY:
                return 3;
            default:
                return 0;
        }
    }
}