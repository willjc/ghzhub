package com.ghz.gov.sdk;

import com.iflytek.fsp.shield.java.sdk.constant.HttpConstant;
import com.iflytek.fsp.shield.java.sdk.constant.SdkConstant;
import com.iflytek.fsp.shield.java.sdk.enums.Method;
import com.iflytek.fsp.shield.java.sdk.enums.ParamPosition;
import com.iflytek.fsp.shield.java.sdk.http.ApiClient;
import com.iflytek.fsp.shield.java.sdk.http.BaseApp;
import com.iflytek.fsp.shield.java.sdk.model.ApiRequest;
import com.iflytek.fsp.shield.java.sdk.model.ApiResponse;

public class SdkTokenApp extends BaseApp {

    public SdkTokenApp() {
        this.apiClient = new ApiClient();
        this.apiClient.init();
        this.appId = "b2619ca04a2446dea85f741328ddc238";
        this.appSecret = "763F673675DE32D1DFA9D2F2F11A2916";
        this.gmAppSecret = "475AB917477753D8BCDB20BA0D9449BC259B23D48D2135C30BA172F99ADBA8B8";
        this.host = "59.207.50.51";
        this.contextPath = "";
        this.httpPort = 4989;
        this.httpsPort = 443;
        this.stage = "RELEASE";
        this.equipmentNo = "XXX";
        this.signStrategyUrl = "/getSignStrategy";
        this.tokenUrl = "/getTokenUrl";
        this.publicKey = "305C300D06092A864886F70D0101010500034B0030480241009E5E39F50BE76C7FC6133C9A4040204C48BEE238C458FE261BE0591D860CCF609E5830E3A9F596E76942893C23A3AAFC13F96E3E1D1B8DCEC4DFAA67CEE038C50203010001";
        this.gmPublicKey = "3059301306072A8648CE3D020106082A811CCF5501822D034200043F1DAABE376691E30650D329AB7C339E5BE2B534758DA4A7535C6BD6685FFA910F1668BC15C724A06723F8E07BD9840EA77FF36A35BCE2D63EB9A08F552E8E6E";
        this.gmPrivateKey = "308193020100301306072A8648CE3D020106082A811CCF5501822D047930770201010420FF7A56EB3B8F8266416C20EB349BF951938DE5BFFE9324292E4268A4D8FF3E8EA00A06082A811CCF5501822DA144034200043F1DAABE376691E30650D329AB7C339E5BE2B534758DA4A7535C6BD6685FFA910F1668BC15C724A06723F8E07BD9840EA77FF36A35BCE2D63EB9A08F552E8E6E";
        this.icloudlockEnabled = false;
    }

    public ApiResponse getToken(String grant_type, String client_id, String client_secret) {
        ApiRequest apiRequest = new ApiRequest(HttpConstant.SCHEME_HTTP, Method.POST,
                "/api/59E003A8E411471FA0710AF9760951ED",
                SdkConstant.AUTH_TYPE_ENCRYPT, "7ce3760681894db8a545e9428630e016");
        apiRequest.addParam("grant_type", grant_type, ParamPosition.FORM, false);
        apiRequest.addParam("client_id", client_id, ParamPosition.FORM, false);
        apiRequest.addParam("client_secret", client_secret, ParamPosition.FORM, false);
        return syncInvoke(apiRequest);
    }
}
