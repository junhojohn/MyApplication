package net.clipcodes.myapplication.ui.pages;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.clipcodes.myapplication.R;

public class PageSearch extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_two = inflater.inflate(R.layout.fragment_search, container, false);

        return fragment_two;
    }

}