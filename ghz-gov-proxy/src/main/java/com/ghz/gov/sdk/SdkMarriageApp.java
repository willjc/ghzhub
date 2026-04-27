package com.ghz.gov.sdk;

import com.iflytek.fsp.shield.java.sdk.constant.HttpConstant;
import com.iflytek.fsp.shield.java.sdk.constant.SdkConstant;
import com.iflytek.fsp.shield.java.sdk.enums.Method;
import com.iflytek.fsp.shield.java.sdk.enums.ParamPosition;
import com.iflytek.fsp.shield.java.sdk.http.ApiClient;
import com.iflytek.fsp.shield.java.sdk.http.BaseApp;
import com.iflytek.fsp.shield.java.sdk.model.ApiRequest;
import com.iflytek.fsp.shield.java.sdk.model.ApiResponse;

public class SdkMarriageApp extends BaseApp {

    public SdkMarriageApp() {
        this.apiClient = new ApiClient();
        this.apiClient.init();
        this.appId = "1284f00407ef40ba8a4586756269b973";
        this.appSecret = "AEF5838F7BF54915EEE2FFD4939C4330";
        this.gmAppSecret = "479FE03BEF6744F9BE5739D10ABF110A21B7F052D0E0A1C2316F46FD70738A87";
        this.host = "59.207.50.51";
        this.contextPath = "";
        this.httpPort = 4989;
        this.httpsPort = 443;
        this.stage = "RELEASE";
        this.equipmentNo = "XXX";
        this.signStrategyUrl = "/getSignStrategy";
        this.tokenUrl = "/getTokenUrl";
        this.publicKey = "305C300D06092A864886F70D0101010500034B00304802410099A2219E918CCD46BF8A26D8E2B33F4E2AA88679AB717CAE6068BC096118E0D22BFC2C1733D017BD515FE2340DADFD0ACCE5DC6A76B688466624F55D5EAE3D830203010001";
        this.gmPublicKey = "3059301306072A8648CE3D020106082A811CCF5501822D03420004A93560403CA41DB1F27E0372F66E99A8D121A8D5A576802E5454DC85DD77C7B09D1DC4BD81360099884C2ECA50FDA7E34C563A843281FC9F473FDC0DEF9134F4";
        this.gmPrivateKey = "308193020100301306072A8648CE3D020106082A811CCF5501822D047930770201010420A96630FEF4C4B14C6186FC949E9FFC130E5F3EDF50956F3F12D4B3184BEF109AA00A06082A811CCF5501822DA14403420004A93560403CA41DB1F27E0372F66E99A8D121A8D5A576802E5454DC85DD77C7B09D1DC4BD81360099884C2ECA50FDA7E34C563A843281FC9F473FDC0DEF9134F4";
        this.icloudlockEnabled = false;
    }

    public ApiResponse query(byte[] body, String access_token) {
        ApiRequest apiRequest = new ApiRequest(HttpConstant.SCHEME_HTTP, Method.POST,
                "/api/51A4C0A42230463BAAB135CD4CB683F1",
                SdkConstant.AUTH_TYPE_ENCRYPT, "7ce3760681894db8a545e9428630e016");
        apiRequest.setBody(body);
        apiRequest.addParam("access_token", access_token, ParamPosition.QUERY, true);
        return syncInvoke(apiRequest);
    }
}
