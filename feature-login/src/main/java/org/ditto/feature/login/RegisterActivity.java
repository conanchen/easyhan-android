package org.ditto.feature.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.alibaba.android.arouter.facade.annotation.Route;

import org.ditto.feature.login.controllers.RegisterController;
import org.ditto.feature.base.BaseFragment;
import org.ditto.feature.base.FragmentsPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = "/feature_login/RegisterActivity")
public class RegisterActivity extends AppCompatActivity implements RegisterController.Callbacks
{

    @BindView(R2.id.view_pager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R2.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupViewPager();
    }

    private void setupViewPager() {
        Map<Integer,Fragment> fmList = new HashMap<>();
        fmList.put(0,RegisterUsernameFragment.create("注册手机号"));
        fmList.put(1,RegisterPasswordFragment.create("填写登录密码"));
        fmList.put(2,RegisterOkFragment.create("注册成功"));

        FragmentsPagerAdapter fmAapter = new FragmentsPagerAdapter(this.getSupportFragmentManager(), fmList);

        viewPager.setAdapter(fmAapter);
        viewPager.setCurrentItem(0, true);

    }

    @Override
    public void onUsernameDone(String username) {
        viewPager.setCurrentItem(1);
    }

    @Override
    public void onPasswordDone(String username) {
        viewPager.setCurrentItem(2);
    }
}
