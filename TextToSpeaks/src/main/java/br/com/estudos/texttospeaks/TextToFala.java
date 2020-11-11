package br.com.estudos.texttospeaks;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;

import java.util.Locale;
import java.util.Set;

public class TextToFala {

    private static final String TAG = TextToFala.class.getName();

    static Context  context= null;
    private TextToSpeech speech;
    private static TextToFala _instance;
    public static Locale _UserLocale = null;

    public static TextToFala getInstance(Context c){
        if(_instance == null){
            _instance = new TextToFala();
            context = c;
        }
        return  _instance;
    }

    public void init(final Context context) {
        _UserLocale= LanguageSettings.getCurrentLocale(context);
        if (speech == null) {
            speech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {

                @Override
                public void onInit(int status) {

                    if (status == speech.SUCCESS) {
                        Locale userLocale = LanguageSettings.getCurrentLocale(context);
                        int result = speech.setLanguage(userLocale);
                        if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                                && result != TextToSpeech.LANG_AVAILABLE) {
                            Log.e(TAG, "don't support that language");
                        }
                    }
                }
            });
        }
    }

    public void speechText(String text) {
        int speakRet =  speech.speak(text, TextToSpeech.QUEUE_ADD, null);
        Log.d(TAG, "speechText() speakRet == "+speakRet);
    }

    public Set<Locale> getAvailableLanguages(){
        return speech.getAvailableLanguages();
    }

    public Set<Voice> getVoice(){
        return speech.getVoices();
    }

    public Set<Voice> getVoices(){
        return speech.getVoices();
    }

    public int Stop(){
        return speech.stop();
    }

    public boolean isSpeaking(){
        return speech.isSpeaking();
    }
}
