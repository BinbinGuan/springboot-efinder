package cn.yours.elfinder.core.impl.sftpfilesystem;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;

import java.io.Serializable;

/**
 * @author: GuanBin
 * @date: Created in 上午7:43 2021/4/27
 */
public class SFtpEntry implements Serializable {

    private static final long serialVersionUID = -7317601468765729645L;
    private String filename;
    private String path;
    private String longname;
    private SftpATTRS attrs;

    public SFtpEntry() {
    }

    SFtpEntry(String path, String filename, String longname, SftpATTRS attrs) {
        this.setPath(path);
        this.setFilename(filename);
        this.setLongname(longname);
        this.setAttrs(attrs);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFilename() {
        return this.filename;
    }

    void setFilename(String filename) {
        this.filename = filename;
    }

    public String getLongname() {
        return this.longname;
    }

    void setLongname(String longname) {
        this.longname = longname;
    }

    public SftpATTRS getAttrs() {
        return this.attrs;
    }

    void setAttrs(SftpATTRS attrs) {
        this.attrs = attrs;
    }

    public String toString() {
        return this.longname;
    }

    public int compareTo(Object o) throws ClassCastException {
        if (o instanceof ChannelSftp.LsEntry) {
            return this.filename.compareTo(((ChannelSftp.LsEntry)o).getFilename());
        } else {
            throw new ClassCastException("a decendent of LsEntry must be given.");
        }
    }


}
