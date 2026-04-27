package com.ghz.gov.sdk;

import com.iflytek.fsp.shield.java.sdk.constant.HttpConstant;
import com.iflytek.fsp.shield.java.sdk.constant.SdkConstant;
import com.iflytek.fsp.shield.java.sdk.enums.Method;
import com.iflytek.fsp.shield.java.sdk.enums.ParamPosition;
import com.iflytek.fsp.shield.java.sdk.http.ApiClient;
import com.iflytek.fsp.shield.java.sdk.http.BaseApp;
import com.iflytek.fsp.shield.java.sdk.model.ApiRequest;
import com.iflytek.fsp.shield.java.sdk.model.ApiResponse;

public class SdkEstateApp extends BaseApp {

    public SdkEstateApp() {
        this.apiClient = new ApiClient();
        this.apiClient.init();
        this.appId = "90da9695616a408c85a31edb51a0da7b";
        this.appSecret = "07834BC97F21886E59A3BE86DEAEBDB9";
        this.gmAppSecret = "FA25650751E35FF4973E40FC0861752D487CE81E751756FE74AE7185FA8769C8";
        this.host = "59.207.50.51";
        this.contextPath = "";
        this.httpPort = 4989;
        this.httpsPort = 443;
        this.stage = "RELEASE";
        this.equipmentNo = "XXX";
        this.signStrategyUrl = "/getSignStrategy";
        this.tokenUrl = "/getTokenUrl";
        this.publicKey = "305C300D06092A864886F70D0101010500034B003048024100C310181626F7F1CD2591B92AF9702215E93EA4FA12B8EA4E4D4F92BF5F8CD0EEECFB1C3A27C8CE045B7604059E501A67B099CF6629F817C3F92E6A3A83D9170F0203010001";
        this.gmPublicKey = "3059301306072A8648CE3D020106082A811CCF5501822D0342000434A8EB34CE9CB2990A9264F742C45F3034833E6EFA3C8F8905272516A79C3121BBEA76D3D87F7D27EB61331388D78E2F721347EFCA9C8B826F2907F5C49836C5";
        this.gmPrivateKey = "308193020100301306072A8648CE3D020106082A811CCF5501822D0479307702010104205CA28814495D9912DE53F1C29169B82A9E09858628F1878312171FB722577ECBA00A06082A811CCF5501822DA1440342000434A8EB34CE9CB2990A9264F742C45F3034833E6EFA3C8F8905272516A79C3121BBEA76D3D87F7D27EB61331388D78E2F721347EFCA9C8B826F2907F5C49836C5";
        this.icloudlockEnabled = false;
    }

    public ApiResponse query(byte[] body, String access_token) {
        ApiRequest apiRequest = new ApiRequest(HttpConstant.SCHEME_HTTP, Method.POST,
                "/api/050C22B4BE534DB89BF7992D2852FFCF",
                SdkConstant.AUTH_TYPE_ENCRYPT, "7ce3760681894db8a545e9428630e016");
        apiRequest.setBody(body);
        apiRequest.addParam("access_token", access_token, ParamPosition.QUERY, true);
        return syncInvoke(apiRequest);
    }
}
