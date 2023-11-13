package kuding.petudio.web;

import kuding.petudio.controller.BundleController;
import kuding.petudio.controller.FourCutsController;
import kuding.petudio.controller.dto.BaseDto;
import kuding.petudio.controller.dto.BundleReturnDto;
import kuding.petudio.controller.dto.PictureReturnDto;
import kuding.petudio.domain.Bundle;
import kuding.petudio.domain.PictureType;
import kuding.petudio.repository.BundleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/th")
@Slf4j
@RequiredArgsConstructor
public class ThController {

    private final BundleRepository bundleRepository;
    private final BundleController bundleController;
    private final FourCutsController fourCutsController;

    @GetMapping
    public String getUploadForm() {
        return "bundle/uploadForm";
    }

//    @PostMapping("/four-cuts")
//    public String createBundle(@RequestParam List<MultipartFile> beforePictures, RedirectAttributes redirectAttributes) {
//        BaseDto baseDto = fourCutsController.uploadBeforePicture(beforePictures);
//        BundleReturnDto bundle = (BundleReturnDto) baseDto.getData();
//        redirectAttributes.addAttribute("bundleId", bundle.getBundleId());
//        return "redirect:/th/after-upload";
//    }

    @GetMapping("/after-upload")
    public String afterUpload(@RequestParam Long bundleId, Model model) {
        model.addAttribute("bundleId", bundleId);
        return "bundle/afterUpload";
    }

    @GetMapping("/bundle/{bundleId}")
    public String getBundle(@PathVariable("bundleId") Long bundleId, Model model) {
        BaseDto base = bundleController.getBundle(bundleId);
        BundleReturnDto bundle = (BundleReturnDto) base.getData();
        List<PictureReturnDto> beforePictures = bundle.getPictureReturnDtos().stream()
                .filter(picture -> picture.getPictureType() == PictureType.BEFORE)
                .collect(Collectors.toList());
        List<PictureReturnDto> afterPictures = bundle.getPictureReturnDtos().stream()
                .filter(picture -> picture.getPictureType() == PictureType.AFTER)
                .collect(Collectors.toList());
        model.addAttribute("beforePictures", beforePictures);
        model.addAttribute("afterPictures", afterPictures);
        return "bundle/bundle";
    }

    @GetMapping("/bundle")
    public String getAllBundle(Model model) {
        List<Bundle> bundles = bundleRepository.findAll();
        model.addAttribute("bundleList", bundles);
        return "bundle/list";
    }
}
