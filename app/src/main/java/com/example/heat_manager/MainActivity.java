package com.example.heat_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class MainActivity extends AppCompatActivity {

    Spinner sp;
    boolean invalid = false;
    String Colector="";
    TextView txtalertName;
    TextView UserName;
    TextView UserPassword;
    EditText UserContact;
    EditText UserComment;
    Button SubmitSave;
    RadioButton Malebtn,Femalbtn;
    CheckBox html,css,php;
    Button btnInDatePicker, btnInTimePicker,btnOutDatePicker, btnOutTimePicker;
    EditText txtInDate,txtInTime,txtOutDate,txtOutTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private int mYear1, mMonth1, mDay1, mHour1, mMinute1;

    NumberPicker numberPicker, numberPickerLiving, numberPickerBed, numberPickerKitchen, numberPickerBath;
    Switch editTemp;

    int commonTempVal = 10;

    public int currentTemperature = 10;
    public int targetTemperature = 21;
    public long targetTime;
    public double specificVolume = 0.85;
    public double specificHeatCapacity = 1.005;
    public int heat = 1500;
    public double heatLoss;
    public double coefficientOfHeatTransfer = 0.5;
    public double heatingTime;
    public Connection connection;
    private Reservation reservation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        sp=findViewById(R.id.SpCountry);
        UserName=(TextView) findViewById(R.id.userName);
        UserPassword= (TextView) findViewById(R.id.userPassword);
//        UserContact=findViewById(R.id.userContact);
       // UserComment=findViewById(R.id.usercomment);
        txtalertName=findViewById(R.id.userAlert);
      //  Malebtn =findViewById(R.id.Male);
     //   Femalbtn=findViewById(R.id.Female);
       // html=findViewById(R.id.HTML);
     //   css=findViewById(R.id.CSS);
     //   php=findViewById(R.id.PHP);
        SubmitSave=findViewById(R.id.btnSubmit);
        btnInDatePicker = (Button) findViewById(R.id.btn_in_date);
        btnInTimePicker=(Button) findViewById(R.id.btn_in_time);
        txtInDate=(EditText)findViewById(R.id.in_date);
        txtInTime=(EditText)findViewById(R.id.in_time);

        btnOutDatePicker = (Button) findViewById(R.id.btn_out_date);
        btnOutTimePicker=(Button) findViewById(R.id.btn_out_time);
        txtOutDate=(EditText)findViewById(R.id.out_date);
        txtOutTime=(EditText)findViewById(R.id.out_time);

        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        numberPickerLiving = (NumberPicker) findViewById(R.id.numberPickerLiving);
        numberPickerBed = (NumberPicker) findViewById(R.id.numberPickerBed);
        numberPickerKitchen = (NumberPicker) findViewById(R.id.numberPickerKitchen);
        numberPickerBath = (NumberPicker) findViewById(R.id.numberPickerBath);

        editTemp = (Switch) findViewById(R.id.editTemp);

        //Populate NumberPicker values from minimum and maximum value range
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(1000);
        numberPickerLiving.setMinValue(0);
        numberPickerLiving.setMaxValue(1000);
        numberPickerBed.setMinValue(0);
        numberPickerBed.setMaxValue(1000);
        numberPickerKitchen.setMinValue(0);
        numberPickerKitchen.setMaxValue(1000);
        numberPickerBath.setMinValue(0);
        numberPickerBath.setMaxValue(1000);

        numberPicker.setWrapSelectorWheel(true);
        numberPickerLiving.setWrapSelectorWheel(true);
        numberPickerBed.setWrapSelectorWheel(true);
        numberPickerKitchen.setWrapSelectorWheel(true);
        numberPickerBath.setWrapSelectorWheel(true);

        numberPicker.setValue(commonTempVal);
        numberPicker.setEnabled(true);

        numberPickerLiving.setValue(commonTempVal);
        numberPickerLiving.setEnabled(false);

        numberPickerBed.setValue(commonTempVal);
        numberPickerBed.setEnabled(false);

        numberPickerKitchen.setValue(commonTempVal);
        numberPickerKitchen.setEnabled(false);

        numberPickerBath.setValue(commonTempVal);
        numberPickerBath.setEnabled(false);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            numberPicker.setTextSize(30);
            numberPickerLiving.setTextSize(30);
            numberPickerBed.setTextSize(30);
            numberPickerKitchen.setTextSize(30);
            numberPickerBath.setTextSize(30);

        }

        editTemp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){

                    numberPicker.setEnabled(true);
                    numberPickerLiving.setEnabled(true);
                    numberPickerBed.setEnabled(true);
                    numberPickerKitchen.setEnabled(true);
                    numberPickerBath.setEnabled(true);
                    numberPickerLiving.setValue(numberPicker.getValue());
                    numberPickerBed.setValue(numberPicker.getValue());
                    numberPickerKitchen.setValue(numberPicker.getValue());
                    numberPickerBath.setValue(numberPicker.getValue());
                }
                if(!isChecked){
                    numberPicker.setEnabled(true);
                    numberPickerLiving.setEnabled(false);
                    numberPickerBed.setEnabled(false);
                    numberPickerKitchen.setEnabled(false);
                    numberPickerBath.setEnabled(false);

                    numberPickerLiving.setValue(numberPicker.getValue());
                    numberPickerBed.setValue(numberPicker.getValue());
                    numberPickerKitchen.setValue(numberPicker.getValue());
                    numberPickerBath.setValue(numberPicker.getValue());


                }

            }
        });

        SubmitSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String name = UserName.getText().toString();
