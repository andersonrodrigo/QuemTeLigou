package com.andersonsilva.quemteligou.receiver;

/**
 * Created by ander on 28/02/2017.
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.andersonsilva.quemteligou.MainActivity;
import com.andersonsilva.quemteligou.R;
import com.andersonsilva.quemteligou.entity.Sms;
import com.andersonsilva.quemteligou.utils.SmsUtils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
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
            Sms sms = new Sms();
            for (int i=0; i<msgs.length; i++)
            {
                try {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    // In case of a particular App / Service.
                    //if(msgs[i].getOriginatingAddress().equals("+91XXX"))
                    //{

                    sms.setMsg(msgs[i].getMessageBody().toString());
                    if (sms.getMsg().indexOf("Te Ligou:") > -1) {
                        ContentResolver contentResolver = context.getContentResolver();
                        SmsUtils.preencheObjetoSms(contentResolver, new SimpleDateFormat("dd/MM/yyyy").format(new Date()), sms);
                        str = "Opa, " + sms.getNomeContato() + " Te ligou!!!";
                    } else if (sms.getMsg().indexOf("Torpedo a Cobrar") > -1) {
                        ContentResolver contentResolver = context.getContentResolver();
                        SmsUtils.preencheObjetoSmsTorpedoCobrar(contentResolver, new SimpleDateFormat("dd/MM/yyyy").format(new Date()), sms);
                        str = "Opa, " + sms.getNomeContato() + " Te Mandou um Torpedo a Cobrar!!!";
                    } else if (sms.getMsg().indexOf("TIM Avisa:") > -1) {
                        ContentResolver contentResolver = context.getContentResolver();
                        SmsUtils.preencheObjetoSms(contentResolver, new SimpleDateFormat("dd/MM/yyyy").format(new Date()), sms);
                        str = "Opa, " + sms.getNomeContato() + " Te ligou!!!";
                    } else if (sms.getMsg().indexOf("Seu Oi recebeu") > 1) {
                        ContentResolver contentResolver = context.getContentResolver();
                        SmsUtils.preencheObjetoSms(contentResolver, new SimpleDateFormat("dd/MM/yyyy").format(new Date()), sms);
                        str = "Opa, " + sms.getNomeContato() + " Te ligou!!!";
                    } else if (sms.getMsg().indexOf("Vivo Avisa:") > 1) {
                        ContentResolver contentResolver = context.getContentResolver();
                        SmsUtils.preencheObjetoSms(contentResolver, new SimpleDateFormat("dd/MM/yyyy").format(new Date()), sms);
                        str = "Opa, " + sms.getNomeContato() + " Te ligou!!!";
                    } else  if (sms.getMsg().indexOf("Seu celular tem") > -1  && sms.getMsg().indexOf(". CLARO")>-1){
                        ContentResolver contentResolver = context.getContentResolver();
                        SmsUtils.preencheObjetoSms(contentResolver, new SimpleDateFormat("dd/MM/yyyy").format(new Date()), sms);
                        str = "Opa, " + sms.getNomeContato() + " Te ligou!!!";
                    }

                }catch (Exception e){

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
                disparaNotificacao(str,sms,context);
            }
        }
    }

    public void disparaNotificacao(String str,Sms sms, Context context){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Te ligaram..")
                        .setContentText(str);
        if (sms.getImagemContato()!=null){
            Bitmap circleBitmap = Bitmap.createBitmap(sms.getImagemContato().getWidth(), sms.getImagemContato().getHeight(), Bitmap.Config.ARGB_8888);
            BitmapShader shader = new BitmapShader(sms.getImagemContato(),  Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Paint paint = new Paint();
            paint.setShader(shader);
            Canvas c = new Canvas(circleBitmap);
            c.drawCircle(sms.getImagemContato().getWidth()/2, sms.getImagemContato().getHeight()/2, sms.getImagemContato().getWidth()/2, paint);
            builder.setLargeIcon(circleBitmap);
        }
        Intent notificationIntent = new Intent(Intent.ACTION_DIAL);
        String numeroSugestao = sms.getNumeroTeLigou();
        if (numeroSugestao.length()==14){
            numeroSugestao = numeroSugestao.substring(5,14);
        }
        notificationIntent.setData(Uri.parse("tel:"+numeroSugestao));
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}