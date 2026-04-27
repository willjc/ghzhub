package com.iflytek.fsp.shield.java.sdk;

import com.iflytek.fsp.shield.java.sdk.constant.HttpConstant;
import com.iflytek.fsp.shield.java.sdk.constant.SdkConstant;
import com.iflytek.fsp.shield.java.sdk.enums.Method;
import com.iflytek.fsp.shield.java.sdk.enums.ParamPosition;
import com.iflytek.fsp.shield.java.sdk.http.ApiClient;
import com.iflytek.fsp.shield.java.sdk.http.BaseApp;
import com.iflytek.fsp.shield.java.sdk.model.ApiCallback;
import com.iflytek.fsp.shield.java.sdk.model.ApiRequest;
import com.iflytek.fsp.shield.java.sdk.model.MultipartFile;
import java.io.File;

public class ShieldAsyncApp_授权_省民政_婚姻实时信息V2接口_67E20E7BE2B14F8CB716D965D5ECF0FA extends BaseApp {

    public ShieldAsyncApp_授权_省民政_婚姻实时信息V2接口_67E20E7BE2B14F8CB716D965D5ECF0FA() {
        this.apiClient = new ApiClient();
        this.apiClient.init();
        // 管理平台应用查看处获取并修改
        this.appId = "1284f00407ef40ba8a4586756269b973";
        // 管理平台应用查看处获取并修改
        this.appSecret = "AEF5838F7BF54915EEE2FFD4939C4330";
        
        // 管理平台应用查看处获取并修改
        this.gmAppSecret = "479FE03BEF6744F9BE5739D10ABF110A21B7F052D0E0A1C2316F46FD70738A87";
        
        // 核心层ip
        this.host = "10.16.71.133";
        //核心层上下文
        this.contextPath ="";
        
        // 核心层暴露的http端口
        this.httpPort = 4989;
        
        
        // 核心层暴露的https端口
        this.httpsPort = 443;
        
        // sdk生成时选择的环境 RELEASE=线上  TEST=测试 PRE=预生产
        this.stage = "RELEASE";
        // 管理平台应用查看处获取并修改
        this.publicKey = "305C300D06092A864886F70D0101010500034B00304802410099A2219E918CCD46BF8A26D8E2B33F4E2AA88679AB717CAE6068BC096118E0D22BFC2C1733D017BD515FE2340DADFD0ACCE5DC6A76B688466624F55D5EAE3D830203010001";
        
        // 管理平台应用查看处获取并修改
        this.gmPublicKey = "3059301306072A8648CE3D020106082A811CCF5501822D03420004A93560403CA41DB1F27E0372F66E99A8D121A8D5A576802E5454DC85DD77C7B09D1DC4BD81360099884C2ECA50FDA7E34C563A843281FC9F473FDC0DEF9134F4";
        
        
        // 管理平台应用查看处获取并修改
        this.gmPrivateKey = "308193020100301306072A8648CE3D020106082A811CCF5501822D047930770201010420A96630FEF4C4B14C6186FC949E9FFC130E5F3EDF50956F3F12D4B3184BEF109AA00A06082A811CCF5501822DA14403420004A93560403CA41DB1F27E0372F66E99A8D121A8D5A576802E5454DC85DD77C7B09D1DC4BD81360099884C2ECA50FDA7E34C563A843281FC9F473FDC0DEF9134F4";
        
        // 此参数暂时无用
        this.equipmentNo = "XXX";
        // 此参数暂时无用
        this.signStrategyUrl = "/getSignStrategy";
        // 此参数暂时无用
        this.tokenUrl = "/getTokenUrl";
        //关闭云锁验证
        this.icloudlockEnabled = false;
    }


  
  /**
    * Version:202604211005366964
    */
    public void 授权_省民政_婚姻实时信息V2接口(byte[] body, String access_token, ApiCallback apiCallback) {
        ApiRequest apiRequest = new ApiRequest(HttpConstant.SCHEME_HTTP, Method.POST, "/api/51A4C0A42230463BAAB135CD4CB683F1", SdkConstant.AUTH_TYPE_ENCRYPT, "7ce3760681894db8a545e9428630e016");
        apiRequest.setBody(body);
        
        apiRequest.addParam("access_token", access_token, ParamPosition.QUERY, true);
        
        asyncInvoke(apiRequest, apiCallback);
    }
   
}