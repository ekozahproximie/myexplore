package com.neural.setting;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.neural.demo.R;

public class SettingListAdapter extends ArrayAdapter<SettingItem>{

   private Context context; 
   private int layoutResourceId;    
   private  SettingItem data[] = null;
    
    public SettingListAdapter(Context context, int layoutResourceId, SettingItem[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SettingItemHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new SettingItemHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            
            row.setTag(holder);
        }
        else
        {
            holder = (SettingItemHolder)row.getTag();
        }        
        SettingItem settingItem = data[position];
        holder.txtTitle.setText(settingItem.title);
        holder.imgIcon.setImageResource(settingItem.icon);
        
        return row;
    }
    
    static class SettingItemHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
    }
}
