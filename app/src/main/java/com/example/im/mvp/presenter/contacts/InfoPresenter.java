package com.example.im.mvp.presenter.contacts;

import com.example.im.bean.AccountInfo;
import com.example.im.bean.contacts.Contact;
import com.example.im.mvp.contract.contacts.IInfoContract;
import com.example.im.mvp.model.contacts.InfoModel;

public class InfoPresenter implements IInfoContract.Presenter {
    private IInfoContract.Model mModel;
    private IInfoContract.View mView;

    private Contact contact;
    private String username;  // My Username

    public InfoPresenter(IInfoContract.View view, Contact contact) {
        this.mModel = new InfoModel(this);
        this.mView = view;
        this.contact = contact;
        this.username = AccountInfo.getInstance().getUsername();
    }

    @Override
    public void showInfo() {
        //mView.setAvatar(contact.getAvatar());
        mView.setNickname(contact.getNickname());
        mView.setUsername(contact.getUsername());
        mView.setAvatar(contact.getAvatar());
    }

    @Override
    public void createChatting() {
        mModel.createChatting(AccountInfo.getInstance().getId(), contact.getId());
    }

    public void createSuccess(String chattingId) {
        mView.gotoChattingActivity(contact, chattingId);
    }
    public void createFailure(String error) {
        mView.showText(error);
    }


    @Override
    public void clearChattingHistory() {
        mModel.delete(contact.getId());
    }

    public void clearSuccess() {
        mView.showText("聊天记录清除成功");
    }
    public void clearFailure(String error) {
        mView.showText(error);
    }


    @Override
    public void add() {
        mModel.add(contact.getId());
    }

    public void addSuccess() {
        mView.showText("添加好友成功");
        mView.gotoInfoActivity(contact);
    }
    public void addFailure(String error) {
        mView.showText(error);
    }


    @Override
    public void delete() {
        mModel.delete(contact.getId());
    }

    public void deleteSuccess() {
        mView.showText("删除好友成功");
        mView.gotoMainActivity();
    }
    public void deleteFailure(String error) {
        mView.showText(error);
    }
}
