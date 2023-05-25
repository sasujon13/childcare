package com.cheradip.childcare;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_WIFI_STATE = 2;

    private EditText mobileNumberEditText;
    private Button loginButton;

    private Button replaceButton;

    private Button registerButton;
    private TextView replaceTextView;
    private TextView replaceTextView2;
    private TextView registerTextView;
    private TextView registerTextView2;

    private String imei;
    private String macAddress;

    private HashMap<String, String> userCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        mobileNumberEditText = findViewById(R.id.phoneNumberEditText);
        loginButton = findViewById(R.id.loginButton);
        replaceButton = findViewById(R.id.replaceButton);
        registerButton = findViewById(R.id.registerButton);
        registerTextView = findViewById(R.id.registerTextView);
        registerTextView2 = findViewById(R.id.registerTextView2);
        replaceTextView = findViewById(R.id.replaceTextView);
        replaceTextView2 = findViewById(R.id.replaceTextView2);

        // Initialize user credentials (replace with your own data)
        userCredentials = new HashMap<>();
        userCredentials.put("1234567890", "abcdef"); // mobileNumber -> IMEI/Mac

        // Request permissions for accessing phone state and Wi-Fi state
        requestPhoneStatePermission();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the onLoginClick method when the login button is clicked
                onLoginClick(v);
            }
        });
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
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    imei = telephonyManager.getImei(0); // Slot index 0 for SIM1
                } else {
                    imei = telephonyManager.getDeviceId(); // Deprecated in Android 10 (API level 29)
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

        // Enable login button after retrieving IMEI and MAC address
        loginButton.setEnabled(true);
    }
    public void onLoginClick(View view) {
        String mobileNumber = mobileNumberEditText.getText().toString().trim();

        if (mobileNumber.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please enter your Registered Phone Number", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, loginButton.getHeight());
            View toastView = toast.getView();
            ((LinearLayout) toastView).setGravity(Gravity.CENTER_HORIZONTAL);
            toast.show();
            return;
        }

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://your_mysql_host:your_mysql_port/your_database", "username", "password");
            String sql = null;
            if (connection != null) {
                sql = "SELECT imei_or_mac FROM user_credentials WHERE mobile_number = ?";
            }
            else {
                Toast toast = Toast.makeText(getApplicationContext(), "Please check your Internet/Database connection!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, loginButton.getHeight());
                View toastView = toast.getView();
                TextView toastText = toastView.findViewById(android.R.id.message);
                toastText.setTextColor(Color.parseColor("#FF018786"));
                ((LinearLayout) toastView).setGravity(Gravity.CENTER_HORIZONTAL);
                toast.show();
            }
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, mobileNumber);
            // Execute the query
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String savedIMEIorMac = resultSet.getString("imei_or_mac");
                if (savedIMEIorMac.equals(imei) || savedIMEIorMac.equals(macAddress)) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, loginButton.getHeight());
                    View toastView = toast.getView();
                    TextView toastText = toastView.findViewById(android.R.id.message);
                    toastText.setTextColor(Color.parseColor("#FF018786"));
                    ((LinearLayout) toastView).setGravity(Gravity.CENTER_HORIZONTAL);
                    toast.show();
                    Intent intent = new Intent(this, DashboardActivity.class);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Invalid IMEI or MAC address! Click on Replace button to add a new device replacing the Previous one.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, loginButton.getHeight());
                    View toastView = toast.getView();
                    TextView toastText = toastView.findViewById(android.R.id.message);
                    toastText.setTextColor(Color.parseColor("#FF018786"));
                    ((LinearLayout) toastView).setGravity(Gravity.CENTER_HORIZONTAL);
                    toast.show();
                    replaceTextView.setVisibility(View.VISIBLE);
                    replaceTextView2.setVisibility(View.VISIBLE);
                    registerButton.setVisibility(View.GONE);
                    replaceButton.setVisibility(View.VISIBLE);
                    registerTextView.setVisibility(View.GONE);
                    registerTextView2.setVisibility(View.GONE);
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Your Number is not Registered! Please click on Register button.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, loginButton.getHeight());
                View toastView = toast.getView();
                TextView toastText = toastView.findViewById(android.R.id.message);
                toastText.setTextColor(Color.parseColor("#FF018786"));
                ((LinearLayout) toastView).setGravity(Gravity.CENTER_HORIZONTAL);

                // Show the toast
                toast.show();
                replaceTextView.setVisibility(View.GONE);
                replaceTextView2.setVisibility(View.GONE);
                registerButton.setVisibility(View.VISIBLE);
                replaceButton.setVisibility(View.GONE);
                registerTextView.setVisibility(View.VISIBLE);
                registerTextView2.setVisibility(View.VISIBLE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the database connection error
        } finally {
            if (connection != null) {
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


//package com.cheradip.childcare;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Color;
//import android.net.wifi.WifiInfo;
//import android.net.wifi.WifiManager;
//import android.os.Bundle;
//import android.telephony.TelephonyManager;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.HashMap;
//
//public class MainActivity extends AppCompatActivity {
//
//    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
//    private static final int PERMISSIONS_REQUEST_ACCESS_WIFI_STATE = 2;
//
//    private EditText mobileNumberEditText;
//    private Button loginButton;
//
//    private Button replaceButton;
//
//    private Button registerButton;
//    private TextView replaceTextView;
//    private TextView replaceTextView2;
//    private TextView registerTextView;
//    private TextView registerTextView2;
//
//    private String imei;
//    private String macAddress;
//
//    private HashMap<String, String> userCredentials;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Initialize views
//        mobileNumberEditText = findViewById(R.id.phoneNumberEditText);
//        loginButton = findViewById(R.id.loginButton);
//        replaceButton = findViewById(R.id.replaceButton);
//        registerButton = findViewById(R.id.registerButton);
//        registerTextView = findViewById(R.id.registerTextView);
//        registerTextView2 = findViewById(R.id.registerTextView2);
//        replaceTextView = findViewById(R.id.replaceTextView);
//        replaceTextView2 = findViewById(R.id.replaceTextView2);
//
//        // Initialize user credentials (replace with your own data)
//        userCredentials = new HashMap<>();
//        userCredentials.put("1234567890", "abcdef"); // mobileNumber -> IMEI/Mac
//
//        // Request permissions for accessing phone state and Wi-Fi state
//        requestPhoneStatePermission();
//
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Call the onLoginClick method when the login button is clicked
//                onLoginClick(v);
//            }
//        });
//    }
//
//    private void requestPhoneStatePermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_READ_PHONE_STATE);
//        } else {
//            // Permission already granted, retrieve IMEI
//            getIMEI();
//        }
//    }
//
//    private void requestWifiStatePermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, PERMISSIONS_REQUEST_ACCESS_WIFI_STATE);
//        } else {
//            // Permission already granted, retrieve MAC address
//            getMacAddress();
//        }
//    }
//
//    private void getIMEI() {
//        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        if (telephonyManager != null) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    imei = telephonyManager.getImei(0); // Slot index 0 for SIM1
//                } else {
//                    imei = telephonyManager.getDeviceId(); // Deprecated in Android 10 (API level 29)
//                }
//            }
//        }
//
//        // Request permissions for accessing Wi-Fi state
//        requestWifiStatePermission();
//    }
//
//    private void getMacAddress() {
//        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        if (wifiManager != null) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {
//                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//                macAddress = wifiInfo.getMacAddress();
//            }
//        }
//
//        // Enable login button after retrieving IMEI and MAC address
//        loginButton.setEnabled(true);
//    }
//    public void onLoginClick(View view) {
//        String mobileNumber = mobileNumberEditText.getText().toString().trim();
//
//        if (mobileNumber.isEmpty()) {Toast toast = Toast.makeText(getApplicationContext(), "Please enter your Registered Phone Number", Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, loginButton.getHeight());
//            View toastView = toast.getView();
//            ((LinearLayout) toastView).setGravity(Gravity.CENTER_HORIZONTAL);
//            toast.show();
//            return;
//        }
//
//        Connection connection = null;
//        try {
//            connection = DriverManager.getConnection("jdbc:mysql://your_mysql_host:your_mysql_port/your_database", "username", "password");
//            String sql = "SELECT imei_or_mac FROM user_credentials WHERE mobile_number = ?";
//            PreparedStatement statement = connection.prepareStatement(sql);
//            statement.setString(1, mobileNumber);
//            // Execute the query
//            ResultSet resultSet = statement.executeQuery();
//
//            if (resultSet.next()) {
//                String savedIMEIorMac = resultSet.getString("imei_or_mac");
//                if (savedIMEIorMac.equals(imei) || savedIMEIorMac.equals(macAddress)) {
//                    Toast toast = Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, loginButton.getHeight());
//                    View toastView = toast.getView();
//                    TextView toastText = toastView.findViewById(android.R.id.message);
//                    toastText.setTextColor(Color.parseColor("#FF018786"));
//                    ((LinearLayout) toastView).setGravity(Gravity.CENTER_HORIZONTAL);
//                    toast.show();
//                    Intent intent = new Intent(this, DashboardActivity.class);
//                    startActivity(intent);
//                } else {
//                    Toast toast = Toast.makeText(getApplicationContext(), "Invalid IMEI or MAC address! Click on Replace button to add a new device replacing the Previous one.", Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, loginButton.getHeight());
//                    View toastView = toast.getView();
//                    TextView toastText = toastView.findViewById(android.R.id.message);
//                    toastText.setTextColor(Color.parseColor("#FF018786"));
//                    ((LinearLayout) toastView).setGravity(Gravity.CENTER_HORIZONTAL);
//                    toast.show();
//                    replaceTextView.setVisibility(View.VISIBLE);
//                    replaceTextView2.setVisibility(View.VISIBLE);
//                    registerButton.setVisibility(View.GONE);
//                    replaceButton.setVisibility(View.VISIBLE);
//                    registerTextView.setVisibility(View.GONE);
//                    registerTextView2.setVisibility(View.GONE);
//                }
//            } else {
//                Toast toast = Toast.makeText(getApplicationContext(), "Your Number is not Registered! Please click on Register button.", Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, loginButton.getHeight());
//                View toastView = toast.getView();
//                TextView toastText = toastView.findViewById(android.R.id.message);
//                toastText.setTextColor(Color.parseColor("#FF018786"));
//                // Set the horizontal gravity to center
//                ((LinearLayout) toastView).setGravity(Gravity.CENTER_HORIZONTAL);
//
//                // Show the toast
//                toast.show();
//                replaceTextView.setVisibility(View.GONE);
//                replaceTextView2.setVisibility(View.GONE);
//                registerButton.setVisibility(View.VISIBLE);
//                replaceButton.setVisibility(View.GONE);
//                registerTextView.setVisibility(View.VISIBLE);
//                registerTextView2.setVisibility(View.VISIBLE);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            // Handle the database connection error
//        } finally {
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case PERMISSIONS_REQUEST_READ_PHONE_STATE: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Permission granted, retrieve IMEI
//                    getIMEI();
//                } else {
//                    // Permission denied, handle accordingly (e.g., show an error message)
//                }
//                break;
//            }
//            case PERMISSIONS_REQUEST_ACCESS_WIFI_STATE: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Permission granted, retrieve MAC address
//                    getMacAddress();
//                } else {
//                    // Permission denied, handle accordingly (e.g., show an error message)
//                }
//                break;
//            }
//        }
//    }
//}
