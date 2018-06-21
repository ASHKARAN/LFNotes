package ir.ashkaran.lfnotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.tech.NfcB;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import app.app;
import cz.msebera.android.httpclient.Header;
import interfaces.NoteStateChangeListener;
import objects.LoginObject;
import objects.NoteObject;
import app.*;
public class ShowNoteActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatImageView back , icon , state , delete ;
    AppCompatTextView title , message ;
    FloatingActionButton edit ;
    SpinKitView progressBar ;

    NoteObject noteObject ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_note);
        initViews();
        init() ;
    }

    private void  initViews() {

        noteObject = (NoteObject) getIntent().getSerializableExtra("object");

        back        = findViewById(R.id.back);
        icon        = findViewById(R.id.icon);
        state       = findViewById(R.id.state);
        delete      = findViewById(R.id.delete);
        title       = findViewById(R.id.title);
        message     = findViewById(R.id.message);
        edit        = findViewById(R.id.edit);
        progressBar = findViewById(R.id.progressBar);


        title.setTypeface(MainActivity.font);
        message.setTypeface(MainActivity.font);
        back.setOnClickListener(this);
        delete.setOnClickListener(this);
        state.setOnClickListener(this);
        edit.setOnClickListener(this);


    }


    private void init() {
        title.setText(noteObject.getTitle());
        message.setText(noteObject.getMessage());
        icon.setImageDrawable(app.getImage(noteObject));
        state.setImageResource(
                noteObject.getSeen()==1?R.drawable.ic_remove_red_eye_black_24dp:R.drawable.ic_visibility_off_black_24dp
        );
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back : {
                finish();
                break;
            }
            case R.id.delete : {
                startDelete() ;
                break;
            }
            case R.id.state :{
                changeState() ;
                break;
            }
            case R.id.edit : {
                Intent intent = new Intent(this , EditNoteActivity.class);
                intent.putExtra("object" , noteObject);
                startActivity(intent);
                break;
            }
        }
    }


    private void startDelete() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete Note");
        alert.setMessage("Are you sure? \nDo you realy want to delete this note?\nThis action cannot be undone !");
        alert.setNegativeButton("Yes, Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                delete();
            }
        });
        alert.setPositiveButton("No" , null);
        alert.show();
    }



    private void delete () {


        RequestParams params = app.getRequestParams();
        params.put(ROUTER.ROUTE , ROUTER.ROUTE_NOTES);
        params.put(ROUTER.ACTION , ROUTER.ACTION_DELETE);
        params.put(ROUTER.INPUT_NOTE_ID , noteObject.getId());
        NoteClient.post(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);


                if(response.equals(ROUTER.SUCCESS)) {
                    NoteBroadCast.send(NoteBroadCast.ACTION_DELETE , noteObject);
                    finish();

                }
                else
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.has("error")) {
                            app.t(jsonObject.getString("error") , app.ToastType.ERROR);
                        }
                    } catch (JSONException e) {

                        app.l(e.toString());
                    }
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

    private void changeState() {


        app.changeNoteState(0 , noteObject, new NoteStateChangeListener() {
            @Override
            public void onChange(int position, int noteID, int state , Boolean success) {

                if(success) {
                    noteObject.setSeen(state);
                    NoteBroadCast.send(position , NoteBroadCast.ACTION_EDIT , noteObject);
                    ShowNoteActivity.this.state.setImageResource(
                            noteObject.getSeen()==1?R.drawable.ic_remove_red_eye_black_24dp:R.drawable.ic_visibility_off_black_24dp
                    );
                }
            }

            @Override
            public void onStart() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });


    }
}
