package ir.ashkaran.lfnotes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import app.*;
import cz.msebera.android.httpclient.Header;
import objects.ErrorObject;
import objects.LoginObject;
import app.spref ;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{


    AppCompatEditText email , password ;
    AppCompatTextView login , register ;
    SpinKitView progressBar ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        progressBar = findViewById(R.id.progressBar);

        login.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == login.getId()) {
            login();
        }
        else {
            startActivity(new Intent(this , RegistrationActivity.class));
        }
    }


    private void login() {


        if(email.getText().toString().length()<5) {
            app.t("Please enter your email" , app.ToastType.ERROR);
            app.animateError(email);
            return;
        }

        if(password.getText().toString().length()<3) {
            app.t("Please enter at least 3 chars as password" , app.ToastType.ERROR);
            app.animateError(password);
            return;
        }


        RequestParams params = app.getRequestParams();
        params.put(ROUTER.ROUTE , ROUTER.ROUTE_LOGIN);
        params.put(ROUTER.ACTION , ROUTER.ACTION_LOGIN);
        params.put(ROUTER.INPUT_EMAIL , email.getText().toString());
        params.put(ROUTER.INPUT_PASSWORD , password.getText().toString());


        NoteClient.post(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody);

                try {

                    JSONObject jsonObject = new JSONObject(response);
                     if(jsonObject.has("error")) {
                         app.t(jsonObject.getString("error") , app.ToastType.ERROR);
                     }
                     else {
                         LoginObject loginObject = new Gson().fromJson(response , LoginObject.class);


                         if(loginObject.getState().equals(ROUTER.SUCCESS)) {
                             String name = loginObject.getUserObject().getFNAME() + " " +  loginObject.getUserObject().getLNAME();
                             app.t( "Welcome " + name, app.ToastType.SUCCESS);

                             spref.get().edit()
                                     .putInt("nightMode"    ,  loginObject.getSettingsObject().getNightMode())
                                     .putString("font"      ,  loginObject.getSettingsObject().getFont())
                                     .putInt("bgColor"      ,  loginObject.getSettingsObject().getBgColor())
                                     .putInt("textColor"    ,  loginObject.getSettingsObject().getTextColor())
                                     .putInt("fontSize"     ,  loginObject.getSettingsObject().getFontSize())
                                     .putInt("avatarID"     ,  loginObject.getSettingsObject().getAvatarObject().getId())
                                     .putString("name"      ,  loginObject.getSettingsObject().getAvatarObject().getName())
                                     .putString("image"     ,  loginObject.getSettingsObject().getAvatarObject().getImage())

                                     .apply();

                             app.l(ROUTER.SESSION + loginObject.getUserObject().getSESSION());
                             app.l(ROUTER.USER_ID + loginObject.getUserObject().getUSER_ID());

                             spref.get().edit()
                                     .putString(ROUTER.SESSION , loginObject.getUserObject().getSESSION())
                                     .putString(ROUTER.INPUT_EMAIL , email.getText().toString())
                                     .putInt(ROUTER.USER_ID , loginObject.getUserObject().getUSER_ID())
                                     .putString(ROUTER.INPUT_FNAME , loginObject.getUserObject().getFNAME())
                                     .putString(ROUTER.INPUT_LNAME , loginObject.getUserObject().getLNAME())
                                     .putInt(ROUTER.INPUT_SEX , loginObject.getUserObject().getSEX())
                                     .apply();

                             Intent intent = new Intent(LoginActivity.this , MainActivity.class);
                             intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                              startActivity(intent);

                         }
                         else {

                         }
                     }

                } catch (JSONException e) {

                    app.l(e.toString());
                }


                app.l(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

            @Override
            public void onStart() {
                progressBar.setVisibility(View.VISIBLE);
                super.onStart();
            }

            @Override
            public void onFinish() {
                progressBar.setVisibility(View.INVISIBLE);
                super.onFinish();
            }
        });















    }





}
