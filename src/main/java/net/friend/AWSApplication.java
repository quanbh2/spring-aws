package net.friend;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import net.friend.service.AwsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class AWSApplication implements CommandLineRunner {

  @Value("${aws.bucketName}")
  String bucketName;

  @Autowired AwsService awsService;

  public static void main(String[] args) {
    SpringApplication.run(AWSApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {

    log.info(" STARTING ..... ");

    AmazonS3 s3Client = awsService.getS3Client();

//    AmazonS3 s3Client =
//        AmazonS3ClientBuilder.standard()
//            .withRegion(Regions.AP_SOUTHEAST_1)
//            .build();

    log.info("s3Client: {}", s3Client);
    log.info("before-Bucket-Policy: {}", s3Client.getBucketPolicy(bucketName).getPolicyText());

    // allow IP Bangkok 147.50.20.98 134.236.242.72
    // awsService.setAllowIpAddressBucket(bucketName, "147.50.20.0/24");

    log.info("after-Bucket-Policy: {}", s3Client.getBucketPolicy(bucketName).getPolicyText());


    //log.info("getObject: {}", s3Client.getObject(bucketName, "library-kelley-21-08-2016-x.vorachai.01lh7-21-8/vl05.mp4"));
  }
}
