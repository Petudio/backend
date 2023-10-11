package kuding.petudio.config.init;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

@Component
public class InitLocalStorage {

    @Value("${local.repository.baseurl}")
    private String baseUrl;

    @PostConstruct
    public void initLocalStorage() {
        File folder = new File(baseUrl);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

}
