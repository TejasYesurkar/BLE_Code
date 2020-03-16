package com.manager.servicesble_code.bluetoothDevice;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manager.servicesble_code.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyHolder>  {

    Context context;
    ArrayList<device> models;

    ItemClickListener itemClickListener;
    public MyAdapter(Context context, ArrayList<device> models,ItemClickListener itemClickListener) {
        this.context = context;
        this.models = models;
        this.itemClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate( R.layout.dashboardrow,null );

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int i) {

            holder.address.setText( models.get( i ).getAddress() );
            holder.name.setText( models.get( i ).getName() );
            holder.mstate.setText( models.get( i ).getState() );


final String str = models.get( i ).getAddress();
                holder.name.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onGetAddress( str );


                    }
                } );
    }


    @Override
    public int getItemCount() {
        return models.size();
    }


}
