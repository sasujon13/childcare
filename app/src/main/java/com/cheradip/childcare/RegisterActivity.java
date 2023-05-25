package com.cheradip.childcare;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class RegisterActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_WIFI_STATE = 2;

    private String imei;
    private String macAddress;

    private EditText childsFullNameEditText;
    private EditText yourMobileNumberEditText;
    private Spinner daySpinner;
    private Spinner monthSpinner;
    private Spinner yearSpinner;

    private static final int START_YEAR = Calendar.getInstance().get(Calendar.YEAR);
    private static final int END_YEAR = START_YEAR - 100;

    private List<String> dayOptions;
    private List<String> monthOptions;
    private List<String> yearOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        childsFullNameEditText = findViewById(R.id.childsFullNameEditText);
        yourMobileNumberEditText = findViewById(R.id.yourMobileNumberEditText);
        daySpinner = findViewById(R.id.daySpinner);
        monthSpinner = findViewById(R.id.monthSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);

        setupDaySpinner();
        setupMonthSpinner();
        setupYearSpinner();

        // Set up your other views and functionality
        // ...
        // Request permissions for accessing phone state and Wi-Fi state
        requestPhoneStatePermission();
    }

    private void setupDaySpinner() {
        dayOptions = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            dayOptions.add(String.valueOf(i));
        }

        ArrayAdapter<String> daySpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dayOptions);
        daySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(daySpinnerAdapter);
    }

    private void setupMonthSpinner() {
        monthOptions = new ArrayList<>();
        monthOptions.add("January");
        monthOptions.add("February");
        monthOptions.add("March");
        monthOptions.add("April");
        monthOptions.add("May");
        monthOptions.add("June");
        monthOptions.add("July");
        monthOptions.add("August");
        monthOptions.add("September");
        monthOptions.add("October");
        monthOptions.add("November");
        monthOptions.add("December");

        ArrayAdapter<String> monthSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monthOptions);
        monthSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthSpinnerAdapter);
    }

    private void setupYearSpinner() {
        yearOptions = new ArrayList<>();
        for (int year = START_YEAR; year >= END_YEAR; year--) {
            yearOptions.add(String.valueOf(year));
        }

        ArrayAdapter<String> yearSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearOptions);
        yearSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearSpinnerAdapter);
    }
    private void requestPhoneStatePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_READ_PHONE_STATE);
        } else {
            // Permission already granted, retrieve IMEI
            getIMEI();
        }
    }

    private void requestWifiStatePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, PERMISSIONS_REQUEST_ACCESS_WIFI_STATE);
        } else {
            // Permission already granted, retrieve MAC address
            getMacAddress();
        }
    }

    private void getIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    imei = telephonyManager.getImei();
                }
            }
        }

        // Request permissions for accessing Wi-Fi state
        requestWifiStatePermission();
    }

    private void getMacAddress() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                macAddress = wifiInfo.getMacAddress();
            }
        }

        // Call your user registration method here and pass the imei and macAddress values
        registerUser(imei, macAddress);
    }

    private void registerUser(String imei, String macAddress) {
        // Retrieve the user-entered data
        EditText childsFullNameEditText = findViewById(R.id.childsFullNameEditText);
        EditText yourMobileNumberEditText = findViewById(R.id.yourMobileNumberEditText);
        Spinner daySpinner = findViewById(R.id.daySpinner);
        Spinner monthSpinner = findViewById(R.id.monthSpinner);
        Spinner yearSpinner = findViewById(R.id.yearSpinner);

        String childsFullName = childsFullNameEditText.getText().toString();
        String mobileNumber = yourMobileNumberEditText.getText().toString();
        String day = daySpinner.getSelectedItem().toString();
        String month = monthSpinner.getSelectedItem().toString();
        String year = yearSpinner.getSelectedItem().toString();

        // Create a connection to the MySQL database
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://your-database-url", "username", "password");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Insert the user data into the database
        if (connection != null) {
            try {
                // Prepare the SQL query
                String query = "INSERT INTO users (full_name, mobile_number, day, month, year, imei, mac_address) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, childsFullName);
                statement.setString(2, mobileNumber);
                statement.setString(3, day);
                statement.setString(4, month);
                statement.setString(5, year);
                statement.setString(6, imei);
                statement.setString(7, macAddress);

                // Execute the query
                statement.executeUpdate();

                // Close the statement
                statement.close();

                // Optionally, you can display a success message or navigate to a new screen
                // upon successful registration

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // Close the database connection
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, retrieve IMEI
                    getIMEI();
                } else {
                    // Permission denied, handle accordingly (e.g., show an error message)
                }
                break;
            }
            case PERMISSIONS_REQUEST_ACCESS_WIFI_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, retrieve MAC address
                    getMacAddress();
                } else {
                    // Permission denied, handle accordingly (e.g., show an error message)
                }
                break;
            }
        }
    }
}
