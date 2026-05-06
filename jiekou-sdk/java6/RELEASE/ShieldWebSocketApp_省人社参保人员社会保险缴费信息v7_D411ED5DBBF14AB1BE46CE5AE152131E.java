package com.iflytek.fsp.shield.java.sdk;

import com.alibaba.fastjson.JSONObject;
import com.iflytek.fsp.shield.java.sdk.websocket.BaseApp;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URISyntaxException;
import java.net.URI;

  public class ShieldWebSocketApp_省人社参保人员社会保险缴费信息v7_D411ED5DBBF14AB1BE46CE5AE152131E extends BaseApp {

      public ShieldWebSocketApp_省人社参保人员社会保险缴费信息v7_D411ED5DBBF14AB1BE46CE5AE152131E()  throws URISyntaxException{
          this.appId = "5153a8a103dd4d86a20a173d6465018c";
          this.appSecret = "BDD7F6FC971D8944CA00B92FB00D6C47";
          
          this.gmAppSecret = "9890395C891E445B8C53C07250E076B1C81FD687CB50DFF9D7321439A289A533";
          
          this.host = "10.16.71.133";
          this.stage = "RELEASE";
          this.publicKey = "305C300D06092A864886F70D0101010500034B003048024100A8F1A6EB97CE8AA69524922CC56156A1F2348A8E034FB0209A5DF9D97C56EC5CEBC8AAAF4B3E3666036F3469E695AAC374CD95AE20BF1B17C07EEF2331D113DD0203010001";
          
          this.gmPublicKey = "3059301306072A8648CE3D020106082A811CCF5501822D0342000491D48E75FE310EFD9A3B1F5909E9C6837B080EAD34265FF330FE3974926D814639BF827FBBDD41A691D9977EA7E7AC21561C44FBB410A58C86BF5ED766CF3D34";
          
          
          this.gmPrivateKey = "308193020100301306072A8648CE3D020106082A811CCF5501822D047930770201010420DE255CB0F5D92741EFC392E498763065FC40236E274C543E2F50FFAA702DBF4AA00A06082A811CCF5501822DA1440342000491D48E75FE310EFD9A3B1F5909E9C6837B080EAD34265FF330FE3974926D814639BF827FBBDD41A691D9977EA7E7AC21561C44FBB410A58C86BF5ED766CF3D34";
          
          this.equipmentNo = "XXX";
          this.signStrategyUrl = "/getSignStrategy";
          this.tokenUrl = "/getTokenUrl";
          this.webSocketPort=4999;
          this.icloudlockEnabled = false;//关闭云锁验证
      }
      private static ShieldWebSocketApp_省人社参保人员社会保险缴费信息v7_D411ED5DBBF14AB1BE46CE5AE152131E singleton;

      public static ShieldWebSocketApp_省人社参保人员社会保险缴费信息v7_D411ED5DBBF14AB1BE46CE5AE152131E getInstance() throws URISyntaxException {
            if (singleton == null) {
                synchronized (ShieldWebSocketApp_省人社参保人员社会保险缴费信息v7_D411ED5DBBF14AB1BE46CE5AE152131E.class) {
                    if (singleton == null) {
                        singleton = new ShieldWebSocketApp_省人社参保人员社会保险缴费信息v7_D411ED5DBBF14AB1BE46CE5AE152131E();
                    }
                }
            }
            return singleton;
      }


}