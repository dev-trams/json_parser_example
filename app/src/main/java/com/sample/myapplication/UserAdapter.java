package com.sample.myapplication;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.Adapter.StateRestorationPolicy>{

    @NonNull
    @Override
    public StateRestorationPolicy onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull StateRestorationPolicy holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
