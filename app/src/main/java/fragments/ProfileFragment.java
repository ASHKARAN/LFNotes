package fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import ir.ashkaran.lfnotes.LoginActivity;
import ir.ashkaran.lfnotes.R;
import app.*;
public class ProfileFragment extends Fragment {

    AppCompatEditText email , fname , lname ;
    AppCompatImageView avatar ;
    AppCompatTextView logout;
    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view =  inflater.inflate(R.layout.fragment_profile, container, false);


       return init(view);
    }


    private View init(View view) {


        email = view.findViewById(R.id.email);
        fname = view.findViewById(R.id.fname);
        lname = view.findViewById(R.id.lname);
        avatar= view.findViewById(R.id.avatar);
        logout= view.findViewById(R.id.logout);




        email.setText(spref.get().getString(ROUTER.INPUT_EMAIL , ""));
        fname.setText(spref.get().getString(ROUTER.INPUT_FNAME , ""));
        lname.setText(spref.get().getString(ROUTER.INPUT_LNAME , ""));


        Glide.with(Application.getContext())
                .load(app.main.URL + spref.get().getString("image" , ""))

                .apply( new RequestOptions()
                        .placeholder(R.drawable.ic_person_black_24dp)
                        .diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(avatar);



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Log Out ?");
                alert.setMessage("Do you really want to log Out ?");
                alert.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        spref.get().edit().clear().apply();
                        startActivity(new Intent(getActivity() , LoginActivity.class));
                        getActivity().finish();
                        
                    }
                });
                alert.setPositiveButton("No" , null);
                alert.show();
            }
        });

        return view ;
    }


}
