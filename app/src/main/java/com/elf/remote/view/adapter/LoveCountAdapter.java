package com.elf.remote.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.elf.mvvmremote.R;
import com.elf.remote.model.data.LoveCount;

import java.util.ArrayList;

@SuppressLint({"ViewHolder", "InflateParams"})
public class LoveCountAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<LoveCount> myLove;
    int checked = -1;
    int Visible = View.GONE;

    public LoveCountAdapter(Context context, ArrayList<LoveCount> data) {
        this.context = context;
        this.myLove = data;
        this.layoutInflater = LayoutInflater.from(this.context);
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

    @Override
    public int getCount() {
        return myLove.size();
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
        View views = layoutInflater.inflate(R.layout.custom_mylove, null);

        TextView title = views.findViewById(R.id.lovTitle);
        TextView memo = views.findViewById(R.id.loveMemo);
        TextView count = views.findViewById(R.id.lovCount);
        views.findViewById(R.id.chk).setVisibility(Visible);

        if(checked == position) views.setBackgroundColor(Color.parseColor("#E5E5E5"));
        String total = myLove.get(position).getTotal() + "/1000";
        String id = String.valueOf(position+1);

        title.setText(myLove.get(position).getName());
        memo.setText(total);
        count.setText(id);

        return views;
    }
}
