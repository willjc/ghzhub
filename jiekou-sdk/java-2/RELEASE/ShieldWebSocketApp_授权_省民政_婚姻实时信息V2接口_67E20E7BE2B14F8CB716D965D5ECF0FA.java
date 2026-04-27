package com.iflytek.fsp.shield.java.sdk;

import com.alibaba.fastjson.JSONObject;
import com.iflytek.fsp.shield.java.sdk.websocket.BaseApp;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URISyntaxException;
import java.net.URI;

  public class ShieldWebSocketApp_授权_省民政_婚姻实时信息V2接口_67E20E7BE2B14F8CB716D965D5ECF0FA extends BaseApp {

      public ShieldWebSocketApp_授权_省民政_婚姻实时信息V2接口_67E20E7BE2B14F8CB716D965D5ECF0FA()  throws URISyntaxException{
          this.appId = "1284f00407ef40ba8a4586756269b973";
          this.appSecret = "AEF5838F7BF54915EEE2FFD4939C4330";
          
          this.gmAppSecret = "479FE03BEF6744F9BE5739D10ABF110A21B7F052D0E0A1C2316F46FD70738A87";
          
          this.host = "10.16.71.133";
          this.stage = "RELEASE";
          this.publicKey = "305C300D06092A864886F70D0101010500034B00304802410099A2219E918CCD46BF8A26D8E2B33F4E2AA88679AB717CAE6068BC096118E0D22BFC2C1733D017BD515FE2340DADFD0ACCE5DC6A76B688466624F55D5EAE3D830203010001";
          
          this.gmPublicKey = "3059301306072A8648CE3D020106082A811CCF5501822D03420004A93560403CA41DB1F27E0372F66E99A8D121A8D5A576802E5454DC85DD77C7B09D1DC4BD81360099884C2ECA50FDA7E34C563A843281FC9F473FDC0DEF9134F4";
          
          
          this.gmPrivateKey = "308193020100301306072A8648CE3D020106082A811CCF5501822D047930770201010420A96630FEF4C4B14C6186FC949E9FFC130E5F3EDF50956F3F12D4B3184BEF109AA00A06082A811CCF5501822DA14403420004A93560403CA41DB1F27E0372F66E99A8D121A8D5A576802E5454DC85DD77C7B09D1DC4BD81360099884C2ECA50FDA7E34C563A843281FC9F473FDC0DEF9134F4";
          
          this.equipmentNo = "XXX";
          this.signStrategyUrl = "/getSignStrategy";
          this.tokenUrl = "/getTokenUrl";
          this.webSocketPort=4999;
          this.icloudlockEnabled = false;//关闭云锁验证
      }
      private static ShieldWebSocketApp_授权_省民政_婚姻实时信息V2接口_67E20E7BE2B14F8CB716D965D5ECF0FA singleton;

      public static ShieldWebSocketApp_授权_省民政_婚姻实时信息V2接口_67E20E7BE2B14F8CB716D965D5ECF0FA getInstance() throws URISyntaxException {
            if (singleton == null) {
                synchronized (ShieldWebSocketApp_授权_省民政_婚姻实时信息V2接口_67E20E7BE2B14F8CB716D965D5ECF0FA.class) {
                    if (singleton == null) {
                        singleton = new ShieldWebSocketApp_授权_省民政_婚姻实时信息V2接口_67E20E7BE2B14F8CB716D965D5ECF0FA();
                    }
                }
            }
            return singleton;
      }


}