package com.example.cyclovillev4;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    public ArrayList<Product> products;
    public Context mCtx;

    private ItemClickListener mClickListener;

    private static final String TAG = "RecyclerViewAdapter";

    // data is passed into the constructor
    public MyRecyclerViewAdapter(android.content.Context mCtx, ArrayList<Product> products) {
        this.mCtx = mCtx;
        this.products =  products;
    }


    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View itemView = inflater.inflate(R.layout.data, null);
        return new ViewHolder(itemView);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Product product = products.get(position);

        //loading the image
        Glide.with(mCtx)
                .asBitmap()
                .load(product.getImage())
                .into(holder.imageView);

        holder.nameTextView.setText(product.getName());
        holder.priceTextView.setText(product.getPrice());
        holder.quantityTextView.setText(product.getQuantity());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + product.getName());

                Toast.makeText(mCtx, product.getName(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mCtx, GalleryViewActivity.class);
                intent.putExtra("image_id", product.getId());
                intent.putExtra("image_url", product.getImage());
                intent.putExtra("image_name", product.getName());
                intent.putExtra("image_price", product.getPrice());
                intent.putExtra("image_quantity", product.getQuantity());
                intent.putExtra("image_category", product.getCategory());
                intent.putExtra("image_description", product.getDescription());
                mCtx.startActivity(intent);
            }
        });

    }

    // total number of rows
    @Override
    public int getItemCount() {

        return products.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView nameTextView;
        public TextView priceTextView;
        public TextView quantityTextView;


        ImageView imageView;


        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.product_name);
            priceTextView = itemView.findViewById(R.id.product_price);
            quantityTextView = itemView.findViewById(R.id.quantity);
            imageView = itemView.findViewById(R.id.imageView3);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    //Product getItem() {
       // return (Product) products.get();
   //}

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);

        //Toast.makeText(getApplicationContext(), "You clicked " + adapter.getItem(position) + " product number " + position, Toast.LENGTH_SHORT).show();

    }

}

