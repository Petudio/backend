package kuding.petudio.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import kuding.petudio.domain.Bundle;
import kuding.petudio.domain.BundleType;
import kuding.petudio.domain.Picture;
import kuding.petudio.repository.BundleRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class BundleService {

    private final BundleRepository bundleRepository;
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Autowired
    public BundleService(BundleRepository bundleRepository, AmazonS3Client amazonS3Client) {
        this.bundleRepository = bundleRepository;
        this.amazonS3Client = amazonS3Client;
    }

    /**
     * 넘겨받은 pictures를 묶어서 하나의 bundle로 DB에 저장
     * pictures를 S3에 저장
     * @param pictureServiceDtos
     * @param bundleType
     * @return bundle_id
     */
    @Transactional
    public Long saveBundleBindingPictures(List<PictureServiceDto> pictureServiceDtos, BundleType bundleType) throws IOException {
        Bundle bundle = new Bundle(bundleType);
        List<pairPictureAndPictureServiceDto> pairs = new ArrayList<>();

        //DB에 저장
        for (PictureServiceDto pictureServiceDto : pictureServiceDtos) {
            String storedName = getStoredName(pictureServiceDto.getOriginalName());
            Picture picture = new Picture(pictureServiceDto.getOriginalName(), storedName, pictureServiceDto.getPictureType());
            pairs.add(new pairPictureAndPictureServiceDto(picture, pictureServiceDto));
            bundle.addPicture(picture);
        }
        Bundle saveBundle = bundleRepository.save(bundle);

        //s3에 저장
        for (pairPictureAndPictureServiceDto pair : pairs) {
            MultipartFile pictureFile = pair.getPictureServiceDto().getPictureFile();
            String storedName = pair.getPicture().getStoredName();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(pictureFile.getContentType());
            metadata.setContentLength(pictureFile.getSize());
            amazonS3Client.putObject(bucket, storedName, pictureFile.getInputStream(), metadata);
        }

        return saveBundle.getId();
    }

    private String getStoredName(String originalName) {
        String uuid = UUID.randomUUID().toString();
        int pos = originalName.lastIndexOf(".");
        if (pos == -1) {
            return uuid;
        }
        String ext = originalName.substring(pos + 1);
        String storedName = uuid + "." + ext;
        return storedName;
    }

    @Data
    static class pairPictureAndPictureServiceDto{
        private final Picture picture;
        private final PictureServiceDto pictureServiceDto;
    }

}
