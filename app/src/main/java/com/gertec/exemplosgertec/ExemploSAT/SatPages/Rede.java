package com.gertec.exemplosgertec.ExemploSAT.SatPages;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.gertec.exemplosgertec.R;

import java.util.Random;

import br.com.estudos.satfuncoes.SatFunctions;

public class Rede extends Activity {
    private Button button_configurar;
    private EditText txtCodAtivacao, txtIp, txtMascara, txtGateway, txtDns, txtDns2, txtProxyIp, txtPorta, txtUser, txtPassword;
    private Spinner spinnerTipoRede, spinnerOptDns, spinnerOptProxy;
    private AlertDialog alerta;
    private static final String[] tipoRede = new String[]{"Estático", "DHCP"};
    private static final String[] optDns = new String[]{"Não", "Sim"};
    private static final String[] optProxy = new String[]{"Não usa proxy", "Proxy com configuração", "Proxy transparente"};
    private String appPackage = "com.gertec.exemplosgertec8";
    private String caminhoXML = "data/data/" + appPackage + "/configRede.xml";
    Random gerador = new Random();
    Context context;
    SatFunctions satFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        satFunctions = new SatFunctions(this);
        setContentView(R.layout.rede);

    }
}
