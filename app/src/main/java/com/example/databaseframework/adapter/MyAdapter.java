package com.example.databaseframework.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.databaseframework.R;
import com.example.databaseframework.bean.User;

import java.util.List;

/**
 * Created by Zachary
 * on 2019/1/21.
 */
public class MyAdapter extends BaseAdapter{
    private Context mContext;
    private List<User> mList;
    private LayoutInflater mInflater;

    public MyAdapter(Context mContext, List<User> mList) {
        this.mContext = mContext;
        this.mList = mList;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            convertView = mInflater.inflate(R.layout.list_item,null);
            viewHolder = new ViewHolder();
            viewHolder.tvId = convertView.findViewById(R.id.tv_id);
            viewHolder.tvName = convertView.findViewById(R.id.tv_name);
            viewHolder.tvPwd = convertView.findViewById(R.id.tv_pwd);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        User user = mList.get(position);
        viewHolder.tvId.setText(String.valueOf(user.getId()));
        viewHolder.tvName.setText(user.getName());
        viewHolder.tvPwd.setText(user.getPassword());

        return convertView;
    }
    class ViewHolder{

        private TextView tvId;
        private TextView tvName;
        private TextView tvPwd;


    }
}
