package com.lambda.assist.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class Br_SMS extends BroadcastReceiver {

    private BRInteraction brInteraction;
    // SMS
    private static final String MSG_RECEIVED =
            "android.provider.Telephony.SMS_RECEIVED";

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(MSG_RECEIVED)) {
            Bundle msg = intent.getExtras();
            Object[] messages = (Object[]) msg.get("pdus");
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) messages[0]);

            String from = sms.getDisplayOriginatingAddress();
            String text = sms.getMessageBody();
            if (brInteraction != null) { //14~17
                String result = "0000";
                String sPwd = text.substring(13, 17);
//                try {
//                    int nPwd = Integer.valueOf(sPwd);
//                    if (1000 > nPwd && nPwd > 0) {
//                        result = Integer.toString(nPwd);
//                    }
//                } catch (NumberFormatException ex) {
//                }
                brInteraction.setText(sPwd);
            }
        }
    }

    public interface BRInteraction {
        void setText(String content);
    }

    public void setBRInteractionListener(BRInteraction brInteraction) {
        this.brInteraction = brInteraction;
    }
}
