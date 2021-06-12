package com.example.im.mvp.presenter.base;

import com.example.im.bean.AccountInfo;
import com.example.im.listener.OnLoginListener;
import com.example.im.mvp.contract.base.ISignUpContract;
import com.example.im.mvp.model.base.SignUpModel;

public class SignUpPresenter implements ISignUpContract.Presenter {
    private ISignUpContract.Model mModel;
    private ISignUpContract.View mView;

    public SignUpPresenter(ISignUpContract.View view) {
        this.mModel = new SignUpModel();
        this.mView = view;
    }

    @Override
    public void login() {
        String username = mView.getUsername();
        String password = mView.getPassword();
        String confirmPassword = mView.getConfirmPassword();
        if (password.equals(confirmPassword)) {
            mModel.login(username, password, new OnLoginListener() {
                @Override
                public void loginSuccess(AccountInfo account) {

                }

                @Override
                public void loginFailed() {

                }
            });
        }
        else {

        }
    }
}
