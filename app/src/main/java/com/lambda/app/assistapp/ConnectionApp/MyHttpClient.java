package com.lambda.app.assistapp.ConnectionApp;

import android.content.Context;


import com.lambda.app.assistapp.R;

import java.io.InputStream;
import java.io.Serializable;
import java.security.KeyStore;


import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.SingleClientConnManager;

import org.apache.http.impl.client.DefaultHttpClient;

public class MyHttpClient extends DefaultHttpClient {
    private static MyHttpClient client = new MyHttpClient();
    private Context context;

    private MyHttpClient() {

    }

    public static MyHttpClient getMyHttpClient() {
        return client;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    protected ClientConnectionManager createClientConnectionManager() {
        SchemeRegistry registry = new SchemeRegistry();
        //Pour les requêtes HTTP, on laisse la classe de base s'en occuper.
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        // Les requêtes HTTPS se font sur le port 443. A chaque connexion HTTPS, c’est notre keystore qui sera utilisé.
        registry.register(new Scheme("https", newSslSocketFactory(), 5000));
        return new SingleClientConnManager(getParams(), registry);
    }

    private SSLSocketFactory newSslSocketFactory() {
        try {
            // On obtient une instance de notre KeyStore
            KeyStore trusted = KeyStore.getInstance("BKS");
            InputStream in = context.getResources().openRawResource(R.raw.clienttruststore);
            try {
                // Initialisation de notre keystore. On entre le mot de passe (storepass)
                trusted.load(in, "testtest".toCharArray());
            } finally {
                in.close();
            }

            // Passons le keystore au SSLSocketFactory qui est responsable de la verification du certificat
            SSLSocketFactory sf = new SSLSocketFactory(trusted);
            sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
            return sf;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}