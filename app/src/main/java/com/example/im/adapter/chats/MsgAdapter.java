package com.example.im.adapter.chats;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.im.R;
import com.example.im.bean.AccountInfo;
import com.example.im.bean.chats.Msg;
import com.example.im.bean.contacts.Contact;
import com.example.im.listener.OnItemClickListener;
import com.example.im.listener.OnItemLongClickListener;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import butterknife.OnLongClick;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.MessageViewHolder> {
    private Context context;
    private OnItemLongClickListener mClickListener;
    private LinkedList<Msg> msgList;
    private ArrayList<Contact> memberList;

    public static class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private MsgAdapter mAdapter;
        private OnItemLongClickListener mListener;

        private View msgItemView;
        private ConstraintLayout leftLayout;
        private ConstraintLayout rightLayout;
        private TextView leftTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        public MessageViewHolder(View itemView, MsgAdapter adapter, OnItemLongClickListener listener) {
            super(itemView);
            this.mAdapter = adapter;
            this.mListener = listener;

            this.msgItemView = itemView.findViewById(R.id.msg);
            this.leftLayout = (ConstraintLayout)itemView.findViewById(R.id.layout_left);
            this.rightLayout = (ConstraintLayout)itemView.findViewById(R.id.layout_right);
            this.leftTextView = (TextView)itemView.findViewById(R.id.text_msg_left);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            mListener.onItemLongClick(v, (int) v.getTag());
            return true;
        }
    }
    public MsgAdapter(LinkedList<Msg> msgList, Context context) {
        this.msgList = msgList;
        this.context = context;
    }

    @NonNull
    @Override
    public MsgAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView;
        mItemView = LayoutInflater.from(context).inflate(R.layout.item_recycle_msg, parent, false);
        return new MsgAdapter.MessageViewHolder(mItemView, this, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.itemView.setTag(position);

        Msg msg = msgList.get(position);
        int memberInx;
        for (int i = 0; i < memberList.size(); ++i)
            if (memberList.get(i).getId().equals(msg.getSpeaker())) { // ????????????????????????????????????????????????
                memberInx = i;

        String url;
        Uri uri;
        if (msg.getSpeaker().equals(AccountInfo.getInstance().getId())) { // ????????????
            holder.leftLayout.setVisibility(View.GONE);  // ??????????????????
            holder.rightLayout.setVisibility(View.VISIBLE);  // ??????????????????

            ImageView imageView = holder.itemView.findViewById(R.id.img_avatar_right);
            String avatarUrl = context.getString(R.string.server) + "/picture/" + AccountInfo.getInstance().getAvatar();
            Glide.with(context).load(avatarUrl).into(imageView);  // ??????????????????

            switch (msg.getType()) {
                case Msg.TYPE_MSG:
                    holder.itemView.findViewById(R.id.img_picture_right).setVisibility(View.GONE);
                    holder.itemView.findViewById(R.id.video_right).setVisibility(View.GONE);
                    TextView textView = holder.itemView.findViewById(R.id.text_msg_right);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(msg.getContent());
                    break;
                case Msg.TYPE_PICTURE:
                    holder.itemView.findViewById(R.id.text_msg_right).setVisibility(View.GONE);
                    holder.itemView.findViewById(R.id.video_right).setVisibility(View.GONE);
                    imageView = holder.itemView.findViewById(R.id.img_picture_right);
                    imageView.setVisibility(View.VISIBLE);
                    if (msg.isLocal()) {
                        uri = Uri.fromFile(new File(msg.getContent()));
                        imageView.setImageURI(uri);
                    }
                    else {
                        url = context.getString(R.string.server) + "/picture/" + msg.getContent();
                        Glide.with(context).load(url).into(imageView);  // ?????????????????????
                    }
                    break;
                case Msg.TYPE_VIDEO:
                    holder.itemView.findViewById(R.id.text_msg_right).setVisibility(View.GONE);
                    holder.itemView.findViewById(R.id.img_picture_right).setVisibility(View.GONE);
                    VideoView videoView = holder.itemView.findViewById(R.id.video_right);
                    videoView.setVisibility(View.VISIBLE);
                    if (msg.isLocal()) {
                        uri = Uri.fromFile(new File(msg.getContent()));
                        videoView.setVideoURI(uri);
                    }
                    else {

                    }
                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mPlayer) {
                            mPlayer.start();
                            mPlayer.setLooping(true);
                        }
                    });
                    videoView.start();
                    break;
            }
        }
        else {  // ????????????
            holder.leftLayout.setVisibility(View.VISIBLE);  // ??????????????????
            holder.rightLayout.setVisibility(View.GONE);  // ??????????????????

            ImageView imageView = holder.itemView.findViewById(R.id.img_avatar_left);
            String avatarUrl = context.getString(R.string.server) + "/picture/"
                    + memberList.get(memberInx).getAvatar();
            Glide.with(context).load(avatarUrl).into(imageView);  // ??????????????????

            switch (msg.getType()) {
                case Msg.TYPE_MSG:
                    holder.itemView.findViewById(R.id.img_picture_left).setVisibility(View.GONE);
                    holder.itemView.findViewById(R.id.video_left).setVisibility(View.GONE);
                    TextView textView = holder.itemView.findViewById(R.id.text_msg_left);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(msg.getContent());
                    break;
                case Msg.TYPE_PICTURE:
                    holder.itemView.findViewById(R.id.text_msg_left).setVisibility(View.GONE);
                    holder.itemView.findViewById(R.id.video_left).setVisibility(View.GONE);
                    imageView = holder.itemView.findViewById(R.id.img_picture_left);
                    imageView.setVisibility(View.VISIBLE);
                    if (msg.isLocal()) {
                        uri = Uri.fromFile(new File(msg.getContent()));
                        imageView.setImageURI(uri);
                    } else {
                        url = context.getString(R.string.server) + "/picture/" + msg.getContent();
                        Glide.with(context).load(url).into(imageView);  // ?????????????????????
                    }
                    break;
                case Msg.TYPE_VIDEO:
                    holder.itemView.findViewById(R.id.text_msg_left).setVisibility(View.GONE);
                    holder.itemView.findViewById(R.id.img_picture_left).setVisibility(View.GONE);
                    VideoView videoView = holder.itemView.findViewById(R.id.video_left);
                    videoView.setVisibility(View.VISIBLE);
                    videoView.setVideoURI(msg.getVideo());
                    //videoView.setMediaController(new MediaController(context));
                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mPlayer) {
                            mPlayer.start();
                            mPlayer.setLooping(true);
                        }
                    });
                    videoView.start();
                    break;
                }
            }
        }
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    public void setMemberList(ArrayList<Contact> memberList) {
        this.memberList = memberList;
    }
}
