package com.gertec.exemplosgertec.ExemploTEF;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.gertec.exemplosgertec.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO IMPRESSAO GLASS
//import com.gertec.exemplosgertec.ExemploImpressora.ConfigPrint;
//import com.gertec.exemplosgertec.ExemploImpressora.GertecPrinter;

public class Tef extends AppCompatActivity {

    public static String acao = "venda";
    Gson gson = new Gson();

    // TODO IMPRESSAO GLASS private GertecPrinter gertecPrinter;
    // TODO IMPRESSAO GLASS private ConfigPrint configPrint = new ConfigPrint();
    /// Difines operação
    private Random r = new Random();
    private Date dt = new Date();
    private String op = String.valueOf(r.nextInt(99999));
    private String currentDateTimeString = DateFormat.getDateInstance().format(new Date());
    private String currentDateTimeStringT = String.valueOf(dt.getHours()) + String.valueOf(dt.getMinutes()) + String.valueOf(dt.getSeconds());
    /// Fim Defines Operação

    private final static int VENDA = 1;
    private final static int CANCELAMENTO = 2;
    private final static int FUNCOES = 3;
    private final static int REIMPRESSAO = 4;

    ///  Defines tef
    private static int REQ_CODE = 4321;
    /// Fim Defines tef


    private EditText txtValorOperacao;
    private EditText txtIpServidor;
    private EditText txtParcelas;

    private Button btnEnviarTransacao;
    private Button btnCancelarTransacao;
    private Button btnFuncoes;
    private Button btnReimpressao;

    private CheckBox cbImpressao;

    private RadioButton rbTodos;
    private RadioButton rbCredito;
    private RadioButton rbDebito;
    private RadioButton rbLoja;
    private RadioButton rbAdm;

