package com.gertec.exemplosgertec;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.sdklib.Observer;
import com.sdklib.command.Command;
import com.sdklib.command.Command.JUSTIFICATION;
import com.sdklib.command.EscCommand;
import com.sdklib.connection.CashDrawerConnection;

import java.io.IOException;

import static com.sdklib.command.Command.*;


public class Impressora extends AppCompatActivity implements View.OnClickListener {

    public static final int IU_DIALOG_STATUS = 1;//STATUS DA IMPRESSORA
    public static final int IU_DIALOG_FALHA = 2;//FALHA DA IMPRESSORA
    public static final int IU_TOAST = 3;       // MENSAGEM GENERICA

    protected void showMessage(String titulo, String mensagem){

        AlertDialog alertDialog = new AlertDialog.Builder(Impressora.this).create();
        alertDialog.setTitle(titulo);
        alertDialog.setMessage(mensagem);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

    private void AtualizaIU(Message msg){
        switch(msg.what){

            case IU_TOAST:
                Toast.makeText(this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                break;

            case IU_DIALOG_STATUS:
                showMessage("Status Impressora", msg.obj.toString());
                break;

            case IU_DIALOG_FALHA:
                showMessage("Falha Impressor", msg.obj.toString());
                break;

        }//switch(msg.what)
    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            AtualizaIU(msg);
        }
    };


    private TextView txtMensagemImpressao;
    private TextView txtImpressoraTitle;

    private Button btnStatusImpressora;
    private Button btnImprimir;
    private Button btnImagem;
    private Button btnBarCode;
    private Button btnDiversasFuncoes;

    //private RadioButton rbEsquerda;
    private RadioButton rbCentralizado;
    private RadioButton rbDireita;

    private ToggleButton btnNegrito;
    private ToggleButton btnAlturaDupla;
    private ToggleButton btnLarguraDupla;
    private ToggleButton btnSublinhado;

    private Spinner spFonte;
    private Spinner spTamanho;
    private Spinner spHeight;
    private Spinner spWidth;
    private Spinner spBarCode;



    final byte CODE_PAGE_860 = 3;
    ///////////////////////// GLASS
    private static final String TAG  = MainActivity.class.getSimpleName();
    /**
     * ESC Query printer real-time status.Out of paper status
     */
    private static final int ESC_STATE_PAPER_ERR = 0x20;

    /**
     * ESC Query printer real-time status.Cover open status
     */
    private static final int ESC_STATE_COVER_OPEN = 0x04;

    /**
     * ESC Query printer real-time status.Printer error
     */
    private static final int ESC_STATE_ERR_OCCURS = 0x40;
    private EscCommand escCommand;
    private CashDrawerConnection drawerConnection;
    private boolean startOrEndSecondaryScreen;
    private Button btnCustomer;
    private ThreadFactoryBuilder threadFactoryBuilder = new ThreadFactoryBuilder("main_activity");
    /**
     * Connect printer
     */

    private void connect(final ConnectState connecter) {
        escCommand = EscCommand.getInstance();
        try {
            escCommand.connect(this, new Observer() {
                @Override
                public void success() {
                    Log.i(TAG, "success");
                    connecter.success();
                }

                @Override
                public void fail() {
                    Log.e(TAG, "Connection failed");
                }

                @Override
                public void error(Throwable throwable) {
                    Log.e(TAG, "Connection error - " + throwable.toString());
                }

                @Override
                public void other(int code) {
                    Log.e(TAG, "Connection other error - " + code);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //////////////////////////////////////////////////

    private void IniciaImpressora(){

        escCommand.addInitializePrinter();
        escCommand.addSelectCodePage((byte) CODE_PAGE_860);
        escCommand.addSelectPrintModes(EscCommand.FONT.FONTA, ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
        escCommand.addSelectJustification(JUSTIFICATION.LEFT);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.impressora);

        // Inicializa os componentes de Texto
        iniTextView();

        // Inicializa os RadioButtons
        iniRbButton();

        // Inicializa os Spinner
        iniSpinner();

        // Inicializa os Buttons
        initButtons();

        // Atribui as funções aos buttons
        initButtonsOnClick();

        Intent intent = new Intent("com.android.settings.EasyService");
        intent.setPackage("com.android.settings");
        drawerConnection = new CashDrawerConnection();
        bindService(intent, drawerConnection, Context.BIND_AUTO_CREATE);


    }

    public void AbreGaveta() {

        escCommand.openCashDrawer(drawerConnection, 1);
        if (escCommand.openCashDrawer(drawerConnection, 1)) {
            SendMessage(IU_TOAST,"Gaveta aberta com sucesso");
        } else {
            SendMessage(IU_TOAST,"Falha na abertura da Gaveta");
        }
    }



    void ExecutaImpressaoGlass(int ViewId) {
        switch (ViewId) {
            case R.id.btnStatusImpressora:
                ShowStatusImpressora();//TODO checar nome
                break;

            case R.id.btnImprimir:
                ImprimirTexto();
                break;

            case R.id.btnImagem:
                ImprimeImagem();
                break;
            case R.id.btnBarCode:
                ImprimeCodigoBarras();
                break;
            case R.id.btnDiversasFuncoes:
                ImprimeDiversasFuncoesGlass();
                break;
        }//switch(ViewId) {

    }

    @Override
    public void onClick(View view) {

        ExecutaImpressaoGlass(view.getId());

        try {
            ThreadPool.getInstantiation().addTask(threadFactoryBuilder.newThread(new Runnable() {
                @Override
                public void run() {
                    escCommand = EscCommand.getInstance();
                    if (!escCommand.isConnected()) {
                        connect(new ConnectState() {
                            @Override
                            public void success() {
                                ExecutaImpressaoGlass(view.getId());
                            }
                        });

                    } else {
                        ExecutaImpressaoGlass(view.getId());
                    }
                }
            }));

        } catch (Exception ex) {

        }
    }


    protected void iniRbButton(){
        //rbEsquerda = findViewById(R.id.rbEsquerda);
        rbCentralizado = findViewById(R.id.rbCentralizado);
        rbDireita = findViewById(R.id.rbDireita);
    }

    protected void iniTextView(){

        txtMensagemImpressao = findViewById(R.id.txtMensagemImpressao);
        txtImpressoraTitle  = findViewById(R.id.txtImpressoraTitle);
        txtImpressoraTitle.setText(MainActivity.getVersion());
    }

    protected void initButtons(){

        btnStatusImpressora = findViewById(R.id.btnStatusImpressora);

        btnImprimir = findViewById(R.id.btnImprimir);

        btnImagem = findViewById(R.id.btnImagem);

        btnNegrito = findViewById(R.id.btnNegrito);
        btnAlturaDupla = findViewById(R.id.btnAlturaDupla);
        btnLarguraDupla = findViewById(R.id.btnLarguraDupla);
        btnSublinhado = findViewById(R.id.btnSublinhado);

        btnBarCode = findViewById(R.id.btnBarCode);

        btnDiversasFuncoes = findViewById(R.id.btnDiversasFuncoes);
    }

    protected void initButtonsOnClick(){

        btnStatusImpressora.setOnClickListener(this);
        btnImprimir.setOnClickListener(this);
        btnImagem.setOnClickListener(this);
        btnBarCode.setOnClickListener(this);
        btnDiversasFuncoes.setOnClickListener(this);

    }

    protected void iniSpinner(){
        spFonte = findViewById(R.id.spFont);
        spTamanho = findViewById(R.id.spTamanho);

        spHeight = findViewById(R.id.spHeight);
        spWidth = findViewById(R.id.spWidth);
        spBarCode = findViewById(R.id.spBarType);

    }

    @TargetApi(Build.VERSION_CODES.O)


    public boolean printerIsNormal() {
        escCommand = EscCommand.getInstance();
        boolean isSuccess = escCommand.addQueryPrinterStatus();
        if(isSuccess){
            byte[] buffer = new byte[4];
            try {
                int ret = escCommand.read(buffer, 2000);
                if (ret > 0) {
                    if ((buffer[0] & ESC_STATE_PAPER_ERR) == 0 && (buffer[0] & ESC_STATE_COVER_OPEN) == 0 && (buffer[0] & ESC_STATE_ERR_OCCURS) == 0) {
                        return true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void SendMessage(int What, String strMessage){

        Message msg =mHandler.obtainMessage();
        msg.what = What;
        msg.obj = strMessage;
        mHandler.sendMessage(msg);

    }

    public void ShowStatusImpressora() {
        escCommand = EscCommand.getInstance();
        boolean isSuccess = escCommand.addQueryPrinterStatus();

        if(isSuccess){
            byte[] buffer = new byte[4];
            try {
                int ret = escCommand.read(buffer, 1000);
                if (ret > 0) {
                    if (((buffer[0] & ESC_STATE_PAPER_ERR) != 0) || ((buffer[0] & ESC_STATE_COVER_OPEN) != 0)){
                        SendMessage(IU_DIALOG_STATUS,"Sem Papel ou Tampa Aberta");
                    } else if ((buffer[0] & ESC_STATE_ERR_OCCURS) != 0) {
                        SendMessage(IU_DIALOG_STATUS,"Falha de Impressora");
                    }else {
                        SendMessage(IU_DIALOG_STATUS, "Status OK");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            SendMessage(IU_DIALOG_FALHA,"Falha ao Ler Status");
        }

    }

    private void ImprimeImagem() {
        try {
            final byte NLinhas = 8; //Linhas de avanço final

            if (printerIsNormal()) {

                IniciaImpressora();

                // Print raster bitmap
                Bitmap bGertec = BitmapFactory.decodeResource(getResources(), R.drawable.gertec);
                escCommand.addRastBitImage(bGertec, 380, 0);
                bGertec.recycle();
                escCommand.addPrintAndFeedLines((byte) NLinhas);
            }

        }catch(Exception ex){

        }
    }


    private void ImprimeDiversasFuncoesGlass() {
        try {

            if (printerIsNormal()) {

                final byte NLinhas = 4; //Linhas de avanço final

                IniciaImpressora();

                // Print raster bitmap
                Bitmap bGertec = BitmapFactory.decodeResource(getResources(),R.drawable.gertec);
                //int width = bGertec.getWidth();
                escCommand.addRastBitImage(bGertec, 380, 0);
                bGertec.recycle();
                escCommand.addPrintAndFeedLines((byte) NLinhas);

                escCommand.addText("====[Fim Impressão Imagem]====");
                escCommand.addPrintAndFeedLines((byte) NLinhas);
                // Fim Imagem


                escCommand.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);


                // Impressão Centralizada
                escCommand.addSelectJustification(JUSTIFICATION.CENTER);
                escCommand.addSetCharacterSize(EscCommand.WIDTH_ZOOM.MUL_1, EscCommand.HEIGHT_ZOOM.MUL_1);
                escCommand.addText("CENTRALIZADO\n");
                escCommand.addPrintAndFeedLines(NLinhas);

                // Fim Impressão Centralizada

                // Impressão Esquerda
                escCommand.addSelectJustification(JUSTIFICATION.LEFT);
                escCommand.addSetCharacterSize(EscCommand.WIDTH_ZOOM.MUL_2, EscCommand.HEIGHT_ZOOM.MUL_2);
                escCommand.addText("ESQUERDA\n");
                escCommand.addSetCharacterSize(EscCommand.WIDTH_ZOOM.MUL_1, EscCommand.HEIGHT_ZOOM.MUL_1);
                escCommand.addPrintAndFeedLines(NLinhas);
                // Fim Impressão Esquerda


                // Impressão Direita
                escCommand.addSelectJustification(JUSTIFICATION.RIGHT);
                escCommand.addSetCharacterSize(EscCommand.WIDTH_ZOOM.MUL_1, EscCommand.HEIGHT_ZOOM.MUL_1);
                escCommand.addText("DIREITA\n");
                escCommand.addPrintAndFeedLines(NLinhas);
                // Fim Impressão Direita

                // Impressão Negrito
                escCommand.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
                escCommand.addSelectJustification(JUSTIFICATION.LEFT);
                escCommand.addSetCharacterSize(EscCommand.WIDTH_ZOOM.MUL_1, EscCommand.HEIGHT_ZOOM.MUL_1);
                escCommand.addText("=======[Escrita Negrito]=======");
                escCommand.addPrintAndFeedLines(NLinhas);


                escCommand.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
                escCommand.addText("=======[Escrita Normal]=======\n");


                String Invertido_ON = new String(new byte[]{29, 66, 49});
                String Invertido_OFF = new String(new byte[]{29, 66, 48});
                escCommand.addText("=======[Escrita "+Invertido_ON+"Invertida"+Invertido_OFF+"]======\n");

                // Impressão Sublinhado
                escCommand.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON);
                escCommand.addText("======[Escrita Sublinhado]=====");
                escCommand.addPrintAndFeedLines(NLinhas);
                // Fim Impressão Sublinhado

                // Impressão Normal
                escCommand.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
                escCommand.addText("======[Escrita Normal]=====");
                escCommand.addPrintAndFeedLines(NLinhas);
                // Fim Impressão Normal

                //escCommand.addPrintAndFeedLines((byte) 8);


                // Print text
                escCommand.addSelectJustification(JUSTIFICATION.CENTER);
                escCommand.addText("=====[Codigo Barras EAN13]=====\n");

                // Set barcode recognizable characters(HRI)
                // Set the position of barcode recognizable characters under the barcode
                escCommand.addSelectPrintingPositionForHRICharacters(EscCommand.HRI_POSITION.NO_PRINT);
                // Set bar code height to 60 points
                escCommand.addSetBarcodeHeight((byte)120);
                // Set barcode unit width to 1
                escCommand.addSetBarcodeWidth((byte)3);
                // Print EAN13
                escCommand.addEAN13("789123456789");

                escCommand.addPrintAndFeedLines(NLinhas);

                // Print text
                escCommand.addText("===[Codigo QrCode Gertec]==\n");
                // Set error correction level
                escCommand.addSelectErrorCorrectionLevelForQRCode((byte) 0x31);
                // Set QRcode module size
                escCommand.addSelectSizeOfModuleForQRCode((byte) 5);
                // Set QRCode content
                String qrcode ="https://www.gertec.com.br";
                escCommand.addStoreQRCodeData(qrcode);
                // Print QRCode
                escCommand.addPrintQRCode();

                //escCommand.addSound((byte)100,(byte)10);
                escCommand.addPrintAndFeedLines(NLinhas);

                AbreGaveta();
            }

        }catch(Exception ex){

        }
    }


    private void ImprimirTexto(){

        if(txtMensagemImpressao.getText().toString().equals("")) {
            SendMessage(IU_TOAST, "Escreva uma mensagem");
            return;
        }

        final byte NLinhas = 4; //Linhas de avanço final

        JUSTIFICATION just = JUSTIFICATION.LEFT;
        if(rbCentralizado.isChecked()){
            just = JUSTIFICATION.CENTER;
        }else if(rbDireita.isChecked()){
            just = JUSTIFICATION.RIGHT;
        }

        FONT font = FONT.FONTA;
        ENABLE emphasized = ENABLE.OFF;
        ENABLE doubleheight = ENABLE.OFF;
        ENABLE doublewidth = ENABLE.OFF;
        ENABLE underline = ENABLE.OFF;

        if(btnNegrito.isChecked())
            emphasized = ENABLE.ON;

        if(btnAlturaDupla.isChecked())
            doubleheight = ENABLE.ON;

        if(btnLarguraDupla.isChecked())
            doublewidth = ENABLE.ON;

        if(btnSublinhado.isChecked())
            underline  = ENABLE.ON;

        if(spFonte.getSelectedItemPosition() == 1)
            font = FONT.FONTB;

        WIDTH_ZOOM width_zoom = EscCommand.WIDTH_ZOOM.values()[spTamanho.getSelectedItemPosition()];
        HEIGHT_ZOOM height_zoom = EscCommand.HEIGHT_ZOOM.values()[spTamanho.getSelectedItemPosition()];


        // Imprimindo Imagem
        escCommand.addInitializePrinter();
        escCommand.addSelectCodePage((byte)CODE_PAGE_860);
        escCommand.addSelectPrintModes(font,emphasized,doubleheight,doublewidth,underline);
        escCommand.addSelectJustification(just);
        escCommand.addSetCharacterSize(width_zoom,height_zoom);
        escCommand.addText(txtMensagemImpressao.getText().toString());
        escCommand.addPrintAndFeedLines(NLinhas);


    }
    private void ImprimeQRCode(String qrcode,byte ModuleSize ) {

        escCommand.addSelectErrorCorrectionLevelForQRCode((byte) 0x31);
        // Set QRcode module size
        escCommand.addSelectSizeOfModuleForQRCode( ModuleSize);
        // Set QRCode content
        escCommand.addStoreQRCodeData(qrcode);
        // Print QRCode
        escCommand.addPrintQRCode();


    }
    private void ImprimeCodigoBarras() {
        try {

            if(txtMensagemImpressao.getText().toString().equals("")) {
                SendMessage(IU_TOAST, "Digite o codigo");
                return;
            }

            if (printerIsNormal()) {

                final byte NLinhas = 8; //Linhas de avanço final

                IniciaImpressora();

                String sBarCode = spBarCode.getSelectedItem().toString();
                String sTexto = txtMensagemImpressao.getText().toString();


                escCommand.addSelectJustification(JUSTIFICATION.CENTER);
                escCommand.addText("===[Codigo Barras "+sBarCode+"]===\n");
                // Set barcode recognizable characters(HRI)
                // Set the position of barcode recognizable characters under the barcode
                escCommand.addSelectPrintingPositionForHRICharacters(EscCommand.HRI_POSITION.NO_PRINT);
                // Set bar code height
                escCommand.addSetBarcodeHeight(Byte.parseByte(spHeight.getSelectedItem().toString()));
                // Set barcode unit width
                escCommand.addSetBarcodeWidth(Byte.parseByte(spWidth.getSelectedItem().toString()));
                // Print Code

                if (sBarCode.equals("QR_CODE")) {
                    ImprimeQRCode(sTexto,Byte.parseByte(spHeight.getSelectedItem().toString()));
                } else if (sBarCode.equals("CODE_39")) {
                    escCommand.addCODE39(sTexto);
                } else if (sBarCode.equals("CODE_93")) {
                    escCommand.addCODE93(sTexto);
                } else if (sBarCode.equals("CODE_128")) {
                    escCommand.addCODE128(sTexto);
                } else if (sBarCode.equals("EAN_13")) {
                    escCommand.addEAN13(sTexto);
                } else if (sBarCode.equals("EAN_8")) {
                    escCommand.addEAN8(sTexto);
                }else if(sBarCode.equals("UPC-A")){
                    escCommand.addUPCA(sTexto);
                }else if(sBarCode.equals("UPC-E")){
                    escCommand.addUPCE(sTexto);
                } else if(sBarCode.equals("ITF")){
                    escCommand.addITF(sTexto);
                }
                escCommand.addPrintAndFeedLines(NLinhas);

            }

        }catch(Exception ex){

        }
    }
}
