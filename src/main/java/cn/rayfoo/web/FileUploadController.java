package cn.rayfoo.web;

import cn.rayfoo.utils.AliyunOSSUtil;
import cn.rayfoo.utils.FtpUtil;
import cn.rayfoo.utils.QRCodeUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * @Author: rayfoo@qq.com
 * @Date: 2020/7/20 4:22 下午
 * @Description:
 */
@Controller
public class FileUploadController extends BaseController {

    @PostMapping("/serverUpload")
    @ResponseBody
    public String fileUpload1(@RequestParam("file") MultipartFile file) {
        //如果文件为空 返回错误信息
        if(file.isEmpty()){
            return "file not found";
        }
        //获取路径
        String uploadPath = getServerPath(fieldPath);
        //创建文件对象
        File dir = new File(uploadPath);
        //判断文件夹是否存在
        if (!dir.exists()) {
            //不存在创建文件夹
            dir.mkdir();
        }
        //使用UUID替换文件名
        String fileName = replaceFileName(file);
        //文件上传逻辑
        try {
            //文件上传
            file.transferTo(new File(dir, fileName));
            //记录日志
            logger.debug("文件上传成功：" + uploadPath + fileName);
            //文件保存在服务器的URL
            String fileURL = getServerURL() + fieldPath + fileName;
            //生成二维码
            QRCodeUtil.encode(fileURL, getServerFile("/img/", "logo.jpg"), getServerFile(fieldPath, fileName + ".png"), true);
            //返回文件的url，二维码的url=文件url+.png
            return "文件上传成功：" + fileURL;
        } catch (Exception ex) {
            ex.printStackTrace();
            //记录日志
            logger.debug("文件上传出现异常：" + ex.getMessage());
            //返回错误信息
            return "文件上传出现异常：" + ex.getMessage();
        }
    }

    @PostMapping("/ftpUpload")
    @ResponseBody
    public String ftpUpload(MultipartFile file) {
        //如果文件为空 返回错误信息
        if(file.isEmpty()){
            return "file not found";
        }
        //获取文件名
        String fileName = replaceFileName(file);
        //上传的目录，如果没有回会动创建
        String filePath = "/img";
        try {
            //上传文件
            boolean reslut = FtpUtil.uploadFile(filePath, fileName, file.getInputStream());
            //判断是否上传成功
            if (reslut) {
                logger.debug("通过ftp文件上传成功！");
                return "success";
            }
            logger.debug("ftp文件上传失败！");
            return "error";
        } catch (IOException e) {
            e.printStackTrace();
            logger.debug("ftp上传服务器发生异常！");
            return "error";
        }
    }

    @GetMapping(value = "/ftpDownload/{fileId}")
    @ResponseBody
    public String ftpDownload(@PathVariable Integer fileId) {
        //校验是否有ftp权限。。。
        //ftp目录，模拟下载  这里的文件路径和文件名是提前定义的 可以通过id从数据库中查到
        String filePath = "/img";
        //获取完整文件名
        String realName = "d795d3b7acd94a0aacba096aca1c2927.jpg";
        //去FTP上拉取
        try {
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            response.setCharacterEncoding("utf-8");
            // 设置返回类型
            response.setContentType("multipart/form-data");
            // 文件名转码一下，不然会出现中文乱码
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(realName, "UTF-8"));
            //下载文件
            boolean flag = FtpUtil.downloadFile(filePath, realName, os);
            os.flush();
            os.close();
            if (flag) {
                logger.debug("ftp文件下载成功");
                return "success";
            }
            throw new RuntimeException("文件下载失败");
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("ftp文件下载失败:" + e.getMessage());
            return "error";
        }
    }


    @GetMapping(value = "/serverDownload/{fileName}/{fileType}")
    public ResponseEntity<byte[]> serverDownload(@PathVariable String fileName, @PathVariable String fileType) {
        //由于get请求无法正常携带.所以需要添加文件类型即后缀
        File file = new File(getServerPath(fieldPath) + fileName + "." + fileType);
        //创建字节数组，用于返回
        byte[] body = null;
        //初始化文件流
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            //解析文件
            body = new byte[is.available()];
            //将文件解析为文件流
            is.read(body);
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("server文件下载出现异常：" + e.getMessage());
        }
        //设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attchement;filename=" + file.getName());
        //返回状态码、文件
        HttpStatus statusCode = HttpStatus.OK;
        ResponseEntity<byte[]> entity = new ResponseEntity<>(body, headers, statusCode);
        logger.debug("server文件已下载");
        return entity;
    }

    @PostMapping("/aliOSSUpload")@ResponseBody
    public String aliOSSUpload(@RequestParam("file")MultipartFile file){
        //如果文件为空 返回错误信息
        if(file.isEmpty()){
            return "file not found";
        }
        //获取原文件名
        String fileName = replaceFileName(file);

        //返回图片的url
        String fileURL = AliyunOSSUtil.uploadFileInputSteam(fileName,file);
        logger.debug("阿里云OSS文件上传成功！");
        try {
            //生成二维码  由于阿里云OSS工具类又在文件之前加了UUID，所以可能会导致文件名和二维码不一致
            QRCodeUtil.encode(fileURL, getServerFile("/img/", "logo.jpg"), getServerFile(fieldPath, fileName + ".png"), true);
            logger.debug("阿里云OSS文件二维码生成成功！");
            //需要将二维码上传到服务器，可以使用getServerFile(fieldPath, fileName + ".png"创建File对象，使用AliyunOSSUtil.uploadFileInputSteam(fileName,file);上传
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("阿里云OSS文件上传失败：" + e.getMessage());
            return "error";
        }
        //在URL显示文件的URL，可以直接通过URL下载
        return fileURL;
    }

}
