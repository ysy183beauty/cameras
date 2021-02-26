package com.camers.adapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.camers.R;
import com.camers.entity.CameraEntity;

import java.util.List;

public class SearchAutoCompleteTextViewAdapter extends ArrayAdapter<CameraEntity> {
    private Context context;
    private int layoutResource;
    private List<CameraEntity> list;

    public SearchAutoCompleteTextViewAdapter(@NonNull Context context, int resource, @NonNull List<CameraEntity> objects) {
        super(context, resource, objects);
        this.context=context;
        this.layoutResource=resource;
        this.list=objects;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView= View.inflate(context,layoutResource,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        CameraEntity cameraEntity=getItem(position);
        viewHolder.id_TextView.setText(cameraEntity.getId());
        viewHolder.cname_TextView.setText(cameraEntity.getCameraName());
        return convertView;
    }

    private class ViewHolder{
        private TextView id_TextView;
        private TextView cname_TextView;
        public ViewHolder(View view) {
            id_TextView=(TextView)view.findViewById(R.id.id_TextView);
            cname_TextView=(TextView)view.findViewById(R.id.cname_TextView);
        }
    }
}
