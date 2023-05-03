package site.bzyl.domain;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
/* @TableName("employee") 实体和表同名可以不加这个注解 */
public class Employee implements Serializable {

    // todo 这句话和上面的实现 Serializable 看不懂
    private static final Long serialVersionUID = 1L;

    /* mysql中bigint对应的字段用Long */
    private Long id;
    private String name;
    private String username;
    private String password;
    private String phone;
    private String sex;
    private String idNumber;
    private Integer status;
    // todo 为什么用 LocalDateTime 不用 Date
    private LocalDateTime createTime;
    // todo 将来会讲TableField
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
