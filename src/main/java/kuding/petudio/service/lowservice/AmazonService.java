package kuding.petudio.service.lowservice;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import kuding.petudio.domain.Bundle;
import kuding.petudio.domain.Picture;
import kuding.petudio.service.dto.ServiceReturnBundleDto;
import kuding.petudio.service.dto.ServiceReturnPictureDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AmazonService {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Autowired
    public AmazonService(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    public void savePicturesToS3(List<PairPictureAndPictureServiceDto> pairs) throws IOException {
        for (PairPictureAndPictureServiceDto pair : pairs) {
            MultipartFile pictureFile = pair.getServiceParamPictureDto().getPictureFile();
            String storedName = pair.getPicture().getStoredName();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(pictureFile.getContentType());
            metadata.setContentLength(pictureFile.getSize());
            amazonS3Client.putObject(bucket, storedName, pictureFile.getInputStream(), metadata);
        }
    }

    public List<ServiceReturnBundleDto> getAllPicturesInAllBundles(List<Bundle> bundles) throws IOException {
        List<ServiceReturnBundleDto> serviceReturnBundleDtos = new ArrayList<>();
        for (Bundle bundle : bundles) {
            ServiceReturnBundleDto serviceReturnBundleDto = new ServiceReturnBundleDto(bundle.getBundleType());
            List<Picture> pictures = bundle.getPictures();
            for (Picture picture : pictures) {
                S3Object s3Object = amazonS3Client.getObject(bucket, picture.getStoredName());
                S3ObjectInputStream inputStream = s3Object.getObjectContent();
                byte[] bytes = inputStream.readAllBytes();
                ServiceReturnPictureDto serviceReturnPictureDto = new ServiceReturnPictureDto(picture.getOriginalName(), bytes, picture.getPictureType());
                serviceReturnBundleDto.getPictures().add(serviceReturnPictureDto);
            }
            serviceReturnBundleDtos.add(serviceReturnBundleDto);
        }
        return serviceReturnBundleDtos;
    }
}
