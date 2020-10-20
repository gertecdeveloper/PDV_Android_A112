package com.gertec.exemplosgertec.CodigoBarras;

import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.gertec.exemplosgertec.CodigoBarras.service.LedUtil;
import com.gertec.exemplosgertec.CodigoBarras.service.ReadCodeService;
import com.gertec.exemplosgertec.CodigoBarras.service.ServiceResultReceiver;
import com.gertec.exemplosgertec.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.gertec.exemplosgertec.CodigoBarras.service.ReadCodeService.SHOW_RESULT;


public class LeituraCodigoActivity extends AppCompatActivity implements ServiceResultReceiver.Receiver {

    private ServiceResultReceiver mServiceResultReceiver;

    Button btnStart, btnStop, btnLigarLed, btnDesligaLed;

    ListView listCursos;
    List<String> consulta;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leituracodigo);

        listCursos = findViewById(R.id.lvConsulta);
        consulta = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, consulta);
        listCursos.setAdapter(adapter);

        btnStart = findViewById(R.id.btnStar);
        btnStop = findViewById(R.id.btnStop);
        btnLigarLed = findViewById(R.id.btnLigarLed);
        btnDesligaLed = findViewById(R.id.btnDesligaLed);

        mServiceResultReceiver = new ServiceResultReceiver(new Handler());
        mServiceResultReceiver.setReceiver(this);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartLeitura(LeituraCodigoActivity.this, mServiceResultReceiver);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReadCodeService.START_SERVICES = false;
            }
        });

        btnLigarLed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LedUtil.setOnLed();
            }
        });

        btnDesligaLed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LedUtil.setOffLed();
            }
        });
    }


    private void StartLeitura(LeituraCodigoActivity leituraCodigoActivity, ServiceResultReceiver mServiceResultReceiver){
        ReadCodeService.enqueueWork(leituraCodigoActivity, mServiceResultReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case SHOW_RESULT:
                if (resultData != null) {
                    consulta.add(resultData.getString("data"));
                    Collections.reverse(consulta);
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

}
