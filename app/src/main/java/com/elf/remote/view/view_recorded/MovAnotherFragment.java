package com.elf.remote.view.view_recorded;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentMovAnotherBinding;
import com.elf.remote.CallActivity;
import com.elf.remote.viewmodel.view_recorded.MovAnotherViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


public class MovAnotherFragment extends DialogFragment implements CallActivity {
    private FragmentMovAnotherBinding binding;
    String path, path2, Fname;

    public MovAnotherFragment() {
    }

    public static MovAnotherFragment getInstance() {
        return new MovAnotherFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mov_another, container, false);
        binding.setViewModel(new MovAnotherViewModel(this));
        binding.executePendingBindings();

        Bundle bundle = getArguments();

        if (bundle != null) {
            path = bundle.getString("text");
            path2 = bundle.getString("path2");
            Fname = bundle.getString("Fname");
        }

        int x = path.indexOf(Fname);
        File path = new File(this.path.substring(0, x));
        File[] list = path.listFiles();
        String name = Fname;
        int i = 1;
        if (list != null) {
            for (File files : list) {
                if (files.getName().equals(name + ".mp4")) {
                    name = Fname + " - R"+i;
                    i++;
                }
            }
        }

        binding.editName.setText(name);

        setCancelable(false);

        return binding.getRoot();
    }

    @Override
    public void callActivity() {
        String asd = String.valueOf(binding.editName.getText());

        if (asd.equals("")) {
            Toast.makeText(getContext(), "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
        } else if (overlapChk(asd)){
            Toast.makeText(getContext(), "이미 같은 이름의 파일이 존재합니다.", Toast.LENGTH_SHORT).show();
        } else {
            int x = path.indexOf(Fname);
            String newName = path.substring(0, x) + binding.editName.getText() + ".mp4";

            File oldfile = new File(path2);
            File file = new File(newName);

            try {
                copy(oldfile, file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            requireActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            Toast.makeText(getContext(), "파일이 저장되었습니다..", Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }

    @Override
    public void exitActivity() {
        dismiss();
    }

    @Override
    public void callDialog() {

    }

    public boolean overlapChk(String name) {
        boolean is = false;
        File files = new File(path);
        files = files.getParentFile();
        if (files != null) {
            File[] list = files.listFiles();
            if (list != null) {
                for (File f : list) {
                    if (f.getName().equals(name+".mp4")) {
                        is = true;
                    }
                }
            }
        }
        return is;
    }

    public void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
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
                dismiss();
            }
        };
    }
}