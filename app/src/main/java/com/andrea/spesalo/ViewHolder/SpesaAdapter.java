package com.andrea.spesalo.ViewHolder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.andrea.spesalo.Model.CartItem;
import com.andrea.spesalo.R;

import java.util.ArrayList;

public class SpesaAdapter extends RecyclerView.Adapter<SpesaAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<CartItem> mList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onDeleteClick(int position);
        void onPlusMinusClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }


    public SpesaAdapter(Context context, ArrayList<CartItem> list){
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.spesa_layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view,mListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CartItem cartitem = mList.get(position);

        //ImageView immagine = holder.immagine;
        TextView nome = holder.nome;
        TextView prezzo = holder.prezzo;
        Button quantita = holder.quantita;

        nome.setText(cartitem.getNome());
        prezzo.setText(cartitem.getPrezzo());
        quantita.setText(cartitem.getQuantita());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nome,prezzo;
        Button quantita;

        public ViewHolder(View itemView, final OnItemClickListener listener){
            super(itemView);

            nome = itemView.findViewById(R.id.cart_item_name);
            prezzo = itemView.findViewById(R.id.cart_item_price);
            quantita = itemView.findViewById(R.id.number_button);


        }
    }
}
