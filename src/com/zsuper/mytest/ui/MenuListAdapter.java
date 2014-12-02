package com.zsuper.mytest.ui;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zsuper.mytest.R;
import com.zsuper.mytest.device.DeviceManager;
import com.zsuper.mytest.device.MdnsDevice;

public class MenuListAdapter extends BaseAdapter
{
    private static final String TAG = MenuListAdapter.class.getSimpleName();
    
    private Context mContext;
    private ArrayList<String> mData;
    
    private class ViewHolder
    {
        TextView mTitle;
        TextView mMsgText;
        LinearLayout mDevLayout;
    };
    
    public MenuListAdapter(Context context, ArrayList<String> data)
    {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount()
    {
        return mData.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
//        ILog.i(TAG, "getView pos : " + position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.menu_item, null);
            holder = new ViewHolder();
            holder.mTitle = (TextView) convertView.findViewById(R.id.item_title);
            holder.mMsgText = (TextView) convertView.findViewById(R.id.item_msg);
            holder.mDevLayout = (LinearLayout) convertView.findViewById(R.id.title_layout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        String title = mData.get(position);
        if (null == title || TextUtils.isEmpty(title)) {
            return null;
        }
        
        holder.mTitle.setText(title);
        
        if (title.equals(mContext.getString(R.string.menu_device_title))) {
            // 设备连接布局重新调整
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.mDevLayout.getLayoutParams();
            params.setMargins(44, 56, 0, 56);
            holder.mDevLayout.setLayoutParams(params);
            
            // 显示设备连接信息
            MdnsDevice device = DeviceManager.newInstance().getChoiceDevice();
            if (null != device) {
                holder.mMsgText.setText(device.getName());
            } else {
                holder.mMsgText.setText(R.string.menu_device_no_connect);
            }
            holder.mMsgText.setVisibility(View.VISIBLE);
        } else {
            holder.mMsgText.setVisibility(View.GONE);
        }
        
        return convertView;
    }
}
