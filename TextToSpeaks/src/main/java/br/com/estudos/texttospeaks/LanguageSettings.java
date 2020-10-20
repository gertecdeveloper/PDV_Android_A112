package br.com.estudos.texttospeaks;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import java.lang.reflect.Method;
import java.util.Locale;

public class LanguageSettings {

    private static final String TAG = TextToFala.class.getName();

    private static final String LOCALE_FILE = "LOCALE_FILE";

    private static final String LOCALE_KEY = "LOCALE_KEY";

    public static Locale getUserLocale(Context pContext) {
        SharedPreferences _SpLocale = pContext.getSharedPreferences(LOCALE_FILE, Context.MODE_PRIVATE);
        String _LocaleJson = _SpLocale.getString(LOCALE_KEY, "");
        return jsonToLocale(_LocaleJson);
    }

    public static Locale getCurrentLocale(Context pContext) {
        Locale _Locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            _Locale = pContext.getResources().getConfiguration().getLocales().get(0);
        } else {
            _Locale = pContext.getResources().getConfiguration().locale;
        }
        return _Locale;
    }

    public static void saveUserLocale(Context pContext, Locale pUserLocale) {
        SharedPreferences _SpLocal=pContext.getSharedPreferences(LOCALE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor _Edit=_SpLocal.edit();
        String _LocaleJson = localeToJson(pUserLocale);
        _Edit.putString(LOCALE_KEY, _LocaleJson);
        _Edit.apply();
    }

    private static String localeToJson(Locale pUserLocale) {
        Gson _Gson = new Gson();
        return _Gson.toJson(pUserLocale);
    }

    private static Locale jsonToLocale(String pLocaleJson) {
        Gson _Gson = new Gson();
        return _Gson.fromJson(pLocaleJson, Locale.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void updateLocale(Context pContext, Locale pNewUserLocale) {
        if (needUpdateLocale(pContext, pNewUserLocale)) {
            Configuration _Configuration = pContext.getResources().getConfiguration();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                _Configuration.setLocale(pNewUserLocale);
            } else {
                _Configuration.locale =pNewUserLocale;
            }
            DisplayMetrics _DisplayMetrics = pContext.getResources().getDisplayMetrics();
            pContext.getResources().updateConfiguration(_Configuration, _DisplayMetrics);
            saveUserLocale(pContext, pNewUserLocale);
        }
    }

    public static boolean needUpdateLocale(Context pContext, Locale pNewUserLocale) {
        return pNewUserLocale != null && !getCurrentLocale(pContext).equals(pNewUserLocale);
    }
    
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void setSystemLanguage(Locale locale){
        Log.d(TAG, locale.getLanguage());
        changeSystemLanguage(new LocaleList(locale));
    }
    
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void changeSystemLanguage(LocaleList locale) {
       
        if (locale != null) {
            try {
                Class classActivityManagerNative = Class.forName("android.app.ActivityManagerNative");
                Method getDefault = classActivityManagerNative.getDeclaredMethod("getDefault");
                Object objIActivityManager = getDefault.invoke(classActivityManagerNative);
                Class classIActivityManager = Class.forName("android.app.IActivityManager");
                Method getConfiguration = classIActivityManager.getDeclaredMethod("getConfiguration");
                Configuration config = (Configuration) getConfiguration.invoke(objIActivityManager);
                Log.d(TAG, "changeSystemLanguage()  LocaleList: " + new Gson().toJson(locale));

                config.setLocales(locale);
                Class[] clzParams = {Configuration.class};
                Method updateConfiguration = classIActivityManager.getDeclaredMethod("updatePersistentConfiguration", clzParams);
                updateConfiguration.invoke(objIActivityManager, config);
            } catch (Exception e) {
                Log.d(TAG, "changeSystemLanguage()  Exception: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }
}
