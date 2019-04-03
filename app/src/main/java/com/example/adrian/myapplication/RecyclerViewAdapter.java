package com.example.adrian.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adrian.myapplication.databinding.ProductDescriptionBinding;
//W tej klasie przypisuje do komponentu "RecyclerView" interesujace mnie dane z bazy tak aby moc je potem wyswietlic
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    Context mContext;
    Cursor mCursor;

    public RecyclerViewAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ProductDescriptionBinding productBinding;

        public ViewHolder(View itemView) {
            super(itemView);
            productBinding = DataBindingUtil.bind(itemView);
        }

        public void bindCursor(Cursor cursor) {
            productBinding.descriptionTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Product.COLUMN_PRODUCT_NAME)));
            productBinding.priceTextView.setText("Cena: " + cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Product.COLUMN_PRODUCT_PRICE)));
            productBinding.quantityTextView.setText("Pozostalo: " + cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Product.COLUMN_PRODUCT_QUANTITY)));

        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_description, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.bindCursor(mCursor);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

}
