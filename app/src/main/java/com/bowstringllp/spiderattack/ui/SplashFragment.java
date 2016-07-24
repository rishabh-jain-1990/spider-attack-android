package com.bowstringllp.spiderattack.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.bowstringllp.spiderattack.R;
import com.bowstringllp.spiderattack.ui.activity.GameActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SplashFragment extends Fragment {


    public SplashFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_splash, container, false);

       view.findViewById(R.id.help_image).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO SHow help
            }
        });

        view.findViewById(R.id.play_image).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GameActivity) getActivity()).showGameView();
            }
        });

        return view;
    }

}
