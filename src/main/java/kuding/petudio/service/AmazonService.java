package kuding.petudio.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import kuding.petudio.etc.callback.CheckedExceptionConverterTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

@Slf4j
@Service
public class AmazonService{

    private final AmazonS3Client amazonS3Client;
    private final CheckedExceptionConverterTemplate checkedExceptionConverterTemplate;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Autowired
    public AmazonService(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
        checkedExceptionConverterTemplate = new CheckedExceptionConverterTemplate();
    }

    public byte[] getPictureBytesFromS3(String storedName) {
        S3Object object = amazonS3Client.getObject(bucket, storedName);
        S3ObjectInputStream is = object.getObjectContent();
        return checkedExceptionConverterTemplate.execute(is::readAllBytes);
    }

    public String getPictureS3Url(String storedName){
        return amazonS3Client.getUrl(bucket, storedName).toString();
    }

    public void saveByteArrayToS3(byte[] pictureFileByteArray, String storedPictureName) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(extractExt(storedPictureName));
        checkedExceptionConverterTemplate.execute(() -> {
            amazonS3Client.putObject(bucket, storedPictureName, new ByteArrayInputStream(pictureFileByteArray), metadata);
            return null;
        });
    }

    private String extractExt(String filename) {
        int idx = filename.lastIndexOf('.');
        if(idx == -1){
            return "";
        }else{
            return filename.substring(idx + 1);
        }
    }
}
