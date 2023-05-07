package site.bzyl.controller;


import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import site.bzyl.commom.Result;
import site.bzyl.service.ICommonService;

import javax.servlet.http.HttpServletResponse;
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

    /**
     * 文件下载, 用输入流从服务器磁盘读出，用输出流写回浏览器
     * 因为是通过输出流写到页面上，所以不需要返回json
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(@Param("name") String name, HttpServletResponse response) {
        commonService.download(name, response);
    }
}
