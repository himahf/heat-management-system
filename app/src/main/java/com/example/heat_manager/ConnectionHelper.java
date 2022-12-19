package com.example.heat_manager;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelper {
    private static String ip = "172.1.1.0";
    private static String port = "1433";
    private static String Classes = "net.sourceforge.jtds.jdbc.Driver";
    private static String database = "Reservations";
    private static String username = "KALPANA\\pubun";
    private static String password = "";
    private static String url = "jdbc:jtds:sqlserver://"+ip+":"+port+"/"+database;
    private Connection connection;

    @SuppressLint("NewApi")
    public Connection setConnection(){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Class.forName(Classes);
            connection = DriverManager.getConnection(url, username,password);
            //textView.setText("SUCCESS");
            Log.e("connection","success");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("connection","error");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("connection","fail");
        }

        return connection;
    }
}
