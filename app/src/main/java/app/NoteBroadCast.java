package app;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import objects.NoteObject;

public class NoteBroadCast {

    public static  final int ACTION_ADD    = 1;
    public static  final int ACTION_EDIT   = 2;
    public static  final int ACTION_DELETE = 3;

    public static void send(int action , NoteObject note) {
        //broadcasr the update

        Intent intent = new Intent(app.main.TAG);
        intent.putExtra("action" , action);
        intent.putExtra("object" , note);
        LocalBroadcastManager.getInstance(Application.getContext()).sendBroadcast(intent);
        app.l("BroadsCast send");

    }

    public static void send(int position , int action , NoteObject note) {
        //broadcasr the update

        Intent intent = new Intent(app.main.TAG);
        intent.putExtra("action" , action);
        intent.putExtra("object" , note);
        intent.putExtra("position" , position);
        LocalBroadcastManager.getInstance(Application.getContext()).sendBroadcast(intent);
        app.l("BroadsCast send");

    }




}
