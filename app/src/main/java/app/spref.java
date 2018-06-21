package app;

import android.content.Context;
import android.content.SharedPreferences;

public class spref {

    private static  SharedPreferences ourInstance = Application.getContext().getSharedPreferences(app.main.TAG , Context.MODE_PRIVATE);

    public static SharedPreferences get() {
        if(ourInstance == null) {
            ourInstance = Application.getContext().getSharedPreferences(app.main.TAG , Context.MODE_PRIVATE);
        }



        return ourInstance;
    }

}
