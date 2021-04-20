package com.mobmixer.sdk;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.mobmixer.manager.SpManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;


public class CommonUtils {


    public static void getAdId(final Context context) {

        AsyncTask.execute(new Runnable() {
            public void run() {
                AdvertisingIdClient.Info adInfo;
                adInfo = null;
                try {
                    if (context != null) {
                        adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                        if (adInfo != null && !adInfo.isLimitAdTrackingEnabled()) {
                            SpManager.setString(context, Key.ADID, adInfo.getId());
                        } else {
                            String uniqueId = UUID.randomUUID().toString();
                            if (uniqueId != null)
                                SpManager.setString(context, Key.ADID, uniqueId);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (adInfo == null) {
                        String uniqueId = UUID.randomUUID().toString();
                        if (uniqueId != null)
                            SpManager.setString(context, Key.ADID, uniqueId);
                    }
                }
            }
        });
    }

    public static String getAppVersion(Context _context) {
        try {
            PackageInfo packageInfo = _context.getPackageManager().getPackageInfo(_context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static String getAdid(Context _context) {
        return SpManager.getString(_context, Key.ADID);
    }


    public static String getParameter(String url, String _param) {
        Uri uri = Uri.parse(url);
        String paramValue = uri.getQueryParameter(_param);
        return paramValue;
    }


    public static String getHtml(String _url) {
        String Html = "";

        HttpURLConnection con = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            URL url = new URL(_url);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            isr = new InputStreamReader(con.getInputStream());
            br = new BufferedReader(isr);

            String str = null;
            while ((str = br.readLine()) != null) {
                Html += str + "\n";
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception e) {
                }
            }

            if (isr != null) {
                try {
                    isr.close();
                } catch (Exception e) {
                }
            }

            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                }
            }
        }
        return Html;
    }

    public static String getTelecom(Context _context) {
        try {
            TelephonyManager manager = (TelephonyManager) _context.getSystemService(Context.TELEPHONY_SERVICE);
            return manager.getNetworkOperatorName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String getManPlusParam(Context context) {
        String params;

        params = "&adid=" + SpManager.getString(context, Key.ADID);
        params += "&osIndex=3";
        params += "&osVer=" + Build.VERSION.RELEASE;
        params += "&appId=" + context.getPackageName();
        params += "&appName=" + getApplicationName(context);
        return params;
    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

}
