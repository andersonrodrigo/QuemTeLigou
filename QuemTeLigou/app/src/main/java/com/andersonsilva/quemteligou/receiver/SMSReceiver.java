package com.andersonsilva.quemteligou.receiver;

/**
 * Created by ander on 28/02/2017.
 */
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.andersonsilva.quemteligou.entity.Sms;
import com.andersonsilva.quemteligou.utils.SmsUtils;
import com.quemtimligou.anderonsilva.com.quemteligou.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Parse the SMS.
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        if (bundle != null)
        {
            // Retrieve the SMS.
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i=0; i<msgs.length; i++)
            {
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                // In case of a particular App / Service.
                //if(msgs[i].getOriginatingAddress().equals("+91XXX"))
                //{
                Sms sms = new Sms();
                sms.setMsg(msgs[i].getMessageBody().toString());
                if (sms.getMsg().indexOf("Te Ligou:") > -1) {
                    ContentResolver contentResolver = context.getContentResolver();
                    SmsUtils.preencheObjetoSms(contentResolver, new SimpleDateFormat("dd/MM/yyyy").format(new Date()), sms);
                    str = "Opa, " + sms.getNomeContato() + " Te ligou!!!";
                }
          /*      str += "SMS from " + msgs[i].getOriginatingAddress();
                str += " :";
                str += msgs[i].getMessageBody().toString();
                str += "n";*/

                //}
            }
            // Display the SMS as Toast.
           // Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
            if (!str.equals("")){
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                mBuilder.setSmallIcon(R.drawable.ic_launcher);
                mBuilder.setContentTitle("Alguem te ligou enquanto vc estava fora..");
                mBuilder.setContentText(str);
            }
        }
    }
}