package com.ruoyi.web.controller.h5;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * e签宝实名认证完成后的中间跳转页
 * 在微信小程序webview中，e签宝认证完成后会重定向到此页面
 * 此页面通过wx.miniProgram.redirectTo跳回小程序原生页面
 * H5环境下2秒后fallback跳转到前端认证页
 */
@Controller
@RequestMapping("/h5/esign")
public class EsignAuthDoneController {

    @GetMapping("/auth-done")
    public void authDone(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write("<!DOCTYPE html>");
        writer.write("<html><head>");
        writer.write("<meta charset=\"UTF-8\">");
        writer.write("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        writer.write("<title>认证完成</title>");
        writer.write("<script src=\"https://res.wx.qq.com/open/js/jweixin-1.6.0.js\"></script>");
        writer.write("</head><body>");
        writer.write("<div style=\"display:flex;align-items:center;justify-content:center;height:100vh;font-size:16px;color:#666;\">认证完成，正在返回...</div>");
        writer.write("<script>");
        writer.write("try { wx.miniProgram.redirectTo({ url: '/pages/auth/verify?auth_done=1' }); } catch(e) {}");
        writer.write("setTimeout(function() { window.location.href = 'https://app.caigon.cn/#/pages/auth/verify?auth_done=1'; }, 2000);");
        writer.write("</script>");
        writer.write("</body></html>");
        writer.flush();
    }
}
