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

public class ShieldAsyncApp_省人社参保人员社会保险缴费信息v7_D411ED5DBBF14AB1BE46CE5AE152131E extends BaseApp {

    public ShieldAsyncApp_省人社参保人员社会保险缴费信息v7_D411ED5DBBF14AB1BE46CE5AE152131E() {
        this.apiClient = new ApiClient();
        this.apiClient.init();
        // 管理平台应用查看处获取并修改
        this.appId = "5153a8a103dd4d86a20a173d6465018c";
        // 管理平台应用查看处获取并修改
        this.appSecret = "BDD7F6FC971D8944CA00B92FB00D6C47";
        
        // 管理平台应用查看处获取并修改
        this.gmAppSecret = "9890395C891E445B8C53C07250E076B1C81FD687CB50DFF9D7321439A289A533";
        
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
        this.publicKey = "305C300D06092A864886F70D0101010500034B003048024100A8F1A6EB97CE8AA69524922CC56156A1F2348A8E034FB0209A5DF9D97C56EC5CEBC8AAAF4B3E3666036F3469E695AAC374CD95AE20BF1B17C07EEF2331D113DD0203010001";
        
        // 管理平台应用查看处获取并修改
        this.gmPublicKey = "3059301306072A8648CE3D020106082A811CCF5501822D0342000491D48E75FE310EFD9A3B1F5909E9C6837B080EAD34265FF330FE3974926D814639BF827FBBDD41A691D9977EA7E7AC21561C44FBB410A58C86BF5ED766CF3D34";
        
        
        // 管理平台应用查看处获取并修改
        this.gmPrivateKey = "308193020100301306072A8648CE3D020106082A811CCF5501822D047930770201010420DE255CB0F5D92741EFC392E498763065FC40236E274C543E2F50FFAA702DBF4AA00A06082A811CCF5501822DA1440342000491D48E75FE310EFD9A3B1F5909E9C6837B080EAD34265FF330FE3974926D814639BF827FBBDD41A691D9977EA7E7AC21561C44FBB410A58C86BF5ED766CF3D34";
        
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
    * Version:202604291728179839
    */
    public void 省人社参保人员社会保险缴费信息v7(byte[] body, String access_token, ApiCallback apiCallback) {
        ApiRequest apiRequest = new ApiRequest(HttpConstant.SCHEME_HTTP, Method.POST, "/api/FEED9E8FDFB54A13988A355EF6D0B6DB", SdkConstant.AUTH_TYPE_ENCRYPT, "7ce3760681894db8a545e9428630e016");
        apiRequest.setBody(body);
        
        apiRequest.addParam("access_token", access_token, ParamPosition.QUERY, true);
        
        asyncInvoke(apiRequest, apiCallback);
    }
   
}