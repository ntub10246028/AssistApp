package com.lambda.assist.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class Br_SMS extends BroadcastReceiver {
    public interface BRInteraction {
        void setText(String content);
    }

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
            if (brInteraction != null && text != null && !text.isEmpty() && text.length() == 6) { //14~17
                brInteraction.setText(text);
            }
        }
    }


    public void setBRInteractionListener(BRInteraction brInteraction) {
        this.brInteraction = brInteraction;
    }
}
