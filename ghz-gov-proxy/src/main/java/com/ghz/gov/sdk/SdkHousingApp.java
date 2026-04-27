package com.ghz.gov.sdk;

import com.iflytek.fsp.shield.java.sdk.constant.HttpConstant;
import com.iflytek.fsp.shield.java.sdk.constant.SdkConstant;
import com.iflytek.fsp.shield.java.sdk.enums.Method;
import com.iflytek.fsp.shield.java.sdk.enums.ParamPosition;
import com.iflytek.fsp.shield.java.sdk.http.ApiClient;
import com.iflytek.fsp.shield.java.sdk.http.BaseApp;
import com.iflytek.fsp.shield.java.sdk.model.ApiRequest;
import com.iflytek.fsp.shield.java.sdk.model.ApiResponse;

public class SdkHousingApp extends BaseApp {

    public SdkHousingApp() {
        this.apiClient = new ApiClient();
        this.apiClient.init();
        this.appId = "1edf6f91befa4d87b92ee172bec75a0f";
        this.appSecret = "1A829B00B8BC996FA3C33F679C61BF28";
        this.gmAppSecret = "A6BE8C2F1CD5AED659DAEA921905F624EF8F692BDF7205DDF2D3FE64A04DD100";
        this.host = "59.207.50.51";
        this.contextPath = "";
        this.httpPort = 4989;
        this.httpsPort = 443;
        this.stage = "RELEASE";
        this.equipmentNo = "XXX";
        this.signStrategyUrl = "/getSignStrategy";
        this.tokenUrl = "/getTokenUrl";
        this.publicKey = "305C300D06092A864886F70D0101010500034B00304802410084B89CBCB0FE645E060FA79030D04F509EFB1EEF52ECFA2A7A410465C21837BE0FA5427273866C600CFEE5D9FC85B4E5B608FAB8953F151B68EB5100A232A52B0203010001";
        this.gmPublicKey = "3059301306072A8648CE3D020106082A811CCF5501822D03420004B870ED844696EB5298DA827CABE2203973A1CECEF902EDB5F73E821C0880478F613785CE8AC81580489D929C6A11FAAC0B9D9FDD9D962724BA09D45AA6E6488B";
        this.gmPrivateKey = "308193020100301306072A8648CE3D020106082A811CCF5501822D047930770201010420FFBFB45885C414726F5EBBB3D8B9A9B09437BE5C95EB8B5F6611F0BCF3FE39D0A00A06082A811CCF5501822DA14403420004B870ED844696EB5298DA827CABE2203973A1CECEF902EDB5F73E821C0880478F613785CE8AC81580489D929C6A11FAAC0B9D9FDD9D962724BA09D45AA6E6488B";
        this.icloudlockEnabled = false;
    }

    public ApiResponse query(byte[] body, String access_token) {
        ApiRequest apiRequest = new ApiRequest(HttpConstant.SCHEME_HTTP, Method.POST,
                "/api/6D3837073E5448B8A8B74213531676EE",
                SdkConstant.AUTH_TYPE_ENCRYPT, "7ce3760681894db8a545e9428630e016");
        apiRequest.setBody(body);
        apiRequest.addParam("access_token", access_token, ParamPosition.QUERY, true);
        return syncInvoke(apiRequest);
    }
}
