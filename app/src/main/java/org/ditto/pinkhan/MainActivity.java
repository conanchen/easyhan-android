package org.ditto.pinkhan;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.crashlytics.android.Crashlytics;
import com.roughike.bottombar.BottomBar;
import com.xdandroid.hellodaemon.IntentWrapper;

import org.ditto.feature.base.BaseFragmentActivity;
import org.ditto.feature.base.FragmentsPagerAdapter;
import org.ditto.feature.my.index.FragmentMy;
import org.ditto.feature.visitor.FragmentVisitorIndices;
import org.ditto.feature.word.index.FragmentWords;
import org.ditto.lib.Constants;
import org.easyhan.common.grpc.HanziLevel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

@Route(path = "/app/MainActivity")
public class MainActivity extends BaseFragmentActivity {

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.navigation)
    BottomBar bottomBar;

    private int mBottombarTab0 = 0;
    private int mBottombarTab0Fragment0;

    private int mBottombarTab1 = 1;
    private int mBottombarTab1Fragment0;

    private int mBottombarTab2 = 2;
    private int mBottombarTab2Fragment0;

    private int mBottombarTab3 = 3;
    private int mBottombarTab3Fragment0;

    private int mBottombarTab4 = 4;
    private int mBottombarTab4Fragment0;

    private final int SDK_PERMISSION_REQUEST = 127;
    private String permissionInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupToolbar();
        setupPagerAndBottombar();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // after andrioid m,must request Permiision on runtime
        getPersimmions();
    }


    //防止华为机型未加入白名单时按返回键回到桌面再锁屏后几秒钟进程被杀
    @Override
    public void onBackPressed() {
        IntentWrapper.onBackPressed(this);
    }


    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //toolbar.setLogo(android.R.drawable.ic_dialog_email);
        toolbar.setTitle("粉红字帖");
        //以上3个属性必须在setSupportActionBar(toolbar)之前调用
        setSupportActionBar(toolbar);
        //设置导航Icon，必须在setSupportActionBar(toolbar)之后设置
        //toolbar.setNavigationIcon(R.drawable.ic_person_black_24dp);
        //添加菜单点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(toolbar, "Click setNavigationIcon", Snackbar.LENGTH_SHORT).show();
            }
        });

    }


    private void setupPagerAndBottombar() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        int fmIdx = 0;
        Map<Integer, Fragment> fmList = new HashMap<>();
        mBottombarTab0Fragment0 = fmList.size();
        fmList.put(fmIdx++, FragmentMy.create("我的资料", HanziLevel.THREE));
        mBottombarTab1Fragment0 = fmList.size();
        fmList.put(fmIdx++, FragmentWords.create("一级字表", HanziLevel.ONE));

        mBottombarTab2Fragment0 = fmList.size();
        fmList.put(fmIdx++, FragmentVisitorIndices.create("今天"));
        mBottombarTab3Fragment0 = fmList.size();
        fmList.put(fmIdx++, FragmentWords.create("二级字表", HanziLevel.TWO));
        mBottombarTab4Fragment0 = fmList.size();
        fmList.put(fmIdx++, FragmentWords.create("三级字表", HanziLevel.THREE));

        FragmentsPagerAdapter fmAapter = new FragmentsPagerAdapter(this.getSupportFragmentManager(), fmList);
        viewPager.setAdapter(fmAapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mBottombarTab0Fragment0 <= position
                        && position < mBottombarTab1Fragment0
                        && mBottombarTab0 != bottomBar.getCurrentTabPosition()) {
                    bottomBar.selectTabAtPosition(mBottombarTab0, true);
                } else if (
                        mBottombarTab1Fragment0 <= position
                                && position < mBottombarTab2Fragment0
                                && mBottombarTab1 != bottomBar.getCurrentTabPosition()) {
                    bottomBar.selectTabAtPosition(mBottombarTab1, true);
                } else if (
                        mBottombarTab2Fragment0 <= position
                                && position < mBottombarTab3Fragment0
                                && mBottombarTab2 != bottomBar.getCurrentTabPosition()) {
                    bottomBar.selectTabAtPosition(mBottombarTab2, true);
                } else if (
                        mBottombarTab3Fragment0 <= position
                                && position < mBottombarTab4Fragment0
                                && mBottombarTab3 != bottomBar.getCurrentTabPosition()) {
                    bottomBar.selectTabAtPosition(mBottombarTab3, true);
                } else if (
                        mBottombarTab4Fragment0 <= position
                                && mBottombarTab4 != bottomBar.getCurrentTabPosition()) {
                    bottomBar.selectTabAtPosition(mBottombarTab4, true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bottomBar.setOnTabSelectListener((@IdRes int tabId) -> {
            switch (tabId) {
                case R.id.navigation_tab0:
                    if (!(mBottombarTab0Fragment0 <= viewPager.getCurrentItem()
                            && viewPager.getCurrentItem() < mBottombarTab1Fragment0)) {
                        viewPager.setCurrentItem(mBottombarTab0Fragment0, true);
                    }
                    break;
                case R.id.navigation_tab1:
                    if (!(mBottombarTab1Fragment0 <= viewPager.getCurrentItem()
                            && viewPager.getCurrentItem() < mBottombarTab2Fragment0)) {
                        viewPager.setCurrentItem(mBottombarTab1Fragment0, true);
                    }
                    break;
                case R.id.navigation_tab2:
                    if (!(mBottombarTab2Fragment0 <= viewPager.getCurrentItem()
                            && viewPager.getCurrentItem() < mBottombarTab3Fragment0)) {
                        viewPager.setCurrentItem(mBottombarTab2Fragment0, true);
                    }
                    break;
                case R.id.navigation_tab3:
                    if (!(mBottombarTab3Fragment0 <= viewPager.getCurrentItem()
                            && viewPager.getCurrentItem() < mBottombarTab4Fragment0)) {
                        viewPager.setCurrentItem(mBottombarTab3Fragment0, true);
                    }
                    break;
                case R.id.navigation_tab4:
                    if (!(mBottombarTab4Fragment0 <= viewPager.getCurrentItem())) {
                        viewPager.setCurrentItem(mBottombarTab4Fragment0, true);
                    }
                    break;
            }
        });

        bottomBar.setOnTabReselectListener((@IdRes int tabId) -> {
            switch (tabId) {
                case R.id.navigation_tab0:
//                    ARouter.getInstance().build("/feature_image/UpsertActivity")
//                            .withString(Constants.HANZILEVEL, HanziLevel.ONE.name())
//                            .navigation();
                    break;
                case R.id.navigation_tab1:
                    break;
                case R.id.navigation_tab3:
                    break;
                case R.id.navigation_tab4:
                    break;
            }
        });

        bottomBar.selectTabAtPosition(mBottombarTab2, true);
    }

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            /*
             * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
