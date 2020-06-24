package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Model.Cart;
import com.example.ecommerce.Privalent.Prevalent;
import com.example.ecommerce.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextProccesBtn;
    private TextView txtTotalAmount, txtMsg1;

    private int overTotalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextProccesBtn = (Button) findViewById(R.id.next_btn);
        txtTotalAmount = (TextView) findViewById(R.id.total_price);
        txtMsg1 = (TextView) findViewById(R.id.msg1);


        NextProccesBtn.setOnClickListener (new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                txtTotalAmount.setText("Total Price Rp. " + String.valueOf(overTotalPrice));

                Intent intent = new Intent(CartActivity.this, ComfirmFinalOrderActivity.class);
                intent.putExtra("Total Price", String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderSate();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View")
                .child(Prevalent.currentOnlineUser.getPhone())
                .child("Products"), Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model)
            {
//                Menampilkan Product
                holder.txtProductName.setText("Nama Produk "+model.getPname());
                holder.txtProductPrice.setText("Harga Produk "+model.getPrice());
                holder.txtProductQuantity.setText("Jumlah Produk "+model.getQuantity());


                int oneTyprProductTPrice=(Integer.parseInt(model.getPrice().replaceAll("\\D+",""))) * Integer.parseInt(model.getQuantity());
                overTotalPrice = overTotalPrice + oneTyprProductTPrice;

//                double oneTypeProductPrice = ((Double.valueOf(model.getPrice()))) * Double.valueOf(model.getQuantity());
//                overTotalPrice = overTotalPrice + oneTypeProductPrice;



                txtTotalAmount.setText("Total Price =  " + String.valueOf(overTotalPrice));

                holder.itemView.setOnClickListener(new View.OnClickListener()
                {

                    @Override
                    public void onClick(View view)
                    {
                        CharSequence options [] = new CharSequence[]
                                {
                                        "Edit",
                                        "Remove"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options");

                        builder.setItems(options, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if (i == 0)
                                {
                                    Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }
                                if (i == 1)
                                {
                                    cartListRef.child("User View")
                                            .child(Prevalent.currentOnlineUser.getPhone())
                                            .child("Products")
                                            .child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>()
                                            {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if (task.isSuccessful())
                                                    {
                                                        Toast.makeText(CartActivity.this, "Item Berhasil DIhapus . . .", Toast.LENGTH_SHORT).show();

                                                        Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                                                        startActivity(intent);
                                                    }

                                                }
                                            });

                                }

                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
/*
                LayoutInflater adalah salah satu class atau library yang digunakan ,
                untuk menjadikan atau mengconvert file layout xml,sebagai View object baru ,
                di dalam layout utama. Kalian juga bisa menyebutnya dengan istilah tumpukan layout.
                Ini dibutuhkan jika kita ingin membangun user interface secara dinamis (programatically).*/


            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

//    Method Untuk Mengecek Tabel Orders

    private void CheckOrderSate()
    {

        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());

        ordersRef.addValueEventListener(new ValueEventListener()
        {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String status = dataSnapshot.child("state").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();

                    if (status.equals("shipped"))
                    {
                        txtTotalAmount.setText("Dear " +userName+"\n Pesanan Anda Berhasil ");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        txtMsg1.setText("Selamat Pesanan Anda Berhasil Ditambahkan, Mohon Bersabar Kami akan Mengkonfimasinya . . .");
                        NextProccesBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "Kamu Dapat Membeli Berberapa Produk Hanya Dengan Sekali mengkonfirmasi", Toast.LENGTH_SHORT).show();


                    }
                    else if (status.equals("not shipped"))
                    {
                        txtTotalAmount.setText("Status Pembelian = Belum Di Kirim ");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        NextProccesBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "Kamu Dapat Membeli Berberapa Produk Hanya Dengan Sekali mengkonfirmasi", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
}
