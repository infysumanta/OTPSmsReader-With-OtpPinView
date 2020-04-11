package com.sumanta.otpsmsreader;

import android.Manifest;
import android.app.Activity;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class SMSOtpVerify {

    final static int PERMISSION_REQUEST_CODE = 17;

    private Activity activity;
    private Fragment fragment;
    private OTPSmsListener<String> otpSmsListener;
    private OTPSmsReceiver otpSmsReceiver;
    private String ssid, ssidFilter;


    /**
     * Activity Constructor
     *
     * @param activity
     * @param otpSmsListener
     */
    public SMSOtpVerify(Activity activity, OTPSmsListener<String> otpSmsListener){
        this.activity = activity;
        this.otpSmsListener = otpSmsListener;
        otpSmsReceiver = new OTPSmsReceiver();
        otpSmsReceiver.setCallBack(this.otpSmsListener);
    }

    /**
     * Fragment Constructor
     *
     * @param activity
     * @param fragment
     * @param otpSmsListener
     */
    public SMSOtpVerify(Activity activity, Fragment fragment, OTPSmsListener<String> otpSmsListener){
        this(activity, otpSmsListener);
        this.fragment = fragment;
    }


    /**
     * Permission Checking Method
     *
     * @param activity
     * @param fragment
     * @return
     */
    public static boolean checkSMSPermission(Activity activity, Fragment fragment){
        if (Build.VERSION.SDK_INT >= 23){
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS)==PackageManager.PERMISSION_GRANTED){
                return true;
            }else {
                if (fragment == null){
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, PERMISSION_REQUEST_CODE);
                }else {
                    fragment.requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, PERMISSION_REQUEST_CODE);
                }
                return false;
            }
        }else {
            return true;
        }
    }

    /**
     * set Filter for SSID
     *
     * @param regexp
     */
    public void setSsidFilter(String regexp){
        this.ssidFilter = regexp;
    }

    /**
     * set SSID OF MESSAGE
     *
     * @param ssid
     */
    public void setSsid(String ssid){
        this.ssid = ssid;
    }


    /**
     * Activity Request Permision Methor
     *
     * @param code
     * @param permissions
     * @param result
     */
    public void onActivityRequestPermissions(int code, String[] permissions, int[] result) {
        switch (code) {
            case PERMISSION_REQUEST_CODE:
                if (result.length > 1 &&
                        result[0] == PackageManager.PERMISSION_GRANTED &&
                        result[1] == PackageManager.PERMISSION_GRANTED) {
                    setupReceiver();
                }
                break;
            default:
                break;
        }
    }

    /**
     * SMS Receiver Setup
     */
    private void setupReceiver() {
        otpSmsReceiver = new OTPSmsReceiver();
        otpSmsReceiver.setCallBack(otpSmsListener);
        otpSmsReceiver.setSenderIDFilter(ssid);
        otpSmsReceiver.setSmsFilter(ssidFilter);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        activity.registerReceiver(otpSmsReceiver, intentFilter);
    }

    /**
     *Activity OnStart Method
     */
    public void onStart() {
        if (checkSMSPermission(activity, fragment)) {
            setupReceiver();
        }
    }

    /**
     * Activity OnStop Method
     */
    public void onStop() {
        try {
            activity.unregisterReceiver(otpSmsReceiver);
        } catch (IllegalArgumentException ignore) {
            //receiver not registered
        }
    }

}
