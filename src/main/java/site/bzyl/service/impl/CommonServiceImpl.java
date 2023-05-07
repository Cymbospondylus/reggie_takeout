package site.bzyl.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import site.bzyl.commom.Result;
import site.bzyl.service.ICommonService;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class CommonServiceImpl implements ICommonService {
    /**
     * 不加el表达式 ${} 相当于直接复制, 而不是读取yaml里的配置属性
     */
    @Value("${reggie.base-path}")
    private String basePath;

    @Override
    public Result<String> upload(MultipartFile file) {
        // 获取文件原始名字
        String originalFilename = file.getOriginalFilename();
        // 获取uuid作为文件名防止文件重名覆盖
        String uuid = UUID.randomUUID().toString();
        // 截取出后缀.xxx
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 用配置里的basePath+uuid+源文件后缀拼成转存的新文件名
        String fileName = uuid + suffix;
        // 获取当前路径
        File dir = new File(basePath);
        // 如果当前路径不存在，则递归创建
        if (!dir.exists()) {
            dir.mkdir();
        }
        // 将file转存为 路径+文件名
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Result.success(fileName);
    }
}
