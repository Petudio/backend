package kuding.petudio.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import kuding.petudio.service.etc.callback.ExceptionResolveTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Slf4j
@Service
public class AmazonService{

    private final AmazonS3Client amazonS3Client;
    private final ExceptionResolveTemplate exceptionResolveTemplate;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Autowired
    public AmazonService(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
        exceptionResolveTemplate = new ExceptionResolveTemplate();
    }

    public byte[] getPictureBytesFromS3(String storedName) {
        S3Object object = amazonS3Client.getObject(bucket, storedName);
        S3ObjectInputStream is = object.getObjectContent();
        return exceptionResolveTemplate.execute(is::readAllBytes);
    }

    public String getPictureS3Url(String storedName){
        return amazonS3Client.getUrl(bucket, storedName).toString();
    }

    public void saveMultipartFileToS3(MultipartFile pictureFile, String storedPictureName) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(pictureFile.getContentType());
        metadata.setContentLength(pictureFile.getSize());
        exceptionResolveTemplate.execute(() -> {
            amazonS3Client.putObject(bucket, storedPictureName, pictureFile.getInputStream(), metadata);
            return null;
        });
    }

    public void saveJavaFileToS3(File pictureFile, String storedPictureName) {
        amazonS3Client.putObject(bucket, storedPictureName, pictureFile);
    }
}
