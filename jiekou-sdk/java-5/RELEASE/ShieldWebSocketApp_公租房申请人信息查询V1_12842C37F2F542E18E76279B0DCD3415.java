package com.iflytek.fsp.shield.java.sdk;

import com.alibaba.fastjson.JSONObject;
import com.iflytek.fsp.shield.java.sdk.websocket.BaseApp;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URISyntaxException;
import java.net.URI;

  public class ShieldWebSocketApp_公租房申请人信息查询V1_12842C37F2F542E18E76279B0DCD3415 extends BaseApp {

      public ShieldWebSocketApp_公租房申请人信息查询V1_12842C37F2F542E18E76279B0DCD3415()  throws URISyntaxException{
          this.appId = "1edf6f91befa4d87b92ee172bec75a0f";
          this.appSecret = "1A829B00B8BC996FA3C33F679C61BF28";
          
          this.gmAppSecret = "A6BE8C2F1CD5AED659DAEA921905F624EF8F692BDF7205DDF2D3FE64A04DD100";
          
          this.host = "10.16.71.133";
          this.stage = "RELEASE";
          this.publicKey = "305C300D06092A864886F70D0101010500034B00304802410084B89CBCB0FE645E060FA79030D04F509EFB1EEF52ECFA2A7A410465C21837BE0FA5427273866C600CFEE5D9FC85B4E5B608FAB8953F151B68EB5100A232A52B0203010001";
          
          this.gmPublicKey = "3059301306072A8648CE3D020106082A811CCF5501822D03420004B870ED844696EB5298DA827CABE2203973A1CECEF902EDB5F73E821C0880478F613785CE8AC81580489D929C6A11FAAC0B9D9FDD9D962724BA09D45AA6E6488B";
          
          
          this.gmPrivateKey = "308193020100301306072A8648CE3D020106082A811CCF5501822D047930770201010420FFBFB45885C414726F5EBBB3D8B9A9B09437BE5C95EB8B5F6611F0BCF3FE39D0A00A06082A811CCF5501822DA14403420004B870ED844696EB5298DA827CABE2203973A1CECEF902EDB5F73E821C0880478F613785CE8AC81580489D929C6A11FAAC0B9D9FDD9D962724BA09D45AA6E6488B";
          
          this.equipmentNo = "XXX";
          this.signStrategyUrl = "/getSignStrategy";
          this.tokenUrl = "/getTokenUrl";
          this.webSocketPort=4999;
          this.icloudlockEnabled = false;//关闭云锁验证
      }
      private static ShieldWebSocketApp_公租房申请人信息查询V1_12842C37F2F542E18E76279B0DCD3415 singleton;

      public static ShieldWebSocketApp_公租房申请人信息查询V1_12842C37F2F542E18E76279B0DCD3415 getInstance() throws URISyntaxException {
            if (singleton == null) {
                synchronized (ShieldWebSocketApp_公租房申请人信息查询V1_12842C37F2F542E18E76279B0DCD3415.class) {
                    if (singleton == null) {
                        singleton = new ShieldWebSocketApp_公租房申请人信息查询V1_12842C37F2F542E18E76279B0DCD3415();
                    }
                }
            }
            return singleton;
      }


}