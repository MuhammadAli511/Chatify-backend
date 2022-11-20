package com.ass3.i190417_i192048;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.onesignal.OneSignal;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUp extends AppCompatActivity {

    TextView signin;
    ImageView profilePic;
    EditText name, email, password, phoneNum;
    CheckBox checkbox;
    RadioGroup genderRadioGroup;
    Button signup;
    ProgressDialog progressDialog;
    RadioButton genderRadioButton;
    Uri imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signin = findViewById(R.id.signin);
        profilePic = findViewById(R.id.profilePic);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phoneNum = findViewById(R.id.phoneNum);
        checkbox = findViewById(R.id.checkbox);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        signup = findViewById(R.id.signup);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("Please wait while we create your account");

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 80);
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, SignIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                SignUp.this.finish();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkbox.isChecked()){
                    Toast.makeText(SignUp.this, "Please accept terms and conditions", Toast.LENGTH_LONG).show();
                } else {
                    progressDialog.show();
                    int selectedId = genderRadioGroup.getCheckedRadioButtonId();
                    genderRadioButton = findViewById(selectedId);
                    String gender = genderRadioButton.getText().toString();
                    if (name.getText().toString().equals("") || email.getText().toString().equals("") || password.getText().toString().equals("") || phoneNum.getText().toString().equals("")){
                        Toast.makeText(SignUp.this, "Please fill all the fields", Toast.LENGTH_LONG).show();
                    } else {
                        MediaManager.get().upload(imageURI).callback(new UploadCallback() {
                            @Override
                            public void onStart(String requestId) {}
                            @Override
                            public void onProgress(String requestId, long bytes, long totalBytes) {}
                            @Override
                            public void onSuccess(String requestId, Map resultData) {
                                String status = "Offline";
                                String imageURL = resultData.get("url").toString();
                                String emailStr = email.getText().toString();
                                String passwordStr = password.getText().toString();
                                String nameStr = name.getText().toString();
                                String phoneNumStr = phoneNum.getText().toString();
                                String deviceID = OneSignal.getDeviceState().getUserId();
                                OkHttpClient okhttpclient = new OkHttpClient();


                                RequestBody body = new FormBody.Builder()
                                        .add("name", nameStr)
                                        .add("email", emailStr)
                                        .add("password", passwordStr)
                                        .add("gender", gender)
                                        .add("phone", phoneNumStr)
                                        .add("profileUrl", imageURL)
                                        .add("userStatus", status)
                                        .add("deviceID", deviceID)
                                        .build();

                                Request request = new Request.Builder().url("http://10.0.2.2:5000/signup").post(body).build();
                                okhttpclient.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(SignUp.this, "Server Not Working", Toast.LENGTH_SHORT).show();
                                                Log.d("error", e.getMessage());
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                        String responseStr = response.body().string();
                                        if (responseStr.contains("User created successfully")){
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(SignUp.this, SignIn.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);

                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(SignUp.this, "Error Creating Account", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                            }
                            @Override
                            public void onError(String requestId, ErrorInfo error) {
                                Toast.makeText(SignUp.this, "Image upload failed", Toast.LENGTH_LONG).show();
                            }
                            @Override
                            public void onReschedule(String requestId, ErrorInfo error) {}
                        }).dispatch();

                    }
                }

            }
        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 80 && resultCode == RESULT_OK) {
            profilePic.setImageURI(data.getData());
            imageURI = data.getData();
        }
    }
}