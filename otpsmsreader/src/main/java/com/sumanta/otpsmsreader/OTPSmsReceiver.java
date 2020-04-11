package com.sumanta.otpsmsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class OTPSmsReceiver extends BroadcastReceiver {

    private String senderIDFilter, smsFilter;
    private OTPSmsListener<String> otpSmsListener;

    /**
     * OTP SMS Callback Function
     * @param callBack
     */
    public void setCallBack(OTPSmsListener<String> callBack){
        this.otpSmsListener = callBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        try{
            if (bundle !=null){
                final Object[] objects = (Object[]) bundle.get("pdus");
                for (int i = 0; i< objects.length; i++){
                    SmsMessage smsMessage = readOTPMessages(objects[i], bundle);
                    String ssid = smsMessage.getDisplayOriginatingAddress();

                    if (senderIDFilter != null && !ssid.equals(senderIDFilter)){
                        return;
                    }

                    String msg = smsMessage.getDisplayMessageBody();
                    if (smsFilter != null && msg.matches(smsFilter)){
                        return;
                    }

                    if (otpSmsListener != null){
                        otpSmsListener.OnSmsReader(msg);
                    }
                }
            }
        }catch (Exception e){
            Log.e("SMSOnReceiveError", e.getMessage());
        }
    }


    /**
     * Read OTP Message from SMS Listener
     * @param object
     * @param bundle
     * @return
     */
    private SmsMessage readOTPMessages(Object object, Bundle bundle){
        SmsMessage smsMessage;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            String sms = bundle.getString("sms");
            smsMessage  = SmsMessage.createFromPdu((byte[]) object, sms);
        }else {
            smsMessage  = SmsMessage.createFromPdu((byte[]) object);
        }
        return smsMessage;
    }

    /**
     * Set SMS Sender ID Filter (QP-SUMNTA)
     *
     * @param senderIDFilter
     */
    public void setSenderIDFilter(String senderIDFilter) {
        this.senderIDFilter = senderIDFilter;
    }

    /**
     * Set SMS Filter With Regular Expression
     * Ex - Your OTP is 4545 = {\\b\\d{4}\\b}
     *
     * @param smsFilterRegexp
     */
    public void setSmsFilter(String smsFilterRegexp) {
        this.smsFilter = smsFilterRegexp;
    }

}
