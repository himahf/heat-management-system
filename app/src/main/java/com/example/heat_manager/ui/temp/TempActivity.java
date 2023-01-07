package com.example.heat_manager.ui.temp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.heat_manager.MainActivity;
import com.example.heat_manager.R;
import com.example.heat_manager.Reservation;
import com.example.heat_manager.ui.login.LoginActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class TempActivity extends AppCompatActivity {

    Spinner sp;
    boolean invalid = false;
    String Colector="";
    TextView txtalertName;
    TextView CurrentTemp;
    TextView UserPassword;
    TextView TxtCountDown;
    EditText UserContact;
    EditText UserComment;
    Button BtnStopHeat;
    Button BtnSetNew;
    RadioButton Malebtn,Femalbtn;
    CheckBox html,css,php;
    Button btnInDatePicker, btnInTimePicker,btnOutDatePicker, btnOutTimePicker;
    EditText txtInDate,txtInTime,txtOutDate,txtOutTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private int mYear1, mMonth1, mDay1, mHour1, mMinute1;

    int commonTempVal = 10;

    public double currentTemperature = 10;
    public double targetTemperature = 21;
    public long targetTime;
    public double specificVolume = 0.85;
    public double specificHeatCapacity = 1.005;
    public int heat = 1500;
    public double heatLoss;
    public double coefficientOfHeatTransfer = 0.5;
    public double heatingTime;
    public Connection connection;
    private Reservation reservation;
    public String commandOutput;
    public int counter;
    private boolean heaterOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.logo, null);
        actionBar.setCustomView(view);
//        sp=findViewById(R.id.SpCountry);
        CurrentTemp=(TextView) findViewById(R.id.userPassword1);
        UserPassword= (TextView) findViewById(R.id.userPassword);
//        UserContact=findViewById(R.id.userContact);
       // UserComment=findViewById(R.id.usercomment);
        txtalertName=findViewById(R.id.userAlert);
      //  Malebtn =findViewById(R.id.Male);
     //   Femalbtn=findViewById(R.id.Female);
       // html=findViewById(R.id.HTML);
     //   css=findViewById(R.id.CSS);
     //   php=findViewById(R.id.PHP);
        BtnStopHeat=findViewById(R.id.btnStopHeat);
        BtnSetNew=findViewById(R.id.btnSetNew);
        TxtCountDown=findViewById(R.id.countDown);
        // read temp sensor value
        //getTemp();

        Intent intent = getIntent();
        String HeatingTime = intent.getStringExtra("HeatingTime");
        String TargetTemp = intent.getStringExtra("TargetTemp");
        String CurrentTem = intent.getStringExtra("CurrentTemp");

        int Heating_Time = Integer.parseInt(HeatingTime)*1000;
        System.out.printf("Heating_Time"+Heating_Time);
        UserPassword.setText(TargetTemp);
        CurrentTemp.setText(CurrentTem);

        new CountDownTimer(Heating_Time, 1000) {

            public void onTick(long millisUntilFinished) {
                //TxtCountDown.setText("Time Remain = " +millisUntilFinished/1000 );
                // Used for formatting digit to be in 2 digits only

                NumberFormat f = new DecimalFormat("00");

                long hours = (millisUntilFinished / 3600000) % 24;
                long mins = (millisUntilFinished / 60000) % 60;
                long secs = (millisUntilFinished / 1000) % 60;

                TxtCountDown.setText(f.format(hours) + ":" + f.format(mins) + ":" + f.format(secs));
            }

            public void onFinish() {
                TxtCountDown.setText("Done!");
                createConnection("tdtool --on 2");
                heaterOn = true;
                BtnStopHeat.setText("RESUME HEATING");
            }
        }.start();


        BtnSetNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TempActivity.this, MainActivity.class);
                startActivity(intent);
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
        //reservation.NoOfPeople = 3;
        reservation.ObjectCount = 5;
        String startDate = "2022-12-18T06:30:38.9933333"; // Input String for testing
        //reservation.CheckinDate = new SimpleDateFormat("dd/MM/yyyy").parse(startDate,new ParsePosition(0));
        //targetTime = reservation.CheckinDate.getTime();

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
                commandOutput = line;
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

    // get temperature
    public void getTemp(){
        createConnection("python IOT_Project/TempSen17.py");
        commandOutput = commandOutput.replace("\"","");
        currentTemperature = Double.parseDouble(commandOutput);
        CurrentTemp.setText(Double.toString(currentTemperature));
    }

    // get turn off
    public void clickStopHeat(View v){

        if(heaterOn){
            createConnection("tdtool --off 2");
        }
        else{
            createConnection("tdtool --on 2");
        }

    }

}