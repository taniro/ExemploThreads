package br.com.ufrn.eaj.tads.exemplothreads;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    ImageView img;
    Bitmap b = null;

    /*
    * Variáveis usadas do exemplo onClick3
     */
    protected static final int MENSAGEM_TESTE = 1;
    private Handler testeHandler = new TesteHandler();
    private Handler handler = new Handler();

    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = (ImageView) findViewById(R.id.imageView);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        /*
        Impossível carregar uma imagem da internet dentro da Ui Thread
        android.os.NetworkOnMainThreadException
        Esse trecho não funcionaria
        try {
            b = loadImageFromNetwork("http://tads.eaj.ufrn.br/projects/tads.png");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    /*
    * Esse método não funciona. É apenas um exemplo ilustrando a impossibilidade de acessar
    * elementos da view fora da UI Thread
    * " Only the original thread that created a view hierarchy can touch its views."
     */

    public void onClick1(View v){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    b = loadImageFromNetwork("http://tads.eaj.ufrn.br/projects/tads.png");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                img.setImageBitmap(b);
            }
        }).start();
    }
    /*
    * Esse método funciona mas começa a ser complicado de compreender
     */

    public void onClick2(View v){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                   b = loadImageFromNetwork("http://tads.eaj.ufrn.br/projects/tads.png");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                img.post(new Runnable() {
                    @Override
                    public void run() {
                        img.setImageBitmap(b);
                    }
                });
            }
        }).start();
    }

    public void onClick3(View v){
        Message msg = new Message();
        msg.what = MENSAGEM_TESTE;
        testeHandler.sendMessage(msg);
    }

    public void onClick4(View v){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), "A mensagem chegou com Runnable!",Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            b = loadImageFromNetwork("http://tads.eaj.ufrn.br/projects/tads.png");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                img.setImageBitmap(b);
            }
        });

    }

    public void onClick5(View v){
        downloadImagem();
    }

    public void downloadImagem(){

        Toast.makeText(this,"Download!",Toast.LENGTH_SHORT).show();
        // Zera a imagem para dar o efeito ao baixar novamente
        img.setImageBitmap(null);
        progressBar.setVisibility(View.VISIBLE);

        new Thread(){
            @Override
            public void run() {
                try {
                    b = loadImageFromNetwork("http://tads.eaj.ufrn.br/projects/tads.png");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Atualiza a imagem
                            progressBar.setVisibility(View.INVISIBLE);
                            img.setImageBitmap(b);
                        }
                    });
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            downloadImagem();
                        }
                    }, 5000);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    Bitmap loadImageFromNetwork(String url) throws IOException {
        Bitmap bitmap = null;
        InputStream in = new URL(url).openStream();
        // Converte a InputStream do Java para Bitmap
        bitmap = BitmapFactory.decodeStream(in);
        in.close();
        return bitmap;
    }

    private class TesteHandler extends Handler {

        protected static final int MENSAGEM_TESTE = 1;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MENSAGEM_TESTE:{
                    Toast.makeText(MainActivity.this, "Chegou a mensagem !", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                b = loadImageFromNetwork("http://tads.eaj.ufrn.br/projects/tads.png");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    img.setImageBitmap(b);
                    break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
