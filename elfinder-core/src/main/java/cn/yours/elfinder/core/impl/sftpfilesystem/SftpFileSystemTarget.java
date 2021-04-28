package cn.yours.elfinder.core.impl.sftpfilesystem;

import cn.yours.elfinder.core.Target;
import cn.yours.elfinder.core.Volume;
import com.jcraft.jsch.ChannelSftp;

import java.io.IOException;

public class SftpFileSystemTarget implements Target {

//    private final OSSObjectSummary ossObject;
    private final Volume volume;
    private final SftpService sftpService;
    private final SFtpEntry lsEntry;

    public SftpFileSystemTarget(SftpFileSystemVolume volume,SftpService sftpService,SFtpEntry sFtpEntry) {
        this.lsEntry = sFtpEntry;
        this.volume = volume;
        this.sftpService = sftpService;
    }

    public SFtpEntry getLsEntry() {
        return lsEntry;
    }

    @Override
    public Volume getVolume() {
        return volume;
    }

    public SftpService getSftpService() {
        return sftpService;
    }


    //    public String getKey() {
//        return this.ossObject.getKey();
//    }

//    public String getName() {
//        if(isRoot()){
//            return "";
//        }
////        String name = Paths.get(ElfinderConfigurationUtils.toURI(getKey())).getFileName().toString();
//        return "";
//    }
//
//    public String getCsscls(Target target) {
//        if(isRoot()){
//            return "elfinder-navbar-root-aliyun";
//        }
//        return null;
//    }

    public String[] getDisabledCmds(Target target) {
        return new String[]{"archive", "rename"};
    }

    public long getSize(){
       return lsEntry.getAttrs().getSize();
    }

    public long getLastModified(){
        return  lsEntry.getAttrs().getMTime();
    }

    public boolean isFolder(){
        return lsEntry.getLongname().startsWith("d");
    }

//    public boolean isRoot(){
//        return this.aliyunOssService.isRoot(ossObject.getKey());
//    }

//    public void delete() throws IOException {
//        if(isFolder()){
//            this.aliyunOssService.deleteFolder(getBucketName(), getKey());
//        } else {
//            this.aliyunOssService.deleteFile(getBucketName(), getKey());
//        }
//    }
//
//    public String getBucketName() {
//        return ((AliyunOssFileSystemVolume)volume).getBucketName();
//    }
//
//    public String getParentPath() {
//        String parent = Paths.get(ElfinderConfigurationUtils.toURI(getKey())).getParent().toString();
//        if(parent.equalsIgnoreCase(ElFinderConstants.ELFINDER_PARAMETER_FILE_SEPARATOR)){
//            return "";
//        }
//
//        parent = this.aliyunOssService.fixOssFolderName(parent);
//
//        return parent;
//    }

//    public boolean exists() {
//        boolean exists = this.aliyunOssService.exists(getBucketName(), getKey());
//        return exists;
//    }

    public String getMimeType() throws IOException {
        if (getVolume().isFolder(this)) {
            return "directory";
        }
        return "file";
    }

}
