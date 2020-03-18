package com.andrea.spesalo.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andrea.spesalo.Model.CatalogoItem;
import com.andrea.spesalo.R;

import java.util.ArrayList;

public class CatalogoAdapter extends RecyclerView.Adapter<CatalogoAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<CatalogoItem> mList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onAddClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }


    public CatalogoAdapter(Context context, ArrayList<CatalogoItem> list){
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.catalogo_layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view,mListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CatalogoItem catalogoitem = mList.get(position);

        TextView nome = holder.nome;
        LinearLayout addLayout = holder.addLayout;
        ImageButton add = holder.add;
        ImageView offerta = holder.offerta;

        nome.setText(catalogoitem.getNome());
        if(catalogoitem.getOfferta() == true) {
            offerta.setVisibility(View.VISIBLE);
        }else{
            offerta.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nome;
        LinearLayout addLayout;
        ImageButton add;
        ImageView offerta;

        public ViewHolder(View itemView, final OnItemClickListener listener){
            super(itemView);

            nome = itemView.findViewById(R.id.nomeCatalogo);
            addLayout = itemView.findViewById(R.id.listaadd);
            add = itemView.findViewById(R.id.lista_item_add);
            offerta = itemView.findViewById(R.id.specialOffer3);

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onAddClick(position);
                        }
                    }
                }
            });

            addLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onAddClick(position);
                        }
                    }
                }
            });



        }
    }
}

