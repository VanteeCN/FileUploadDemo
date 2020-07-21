package cn.rayfoo.common;

/**
 * @Author: rayfoo@qq.com
 * @Date: 2020/7/21 10:46 上午
 * @Description: 封装http和https
 */
public enum Protocol {

    SECURITY_HTTP("https://"),

    COMMON_HTTP("http://");

    private String protocolType;

    public String getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(String protocolType) {
        this.protocolType = protocolType;
    }

    Protocol(String protocolType) {
        this.protocolType = protocolType;
    }
}
