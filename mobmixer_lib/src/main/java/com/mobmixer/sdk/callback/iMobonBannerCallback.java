package com.mobmixer.sdk.callback;

/**
 * - 모비온 배너 콜백
 */
public interface iMobonBannerCallback {

    /**
     * 광고 데이터 로딩 성공에 대한 콜백 인터페이스
     */
    void onLoadedAdInfo(final boolean result, final String errorStr);

    void onAdClicked();

}
