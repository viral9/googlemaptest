package com.example.mapofflinej;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class buttom_sheet_loaction_name extends BottomSheetDialogFragment {

    TextView tv_first,tv_secound;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.buttom_sheet_loaction_name,container,false);


        tv_first = view.findViewById(R.id.tv_first);
        tv_secound = view.findViewById(R.id.tv_secound);
        tv_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetLisner.OnClickText("Testing done");

                //tv_secound.setText("advsdv");
                dismiss();
            }
        });
        tv_secound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    public interface BottomSheetLisner{
        void OnClickText(String text);
    }
    BottomSheetLisner bottomSheetLisner;

    public void setCallback(BottomSheetLisner bottomSheetLisner) {
        this.bottomSheetLisner = bottomSheetLisner;
    }


}
