package com.lw;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lw.widget.circlelayout.CircleLayout;

/**
 * Created by yjwfn on 16-1-11.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.actigity_main);
    }


    public void progressBar(View view){
        startActivity(RoundedProgressActivity.class);
    }

    public void circleLayout(View view){
        startActivity(CircleLayoutActivity.class);
    }

    private void startActivity(Class clz){
        Intent intent = new Intent(this, clz);
        startActivity(intent);
    }
}
