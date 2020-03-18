package com.andrea.spesalo.ViewHolder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.andrea.spesalo.Model.CalendarioItem;
import com.andrea.spesalo.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CalendarioAdapter extends RecyclerView.Adapter<CalendarioAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<CalendarioItem> mList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onSpesaClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }


    public CalendarioAdapter(Context context, ArrayList<CalendarioItem> list){
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.calendario_layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view,mListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CalendarioItem calendarioitem = mList.get(position);

        TextView id = holder.id;
        TextView data = holder.data;
        TextView prezzo = holder.prezzo;

        id.setText(calendarioitem.getId());
        data.setText(calendarioitem.getData());
        prezzo.setText(calendarioitem.getPrezzo());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView id;
        TextView data;
        TextView prezzo;
        ImageButton selectedmese;

        public ViewHolder(View itemView, final OnItemClickListener listener){
            super(itemView);

            id = itemView.findViewById(R.id.id_calendario);
            data = itemView.findViewById(R.id.data_calendario);
            prezzo = itemView.findViewById(R.id.prezzo_calendario);
            selectedmese = itemView.findViewById(R.id.selected_spesa);

            selectedmese.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onSpesaClick(position);
                        }
                    }
                }
            });


        }
    }
}

