package com.example.im.mvp.model.chats;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.example.im.R;
import com.example.im.bean.chats.Chat;
import com.example.im.bean.chats.Msg;
import com.example.im.bean.contacts.Contact;
import com.example.im.listener.HttpCallbackListener;
import com.example.im.mvp.contract.chats.IChatsContract;
import com.example.im.mvp.model.contacts.ContactsModel;
import com.example.im.mvp.presenter.chats.ChatsPresenter;
import com.example.im.mvp.presenter.contacts.ContactsPresenter;
import com.example.im.util.HttpUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ChatsModel implements IChatsContract.Model {
    private static final int LOAD_SUCCESS = 1;
    private static final int LOAD_CONTACT_SUCCESS = 2;
    private static final int LOAD_FAILURE = 100;

    private ChatsModel.MyHandler mHandler;
    public ChatsModel(ChatsPresenter presenter) {
        mHandler = new ChatsModel.MyHandler(presenter);
    }

    private static class MyHandler extends Handler {
        private WeakReference<ChatsPresenter> mWeakReference;

        public MyHandler(ChatsPresenter presenter) {
            mWeakReference = new WeakReference<>(presenter);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ChatsPresenter mPresenter = mWeakReference.get();
            switch (msg.what) {
                case LOAD_SUCCESS:
                    mPresenter.loadSuccess((LinkedList<Chat>) msg.obj);
                    break;
                case LOAD_CONTACT_SUCCESS:
                    mPresenter.loadContactSuccess((LinkedList<Contact>) msg.obj);
                    break;
                case LOAD_FAILURE:
                    mPresenter.loadFailure(msg.obj.toString());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void loadChatList() {
        try {
            String url = "http://8.140.133.34:7200/chat/getAll";
            HttpUtil.sendHttpRequest(url, null, false, new HttpCallbackListener() {  // 发起http请求
                @Override
                public void onSuccess(String response) {  // http请求成功
                    Message msg = new Message();
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        if (jsonObject.getBoolean("success")) {
                            msg.what = LOAD_SUCCESS;
                            LinkedList<Chat> chatList = new LinkedList<>();

                            // 解析会话列表
                            JSONArray jsonArray = jsonObject.getJSONArray("chats");
                            for (int i = 0; i < jsonArray.length(); ++i) {
                                JSONObject chatJsonObject = jsonArray.getJSONObject(i);
                                String id = chatJsonObject.getString("id");
                                String type = chatJsonObject.getString("groupType");
                                String name = chatJsonObject.getString("groupName");
                                String[] members = new Gson().fromJson(
                                        chatJsonObject.getString("memberIdList"), String[].class);
                                chatList.add(new Chat(type, id, name, members));
                            }
                            msg.obj = chatList;
                        }
                        else {
                            msg.what = LOAD_FAILURE;
                            msg.obj = jsonObject.getString("msg");
                            mHandler.sendMessage(msg);
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    mHandler.sendMessage(msg);
                }

                @Override
                public void onFailure(Exception e) {  // http请求失败
                    Message msg = new Message();
                    msg.what = LOAD_FAILURE;
                    msg.obj = e.toString();
                    mHandler.sendMessage(msg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void loadContactInfo(ArrayList<String> contacts) {
        try {
            // 构建http请求的body
            JSONObject body = new JSONObject();
            JSONArray memberList = new JSONArray(contacts);
            body.put("users", memberList);  // 会话类型：群聊

            String url = "http://8.140.133.34:7200/user/searchUsers";
            HttpUtil.sendHttpRequest(url, body, false, new HttpCallbackListener() {  // 发起http请求
                @Override
                public void onSuccess(String response) {  // http请求成功
                    Message msg = new Message();
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        if (jsonObject.getBoolean("success")) {  // 加载群成员成功
                            msg.what = LOAD_CONTACT_SUCCESS;
                            Gson gson = new Gson();
                            Contact[] contacts = gson.fromJson(jsonObject.getString("users"), Contact[].class);
                            msg.obj = new LinkedList(Arrays.asList(contacts));
                        }
                        else {  // 加载失败
                            msg.what = LOAD_FAILURE;
                            msg.obj = jsonObject.getString("msg");  // 失败原因
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    mHandler.sendMessage(msg);
                }

                @Override
                public void onFailure(Exception e) {  // http请求失败
                    Message msg = new Message();
                    msg.what = LOAD_FAILURE;
                    msg.obj = e.toString();
                    mHandler.sendMessage(msg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
