package kuding.petudio.controller;

import kuding.petudio.controller.dto.BaseDto;
import kuding.petudio.controller.dto.BundleReturnDto;
import kuding.petudio.controller.dto.DtoConverter;
import kuding.petudio.service.BundleService;
import kuding.petudio.service.dto.ServiceReturnBundleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bundle")
public class BundleController {

    private final BundleService bundleService;

    /**
     * Bundle 목록 조회
     *
     * @return 번들 리스트에 대한 JSON 형식 데이터
     */
    @GetMapping
    public BaseDto bundleList(@RequestParam int pageOffset, @RequestParam int pageSize) {
        List<ServiceReturnBundleDto> findRecentBundles = bundleService.findRecentPublicBundles(pageOffset, pageSize);

        List<BundleReturnDto> recentBundles = findRecentBundles.stream()
                .map(DtoConverter::serviceReturnBundleToBundleReturn)
                .collect(Collectors.toList());
        return new BaseDto(recentBundles);
    }

    /**
     * @param bundleId 커뮤니티 업로드 버튼을 눌렀을 시 실행. 실제 업로드가 아닌 기존에 DB에 저장해둔 사진을 공개 상태로 전환
     */
    @PostMapping("/new")
    public BaseDto uploadBundle(@RequestParam("bundleId") Long bundleId, @RequestParam("title") String title) {
        bundleService.changeToPublic(bundleId, title);
        return new BaseDto(null);
    }

    @PostMapping("/like/{bundleId}")
    public BaseDto addLikeCount(@PathVariable Long bundleId) {
        bundleService.addLikeCount(bundleId);
        return new BaseDto(null);
    }

    @GetMapping("/s3url/{bundleId}")
    public BaseDto getBundle(@PathVariable Long bundleId) {
        ServiceReturnBundleDto findBundle = bundleService.findBundleById(bundleId);
        BundleReturnDto bundleReturnDto = DtoConverter.serviceReturnBundleToBundleReturn(findBundle);
        return new BaseDto(bundleReturnDto);
    }

}

