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
        String selectedBackgrounds = "{\"구역 1\":[\"모자\",\"선글라스\"],\"구역 2\":[\"모자\"],\"구역 3\":[\"선글라스\"],\"구역 4\":[]}";
        String selectedItems = "{\"구역 1\":\"피라미드\",\"구역 2\":\"꽃밭\",\"구역 3\":\"수영\",\"구역 4\":\"우주\"}";


        String randomName1 = "KU_DOG";
        if(bundleRepository.findByRandomName(randomName1).isEmpty()){
            Long bundleId = bundleService.createBundle(BundleType.FOUR_AI_PICTURES, AnimalType.dog);
            bundleService.changeRandomName(bundleId, randomName1);
            ServiceReturnBundleDto bundle = bundleService.findBundleById(bundleId);
            List<Pair<Integer, String>> prompts = promptService.makePrompt(selectedItems, selectedBackgrounds, bundle.getRandomName(), bundle.getAnimalType());
            bundleService.addPromptsToBundle(bundleId, prompts);
        }

        String randomName2 = "KU_CAT";
        if(bundleRepository.findByRandomName(randomName2).isEmpty()){
            Long bundleId2 = bundleService.createBundle(BundleType.FOUR_AI_PICTURES, AnimalType.cat);
            bundleService.changeRandomName(bundleId2, randomName2);
            ServiceReturnBundleDto bundle = bundleService.findBundleById(bundleId2);
            List<Pair<Integer, String>> prompts = promptService.makePrompt(selectedItems, selectedBackgrounds, bundle.getRandomName(), bundle.getAnimalType());
            bundleService.addPromptsToBundle(bundleId2, prompts);
        }
    }
}
