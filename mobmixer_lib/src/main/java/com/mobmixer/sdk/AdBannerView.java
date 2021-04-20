package com.mobmixer.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.mobmixer.manager.LogPrint;
import com.mobmixer.manager.SpManager;
import com.mobmixer.sdk.callback.iMobonBannerCallback;


public class AdBannerView extends RelativeLayout {
    private final Context mContext;
    private String mBannerType = BannerType.BANNER_320x50;
    private String mBannerSiteCode = null;
    private iMobonBannerCallback mIBannerAdCallback = null;

    private WebView mWebview;
    private RelativeLayout mMainLayout;

    public AdBannerView(Context context) {
        super(context);

        this.mContext = context;
        mBannerType = BannerType.BANNER_320x50;
        onInit();


    }

    public AdBannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

    }

    public AdBannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;

    }

    public AdBannerView(Context context, String _adType) {
        super(context);
        this.mContext = context;
        mBannerType = _adType;
        onInit();
    }

    public AdBannerView setBannerUnitId(String unitId) {
        mBannerSiteCode = unitId;
        return this;
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        LogPrint.d("onWindowVisibilityChanged = " + visibility);
        if (visibility == View.GONE) {
            if (mWebview != null)
                mWebview.onPause();
        } else if (visibility == View.VISIBLE) {
            if (mWebview != null)
                mWebview.onResume();
        }

        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        LogPrint.d("hasWindowFocus = " + hasWindowFocus);
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        destroyAd();
    }


    private void onInit() {
        String adid = SpManager.getString(mContext, Key.ADID);
        if (TextUtils.isEmpty(adid)) {
            CommonUtils.getAdId(mContext);
        }
    }


    public void loadAd() {
        mMainLayout = new RelativeLayout(mContext);
        mMainLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        addView(mMainLayout);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                UpdateScriptUI(mBannerSiteCode);
            }
        }, 10);

    }

    public void destroyAd() {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mWebview != null)
                    mWebview.destroy();
                removeAllViews();
            }
        });

        mIBannerAdCallback = null;
    }


    public void setAdListener(iMobonBannerCallback pMobonAdCallback) {
        mIBannerAdCallback = pMobonAdCallback;
    }

    private void UpdateScriptUI(final String scriptCode) {

        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface", "ClickableViewAccessibility"})
                @Override
                public void run() {
                    mMainLayout.removeAllViews();
                    // inflate(mContext, R.layout.banner_script_layout, mMainLayout);
                    // mWebview = mMainLayout.findViewById(R.id.webview);
                    mWebview = new WebView(mContext);
                    mMainLayout.addView(mWebview);

                    WebSettings settings = mWebview.getSettings();
                    settings.setJavaScriptEnabled(true);
                    settings.setBuiltInZoomControls(false);
                    //settings.setDefaultTextEncodingName("EUC-KR");
                    settings.setJavaScriptCanOpenWindowsAutomatically(true);
                    settings.setSupportMultipleWindows(true);

                    // 웹뷰 - HTML5 창 속성 추가
                    String path = mContext.getDir("database", Context.MODE_PRIVATE).getPath();
                    settings.setDatabaseEnabled(true);
                    settings.setDatabasePath(path);
                    settings.setDomStorageEnabled(true);
                    settings.setBlockNetworkLoads(false);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// https 이미지.
                        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                    }

                    AndroidBridge ab = new AndroidBridge();
                    mWebview.addJavascriptInterface(ab, "mobonSDK");
                    mWebview.setVerticalScrollBarEnabled(false);
                    mWebview.setBackgroundColor(Color.TRANSPARENT);

                    mWebview.setOnTouchListener(new View.OnTouchListener() {
                        public boolean onTouch(View v, MotionEvent event) {
                            return (event.getAction() == MotionEvent.ACTION_MOVE);
                        }
                    });

                    mWebview.setWebChromeClient(new WebChromeClient() {
                        @Override
                        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {

                            WebView newWebView = new WebView(mContext);
                            view.addView(newWebView);
                            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                            transport.setWebView(newWebView);
                            resultMsg.sendToTarget();

                            newWebView.setWebViewClient(new WebViewClient() {
                                @Override
                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                    browserIntent.setData(Uri.parse(url));
                                    mContext.startActivity(browserIntent);
                                    if (mIBannerAdCallback != null)
                                        mIBannerAdCallback.onAdClicked();

                                    if (url.contains("//img.mobon.net/ad/linfo.php"))
                                        mWebview.goBack();
                                    return true;
                                }
                            });
                            return true;
                        }
                    });

                    mWebview.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            return super.shouldOverrideUrlLoading(view, url);
                        }

                        @Override
                        public void onPageFinished(WebView view, String url) {
                            //  view.setVisibility(View.VISIBLE);
                            if (url.contains("/servlet/auid")) {
                                loadSSPScript(mWebview, scriptCode);
                            } else {
                                mMainLayout.setVisibility(View.VISIBLE);
                            }

                        }
                    });

                    CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.setAcceptCookie(true);
                    if (Build.VERSION.SDK_INT >= 21) {
                        cookieManager.setAcceptThirdPartyCookies(mWebview, true);
                    }

                    String mediacategoryCookie = cookieManager.getCookie("https://mediacategory.com");
                    String au_id = SpManager.getString(mContext, Key.AUID);

                    if (TextUtils.isEmpty(mediacategoryCookie) || TextUtils.isEmpty(au_id) || (mediacategoryCookie != null && !mediacategoryCookie.contains(au_id))) {
                        mMainLayout.setVisibility(View.INVISIBLE);
                        String url = Url.DOMAIN_PROTOCOL + Url.API_AUID + "?adid=" + SpManager.getString(mContext, Key.ADID);
                        mWebview.loadUrl(url);
                    } else {
                        loadSSPScript(mWebview, scriptCode);
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            LogPrint.d("ERROR", "error => " + e.toString());
        }


    }

    private void loadSSPScript(WebView wv, final String code) {
        int width = 320;
        int height = 50;

        switch (mBannerType) {
            case BannerType.BANNER_320x50:
                width = 320;
                height = 50;
                break;
            case BannerType.BANNER_320x100:
                width = 320;
                height = 100;
                break;
            case BannerType.BANNER_300x250:
                width = 300;
                height = 250;
                break;
        }


        String htmlContent = "<script src=\"https://mixer.mobon.net/js/sspScript.min.js\" type=\"text/javascript\"></script>\n" +
                "<script type=\"text/javascript\">\n" +
                "    (function(){\n" +
                "        document.write(\"<div id='sspScript_div'></div>\");\n" +
                "        const sspId = \"" + code + "_" + width + "_" + height + "\";\n" +
                "        const sspNo = \"" + code + "\";\n" +
                "        const sspWidth = \"" + width + "\";\n" +
                "        const sspHeight = \"" + height + "\";\n" +
                "        const sspHref = window.location.href;\n" +
                "        const sspVer = \"" + CommonUtils.getAppVersion(mContext) + "\";\n" +
                "        const sspTelecom = \"" + CommonUtils.getTelecom(mContext) + "\";\n" +
                "        const sspGaid = \"" + CommonUtils.getAdid(mContext) + "\";\n" +
                "        sspScriptAppFn(sspNo,sspWidth,sspHeight,sspHref,sspVer,sspTelecom,sspGaid);\n" +
                "    })();\n" +
                "</script>";

        wv.loadDataWithBaseURL("https://mediacategory.com", htmlContent, "text/html; charset=utf-8", "UTF-8", null);

        if (mIBannerAdCallback != null)
            mIBannerAdCallback.onLoadedAdInfo(true, "");

    }


    private class AndroidBridge {
        @JavascriptInterface
        public void SuccessCallback() {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (mIBannerAdCallback != null)
                        mIBannerAdCallback.onLoadedAdInfo(true, "");
                }
            });
        }

        @JavascriptInterface
        public void FailCallback() {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                    mMainLayout.removeAllViews();
                    if (mIBannerAdCallback != null)
                        mIBannerAdCallback.onLoadedAdInfo(false, Key.NOFILL);

                }
            });
        }
    }


    @Override
    public int getDescendantFocusability() {
        return super.getDescendantFocusability();
    }


}
