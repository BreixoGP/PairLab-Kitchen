package com.example.fpappfront.ui.home;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fpappfront.R;
import com.example.fpappfront.data.model.Combo;

import java.util.ArrayList;
import java.util.List;

public class ComboAdapter extends RecyclerView.Adapter<ComboAdapter.ComboViewHolder> {

    private List<Combo> combos = new ArrayList<>();

    public interface OnComboClickListener {
        void onComboClick(List<String> ingredientsInCombo);
    }

    private OnComboClickListener clickListener;

    public void setOnComboClickListener(OnComboClickListener listener) {
        this.clickListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Combo> newData) {
        combos = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ComboViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_combo, parent, false);
        return new ComboViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComboViewHolder holder, int position) {
        Combo combo = combos.get(position);

        StringBuilder comboText = new StringBuilder();
        for (int i = 0; i < combo.combo.size(); i++) {
            comboText.append(combo.combo.get(i).name);
            if (i < combo.combo.size() - 1) {
                comboText.append(" + ");
            }
        }

        holder.tvCombo.setText(comboText.toString());
        holder.tvScore.setText(String.valueOf(combo.score));

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null && combo.combo != null) {
                List<String> ingredientNames = new ArrayList<>();


                for (int i = 0; i < combo.combo.size(); i++) {
                    ingredientNames.add(combo.combo.get(i).name.toLowerCase());
                }


                clickListener.onComboClick(ingredientNames);
            }
        });
    }

    @Override
    public int getItemCount() {
        return combos.size();
    }

    public static class ComboViewHolder extends RecyclerView.ViewHolder {

        TextView tvCombo;
        TextView tvScore;
        ImageView ivComboIcon;

        public ComboViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCombo = itemView.findViewById(R.id.tvCombo);
            tvScore = itemView.findViewById(R.id.tvScore);
            ivComboIcon = itemView.findViewById(R.id.ivComboIcon);
        }
    }
}