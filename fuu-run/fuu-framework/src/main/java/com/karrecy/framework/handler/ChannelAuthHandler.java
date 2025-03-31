package com.karrecy.framework.handler;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.karrecy.common.core.domain.model.LoginUser;
import com.karrecy.common.enums.UserType;
import com.karrecy.common.helper.LoginHelper;
import com.karrecy.framework.websocket.channel.NioWebSocketChannelPool;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * netty连接校验
 */
@Slf4j
@ChannelHandler.Sharable
@Component
@RequiredArgsConstructor
public class ChannelAuthHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final NioWebSocketChannelPool channelPool;

    private final NioWebSocketChannelPool webSocketChannelPool;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        try {
            String authorization = request.headers().get("Authorization");
            LoginUser loginUser = null;
            if (ObjectUtil.isNull(authorization)) {
                // 1. 获取查询参数中的 token（假设参数名为 'token'）
                String uri = request.uri();
                // 从 URI 中提取 token（假设URL形式为 ws://your-websocket-url/ws?token=yourToken）
                String token = uri.substring(uri.indexOf("token=") + 6);
                // 2. 校验 token 并获取用户信息
                loginUser = LoginHelper.getLoginUser(token);
            }else {
                String tokenValue = authorization.substring(7);
                loginUser = LoginHelper.getLoginUser(tokenValue);
            }

            if (ObjectUtil.isNull(loginUser)) {
                log.error("token校验失败,拒绝连接");
                ctx.close();
            }
            ctx.channel().attr(webSocketChannelPool.USERTYPE).set(UserType.getByUserType(loginUser.getUserType()).getCode());
            ctx.channel().attr(webSocketChannelPool.UID).set(loginUser.getUid());
            webSocketChannelPool.addChannel(loginUser.getUid(),ctx.channel());
            request.setUri("/ws");
            // 传递到下一个handler：升级握手
            ctx.fireChannelRead(request.retain());
            // 在本channel上移除这个handler消息处理，即只处理一次，鉴权通过与否
            ctx.pipeline().remove(ChannelAuthHandler.class);
        }
        catch (Exception e) {
            log.error("token校验失败,拒绝连接");
            ctx.close();
        }
    }
}
