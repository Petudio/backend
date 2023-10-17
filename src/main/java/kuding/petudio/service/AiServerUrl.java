package kuding.petudio.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * AiServerUrl에 대한 정보를 저장하는 component
 */
@Component
public class AiServerUrl {
    @Value("${petudio.ai.server.url}")
    private String AI_SERVER_BASE_URL;
    public String COPY_UPLOAD;

    @PostConstruct
    public void urlCreation() {
        this.COPY_UPLOAD = AI_SERVER_BASE_URL + "/copy/upload";
    }
}
