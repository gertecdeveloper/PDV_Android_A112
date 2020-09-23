package com.gertec.exemplosgertec;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static String Model = Build.MODEL;
    public static String PLATAFORMA = "Android Studio";
    public static final String VERSION = "1.0.0";

    public static final String GLASS_MODEL = "GLASS A112";

    public static String getVersion(){
        return PLATAFORMA+" - "+VERSION+" - "+Model;
    }

    //REFERENCIA DAS ACTIVITIES
    //GLASS A112
    public static final String  ACTIVITY_IMPRESSAO = "Impressão";
    public static final String  ACTIVITY_DISPLAY = "Display Consumidor";
    //G-BOT - Implementação Futura
    public static final String  ACTIVITY_CODIGO_BARRAS = "Código de Barras";
    public static final String  ACTIVITY_NFC = "NFC";


    ArrayList<Projeto> projetos = new ArrayList<Projeto>();
    ListView lvProjetos;

    TextView txtProject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvProjetos = findViewById(R.id.lvProjetos);
        txtProject = findViewById(R.id.txtNameProject);

        txtProject.setText(getVersion());


        if(Model.equals(GLASS_MODEL)) {
            projetos.add(new Projeto(ACTIVITY_IMPRESSAO, R.drawable.print));
            projetos.add(new Projeto(ACTIVITY_DISPLAY, R.drawable.customerdisplay));
        }else { //G-BOT - Implementação Futura
            projetos.add(new Projeto(ACTIVITY_CODIGO_BARRAS, R.drawable.barcode));
            projetos.add(new Projeto(ACTIVITY_NFC, R.drawable.nfc2));

        }

        ProjetoAdapter adapter = new ProjetoAdapter(getBaseContext(), R.layout.listprojetos, projetos);
        lvProjetos.setAdapter(adapter);
        lvProjetos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Projeto projeto = (Projeto) lvProjetos.getItemAtPosition(i);

                Intent intent = null;
                switch (projeto.getNome()){
                    //G-BOT Implementação Futura
                    case ACTIVITY_CODIGO_BARRAS:
                        //intent = new Intent(MainActivity.this, CodigoBarras1.class);
                        break;
                    case ACTIVITY_NFC:
                        //intent = new Intent(MainActivity.this, NfcExemplo.class);
                        break;

                    //GLASS A112
                    case ACTIVITY_IMPRESSAO:
                        intent = new Intent(MainActivity.this, Impressora.class);
                        break;
                    case ACTIVITY_DISPLAY:
                        intent = new Intent(MainActivity.this, DisplayActivity.class);
                        break;

                }//switch (projeto.getNome())

                if(intent != null){
                    startActivity(intent);
                }
            }
        });

    }
}
