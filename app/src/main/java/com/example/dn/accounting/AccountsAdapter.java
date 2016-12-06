package com.example.dn.accounting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dn on 2016/8/19.
 */

public class AccountsAdapter extends ArrayAdapter<AccountsMessage> {

    private String test = null;

    private int resourceId;
    public AccountsAdapter(Context context, int resource, List<AccountsMessage> msgslist) {
        super(context, resource, msgslist);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AccountsMessage message = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView==null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.eventsText = (TextView) view.findViewById(R.id.eventsList_textview);
            viewHolder.costText = (TextView) view.findViewById(R.id.costList_textview);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.eventsText.setText(message.getEvents().toString());
        viewHolder.costText.setText(String.valueOf(message.getCost()));
        if (message.getCost()<0){
            viewHolder.costText.setTextColor(0xff99ff00);
        }else{
            viewHolder.costText.setTextColor(0xffff0000);
        }
        return view;
    }

    class ViewHolder{
        TextView eventsText;
        TextView costText;
    }
}
