package com.andersonsilva.quemteligou;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;



public class HelpActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ajuda");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"anderson.rodrigo@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "Ajuda");
                email.putExtra(Intent.EXTRA_TEXT, "");
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Escolha um Cliente de email:"));

            }
        });

        TextView textView = (TextView) findViewById(R.id.textHelp);
        String texto = "Esta aplicação Lê os SMS enviados pela sua operadora e organiza mostrando os dados dos contatos já existentes no seu telefone. \n" +
                "Ela possui um receiver que no nomento que chega o SMS avisando que a pessoa te ligou ela dispara uma notificação para falicitar para você" +
                "ligar de volta para a pessoa.";
        textView.setText(texto);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }



}
