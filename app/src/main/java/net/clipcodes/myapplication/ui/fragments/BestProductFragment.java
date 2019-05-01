package net.clipcodes.myapplication.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.clipcodes.myapplication.models.FlowerData;
import net.clipcodes.myapplication.ui.adapters.MyAdapter;
import net.clipcodes.myapplication.R;

import java.util.ArrayList;
import java.util.List;


public class BestProductFragment extends Fragment {
    RecyclerView mRecyclerView;
    List<FlowerData> mFlowerList;
    FlowerData mFlowerData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View seller = inflater.inflate(R.layout.tab_best_product, container, false);

        mFlowerList = new ArrayList<>();
        mFlowerData = new FlowerData("Rose", getString(R.string.description_flower_rose),
                R.drawable.rose);
        mFlowerList.add(mFlowerData);
        mFlowerData = new FlowerData("Carnation", getString(R.string.description_flower_carnation),
                R.drawable.carnation);
        mFlowerList.add(mFlowerData);
        mFlowerData = new FlowerData("Tulip", getString(R.string.description_flower_tulip),
                R.drawable.tulip);
        mFlowerList.add(mFlowerData);
        mFlowerData = new FlowerData("Daisy", getString(R.string.description_flower_daisy),
                R.drawable.daisy);
        mFlowerList.add(mFlowerData);
        mFlowerData = new FlowerData("Sunflower", getString(R.string.description_flower_sunflower),
                R.drawable.sunflower);
        mFlowerList.add(mFlowerData);
        mFlowerData = new FlowerData("Daffodil", getString(R.string.description_flower_daffodil),
                R.drawable.daffodil);
        mFlowerList.add(mFlowerData);
        mFlowerData = new FlowerData("Gerbera", getString(R.string.description_flower_gerbera),
                R.drawable.gerbera);
        mFlowerList.add(mFlowerData);
        mFlowerData = new FlowerData("Orchid", getString(R.string.description_flower_orchid),
                R.drawable.orchid);
        mFlowerList.add(mFlowerData);
        mFlowerData = new FlowerData("Iris", getString(R.string.description_flower_iris),
                R.drawable.iris);
        mFlowerList.add(mFlowerData);
        mFlowerData = new FlowerData("Lilac", getString(R.string.description_flower_lilac),
                R.drawable.lilac);
        mFlowerList.add(mFlowerData);

        mRecyclerView = seller.findViewById(R.id.recyclerview);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        MyAdapter myAdapter = new MyAdapter(getActivity(), mFlowerList);
        mRecyclerView.setAdapter(myAdapter);
        return seller;
    }
}