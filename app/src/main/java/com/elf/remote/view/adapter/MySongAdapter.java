package com.elf.remote.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.elf.mvvmremote.R;
import com.elf.remote.model.data.MySong;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MySongAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    List<MySong> mySonglist;
    ArrayList<MySong> mySongs;
    int kind;


    public MySongAdapter(Context context, List<MySong> data, int i) {
        this.context = context;
        this.mySonglist = data;
        this.layoutInflater = LayoutInflater.from(this.context);
        this.mySongs = new ArrayList<>();
        this.mySongs.addAll(data);
        this.kind = i;
    }

    public static class ViewHolder {
        TextView title;
        TextView singer;
        TextView Number;
        TextView Tempo;
        TextView interval;
        CheckBox chk;
    }

    @Override
    public int getCount() {
        return mySonglist.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (mySonglist.size() > position) {
            final MySong mySong = mySonglist.get(position);

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
            holder.Number.setText(String.valueOf(mySong.Number));
            holder.title.setText(mySong.Title);
            holder.singer.setText(mySong.Singer);
            holder.interval.setText(mySong.Main);
            holder.Tempo.setText(String.valueOf(mySong.Tempo));
            holder.chk.setVisibility(View.GONE);

            if (kind == 1) {
                holder.interval.setVisibility(View.INVISIBLE);
                holder.Tempo.setVisibility(View.INVISIBLE);
            }
        }
        return view;
    }


    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        if (charText.length() == 0) {
            mySonglist.clear();
        }
    }
}
