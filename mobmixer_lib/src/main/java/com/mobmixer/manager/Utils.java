package com.mobmixer.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.mobmixer.sdk.Key;
import com.mobmixer.sdk.Url;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

@SuppressWarnings("MissingPermission")
public class Utils {

    /**
     * Added by 김현준 (2015-02-07) : DP를 density 가 반영된 PX로 바꿔주는 메서드
     *
     * @param pContext
     * @param pDp
     * @return
     */
    public static int convertDpToPx(Context pContext, int pDp) {
        try {
            return ((int) (pDp * pContext.getResources().getDisplayMetrics().density));
        } catch (Exception e) {
            LogPrint.e("convertDpToPx() Exception!", e);
            return 0;
        }
    }

    public static int convertpxTodp(Context pContext, int pPx) {
        try {
            return ((int) (pPx / pContext.getResources().getDisplayMetrics().density));
        } catch (Exception e) {
            LogPrint.e("convertPxToDp() Exception!", e);
            return 0;
        }
    }


    public static boolean isConnectNetwork(Context context) {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    public static Intent getDefaultBrowserIntent(Context context, String url) {
        PackageManager pm = context.getPackageManager();
        String[] browserPackages = {"com.android.browser", "com.sec.android.app.sbrowser", "com.android.chrome"};

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        for (String pkg : browserPackages) {
            intent.setPackage(pkg);
            if (intent.resolveActivity(pm) != null) {
                return intent;
            }
        }
        return intent.setPackage(null);
    }



    public static boolean getBrowserPackageName(Context _context, String url, boolean isBacon) {
        if (_context == null)
            return false;
        try {

         //   url = "https://mbris.loobig.co.kr/mediaCategory/externalcontent/mobLanding.html?siteUrl=" + URLEncoder.encode(url);
            String default_pkg = "";
            if (!isBacon) {
                Uri uri = Uri.parse(url);
                String auid = uri.getQueryParameter("au_id");
                if (TextUtils.isEmpty(auid)) {
                    url = url.replace("&au_id=", "");
                    Uri builtUri =
                            Uri.parse(url).buildUpon()
                                    .appendQueryParameter("au_id", SpManager.getString(_context, Key.AUID)).build();
                    url = builtUri.toString();
                }
            }

            ArrayList<String> pkgs = new ArrayList<>();
            String orderData = SpManager.getString(_context, Key.BROWSER_ORDER_DATA);
            if (TextUtils.isEmpty(orderData)) {
                String cross = SpManager.getString(_context, Key.MOBON_MEDIA_CROSS_BROWSER_VALUE); //y 일경우 실행 브라우저 순으로 ...


                if (TextUtils.equals(cross, "y") || TextUtils.equals(cross, "Y")) {
                    pkgs = new ArrayList<String>(Arrays.asList("com.nhn.android.search", "com.android.chrome", "com.sec.android.app.sbrowser", "net.daum.android.daum"));

                    if (!SpManager.getBoolean(_context, Key.NAVER_BROWSER_RUN) && isPackageInstalled(pkgs.get(0), _context)) {
                        default_pkg = pkgs.get(0);
                        SpManager.setBoolean(_context, Key.NAVER_BROWSER_RUN, true);
                    } else if (!SpManager.getBoolean(_context, Key.CHROME_BROWSER_RUN) && isPackageInstalled(pkgs.get(1), _context)) {
                        default_pkg = pkgs.get(1);
                        SpManager.setBoolean(_context, Key.CHROME_BROWSER_RUN, true);
                    } else if (!SpManager.getBoolean(_context, Key.SAMSUNG_BROWSER_RUN) && isPackageInstalled(pkgs.get(2), _context)) {
                        default_pkg = pkgs.get(2);
                        SpManager.setBoolean(_context, Key.SAMSUNG_BROWSER_RUN, true);
                    } else if (!SpManager.getBoolean(_context, Key.DAUM_BROWSER_RUN) && isPackageInstalled(pkgs.get(3), _context)) {
                        default_pkg = pkgs.get(3);
                        SpManager.setBoolean(_context, Key.DAUM_BROWSER_RUN, true);
                    } else {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
                        PackageManager pm = _context.getPackageManager();
                        final ResolveInfo mInfo = pm.resolveActivity(i, 0);
                        if (mInfo != null && mInfo.activityInfo != null && mInfo.activityInfo.applicationInfo != null && !TextUtils.equals(mInfo.activityInfo.applicationInfo.packageName, "android")) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            browserIntent.setPackage(mInfo.activityInfo.applicationInfo.packageName);
                            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            _context.startActivity(browserIntent);
                            return true;
                        } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            _context.startActivity(intent);
                            return true;
                        }
                    }
                    pkgs = null;
                } else {
                    SpManager.setBoolean(_context, Key.NAVER_BROWSER_RUN, false);
                    SpManager.setBoolean(_context, Key.CHROME_BROWSER_RUN, false);
                    SpManager.setBoolean(_context, Key.SAMSUNG_BROWSER_RUN, false);
                    SpManager.setBoolean(_context, Key.DAUM_BROWSER_RUN, false);

                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
                    PackageManager pm = _context.getPackageManager();
                    final ResolveInfo mInfo = pm.resolveActivity(i, 0);
                    if (mInfo != null && mInfo.activityInfo != null && mInfo.activityInfo.applicationInfo != null && !TextUtils.equals(mInfo.activityInfo.applicationInfo.packageName, "android")) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        browserIntent.setPackage(mInfo.activityInfo.applicationInfo.packageName);
                        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        _context.startActivity(browserIntent);
                        return true;
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        _context.startActivity(intent);
                        return true;
                    }
                }
            } else
                pkgs = new ArrayList<String>(Arrays.asList(orderData.split("\\|\\|")));

            if (TextUtils.isEmpty(default_pkg) && pkgs != null) {
                for (String pkgName : pkgs) {
                    if (isPackageInstalled(pkgName, _context)) {
                        default_pkg = pkgName;
                        break;
                    }
                }
            }

            LogPrint.d("getBrowserPackageName :: " + default_pkg);

            if (TextUtils.isEmpty(default_pkg) || default_pkg.equalsIgnoreCase("android")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                _context.startActivity(intent);
            } else {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    browserIntent.setPackage(default_pkg);
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(browserIntent);
                    return true;
                } catch (Exception e) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(intent);
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    public static String getDateTime() {
        String date = "";
        try {
            SimpleDateFormat formater = new SimpleDateFormat(Key.DATE_COMPARE_FORMAT_TIME);
            date = formater.format(new Date());
        } catch (Exception e) {
            LogPrint.e("getDateTime()", e);
        }

        return date;
    }

    public static final boolean isOverDays(final String pStartDate, final int pTargetHours) {
        try {
            if (!TextUtils.isEmpty(pStartDate)) {
                SimpleDateFormat dataFormat = new SimpleDateFormat(Key.DATE_COMPARE_FORMAT_DAY, Locale.KOREA);
                Date startDate = dataFormat.parse(pStartDate);
                Date endDate = new Date(); // dataFormat.parse("2015.02.05");
                // 시간 차이 계산 - milisec
                long duration = endDate.getTime() - startDate.getTime();
                // 경과 시간을 시간 단위로 환산
                long elapsedTime = (duration / (1000 * 60 * 60 * 24));
                // 비교
                return elapsedTime >= pTargetHours;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getNavigationBarHeight(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            ((Activity) context).getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

}