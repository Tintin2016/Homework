package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    String amount;
    EditText Money,Person;
    TextView Result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Money=(EditText) findViewById(R.id.EtMooney);
        Person=(EditText) findViewById(R.id.EtPerson);
        Result=(TextView) findViewById(R.id.Result);
    }

    public void Btn(View v) {
        Double total = Double.parseDouble(Money.getText().toString());
        Double persons = Double.parseDouble(Person.getText().toString());
        Double share = total / persons;
        share = Double.valueOf(Math.round(share * 100));
        share = share / 100;
        Result.setText(String.valueOf(share));
    }
}
