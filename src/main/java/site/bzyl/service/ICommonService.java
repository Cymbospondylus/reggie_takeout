package site.bzyl.service;

import org.springframework.web.multipart.MultipartFile;
import site.bzyl.commom.Result;

public interface ICommonService{
    Result<String> upload(MultipartFile file);
}
