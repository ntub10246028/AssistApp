package com.example.apple.assistapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView t=(TextView)findViewById(R.id.mytextview);


        TelephonyManager tM=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);

        final String imei = tM.getDeviceId();
        final Context ctx=this.getApplicationContext();
        final MyHttpClient client=new MyHttpClient(ctx);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SignatureApp sa = new SignatureApp(ctx, R.raw.sign);
                String session=null;

                while (!sa.isSuccess()) {
                    session=sa.postSignature(imei,client);
                }

                try {
                    //HttpClient client =new MyHttpClient(ctx);
                    HttpGet hg = new HttpGet("https://app.lambda.tw/session");
                    hg.setHeader("lack.session",session);
                    Log.d(session, hg.getFirstHeader("lack.session").toString());
                    HttpResponse response = client.execute(hg);
                    HttpEntity entity = response.getEntity();
                    Log.d("xxxxxxxxxxxxxx", EntityUtils.toString(entity));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Log.d("over",imei);
            }
        }).start();
/*
        new Thread(new Runnable(){
            @Override
            public void run() {
                JsonReaderPost j = new JsonReaderPost(getApplicationContext());
                try {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("query","SELECT+AlertId+FROM+Orion.Alerts"));
                    j.Reader(params);
                    Log.d("DEBUG", "reader");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

