package kuding.petudio.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kuding.petudio.domain.type.AnimalType;
import kuding.petudio.etc.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class PromptService {
    Map<String, String> promptConverter = new ConcurrentHashMap<>();

    @PostConstruct
    public void promptNaneConverterConstruct() {
        promptConverter.put("모자", "hat");
        promptConverter.put("선글라스", "sun glass");
        promptConverter.put("수영", "swimming pool");
        promptConverter.put("우주", "galaxy");
        promptConverter.put("꽃밭", "flower garden");
        promptConverter.put("피라미드", "pyramid");
        promptConverter.put("동화", "fairy tale");
    }

    public List<Pair<Integer, String>> makePrompt(String selectedItems, String selectedBackground, String randomName, String animalType) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> objectSelectedBackground = objectMapper.readValue(selectedBackground, new TypeReference<Map<String, String>>() {
        });
        Map<String, List<String>> objectSelectedItems = objectMapper.readValue(selectedItems, new TypeReference<Map<String, List<String>>>() {
        });
        List<Pair<Integer, String>> promptMap = new ArrayList<>();


        for (int idx = 1; idx < 5; idx++) {
            String koreaBackground = objectSelectedBackground.get("구역 " + String.valueOf(idx));
            List<String> koreItems = objectSelectedItems.get("구역 " + String.valueOf(idx));

            StringBuilder sb = new StringBuilder();
            sb.append("photo of a ").append(randomName).append(" ").append(animalType);

            if (koreItems.size() != 0) {
                sb.append(" with ");
                koreItems.forEach(koreaItem -> {
                    String item = promptConverter.get(koreaItem);
                    sb.append(item).append(",");
                });
            }
            if(promptConverter.get(koreaBackground) != null){
                sb.append(" on the background of the ").append(promptConverter.get(koreaBackground));
            }else{
                sb.append(" elegant, luxury, clean, smooth, elegant, beautiful, highly detailed, sharp focus, studio photography,realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced ");
            }

            String prompt = sb.toString();
            promptMap.add(new Pair<>(Integer.valueOf(idx), prompt));
        }
        return promptMap;
    }
}
