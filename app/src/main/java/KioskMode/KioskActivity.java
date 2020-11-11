package KioskMode;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gertec.exemplosgertec.R;

import br.com.estudos.kioskmode.KioskMode;

public class KioskActivity extends AppCompatActivity {

    TextView txtKioskMode;
    Button btnStar, btnStop;
    KioskMode kioskMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosk);

        btnStar = findViewById(R.id.btStartLockTask);
        btnStop = findViewById(R.id.btStopLockTask);
        txtKioskMode = findViewById(R.id.txtKioksMode);

        kioskMode = new KioskMode(this);
        txtKioskMode.setText("Modo Kiosk desativado");

        btnStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kioskMode.startKiosMode();
                txtKioskMode.setText("Modo Kiosk Ativado");
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    kioskMode.stopKioskMode();
                    txtKioskMode.setText("Modo Kiosk desativado");
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}