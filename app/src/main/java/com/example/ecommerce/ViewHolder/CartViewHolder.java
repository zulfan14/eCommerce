package com.example.ecommerce.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.Interface.ItemClikListener;
import com.example.ecommerce.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtProductName, txtProductPrice, txtProductQuantity;
    private ItemClikListener itemClikListener;

    public CartViewHolder(@NonNull View itemView)
    {
        super(itemView);

        txtProductName = itemView.findViewById(R.id.cart_produck_name);
        txtProductPrice = itemView.findViewById(R.id.cart_produck_price);
        txtProductQuantity = itemView.findViewById(R.id.cart_produck_quantity);
    }

    @Override
    public void onClick(View view)
    {
        itemClikListener.onClick(view, getAdapterPosition(), false);
    }

    public void setItemClikListener(ItemClikListener itemClikListener)
    {
        this.itemClikListener = itemClikListener;
    }
}
