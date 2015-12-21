package com.example.apple.assistapp.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.apple.assistapp.R;

/**
 * Created by v on 2015/12/20.
 */
public class Act_IssueArticle extends AppCompatActivity {

    private Context ctxt = Act_IssueArticle.this;
    private Button bt_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issuearticle);
        InitialUI();
    }

    private void InitialUI() {
        bt_add = (Button) findViewById(R.id.bt_add);
        bt_add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        });
    }
}
