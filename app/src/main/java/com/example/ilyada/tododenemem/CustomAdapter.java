package com.example.ilyada.tododenemem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by ilyada on 13.12.2017.
 */

public class CustomAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<Notlar> notlarim;

    public CustomAdapter(Context context, int textViewResourceId, ArrayList objects) {
        super(context,textViewResourceId, objects);
        this.context= context;
        notlarim=objects;

    }

    private class ViewHolder
    {
        TextView not_baslik;
//        TextView not_icerik;
        TextView not_deadline;
        TextView not_giristarihi;
        TextView not_priority;
        TextView not_kategori;



//        TextView carPlace;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder=null;
        if (convertView == null)
        {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.not_row, null);

            holder = new ViewHolder();
            holder.not_baslik = (TextView) convertView.findViewById(R.id.not_baslik);
            holder.not_giristarihi = (TextView) convertView.findViewById(R.id.not_giristarihi);
            holder.not_deadline = (TextView) convertView.findViewById(R.id.not_deadline);
            holder.not_priority = (TextView) convertView.findViewById(R.id.not_priority);
            holder.not_kategori = (TextView) convertView.findViewById(R.id.not_kategori);

//            holder.carPlace=(TextView)convertView.findViewById(R.id.carPlace);
            convertView.setTag(holder);

        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Notlar not_gelen= notlarim.get(position);
        holder.not_baslik.setText("Başlık: " +  not_gelen.getBaslik() + "");
//        holder.not_baslik.setTextSize(15);
        holder.not_giristarihi.setText("Güncellenme Tarihi: "+ not_gelen.getYazim_tarihi()+"");
        holder.not_priority.setText("Priority: "+ not_gelen.getPriority()+"");
        holder.not_deadline.setText("Deadline Tarihi: "+ not_gelen.getDeadline_tarihi()+"");
        holder.not_kategori.setText("Kategori: "+ not_gelen.getKategori()+"");



//        holder.carColor.setText("Car Color: "+individualCar.getColor());
        return convertView;


    }

}