    private TextView txtCupom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tef);

        txtCupom = findViewById(R.id.txtRetorno);
        // Inicializa todos os EditText
        txtValorOperacao = findViewById(R.id.txtValorOperacao);
        txtIpServidor = findViewById(R.id.txtIpServidor);
        txtParcelas = findViewById(R.id.txtParcelas);

        // Inicializa todos os Buttons
        btnEnviarTransacao = findViewById(R.id.btnEnviarTransacao);
        btnCancelarTransacao = findViewById(R.id.btnCancelarTransacao);
        btnFuncoes = findViewById(R.id.btnFuncoes);
        btnReimpressao = findViewById(R.id.btnReimpressao);

        // Inicializa todos os RadioButtons

        rbCredito = findViewById(R.id.rbCredito);
        rbDebito = findViewById(R.id.rbDebito);
        rbTodos = findViewById(R.id.rbTodos);
        rbLoja = findViewById(R.id.radioLoja);
        rbAdm = findViewById(R.id.radioAdm);

        // Inicializa o CheckBox

        //* Caso seja M-sitef, este parâmetro não surge efeito (linhas comentadas na funções do M-Sitef (Abaixo)), pois na versão v3.70 está opção foi removida do Sitef **
        cbImpressao = findViewById(R.id.cbImpressao);
        cbImpressao.setEnabled(false);
        cbImpressao.setChecked(false);

        // rbGer7 = findViewById(R.id.rbGer7);
        // rbMsitef = findViewById(R.id.rbMsitef);
        // Adiciona mascara nos campos
        maskTextEdits();
        txtValorOperacao.setHint("");
        txtValorOperacao.setText("12,34");
        txtIpServidor.setText("192.168.15.9");
        rbAdm.setChecked(true);
        cbImpressao.setChecked(true);
        txtIpServidor.setInputType(InputType.TYPE_CLASS_NUMBER);
        txtIpServidor.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        rbDebito.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (rbTodos.isChecked() || rbDebito.isChecked()) {
                    txtParcelas.setText("1");
                    txtParcelas.setEnabled(false);
                } else {
                    txtParcelas.setEnabled(true);
                }
            }
        });

        rbTodos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (rbTodos.isChecked() || rbDebito.isChecked()) {
                    txtParcelas.setText("1");
                    txtParcelas.setEnabled(false);
                } else {
                    txtParcelas.setEnabled(true);
                }
            }
        });

        btnEnviarTransacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acao = "venda";
                if (Mask.unmask(txtValorOperacao.getText().toString()).equals("000")) {
                    dialogoErro("O valor de venda digitado deve ser maior que 0");
                } else if (validaIp(txtIpServidor.getText().toString()) == false) {
                    dialogoErro("Digite um IP válido");
                } else {
                    if (rbCredito.isChecked() && (txtParcelas.getText().toString().isEmpty() || txtParcelas.getText().toString().equals("0"))) {
                        dialogoErro("É necessário colocar o número de parcelas desejadas (obs.: Opção de compra por crédito marcada)");
                    } else {
                        execultaTEF(VENDA);
                    }
                }
            }
        });

        btnCancelarTransacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acao = "cancelamento";
                if (Mask.unmask(txtValorOperacao.getText().toString()).equals("000")) {
                    dialogoErro("O valor de venda digitado deve ser maior que 0");
                } else if (validaIp(txtIpServidor.getText().toString()) == false) {
                    dialogoErro("Digite um IP válido");
                } else {
                    execultaTEF(CANCELAMENTO);
                }
            }
        });

        btnFuncoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acao = "funcoes";
                if (Mask.unmask(txtValorOperacao.getText().toString()).equals("000")) {
                    dialogoErro("O valor de venda digitado deve ser maior que 0");
                } else if (validaIp(txtIpServidor.getText().toString()) == false) {
                    dialogoErro("Digite um IP válido");
                } else {
                    execultaTEF(FUNCOES);
                }
            }
        });

        btnReimpressao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acao = "reimpressao";
                if (Mask.unmask(txtValorOperacao.getText().toString()).equals("000")) {
                    dialogoErro("O valor de venda digitado deve ser maior que 0");
                } else if (validaIp(txtIpServidor.getText().toString()) == false) {
                    dialogoErro("Digite um IP válido");
                } else {
                    execultaTEF(REIMPRESSAO);
                }
            }
        });
    }

    // O M-Sitef não retorna um json como resposta, logo é criado um json com a
    // reposta do Sitef.
    public String respSitefToJson(Intent data) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("CODRESP", data.getStringExtra("CODRESP"));
        json.put("COMP_DADOS_CONF", data.getStringExtra("COMP_DADOS_CONF"));
        json.put("CODTRANS", data.getStringExtra("CODTRANS"));
        json.put("VLTROCO", data.getStringExtra("VLTROCO"));
        json.put("REDE_AUT", data.getStringExtra("REDE_AUT"));
        json.put("BANDEIRA", data.getStringExtra("BANDEIRA"));
        json.put("NSU_SITEF", data.getStringExtra("NSU_SITEF"));
        json.put("NSU_HOST", data.getStringExtra("NSU_HOST"));
        json.put("COD_AUTORIZACAO", data.getStringExtra("COD_AUTORIZACAO"));
        json.put("NUM_PARC", data.getStringExtra("NUM_PARC"));
        json.put("TIPO_PARC", data.getStringExtra("TIPO_PARC"));
        json.put("VIA_ESTABELECIMENTO", data.getStringExtra("VIA_ESTABELECIMENTO"));
        json.put("VIA_CLIENTE", data.getStringExtra("VIA_CLIENTE"));
        return json.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        RetornoMsiTef retornoSitef = null;
        try {
            retornoSitef = gson.fromJson(respSitefToJson(data), RetornoMsiTef.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (requestCode == REQ_CODE && resultCode == RESULT_OK) {
            if (retornoSitef.getCodResp().equals("0")) {
                String impressao = "";
                // Verifica se tem algo pra imprimir
                if (!retornoSitef.textoImpressoCliente().isEmpty()) {
                    impressao += retornoSitef.textoImpressoCliente();
                }
                if (!retornoSitef.textoImpressoEstabelecimento().isEmpty()) {
                    impressao += "\n\n-----------------------------     \n";
                    impressao += retornoSitef.textoImpressoEstabelecimento();
                }

                Log.d("Geovani", impressao);

                txtCupom.setText(impressao);
            }
            // Verifica se ocorreu um erro durante venda ou cancelamento
            if (acao.equals("venda") || acao.equals("cancelamento")) {
                if (retornoSitef.getCodResp().isEmpty() || !retornoSitef.getCodResp().equals("0") || retornoSitef.getCodResp() == null) {
                    dialodTransacaoNegadaMsitef(retornoSitef);
                } else {
                    dialodTransacaoAprovadaMsitef(retornoSitef);
                }
            }
        } else {
            // ocorreu um erro
            if (acao.equals("venda") || acao.equals("cancelamento")) {
                dialodTransacaoNegadaMsitef(retornoSitef);
            }
        }
    }

    boolean validaIp(String ipserver) {

        Pattern p = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
        Matcher m = p.matcher(ipserver);
        boolean b = m.matches();
        return b;
    }

    private void maskTextEdits() {
        txtValorOperacao.addTextChangedListener(new MoneyTextWatcher(txtValorOperacao));
    }

    private void dialodTransacaoAprovadaMsitef(RetornoMsiTef retornoMsiTef) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        StringBuilder cupom = new StringBuilder();
        cupom.append("CODRESP: " + retornoMsiTef.getCodResp() + "\n");
        cupom.append("COMP_DADOS_CONF: " + retornoMsiTef.getCompDadosConf() + "\n");
        cupom.append("CODTRANS: " + retornoMsiTef.getCodTrans() + "\n");
        cupom.append("CODTRANS (Name): " + retornoMsiTef.getNameTransCod() + "\n");
        cupom.append("VLTROCO: " + retornoMsiTef.getvlTroco() + "\n");
        cupom.append("REDE_AUT: " + retornoMsiTef.getRedeAut() + "\n");
        cupom.append("BANDEIRA: " + retornoMsiTef.getBandeira() + "\n");
        cupom.append("NSU_SITEF: " + retornoMsiTef.getNSUSitef() + "\n");
        cupom.append("NSU_HOST: " + retornoMsiTef.getNSUHOST() + "\n");
        cupom.append("COD_AUTORIZACAO: " + retornoMsiTef.getCodAutorizacao() + "\n");
        cupom.append("NUM_PARC: " + retornoMsiTef.getParcelas() + "\n");
        alertDialog.setTitle("Ação executada com sucesso");
        alertDialog.setMessage(cupom.toString());
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Não existe nenhuma ação
            }
        });
        alertDialog.show();
    }


    private void dialogoErro(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Erro ao executar função");
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Não existe nenhuma ação
            }
        });
        alertDialog.show();

    }

    private void dialodTransacaoNegadaMsitef(RetornoMsiTef retornoMsiTef) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        StringBuilder cupom = new StringBuilder();
        cupom.append("CODRESP: " + retornoMsiTef.getCodResp());
        alertDialog.setTitle("Ocorreu um erro durante a realização da ação");
        alertDialog.setMessage(cupom.toString());
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Não existe nenhuma ação
            }
        });
        alertDialog.show();
    }


    private void execultaTEF(int operacao){

        Intent intentSitef = new Intent("br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF");

        intentSitef.putExtra("empresaSitef", "00000000");
        intentSitef.putExtra("enderecoSitef", txtIpServidor.getText().toString().replaceAll("\\s+", ""));
        intentSitef.putExtra("operador", "0001");
        intentSitef.putExtra("data", "20200324");
        intentSitef.putExtra("hora", "130358");
        intentSitef.putExtra("numeroCupom", op);
        intentSitef.putExtra("comExterna", "0"); // 0 – Sem (apenas para SiTef dedicado)
        intentSitef.putExtra("CNPJ_CPF", "03654119000176"); // CNPJ ou CPF do estabelecimento.

        switch (operacao){
            case VENDA:

                intentSitef.putExtra("valor", Mask.unmask(txtValorOperacao.getText().toString()));

                if (rbCredito.isChecked()) {
                    intentSitef.putExtra("modalidade", "3");
                    if (txtParcelas.getText().toString().equals("0") || txtParcelas.getText().toString().equals("1")) {
                        intentSitef.putExtra("transacoesHabilitadas", "26");
                    } else if (rbLoja.isChecked()) {
                        // Essa informações habilida o parcelamento Loja
                        intentSitef.putExtra("transacoesHabilitadas", "27");
                    } else if (rbAdm.isChecked()) {
                        // Essa informações habilida o parcelamento ADM
                        intentSitef.putExtra("transacoesHabilitadas", "28");
                    }
                    intentSitef.putExtra("numParcelas", txtParcelas.getText().toString());
                }

                if (rbDebito.isChecked()) {
                    intentSitef.putExtra("modalidade", "2");
                    intentSitef.putExtra("transacoesHabilitadas", "16");
                }

                if (rbTodos.isChecked()) {
                    intentSitef.putExtra("modalidade", "0");
                }

                // intentSitef.putExtra("restricoes", "transacoesHabilitadas=16;26;27");

                break;

            case CANCELAMENTO:
                intentSitef.putExtra("modalidade", "200");
                break;
            case FUNCOES:
                intentSitef.putExtra("modalidade", "110");
                break;

            case REIMPRESSAO:
                intentSitef.putExtra("modalidade", "114");
                break;

        }

        intentSitef.putExtra("isDoubleValidation", "0");
        intentSitef.putExtra("caminhoCertificadoCA", "ca_cert_perm");
        intentSitef.putExtra("cnpj_automacao", "03654119000176"); // CNPJ da empresa que desenvolveu a automação comercial.

        startActivityForResult(intentSitef, REQ_CODE);

    }

}
