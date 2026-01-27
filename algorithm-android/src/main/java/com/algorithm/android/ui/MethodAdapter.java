package com.algorithm.android.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.algorithm.android.R;

/**
 * RecyclerView Adapter for Method List
 */
public class MethodAdapter extends RecyclerView.Adapter<MethodAdapter.MethodViewHolder> {

    private final String[] methods;
    private final OnMethodClickListener listener;

    public interface OnMethodClickListener {
        void onMethodClick(String methodName);
    }

    public MethodAdapter(String[] methods, OnMethodClickListener listener) {
        this.methods = methods;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MethodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_method, parent, false);
        return new MethodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MethodViewHolder holder, int position) {
        try {
            if (methods == null || position < 0 || position >= methods.length) {
                android.util.Log.e("MethodAdapter", "Invalid position or methods array: " + position);
                return;
            }
            
            String methodName = methods[position];
            if (methodName == null) {
                android.util.Log.e("MethodAdapter", "Method name is null at position: " + position);
                holder.methodName.setText("Unknown Method");
                holder.methodNameFull.setText("");
                return;
            }
            
            // Format method name for display (remove "compute" prefix, add spaces)
            String displayName = formatMethodName(methodName);
            holder.methodName.setText(displayName);
            holder.methodNameFull.setText(methodName);
            
            holder.itemView.setOnClickListener(v -> {
                try {
                    if (listener != null && methodName != null) {
                        listener.onMethodClick(methodName);
                    }
                } catch (Exception e) {
                    android.util.Log.e("MethodAdapter", "Error on method click", e);
                }
            });
        } catch (Exception e) {
            android.util.Log.e("MethodAdapter", "Error binding view holder", e);
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return methods.length;
    }

    private String formatMethodName(String methodName) {
        // Convert "computeWithHashMap" to "HashMap"
        // Convert "computeRecursive" to "Recursive"
        if (methodName.startsWith("compute")) {
            String rest = methodName.substring(7); // Remove "compute"
            if (rest.startsWith("With")) {
                rest = rest.substring(4); // Remove "With"
            }
            // Add spaces before capital letters
            return rest.replaceAll("([A-Z])", " $1").trim();
        }
        return methodName;
    }

    static class MethodViewHolder extends RecyclerView.ViewHolder {
        TextView methodName;
        TextView methodNameFull;

        MethodViewHolder(@NonNull View itemView) {
            super(itemView);
            methodName = itemView.findViewById(R.id.methodNameTextView);
            methodNameFull = itemView.findViewById(R.id.methodNameFullTextView);
        }
    }
}

