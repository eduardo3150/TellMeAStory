package com.chavez.eduardo.tellmeastory.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by eduardo3150 on 9/18/17.
 */

public class SpeakRequest implements TextToSpeech.OnInitListener {
    TextToSpeech textToSpeech;
    Locale spanish = new Locale("es", "ES");
    Locale spanishUS = new Locale("es", "US");
    Locale english = new Locale("us", "US");
    Context context;

    public SpeakRequest(Context context) {
        this.context = context;
        textToSpeech = new TextToSpeech(context, this);
    }

    public TextToSpeech getTextToSpeech() {
        return textToSpeech;
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(spanishUS);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("error", "This Language is not supported");
            }

        } else {
            Log.e("error", "Initialization Failed!");
        }
    }

    public void speak(final String text) {
        if (text == null || "".equals(text)) {
            String errorText = "No existe contenido asociado";
            HashMap<String, String> params = new HashMap<String, String>();

            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId");
            textToSpeech.speak(errorText, TextToSpeech.QUEUE_FLUSH, params);

        } else {
            while (isSpeaking()) {

            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HashMap<String, String> params = new HashMap<String, String>();

                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId");
                    textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, params);
                }
            }).start();
        }
    }

    public void speak(final String[] lines) {
        String text = "";

        for (String tmp : lines) {
            text += tmp + ".";
        }

        speak(text);
    }

    public void speak(ArrayList<String> lines) {
        speak((String[]) lines.toArray());
    }

    public boolean isSpeaking() {
        return this.textToSpeech.isSpeaking();
    }

    public void stopSpeak() {
        textToSpeech.stop();
    }

    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

}
