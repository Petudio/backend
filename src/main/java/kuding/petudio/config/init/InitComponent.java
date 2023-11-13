package kuding.petudio.config.init;

import kuding.petudio.domain.BundleType;
import kuding.petudio.domain.Prompt;
import kuding.petudio.etc.Pair;
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

    @PostConstruct
    public void initSksBundle() {
        Long bundleId = bundleService.createBundle(BundleType.FOUR_AI_PICTURES);
        bundleService.changeRandomName(bundleId, "SKS");
        List<Pair<Integer, String>> promptList= new ArrayList<>();
        promptList.add(new Pair<>(1, "sample1"));
        promptList.add(new Pair<>(2, "sample2"));
        bundleService.addPromptsToBundle(bundleId, promptList);
    }
}
