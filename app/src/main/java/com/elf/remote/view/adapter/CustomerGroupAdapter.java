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
import com.elf.mvvmremote.databinding.FragmentMyloveBinding;
import com.elf.remote.model.data.CustomerGroup;

import java.util.ArrayList;

@SuppressLint({"ViewHolder", "InflateParams"})
public class CustomerGroupAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<CustomerGroup> myLove;
    FragmentMyloveBinding binding;
    int checked = -1;
    int Visible = View.GONE;

    public CustomerGroupAdapter(Context context, ArrayList<CustomerGroup> data) {
        this.context = context;
        this.myLove = data;
        this.layoutInflater = LayoutInflater.from(this.context);
    }

    public void setBinding(FragmentMyloveBinding binding) {
        this.binding = binding;
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
        CheckBox chkBox = views.findViewById(R.id.chk);

        chkBox.setVisibility(Visible);

        if(checked == position) views.setBackgroundColor(Color.parseColor("#E5E5E5"));

        String id = String.valueOf(myLove.get(position).getId());

        title.setText(myLove.get(position).getName());
        memo.setText(myLove.get(position).getMemo());
        count.setText(id);

        chkBox.setOnClickListener(v -> binding.loveList.setItemChecked(position, chkBox.isChecked()));

        return views;
    }

    public void isChk(int visible) {
        this.Visible = visible;
    }
}
