package kuding.petudio.service;

import kuding.petudio.domain.Bundle;
import kuding.petudio.domain.BundleType;
import kuding.petudio.domain.Picture;
import kuding.petudio.repository.BundleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class BundleService {

    private final BundleRepository bundleRepository;

    @Autowired
    public BundleService(BundleRepository bundleRepository) {
        this.bundleRepository = bundleRepository;
    }

    /**
     * 넘겨받은 pictures를 묶어서 하나의 bundle로 DB에 저장
     * @param pictureServiceDtos
     * @param bundleType
     * @return bundle_id
     */
    @Transactional
    public Long saveBundle(List<PictureServiceDto> pictureServiceDtos, BundleType bundleType) {
        Bundle bundle = new Bundle(bundleType);
        for (PictureServiceDto pictureServiceDto : pictureServiceDtos) {
            Picture picture = new Picture(pictureServiceDto.getOriginalName(), pictureServiceDto.getPath(), pictureServiceDto.getPictureType());
            bundle.addPicture(picture);
        }
        Bundle saveBundle = bundleRepository.save(bundle);
        return saveBundle.getId();
    }

}
