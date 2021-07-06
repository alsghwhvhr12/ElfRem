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
import com.elf.remote.model.data.MySlove;

import java.util.ArrayList;
import java.util.List;

public class MySLoveAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    List<MySlove> mySonglist;
    ArrayList<MySlove> mySongs;
    String interval;
    int checked = -1;
    public int count = 20;

    public MySLoveAdapter(Context context, List<MySlove> data) {
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
        final MySlove mySong = mySonglist.get(position);

        if (view == null) {
            holder = new ViewHolder();
            int layoutId = R.layout.custom_mysong;
            view = layoutInflater.inflate(layoutId, null);

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

        if(checked == position) view.setBackgroundColor(Color.parseColor("#E5E5E5"));
        else view.setBackgroundColor(Color.parseColor("#FFFFFF"));

        holder.Number.setText(String.valueOf(mySong.Number));
        holder.title.setText(mySong.Title);
        holder.singer.setText(mySong.Singer);
        holder.chk.setVisibility(View.GONE);
        interval = mySong.Intaval;

        if (mySong.Tmep == mySong.Tempo) holder.Tempo.setText(String.valueOf(mySong.Tmep));
        else if (mySong.Tmep < mySong.Tempo)
            holder.Tempo.setText(mySong.Tmep + "(" + (mySong.Tmep - mySong.Tempo) + ")");
        else holder.Tempo.setText(mySong.Tmep + "(+" + (mySong.Tmep - mySong.Tempo) + ")");

        if (mySong.count == 0) holder.interval.setText(interval);
        else if (mySong.count < 0) holder.interval.setText(interval + "(" + (mySong.count) + ")");
        else holder.interval.setText(interval + "(+" + (mySong.count) + ")");

        return view;
    }
}
