package com.elf.remote.view.my_recorded;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.Button;

import com.elf.mvvmremote.R;

public class AutoPlayFragment {
    OnMyDialogResult mDr;
    SharedPreferences preferences;

    private final Context context;

    public AutoPlayFragment(Context context) {
        this.context = context;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction() {
        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.fragment_auto_play);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        final Button xBtn = dlg.findViewById(R.id.xBtn);
        final Button autoBtn = dlg.findViewById(R.id.autoBtn);
        final Button manualBtn = dlg.findViewById(R.id.manualBtn);

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        String autoSet = "autoSet";
        xBtn.setOnClickListener(view -> dlg.dismiss());
        autoBtn.setOnClickListener(view -> {
            mDr.finish(0);
            dlg.dismiss();
            editor.putInt(autoSet, 0);
            editor.apply();
        });
        manualBtn.setOnClickListener(v -> {
            mDr.finish(1);
            dlg.dismiss();
            editor.putInt(autoSet, 1);
            editor.apply();
        });
    }

    public void setDialogR(OnMyDialogResult dialogR) {
        mDr = dialogR;
    }

    public interface OnMyDialogResult {
        void finish(int result);
    }
}