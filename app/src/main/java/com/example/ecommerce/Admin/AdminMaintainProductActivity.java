package com.example.ecommerce.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductActivity extends AppCompatActivity {

    private Button editProductBtn, deleteBtn;
    private EditText editProductName, editProductPrice, editProductDesc;
    private ImageView editProductImage;

    private String productID = "";
    private DatabaseReference productRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_product);

        productID = getIntent().getStringExtra("pid");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);

        deleteBtn = findViewById(R.id.delete_product_btn);
        editProductBtn = findViewById(R.id.edit_product_btn);
        editProductName = findViewById(R.id.edit_product_name);
        editProductPrice = findViewById(R.id.edit_product_price);
        editProductDesc = findViewById(R.id.edit_product_description);
        editProductImage = findViewById(R.id.edit_product_image);

        displaySpecificProductsInfo();

        editProductBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                editProduct();

            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                deleteProduct();
            }
        });
    }

    private void deleteProduct()
    {
        productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {

                Intent intent = new Intent(AdminMaintainProductActivity.this, AdminCategoryActivity.class);
                startActivity(intent);
                finish();

                Toast.makeText(AdminMaintainProductActivity.this, "Produk Berhasil dihapus", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void editProduct()
    {
        String name = editProductName.getText().toString();
        String price = editProductPrice.getText().toString();
        String description = editProductDesc.getText().toString();

        if (name.equals(""))
        {
            Toast.makeText(this, "Tulis Kembali Nama Produk . .", Toast.LENGTH_SHORT).show();
        }
        else if (price.equals(""))
        {
            Toast.makeText(this, "Tulis Kembali Harga Produk . .", Toast.LENGTH_SHORT).show();
        }
        if (description.equals(""))
        {
            Toast.makeText(this, "Tulis Kembali Deskripsi Produk . .", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", productID);
            productMap.put("description", description);
            productMap.put("price", price);
            productMap.put("pname", name);

            productRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(AdminMaintainProductActivity.this, "Updae Data Produk Berhasil", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(AdminMaintainProductActivity.this, AdminCategoryActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    private void displaySpecificProductsInfo()
    {
        productRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String pName = dataSnapshot.child("pname").getValue().toString();
                    String pPrice = dataSnapshot.child("price").getValue().toString();
                    String pDescription = dataSnapshot.child("description").getValue().toString();
                    String pImage = dataSnapshot.child("image").getValue().toString();


                    editProductName.setText(pName);
                    editProductPrice.setText(pPrice);
                    editProductDesc.setText(pDescription);

                    Picasso.get().load(pImage).into(editProductImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
}
