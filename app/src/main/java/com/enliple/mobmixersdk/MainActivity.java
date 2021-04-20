package com.enliple.mobmixersdk;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mobmixer.sdk.AdBannerView;
import com.mobmixer.sdk.BannerType;
import com.mobmixer.sdk.callback.iMobonBannerCallback;

public class MainActivity extends AppCompatActivity {

    private AdBannerView adBannerView;
    private LinearLayout banner_container;
    private String UNIT_ID = "43";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        banner_container = findViewById(R.id.banner_container);

        adBannerView = new AdBannerView(this, BannerType.BANNER_320x50).setBannerUnitId(UNIT_ID);
        adBannerView.setAdListener(new iMobonBannerCallback() {
            @Override
            public void onLoadedAdInfo(boolean result, String errorcode) {
                if (result) {
                    //배너 광고 로딩 성공
                    System.out.println("광고로딩!!!!!");
                    banner_container.addView(adBannerView);
                } else {
                    Toast.makeText(MainActivity.this, errorcode, Toast.LENGTH_SHORT).show();
                    System.out.println("광고실패 : " + errorcode);
                    adBannerView.destroyAd();
                }
            }

            @Override
            public void onAdClicked() {

            }

        });
        adBannerView.loadAd();
    }
}