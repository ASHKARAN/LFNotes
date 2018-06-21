package ir.ashkaran.lfnotes;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.ybq.android.spinkit.SpinKitView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import app.*;
import cz.msebera.android.httpclient.Header;
import objects.NoteObject;

public class AddNoteActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatImageView back , save ;
    AppCompatEditText title , message ;

    SpinKitView progressBar ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        init() ;
    }


    private void init() {
        back = findViewById(R.id.back);
        save = findViewById(R.id.save);
        title = findViewById(R.id.title);
        message = findViewById(R.id.message);
        progressBar = findViewById(R.id.progressBar);
        title.setText(spref.get().getString(ROUTER.INPUT_TITLE , ""));
        message.setText(spref.get().getString(ROUTER.INPUT_MESSAGE , ""));
        title.setTypeface(MainActivity.font);
        message.setTypeface(MainActivity.font);
        back.setOnClickListener(this);
        save.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if(view == back) {
            spref.get().edit()
                    .putString(ROUTER.INPUT_TITLE , title.getText().toString())
                    .putString(ROUTER.INPUT_MESSAGE , message.getText().toString())
                    .apply();
            finish();
        }
        else {
            saveNote();
        }
    }


    private void saveNote() {

        if(title.getText().toString().length()==0) {
            app.t("Please enter a title" , app.ToastType.ERROR);
            app.animateError(title);
            return;
        }

        if(message.getText().toString().length()==0) {
            app.t("Please enter a Note" , app.ToastType.ERROR);
            app.animateError(message);
            return;
        }


        RequestParams params = app.getRequestParams();
        params.put(ROUTER.ROUTE , ROUTER.ROUTE_NOTES);
        params.put(ROUTER.ACTION , ROUTER.ACTION_ADD);
        params.put(ROUTER.INPUT_TITLE , title.getText().toString());
        params.put(ROUTER.INPUT_MESSAGE , message.getText().toString());


        NoteClient.post(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);

                try {
                    JSONObject object = new JSONObject(response);

                    if(object.has("error")) {
                        app.t(object.getString("error") , app.ToastType.ERROR);
                    }
                    else if(object.has("status") && object.getString("status").equals("SUCCESS")){
                        int id = object.getInt("id");
                        spref.get().edit().remove(ROUTER.INPUT_TITLE).remove(ROUTER.INPUT_MESSAGE).apply();
                        NoteObject noteObject = new NoteObject(id ,
                                spref.get().getInt(ROUTER.USER_ID , -1) ,
                                title.getText().toString() , message.getText().toString() , 0 , "");
                        NoteBroadCast.send(NoteBroadCast.ACTION_ADD , noteObject);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                app.l("response  : " + response);
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
