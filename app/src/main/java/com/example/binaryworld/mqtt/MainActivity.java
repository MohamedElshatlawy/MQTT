package com.example.binaryworld.mqtt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;


public class MainActivity extends AppCompatActivity implements MqttCallback {


    Button bt1,bt2,bt3;
    EditText e0,e1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        e0=findViewById(R.id.topic);
        e1=findViewById(R.id.msg);
        bt1=findViewById(R.id.bt1);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });
        
        bt2=findViewById(R.id.bt2);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publish();
            }
        });
        bt3=findViewById(R.id.bt3);
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscribe();
            }
        });
    }

    private void subscribe() {
        String topic = e0.getText().toString();
        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published

                    //Toast.makeText(MainActivity.this,asyncActionToken.getResponse().getMessageId(),Toast.LENGTH_LONG).show();
                    Toast.makeText(MainActivity.this,"SubscribeSuccess",Toast.LENGTH_LONG).show();
                    System.out.println(asyncActionToken.getTopics().length);
                    for(String a:asyncActionToken.getTopics()){
                        System.out.println("arr "+a);

                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards

                    Log.e("error",exception.getMessage());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void publish() {
        String topic = e0.getText().toString();
        String payload = e1.getText().toString();
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
            client.setCallback(this);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }
    MqttAndroidClient client;
    private void connect() {
        String clientId = MqttClient.generateClientId();

        client= new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883",
                        clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(MainActivity.this,"Connection Success...",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(MainActivity.this,"Connection Failed..."+exception.getMessage(),Toast.LENGTH_LONG).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Toast.makeText(this,"ConnectionLostTyYAgain...",Toast.LENGTH_LONG).show();

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        System.out.println("message: "+message.toString());
        Toast.makeText(MainActivity.this,"Message: "+message.toString(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}