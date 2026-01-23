package com.digital.cnzz.gateway.demo.application;

import com.alibaba.fastjson.JSONObject;
import com.digital.szzz.gateway.sdk.api.GatewaySender;
import com.digital.szzz.gateway.sdk.bean.GatewayResponse;
import com.digital.szzz.gateway.sdk.util.AesUtil;
import com.digital.szzz.gateway.sdk.util.SignUtil;

import java.nio.charset.StandardCharsets;
/**
 *
 * 必读说明:
 * 当前文件代码为后端获取用户信息的请求示例
 * 此代码仅供逻辑参考,不提供稳定性和安全性保障,请根据业务系统需求进行修改
 * 请注意看代码注释,根据需要进行相关参数的修改
 * 当前代码使用到的参数均为测试应用示例参数,请勿直接在生产环境使用,发布上线请使用数郑给对应应用分配的相关参数
 *
 * pom.xml文件中的com.digital.szzz.gateway.sdk为私有仓库包,可能无法直接下载,请使用数郑提供的jar包手动导入项目当中
 * com.digital.szzz.gateway.sdk包地址:  https://cdn.digitalcnzz.com/open-platform/static/szzz-open-gateway-sdk-1.0-SNAPSHOT.jar
 *
 */
public class GetUserDetailTest {
    public static void main(String[] args) {
        /** 通过网关调用的method名称 szzz.uc.api.userdetail.get为获取用户详细信息接口 szzz.uc.api.userinfo.get为获取用户基本信息接口 */
        String method = "szzz.uc.api.userdetail.get";

        /** 业务参数，具体取值参考下文接口入参*/
        JSONObject json = new JSONObject();
        json.put("moduleId", "1220");
        /** oauthToken为GetOAuthToken中接口的出参参数 **/
        json.put("oauthToken", "424F7pvj1q4THDr30uVLx0QeHyXkJg6nkmbNk0sW");
        String body = json.toJSONString();


        /** 网关必传参数,用户信息授权接口申请通过后数郑分配,参见文末用户授权接入流程 */
        String appId = "testApi";
        String ivStr = "dTT5Gm+jR+P302sthbfguA==";
        String keyStr = "T69mMOGctM4XUbCsphlaUQ==";
        String secretKey = "abcsadfefafd";

        /** 请求网关接口 */
        GatewayResponse response = GatewaySender.send(appId, method, ivStr, keyStr, secretKey, body, 2000, 2000);
        /** code不为200时,请求异常(此状态码为网络状态码,非接口出参code) */
        if (response.getCode() != 200) {
            System.out.println("code not succ  " + response);
            return;
        }

        /** 验证接口出参未被恶意拦截篡改 */
        String expectSign = response.getSign();
        String factSign = SignUtil.signResponse(response, secretKey);
        if (expectSign.equals(factSign)) {
            /** 网关出参未被篡改 */
            System.out.println("sign succ " + factSign);
            /** 接口出参获取 */
            byte[] data = AesUtil.base64ToByte(response.getData());
            byte[] plainData = AesUtil.decrypt(AesUtil.base64ToByte(ivStr), AesUtil.base64ToByte(keyStr), data);
            System.out.println("receive plain data " + new String(plainData, StandardCharsets.UTF_8));
        } else {
            /** 网关出参被篡改 */
            System.out.println("sign failed!  " + factSign);
        }

    }
}
