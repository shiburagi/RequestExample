package com.app.infideap.requestexample;

import android.content.Context;

import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Shiburagi on 03/11/2016.
 */

public class Request {
    private static final String TAG = Request.class.getSimpleName();

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_HEAD = "HEAD";
    public static final String METHOD_OPTIONS = "OPTIONS";
    public static final String METHOD_TRACE = "TRACE";
    private static int TIMEOUT_MILISECONDS = 10000;

    private static final Request instance;

    static {
        instance = new Request();
    }

    private long responseTime;

    public static Request getInstance() {
        return instance;
    }

    private Request() {

    }

    public String openConnection(String url, String method, String json) throws IOException {
        responseTime = 0;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        String USER_AGENT = "Mozilla/5.0";
        //add reuqest header
        con.setRequestMethod(method);
        con.setRequestProperty("User-Agent", USER_AGENT);

        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type", "application/json");

        if (METHOD_POST.equals(method) || METHOD_PUT.equals(method)) {

            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            byte[] bytes = json.toString().getBytes();

            wr.write(bytes);
            wr.flush();
            wr.close();


        }
        long millis = System.currentTimeMillis();
        con.connect();
        InputStream in;
        int status = con.getResponseCode();

        if (status >= 400)
            in = con.getErrorStream();
        else
            in = con.getInputStream();

        String result = getResponse(in).toString();
        responseTime = System.currentTimeMillis() - millis;
        return result;
    }

    /**
     * To get response/feedback from URL/REST API
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private StringBuilder getResponse(InputStream inputStream) throws IOException {

        BufferedReader in = new BufferedReader(
                new InputStreamReader(inputStream));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response;
    }


    public String requestIon(Context context, String url, String method, Object object) throws ExecutionException, InterruptedException {
        responseTime = 0;
        Builders.Any.B b = Ion.with(context).load(method, url);
        if (object != null) {
            b.setJsonPojoBody(object);
        }
        long millis = System.currentTimeMillis();
        String response = b.asString().get();
        responseTime = System.currentTimeMillis() - millis;
        return response;
    }


    public String requestOkHttp(String url, String method, String json) throws IOException {
        responseTime = 0;
        RequestBody requestBody;
        requestBody = METHOD_POST.equals(method) || METHOD_PUT.equals(method) ?
                RequestBody.create(JSON, json == null ? "" : json) : null;

        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
                .url(url);

        requestBuilder.method(method, requestBody);

        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(requestBuilder.build());

        long millis = System.currentTimeMillis();
        Response response = call.execute();
        String result = response.body().string();
        responseTime = System.currentTimeMillis() - millis;
        return result;
    }

    public long getResponseTime() {
        return responseTime;
    }
}
