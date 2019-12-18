package net.friend.service.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.policy.Condition;
import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Principal;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.Statement.Effect;
import com.amazonaws.auth.policy.actions.S3Actions;
import com.amazonaws.auth.policy.conditions.IpAddressCondition;
import com.amazonaws.auth.policy.conditions.IpAddressCondition.IpAddressComparisonType;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.friend.service.AwsService;
import net.friend.util.UtilMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AwsServiceImpl implements AwsService {

  @Value("${aws.accessKey}")
  String accessKey;

  @Value("${aws.secretKey}")
  String secretKey;

  @Override
  public AmazonS3 getS3Client() {

    AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

    AmazonS3 s3Client =
        AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.AP_SOUTHEAST_1)
            .build();

    return s3Client;
  }

  @Override
  public void setAllowIpAddressBucket(String bucketName, String ip) {

    Condition condition = createDenyIpAddressCondition(ip);

    String policyJson = createJsonBucketPolicy(bucketName, condition);

    AmazonS3 s3Client = getS3Client();

    log.info(" bucketName: {}", bucketName);
    log.info(" policyJson: {}", policyJson);
    s3Client.setBucketPolicy(bucketName, policyJson);
    log.info("DONE setAllowIpAddressBucket");
  }

  @Override
  public void setDenyIpAddressBucket(String bucketName, String ip) {

    Condition condition = createDenyIpAddressCondition(ip);

    String policyJson = createJsonBucketPolicy(bucketName, condition);

    AmazonS3 s3Client = getS3Client();

    s3Client.setBucketPolicy(bucketName, policyJson);
    log.info("DONE setDenyIpAddressBucket");
  }

  @Override
  public void setAllowDownloadFile(String bucketName, String linkFileKeyName) {
    List<String> keyNameList = UtilMethod.getListKeyNameFromFile(linkFileKeyName);
    setObjectAcl(bucketName, keyNameList, CannedAccessControlList.PublicReadWrite);

    log.info("DONE setAllowDownloadFile");
  }

  @Override
  public void setRejectDownloadFile(String bucketName, String linkFileKeyName) {
    List<String> keyNameList = UtilMethod.getListKeyNameFromFile(linkFileKeyName);
    setObjectAcl(bucketName, keyNameList, CannedAccessControlList.Private);

    log.info("DONE setRejectDownloadFile");
  }

  private void setObjectAcl(
      String bucketName, List<String> keyNameList, CannedAccessControlList acl) {

    AmazonS3 s3Client = getS3Client();

    keyNameList.forEach(
        key -> {
          s3Client.setObjectAcl(bucketName, key, acl);
          log.info("DONE - {}", key);
        });

    //      // Get the existing object ACL that we want to modify.
    //      AccessControlList acl = s3Client.getObjectAcl(bucketName, keyName);
    //
    //      // Clear the existing list of grants.
    //      acl.getGrantsAsList().clear();
    //
    //      // Grant a sample set of permissions, using the existing ACL owner for Full Control
    // permissions.
    //      acl.grantPermission(new CanonicalGrantee(CannedAccessControlList.PublicRead),
    // Permission.FullControl);
    //
    //      //acl.grantPermission(new EmailAddressGrantee(emailGrantee), Permission.WriteAcp);
    //
    //      // Save the modified ACL back to the object.
    //      s3Client.setObjectAcl(bucketName, keyName, acl);
  }

  // Private Method

  private String createJsonBucketPolicy(
      String bucketName, Condition... conditions) {
    Policy bucket_policy =
        new Policy()
            .withStatements(
                new Statement(Effect.Deny)
                    .withPrincipals(Principal.AllUsers)
                    .withActions(S3Actions.GetObject)
                    .withResources(new Resource("arn:aws:s3:::" + bucketName+"/*"))
                    .withConditions(conditions));
    return bucket_policy.toJson();
  }

  private Condition createAllowIpAddressCondition(String ip) {
    return new IpAddressCondition(IpAddressComparisonType.IpAddress, ip);
  }

  private Condition createDenyIpAddressCondition(String ip) {
    return new IpAddressCondition(IpAddressComparisonType.NotIpAddress, ip);
  }
}
