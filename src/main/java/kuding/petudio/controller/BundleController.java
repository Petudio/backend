package kuding.petudio.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/bundle")
public class BundleController {

    @GetMapping
    public void bundleList() {
        // 모든 게시글 조회
    }

    @PostMapping("/new")
    public void uploadBundle() {
        // 게시글 업로드
    }

    @PostMapping("/{bundleId}/like")
    public void addLikeCount() {
        // 좋아요 눌렀을 때
    }

}
