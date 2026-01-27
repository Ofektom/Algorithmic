package com.algorithm.android.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.algorithm.android.R;

import java.util.List;

/**
 * RecyclerView Adapter for Category List
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final String[] categories;
    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(int position);
    }

    public CategoryAdapter(String[] categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.categoryText.setText(categories[position]);
        holder.itemView.setOnClickListener(v -> listener.onCategoryClick(position));
    }

    @Override
    public int getItemCount() {
        return categories.length;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryText;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryText = itemView.findViewById(android.R.id.text1);
        }
    }
}

