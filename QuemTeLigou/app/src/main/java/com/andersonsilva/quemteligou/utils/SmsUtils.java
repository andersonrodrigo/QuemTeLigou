package com.andersonsilva.quemteligou.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Settings;

import com.andersonsilva.quemteligou.entity.Sms;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by anderson.silva on 14/02/2017.
 */
public class SmsUtils {


    private  List<Sms> sms_All;


    static NumberFormat formato2 = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));






    /**
     *
     * @param lista
     * @param ordem
     */
    public static void ordenaListaValorData(List<Sms> lista,final int ordem){
        Collections.sort(lista,(new Comparator<Sms>() {
            @Override
            public int compare(Sms sms, Sms t1) {
                Date data1 = null;
                Date data2 = null;
                try{
                    data1 = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(sms.getDataCompra());
                    data2 = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(t1.getDataCompra());
                    return data1.compareTo(data2) * ordem;
                }catch (Exception e){
                    return 0;
                }

            }
        }));
    }


    /**
     * Formata do formato mes ano
     *
     * @param listaSms
     */
    private static void formataDataParaExibicao(List<Sms> listaSms) {
        for(Sms s:listaSms){
            try {
                s.setDataCompra(new SimpleDateFormat("MM/yyyy").format(new SimpleDateFormat("dd/MM/yyyy").parse(s.getDataCompra())));
            }catch (Exception e){

            }
        }
    }





    /**
     *
     * @return
     */
    public static ArrayList<Sms> getAllSms(ContentResolver cr ) {
        try {
            ArrayList<Sms> listaSms = new ArrayList<Sms>();
            Uri message = Uri.parse("content://sms/");
            String[] reqCols = new String[] { "_id", "address", "body","date" };
            Cursor c = cr.query(message, reqCols, null, null, null);
            //  startManagingCursor(c);
            int totalSMS = c.getCount();
            if (c.moveToFirst()) {
                for (int i = 0; i < totalSMS; i++) {
                    Sms objSms = new Sms();
                    objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));

                    if (objSms.getMsg() != null) {

                        if (objSms.getMsg().indexOf("Te Ligou:") > -1) {
                            try {
                                objSms.setNumeroTeLigou(objSms.getMsg().substring(objSms.getMsg().indexOf("<")+1,objSms.getMsg().indexOf(">")));
                                objSms.setDataLigacao(objSms.getMsg().substring(objSms.getMsg().indexOf(">")+2,objSms.getMsg().indexOf(">")+7));
                                objSms.setHoraLigacao(objSms.getMsg().substring(objSms.getMsg().indexOf(">")+7,objSms.getMsg().indexOf(">")+13));

                                String data = c.getString(c.getColumnIndexOrThrow("date"));
                                Calendar d = Calendar.getInstance();
                                d.setTimeInMillis(Long.valueOf(data));
                                String ano = (new SimpleDateFormat("yyyy").format(d.getTime()));
                                objSms.setDataLigacao(objSms.getDataLigacao()+"/"+ano);
                                String numeroBase = objSms.getNumeroTeLigou();
                                if (numeroBase.length() == 14){
                                    numeroBase = numeroBase.substring(6,14);
                                }
                                objSms.setNomeContato(recuperaNomeContato(cr,numeroBase));
                                listaSms.add(objSms);
                            } catch (Exception e) {

                            }

                        }


                    }
                    c.moveToNext();

                }

            }
            c.close();
            return listaSms;
        }catch(Exception e){
            return new ArrayList<Sms>();
        }
    }

    /**
     *
     * @param numero
     * @return
     */
    private static String recuperaNomeContato(ContentResolver cr,String numero){
        try {
            String[] PROJECTION = new String[] { ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER };

            Cursor c = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, null);
            if (c.moveToFirst()) {
                String clsPhonename = null;
                String clsphoneNo = null;

                do {
                    clsPhonename =       c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    clsphoneNo = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String numeroBase = clsphoneNo.replace("-","");
                    if (numeroBase.indexOf(numero)>-1) {
                        return clsPhonename;
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recuperaNomeContatoSIM(cr,numero);

    }

private static String recuperaNomeContatoSIM(ContentResolver cr,String numero){
    try {
        String clsSimPhonename = null;
        String clsSimphoneNo = null;

        Uri simUri = Uri.parse("content://icc/adn");
        Cursor cursorSim = cr.query(simUri, null,
                null, null, null);
        while (cursorSim.moveToNext()) {
            clsSimPhonename = cursorSim.getString(cursorSim.getColumnIndex("name"));
            clsSimphoneNo = cursorSim.getString(cursorSim.getColumnIndex("number"));
            String numeroBase = clsSimphoneNo.replace("-","");
            if (numeroBase.indexOf(numero)>-1) {
                return clsSimPhonename;
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return "Não encontrado nos Contatos";
}

}

