package ma.neoxia.workflow.config;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${s3.url}")
    private String s3Url;
    @Value("${s3.accessKey}")
    private String accessKey;
    @Value("${s3.secretKey}")
    private String secretKey;

    @Bean
    public MinioClient MinioClient() throws InvalidPortException, InvalidEndpointException {
        return new MinioClient(s3Url, accessKey, secretKey);
    }


}
