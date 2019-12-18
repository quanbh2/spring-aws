package net.friend.service;

import com.amazonaws.services.s3.AmazonS3;

public interface AwsService {

  AmazonS3 getS3Client();

  void setAllowIpAddressBucket(String bucketName, String ip);

  void setDenyIpAddressBucket(String bucketName, String ip);

  void setAllowDownloadFile(String bucketName, String linkFileKeyName);

  void setRejectDownloadFile(String bucketName, String linkFileKeyName);
}
