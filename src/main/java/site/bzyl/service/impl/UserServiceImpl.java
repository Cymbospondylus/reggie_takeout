package site.bzyl.service.impl;

import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import site.bzyl.commom.Result;
import site.bzyl.constant.HttpConstant;
import site.bzyl.constant.RedisCacheConstant;
import site.bzyl.dao.UserMapper;
import site.bzyl.dto.UserDTO;
import site.bzyl.entity.User;
import site.bzyl.service.IUserService;
import site.bzyl.util.ValidateCodeUtils;

import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Override
    public Result<String> generateValidationCode(User user, HttpSession session) {
        // 简单检查手机号码是否正确
        String phone = user.getPhone();
        if (StringUtils.isEmpty(phone)) {
            return Result.error("发送失败，手机号码不能为空！");
        }
        // 生成验证码并调用阿里云sms服务
        String code = ValidateCodeUtils.generateValidateCode(6).toString();
        log.info("验证码：{}", code);
        /*SMSUtils.sendMessage(SystemConstant.SING_NAME, SystemConstant.TEMPLATE_CODE, phone, code);*/
        // 将手机号和验证码存入Redis缓存, 过期时间五分钟
        redisTemplate.opsForValue().set(RedisCacheConstant.LOGIN_CODE_PREFIX + phone, code, 5, TimeUnit.MINUTES);
        // 返回结果信息, 在前端弹出窗口代替发送短信
        return Result.success(code);
    }

    @Override
    public Result<User> login(UserDTO userDTO, HttpSession session) {
        // 获取手机号
        String phone = userDTO.getPhone();
        // 根据手机号码从Redis中获取验证码
        String validationCode = redisTemplate.opsForValue().get(RedisCacheConstant.LOGIN_CODE_PREFIX + phone);
        // Redis中不存在code或者输入的验证码与session中的不同
        if (StringUtils.isEmpty(validationCode) || !validationCode.equals(userDTO.getCode())) {
            return Result.error("验证码错误，请重试！");
        }
        // 根据电话查询用户信息
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getPhone, phone);
        User user = this.getOne(lqw);
        // 用户第一次登录，注册新账号
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setStatus(1);
            this.save(user);
        }
        // 将当前用户存入session
        session.setAttribute(HttpConstant.CURRENT_LOGIN_USER_ID, user.getId());
        // 登陆成功后删除验证码
        redisTemplate.delete(RedisCacheConstant.LOGIN_CODE_PREFIX + phone);
        // 登录成功
        return Result.success(user);
    }

    @Override
    public Result<String> logout(HttpSession session) {
        // 前端做了页面跳转，所以只需要删除session里的信息就可以了
        session.removeAttribute(HttpConstant.CURRENT_LOGIN_USER_ID);
        return Result.success("退出成功！");
    }
}
