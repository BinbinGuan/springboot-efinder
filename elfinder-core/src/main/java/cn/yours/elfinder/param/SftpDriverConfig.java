package cn.yours.elfinder.param;

/**
 * @author: GuanBin
 * @date: Created in 上午10:16 2021/4/26
 */
public class SftpDriverConfig {
    private String ip;
    private String userName;
    private String password;
    private int port=22;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
