package net.clipcodes.myapplication.ui.pages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kakao.auth.Session;
import com.melnykov.fab.FloatingActionButton;

import net.clipcodes.myapplication.accounts.SessionCallback;
import net.clipcodes.myapplication.models.AdditionalProductInfo;
import net.clipcodes.myapplication.ui.LOGIN_AFTER_REDIR_PAGE_ENUM;
import net.clipcodes.myapplication.ui.activities.DetailProductActivity;
import net.clipcodes.myapplication.ui.activities.LoginActivity;
import net.clipcodes.myapplication.ui.activities.RegisterActivity;
import net.clipcodes.myapplication.ui.fragments.CheapProductFragment;
import net.clipcodes.myapplication.ui.fragments.BestProductFragment;
import net.clipcodes.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PageHome extends Fragment {
    BestProductFragment bestProductFragment = new BestProductFragment();
    CheapProductFragment cheapProductFragment = new CheapProductFragment();
    FrameLayout bestFragment, cheapFragment;
    View bestProductView, cheapProductView;
    TextView textBest, textCheap;
    int NUM_PAGES = 2;
    int currentPage = 0;
    boolean touched = false;
    Handler handler = new Handler();
    Runnable update;
    FloatingActionButton btnRegister;
    private String TAG = "PageHome";
    private ArrayList<AdditionalProductInfo> productItemList  = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_three = inflater.inflate(R.layout.fragment_home, container, false);
        productItemList = (ArrayList<AdditionalProductInfo>)getArguments().getSerializable("productItemList");
        //INIT VIEWS
        init(fragment_three);
        //SET TABS ONCLICK
        bestFragment.setOnClickListener(clik);
        cheapFragment.setOnClickListener(clik);
        btnRegister.setOnClickListener(clik);

        // pass parameters
        ArrayList<AdditionalProductInfo> bestProductList = new ArrayList<AdditionalProductInfo>();
        ArrayList<AdditionalProductInfo> cheapProductList = new ArrayList<AdditionalProductInfo>();
        for(AdditionalProductInfo additionalProductInfo : productItemList){
            if(additionalProductInfo.getBigCategory().equals(getString(R.string.title_best_product))){
                bestProductList.add(additionalProductInfo);
            }else if(additionalProductInfo.getBigCategory().equals(getString(R.string.title_cheap_product))){
                cheapProductList.add(additionalProductInfo);
            }
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("bestProductList", bestProductList);
        bestProductFragment.setArguments(bundle);
        bundle.putSerializable("cheapProductList", cheapProductList);
        cheapProductFragment.setArguments(bundle);

        //LOAD PAGE FOR FIRST
        loadPage(bestProductFragment);
        textBest.setTextColor(getActivity().getResources().getColor(R.color.gradStop));
//        startPagerAutoSwipe();
        return fragment_three;
    }

    public void init(View v){
        bestFragment = v.findViewById(R.id.best_fragment);
        cheapFragment = v.findViewById(R.id.cheap_fragment);
        bestProductView = v.findViewById(R.id.bestProductView);
        cheapProductView = v.findViewById(R.id.cheapProductView);
        textBest = v.findViewById(R.id.text_best);
        textCheap = v.findViewById(R.id.text_cheap);
        btnRegister = v.findViewById(R.id.btn_register);
    }

    View.OnTouchListener autoMove = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    touched = true;
                    return true;

                case MotionEvent.ACTION_UP:
                    touched = false;
                    return true;
            }
            return false;
        }
    };
    //ONCLICK LISTENER
    public View.OnClickListener clik = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.best_fragment:
                    //ONSELLER CLICK
                    //LOAD SELLER FRAGMENT CLASS
                    loadPage(bestProductFragment);

                    //WHEN CLICK TEXT COLOR CHANGED
                    textBest.setTextColor(getActivity().getResources().getColor(R.color.gradStop));
                    textCheap.setTextColor(getActivity().getResources().getColor(R.color.grey));

                    //VIEW VISIBILITY WHEN CLICKED
                    bestProductView.setVisibility(View.VISIBLE);
                    cheapProductView.setVisibility(View.INVISIBLE);
                    break;
                case R.id.cheap_fragment:
                    //ONBUYER CLICK
                    //LOAD BUYER FRAGMENT CLASS
                    loadPage(cheapProductFragment);

                    //WHEN CLICK TEXT COLOR CHANGED
                    textBest.setTextColor(getActivity().getResources().getColor(R.color.grey));
                    textCheap.setTextColor(getActivity().getResources().getColor(R.color.gradStop));

                    //VIEW VISIBILITY WHEN CLICKED
                    bestProductView.setVisibility(View.INVISIBLE);
                    cheapProductView.setVisibility(View.VISIBLE);
                    break;
                case R.id.btn_register:
                    if(Session.getCurrentSession().checkAndImplicitOpen()){

                        Intent mIntent = new Intent(view.getContext(), RegisterActivity.class);
                        mIntent.putExtra("sellerInfo", SessionCallback.getInstance().getSellerInfo());
                        view.getContext().startActivity(mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }else{
                        Intent loginIntent = new Intent(view.getContext(), LoginActivity.class);
                        loginIntent.putExtra("nextActivityToMove", LOGIN_AFTER_REDIR_PAGE_ENUM.REGISTER_ACTIVITY.getActivityName());
                        startActivity(loginIntent);
                    }


                    break;
            }
        }
    };

    //LOAD PAGE FRAGMENT METHOD
    private boolean loadPage(Fragment fragment) {

        if (fragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerpage, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void startPagerAutoSwipe() {
        update = new Runnable() {
            public void run() {
                if(!touched){
                    if (currentPage == 0) {
                        loadPage(bestProductFragment);

                        //WHEN CLICK TEXT COLOR CHANGED
                        textBest.setTextColor(getActivity().getResources().getColor(R.color.gradStop));
                        textCheap.setTextColor(getActivity().getResources().getColor(R.color.grey));

                        //VIEW VISIBILITY WHEN CLICKED
                        bestProductView.setVisibility(View.VISIBLE);
                        cheapProductView.setVisibility(View.INVISIBLE);
                        currentPage++;
                    }else if(currentPage == 1){
                        currentPage = 0;
                        loadPage(bestProductFragment);

                        //WHEN CLICK TEXT COLOR CHANGED
                        textBest.setTextColor(getActivity().getResources().getColor(R.color.grey));
                        textCheap.setTextColor(getActivity().getResources().getColor(R.color.gradStop));

                        //VIEW VISIBILITY WHEN CLICKED
                        bestProductView.setVisibility(View.INVISIBLE);
                        cheapProductView.setVisibility(View.VISIBLE);
                    }
                }
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, 5000, 5000);
        swipeTimer.cancel();
    }
}