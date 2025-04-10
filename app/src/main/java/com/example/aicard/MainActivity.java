package com.example.aicard;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends CustomizeMainActivity implements View.OnClickListener {

    // 顶部标签栏容器
    private LinearLayout tabContainer;
    // 底部导航栏的主页按钮
    private LinearLayout navHome;
    // 底部导航栏的列表按钮
    private LinearLayout navList;
    // 底部导航栏的设置按钮
    private LinearLayout navSettings;

    // 当前选中的底部标签栏位置
    private int currentBottomTab = 0;
    // 当前选中的顶部标签栏位置
    private int currentTopTab = 0;

    private final Map<Integer, List<View>> tabsMap = new HashMap<>();
    public final Map<Integer, ContentFragment> tabAndContentMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupBottomNavigation();
        updateTopTabs(currentBottomTab);
    }

    // 初始化视图，查找布局中的视图组件
    private void initViews() {
        tabContainer = findViewById(R.id.tab_container);
        navHome = findViewById(R.id.nav_home);
        navList = findViewById(R.id.nav_list);
        navSettings = findViewById(R.id.nav_settings);
    }

    // 设置底部导航栏的点击监听器
    private void setupBottomNavigation() {
        navHome.setOnClickListener(this);
        navList.setOnClickListener(this);
        navSettings.setOnClickListener(this);
    }

    // 处理视图点击事件，根据点击的视图 ID 更新顶部标签栏
    @Override
    public void onClick(View v) {
        String msg = String.format("%s<%s>  id=%s", v.getClass().getSimpleName(), getText_TextView(v), v.getId());
        Log.v(TAG, ">>> onClick: " + msg);

        // 切换导航页
        if (isClickNavigationBar(v)) onClickNavigationBar(v);
    }

    // 底部导航栏
    //Set<Integer> navigationBar = new HashSet<>(Arrays.asList(R.id.nav_home, R.id.nav_list, R.id.nav_settings));
    private static final int[] NAVIGATIONBAR = new int[]{R.id.nav_home, R.id.nav_list, R.id.nav_settings};

    /**
     * 检查点击的view是否是navigationBar中的
     */
    public boolean isClickNavigationBar(View view) {
        for (int i : NAVIGATIONBAR) {
            if (i == view.getId()) return true;
        }
        return false;
    }

    /**
     * 点击底部导航栏时
     */
    public void onClickNavigationBar(View view) {
        // 点击底部导航栏，切换顶部标签栏
        int index = 0;
        for (int i = 0; i < NAVIGATIONBAR.length; i++) {
            if (view.getId() == NAVIGATIONBAR[i]) {
                index = i;
            }
            findViewById(NAVIGATIONBAR[i]).setBackgroundColor(Color.TRANSPARENT);
            View lastTab = tabsMap.get(currentBottomTab).get(currentTopTab);
            lastTab.setBackgroundColor(Color.TRANSPARENT);
        }
        currentBottomTab = index;
        updateTopTabs(index);
        view.setBackgroundColor(Color.GRAY);
        Log.i(TAG, String.format("切换导航(%s): %s  id=%s", index, getText_TextView(view), view.getId()));
    }


    // 根据点击的底部标签栏 更新顶部标签栏
    private void updateTopTabs(int bottomTab) {
        if (!tabsMap.containsKey(bottomTab)) {
            // 初始化top标签组
            String[] titles = getTobTabTitles(bottomTab);
            if (titles.length == 0)
                throw new RuntimeException("无顶部标签栏，所属导航页 id=" + NAVIGATIONBAR[bottomTab]);
            // 创建top标签组
            ArrayList<View> tabs = new ArrayList<>();
            for (int i = 0; i < titles.length; i++) {
                TextView tab = createTobTab(titles[i], i);
                tabs.add(tab);
            }
            tabsMap.put(bottomTab, tabs);
            // 默认为标签组的第一个
            currentTopTab = 0;
        }
        // 提取已有top标签组
        List<View> tabs = tabsMap.get(bottomTab);
        if (tabs == null || tabs.isEmpty())
            throw new RuntimeException("top标签栏缺失，所属导航页 id=" + NAVIGATIONBAR[bottomTab]);
        tabContainer.removeAllViews();
        for (int i = 0; i < tabs.size(); i++) {
            tabContainer.addView(tabs.get(i), i);
        }
        // 切换top标签栏时 默认第一个标签 更新Content
        updateContent(tabs.get(0).getId());
    }

    // 创建顶部标签栏的标签视图
    private TextView createTobTab(String title, int index) {
        TextView tab = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(20, 0, 20, 0);
        tab.setId(View.generateViewId());
        tab.setLayoutParams(params);
        tab.setText(title);
        tab.setGravity(android.view.Gravity.CENTER);
        tab.setPadding(20, 0, 20, 0);
        tab.setMinWidth(200);

        // 设置标签视图的点击监听器，更新当前顶部标签栏位置并更新内容
        tab.setOnClickListener(v -> {
            View lastTab = tabsMap.get(currentBottomTab).get(currentTopTab);
            lastTab.setBackgroundColor(Color.TRANSPARENT);

            currentTopTab = index;
            v.setBackgroundColor(Color.GRAY);
            updateContent(tab.getId());
            Log.v(TAG, String.format("切换标签(%s): %s  id=%s", currentTopTab, tab.getText(), tab.getId()));
        });
        return tab;
    }

    private void updateContent(int id) {
        ContentFragment fragment;
        if (!tabAndContentMap.containsKey(id)) {
            // 初始化fragment
            ContentFragment frag = ContentFragment.newInstance(id);
            Log.i(TAG, String.format("初始化 ContentFragment id = %s, tabTd = %s ", frag.getId(), frag.getTab()));
            tabAndContentMap.put(id, frag);
        }
        // 提取已有 fragment
        fragment = tabAndContentMap.get(id);
        try {
            // 替换指定容器中的 Fragment
            String binding = String.valueOf(id);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content_container, fragment, binding);
            // 提交 Fragment 事务
            transaction.commit();
        } catch (Exception e) {
            // 处理 Fragment 事务提交失败的异常
            Log.e(TAG, "Fragment 切换异常", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    // 根据底部标签栏位置获取顶部标签栏的标题数组
    private String[] getTobTabTitles(int bottomTab) {
        switch (bottomTab) {
            case 0:
                return new String[]{"推荐", "最新", "热门", "关注"};
            case 1:
                return new String[]{"待办", "已完成", "进行中", "已归档"};
            case 2:
                return new String[]{"通用", "账号", "通知", "关于"};
            default:
                return new String[]{};
        }
    }

    public String getText_TextView(View view) {
        if (!(view instanceof TextView)) return "";
        return ((TextView) view).getText().toString();
    }
}