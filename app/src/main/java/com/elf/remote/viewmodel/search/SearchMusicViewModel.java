package com.elf.remote.viewmodel.search;

import androidx.databinding.BaseObservable;

import com.elf.remote.SearchSong;

public class SearchMusicViewModel extends BaseObservable {
    private final SearchSong navi;

    public SearchMusicViewModel(SearchSong navi) {
        this.navi = navi;
    }

    public void onSloClick() {
        navi.sloCall();
    }

    public void onScuClick() {
        navi.scuCall();
    }

    public void onSrlClick() {
        navi.srlCall();
    }

    public void onJKindClick() {
        navi.jKindCall();
    }

    public void onCKindClick() {
        navi.cKindCall();
    }

    public void onAddSetClick() {
        navi.addSetCall();
    }

    public void onSonSortClick() {
        navi.sortCall();
    }

    public void onRmtClick() {
        navi.remotCall();
    }

    public void onEditClick() {
        navi.EditSaveCAll();
    }

    public void onFindClick() {
        navi.searchCall();
    }

    public void onUpClick() {
        navi.upCall();
    }

    public void onDnClick() {
        navi.downCall();
    }

    public void onTopClick() {
        navi.topCall();
    }

    public void onBotClick() {
        navi.bottomCall();
    }
}
