package com.github.cwdtom.gateway.util;

import com.github.cwdtom.gateway.entity.Constant;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URLConnection;

/**
 * 响应工具
 *
 * @author chenweidong
 * @since 1.0.0
 */
public class ResponseUtils {
    /**
     * 构造失败响应
     *
     * @param status 状态码
     * @return 失败响应
     */
    public static FullHttpResponse buildFailResponse(HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
        return response;
    }

    /**
     * 构造成功响应
     *
     * @param content     内容
     * @param contentType 内容类型
     * @return 成功响应
     */
    public static FullHttpResponse buildSuccessResponse(String content, String contentType) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(content.getBytes()));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        return response;
    }

    /**
     * 构造响应
     *
     * @param connection 连接
     * @return 响应
     */
    public static FullHttpResponse buildResponse(URLConnection connection) throws IOException {
        // 请求代理地址
        byte[] bytes = IOUtils.toByteArray(connection.getInputStream());
        // 获取http标识
        String[] strings = connection.getHeaderField(0).split(" ");
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.valueOf(strings[0]),
                HttpResponseStatus.valueOf(Integer.parseInt(strings[1])),
                Unpooled.wrappedBuffer(bytes));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, connection.getContentType());
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, connection.getContentLength());
        return response;
    }

    /**
     * 构造重定向响应
     *
     * @param host 重定向host
     * @return 响应
     */
    public static FullHttpResponse buildRedirectResponse(String host) {
        byte[] content = String.format(Constant.REDIRECT_TEMPLATE, Constant.HTTPS_PREFIX + host).getBytes();
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(content));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.length);
        return response;
    }
}