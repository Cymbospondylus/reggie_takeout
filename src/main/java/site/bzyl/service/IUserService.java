package site.bzyl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.bzyl.commom.Result;
import site.bzyl.dto.UserDTO;
import site.bzyl.entity.User;

import javax.servlet.http.HttpSession;

public interface IUserService extends IService<User> {
    Result<String> generateValidationCode(User user, HttpSession session);

    Result<String> login(UserDTO userDTO,  HttpSession session);
}
