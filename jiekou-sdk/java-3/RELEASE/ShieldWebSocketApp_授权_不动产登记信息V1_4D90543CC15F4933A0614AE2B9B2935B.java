package com.iflytek.fsp.shield.java.sdk;

import com.alibaba.fastjson.JSONObject;
import com.iflytek.fsp.shield.java.sdk.websocket.BaseApp;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URISyntaxException;
import java.net.URI;

  public class ShieldWebSocketApp_授权_不动产登记信息V1_4D90543CC15F4933A0614AE2B9B2935B extends BaseApp {

      public ShieldWebSocketApp_授权_不动产登记信息V1_4D90543CC15F4933A0614AE2B9B2935B()  throws URISyntaxException{
          this.appId = "90da9695616a408c85a31edb51a0da7b";
          this.appSecret = "07834BC97F21886E59A3BE86DEAEBDB9";
          
          this.gmAppSecret = "FA25650751E35FF4973E40FC0861752D487CE81E751756FE74AE7185FA8769C8";
          
          this.host = "10.16.71.133";
          this.stage = "RELEASE";
          this.publicKey = "305C300D06092A864886F70D0101010500034B003048024100C310181626F7F1CD2591B92AF9702215E93EA4FA12B8EA4E4D4F92BF5F8CD0EEECFB1C3A27C8CE045B7604059E501A67B099CF6629F817C3F92E6A3A83D9170F0203010001";
          
          this.gmPublicKey = "3059301306072A8648CE3D020106082A811CCF5501822D0342000434A8EB34CE9CB2990A9264F742C45F3034833E6EFA3C8F8905272516A79C3121BBEA76D3D87F7D27EB61331388D78E2F721347EFCA9C8B826F2907F5C49836C5";
          
          
          this.gmPrivateKey = "308193020100301306072A8648CE3D020106082A811CCF5501822D0479307702010104205CA28814495D9912DE53F1C29169B82A9E09858628F1878312171FB722577ECBA00A06082A811CCF5501822DA1440342000434A8EB34CE9CB2990A9264F742C45F3034833E6EFA3C8F8905272516A79C3121BBEA76D3D87F7D27EB61331388D78E2F721347EFCA9C8B826F2907F5C49836C5";
          
          this.equipmentNo = "XXX";
          this.signStrategyUrl = "/getSignStrategy";
          this.tokenUrl = "/getTokenUrl";
          this.webSocketPort=4999;
          this.icloudlockEnabled = false;//关闭云锁验证
      }
      private static ShieldWebSocketApp_授权_不动产登记信息V1_4D90543CC15F4933A0614AE2B9B2935B singleton;

      public static ShieldWebSocketApp_授权_不动产登记信息V1_4D90543CC15F4933A0614AE2B9B2935B getInstance() throws URISyntaxException {
            if (singleton == null) {
                synchronized (ShieldWebSocketApp_授权_不动产登记信息V1_4D90543CC15F4933A0614AE2B9B2935B.class) {
                    if (singleton == null) {
                        singleton = new ShieldWebSocketApp_授权_不动产登记信息V1_4D90543CC15F4933A0614AE2B9B2935B();
                    }
                }
            }
            return singleton;
      }


}