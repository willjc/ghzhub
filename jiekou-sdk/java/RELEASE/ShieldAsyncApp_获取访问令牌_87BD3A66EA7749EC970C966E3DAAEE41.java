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

public class ShieldAsyncApp_获取访问令牌_87BD3A66EA7749EC970C966E3DAAEE41 extends BaseApp {

    public ShieldAsyncApp_获取访问令牌_87BD3A66EA7749EC970C966E3DAAEE41() {
        this.apiClient = new ApiClient();
        this.apiClient.init();
        // 管理平台应用查看处获取并修改
        this.appId = "b2619ca04a2446dea85f741328ddc238";
        // 管理平台应用查看处获取并修改
        this.appSecret = "763F673675DE32D1DFA9D2F2F11A2916";
        
        // 管理平台应用查看处获取并修改
        this.gmAppSecret = "475AB917477753D8BCDB20BA0D9449BC259B23D48D2135C30BA172F99ADBA8B8";
        
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
        this.publicKey = "305C300D06092A864886F70D0101010500034B0030480241009E5E39F50BE76C7FC6133C9A4040204C48BEE238C458FE261BE0591D860CCF609E5830E3A9F596E76942893C23A3AAFC13F96E3E1D1B8DCEC4DFAA67CEE038C50203010001";
        
        // 管理平台应用查看处获取并修改
        this.gmPublicKey = "3059301306072A8648CE3D020106082A811CCF5501822D034200043F1DAABE376691E30650D329AB7C339E5BE2B534758DA4A7535C6BD6685FFA910F1668BC15C724A06723F8E07BD9840EA77FF36A35BCE2D63EB9A08F552E8E6E";
        
        
        // 管理平台应用查看处获取并修改
        this.gmPrivateKey = "308193020100301306072A8648CE3D020106082A811CCF5501822D047930770201010420FF7A56EB3B8F8266416C20EB349BF951938DE5BFFE9324292E4268A4D8FF3E8EA00A06082A811CCF5501822DA144034200043F1DAABE376691E30650D329AB7C339E5BE2B534758DA4A7535C6BD6685FFA910F1668BC15C724A06723F8E07BD9840EA77FF36A35BCE2D63EB9A08F552E8E6E";
        
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
    * Version:202604210957446604
    */
    public void 获取访问令牌(String grant_type, String client_id, String client_secret, ApiCallback apiCallback) {
        ApiRequest apiRequest = new ApiRequest(HttpConstant.SCHEME_HTTP, Method.POST, "/api/59E003A8E411471FA0710AF9760951ED", SdkConstant.AUTH_TYPE_ENCRYPT, "7ce3760681894db8a545e9428630e016");
        
        
        apiRequest.addParam("grant_type", grant_type, ParamPosition.FORM, false);
        
        apiRequest.addParam("client_id", client_id, ParamPosition.FORM, false);
        
        apiRequest.addParam("client_secret", client_secret, ParamPosition.FORM, false);
        
        asyncInvoke(apiRequest, apiCallback);
    }
   
}