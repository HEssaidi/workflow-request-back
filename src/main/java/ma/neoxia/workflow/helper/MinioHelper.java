package ma.neoxia.workflow.helper;

import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class MinioHelper {

    private final MinioClient minioClient;


    public MinioHelper(MinioClient s3) {
        this.minioClient = s3;
    }

    public void uploadFile(String bucketName, String originalFilename, byte[] bytes) throws Exception {
        File file = upload(bucketName, originalFilename, bytes);
        minioClient.putObject(bucketName, originalFilename, new FileInputStream(file), new PutObjectOptions(file.length(), -1));
    }


    public byte[] downloadFile(String bucketName, String fileUrl) throws Exception {
        return getFile(bucketName, fileUrl);
    }

    public void deleteFile(String bucketName, String fileUrl) throws Exception {
        minioClient.removeObject(bucketName, fileUrl);
    }


    public List<String> listFiles(String bucketName) throws Exception {
        List<String> list = new LinkedList<>();
        Iterable<Result<Item>> buckets = minioClient.listObjects(bucketName);
        for (Result<Item> itemResult : buckets) {
            Item item = itemResult.get();  //we can get from item some metadata
            list.add(item.objectName());
        }
        return list;
    }


    public File upload(String bucketName, String name, byte[] content) throws Exception {
        File file = new File("/tmp/" + name);
        file.canWrite();
        file.canRead();
        FileOutputStream iofs = null;
        iofs = new FileOutputStream(file);
        iofs.write(content);
        return file;
    }

    public byte[] getFile(String bucketName, String key) throws Exception {
        InputStream stream = minioClient.getObject(bucketName, key);
        try {
            byte[] content = IOUtils.toByteArray(stream);
            stream.close();
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String fileUri(String bucketName, String objectName) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException, InvalidExpiresRangeException {
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("response-content-type", "application/pdf"); //very helpfull to preview pdf from browser
        return minioClient.getPresignedObjectUrl(Method.GET,bucketName,objectName,20000,reqParams);
    }
}
