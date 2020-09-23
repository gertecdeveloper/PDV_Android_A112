package com.gertec.exemplosgertec;

import java.io.File;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

public class GlassDisplay {

    static final int SIMBOLO_NENHUM= 0;
    static final int SIMBOLO_UNIDADE = 1;
    static final int SIMBOLO_TOTAL = 2;
    static final int SIMBOLO_PAGO = 3;
    static final int SIMBOLO_TROCO = 4;

    static File mydevice = new File("/dev/ttyHSL2");
    SerialPort mSerialPort;

    GlassDisplay() {

        try {
            mSerialPort = new SerialPort(mydevice, 2400, 0);
        } catch (Exception ex) {

        }
    }

    public void EnviaDados(String Numeros){

        OutputStream mOutputStream = mSerialPort.getOutputStream();

        byte[] Numbers = Numeros.getBytes();
        byte[] Prefix = new byte[]{0x1B, 0x51, 0x41};

        try {

            mOutputStream.write(Prefix);
            mOutputStream.write(Numbers);
            mOutputStream.write(0x0d);

        } catch (Exception ex) {

        }


    }

    public void EnviaSimbolo(int Numero){

        OutputStream mOutputStream = mSerialPort.getOutputStream();
        byte[] Prefix = new byte[]{0x1B, 0x73,0x00};
        if((Numero>=1)&&(Numero<=4))
            Prefix[2]=(byte)Numero;
        try{
            mOutputStream.write(Prefix);
        } catch (Exception ex) {

        }


    }

    public void ApagaDisplay(){

        OutputStream mOutputStream = mSerialPort.getOutputStream();

        try{
            mOutputStream.write(0x0c);
        } catch (Exception ex) {

        }


    }

    public void ZeraDisplay(){

        EnviaDados("0.00");

    }

}
