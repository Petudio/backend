package kuding.petudio.config.init;

import com.fasterxml.jackson.core.JsonProcessingException;
import kuding.petudio.domain.type.BundleType;
import kuding.petudio.repository.BundleRepository;
import kuding.petudio.service.BundleService;
import kuding.petudio.service.PromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class InitComponent {

    private final BundleService bundleService;
    private final BundleRepository bundleRepository;
    private final PromptService promptService;


    @PostConstruct
    public void initKuBundle() throws JsonProcessingException {
        String randomName1 = "KU";
        if(bundleRepository.findByRandomName(randomName1).isEmpty()){
            Long bundleId = bundleService.createBundle(BundleType.FOUR_AI_PICTURES);
            bundleService.changeRandomName(bundleId, randomName1);
        }

        String randomName2 = "KU1";
        if(bundleRepository.findByRandomName(randomName2).isEmpty()){
            Long bundleId2 = bundleService.createBundle(BundleType.FOUR_AI_PICTURES);
            bundleService.changeRandomName(bundleId2, randomName2);
        }
    }
}
