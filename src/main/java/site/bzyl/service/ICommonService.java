package site.bzyl.service;

import org.springframework.web.multipart.MultipartFile;
import site.bzyl.commom.Result;

import javax.servlet.http.HttpServletResponse;

public interface ICommonService{
    Result<String> upload(MultipartFile file);

    void download(String name, HttpServletResponse response);
}
