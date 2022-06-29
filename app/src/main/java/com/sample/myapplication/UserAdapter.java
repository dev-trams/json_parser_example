package com.sample.myapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.CustomViewHolder> {
    private ArrayList<PersonalData> mList = null;
    private Activity context = null;

    // ArrayList 에 있는 PersonalData 타입의 데이털르 RecyclerView 에 보여주는 작업을 하는 class
    public UserAdapter(Activity context, ArrayList<PersonalData> mList) {
        this.context = context;
        this.mList = mList;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView id;
        protected TextView num;
        protected TextView title;

        //레이아웃 파일에 있는 UI 컴포넌트를 CustomViewHolder 클래스의 멤버변수와 연결하는 구간
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.id = itemView.findViewById(R.id.textview_list_id);
            this.num = itemView.findViewById(R.id.textview_list_num);
            this.title = itemView.findViewById(R.id.textview_list_title);
        }
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        //onBindViewHolder 호출될때 CustomViewHolder 에 데이터를 추가한다.
        holder.id.setText(mList.get(position).getKaraoke_id());
        holder.num.setText(mList.get(position).getKaraoke_num());
        holder.title.setText(mList.get(position).getKaraoke_title());

    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0); // mList가 반환하는 값이 null이면 0을 반환하고 아니면 mList의 size인자를 반환한다.
    }
}
