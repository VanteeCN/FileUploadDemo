package cn.rayfoo.web;

import cn.rayfoo.common.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @Author: rayfoo@qq.com
 * @Date: 2020/7/20 4:49 下午
 * @Description: controller的公共内容  这里为了简化FileUploadController的代码，将非请求方法都放入了baseController中
 */
public class BaseController {

    /**
     * 日志记录
     */
    Logger logger = LoggerFactory.getLogger(BaseController.class);

    /**
     * 用于初始化请求、响应、session
     */
    protected HttpServletResponse response;
    protected HttpServletRequest request;
    protected HttpSession session;

    //server上传的基础路径
    protected String fieldPath = "/upload/";

    /**
     * 在所有请求执行之前执行 初始化一些参数
     * @param request
     * @param response
     */
    @ModelAttribute
    protected void init(HttpServletRequest request,HttpServletResponse response){
        this.request = request;
        this.response = response;
        this.session = request.getSession();
    }

    /**
     * 获取服务器的url
     * @return
     */
    protected String getServerURL(){
        return Protocol.COMMON_HTTP.getProtocolType() + request.getLocalAddr() + ":" + request.getLocalPort() + request.getContextPath();
    }

    /**
     * 获取服务器的一个文件夹路径 会在末尾加上/
     * @param dirName 文件夹名称
     * @return
     */
    protected String getServerPath(String dirName){
        return session.getServletContext().getRealPath( dirName) + "/";
    }

    /**
     * 获取服务器上的某个文件路径
     * @param dirName 文件夹名称
     * @param fileName 文件名称
     * @return
     */
    protected String getServerFile(String dirName,String fileName){
        return getServerPath(dirName) + fileName;
    }

    /**
     * 使用uuid替换文件名
     * @param file MultipartFile对象
     * @return
     */
    protected String replaceFileName(MultipartFile file){
        //获取文件名
        String fileName = file.getOriginalFilename();
        //获取文件后缀名
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //使用uuid替换文件名
        fileName = UUID.randomUUID().toString().replace("-", "") + suffix;
        //返回文件名
        return fileName;
    }

}
