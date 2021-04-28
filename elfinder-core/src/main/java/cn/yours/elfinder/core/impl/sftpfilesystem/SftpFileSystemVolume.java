package cn.yours.elfinder.core.impl.sftpfilesystem;

import cn.yours.elfinder.core.Target;
import cn.yours.elfinder.core.Volume;
import cn.yours.elfinder.core.VolumeBuilder;
import cn.yours.elfinder.core.impl.aliyunoss.AliyunOssFileSystemTarget;
import cn.yours.elfinder.core.impl.filesystem.NIO2FileSystemTarget;
import cn.yours.elfinder.core.impl.filesystem.NIO2FileSystemVolume;
import cn.yours.elfinder.param.Node;
import cn.yours.elfinder.param.SftpDriverConfig;
import cn.yours.elfinder.support.nio.NioHelper;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import static cn.yours.elfinder.service.VolumeSources.SFTPFILESYSTEM;

/**
 * Aliyun OSS Volume Implementation.
 *
 * @author Van
 */
public class SftpFileSystemVolume implements Volume {

    private static final Logger logger = LoggerFactory.getLogger(SftpFileSystemVolume.class);

    private final String alias;
    private final String source;
    private final SftpDriverConfig sftpDriverConfig;
    private final SFtpEntry rootEntry;
    private final SftpFileSystemTarget rootTarget;
    private final SftpService sftpService;

    private SftpFileSystemVolume(Builder builder, Node nodeConfig) {

        this.sftpDriverConfig = nodeConfig.getSftpDriverConfig();
        if (sftpDriverConfig == null) {
            throw new RuntimeException("Please config your aliyun driver");
        }
        this.sftpService = new SftpService(sftpDriverConfig.getIp(), sftpDriverConfig.getPort(), sftpDriverConfig.getUserName(), sftpDriverConfig.getPassword());
        this.alias = builder.alias;
        this.rootEntry = sftpService.getRootEntry(builder.rootPath);
        this.rootTarget = new SftpFileSystemTarget(this, sftpService, rootEntry);
        this.source = SFTPFILESYSTEM.name();
    }


    public SftpService getSftpService() {
        return sftpService;
    }


//    public String getBucketName() {
//        return this.bucketName;
//    }

    private void checkBucket() {
//        if(!this.aliyunOssService.isBucketExists(bucketName)){
//            throw new RuntimeException("Unable to create root dir folder");
//        }
    }

    public String getRootDir() {
        return rootEntry.getFilename();
    }

