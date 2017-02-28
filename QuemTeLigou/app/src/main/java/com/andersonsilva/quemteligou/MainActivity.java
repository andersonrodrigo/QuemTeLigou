package com.andersonsilva.quemteligou;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andersonsilva.quemteligou.adapter.ExpandableListAdapter;
import com.andersonsilva.quemteligou.adapter.SmsAdapter;
import com.quemtimligou.anderonsilva.com.quemteligou.R;
import com.andersonsilva.quemteligou.entity.Sms;
import com.andersonsilva.quemteligou.utils.SmsUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static int  REQUEST_CODE_A = 10;
    ArrayList<Sms> listaSms = null;
    ListView listView = null;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    private static SmsAdapter adapter;

    private boolean checkIfAlreadyhavePermission() {
        int resultSms     = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_SMS);
        int resultContact =  ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS);
        int resultReceivedSms = ContextCompat.checkSelfPermission(this,Manifest.permission.RECEIVE_SMS);
        if (resultSms == PackageManager.PERMISSION_GRANTED
                && resultContact == PackageManager.PERMISSION_GRANTED
                && resultReceivedSms == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(MainActivity.this, R.string.ERRO_PERMISSAO, Toast.LENGTH_LONG).show();
                    TextView valorTotal = (TextView) findViewById(R.id.tituloGeralApp);
                    valorTotal.setText("Não consegui Ler seus SMSs, reintale novamente e me de permissão por favor.. :)");

                    //   Toast.makeText(MainActivity.class, "Não posso ler seus SMSs.. :(", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkIfAlreadyhavePermission()){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_SMS)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECEIVE_SMS)
                    ) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{ Manifest.permission.READ_SMS},
                        REQUEST_CODE_A);
                ActivityCompat.requestPermissions(this,
                        new String[]{ Manifest.permission.READ_CONTACTS},
                        REQUEST_CODE_A);
                ActivityCompat.requestPermissions(this,
                        new String[]{ Manifest.permission.RECEIVE_SMS},
                        REQUEST_CODE_A);


            }
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);

        iniciaActivity(toolbar);
    }


    public void iniciaActivity( Toolbar toolbar){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        // Locate MenuItem with ShareActionProvider

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    protected void onResume() {
        super.onResume();
        listaSms = SmsUtils.getAllSms(getContentResolver());
        listView = (ListView) findViewById(R.id.listaNumeros);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Sms dataModel= listaSms.get(position);

            //   Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Bundle params = new Bundle();
                params.putString("numeroReferencia", dataModel.getDataCompra());
                Toast.makeText(getBaseContext(),"gg",Toast.LENGTH_LONG);
               // intent.putExtras(params);
              //  startActivity(intent);
            }
        });
      /*  adapter= new SmsAdapter(listaSms,getApplicationContext());
        listView.setAdapter(adapter);

*/

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.listaNumeros);

        // preparing list data
        prepareListData(listaSms);

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
    }

    /*
    * Preparing the list data
    */
    private void prepareListData(List<Sms> listaSms) {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        listaSms = agrupaSms(listaSms);
        /*
        // Adding child data
        listDataHeader.add("991642374");
        listDataHeader.add("99665500");


        // Adding child data
        List<String> sub1 = new ArrayList<String>();
        sub1.add("01/01/2011 11:55");
        sub1.add("01/01/2011 11:57");

        List<String> sub2 = new ArrayList<String>();
        sub2.add("01/01/2014 11:55");
        sub2.add("01/01/2014 11:57");*/

        for (Sms sms: listaSms){
            listDataHeader.add(sms.getNumeroTeLigou());
            listDataChild.put(sms.getNumeroTeLigou(), sms.getOcorrencias()); // Header, Child data
        }




    }

    /**
     *
     * @param listaSms
     */
    private List<Sms> agrupaSms(List<Sms> listaSms) {
        List<Sms> retorno = new ArrayList<Sms>();
        for (Sms sms:listaSms){
             Sms s1 = recuperaSmsExistente(retorno,sms);
            if (s1.getOcorrencias()==null){
                s1.setOcorrencias(new ArrayList<String>());
            }
            s1.getOcorrencias().add(sms.getDataLigacao()+" "+sms.getHoraLigacao());
            retorno.add(s1);
        }
        return retorno;

    }

    /**
     *
     * @param listaRetorno
     * @param sms
     * @return
     */
    private Sms recuperaSmsExistente(List<Sms> listaRetorno, Sms sms){
        for(Sms s1:listaRetorno){
           if (s1.getNumeroTeLigou().equals(sms.getNumeroTeLigou())){
               return s1;
           }
        }
        Sms s1 = new Sms();
        s1.setNumeroTeLigou(sms.getNumeroTeLigou() + " - " + sms.getNomeContato());
        return s1;
    }

}
