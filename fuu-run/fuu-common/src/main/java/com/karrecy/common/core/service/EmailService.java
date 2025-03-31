package com.karrecy.common.core.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.karrecy.common.config.FuuConfig;
import com.karrecy.common.config.properties.EmailProperties;
import com.karrecy.common.constant.CacheNames;
import com.karrecy.common.utils.redis.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;

/**
 * email服务类
 */
@Service
@RequiredArgsConstructor
public class EmailService {
    private final EmailProperties emailProperties;
    private final FuuConfig fuuConfig;

    private final Duration duration = Duration.ofMinutes(5);
    private MailAccount account;

    @PostConstruct  // 该方法会在Spring完成依赖注入后执行
    private void init() {
        this.account = new MailAccount()
                .setFrom(fuuConfig.getName()+"<"+emailProperties.getUser()+">")
                .setAuth(true)
                .setHost(emailProperties.getHost())
                .setPort(emailProperties.getPort())
                .setUser(emailProperties.getUser())
                .setPass(emailProperties.getPass())
        ;

    }
    public void sendHtml(String email,String subject,String content) {
        MailUtil.send(account, email,subject, content, false);

    }
    public void sendEmailCode(String email) {
        // 生成6位随机数字验证码
        String code = RandomUtil.randomNumbers(6);
        // 发送邮件
        MailUtil.send(account, email, "验证码邮件", "您的验证码是：" + code + "，5分钟内有效", false);
        // 存储验证码
        RedisUtils.setCacheObject(
                CacheNames.EMAIL_CODE + email,
                code,
                duration
        );
    }
    public boolean verifyCode(String email, String code) {
        String key = CacheNames.EMAIL_CODE + email;
        String storedCode = RedisUtils.getCacheObject(key);

        if (storedCode == null || !storedCode.equals(code)) {
            return false;
        }
        // 验证成功后删除key（原子操作）
        RedisUtils.deleteObject(key);
        return true;
    }
}