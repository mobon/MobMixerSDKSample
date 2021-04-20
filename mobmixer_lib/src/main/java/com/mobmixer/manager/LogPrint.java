package com.mobmixer.manager;

import android.content.Context;
import android.util.Log;

// Added by 문승현 (2015-04-03) : 로그 추가 및 변경
// 매체에서 이슈 발생 시, isDebugError 로그를 true로 설정하고 테스트를 진행하면 error type의 로그는 표시가 된다. (exception 로그 등)
// 추가로 질의값 버전의 MainCallbackActivity 의 이슈에 대해서도 디버깅 가능하도록 로그 추가 예정
public class LogPrint {
	private static final String TAG = "MobonSDK";
	private static boolean isDebugPrint = false;
	private static boolean isDebugPrintPrivate = false;
	// [START] Added by 문승현 (2015-04-02) : 매체 이슈시 확인할 수 있도록 디버깅 가능한 로그만 표시
	private static boolean isDebugError = false;

	public static void debug(String logStr) {
		if (isDebugError)
			Log.d(TAG, "######## > " + logStr);

		if (isDebugPrint && isDebugPrintPrivate)
			Log.d(TAG, "######## > " + logStr);
	}

	// [END] Added by 문승현 (2015-04-02) : 매체 이슈시 확인할 수 있도록 디버깅 가능한 로그만 표시

	public static void d(String logStr) {
		// Modified by 문승현 (2015-04-02): isDebugPrintPrivate flag 추가
		if (isDebugPrint && isDebugPrintPrivate) {
			Log.d(TAG, "######## > " + logStr);
		}
	}

	public static void d(int logStr) {
		// Added by 문승현 (2015-04-03): 로그 flag 추가
		if (isDebugPrint && isDebugPrintPrivate)
			Log.d(TAG, "######## > " + logStr);
	}

	public static void line() {
		// Added by 문승현 (2015-04-03): 로그 flag 추가
		if (isDebugPrint && isDebugPrintPrivate)
			Log.d(TAG, "######## ----------------------------------------------");
	}

	public static void v(String logName) {
		if (isDebugPrint)
			Log.w(TAG, "### > " + logName + " : " + "");
	}

	public static void d(String logName, double logValue) {
		d(logName, String.valueOf(logValue));
	}

	public static void d(String logName, boolean logValue) {
		d(logName, String.valueOf(logValue));
	}

	public static void d(String logName, float logValue) {
		d(logName, String.valueOf(logValue));
	}

	public static void d(String logName, int logValue) {
		d(logName, String.valueOf(logValue));
	}

	public static void d(int logName, int logValue) {
		d("######## > " + logName, String.valueOf(logValue));
	}

	public static void d(String logName, String logValue) {
		// Added by 문승현 (2015-04-03): 로그 flag 추가
		if (isDebugPrint && isDebugPrintPrivate)
			Log.d(TAG, "######## > " + logName + " : " + logValue);
	}

	public static void e(String logStr) {
		// Modified by 문승현 (2015-04-03): 매체에서 콜백 적용 시 디버깅 가능하도록 flag 변경
		if (/* isDebugPrint */isDebugError || isDebugPrintPrivate) {
			Log.e(TAG, "######## ---------------------------------------------------------------------------------------------");
			Log.e(TAG, "### " + logStr);
			Log.e(TAG, "######## ---------------------------------------------------------------------------------------------");
		}
	}

	// Added by 김현준 (2015-02-09): 로그 함수 타입 추가!
	public static void e(Throwable t) {
		// Modified by 문승현 (2015-04-03): 매체에서 콜백 적용 시 디버깅 가능하도록 flag 변경
		if (/* isDebugPrint */isDebugError || isDebugPrintPrivate) {
			Log.e(TAG, "### ---------------------------------------------------------------------------------------------");
			Log.e(TAG, "### Exception!", t);
			Log.e(TAG, "### ---------------------------------------------------------------------------------------------");
		}
	}

	// Added by 김현준 (2015-02-09): 로그 함수 타입 추가!
	public static void e(String logStr, Throwable t) {
		// Modified by 문승현 (2015-04-03): 매체에서 콜백 적용 시 디버깅 가능하도록 flag 변경
		if (/* isDebugPrint */isDebugError || isDebugPrintPrivate) {
			Log.e(TAG, "### ---------------------------------------------------------------------------------------------");
			Log.e(TAG, "### " + logStr, t);
			Log.e(TAG, "### ---------------------------------------------------------------------------------------------");
		}
	}

	// Added by 김현준 (2015-02-09): 로그 함수 타입 추가!
	public static void e(String logName, String logStr) {
		// Modified by 문승현 (2015-04-03): 매체에서 콜백 적용 시 디버깅 가능하도록 flag 변경
		if (/* isDebugPrint */isDebugError || isDebugPrintPrivate) {
			Log.e(TAG, "### ---------------------------------------------------------------------------------------------");
			Log.e(TAG, "### > " + logName + " : " + logStr);
			Log.e(TAG, "### ---------------------------------------------------------------------------------------------");
		}
	}

	public static void callClass(Context context) {
		if (isDebugPrint)
			Log.d("jbon", "[Call Class] " + context.getClass().getName());
	}

	public static void callMethod(String methodName) {
		if (isDebugPrint)
			Log.d("jbon", "[Call Method] " + methodName);
	}

	public static void setLogPrint(boolean _is) {
		isDebugPrint = isDebugPrintPrivate = isDebugError = _is;
	}

	public static boolean isLogPrint() {
		return isDebugPrint;
	}

}
