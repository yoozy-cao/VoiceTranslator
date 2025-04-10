package com.example.aicard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicInteger;

public class ContentFragment extends CustomizeFragment {
    private static final String ARG_POSITION_KEY = "position";
    private static final AtomicInteger ARG_POSITION_VALUE = new AtomicInteger(0);
    private int tab;

    private ContentFragment() {
    }

    public static ContentFragment newInstance(int id) {
        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION_KEY, ARG_POSITION_VALUE.getAndAdd(1));
        fragment.setArguments(args);
        fragment.tab = id;
        return fragment;
    }

    public int getTab() {
        return tab;
    }

    public static int getPosition() {
        return ARG_POSITION_VALUE.get();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView view = new TextView(getContext());
        // int position = getArguments().getInt(ARG_POSITION_KEY);
        view.setText("页面内容 " + this.getTab());

        return view;
    }


} 