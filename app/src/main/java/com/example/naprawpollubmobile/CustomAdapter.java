package com.example.naprawpollubmobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    ArrayList<DefectForList> defects = new ArrayList<>();
    Context mContext;

    public CustomAdapter(Context context, ArrayList<DefectForList> defects){
        this.defects = defects;
        mContext = context;
    }

    @Override
    public int getCount() {
        return defects.size();
    }

    @Override
    public Object getItem(int position) {
        return defects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row, parent, false);
        }

        DefectForList tempDefectForList = (DefectForList)getItem(position);

        TextView miejsce = (TextView)convertView.findViewById(R.id.miejsce);
        TextView sala = (TextView)convertView.findViewById(R.id.sala);
        TextView typ = (TextView)convertView.findViewById(R.id.typ);
        TextView opis = (TextView)convertView.findViewById(R.id.opis);

        miejsce.setText(tempDefectForList.place);
        sala.setText(tempDefectForList.room);
        typ.setText(tempDefectForList.defectType);
        opis.setText(tempDefectForList.description);



        return convertView;
    }
}
