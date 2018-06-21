package ir.ashkaran.lfnotes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.RadioGroup;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ParameterMetaData;

import app.*;
import cz.msebera.android.httpclient.Header;
import objects.LoginObject;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {


    AppCompatEditText email , password , rePassword , fname , lname ;
    RadioGroup sex ;
    AppCompatTextView register , login ;

    SpinKitView progressBar ;

    int sexInt = 1 ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        init() ;
    }


    private void init() {

        email       = findViewById(R.id.email);
        password    = findViewById(R.id.password);
        rePassword  = findViewById(R.id.rePassword);
        fname       = findViewById(R.id.fname);
        lname       = findViewById(R.id.lname);
        sex         = findViewById(R.id.sex);

        register    = findViewById(R.id.register);
        login       = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar);

        register.setOnClickListener(this);
        login.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view == register) {
            Register();
        }
        else {
            finish();
        }
    }


    private void Register() {

        if(email.getText().toString().length()<5) {
            app.t("Please enter your email" , app.ToastType.ERROR);
            app.animateError(email);
            return;
        }

        if(fname.getText().toString().length()<3) {
            app.t("Please enter your first Name" , app.ToastType.ERROR);
            app.animateError(fname);
            return;
        }

        if(lname.getText().toString().length()<3) {
            app.t("Please enter your last name" , app.ToastType.ERROR);
            app.animateError(lname);
            return;
        }

        if(password.getText().toString().length()<3) {
            app.t("Please enter a password more than 3 chars" , app.ToastType.ERROR);
            app.animateError(password);
            return;
        }

        if(!password.getText().toString().equals(rePassword.getText().toString())) {
            app.t("Password not matched " , app.ToastType.ERROR);
            app.animateError(password);
            app.animateError(rePassword);
            return;
        }

        /*
         female = 0
         male = 1
         other = 2
         */

        if(sex.getCheckedRadioButtonId() == R.id.female) sexInt = 0 ;
        else if(sex.getCheckedRadioButtonId() == R.id.other) sexInt = 2;

        RequestParams params = app.getRequestParams();
        params.put(ROUTER.ROUTE         , ROUTER.ROUTE_LOGIN);
        params.put(ROUTER.ACTION        , ROUTER.ACTION_REGISTERATION);
        params.put(ROUTER.INPUT_EMAIL   , email.getText().toString());
        params.put(ROUTER.INPUT_PASSWORD, password.getText().toString());
        params.put(ROUTER.INPUT_FNAME   , fname.getText().toString());
        params.put(ROUTER.INPUT_LNAME   , lname.getText().toString());
        params.put(ROUTER.INPUT_SEX     , sexInt+"");

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
                            String name = fname.getText().toString() + " " +  lname.getText().toString();
                            app.t( "Welcome " + name, app.ToastType.SUCCESS);

                            spref.get().edit()
                                    .putInt("nightMode"    , loginObject.getSettingsObject().getNightMode())
                                    .putString("font"      , loginObject.getSettingsObject().getFont())
                                    .putInt("bgColor"      , loginObject.getSettingsObject().getBgColor())
                                    .putInt("textColor"    , loginObject.getSettingsObject().getTextColor())
                                    .putInt("fontSize"     ,  loginObject.getSettingsObject().getFontSize())
                                    .putInt("avatarID"     ,  loginObject.getSettingsObject().getAvatarObject().getId())
                                    .putString("name"      ,  loginObject.getSettingsObject().getAvatarObject().getName())
                                    .putString("image"     ,  loginObject.getSettingsObject().getAvatarObject().getImage())

                                    .apply();

                            app.l(ROUTER.SESSION    + loginObject.getSession());
                            app.l(ROUTER.USER_ID    + loginObject.getId());
                            spref.get().edit()
                                    .putString(ROUTER.SESSION     , loginObject.getSession())
                                    .putString(ROUTER.INPUT_EMAIL     , email.getText().toString())
                                    .putInt(ROUTER.USER_ID        , loginObject.getId())
                                    .putString(ROUTER.INPUT_FNAME , fname.getText().toString())
                                    .putString(ROUTER.INPUT_LNAME , lname.getText().toString())
                                    .putInt(ROUTER.INPUT_SEX      , sexInt)
                                    .apply();

                            Intent intent = new Intent(RegistrationActivity.this , MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }
                        else {

                        }
                    }

                } catch (JSONException e) {

                    app.l(e.toString());
                }




                app.l("response : " + response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                app.l("onFailure");
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
