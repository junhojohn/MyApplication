package net.clipcodes.myapplication.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;

import net.clipcodes.myapplication.R;
import net.clipcodes.myapplication.accounts.SessionCallback;
import net.clipcodes.myapplication.models.AdditionalProductInfo;
import net.clipcodes.myapplication.ui.ConnectionConst;
import net.clipcodes.myapplication.utils.Libraries;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SplashscreenActivity extends Activity {
    private String TAG = "SplashscreenActivity";
    private static final String TAG_RESULTS             = "result";
    private static final String TAG_PRODUCT_NAME        = "productName";
    private static final String TAG_SELLER_ID           = "sellerID";
    private static final String TAG_PRODUCT_ITEM_CNT    = "productItemCnt";
    private static final String TAG_PRODUCT_PRICE       = "productPrice";
    private static final String TAG_PRODUCT_DESC        = "productDescription";
    private static final String TAG_PRODUCT_BIG_CATEGORY= "bigCategory";
    private static final String TAG_PRODUCT_MID_CATEGORY= "midCategory";
    private static final String TAG_PRODUCT_SMALL_CATEGORY= "smallCategory";
    private static final String TAG_PRODUCT_IMG         = "imageFilePath";
    private ArrayList<AdditionalProductInfo> productItemList  = null;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
    /** Called when the activity is first created. */
    Thread splashTread;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        checkKakaoLoginSession();
//        onClickLogout();
//        StartAnimations();

        getData();
    }

    private void checkKakaoLoginSession(){
        if(Session.getCurrentSession().checkAndImplicitOpen()){
            Log.e(TAG, "토큰큰 : " + Session.getCurrentSession().getTokenInfo().getAccessToken());
            Log.e(TAG, "토큰큰 리프레쉬토큰 : " + Session.getCurrentSession().getTokenInfo().getRefreshToken());
            Log.e(TAG, "토큰큰 파이어데이트 : " + Session.getCurrentSession().getTokenInfo().getRemainingExpireTime());

            Session.getCurrentSession().addCallback(SessionCallback.getInstance());
            SessionCallback.getInstance().requestMe();
        }
    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_splash_alpha_color);
        anim.reset();
        LinearLayout l=(LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.splash);
        iv.clearAnimation();
        iv.startAnimation(anim);

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 2000) {
                        sleep(100);
                        waited += 100;
                    }
                    Intent intent = new Intent(SplashscreenActivity.this,
                            MainActivity.class);

                    intent.putExtra("productItemList", productItemList);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    SplashscreenActivity.this.finish();
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    SplashscreenActivity.this.finish();
                }

            }
        };
        splashTread.start();

    }

    public void getData(){

        class GetDataJSON extends AsyncTask<String, Void, List> {

            @Override
            protected List<AdditionalProductInfo> doInBackground(String... params) {
                if(productItemList == null || productItemList.size() == 0){
                    String myJSON = getHttpJSONData(ConnectionConst.PRODUCT_INFO_SEARCH_SERVER_URL);
                    productItemList = setJSONDataToProductItem(myJSON);
                }

                return productItemList;
            }

            @Override
            protected void onPostExecute(List productItemList) {
                StartAnimations();
            }
        }

        GetDataJSON getDataJSON = new GetDataJSON();
        getDataJSON.execute();

    }

    protected String getHttpJSONData(String url){
        long currTime = System.currentTimeMillis();
        String result = null;
        DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
        HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Content-type", "application/json");

        InputStream inputStream = null;
        try{
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            inputStream = entity.getContent();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            result = sb.toString();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
        }
        System.out.println("getHttpJSONData: " + (System.currentTimeMillis() - currTime));
        return result;
    }

    protected ArrayList<AdditionalProductInfo> setJSONDataToProductItem(String myJSON){
        long currTime = System.currentTimeMillis();
        ArrayList<AdditionalProductInfo> productItemList = new ArrayList<>();
        AdditionalProductInfo productItem = null;
        JSONArray peoples = null;

        try{
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);
            for(int i = 0 ; i < peoples.length() ; i ++){
                JSONObject productJsonData = peoples.getJSONObject(i);
                String productName      = productJsonData.getString(TAG_PRODUCT_NAME);
                String sellerID         = productJsonData.getString(TAG_SELLER_ID);
                String productPrice     = productJsonData.getString(TAG_PRODUCT_PRICE);
                String productItemCnt   = productJsonData.getString(TAG_PRODUCT_ITEM_CNT);
                String productDesc      = productJsonData.getString(TAG_PRODUCT_DESC);
                String bigCategory      = productJsonData.getString(TAG_PRODUCT_BIG_CATEGORY);
                String midCategory      = productJsonData.getString(TAG_PRODUCT_MID_CATEGORY);
                String smallCategory    = productJsonData.getString(TAG_PRODUCT_SMALL_CATEGORY);
                String productImg       = productJsonData.getString(TAG_PRODUCT_IMG);

//                if(bigCategory.equals(getString(R.string.title_best_product))){
                    productItem = getContainObject(productItemList, productName);
                    if(productItem == null){
                        productItem = new AdditionalProductInfo(productName, productDesc);
                        productItem.setSellerName(sellerID);
                        productItem.setItemCount(Integer.parseInt(productItemCnt));
                        productItem.setPrice(Integer.parseInt(productPrice));
                        productItem.setBigCategory(bigCategory);
                        productItem.setMidCategory(midCategory);
                        productItem.setSmallCategory(smallCategory);
                        productItemList.add(productItem);
                    }

                    Bitmap bitmap = getProductImages(ConnectionConst.IMAGE_DOWNLOAD_SERVER_URL + productImg);
//                    int orientDegree = Libraries.getOrientationOfImage(ConnectionConst.IMAGE_DOWNLOAD_SERVER_URL + productImg);
//                    bitmap = Libraries.getRotatedBitmap(bitmap, orientDegree);
                    Libraries.saveBitmapToJpeg(bitmap, this.getCacheDir().toString(), productImg);

                    productItem.getImageURLPathList().add(this.getCacheDir().toString() + "/" + productImg);
                    bitmap = null;


//                }
            }
            System.out.println("setJSONDataToProductItem: " + (System.currentTimeMillis() - currTime));
        }catch (JSONException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return productItemList;
    }

    public AdditionalProductInfo getContainObject(List<AdditionalProductInfo> productItemList, String productName){
        for(AdditionalProductInfo productInfo : productItemList){
            if(productInfo.getName().equals(productName)){
                return productInfo;
            }
        }
        return null;
    }

    protected Bitmap getProductImages(String imageFileUrl){
        Bitmap bmImg = null;
        try{
            URL myFileUrl = new URL(imageFileUrl);
            HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();

            bmImg = BitmapFactory.decodeStream(is);


        }catch(IOException e){
            e.printStackTrace();
        }
        return  bmImg;
    }
}