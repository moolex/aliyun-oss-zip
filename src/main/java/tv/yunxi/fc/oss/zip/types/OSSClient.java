package tv.yunxi.fc.oss.zip.types;

import com.aliyun.fc.runtime.Credentials;
import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;

/**
 * @author moyo
 */
public class OSSClient {
    private String region;
    private Credentials credentials;

    public OSSClient(String region, Credentials credentials) {
        this.region = region;
        this.credentials = credentials;
    }

    public OSS create() {
        CredentialsProvider creds = new DefaultCredentialProvider(
            credentials.getAccessKeyId(),
            credentials.getAccessKeySecret(),
            credentials.getSecurityToken()
        );

        return new com.aliyun.oss.OSSClient(
            region,
            creds,
            new ClientConfiguration()
        );
    }
}
