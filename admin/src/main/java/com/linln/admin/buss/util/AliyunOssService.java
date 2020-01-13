package com.linln.admin.buss.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.ListBucketsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AliyunOssService {

    /**
     * 斜杠
     */
    private final String FLAG_SLANTING_ROD = "/";
    /**
     * http://
     */
    private final String FLAG_HTTP = "http://";
    /**
     * https://
     */
    private final String FLAG_HTTPS = "https://";
    /**
     * 空字符串
     */
    private final String FLAG_EMPTY_STRING = "";
    /**
     * 点号
     */
    private final String FLAG_DOT = ".";
    /**
     * 横杠
     */
    private final String FLAG_CROSSBAR = "-";

    /**
     * 缺省的最大上传文件大小：20M
     */
    private final int DEFAULT_MAXIMUM_FILE_SIZE = 20;

    /**
     * endpoint
     */
    private String endpoint = "http://oss-cn-beijing.aliyuncs.com";

    /**
     * access key id
     */
    private String accessKeyId = "LTAI4FfXfoqYYVhVbUfwZSM1";

    /**
     * access key secret
     */
    private String accessKeySecret = "SCw1mNAZCikk81QDDxFC8IUwrNdDjU";

    /**
     * bucket name (namespace)
     */
    private String bucketName = "caterina202001121459";

    /**
     * 以文件流的方式上传文件
     * @Author: Captain&D
     * @cnblogs: https://www.cnblogs.com/captainad
     * @param fileName 文件名称
     * @param filePath 文件路径
     * @param inputStream 文件输入流
     * @return
     */
    public String uploadFile(String fileName, String filePath, InputStream inputStream) {
        return coreUpload(fileName, filePath, inputStream);
    }

	public String uploadImg2Oss(MultipartFile file) {
		if (file.getSize() > 1024 * 1024 * 20) {
			return "图片太大";
		}
		try {
			InputStream inputStream = file.getInputStream();
			
            String uuidFileName = UUID.randomUUID().toString().replace(FLAG_CROSSBAR, FLAG_EMPTY_STRING);
            String fname = file.getOriginalFilename();
            String suffix = fname.substring(fname.lastIndexOf(FLAG_DOT), fname.length());
            String fileName = uuidFileName.concat(suffix);
		    
			String fileUrl = coreUpload(fileName,"",inputStream);
			
			return fileUrl;
		} catch (Exception e) {
		    e.printStackTrace();
			return null;
		}
	}
    
    
    
    /**
     * 核心上传功能
     * @Author: Captain&D
     * @cnblogs: https://www.cnblogs.com/captainad
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param inputStream 文件输入流
     * @return
     */
    private String coreUpload(String fileName, String filePath, InputStream inputStream) {
        log.info("Start to upload file....");
        if(StringUtils.isEmpty(fileName) || inputStream == null) {
            log.error("Filename Or inputStream is lack when upload file.");
            return null;
        }
        if(StringUtils.isEmpty(filePath)) {
            log.warn("File path is lack when upload file but we automatically generated");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String dateCategory = sdf.format(new Date());
//            String dateCategory = DateUtil.getFormatDate(new Date(), "yyyyMMdd");
//            filePath = FLAG_SLANTING_ROD.concat(dateCategory).concat(FLAG_SLANTING_ROD);
            filePath = dateCategory.concat(FLAG_SLANTING_ROD);
        }
        String fileUrl;
        OSSClient ossClient = null;
        try{

            // If the upload file size exceeds the limit
            long maxSizeAllowed = getMaximumFileSizeAllowed();
            if(Long.valueOf(inputStream.available()) > maxSizeAllowed) {
                log.error("Uploaded file is too big.");
                return null;
            }

            // Create OSS instance
            ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

            // Create bucket if not exists
            if (!ossClient.doesBucketExist(bucketName)) {
                log.info("Bucket '{}' is not exists and create it now.", bucketName);
                ossClient.createBucket(bucketName);
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
                createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
                ossClient.createBucket(createBucketRequest);
            }

            /*********************************/
            // List the bucket in my account
            //listBuckets(ossClient);
            /*********************************/

            // File path format
            if(!filePath.endsWith(FLAG_SLANTING_ROD)) {
                filePath = filePath.concat(FLAG_SLANTING_ROD);
            }

            // File url
            StringBuilder buffer = new StringBuilder();
            buffer.append(filePath).append(fileName);
            fileUrl = buffer.toString();
            log.info("After format the file url is {}", fileUrl);

            // Upload file and set ACL
            PutObjectResult result = ossClient.putObject(new PutObjectRequest(bucketName, fileUrl, inputStream));
            ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
            if(result != null) {
                log.info("Upload result:{}", result.getETag());
                log.info("Upload file {} successfully.", fileName);
            }
            fileUrl = getHostUrl().concat(fileUrl);
            log.info("Call path is {}", fileUrl);

            /***********************************/
            // List objects in your bucket
            //listObjects(ossClient);
            /***********************************/

        }catch (Exception e){
            log.error("Upload file failed.", e);
            fileUrl = null;
        }finally {
            if(ossClient != null) {
                ossClient.shutdown();
            }
        }
        return fileUrl;
    }

    /**
     * 以文件的形式上传文件
     * @Author: Captain&D
     * @cnblogs: https://www.cnblogs.com/captainad
     * @param fileName
     * @param filePath
     * @param file
     * @return
     */
    public String uploadFile(String fileName, String filePath, File file) {
        if(file == null) {
            log.warn("File is lack when upload.");
            return null;
        }
        if(StringUtils.isEmpty(fileName)) {
            log.warn("File name is lack when upload file but we automatically generated");
            String uuidFileName = UUID.randomUUID().toString().replace(FLAG_CROSSBAR, FLAG_EMPTY_STRING);
            String fname = file.getName();
            String suffix = fname.substring(fname.lastIndexOf(FLAG_DOT), fname.length());
            fileName = uuidFileName.concat(suffix);
        }
        InputStream inputStream = null;
        String fileUrl = null;
        try{
            inputStream = new FileInputStream(file);
            fileUrl = uploadFile(fileName, filePath, inputStream);
        }catch (Exception e){
            log.error("Upload file error.", e);
        }finally {
            IOUtils.safeClose(inputStream);
        }
        return fileUrl;
    }

    /**
     * 获取访问的base地址
     * @Author: Captain&D
     * @cnblogs: https://www.cnblogs.com/captainad
     * @return
     */
    private String getHostUrl() {
        String hostUrl = null;
        if(this.endpoint.startsWith(FLAG_HTTP)) {
            hostUrl = FLAG_HTTP.concat(this.bucketName).concat(FLAG_DOT)
                    .concat(this.endpoint.replace(FLAG_HTTP, FLAG_EMPTY_STRING)).concat(FLAG_SLANTING_ROD);
        } else if (this.endpoint.startsWith(FLAG_HTTPS)) {
            return FLAG_HTTPS.concat(this.bucketName).concat(FLAG_DOT)
                    .concat(this.endpoint.replace(FLAG_HTTPS, FLAG_EMPTY_STRING)).concat(FLAG_SLANTING_ROD);
        }
        return hostUrl;
    }

    /**
     * 获取最大允许上传文件的大小
     * @Author: Captain&D
     * @cnblogs: https://www.cnblogs.com/captainad
     * @return
     */
    private long getMaximumFileSizeAllowed() {
        // 缓存单位是M
        return DEFAULT_MAXIMUM_FILE_SIZE * 1024L * 1024L;
    }

    /**
     * 删除文件
     * @Author: Captain&D
     * @cnblogs: https://www.cnblogs.com/captainad
     * @param fileUrl 文件访问的全路径
     */
    public void deleteFile(String fileUrl) {
        log.info("Start to delete file from OSS.{}", fileUrl);
        if(StringUtils.isEmpty(fileUrl)
                || (!fileUrl.startsWith(FLAG_HTTP)
                && !fileUrl.startsWith(FLAG_HTTPS))) {
            log.error("Delete file failed because the invalid file address. -> {}", fileUrl);
            return;
        }
        OSSClient ossClient = null;
        try{
            /**
             * http:// bucketname                                dev/test/pic/abc.jpg = key
             * http:// captainad.oss-ap-southeast-1.aliyuncs.com/dev/test/pic/abc.jpg
             */
            String key = fileUrl.replace(getHostUrl(), FLAG_EMPTY_STRING);
            if(log.isDebugEnabled()) {
                log.debug("Delete file key is {}", key);
            }
            ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
            ossClient.deleteObject(bucketName, key);
        }catch (Exception e){
           log.error("Delete file error.", e);
        } finally {
            if(ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    
    public static void main(String[] args) {
		AliyunOssService aliyunOssService = new AliyunOssService();
		File file = new File("E:\\abc.jpeg");
		aliyunOssService.uploadFile("", "", file);
	}

}