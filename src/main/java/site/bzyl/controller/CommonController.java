package site.bzyl.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import site.bzyl.commom.Result;
import site.bzyl.service.ICommonService;

import java.io.File;
import java.io.IOException;

/**
 * 使用Spring-web里的Multipart实现文件上传和下载
 * 上传的MultipartFile是服务器本地的临时文件，请求结束后自动清理，需要调用transferTo方法转存
 */

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private ICommonService commonService;
    /**
     * 文件上传, 参数名必须和前端表单提交的name字段相同, 否则为null
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        return commonService.upload(file);
    }
}
