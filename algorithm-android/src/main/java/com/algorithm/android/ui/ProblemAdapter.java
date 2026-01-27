package com.algorithm.android.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.algorithm.android.R;
import com.algorithm.android.data.ProblemInfo;

import java.util.List;

/**
 * RecyclerView Adapter for Problem List
 */
public class ProblemAdapter extends RecyclerView.Adapter<ProblemAdapter.ProblemViewHolder> {

    private final List<ProblemInfo> problems;
    private final OnProblemClickListener listener;

    public interface OnProblemClickListener {
        void onProblemClick(ProblemInfo problem);
    }

    public ProblemAdapter(List<ProblemInfo> problems, OnProblemClickListener listener) {
        this.problems = problems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProblemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_problem, parent, false);
        return new ProblemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProblemViewHolder holder, int position) {
        try {
            if (position < 0 || position >= problems.size()) {
                android.util.Log.e("ProblemAdapter", "Invalid position: " + position);
                return;
            }
            
            ProblemInfo problem = problems.get(position);
            if (problem == null) {
                android.util.Log.e("ProblemAdapter", "Problem is null at position: " + position);
                holder.problemName.setText("Unknown Problem");
                holder.problemDescription.setVisibility(View.GONE);
                return;
            }
            
            holder.problemName.setText(problem.name != null ? problem.name : "Unknown");
            
            // Set short description if available
            if (problem.shortDescription != null && !problem.shortDescription.isEmpty()) {
                holder.problemDescription.setText(problem.shortDescription);
                holder.problemDescription.setVisibility(View.VISIBLE);
            } else {
                holder.problemDescription.setVisibility(View.GONE);
            }
            
            holder.itemView.setOnClickListener(v -> {
                try {
                    if (listener != null && problem != null) {
                        listener.onProblemClick(problem);
                    }
                } catch (Exception e) {
                    android.util.Log.e("ProblemAdapter", "Error on problem click", e);
                }
            });
        } catch (Exception e) {
            android.util.Log.e("ProblemAdapter", "Error binding view holder", e);
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return problems.size();
    }

    static class ProblemViewHolder extends RecyclerView.ViewHolder {
        TextView problemName;
        TextView problemDescription;

        ProblemViewHolder(@NonNull View itemView) {
            super(itemView);
            problemName = itemView.findViewById(R.id.problemNameTextView);
            problemDescription = itemView.findViewById(R.id.problemDescriptionTextView);
        }
    }
}

