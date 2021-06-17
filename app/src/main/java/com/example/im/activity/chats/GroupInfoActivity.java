package com.example.im.activity.chats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.im.R;
import com.example.im.activity.contacts.InfoActivity;
import com.example.im.activity.contacts.GroupCreatingActivity;
import com.example.im.adapter.chats.GroupMemberAdapter;
import com.example.im.bean.contacts.Contact;
import com.example.im.listener.OnItemClickListener;
import com.example.im.mvp.contract.chats.IGroupInfoContract;
import com.example.im.mvp.presenter.chats.GroupInfoPresenter;

import java.util.LinkedList;
import java.util.List;

public class GroupInfoActivity extends AppCompatActivity implements IGroupInfoContract.View, View.OnClickListener, OnItemClickListener {
    private Context context;
    private GroupInfoPresenter mPresenter;

    private GroupMemberAdapter groupMemberAdapter;
    private RecyclerView recyclerView;
    private LinearLayout deleteLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        Intent intent = getIntent();
        String groupId = intent.getStringExtra("Group ID");

        context = getApplicationContext();
        mPresenter = new GroupInfoPresenter(this, groupId);

        recyclerView = (RecyclerView)findViewById(R.id.recycle_view_group_members);
        deleteLayout = (LinearLayout)findViewById(R.id.layout_group_delete);
        deleteLayout.setOnClickListener(this);

        mPresenter.showMemberList();
    }

    @Override
    public void onClick(View view) {
        // 点击事件：退出群聊
        Toast.makeText(GroupInfoActivity.this, "Delete and Leave", Toast.LENGTH_SHORT).show();
        mPresenter.delete();
    }

    @Override
    public void onItemClick(View view, int position) {
        if (position == groupMemberAdapter.getItemCount() - 1) {
            // 点击事件：邀请联系人加入群聊，即跳转至群聊创建界面
            Intent intent = new Intent(context, GroupCreatingActivity.class);
            intent.putExtra("Type", GroupCreatingActivity.TYPE_INVITE);
            intent.putExtra("Group ID", mPresenter.getGroupID());
            startActivityForResult(intent, 1);
        }
        else {
            // 点击事件：查看群聊成员信息
            Intent intent2 = new Intent(context, InfoActivity.class);

            // TODO: 决定联系人类型
            intent2.putExtra("Type", Contact.CONTACT_TYPE_LIST);

            intent2.putExtra("Contact", mPresenter.getMember(position));  // 传递联系人信息
            startActivityForResult(intent2, 1);
        }
    }

    @Override
    public void setMemberList(List list) {
        groupMemberAdapter = new GroupMemberAdapter((LinkedList<Contact>) list, this);
        groupMemberAdapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setAdapter(groupMemberAdapter);
    }

    @Override
    public void updateMemberList() {
        groupMemberAdapter.notifyDataSetChanged();
    }

    @Override
    public void gotoChattingActivity() {
        finish();
    }

    @Override
    public void gotoInfoActivity(Contact contact, boolean isContact) {
        Intent intent = new Intent(context, InfoActivity.class);
        if (!isContact)  // 如果查找的用户不是当前用户的好友
            intent.putExtra("Type", Contact.CONTACT_TYPE_SEARCH);
        else  // 查找的用户是当前用户的好友
            intent.putExtra("Type", Contact.CONTACT_TYPE_LIST);
        intent.putExtra("Contact", contact);  // 传递联系人信息
        startActivityForResult(intent, 1);
    }

    @Override
    public void showText(String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
}
