package com.elf.remote.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.ActivitySearchMusicBinding;
import com.elf.remote.model.data.DataAdapter;
import com.elf.remote.model.data.LoveFile;
import com.elf.remote.model.data.MySong;

import java.util.ArrayList;
import java.util.List;

public class LoveAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    List<LoveFile> mySonglist;
    ArrayList<LoveFile> mySongs;
    List<MySong> loveList;
    public int count = 20;
    ActivitySearchMusicBinding binding;

    int checked = -1, find;
    int Visible = View.GONE;

    String[] finds;

    String[] a1 = {"Ab", "A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F", "F#", "G"};
    String[] a2 = {"Ab", "A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F", "Gb", "G"};
    String[] a3 = {"Am", "Bbm", "Bm", "Cm", "C#m", "Dm", "Ebm", "Em", "Fm", "F#m", "Gm", "G#m"};
    String[] a4 = {"Am", "Bbm", "Bm", "Cm", "C#m", "Dm", "D#m", "Em", "Fm", "F#m", "Gm", "G#m"};

    public LoveAdapter(Context context, List<com.elf.remote.model.data.LoveFile> data) {
        this.context = context;
        this.mySonglist = data;
        this.layoutInflater = LayoutInflater.from(this.context);
        this.mySongs = new ArrayList<>();
        this.mySongs.addAll(data);
    }

    public static class ViewHolder {
        TextView title;
        TextView singer;
        TextView Number;
        TextView Tempo;
        TextView interval;
        CheckBox chk;
    }

    public void setBinding(ActivitySearchMusicBinding binding) {
        this.binding = binding;
    }

    public void isChk(int visible) {
        this.Visible = visible;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

    @Override
    public int getCount() {
        if(mySonglist.size() < count) count = mySonglist.size();
        return count;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        final LoveFile mySong = mySonglist.get(position);
        getLoveSong(mySong.Number);

        if (loveList.get(0).Main.equals("C#")) a1[5] = "C#";

        findMyInterval(a1, loveList.get(0).Main);
        findMyInterval(a3, loveList.get(0).Main);

        if (finds == null) {
            findMyInterval(a2, loveList.get(0).Main);
            findMyInterval(a4, loveList.get(0).Main);
        }

        if (view == null) {
            holder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.custom_mysong, null);

            holder.Number = view.findViewById(R.id.number);
            holder.title = view.findViewById(R.id.SongTitle);
            holder.singer = view.findViewById(R.id.singer);
            holder.Tempo = view.findViewById(R.id.tempo);
            holder.interval = view.findViewById(R.id.play);
            holder.chk = view.findViewById(R.id.chk);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (checked == position) view.setBackgroundColor(Color.parseColor("#E5E5E5"));
        else view.setBackgroundColor(Color.parseColor("#FFFFFF"));

        holder.Number.setText(String.valueOf(mySong.Number));
        holder.title.setText(loveList.get(0).Title);
        holder.singer.setText(loveList.get(0).Singer);
        holder.chk.setVisibility(Visible);

        holder.chk.setOnClickListener(v -> binding.SongList.setItemChecked(position, holder.chk.isChecked()));

        if (loveList.get(0).AbsMain == mySong.PlayKey) {
            holder.interval.setText(loveList.get(0).Main);
        } else if (loveList.get(0).AbsMain < mySong.PlayKey) {
            int c = mySong.PlayKey - loveList.get(0).AbsMain;
            find = find + c;
            if (find >= 12) find = find - 12;
            holder.interval.setText(finds[find] + "(+" + (mySong.PlayKey - loveList.get(0).AbsMain) + ")");
        } else {
            int c = loveList.get(0).AbsMain - mySong.PlayKey;
            find = find - c;
            if (find < 0) find = find + 12;
            holder.interval.setText(finds[find] + "(" + (mySong.PlayKey - loveList.get(0).AbsMain) + ")");
        }

        if (loveList.get(0).Tempo == mySong.Tempo) {
            holder.Tempo.setText(String.valueOf(mySong.Tempo));
        } else if (loveList.get(0).Tempo < mySong.Tempo) {
            holder.Tempo.setText(mySong.Tempo + "(+" + (mySong.Tempo - loveList.get(0).Tempo) + ")");
        } else {
            holder.Tempo.setText(mySong.Tempo + "(" + (mySong.Tempo - loveList.get(0).Tempo) + ")");
        }

        return view;
    }

    private void getLoveSong(int text) {
        DataAdapter mDbHelper = new DataAdapter(context.getApplicationContext());
        mDbHelper.open();
        // db에 있는 값들을 model을 적용해서 넣는다.
        loveList = mDbHelper.getCustomer(text);
        // db 닫기
        mDbHelper.close();
    }

    void findMyInterval(String[] b, String a) {
        int count = 0;
        for (Object o : b) {
            if (o.equals(a)) {
                finds = b;
                find = count;
                break;
            }
            count++;
        }
    }
}
