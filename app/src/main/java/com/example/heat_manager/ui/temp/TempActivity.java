package com.example.heat_manager.ui.temp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.heat_manager.MainActivity;
import com.example.heat_manager.R;
import com.example.heat_manager.Reservation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class TempActivity extends AppCompatActivity {

    TextView txtalertName;
    TextView CurrentTemp;
    TextView TargetTemp;
    TextView TxtCountDown;
    TextView TxtHeatingCountDown;
    Button BtnStopHeat;
    Button BtnSetNew;

    public double currentTemperature = 10;
    public double targetTemperature = 21;
    public double specificVolume = 0.85;
    public double specificHeatCapacity = 1.005;
    public int heat = 1500;
    public double heatLoss;
    public double coefficientOfHeatTransfer = 0.5;
    public double heatingTime;
    private Reservation reservation;
    public String commandOutput;
    private boolean heaterOn = false;
    private boolean startCounter = false;
    private CountDownTimer countDownTimer;
    private CountDownTimer heatingCountDownTimer;
    String HeatingTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_temp);
            ActionBar actionBar = getSupportActionBar();

            actionBar.setDisplayShowCustomEnabled(true);
            LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.logo, null);
            actionBar.setCustomView(view);

            CurrentTemp=(TextView) findViewById(R.id.userPassword1);
            TargetTemp= (TextView) findViewById(R.id.TargetTemp);

            txtalertName=findViewById(R.id.userAlert);

            BtnStopHeat=findViewById(R.id.btnStopHeat);
            BtnSetNew=findViewById(R.id.btnSetNew);
            TxtCountDown=findViewById(R.id.countDown);
            TxtHeatingCountDown = findViewById(R.id.countDown2);

            Intent intent = getIntent();
            String RemainingSeconds = intent.getStringExtra("RemainingSeconds");
            String TargetTem = intent.getStringExtra("TargetTemp");
            String CurrentTem = intent.getStringExtra("CurrentTemp");
            HeatingTime = intent.getStringExtra("HeatingTime");

            int Heating_Time = Integer.parseInt(RemainingSeconds)*1000;
            System.out.printf("Heating_Time"+Heating_Time);
            TargetTemp.setText(TargetTem);
            CurrentTemp.setText(CurrentTem);

            countDownTimer = new CountDownTimer(Heating_Time, 1000) {

                public void onTick(long millisUntilFinished) {

                    // Used for formatting digit to be in 2 digits only
                    NumberFormat f = new DecimalFormat("00");

                    long hours = (millisUntilFinished / 3600000) % 24;
                    long mins = (millisUntilFinished / 60000) % 60;
                    long secs = (millisUntilFinished / 1000) % 60;

                    startCounter = true;
                    TxtCountDown.setText(f.format(hours) + ":" + f.format(mins) + ":" + f.format(secs));
                }

                public void onFinish() {
                    TxtCountDown.setText("Done!");
                    // start heating process and counter
                    createConnection("tdtool --on 2");
                    heaterOn = true;
                    startCounter = false;
                    heatingTimeCountdown();
                }
            }.start();


            BtnSetNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createConnection("tdtool --off 2");
                    heaterOn = false;

                    Intent intent = new Intent(TempActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });


        }
        catch(Exception e) {

        }

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
        reservation.ObjectCount = 5;
        String startDate = "2022-12-18T06:30:38.9933333"; // Input String for testing

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
            BtnStopHeat.setText("RESUME HEATING");
            heaterOn = false;
        }

        else{
            createConnection("tdtool --on 2");
            BtnStopHeat.setText("STOP HEATING");
            heaterOn = true;
        }

    }

    // heating time countdown
    public  void heatingTimeCountdown(){
        int Heating_Time = Integer.parseInt(HeatingTime)*1000;
        heatingCountDownTimer = new CountDownTimer(Heating_Time, 1000) {

            public void onTick(long millisUntilFinished) {
                // Used for formatting digit to be in 2 digits only
                NumberFormat f = new DecimalFormat("00");

                long hours = (millisUntilFinished / 3600000) % 24;
                long mins = (millisUntilFinished / 60000) % 60;
                long secs = (millisUntilFinished / 1000) % 60;

                startCounter = true;
                TxtHeatingCountDown.setText(f.format(hours) + ":" + f.format(mins) + ":" + f.format(secs));
            }

            public void onFinish() {
                TxtHeatingCountDown.setText("Heating process done!");
                createConnection("tdtool --off 2");
                BtnStopHeat.setText("RESUME HEATING");
                heaterOn = false;
                startCounter = false;

                // update new temperature
                getTemp();
            }
        }.start();

    }
}
