package kuding.petudio.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kuding.petudio.controller.dto.BaseDto;
import kuding.petudio.controller.dto.BundleReturnDto;
import kuding.petudio.controller.dto.DtoConverter;
import kuding.petudio.domain.BundleType;
import kuding.petudio.domain.PictureType;
import kuding.petudio.etc.Pair;
import kuding.petudio.etc.callback.CheckedExceptionConverterTemplate;
import kuding.petudio.service.BundleService;
import kuding.petudio.service.ColabServerCallService;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import kuding.petudio.service.dto.ServiceReturnBundleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/four-cuts")
public class FourCutsController {

    private final BundleService bundleService;
    private final ColabServerCallService colabServerCallService;
    private final CheckedExceptionConverterTemplate template = new CheckedExceptionConverterTemplate();

    /**
     * AI 생성 전 Before 이미지 업로드
     * 업로드 된 이미지를 AI를 통해 변환 후 DB 저장 -> aiPictureService
     */
    @PostMapping("/upload")
    public BaseDto uploadBeforePicture(@RequestPart("beforePictures") List<MultipartFile> beforePictures,
                                       @RequestParam("selectedItems") String selectedItems,
                                       @RequestParam("selectedBackground") String selectedBackground
                                       ) throws JsonProcessingException {

        System.out.println("selectedItems = " + selectedItems);
        System.out.println("selectedBackground = " + selectedBackground);

        List<Pair<Integer, String>> promptList = makePrompt(selectedItems, selectedBackground);
        System.out.println("promptList = " + promptList);

        List<ServiceParamPictureDto> pictureDtos = new ArrayList<>();
        for (MultipartFile beforePicture : beforePictures) {
            pictureDtos.add(new ServiceParamPictureDto(beforePicture.getOriginalFilename(), template.execute(beforePicture::getBytes) ,PictureType.BEFORE, -1));
        }
        Long bundleId = bundleService.createBundle(BundleType.FOUR_AI_PICTURES);
        bundleService.addPicturesToBundle(bundleId, pictureDtos);
        bundleService.addPromptsToBundle(bundleId, promptList);
        colabServerCallService.sendBeforePicturesToAiServer(bundleId);

        ServiceReturnBundleDto bundleDto = bundleService.findBundleById(bundleId);
        BundleReturnDto bundle = DtoConverter.serviceReturnBundleToBundleReturn(bundleDto);
        return new BaseDto(bundle);
    }

    @GetMapping("/generate")
    public BaseDto generateAfterPictures(Long bundleId) {

        if (!colabServerCallService.checkTrainingComplete(bundleId)) {
            return new BaseDto("Training is not yet complete");
        }
        colabServerCallService.generateAfterPicture(bundleId);
        ServiceReturnBundleDto bundle = bundleService.findBundleById(bundleId);
        return new BaseDto(bundle);
    }

    private List<Pair<Integer, String>> makePrompt(String selectedItems, String selectedBackground) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> objectSelectedBackground = objectMapper.readValue(selectedBackground, new TypeReference<Map<String, String>>() {
        });
        Map<String, List<String>> objectSelectedItems = objectMapper.readValue(selectedItems, new TypeReference<Map<String, List<String>>>() {
        });
        List<Pair<Integer, String>> promptMap = new ArrayList<>();


        for (int idx = 1; idx < 5; idx++) {
            String backgrounds = objectSelectedBackground.get("구역 " + String.valueOf(idx));
            List<String> items = objectSelectedItems.get("구역 " + String.valueOf(idx));
            String p = backgrounds+",";
            for (String item : items) {
                p += item;
            }
            System.out.println("p = " + p);
            promptMap.add(new Pair<>(Integer.valueOf(idx),p));
        }
        return promptMap;
    }
}



