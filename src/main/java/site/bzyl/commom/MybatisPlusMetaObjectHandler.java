package site.bzyl.commom;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import site.bzyl.util.EmployeeHolder;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());

        Long currentEmployeeId = EmployeeHolder.getCurrentEmployeeId();
        metaObject.setValue("createUser", currentEmployeeId);
        metaObject.setValue("updateUser", currentEmployeeId);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime", LocalDateTime.now());
        Long currentEmployeeId = EmployeeHolder.getCurrentEmployeeId();
        metaObject.setValue("updateUser", currentEmployeeId);
    }
}
