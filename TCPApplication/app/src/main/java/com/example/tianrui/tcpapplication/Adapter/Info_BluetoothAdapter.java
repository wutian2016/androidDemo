package com.example.tianrui.tcpapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tianrui.tcpapplication.Bean.Info_Bluetooth;
import com.example.tianrui.tcpapplication.Bean.Msg;
import com.example.tianrui.tcpapplication.R;

import java.util.List;


public class Info_BluetoothAdapter extends ArrayAdapter<Info_Bluetooth> {
    private int resourceId;
    public Info_BluetoothAdapter(Context context, int textViewResourceId, List<Info_Bluetooth>
            objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Info_Bluetooth msg = getItem(position);
        View view;
        Info_BluetoothAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new Info_BluetoothAdapter.ViewHolder();
            viewHolder.leftLayout = (LinearLayout) view.findViewById
                    (R.id.left_layout);
            viewHolder.rightLayout = (LinearLayout) view.findViewById
                    (R.id.right_layout);
            viewHolder.leftMsg = (TextView) view.findViewById(R.id.left_msg);
            viewHolder.rightMsg = (TextView) view.findViewById(R.id.right_msg);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (Info_BluetoothAdapter.ViewHolder) view.getTag();
        }
        if (msg.getType() == Msg.TYPE_RECEIVED) {
// 如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.leftMsg.setText(msg.getContent());
        } else if(msg.getType() == Msg.TYPE_SENT) {
// 如果是发出的消息，则显示右边的消息布局，将左边的消息布局隐藏
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightMsg.setText(msg.getContent());
        }
        return view;
    }
    class ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
    }
}
