package com.gertec.exemplosgertec;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

public class DisplayActivity extends AppCompatActivity {

    Button mBtnEnviarDados;
    Button mBtnApagaDisplay;

    EditText mEtData;
    RadioButton radio_nenhum;
    RadioButton radio_unidade;
    RadioButton radio_total;
    RadioButton radio_pago;
    RadioButton radio_troco;

    GlassDisplay display = new GlassDisplay();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.customer_display );

        mBtnEnviarDados = (Button)findViewById(R.id.btn_enviar_dados);
        mBtnApagaDisplay = (Button)findViewById(R.id.btn_apagar_display);

        mEtData = (EditText)findViewById(R.id.et_data);

        radio_nenhum = (RadioButton) findViewById(R.id.radio_nenhum);
        radio_unidade = (RadioButton) findViewById(R.id.radio_unidade);
        radio_total = (RadioButton) findViewById(R.id.radio_total);
        radio_pago = (RadioButton) findViewById(R.id.radio_pago);
        radio_troco = (RadioButton) findViewById(R.id.radio_troco);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void ApagarDisplay(View v) {
        display.ApagaDisplay();
    }

    public void EnviarDados(View v) {

        try {
            String text = mEtData.getText().toString().trim();

            if(radio_nenhum.isChecked())
                display.EnviaSimbolo(GlassDisplay.SIMBOLO_NENHUM);
            else if (radio_unidade.isChecked())
                display.EnviaSimbolo(GlassDisplay.SIMBOLO_UNIDADE);
            else if (radio_total.isChecked())
                display.EnviaSimbolo(GlassDisplay.SIMBOLO_TOTAL);
            else if (radio_pago.isChecked())
                display.EnviaSimbolo(GlassDisplay.SIMBOLO_PAGO);
            else if (radio_troco.isChecked())
                display.EnviaSimbolo(GlassDisplay.SIMBOLO_TROCO);

            display.EnviaDados(text);

        }catch(Exception ex){

        }
    }

}
