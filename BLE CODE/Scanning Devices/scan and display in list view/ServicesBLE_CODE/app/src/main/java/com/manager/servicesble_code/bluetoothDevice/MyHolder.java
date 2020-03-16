package com.manager.servicesble_code.bluetoothDevice;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manager.servicesble_code.R;

public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView name,address,mstate;

    ItemClickListener itemClickListener;
    Context context;

    public MyHolder(@NonNull View itemView) {
        super( itemView );

        this.mstate = itemView.findViewById( R.id.state );
        this.address = itemView.findViewById( R.id.Address);
        this.name = itemView.findViewById( R.id.name);


        itemView.setOnClickListener( this );

    }

    @Override
    public void onClick(View v) {
           this.itemClickListener.onItemClickListener(v,getLayoutPosition());
           device models = null;


    }

    public void setItemClickListener(ItemClickListener itemClickListener) {

        this.itemClickListener = itemClickListener;
    }
}
