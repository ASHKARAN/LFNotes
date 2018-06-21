package app;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;
import interfaces.NoteStateChangeListener;
import ir.ashkaran.lfnotes.MainActivity;
import objects.NoteObject;

public class app {


    public  enum ToastType {
        ERROR , WARNING , SUCCESS , NORMAL , INFO
    }
    public static class  main {
        public static  final String TAG = "LFNotes";
        public static  final String URL = "http://lfcode.ir/LFNote/";
    }


    public static void l (String message) {
        Log.e(main.TAG , message);
    }


    public static void t(String message , ToastType type) {

        switch (type) {
            case ERROR:       Toasty.error(Application.getContext()   , message , Toast.LENGTH_LONG).show();break;
            case WARNING:     Toasty.warning(Application.getContext() , message , Toast.LENGTH_LONG).show();break;
            case SUCCESS:     Toasty.success(Application.getContext() , message , Toast.LENGTH_LONG).show();break;
            case NORMAL:      Toasty.normal(Application.getContext()  , message , Toast.LENGTH_LONG).show();break;
            case INFO:        Toasty.info(Application.getContext()    , message , Toast.LENGTH_LONG).show();break;
        }
    }



    public static Drawable getImage(NoteObject note) {
        String text = note.getTitle().substring(0 , 1).toUpperCase();
         if(note.getTitle().length()>1)
             text = text.substring(0 , 1).toUpperCase();

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color2 = generator.getColor("id"+note.getId());

        TextDrawable drawable = TextDrawable.builder()
                .beginConfig().useFont(MainActivity.font)
                .endConfig()
                .buildRound(text, color2);

        return  drawable ;
    }



    public static RequestParams getRequestParams() {
        RequestParams params = new RequestParams();
        params.put(ROUTER.SESSION , spref.get().getString(ROUTER.SESSION , ""));
        params.put(ROUTER.USER_ID , spref.get().getInt(ROUTER.USER_ID , -1) + "");

        return params;
    }


    public static  void animateError(View view) {
        YoYo.with(Techniques.Pulse).duration(500).playOn(view);
    }

    public static void changeNoteState(final int position , final NoteObject object , final NoteStateChangeListener listener) {
        final int newState = object.getSeen()==0?1:0;
        RequestParams params = app.getRequestParams();
        params.put(ROUTER.ROUTE , ROUTER.ROUTE_NOTES);
        params.put(ROUTER.ACTION , ROUTER.ACTION_CHANGE_STATE);
        params.put(ROUTER.INPUT_NOTE_ID , object.getId());
        params.put(ROUTER.INPUT_STATE , newState);

        NoteClient.post(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);


                if(response.equals(ROUTER.SUCCESS)) {
                    listener.onChange(position , object.getId() , newState , true);
                    return;
                }

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.has("error")) {
                        app.t(jsonObject.getString("error") , app.ToastType.ERROR);
                        listener.onChange(position , object.getId() , 0 , false);
                    }
                    else {

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }



                app.l(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

            @Override
            public void onStart() {
                listener.onStart();
                super.onStart();
            }

            @Override
            public void onFinish() {
                listener.onFinish();
                super.onFinish();
            }
        });
    }


}

/*




TextDrawable drawable1 = TextDrawable.builder()
                .buildRoundRect("A", Color.RED, 10); // radius in px




 */