package site.bzyl.service.impl;

import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.bzyl.commom.Result;
import site.bzyl.constant.HttpConstant;
import site.bzyl.dao.UserMapper;
import site.bzyl.dto.UserDTO;
import site.bzyl.entity.User;
import site.bzyl.service.IUserService;
import site.bzyl.util.ValidateCodeUtils;

import javax.servlet.http.HttpSession;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

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
        // 将手机号和验证码存入Session
        session.setAttribute(phone, code);
        // 返回结果信息
        return Result.success("验证码发送成功！");
    }

    @Override
    public Result<User> login(UserDTO userDTO, HttpSession session) {
        String validationCode = (String) session.getAttribute(userDTO.getPhone());
        // session中不存在code或者输入的验证码与session中的不同
        if (StringUtils.isEmpty(validationCode) || !validationCode.equals(userDTO.getCode())) {
            return Result.error("验证码错误，请重试！");
        }
        // 根据电话查询用户信息
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getPhone, userDTO.getPhone());
        User user = this.getOne(lqw);
        // 用户第一次登录，注册新账号
        if (user == null) {
            user = new User();
            user.setPhone(userDTO.getPhone());
            user.setStatus(1);
            this.save(user);
        }
        // 将当前用户存入session
        session.setAttribute(HttpConstant.CURRENT_LOGIN_USER_ID, user.getId());

        // 登录成功
        return Result.success(user);
    }
}
