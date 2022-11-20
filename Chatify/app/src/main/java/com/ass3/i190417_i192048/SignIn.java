package com.ass3.i190417_i192048;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.onesignal.OneSignal;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignIn extends AppCompatActivity {

    TextView signup;
    EditText email, password;
    Button signin;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signup = findViewById(R.id.signup);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signin = findViewById(R.id.signin);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Signing In");
        progressDialog.setMessage("Please wait while we are signing you in");

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().equals("") || password.getText().toString().equals("")){
                    Toast.makeText(SignIn.this, "Please fill all the fields", Toast.LENGTH_LONG).show();
                } else{
                    progressDialog.show();
                    String emailStr = email.getText().toString();
                    String passwordStr = password.getText().toString();
                    String deviceID = OneSignal.getDeviceState().getUserId();
                    OkHttpClient okhttpclient = new OkHttpClient();


                    RequestBody body = new FormBody.Builder()
                            .add("email", emailStr)
                            .add("password", passwordStr)
                            .add("deviceID", deviceID)
                            .build();

                    Request request = new Request.Builder().url("http://10.0.2.2:5000/signin").post(body).build();
                    okhttpclient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SignIn.this, "Server Not Working", Toast.LENGTH_SHORT).show();
                                    Log.d("error", e.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            String responseStr = response.body().string();
                            if (responseStr.contains("User signed in successfully")){
                                progressDialog.dismiss();
                                Intent intent = new Intent(SignIn.this, ChatMainScreen.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(SignIn.this, "Error Signing in Account", Toast.LENGTH_SHORT).show();
                            }
                        }


                    });
                }
            }
        });





    }
    
}