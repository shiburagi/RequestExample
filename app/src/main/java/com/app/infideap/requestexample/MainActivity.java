package com.app.infideap.requestexample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private String url = "https://jsonplaceholder.typicode.com/posts";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView_result);
    }

    public void openConnection(View view) {
        request(0);
    }

    public void ion(View view) {
        request(1);
    }

    public void okHttp(View view) {
        request(2);
    }

    private void request(final int type) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                final Request request = Request.getInstance();
                String result = null;
                switch (type) {
                    case 1:
                        try {
                            result = request.requestIon(
                                    MainActivity.this,
                                    url,
                                    Request.METHOD_GET,
                                    null

                            );
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        try {
                            result = request.requestOkHttp(
                                    url,
                                    Request.METHOD_GET,
                                    null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        try {
                            result = request.openConnection(
                                    url,
                                    Request.METHOD_GET,
                                    null
                            );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                }
                final String finalResult = result;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(String.format(
                                Locale.getDefault(),
                                "Time : %d ms\n\nResponse :\n",
                                request.getResponseTime()
                        ));

                        if (finalResult != null)
                            textView.append(finalResult);
                    }
                });
                return null;
            }
        }.execute();
    }
}
