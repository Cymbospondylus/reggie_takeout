package site.bzyl.dto;

import lombok.Data;
import site.bzyl.entity.User;
@Data
public class UserDTO extends User {
    private String code;
}
