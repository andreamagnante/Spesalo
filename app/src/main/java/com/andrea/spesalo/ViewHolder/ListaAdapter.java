package com.andrea.spesalo.ViewHolder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andrea.spesalo.ListaspesaActivity;
import com.andrea.spesalo.ListavuotaActivity;
import com.andrea.spesalo.Model.ListaItem;
import com.andrea.spesalo.R;

import java.util.ArrayList;

public class ListaAdapter extends RecyclerView.Adapter<ListaAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ListaItem> mList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onDeleteClick(int position);
        void onCheckClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }


    public ListaAdapter(Context context, ArrayList<ListaItem> list){
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.lista_layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view,mListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ListaItem listaitem = mList.get(position);

        TextView nome = holder.nome;
        CheckBox checkboxlista = holder.checkboxlista;
        ImageView offerta = holder.offerta;

        nome.setText(listaitem.getNome());
        checkboxlista.setChecked(listaitem.getCheckboxLista());
        if(listaitem.getCheckboxLista() == true){
             nome.setTextColor(Color.parseColor("#51ff0d"));
             nome.setPaintFlags(nome.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        if(listaitem.getOfferta()) {
            offerta.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CheckBox nome,checkboxlista;
        ImageButton delete;
        ImageView offerta;

        public ViewHolder(View itemView, final OnItemClickListener listener){
            super(itemView);

            nome = itemView.findViewById(R.id.checkboxLista);
            checkboxlista = itemView.findViewById(R.id.checkboxLista);
            offerta = itemView.findViewById(R.id.specialOffer);
            delete = itemView.findViewById(R.id.lista_item_delete);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });

            checkboxlista.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onCheckClick(position);
                            if(!checkboxlista.isChecked()){
                                nome.setTextColor(Color.parseColor("#ffffff"));
                                nome.setPaintFlags(nome.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                            }
                            if(checkboxlista.isChecked()) {
                                nome.setTextColor(Color.parseColor("#51ff0d"));
                                nome.setPaintFlags(nome.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            }

                        }
                    }
                }
            });


        }
    }
}

