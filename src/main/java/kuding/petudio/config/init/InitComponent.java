package kuding.petudio.config.init;

import kuding.petudio.domain.BundleType;
import kuding.petudio.domain.Prompt;
import kuding.petudio.etc.Pair;
import kuding.petudio.repository.BundleRepository;
import kuding.petudio.service.BundleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitComponent {

    private final BundleService bundleService;
    private final BundleRepository bundleRepository;

    @PostConstruct
    public void initKuBundle() {
        List<Pair<Integer, String>> promptList= new ArrayList<>();
        promptList.add(new Pair<>(1, "sample1"));
        promptList.add(new Pair<>(2, "sample2"));
        promptList.add(new Pair<>(3, "sample3"));
        promptList.add(new Pair<>(4, "sample4"));

        String randomName1 = "KU_DOG";
        if(bundleRepository.findByRandomName(randomName1).isEmpty()){
            Long bundleId = bundleService.createBundle(BundleType.FOUR_AI_PICTURES);
            bundleService.changeRandomName(bundleId, randomName1);
            bundleService.addPromptsToBundle(bundleId, promptList);
        }

        String randomName2 = "KU_CAT";
        if(bundleRepository.findByRandomName(randomName2).isEmpty()){
            Long bundleId2 = bundleService.createBundle(BundleType.FOUR_AI_PICTURES);
            bundleService.changeRandomName(bundleId2, randomName2);
            bundleService.addPromptsToBundle(bundleId2, promptList);
        }
    }
}
