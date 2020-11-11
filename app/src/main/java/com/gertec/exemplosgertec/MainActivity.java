package com.gertec.exemplosgertec;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gertec.exemplosgertec.CodigoBarras.LeituraCodigoActivity;
import com.gertec.exemplosgertec.ExemploNFCIdRW.NfcExemplo;
import com.gertec.exemplosgertec.ExemploSAT.SatPages.MenuSat;
import com.gertec.exemplosgertec.ExemploTEF.Tef;

import com.gertec.exemplosgertec.fala.FalaActivity;
import com.gertec.exemplosgertec.sensor.SensorActivity;

import java.util.ArrayList;

import KioskMode.KioskActivity;

public class MainActivity extends AppCompatActivity {

    public static String Model = Build.MODEL;
    public static String PLATAFORMA = "Android Studio";
    public static final String VERSION = "1.0.0";

    public static final String GLASS_MODEL = "A112";

    public static String getVersion(){
        return PLATAFORMA+" - "+VERSION+" - "+Model;
    }

    //REFERENCIA DAS ACTIVITIES
    //GLASS A112
    public static final String  ACTIVITY_IMPRESSAO = "Impressão";
    public static final String  ACTIVITY_DISPLAY = "Display Consumidor";

    //G-BOT - Implementação Futura
    public static final String  ACTIVITY_CODIGO_BARRAS = "Código de Barras";
    public static final String  ACTIVITY_NFCx = "NFC - NDEF";
    public static final String  ACTIVITY_FALA = "FALA G-Bot";

    //ACtivities Comuns
    public static final String  ACTIVITY_SAT = "Sat";
    public static final String  ACTIVITY_TEF = "Tef";
    public static final String  ACTIVITY_KIOSKMODE = "Modo Quiosque";
    public static final String  ACTIVITY_SENSOR_PRESENSA = "Sensor de Presença";

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
            projetos.add(new Projeto(ACTIVITY_NFCx, R.drawable.nfc2));
            projetos.add(new Projeto(ACTIVITY_SENSOR_PRESENSA, R.drawable.sensor));
        }

        projetos.add(new Projeto(ACTIVITY_FALA, R.drawable.speaker));
        projetos.add(new Projeto(ACTIVITY_KIOSKMODE, R.drawable.kiosk));
        projetos.add(new Projeto(ACTIVITY_TEF, R.drawable.tef));
        projetos.add(new Projeto(ACTIVITY_SAT, R.drawable.icon_sat));


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
                        intent = new Intent(MainActivity.this, LeituraCodigoActivity.class);
                        break;
                    case ACTIVITY_NFCx:
                        intent = new Intent(MainActivity.this, NfcExemplo.class);
                        break;

                    //A112
                    case ACTIVITY_IMPRESSAO:
                        intent = new Intent(MainActivity.this, Impressora.class);
                        break;
                    case ACTIVITY_DISPLAY:
                        intent = new Intent(MainActivity.this, DisplayActivity.class);
                        break;

                    //Activities Comuns
                    case ACTIVITY_TEF:
                        intent = new Intent(MainActivity.this, Tef.class);
                        break;

                    case ACTIVITY_SAT:
                        intent = new Intent(MainActivity.this, MenuSat.class);
                        break;

                    case ACTIVITY_FALA:
                        intent = new Intent(MainActivity.this, FalaActivity.class);
                        break;

                    case ACTIVITY_KIOSKMODE:
                        intent = new Intent(MainActivity.this, KioskActivity.class);
                        break;

                    case ACTIVITY_SENSOR_PRESENSA:
                        intent = new Intent(MainActivity.this, SensorActivity.class);
                        break;
                }

                if(intent != null){
                    startActivity(intent);
                }
            }
        });

    }

}
