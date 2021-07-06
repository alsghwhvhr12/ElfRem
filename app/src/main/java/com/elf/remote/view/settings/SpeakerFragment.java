package com.elf.remote.view.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentSpeakerBinding;
import com.elf.remote.CallActivity;
import com.elf.remote.viewmodel.settings.SpeakerViewModel;


public class SpeakerFragment extends DialogFragment implements CallActivity {
    FragmentSpeakerBinding binding;

    public static SpeakerFragment speakerFragment;

    public System system;
    public SpeakerFragment() {
    }

    public static SpeakerFragment getInstance() {
        return new SpeakerFragment();
    }

    Step1Fragment step1;
    Step2Fragment step2;
    Step3Fragment step3;
    Step4Fragment step4;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SpeakerViewModel model = new SpeakerViewModel(this);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_speaker, container, false);
        binding.setViewModel(model);
        binding.executePendingBindings();

        step1 = new Step1Fragment();
        step2 = new Step2Fragment();
        step3 = new Step3Fragment();
        step4 = new Step4Fragment();

        speakerFragment = this;

        getChildFragmentManager().beginTransaction().replace(R.id.container, step1).commit();
        binding.preBtn.setVisibility(View.INVISIBLE);

        binding.nextBtn.setOnClickListener(v -> {
            if(step1.isVisible()) {
                getChildFragmentManager().beginTransaction().replace(R.id.container, step2).commit();
                binding.preBtn.setVisibility(View.VISIBLE);
            } else if (step2.isVisible()) {
                getChildFragmentManager().beginTransaction().replace(R.id.container, step3).commit();
                binding.nextBtn.setVisibility(View.INVISIBLE);
            }
        });

        binding.preBtn.setOnClickListener(v -> {
            if(step2.isVisible()) {
                getChildFragmentManager().beginTransaction().replace(R.id.container, step1).commit();
                binding.preBtn.setVisibility(View.INVISIBLE);
            } else if (step3.isVisible()) {
                getChildFragmentManager().beginTransaction().replace(R.id.container, step2).commit();
                binding.nextBtn.setVisibility(View.VISIBLE);
            }
        });
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void callActivity() {
    }

    @Override
    public void exitActivity() {
        dismiss();
    }

    @Override
    public void callDialog() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                if(step1.isVisible()) {
                    dismiss();
                } else if(step2.isVisible()) {
                    getChildFragmentManager().beginTransaction().replace(R.id.container, step1).commit();
                    binding.preBtn.setVisibility(View.INVISIBLE);
                } else if (step3.isVisible()) {
                    getChildFragmentManager().beginTransaction().replace(R.id.container, step2).commit();
                    binding.nextBtn.setVisibility(View.VISIBLE);
                }
            }
        };
    }
}