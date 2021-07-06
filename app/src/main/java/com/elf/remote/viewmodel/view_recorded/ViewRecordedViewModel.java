package com.elf.remote.viewmodel.view_recorded;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.elf.remote.SubCall;

public class ViewRecordedViewModel extends BaseObservable {
    @Bindable
    private String viewRMessage = null;

    public String getViewRMessage() {
        return viewRMessage;
    }

    private void setViewRMessage() {
        this.viewRMessage = "hide";
        notifyPropertyChanged(BR.viewRMessage);
    }

    private final SubCall navi;

    public ViewRecordedViewModel(SubCall navi) {
        this.navi = navi;
    }

    public void onMovClicked() {
        navi.twoCall();
    }

    public void onStartClicked() {
        navi.closeCall();
    }

    public void onSaveFileClicked() {
        navi.oneCall();
    }

    public void hideClicked() {
        setViewRMessage();
    }

    public void plusClicked() {
        navi.fourCall();
    }

    public void minusClicked() {
        navi.threeCall();
    }

}
