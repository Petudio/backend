package kuding.petudio.config.init;

import com.fasterxml.jackson.core.JsonProcessingException;
import kuding.petudio.domain.type.AnimalType;
import kuding.petudio.domain.type.BundleType;
import kuding.petudio.etc.Pair;
import kuding.petudio.repository.BundleRepository;
import kuding.petudio.service.BundleService;
import kuding.petudio.service.PromptService;
import kuding.petudio.service.dto.ServiceReturnBundleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitComponent {

    private final BundleService bundleService;
    private final BundleRepository bundleRepository;
    private final PromptService promptService;


    @PostConstruct
    public void initKuBundle() throws JsonProcessingException {
        String randomName1 = "KU_DOG";
        if(bundleRepository.findByRandomName(randomName1).isEmpty()){
            Long bundleId = bundleService.createBundle(BundleType.FOUR_AI_PICTURES);
            bundleService.changeRandomName(bundleId, randomName1);
        }

        String randomName2 = "KU_CAT";
        if(bundleRepository.findByRandomName(randomName2).isEmpty()){
            Long bundleId2 = bundleService.createBundle(BundleType.FOUR_AI_PICTURES);
            bundleService.changeRandomName(bundleId2, randomName2);
        }
    }
}
