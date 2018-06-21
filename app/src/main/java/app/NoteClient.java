package app;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class NoteClient {


    private  static AsyncHttpClient client = new AsyncHttpClient();



    public static  void post(RequestParams params , AsyncHttpResponseHandler handler) {
        client.post(app.main.URL  , params , handler);
    }






}
