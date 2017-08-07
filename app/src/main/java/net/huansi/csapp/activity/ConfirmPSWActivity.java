package net.huansi.csapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import net.huansi.csapp.R;

public class ConfirmPSWActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_psw);
    }
    public void commit(View v){
        Intent intent=new Intent(this,ResetPSWActivity.class);
        startActivity(intent);
    }
}
