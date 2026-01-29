package io.github.iso53.nothingcompass.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.iso53.nothingcompass.R;
import io.github.iso53.nothingcompass.model.OptionItem;

public class OptionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<OptionItem> items;

    public OptionsAdapter(List<OptionItem> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == OptionItem.TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header,
                    parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_option,
                    parent, false);
            return new OptionViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        OptionItem item = items.get(position);

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(item);
        } else if (holder instanceof OptionViewHolder) {
            ((OptionViewHolder) holder).bind(item, position);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        HeaderViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView;
        }

        void bind(OptionItem item) {
            title.setText(item.title);
        }
    }

    class OptionViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        ImageView chevron;

        OptionViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.itemIcon);
            title = itemView.findViewById(R.id.itemTitle);
            chevron = itemView.findViewById(R.id.itemChevron);
        }

        void bind(OptionItem item, int position) {
            icon.setImageResource(item.iconRes);
            title.setText(item.title);
            itemView.setOnClickListener(item.action);

            // Determine background based on position
            // Check previous and next items to determine if we are Top, Middle, Bottom, or
            // Single
            boolean isPrevHeaderOrNull;
            if (position == 0) {
                isPrevHeaderOrNull = true;
            } else {
                isPrevHeaderOrNull = items.get(position - 1).type == OptionItem.TYPE_HEADER;
            }

            boolean isNextHeaderOrNull = false;
            if (position == items.size() - 1) {
                isNextHeaderOrNull = true;
            } else {
                isNextHeaderOrNull = items.get(position + 1).type == OptionItem.TYPE_HEADER;
            }

            if (isPrevHeaderOrNull && isNextHeaderOrNull) {
                itemView.setBackgroundResource(R.drawable.bg_option_single);
            } else if (isPrevHeaderOrNull) {
                itemView.setBackgroundResource(R.drawable.bg_option_top);
            } else if (isNextHeaderOrNull) {
                itemView.setBackgroundResource(R.drawable.bg_option_bottom);
            } else {
                itemView.setBackgroundResource(R.drawable.bg_option_middle);
            }
        }
    }
}
