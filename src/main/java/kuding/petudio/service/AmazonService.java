package kuding.petudio.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import kuding.petudio.service.etc.callback.IoExceptionResolveTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class AmazonService{

    private final AmazonS3Client amazonS3Client;
    private final IoExceptionResolveTemplate ioExceptionResolveTemplate;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.s3.bucket.url}")
    private String bucketUrl;

    @Autowired
    public AmazonService(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
        ioExceptionResolveTemplate = new IoExceptionResolveTemplate();
    }

    public String getPictureS3Url(String storedName){
        return bucketUrl + "/" + storedName;
    }

    public void savePictureToS3(MultipartFile pictureFile, String storedPictureName) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(pictureFile.getContentType());
        metadata.setContentLength(pictureFile.getSize());
        ioExceptionResolveTemplate.execute(() -> {
            amazonS3Client.putObject(bucket, storedPictureName, pictureFile.getInputStream(), metadata);
            return null;
        });
    }
}
