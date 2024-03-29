package com.pcare.api.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pcare.api.R;
import com.pcare.api.entity.MsgEntity;

import java.util.List;

public class QuestionSpeakAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<MsgEntity> msgList;


    public QuestionSpeakAdapter(Context context, List<MsgEntity> msgList) {
        this.context = context;
        this.msgList = msgList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if(viewType == MsgEntity.RECV_MSG){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_chat_left,parent,false);
            return new LeftViewHolder(view);
        }else if(viewType == MsgEntity.SEND_MSG){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_chat_right,parent,false);
            return new RightViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof LeftViewHolder){
            ((LeftViewHolder) holder).msg.setText(msgList.get(position).getContent());
        }else{
            ((RightViewHolder) holder).msg.setText(msgList.get(position).getContent());
        }


    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return msgList.get(position).getType();
    }

    static class LeftViewHolder extends RecyclerView.ViewHolder{
        public TextView msg;

        public LeftViewHolder(View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.chat_left_msg);
        }
    }
    static class RightViewHolder extends RecyclerView.ViewHolder{
        public TextView msg;

        public RightViewHolder(View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.chat_right_msg);
        }
    }
}
