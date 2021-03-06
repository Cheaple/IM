package com.example.im.activity.chats;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.TakePhotoActivity;
import com.dmcbig.mediapicker.entity.Media;
import com.example.im.R;
import com.example.im.activity.contacts.InfoActivity;
import com.example.im.adapter.chats.MsgAdapter;
import com.example.im.bean.chats.Chat;
import com.example.im.bean.chats.Msg;
import com.example.im.bean.contacts.Contact;
import com.example.im.listener.OnItemLongClickListener;
import com.example.im.listener.OnLoginListener;
import com.example.im.mvp.contract.chats.IChattingContract;
import com.example.im.mvp.presenter.chats.ChattingPresenter;
import com.example.im.util.UriUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ChattingActivity extends AppCompatActivity implements IChattingContract.View, View.OnClickListener, OnItemLongClickListener {
    private static final int REQUEST_PICTURE = 100;
    private static final int REQUEST_CAMERA = 101;
    private static final int REQUEST_VIDEO = 102;
    private static final int REQUEST_RECORD = 103;

    private static final int TYPE_PICTURE = 1;
    private static final int TYPE_VIDEO = 3;

    private Context context;
    private ChattingPresenter mPresenter;

    private MsgAdapter messageAdapter;
    private RecyclerView recyclerView;
    private EditText inputText;
    private ImageView sendImageView;
    private ImageView moreImageView;

    Uri pictureUri;  // ???????????????????????????????????????

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        Intent intent = getIntent();
        String type = intent.getStringExtra("Chat Type");
        String id = intent.getStringExtra("Chat ID");
        ArrayList<String> members = intent.getStringArrayListExtra("Group Members");

        context = getApplicationContext();

        if (type == Chat.CHAT_TYPE_SINGLE)
            mPresenter = new ChattingPresenter(this, context, type, id, intent.getStringExtra("Chat Contact"));
        else
            mPresenter = new ChattingPresenter(this, context, type, id);

        recyclerView = (RecyclerView) findViewById(R.id.msg_recycle_view);
        inputText = (EditText) findViewById(R.id.input_text);
        sendImageView = (ImageView) findViewById(R.id.img_send);
        moreImageView = (ImageView) findViewById(R.id.img_more);

        sendImageView.setOnClickListener(this);
        moreImageView.setOnClickListener(this);

        mPresenter.loadMemberInfo(members);  // ??????????????????????????????????????????
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PICTURE:
                if (resultCode != PickerConfig.RESULT_CODE) return;
                ArrayList<Media> selected = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
                if (!selected.isEmpty()) {
                    Media media = selected.get(0);
                    if (media.mediaType == TYPE_PICTURE)  // ?????????????????????
                        mPresenter.sendPicture(media.path);
                    else if (media.mediaType == TYPE_VIDEO)  // ???????????????
                        mPresenter.sendVideo(media.path);
                }
                break;
            case REQUEST_CAMERA:
                if (resultCode != Activity.RESULT_OK) return;
                //mPresenter.sendPicture(UriUtil.getFileAbsolutePath(context, pictureUri));
                break;
            case REQUEST_VIDEO:
                if (resultCode != PickerConfig.RESULT_CODE) return;
                Uri uri = data.getData();
                //mPresenter.sendVideo(UriUtil.getFileAbsolutePath(context, uri));
                break;
            case REQUEST_RECORD:
                // TODO: ????????????
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chatting_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // ???????????????????????????????????????????????????
        if (item.getItemId() == R.id.menu_info) {
            if (mPresenter.getType().equals(Chat.CHAT_TYPE_SINGLE)) {
                mPresenter.checkInfo();
            }
            else {
                gotoGroupInfoActivity(mPresenter.getId());
            }
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_send:
                mPresenter.sendMsg();  // ???????????????????????????
                break;
            case R.id.img_more:
                showPopupMenu(view);
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.message_delete_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            // ?????????????????????????????????
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mPresenter.deleteMsg(position);
                return true;
            }
        });
        popupMenu.show();
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.message_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_album:
                        selectPicture();
                        break;
                    case R.id.menu_camera:
                        takePhoto();
                        break;
                    case R.id.menu_video:
                        takeVideo();
                        break;
                    case R.id.menu_record:
                        takeRecord();
                        break;
                    case R.id.menu_location:
                        getLocation();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void selectPicture() {
        Intent intent = new Intent(context, PickerActivity.class);
        intent.putExtra(PickerConfig.MAX_SELECT_COUNT, 1);  // ?????????????????????1
        startActivityForResult(intent, REQUEST_PICTURE);
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "My Photo");  //?????????????????????????????????????????????Uri
        pictureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        System.out.println(pictureUri.getPath());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void takeVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);  //????????????s
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 30 * 1024 * 1024);  // ????????????
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);  //????????????
        startActivityForResult(intent, REQUEST_VIDEO);
    }

    private void takeRecord() {
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        startActivityForResult(intent, REQUEST_RECORD);
    }

    private void getLocation() {
    }


    @Override
    public void setMsgList(LinkedList<Msg> msgList, ArrayList<Contact> memberList) {
        messageAdapter = new MsgAdapter(msgList, context);
        messageAdapter.setMemberList(memberList);
        messageAdapter.setOnItemLongClickListener(this);  // ???????????????????????????
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.scrollToPosition(messageAdapter.getItemCount()-1);  // ??????????????????????????????????????????
    }

    @Override
    public void updateMsgList() {
        messageAdapter.notifyDataSetChanged();
        messageAdapter.setOnItemLongClickListener(this);
        recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);  // ??????????????????????????????????????????
    }

    @Override
    public String getMsg() {
        return inputText.getText().toString();
    }

    @Override
    public void clearMsg() { inputText.setText(""); }

    @Override
    public void gotoInfoActivity(Contact contact, boolean isContact) {
        Intent intent = new Intent(context, InfoActivity.class);
        if (!isContact)  // ????????????????????????????????????????????????
            intent.putExtra("Type", Contact.CONTACT_TYPE_SEARCH);
        else  // ???????????????????????????????????????
            intent.putExtra("Type", Contact.CONTACT_TYPE_LIST);
        intent.putExtra("Contact", contact);  // ?????????????????????
        startActivityForResult(intent, 1);
    }

    @Override
    public void gotoGroupInfoActivity(String groupID) {
        Intent intent = new Intent(ChattingActivity.this, GroupInfoActivity.class);
        intent.putExtra("Group ID", groupID);  // ????????????ID
        if (mPresenter.getMemberInfo() == null) return;
        intent.putParcelableArrayListExtra("Group Member Info", mPresenter.getMemberInfo());
        startActivityForResult(intent, 1);
    }

    @Override
    public void showText(String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
}
