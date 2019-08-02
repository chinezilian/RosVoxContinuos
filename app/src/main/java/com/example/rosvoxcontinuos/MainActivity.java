package com.example.rosvoxcontinuos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.ros.android.view.RosTextView;
import org.ros.android.MessageCallable;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends RosActivity implements
        RecognitionListener {

    private RosTextView<std_msgs.String> rosTextView;
    private static Talker talker;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private TextView returnedText;
    private TextView returnedError;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private TextView editText;
    static String SaidaVoz;
    private Listener listener;
    RelativeLayout layout_joystick;
    ImageView image_joystick, image_border;
    TextView textView1, textView2, textView3, textView4, textView5;

    JoyStickClass js;
    Button botao;
    ImageButton up,down,left,right,stop;

    public static void setSaidaVoz(String saidaVoz) {
        SaidaVoz = saidaVoz;
    }
    public static String getSaidaVoz() {
        return SaidaVoz;
    }


    public MainActivity() {
        super("RosVoxContinuos", "RosVoxContinuos");
    }

    private void resetSpeechRecognizer() {

        if(speech != null)
            speech.destroy();
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        if(SpeechRecognizer.isRecognitionAvailable(this))
            speech.setRecognitionListener(this);
        else
            finish();
    }

    private void setRecogniserIntent() {

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI initialisation
        returnedText = findViewById(R.id.textView);
        returnedError = findViewById(R.id.errorView1);
        progressBar =  findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.INVISIBLE);
        editText = findViewById(R.id.edit_text);
        botao = (Button) findViewById(R.id.button);

        up = (ImageButton) findViewById(R.id.upper);
        down = (ImageButton) findViewById(R.id.downn);
        left = (ImageButton) findViewById(R.id.leeft);
        right = (ImageButton) findViewById(R.id.righht);
        stop = (ImageButton) findViewById(R.id.stoop);

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                botaoup();
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                botaodown();
            }
        });
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                botaoleft();
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                botaoright();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                botaostop();
            }
        });
        // start speech recogniser
        resetSpeechRecognizer();

        // start progress bar
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);

        // check for permission
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }

        setRecogniserIntent();
        speech.startListening(recognizerIntent);


    }


    public void botaoup(){
        SaidaVoz = "frente";
        Toast.makeText(getApplicationContext(),SaidaVoz,Toast.LENGTH_SHORT).show();
    }
    public void botaoleft(){
        SaidaVoz = "esquerda";
        Toast.makeText(getApplicationContext(),SaidaVoz,Toast.LENGTH_SHORT).show();
    }
    public void botaoright(){
        SaidaVoz = "direita";
        Toast.makeText(getApplicationContext(),SaidaVoz,Toast.LENGTH_SHORT).show();
    }
    public void botaodown(){
        SaidaVoz = "tras";
        Toast.makeText(getApplicationContext(),SaidaVoz,Toast.LENGTH_SHORT).show();
    }
    public void botaostop(){
        SaidaVoz = "pare";
        Toast.makeText(getApplicationContext(),SaidaVoz,Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                speech.startListening(recognizerIntent);
            } else {
                Toast.makeText(MainActivity.this, "Permission Denied!", Toast
                        .LENGTH_SHORT).show();
                finish();
            }
        }
    }


    @Override
    public void onResume() {
        Log.i(LOG_TAG, "resume");
        super.onResume();
        resetSpeechRecognizer();
        speech.startListening(recognizerIntent);
    }

    @Override
    protected void onPause() {
        Log.i(LOG_TAG, "pause");
        super.onPause();
        speech.stopListening();
    }

    @Override
    protected void onStop() {
        Log.i(LOG_TAG, "stop");
        super.onStop();
        if (speech != null) {
            speech.destroy();
        }
    }


    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
        botao.setBackgroundResource(R.color.Blue);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        progressBar.setIndeterminate(true);
        botao.setBackgroundResource(R.color.Red);
        speech.stopListening();
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";

        //returnedText.setText(text);
        String EntradaVoz;
        EntradaVoz = matches.get(0);
        SaidaVoz = comparator(EntradaVoz);
        editText.setText(SaidaVoz);

        speech.startListening(recognizerIntent);
    }

    String comparator(String fala) {
        String listCommand[] = {"frente", "em frente","avante","adiante", "avance", "front", "in front","forward", "go"};
        String listCommand1[] = {"direita","a direita","right", "", "", "", "", "", ""};
        String listCommand2[] = {"esquerda","a esquerda","left", "", "", "", "", "", ""};
        String listCommand3[] = {"traz", "atrás","ré","back", "backward", "", "", "", ""};
        String listCommand4[] = {"devagar","lento","desacelera","slow", "", "", "", "", ""};
        String listCommand5[] = {"rápido", "veloz","acelera","fast", "", "", "", "", ""};
        String listCommand6[] = {"parar","pare","pausar","pause","break","stop", "", "", ""};
        for (int i = 0; i < listCommand.length; i++) {
            if (listCommand[i].equalsIgnoreCase(fala)) {
                fala="frente";
                Toast.makeText(getApplicationContext(), fala, Toast.LENGTH_SHORT).show();
                break;
            }
            if (listCommand1[i].equalsIgnoreCase(fala)) {
                    fala="direita";
                    Toast.makeText(getApplicationContext(),fala,Toast.LENGTH_SHORT).show();
                    break;
                }
             if(listCommand2[i].equalsIgnoreCase(fala)) {
                    fala = "esquerda";
                    Toast.makeText(getApplicationContext(),fala,Toast.LENGTH_SHORT).show();
                    break;
                }
            if(listCommand3[i].equalsIgnoreCase(fala)) {
                fala = "tras";
                Toast.makeText(getApplicationContext(),fala,Toast.LENGTH_SHORT).show();
                break;
            }
            if(listCommand4[i].equalsIgnoreCase(fala)) {
                fala = "lento";
                Toast.makeText(getApplicationContext(),fala,Toast.LENGTH_SHORT).show();
                break;
            }
            if(listCommand5[i].equalsIgnoreCase(fala)) {
                fala ="rapido" ;
                Toast.makeText(getApplicationContext(),fala,Toast.LENGTH_SHORT).show();
                break;
            }
            if(listCommand6[i].equalsIgnoreCase(fala)) {
                fala = "pare";
                Toast.makeText(getApplicationContext(),fala,Toast.LENGTH_SHORT).show();
                break;
            }
                else if(listCommand.length - 1 == i){
                    Toast.makeText(getApplicationContext(), "Comando inválido,tente novamente", Toast.LENGTH_SHORT).show();
                    fala = "invalido";
                }
        }
        return fala;
    }


    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.i(LOG_TAG, "FAILED " + errorMessage);
        returnedError.setText(errorMessage);

        // rest voice recogniser
        resetSpeechRecognizer();
        speech.startListening(recognizerIntent);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
        botao.setBackgroundResource(R.color.Green);
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        //Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
    }

    public String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
    public void startSecondActivity(View view) {

        Intent secondActivity = new Intent(this, Main1Activity.class);
        startActivity(secondActivity);
    }
    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        talker = new Talker();
        listener = new Listener();
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(getRosHostname());
        nodeConfiguration.setMasterUri(getMasterUri());
        nodeMainExecutor.execute(talker, nodeConfiguration);
        nodeMainExecutor.execute(listener, nodeConfiguration);
    }
}

