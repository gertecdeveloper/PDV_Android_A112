package com.gertec.exemplosgertec.sensor;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.DateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.gertec.exemplosgertec.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SensorActivity extends AppCompatActivity {

    boolean sensorStart = false;

    Button btnStart;
    Button btnStop;

    ListView listSensor;
    List<String> consulta;
    List<String> consultaOrder;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        btnStart = findViewById(R.id.btnStar);
        btnStop = findViewById(R.id.btnStop);

        listSensor = findViewById(R.id.lvSensor);
        consulta = new ArrayList<String>();
        consultaOrder = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, consultaOrder);
        listSensor.setAdapter(adapter);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensorStart = true;
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensorStart = false;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Date dt = new Date();
        String currentTimeString = String.valueOf(dt.getHours()) + ":" + String.valueOf(dt.getMinutes()) + ":" + String.valueOf(dt.getSeconds());

        switch (event.getKeyCode()){
            case KeyEvent.KEYCODE_F4:

                if (sensorStart == false){ return false;}
                Log.d("Geovani", "aqui");
                consulta.add(currentTimeString + " - Pessoa identificada a frete do equipamento.");
                consultaOrder.clear();
                consultaOrder.addAll(consulta);
                Collections.reverse(consultaOrder);
                adapter.notifyDataSetChanged();

                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}