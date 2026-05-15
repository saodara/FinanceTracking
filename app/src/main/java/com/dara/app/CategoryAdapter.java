package com.dara.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private OnCategoryListener onCategoryListener;

    public interface OnCategoryListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public CategoryAdapter(List<Category> categoryList, OnCategoryListener onCategoryListener) {
        this.categoryList = categoryList;
        this.onCategoryListener = onCategoryListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(itemView, onCategoryListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryNameText.setText(category.getName());
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView categoryNameText;
        public ImageButton editCategoryButton, deleteCategoryButton;
        private OnCategoryListener onCategoryListener;

        public CategoryViewHolder(View view, OnCategoryListener onCategoryListener) {
            super(view);
            categoryNameText = view.findViewById(R.id.category_name_text);
            editCategoryButton = view.findViewById(R.id.edit_category_button);
            deleteCategoryButton = view.findViewById(R.id.delete_category_button);
            this.onCategoryListener = onCategoryListener;

            editCategoryButton.setOnClickListener(this);
            deleteCategoryButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.edit_category_button) {
                onCategoryListener.onEditClick(getAdapterPosition());
            } else if (v.getId() == R.id.delete_category_button) {
                onCategoryListener.onDeleteClick(getAdapterPosition());
            }
        }
    }
}