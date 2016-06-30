package com.lambda.assist.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;

import com.lambda.assist.Other.Hardware;
import com.lambda.assist.Other.IsVaild;
import com.lambda.assist.R;

public class Act_AlterMission extends AppCompatActivity implements View.OnClickListener {

    private EditText et_title, et_content;
    private ImageView iv_pic, iv_map, iv_time, iv_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter_mission);
        et_title = (EditText) findViewById(R.id.et_altermission_title);
        et_content = (EditText) findViewById(R.id.et_altermission_content);
        iv_pic = (ImageView) findViewById(R.id.iv_altermission_pic);
        iv_map = (ImageView) findViewById(R.id.iv_altermission_map);
        iv_time = (ImageView) findViewById(R.id.iv_altermission_time);
        iv_send = (ImageView) findViewById(R.id.iv_altermission_send);
        iv_pic.setOnClickListener(this);
        iv_map.setOnClickListener(this);
        iv_time.setOnClickListener(this);
        iv_send.setOnClickListener(this);
    }

    private int ol_hour = 24, ol_min = 0, ol_sec = 0;
    private int r_hour = 24, r_min = 0, r_sec = 0;
    private static final int ONLINE = 0;
    private static final int RUN = 1;

    private void TimeDialog(final View bt, final int mode) {
        AlertDialog.Builder b = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        View v = getLayoutInflater().inflate(R.layout.ui_timepicker, null);
        final NumberPicker np_hour = (NumberPicker) v.findViewById(R.id.np_hour);
        final NumberPicker np_min = (NumberPicker) v.findViewById(R.id.np_min);
        final NumberPicker np_sec = (NumberPicker) v.findViewById(R.id.np_sec);
        np_hour.setMaxValue(24);
        np_min.setMaxValue(59);
        np_sec.setMaxValue(59);
        np_hour.setValue(mode == ONLINE ? ol_hour : r_hour);
        np_min.setValue(mode == ONLINE ? ol_min : r_min);
        np_sec.setValue(mode == ONLINE ? ol_sec : r_sec);
        np_hour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldV, int newV) {
                // when hour equals 24 , min and sec change to 0 ;
                if (newV == 24) {
                    np_min.setValue(0);
                    np_sec.setValue(0);
                    if (mode == ONLINE) {
                        ol_min = 0;
                        ol_sec = 0;
                    } else {
                        r_min = 0;
                        r_sec = 0;
                    }
                }
                if (mode == ONLINE) {
                    ol_hour = newV;
                } else {
                    r_hour = newV;
                }
            }
        });
        np_min.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldV, int newV) {
                if (mode == ONLINE) {
                    ol_min = newV;
                } else {
                    r_min = newV;
                }
            }
        });
        np_sec.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldV, int newV) {
                if (mode == ONLINE) {
                    ol_sec = newV;
                } else {
                    r_sec = newV;
                }
            }
        });
        b.setPositiveButton(getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String result = IsVaild.isVail_TimePick(Act_AlterMission.this, np_hour.getValue(), np_min.getValue(), np_sec.getValue());
                if (result.equals(getResources().getString(R.string.default_time))) {
                    if (mode == ONLINE) {
                        ol_hour = 24;
                        ol_min = 0;
                        ol_sec = 0;
                    } else {
                        r_hour = 24;
                        r_min = 0;
                        r_sec = 0;
                    }
                }

                ((Button) bt).setText(result);
            }
        });
        b.setView(v);
        b.show();
    }

    private void PickTimeDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        View v = getLayoutInflater().inflate(R.layout.dialog_picktime, null);
        Button bt_onlinetime = (Button) v.findViewById(R.id.bt_picktime_onlinetime);
        Button bt_runtime = (Button) v.findViewById(R.id.bt_picktime_runtime);
        bt_onlinetime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TimeDialog(v, ONLINE);
            }
        });
        bt_runtime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TimeDialog(v, RUN);
            }
        });
        b.setPositiveButton(getResources().getString(R.string.button_ok), null);
        b.setView(v);
        b.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_altermission_pic) {

        } else if (id == R.id.iv_altermission_map) {

        } else if (id == R.id.iv_altermission_time) {
            Hardware.closeKeyBoard(this, v);
            PickTimeDialog();
        } else if (id == R.id.iv_altermission_send) {

        }
    }
}
