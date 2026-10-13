package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/common")
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 上传文件到阿里云OSS
     * @param file  上传的文件
     * @return
     */
    @RequestMapping("/upload")
    public Result upload(MultipartFile file) {
        log.info("开始上传文件: {}", file);

        try {
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            //文件后缀
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            //构造新文件名
            String objectName = UUID.randomUUID().toString() + suffix;
            //文件请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            //返回文件请求路径
            return Result.success(filePath);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        //上传文件失败
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
