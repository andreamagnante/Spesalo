package com.andrea.spesalo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrea.spesalo.Model.OffertaItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SlidingOfferte_Adapter extends PagerAdapter {


    private ArrayList<OffertaItem> offertaList = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;
    Integer iterator=0;


    public SlidingOfferte_Adapter(Context context,ArrayList<OffertaItem> offertaList,Integer iterator) {
        this.context = context;
        this.offertaList = offertaList;
        this.iterator = iterator;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return offertaList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View imageLayout = inflater.inflate(R.layout.slidingofferte_layout, view, false);

        assert imageLayout != null;
        final RelativeLayout Rlayout = (RelativeLayout) imageLayout
                .findViewById(R.id.relav1);
        final TextView nome = (TextView) imageLayout
                .findViewById(R.id.nomeoffV);
        final TextView originale = (TextView) imageLayout
                .findViewById(R.id.originaleV);
        final TextView offerta = (TextView) imageLayout
                .findViewById(R.id.offV);


        nome.setText(offertaList.get(position).getNome());
        originale.setText(offertaList.get(position).getOriginale());
        offerta.setText(offertaList.get(position).getOfferta());

        view.addView(imageLayout, 0);

        Rlayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if(iterator == 0){
                    if (context instanceof ListavuotaActivity) {

                        ((ListavuotaActivity) context).inserisciInLista(position);
                    }
                }else{
                    if (context instanceof ListaspesaActivity) {

                        ((ListaspesaActivity) context).inserisciInLista2(position);
                    }
                }


            }
        });

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}