package com.madebyaron.betterhealth.view.list;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.madebyaron.betterhealth.databinding.ItemListBinding;
import com.madebyaron.betterhealth.model.Data;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListItemHolder> {

    private final FragmentActivity activity;

    private List<Data> dataList = new ArrayList<>();

    private final ItemTouchHelper touchHelper = new ItemTouchHelper(
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START | ItemTouchHelper.END) {

                @Override
                public boolean onMove(
                        @NonNull RecyclerView recyclerView,
                        @NonNull RecyclerView.ViewHolder viewHolder,
                        @NonNull RecyclerView.ViewHolder target
                ) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    if (viewHolder instanceof ListItemHolder) {
                        ((ListItemHolder) viewHolder).remove();
                    }
                }
            }
    );

    public ListAdapter(FragmentActivity activity) {
        super();
        this.activity = activity;
    }

    public void setData(List<Data> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

	public void attachToRecyclerView(RecyclerView recyclerView) {
		recyclerView.setAdapter(this);
		touchHelper.attachToRecyclerView(recyclerView);
	}

    @NonNull
    @Override
    public ListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ListItemHolder(activity, ItemListBinding.inflate(inflater, parent, false));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemHolder holder, int position) {
        holder.bind(dataList.get(position));
    }
}