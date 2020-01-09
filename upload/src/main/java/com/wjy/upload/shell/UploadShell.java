package com.wjy.upload.shell;

import com.wjy.upload.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;
import java.util.Map;

@Slf4j
@ShellComponent
public class UploadShell {
    @Autowired
    private UploadService uploadService;

    @ShellMethod(value = "multiUpload")
    public Map<String, Object>  multiUpload(@ShellOption("--file")String filePath, @ShellOption("--size")int byteSize
        ,@ShellOption("--target") String targetFile){
        Map<String, Object> map = uploadService.multiUpload(filePath, byteSize);
        double uploadRate=0;
        String uploadId = (String) map.get("uploadId");
        //保证分块上传完成
        do{
            uploadRate = uploadService.getUploadRate(uploadId);
        }while (uploadRate !=1);
        try {
            uploadService.mergeFile(uploadId,byteSize,targetFile);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
        return map;
    }

    @ShellMethod(value = "breakpointUpload",key = "breakpoint")
    public void breakpointUpload(@ShellOption("--id")String uploadId, @ShellOption(value = "--size")Integer byteSize
            ,@ShellOption(value = "--file",defaultValue = "")String filepath){
        uploadService.breakpointUpload(uploadId,byteSize,filepath);
    }
}
