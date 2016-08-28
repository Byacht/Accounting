package com.example.dn.accounting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ShowDetailActivity extends AppCompatActivity {

    private TextView showEvent;
    private TextView showCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);

        showEvent = (TextView) findViewById(R.id.showevent);
        showCost = (TextView) findViewById(R.id.showcost);

        String event = getIntent().getStringExtra("event");
        float cost = getIntent().getFloatExtra("cost",0);
        String costToString = String.valueOf(cost);

        showEvent.setText(event);
        showCost.setText(costToString);

    }
}
