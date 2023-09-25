package kuding.petudio.controller;

import kuding.petudio.domain.Picture;
import kuding.petudio.service.PictureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/pictures")
public class PictureController {

    private final PictureService pictureService;

    @PostMapping("/new")
    public void uploadPicture() {
        // 사진 업로드
    }

    @GetMapping("/{pictureId}")
    public void getPicture() {
        // 업로드 된 사진 불러오기
    }

    @GetMapping("/group/{pictureId}")
    public void getTwoTypePicture() {
        //Before, After 두 타입 사진 모두 조회
    }

    @GetMapping("/download/{PictureId}")
    public void downloadPicture() {
        //다운로드
    }
}
