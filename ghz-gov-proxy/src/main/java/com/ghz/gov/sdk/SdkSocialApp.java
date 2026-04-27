package com.ghz.gov.sdk;

import com.iflytek.fsp.shield.java.sdk.constant.HttpConstant;
import com.iflytek.fsp.shield.java.sdk.constant.SdkConstant;
import com.iflytek.fsp.shield.java.sdk.enums.Method;
import com.iflytek.fsp.shield.java.sdk.enums.ParamPosition;
import com.iflytek.fsp.shield.java.sdk.http.ApiClient;
import com.iflytek.fsp.shield.java.sdk.http.BaseApp;
import com.iflytek.fsp.shield.java.sdk.model.ApiRequest;
import com.iflytek.fsp.shield.java.sdk.model.ApiResponse;

public class SdkSocialApp extends BaseApp {

    public SdkSocialApp() {
        this.apiClient = new ApiClient();
        this.apiClient.init();
        this.appId = "30fd77a886ae496e8b6a54ac68115fa5";
        this.appSecret = "5C462B2CE2C4FE48E8211B2BC567A60E";
        this.gmAppSecret = "FC9250F9C3C0CEB58FE228FACEF11A26752990BB2580D378E244CB59B75737BF";
        this.host = "59.207.50.51";
        this.contextPath = "";
        this.httpPort = 4989;
        this.httpsPort = 443;
        this.stage = "RELEASE";
        this.equipmentNo = "XXX";
        this.signStrategyUrl = "/getSignStrategy";
        this.tokenUrl = "/getTokenUrl";
        this.publicKey = "305C300D06092A864886F70D0101010500034B0030480241009A58C2239600B738ECD4CFEA897CD58F73777B0C6838421EFEF7C78936033E78D34C945E39B5529E3FB19706DFBB21A20D2210D721C06363A5881EB24767D23F0203010001";
        this.gmPublicKey = "3059301306072A8648CE3D020106082A811CCF5501822D03420004F30F80E749FC9711F62AE6BFFA2BAB86B7F7292704A74F825E71ADF87B9CA6168371265938A708A62FBB2480AC045D8D1509BD65E7F8140D384E63ABAF66A87B";
        this.gmPrivateKey = "308193020100301306072A8648CE3D020106082A811CCF5501822D047930770201010420BB60001E3F15B6EDC77443B55E3953A19D5B0D210693CA38D4142503C87F2BD5A00A06082A811CCF5501822DA14403420004F30F80E749FC9711F62AE6BFFA2BAB86B7F7292704A74F825E71ADF87B9CA6168371265938A708A62FBB2480AC045D8D1509BD65E7F8140D384E63ABAF66A87B";
        this.icloudlockEnabled = false;
    }

    public ApiResponse query(byte[] body, String access_token) {
        ApiRequest apiRequest = new ApiRequest(HttpConstant.SCHEME_HTTP, Method.POST,
                "/api/7131C082B84540C4A4DDE28BE6D36CCA",
                SdkConstant.AUTH_TYPE_ENCRYPT, "7ce3760681894db8a545e9428630e016");
        apiRequest.setBody(body);
        apiRequest.addParam("access_token", access_token, ParamPosition.QUERY, true);
        return syncInvoke(apiRequest);
    }
}
