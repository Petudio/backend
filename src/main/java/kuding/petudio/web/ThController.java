package kuding.petudio.web;

import kuding.petudio.controller.BundleController;
import kuding.petudio.controller.dto.BaseDto;
import kuding.petudio.controller.dto.BundleReturnDto;
import kuding.petudio.controller.dto.PictureReturnDto;
import kuding.petudio.domain.Bundle;
import kuding.petudio.domain.type.PictureType;
import kuding.petudio.etc.Pair;
import kuding.petudio.etc.callback.CheckedExceptionConverterTemplate;
import kuding.petudio.repository.BundleRepository;
import kuding.petudio.service.BundleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/th")
@Slf4j
@RequiredArgsConstructor
public class ThController {

    private final BundleRepository bundleRepository;
    private final BundleController bundleController;
//    private final FourCutsController fourCutsController;
    private final BundleService bundleService;
    private final CheckedExceptionConverterTemplate template = new CheckedExceptionConverterTemplate();

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
        model.addAttribute("bundleId", bundleId);
        model.addAttribute("beforePictures", beforePictures);
        model.addAttribute("afterPictures", afterPictures);
        return "bundle/bundle";
    }

    @GetMapping("/bundle")
    public String getAllBundle(Model model) {
        List<Bundle> bundles = bundleRepository.findAll(Sort.by(Sort.Direction.DESC, "createdDate"));
        model.addAttribute("bundleList", bundles);
        return "bundle/list";
    }

    @GetMapping("/bundle/{bundleId}/beforeImages")
    public ResponseEntity<byte[]> getImages(@PathVariable("bundleId") Long bundleId) {
        Bundle bundle = bundleRepository.findById(bundleId).orElseThrow();
        List<Pair<String, byte[]>> pictures = bundleService.getBeforeImageByteArray(bundleId);
        byte[] zipBytes = createZipBytes(pictures);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", bundle.getRandomName() + ".zip");

        return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);
    }

    private byte[] createZipBytes(List<Pair<String, byte[]>> pictures)  {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
        pictures.forEach(picture -> {
            ZipEntry zipEntry = new ZipEntry(picture.getFirst());
            try {
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(picture.getSecond());
                zipOutputStream.closeEntry();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });

        try {
            zipOutputStream.close();
        } catch (IOException e) {
            new RuntimeException(e);
        }

        return byteArrayOutputStream.toByteArray();
    }
}
