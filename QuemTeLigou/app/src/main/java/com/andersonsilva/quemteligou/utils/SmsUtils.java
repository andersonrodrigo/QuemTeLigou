package com.andersonsilva.quemteligou.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;

import com.andersonsilva.quemteligou.entity.Sms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
                                String data = c.getString(c.getColumnIndexOrThrow("date"));
                                if (objSms.getMsg().indexOf("<")>-1) {
                                    preencheObjetoSms(cr, data, objSms);
                                    listaSms.add(objSms);
                                }
                            } catch (Exception e) {

                            }
//TIM Avisa: <04131983798686>
                        }else  if (objSms.getMsg().indexOf("TIM Avisa:") > -1) {
                            try {
                                objSms.setNumeroTeLigou(objSms.getMsg().substring(objSms.getMsg().indexOf("<")+1,objSms.getMsg().indexOf(">")));
                                objSms.setDataLigacao(objSms.getMsg().substring(objSms.getMsg().indexOf("dia")+4,objSms.getMsg().indexOf("dia")+9));
                                objSms.setHoraLigacao(objSms.getMsg().substring(objSms.getMsg().indexOf("as")+3,objSms.getMsg().indexOf("as")+8));
                                Calendar d = Calendar.getInstance();
                                d.setTimeInMillis(Long.valueOf(c.getString(c.getColumnIndexOrThrow("date"))));
                                String ano = (new SimpleDateFormat("yyyy").format(d.getTime()));
                                objSms.setDataLigacao(objSms.getDataLigacao()+"/"+ano);
                                String numeroBase = objSms.getNumeroTeLigou();
                                if (numeroBase.length() == 14){
                                    numeroBase = numeroBase.substring(6,14);
                                }
                                recuperaNomeContato(cr,numeroBase,objSms);
                                listaSms.add(objSms);
                            } catch (Exception e) {

                            }

                        }else  if (objSms.getMsg().indexOf("Seu Oi recebeu") > -1) {
                            try {
                                objSms.setNumeroTeLigou(objSms.getMsg().substring(objSms.getMsg().indexOf("Seu Oi recebeu ligacoes de:") + 28, objSms.getMsg().indexOf("Seu Oi recebeu ligacoes de:") + 27 + 13));
                                objSms.setDataLigacao(objSms.getMsg().substring(objSms.getMsg().indexOf("Seu Oi recebeu ligacoes de:") + 27 + 13, objSms.getMsg().indexOf("Seu Oi recebeu ligacoes de:") + 27 + 13 + 6));
                                objSms.setHoraLigacao(objSms.getMsg().substring(objSms.getMsg().indexOf("Seu Oi recebeu ligacoes de:") + 27 + 13 + 6, objSms.getMsg().indexOf("Seu Oi recebeu ligacoes de:") + 27 + 13 + 6 + 6));
                                Calendar d = Calendar.getInstance();
                                d.setTimeInMillis(Long.valueOf(c.getString(c.getColumnIndexOrThrow("date"))));
                                String ano = (new SimpleDateFormat("yyyy").format(d.getTime()));
                                objSms.setDataLigacao(objSms.getDataLigacao() + "/" + ano);
                                String numeroBase = objSms.getNumeroTeLigou();
                                if (numeroBase.length() == 12) {
                                    numeroBase = numeroBase.substring(3, 12);
                                }
                                recuperaNomeContato(cr, numeroBase, objSms);
                                listaSms.add(objSms);
                            } catch (Exception e) {

                            }
                            //"Vivo Avisa: Voce recebeu 1 ligacao de: 01531996365970 em 20/02 as 13:24."
//objSms.setMsg("Vivo Avisa: Voce recebeu 1 ligacao de: 01531993068958 em 20/02 as 13:24.");
                        }else if (objSms.getMsg().indexOf("Vivo Avisa:") > -1){
                            try {
                                objSms.setNumeroTeLigou(objSms.getMsg().substring(objSms.getMsg().indexOf(" de: ") + 5, objSms.getMsg().indexOf(" em ")));
                                objSms.setDataLigacao(objSms.getMsg().substring(objSms.getMsg().indexOf(" em ") +4, objSms.getMsg().indexOf(" as ")));
                                objSms.setHoraLigacao(objSms.getMsg().substring(objSms.getMsg().indexOf(" as ") + 4, objSms.getMsg().indexOf(" as ") + 4 + 5));
                                Calendar d = Calendar.getInstance();
                                d.setTimeInMillis(Long.valueOf(c.getString(c.getColumnIndexOrThrow("date"))));
                                String ano = (new SimpleDateFormat("yyyy").format(d.getTime()));
                                objSms.setDataLigacao(objSms.getDataLigacao() + "/" + ano);
                                String numeroBase = objSms.getNumeroTeLigou();
                                if (numeroBase.length() == 14) {
                                    numeroBase = numeroBase.substring(5, 14);
                                }
                                recuperaNomeContato(cr, numeroBase, objSms);
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
     * @param cr
     * @param objSms
     *  TIM Avisa: <04131983798686> te ligou, dia 02/03 as 14:44 powered by Truecaller
     */
    public static void preencheObjetoSms(ContentResolver cr,String data,Sms objSms){
        try {
            if (objSms.getMsg().indexOf("TIM Avisa:") > -1) {
                objSms.setNumeroTeLigou(objSms.getMsg().substring(objSms.getMsg().indexOf("<") + 1, objSms.getMsg().indexOf(">")));
                objSms.setDataLigacao(objSms.getMsg().substring(objSms.getMsg().indexOf("dia ") + 4, objSms.getMsg().indexOf(" as ")));
                objSms.setHoraLigacao(objSms.getMsg().substring(objSms.getMsg().indexOf(" as ") + 4, objSms.getMsg().indexOf(" as ") + 5));
                String ano = (new SimpleDateFormat("yyyy").format(new Date()));
                objSms.setDataLigacao(objSms.getDataLigacao() + "/" + ano);
                String numeroBase = objSms.getNumeroTeLigou();
                if (numeroBase.length() == 14) {
                    numeroBase = numeroBase.substring(6, 14);
                }
                recuperaNomeContato(cr, numeroBase, objSms);
            } else if (objSms.getMsg().indexOf("Seu Oi recebeu") > -1) {
                try {
                    objSms.setNumeroTeLigou(objSms.getMsg().substring(objSms.getMsg().indexOf("Seu Oi recebeu ligacoes de:") + 28, objSms.getMsg().indexOf("Seu Oi recebeu ligacoes de:") + 27 + 13));
                    objSms.setDataLigacao(objSms.getMsg().substring(objSms.getMsg().indexOf("Seu Oi recebeu ligacoes de:") + 27 + 13, objSms.getMsg().indexOf("Seu Oi recebeu ligacoes de:") + 27 + 13 + 6));
                    objSms.setHoraLigacao(objSms.getMsg().substring(objSms.getMsg().indexOf("Seu Oi recebeu ligacoes de:") + 27 + 13 + 6, objSms.getMsg().indexOf("Seu Oi recebeu ligacoes de:") + 27 + 13 + 6 + 6));
                    Calendar d = Calendar.getInstance();
                    String ano = (new SimpleDateFormat("yyyy").format(d.getTime()));
                    objSms.setDataLigacao(objSms.getDataLigacao() + "/" + ano);
                    String numeroBase = objSms.getNumeroTeLigou();
                    if (numeroBase.length() == 12) {
                        numeroBase = numeroBase.substring(3, 12);
                    }
                    recuperaNomeContato(cr, numeroBase, objSms);

                } catch (Exception e) {

                }
            }else if (objSms.getMsg().indexOf("Vivo Avisa:") > -1){
                try {
                    objSms.setNumeroTeLigou(objSms.getMsg().substring(objSms.getMsg().indexOf(" de: ") + 5, objSms.getMsg().indexOf(" em ")));
                    objSms.setDataLigacao(objSms.getMsg().substring(objSms.getMsg().indexOf(" em ") +4, objSms.getMsg().indexOf(" as ")));
                    objSms.setHoraLigacao(objSms.getMsg().substring(objSms.getMsg().indexOf(" as ") + 4, objSms.getMsg().indexOf(" as ") + 4 + 5));

                    String ano = (new SimpleDateFormat("yyyy").format(new Date()));
                    objSms.setDataLigacao(objSms.getDataLigacao() + "/" + ano);
                    String numeroBase = objSms.getNumeroTeLigou();
                    if (numeroBase.length() == 14) {
                        numeroBase = numeroBase.substring(5, 14);
                    }
                    recuperaNomeContato(cr, numeroBase, objSms);

                } catch (Exception e) {

                }
            }else {
                objSms.setNumeroTeLigou(objSms.getMsg().substring(objSms.getMsg().indexOf("<") + 1, objSms.getMsg().indexOf(">")));
                objSms.setDataLigacao(objSms.getMsg().substring(objSms.getMsg().indexOf(">") + 2, objSms.getMsg().indexOf(">") + 7));
                objSms.setHoraLigacao(objSms.getMsg().substring(objSms.getMsg().indexOf(">") + 7, objSms.getMsg().indexOf(">") + 13));
                String ano = (new SimpleDateFormat("yyyy").format(new Date()));
                objSms.setDataLigacao(objSms.getDataLigacao() + "/" + ano);
                String numeroBase = objSms.getNumeroTeLigou();
                if (numeroBase.length() == 14) {
                    numeroBase = numeroBase.substring(6, 14);
                }
                recuperaNomeContato(cr, numeroBase, objSms);
            }
        }catch (Exception e){

        }
    }

    /**
     *
     * @param cr
     * @param data
     * @param objSms
     */
    public static void preencheObjetoSmsTorpedoCobrar(ContentResolver cr,String data,Sms objSms){
        if (objSms.getMsg().indexOf("te enviou um")>-1){
            objSms.setNumeroTeLigou(objSms.getMsg().substring(0,objSms.getMsg().indexOf("te enviou")));
            objSms.setDataLigacao(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            objSms.setHoraLigacao(new SimpleDateFormat("HH:mm").format(new Date()));
        }else if (objSms.getMsg().indexOf("Vc tem ")>-1){
            objSms.setNumeroTeLigou(objSms.getMsg().substring(objSms.getMsg().indexOf("Cobrar de ")+10,objSms.getMsg().indexOf(" pendente")));
            objSms.setDataLigacao(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            objSms.setHoraLigacao(new SimpleDateFormat("HH:mm").format(new Date()));
        }else if (objSms.getMsg().indexOf("Para ver o restante do Torpedo")>-1){
            objSms.setNumeroTeLigou(objSms.getMsg().substring(objSms.getMsg().indexOf("o numero ")+9,objSms.getMsg().indexOf(" te enviou responda")));
            objSms.setDataLigacao(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            objSms.setHoraLigacao(new SimpleDateFormat("HH:mm").format(new Date()));
        }
        String numeroBase = objSms.getNumeroTeLigou();
        if (numeroBase!=null && numeroBase.length() == 14){
            numeroBase = numeroBase.substring(6,14);
            recuperaNomeContato(cr,numeroBase,objSms);
        }

    }



    /**
     *
     * @param numero
     * @return
     */
    private static void recuperaNomeContato(ContentResolver cr,String numero,Sms objSms){
        try {
            String[] PROJECTION = new String[] { ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone.PHOTO_URI };

            Cursor c = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, null);
            if (c.moveToFirst()) {
                String clsPhonename = null;
                String clsphoneNo = null;
                long contactId ;
                do {
                    clsPhonename = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    contactId    = c.getLong(c.getColumnIndex(ContactsContract.Contacts._ID));
                    clsphoneNo = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String numeroBase = clsphoneNo.replace("-","");
                    if (numeroBase.indexOf(numero) > -1) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media .getBitmap(cr, Uri.parse(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))));
                            objSms.setImagemContato(bitmap);
                        } catch (Exception e) {
                            objSms.setImagemContato(null);
                        }
                        objSms.setNomeContato(clsPhonename);
                        break;
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (objSms.getNomeContato() == null || objSms.getNomeContato().equals(""))
            recuperaNomeContatoSIM(cr,numero,objSms);

    }

private static void recuperaNomeContatoSIM(ContentResolver cr,String numero, Sms objSms){
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
                objSms.setNomeContato(clsSimPhonename);
                break;
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    objSms.setNomeContato("Não encontrado.");
}






}

