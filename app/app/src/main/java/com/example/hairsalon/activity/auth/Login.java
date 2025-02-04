package com.example.hairsalon.activity.auth;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.hairsalon.R;
import com.example.hairsalon.activity.employee.HomeEmployee;
import com.example.hairsalon.api.ApiService;
import com.example.hairsalon.model.AuthenticationRequest;
import com.example.hairsalon.activity.home.HomeCustomer;
import com.example.hairsalon.model.ResponseAuthData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    AppCompatButton btnLogin;
    TextView txtForgetPassword,txtRegister;
    ImageView passwordIcon;
    RelativeLayout signInWithOTP;
    private boolean passwordShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setControl();
        setEvent();
    }

    private void setControl(){
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtForgetPassword = findViewById(R.id.txtForgetPassword);
        txtRegister = findViewById(R.id.txtRegister);
        signInWithOTP = findViewById(R.id.signInOTP);
        passwordIcon = findViewById(R.id.passwordIcon);

    }

    private  void setEvent(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        txtForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgetPassword.class);
                startActivity(intent);
            }
        });
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
        passwordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passwordShow){
                    passwordShow = false;
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordIcon.setImageResource(R.drawable.show_password);
                }
                else {
                    passwordShow = true;
                    edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordIcon.setImageResource(R.drawable.hide_password);
                }
                edtPassword.setSelection(edtPassword.length());
            }
        });

        signInWithOTP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent intent = new Intent(Login.this, ChangePassword.class);
                startActivity(intent);
            }
        });

    }

    private void login() {
        String email, pass;
        email = edtEmail.getText().toString();
        pass = edtPassword.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Vui lòng nhập email!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isValidEmail(email)){
            Toast.makeText(this, "Vui lòng nhập email hợp lệ!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pass)){
            Toast.makeText(this, "Vui lòng nhập mật khẩu!",Toast.LENGTH_SHORT).show();
            return;
        }
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(email ,pass);
        ApiService.apiService.authenticateUser(authenticationRequest).enqueue(new Callback<ResponseAuthData>() {
            @Override
            public void onResponse(Call<ResponseAuthData> call, Response<ResponseAuthData> response) {
                if (response.isSuccessful()) {
                    ResponseAuthData responseAuthData = response.body();
                    SharedPreferences prefs = getSharedPreferences("User", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("userId", responseAuthData.getAccountId());
                    editor.putString("role", responseAuthData.getRole());
                    editor.putString("access_token", responseAuthData.getAccess_token());
                    editor.apply();
                    if(responseAuthData.getRole().equals("ADMIN")) {
                        Toast.makeText(getApplicationContext(), "Thông tin đăng nhập không chính xác!",Toast.LENGTH_SHORT).show();
                    } else if (responseAuthData.getRole().equals("CUSTOMER")){
                        Toast.makeText(getApplicationContext(), "Đăng nhập thành công!",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, HomeCustomer.class);
                        startActivity(intent);
                    } else if (responseAuthData.getRole().equals("EMPLOYEE")){
                        Toast.makeText(getApplicationContext(), "Đăng nhập thành công!",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, HomeEmployee.class);
                        startActivity(intent);
                    }
                    Log.e("Error", "login complete");
                } else {
                    Log.e("Error", "login failed: ");
                    Toast.makeText(getApplicationContext(), "Thông tin đăng nhập không chính xác!",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseAuthData> call, Throwable t) {
                Log.e("Error", "API call failed: " + t.getMessage());
                Toast.makeText(getApplicationContext(), "Không thể kết nối đến máy chủ. Vui lòng thử lại!",Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isValidEmail(CharSequence target) {
        return (target != null && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

}