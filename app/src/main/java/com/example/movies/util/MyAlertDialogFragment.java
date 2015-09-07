package com.example.movies.util;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.movies.R;

public class MyAlertDialogFragment extends AppCompatDialogFragment {

    public static final String TAG = MyAlertDialogFragment.class.getSimpleName();
    LayoutInflater mInflater;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_dialog, container, false);
        getDialog().setTitle("Ups!!!");

        Bundle mArgs = getArguments();
        String exception = mArgs.getString(TAG);
        TextView text = (TextView) rootView.findViewById(R.id.dialog_title);
        text.setText(exception);

        Button dismiss = (Button) rootView.findViewById(R.id.dialog_dimiss);
        dismiss.setText(R.string.ok);
        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().finish();
                dismiss();
            }
        });
        return rootView;
    }
}