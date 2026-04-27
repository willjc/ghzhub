package com.iflytek.fsp.shield.java.sdk;

import com.alibaba.fastjson.JSONObject;
import com.iflytek.fsp.shield.java.sdk.websocket.BaseApp;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URISyntaxException;
import java.net.URI;

  public class ShieldWebSocketApp_授权省人社_参保单位社会保险缴费信息V7_548E6780B5974C92B3CE92274AC14375 extends BaseApp {

      public ShieldWebSocketApp_授权省人社_参保单位社会保险缴费信息V7_548E6780B5974C92B3CE92274AC14375()  throws URISyntaxException{
          this.appId = "30fd77a886ae496e8b6a54ac68115fa5";
          this.appSecret = "5C462B2CE2C4FE48E8211B2BC567A60E";
          
          this.gmAppSecret = "FC9250F9C3C0CEB58FE228FACEF11A26752990BB2580D378E244CB59B75737BF";
          
          this.host = "10.16.71.133";
          this.stage = "RELEASE";
          this.publicKey = "305C300D06092A864886F70D0101010500034B0030480241009A58C2239600B738ECD4CFEA897CD58F73777B0C6838421EFEF7C78936033E78D34C945E39B5529E3FB19706DFBB21A20D2210D721C06363A5881EB24767D23F0203010001";
          
          this.gmPublicKey = "3059301306072A8648CE3D020106082A811CCF5501822D03420004F30F80E749FC9711F62AE6BFFA2BAB86B7F7292704A74F825E71ADF87B9CA6168371265938A708A62FBB2480AC045D8D1509BD65E7F8140D384E63ABAF66A87B";
          
          
          this.gmPrivateKey = "308193020100301306072A8648CE3D020106082A811CCF5501822D047930770201010420BB60001E3F15B6EDC77443B55E3953A19D5B0D210693CA38D4142503C87F2BD5A00A06082A811CCF5501822DA14403420004F30F80E749FC9711F62AE6BFFA2BAB86B7F7292704A74F825E71ADF87B9CA6168371265938A708A62FBB2480AC045D8D1509BD65E7F8140D384E63ABAF66A87B";
          
          this.equipmentNo = "XXX";
          this.signStrategyUrl = "/getSignStrategy";
          this.tokenUrl = "/getTokenUrl";
          this.webSocketPort=4999;
          this.icloudlockEnabled = false;//关闭云锁验证
      }
      private static ShieldWebSocketApp_授权省人社_参保单位社会保险缴费信息V7_548E6780B5974C92B3CE92274AC14375 singleton;

      public static ShieldWebSocketApp_授权省人社_参保单位社会保险缴费信息V7_548E6780B5974C92B3CE92274AC14375 getInstance() throws URISyntaxException {
            if (singleton == null) {
                synchronized (ShieldWebSocketApp_授权省人社_参保单位社会保险缴费信息V7_548E6780B5974C92B3CE92274AC14375.class) {
                    if (singleton == null) {
                        singleton = new ShieldWebSocketApp_授权省人社_参保单位社会保险缴费信息V7_548E6780B5974C92B3CE92274AC14375();
                    }
                }
            }
            return singleton;
      }


}