//                String Pascode=UserPassword.getText().toString();
//                String contact=UserContact.getText().toString();
//                String comment=UserComment.getText().toString();
//                if (name.isEmpty()){
//                    Toast.makeText(MainActivity.this,"Pleas fill the password field",Toast.LENGTH_SHORT).show();
//                }
//                else if (name.equals("Sameh") ||name.equals("UlHaq")){
//                    invalid=true;
//                    txtalertName.setText("Name Already exist");
//                }
//
//                else if(Pascode.isEmpty()){
//                    Toast.makeText(MainActivity.this,"Pleas fill the password field",Toast.LENGTH_SHORT).show();
//                }
//
//
//                else if (contact.isEmpty()){
//                    Toast.makeText(MainActivity.this,"Pleas fill the Contact field",Toast.LENGTH_SHORT).show();
//                }
//
//                else if (comment.isEmpty()){
//                    Toast.makeText(MainActivity.this,"Pleas fill the Comment field",Toast.LENGTH_SHORT).show();
//                }
//
//
//
//                else{
//
//                    Colector+=name+"\n";
//                    Colector+=Pascode+"\n";
//                    Colector+=contact+"\n";
//                    Colector+=comment+"\n";
//                    if (html.isChecked()){
//                        Colector+="HTML"+"\n";
//                        if (css.isChecked()){
//                            Colector+="CSS"+"\n";
//                        }
//                        if (php.isChecked()){
//                            Colector+="PHP"+"\n";
//                        }
//                    }
//                    Toast.makeText(MainActivity.this,"User Info \n:"+Colector,Toast.LENGTH_SHORT).show();
//                }
//
            }
        });
        btnInDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog( view.getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                txtInDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        btnOutDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear1 = c.get(Calendar.YEAR);
                mMonth1 = c.get(Calendar.MONTH);
                mDay1 = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog( view.getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                txtOutDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear1, mMonth1, mDay1);
                datePickerDialog.show();
            }
        });
        btnInTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                txtInTime.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        btnOutTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour1 = c.get(Calendar.HOUR_OF_DAY);
                mMinute1 = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                txtOutTime.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour1, mMinute1, false);
                timePickerDialog.show();
            }
        });
        List<String> categoryCountry=new ArrayList<>();
        categoryCountry.add("Select Country");
        categoryCountry.add("PAKISTAN");
        categoryCountry.add("AFGHANISTAN");
        categoryCountry.add("UAE");
        categoryCountry.add("TURKEY");
        categoryCountry.add("AMERICA");
        ArrayAdapter<String> arrayAdapter;
        arrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,categoryCountry);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        sp.setAdapter(arrayAdapter);
//        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
//                if(parent.getItemAtPosition(position).equals("Select Country")){
//                    //Do Nothing
//
//                }
//                else{
//                    String item=parent.getItemAtPosition(position).toString();
//                    Colector+=item+"\n";
//                    Toast.makeText(MainActivity.this, "Selected Country: "+item, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
    }

    // get heating time
    protected double getHeatingTime(){
        // create reservation object
        reservation = new Reservation();
        // need to fetch the data from db
        reservation.Id = 1;
        reservation.Height = 3;
        reservation.Width = 20;
        reservation.Length = 30;
        reservation.NoOfPeople = 3;
        reservation.ObjectCount = 5;
        String startDate = "2022-12-18T06:30:38.9933333"; // Input String for testing
        reservation.CheckinDate = new SimpleDateFormat("dd/MM/yyyy").parse(startDate,new ParsePosition(0));
        targetTime = reservation.CheckinDate.getTime();

        // calculate heat loss
        heatLoss = 2 * (coefficientOfHeatTransfer * reservation.Height * reservation.Width * (targetTemperature - currentTemperature)) +
                2 * (coefficientOfHeatTransfer * reservation.Height * reservation.Length * (targetTemperature - currentTemperature));

        // calculate heating time
        heatingTime = (reservation.Height * reservation.Length * reservation.Width * specificHeatCapacity
                * (targetTemperature - currentTemperature)) /
                (specificVolume * (heat - heatLoss));

        return heatingTime;
    }

    // create connection with raspberry pi
    public void createConnection(String command) {
        String hostname = "130.237.177.211";
        String username = "pi";
        String password = "IoT@2021";
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try
        {

            ch.ethz.ssh2.Connection conn = new ch.ethz.ssh2.Connection(hostname); //init connection

            conn.connect(); //start connection to the hostname
            boolean isAuthenticated = conn.authenticateWithPassword(username,
                    password);
            if (isAuthenticated == false)
                throw new IOException("Authentication failed.");
            Session sess = conn.openSession();
            sess.execCommand(command);
            InputStream stdout = new StreamGobbler(sess.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

            while (true){
                String line = br.readLine(); // read line
                if (line == null)
                    break;
                System.out.println(line);
                //output = line;
            }
            /* Show exit status, if available (otherwise "null") */
            System.out.println("ExitCode: " + sess.getExitStatus());
            sess.close(); // Close this session
            conn.close();
        }
        catch (IOException e)
        {
            System.out.printf(e.toString());
            e.printStackTrace(System.err);
            System.exit(2); }
    }

}