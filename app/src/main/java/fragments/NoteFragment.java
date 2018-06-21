package fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adapter.NoteAdapter;
import app.NoteClient;
import app.*;
import cz.msebera.android.httpclient.Header;
import ir.ashkaran.lfnotes.AddNoteActivity;
import ir.ashkaran.lfnotes.LoginActivity;
import ir.ashkaran.lfnotes.MainActivity;
import ir.ashkaran.lfnotes.R;
import objects.LoginObject;
import objects.NoteObject;


public class NoteFragment extends Fragment {


    int start = 0  ;
    ShimmerRecyclerView recyclerView;
    List<NoteObject> list ;
    NoteAdapter adapter ;
    FloatingActionButton add ;

    AppCompatTextView noNote;
    public NoteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view =  inflater.inflate(R.layout.fragment_note, container, false);

       view = init(view);
        return view ;
    }


    private View init(View view) {

        recyclerView = view.findViewById(R.id.shimmer_recycler_view);
        noNote = view.findViewById(R.id.noNote);
        add = view.findViewById(R.id.add);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        list = new ArrayList<>();
        adapter = new NoteAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);
        getData();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity() , AddNoteActivity.class));
            }
        });

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver , new IntentFilter(app.main.TAG));
        return view;
    }


    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        super.onDestroyView();
    }

    private void getData() {
        RequestParams params = app.getRequestParams();
        params.put(ROUTER.ROUTE , ROUTER.ROUTE_NOTES);
        params.put(ROUTER.ACTION , ROUTER.ACTION_READ);
        params.put(ROUTER.INPUT_START , start);

        app.l("session : " + spref.get().getString(ROUTER.SESSION , "") );
        app.l("USER_ID : " + spref.get().getInt(ROUTER.USER_ID , -1) +"");
        NoteClient.post(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("error")) {
                        app.t(jsonObject.getString("error") , app.ToastType.ERROR);
                        if(jsonObject.has("action") && jsonObject.getString("action").equals("logout")) {
                            spref.get().edit().clear();
                            getActivity().finish();
                            getActivity().startActivity(new Intent(getActivity() , LoginActivity.class));
                        }
                    }
                    else {


                    }

                } catch (JSONException e) {

                   //inja bayad data ro handle konim

                    app.l("response : " + response);

                    NoteObject [] objects = new Gson().fromJson(response , NoteObject[].class);
                    list.addAll(Arrays.asList(objects));


                    noNote.setVisibility(list.size()==0?View.VISIBLE:View.GONE);
                    adapter.notifyDataSetChanged();





                }



                app.l("response : " + response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                app.l("onFailure");
            }

            @Override
            public void onStart() {
                recyclerView.showShimmerAdapter();
                super.onStart();
            }

            @Override
            public void onFinish() {
               recyclerView.hideShimmerAdapter();
                super.onFinish();
            }
        });
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int action = intent.getIntExtra("action" , -1 );
            if(action == NoteBroadCast.ACTION_ADD) {
                NoteObject note = (NoteObject) intent.getSerializableExtra("object");

                List<NoteObject> objects = new ArrayList<>();
                objects.add(note);
                objects.addAll(list);
                list.clear();
                list.addAll(objects);
                adapter.notifyDataSetChanged();

                noNote.setVisibility(list.size()==0?View.VISIBLE:View.GONE);


            }
            else if(action == NoteBroadCast.ACTION_EDIT) {

                int position = intent.getIntExtra("position", 0);
                if(list.size()>position) {
                    NoteObject object = (NoteObject) intent.getSerializableExtra("object");
                    list.set(position , object);
                    adapter.notifyDataSetChanged();

                }

            }
            else if(action == NoteBroadCast.ACTION_DELETE) {
                NoteObject noteObject = (NoteObject) intent.getSerializableExtra("object");
                int len = list.size();
                for(int i = 0 ; i<len; i++) {
                    NoteObject note =  list.get(i);
                    if(note.getId() == noteObject.getId()) {
                        list.remove(i);
                        adapter.notifyDataSetChanged();

                        noNote.setVisibility(list.size()==0?View.VISIBLE:View.GONE);

                        break;
                    }
                }
            }
            app.l("i got BroadCast in NoteFragment");
        }
    };


}