    public SftpFileSystemTarget getRootDirTarget() {
        return rootTarget;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public void createFile(Target target) throws IOException {
        SftpFileSystemTarget target1 = ((SftpFileSystemTarget) target);
        ChannelSftp sftp = target1.getSftpService().getSftp();
        String path = target1.getLsEntry().getPath();
        try {
            sftp.put(new ByteArrayInputStream("".getBytes()), path);
            target1.getLsEntry().setAttrs(sftp.stat(path));
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createFolder(Target target) throws IOException {
        SftpFileSystemTarget target1 = ((SftpFileSystemTarget) target);
        ChannelSftp sftp = target1.getSftpService().getSftp();
        String path = target1.getLsEntry().getPath();
        try {
            sftp.mkdir(path);
            target1.getLsEntry().setAttrs(sftp.stat(path));
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFile(Target target) throws IOException {
        String path = ((SftpFileSystemTarget) target).getLsEntry().getPath();
        logger.info("start to delete file by path:{}", path);
        try {
            sftpService.getSftp().rm(path);
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFolder(Target target) throws IOException {
        String path = ((SftpFileSystemTarget) target).getLsEntry().getPath();
        logger.info("start to delete directory by path:{}", path);
        try {
            sftpService.getSftp().rmdir(path);
        } catch (SftpException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean exists(Target target) {
//        boolean exists = ((AliyunOssFileSystemTarget)target).exists();
        return true;
    }

    @Override
    public Target fromPath(String relativePath) {
        String rootDir = getRootDir().toString();
        SFtpEntry sFtpEntry = new SFtpEntry();
        if (relativePath.startsWith(rootDir)) {
            sFtpEntry = getRootEntry();
        } else {
            String path = String.format("%s%s", rootDir, relativePath);
            sFtpEntry.setPath(path);
            int one = path.lastIndexOf("/");
            String fileName = path.substring((one + 1));
            sFtpEntry.setFilename(fileName);
            try {
                if (isExist(path)) {
                    sFtpEntry.setAttrs(sftpService.getSftp().stat(path));
                }
            } catch (SftpException e) {
                e.printStackTrace();
            }
        }
        return fromPath(sFtpEntry);
    }

    public boolean isExist(String directory) {
        boolean isExistFlag = false;
        try {
            SftpATTRS sftpATTRS = this.getSftpService().getSftp().stat(directory);
            isExistFlag = true;
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().equals("no such file")) {
                isExistFlag = false;
            }
        }
        return isExistFlag;
    }


    private Target fromPath(SFtpEntry sFtpEntry) {
        return fromPath(this, sFtpEntry);
    }

    public static Target fromPath(SftpFileSystemVolume volume, SFtpEntry path) {
        return new SftpFileSystemTarget(volume, volume.getSftpService(), path);
    }
//    public Target fromOSSObjectSummary(OSSObjectSummary ossObjectSummary) {
//        AliyunOssFileSystemTarget target = new AliyunOssFileSystemTarget(this, ossObjectSummary);
//        return target;
//    }

    @Override
    public long getLastModified(Target target) throws IOException {
        return ((SftpFileSystemTarget) target).getLastModified();
    }

    @Override
    public String getMimeType(Target target) throws IOException {
        return ((SftpFileSystemTarget) target).getMimeType();
    }

    @Override
    public String getName(Target target) {
        return ((SftpFileSystemTarget) target).getLsEntry().getFilename();
    }

    @Override
    public String getCsscls(Target target) {
        return null;
    }

    @Override
    public String getExternalUrl(Target target) {
        String protocol = "https://";
//        if(aliyunDriverConfig.getBindedDomain() != null){
//            return protocol + aliyunDriverConfig.getBindedDomain() + ElFinderConstants.ELFINDER_PARAMETER_FILE_SEPARATOR + ((AliyunOssFileSystemTarget)target).getKey();
//        } else {
//            return protocol + bucketName + "." + aliyunDriverConfig.getEndpoint() + ElFinderConstants.ELFINDER_PARAMETER_FILE_SEPARATOR + ((AliyunOssFileSystemTarget)target).getKey();
//        }
        return protocol;
    }

    @Override
    public String[] getDisabledCmds(Target target) {
        return null;
    }

    @Override
    public Target getParent(Target target) {
        String path = ((SftpFileSystemTarget) target).getLsEntry().getPath();

        int i = path.lastIndexOf("/");
        String parentPath = path.substring(0, i);
        SftpATTRS stat = null;
        synchronized (sftpService.getSftp()) {
            try {
                stat = sftpService.getSftp().stat(parentPath);
                System.out.println("打印异常信息***********");
            } catch (SftpException e) {
                e.printStackTrace();
            }
        }
        SFtpEntry sFtpEntry = new SFtpEntry();
        sFtpEntry.setAttrs(stat);
        int one = parentPath.lastIndexOf("/");
        String fileName = parentPath.substring((one + 1));
        sFtpEntry.setFilename(fileName);
        sFtpEntry.setPath(parentPath);
        return fromPath(sFtpEntry);
    }

    @Override
    public String getPath(Target target) throws IOException {
        return ((SftpFileSystemTarget) target).getLsEntry().getPath().substring(rootEntry.getPath().length());
    }

    @Override
    public Target getRoot() {
        return rootTarget;
    }

    public SFtpEntry getRootEntry() {
        return rootEntry;
    }

    @Override
    public long getSize(Target target) throws IOException {
        return ((SftpFileSystemTarget) target).getSize();
    }

    @Override
    public boolean isFolder(Target target) {
        try {
            SFtpEntry lsEntry = ((SftpFileSystemTarget) target).getLsEntry();
            SftpATTRS attrs = lsEntry.getAttrs();
            if (attrs == null) {
                attrs = sftpService.getSftp().stat(lsEntry.getPath());
            }
            return attrs.isDir();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isRoot(Target target) {
        return ((SftpFileSystemTarget) target).getLsEntry().getPath().equalsIgnoreCase("/root");
    }

    @Override
    public boolean hasChildFolder(Target target) throws IOException {
        return true;
    }

    @Override
    public Target[] listChildren(Target target) throws IOException {
        SftpFileSystemTarget target1 = (SftpFileSystemTarget) target;
        String path = target1.getLsEntry().getPath();
        List<Target> targets = new ArrayList<>();
        try {
            Vector filelist = sftpService.getSftp().ls(path);
            for (int i = 0; i < filelist.size(); i++) {
                ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) filelist.get(i);
                if (lsEntry.getFilename().getBytes()[0] == 46) {
                    continue;
                }
                String p = String.format("%s/%s", path, lsEntry.getFilename());
                SFtpEntry sFtpEntry = new SFtpEntry(p, lsEntry.getFilename(), lsEntry.getLongname(), lsEntry.getAttrs());
                targets.add(new SftpFileSystemTarget(this, sftpService, sFtpEntry));
            }
        } catch (SftpException e) {
            e.printStackTrace();
        }
        return targets.toArray(new Target[targets.size()]);
    }

    @Override
    public InputStream openInputStream(Target target) throws IOException {
        // 断点续传
        SftpFileSystemTarget target1 = (SftpFileSystemTarget) target;
        String path = target1.getLsEntry().getPath();
        InputStream inputStream=null;
        try {
            inputStream=   sftpService.getSftp().get(path);
        } catch (SftpException e) {
            e.printStackTrace();
        }
//        return this.aliyunOssService.openInputStream(bucketName, getPath(target));
        return inputStream;
    }

    @Override
    public void rename(Target origin, Target destination) throws IOException {
        //AliyunOssHelper.rename(getPath(origin), getPath(destination));
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Target> search(String target) throws IOException {
        List<Target> targets = new ArrayList<>(0);
        return Collections.unmodifiableList(targets);
    }

    @Override
    public void createAndCopy(Target src, Target dst) throws IOException {
        if (src.getVolume().isFolder(src)) {
            createAndCopyFolder(src, dst);
        } else {
            createAndCopyFile(src, dst);
        }
    }

    private void createAndCopyFile(Target src, Target dst) throws IOException {
        InputStream is = src.getVolume().openInputStream(src);
        putFile(dst, is);
        is.close();
    }

    private void createAndCopyFolder(Target src, Target dst) throws IOException {
//        dst.getVolume().createFolder(dst);
//
//        String dstPath = dst.getVolume().getPath(dst);
//        dstPath = this.aliyunOssService.fixOssFolderName(dstPath);
//        for (Target c : src.getVolume().listChildren(src)) {
//            createAndCopy(c, dst.getVolume().fromPath(dstPath + c.getVolume().getName(c)));
//        }
    }

    @Override
    public void putFile(Target target, String content, String encoding) {
//        try {
//            deleteFile(target);
//            this.aliyunOssService.createFile(bucketName, getPath(target), content.getBytes(encoding));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void putFile(Target target, InputStream inputStream) {
//        try {
//            deleteFile(target);
//            this.aliyunOssService.createFile(bucketName, getPath(target), inputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * Gets a Builder for creating a new AliyunOssFileSystemVolume instance.
     *
     * @return a new Builder for AliyunOssFileSystemVolume.
     */
    public static Builder builder(String alias, String path, Node nodeConfig) {
        return new SftpFileSystemVolume.Builder(alias, path, nodeConfig);
    }

    public static class Builder implements VolumeBuilder<SftpFileSystemVolume> {
        // required fields
        private final String alias;
        private final Node nodeConfig;
        private final String rootPath;

        public Builder(String alias, String path, Node nodeConfig) {
            this.alias = alias;
            this.nodeConfig = nodeConfig;
            this.rootPath = path;
        }

        @Override
        public SftpFileSystemVolume build() {
            return new SftpFileSystemVolume(this, nodeConfig);
        }
    }

}