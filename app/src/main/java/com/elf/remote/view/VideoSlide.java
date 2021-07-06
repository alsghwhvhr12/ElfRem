package com.elf.remote.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.elf.mvvmremote.R;
import com.elf.remote.model.data.VerSionMachin;

public class VideoSlide extends Fragment {
    private int pos, kind;
    private final int[] Vimages = {R.drawable.recimg1g, R.drawable.recimg2g, R.drawable.recimg3g};
    private final int[] Vimage = {R.drawable.recimg1, R.drawable.recimg2, R.drawable.recimg3};
    private final int[] Rimages = {R.drawable.myvidimg1, R.drawable.myvidimg2};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            pos = bundle.getInt("pos");
            kind = bundle.getInt("kind");
        }

        return inflater.inflate(R.layout.fragment_silde, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imageView = view.findViewById(R.id.img);

        if(kind == 2) imageView.setBackgroundResource(Rimages[pos]);
        else {
            if (VerSionMachin.getName().equals("G10")) imageView.setBackgroundResource(Vimages[pos]);
            else imageView.setBackgroundResource(Vimage[pos]);
        }
    }
}