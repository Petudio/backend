package kuding.petudio.controller;

import kuding.petudio.service.AiPictureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/pictures")
public class PictureController {

    private final AiPictureService aiPictureService;

    @GetMapping("/download/{PictureId}")
    public void downloadPicture(@PathVariable Long PictureId) {
        //다운로드
    }
}
