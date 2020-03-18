package com.andrea.spesalo.ViewHolder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.andrea.spesalo.Model.CartItem;
import com.andrea.spesalo.R;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<CartItem> mList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
                void onDeleteClick(int position);
                void onPlusMinusClick(int position);
                void onChangeDescriptionClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }


    public CartAdapter(Context context, ArrayList<CartItem> list){
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.cart_layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view,mListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CartItem cartitem = mList.get(position);

        //ImageView immagine = holder.immagine;
        TextView nome = holder.nome;
        TextView prezzo = holder.prezzo;
        TextView prezzofinale = holder.prezzofinale;
        Button quantita = holder.quantita;

        nome.setText(cartitem.getNome());
        prezzo.setText(cartitem.getPrezzo());
        quantita.setText(cartitem.getQuantita());
        prezzofinale.setText(cartitem.getPrezzoFinale());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nome,prezzo,prezzofinale;
        ImageButton delete;
        Button quantita;

        public ViewHolder(View itemView, final OnItemClickListener listener){
            super(itemView);

            nome = itemView.findViewById(R.id.cart_item_name);
            prezzo = itemView.findViewById(R.id.cart_item_price);
            delete = itemView.findViewById(R.id.cart_item_delete);
            quantita = itemView.findViewById(R.id.cart_item_plusminus);
            prezzofinale = itemView.findViewById(R.id.cart_item_price_final);

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

            quantita.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onPlusMinusClick(position);
                        }
                    }
                }
            });

            nome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onChangeDescriptionClick(position);
                        }
                    }
                }
            });


        }
    }
}
