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
    List<Pair<String, String>> randomPrompt = new ArrayList<>();

    public List<Pair<Integer, String>> makePrompt(String selectedItems, String selectedBackground, String randomName, String animalType) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> objectSelectedBackground = objectMapper.readValue(selectedBackground, new TypeReference<Map<String, String>>() {
        });
        Map<String, List<String>> objectSelectedItems = objectMapper.readValue(selectedItems, new TypeReference<Map<String, List<String>>>() {
        });

        List<Pair<Integer, String>> promptList = new ArrayList<>();

        for (int idx = 1; idx < 5; idx++) {
            String koreaBackground = objectSelectedBackground.get("구역 " + String.valueOf(idx));
            List<String> koreItems = objectSelectedItems.get("구역 " + String.valueOf(idx));
            if(koreaBackground.equals("랜덤")){
                int i = (int)(Math.random() * randomPrompt.size());
                StringBuilder sb = new StringBuilder();
                sb.append(randomPrompt.get(i).getFirst()).append(randomName).append(randomPrompt.get(i).getSecond());
                String prompt = sb.toString();
                promptList.add(new Pair<>(idx, prompt));
            }else{
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
                promptList.add(new Pair<>(idx, prompt));
            }
        }
        return promptList;
    }

    @PostConstruct
    public void promptNameConverterInit() {
        promptConverter.put("모자", "hat");
        promptConverter.put("선글라스", "sun glass");
        promptConverter.put("수영", "swimming pool");
        promptConverter.put("우주", "galaxy");
        promptConverter.put("꽃밭", "flower garden");
        promptConverter.put("피라미드", "pyramid");
        promptConverter.put("동화", "fairy tale");
    }

    @PostConstruct
    public void randomPromptInit() {
        randomPrompt.add(new Pair<>("photo of a ", " fireman, extinguishes fire, sausages, sausage, hyper-detailed, hyper-realism, sharp frame, cinematic, (background action-packed), lit"));
        randomPrompt.add(new Pair<>("abstract expressionist painting of an award-winning photo of a ", " hyperrealistic <lora:xl_more_art-full_v1:0.7> <lora:zhibi-sdxl:1> zhibi, energetic brushwork, bold colors, abstract forms, expressive, emotional"));
        randomPrompt.add(new Pair<>("Astral Aura, an award-winning photo of a ", ", hyperrealistic <lora:xl_more_art-full_v1:0.5>, astral, colorful aura, vibrant energy"));
        randomPrompt.add(new Pair<>("photo of a ", " athletes, sausage race, olympic games, hyper-detailed, hyper-realism, sharp frame, cinematic, (background action-packed), lit <lora:lit:1>"));
        randomPrompt.add(new Pair<>("photo of a ", " in Egyptian robes, pharaoh cat, on the background of the pyramids), hyper-detailed, hyper-realism, sharp shot, cinematic, background action-packed, <lora:xl_more_art-full_v1:1>, <lora:lit:0.5>, effect bokeh"));
        randomPrompt.add(new Pair<>("photo of a ", " in a kimono with a katana in his hands, against the background of sakura, sakura petals), hyper-detailed, hyper-realism, sharp shot, cinematic, background action-packed, <lora:xl_more_art-full_v1:1>, <lora:lit:0.5>, effect bokeh"));
        randomPrompt.add(new Pair<>("photo of a ", ",with hat,with sunglass, elegant, luxury, clean, smooth, elegant, beautiful, highly detailed, sharp focus, studio photography, realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced"));
        randomPrompt.add(new Pair<>("photo of a ", " holding strawberry, elegant, luxury, clean, smooth, elegant, beautiful, highly detailed, sharp focus, studio photography, xf iq 4, 1 5 0 mp, 5 0 mm, iso 2 0 0, 1 / 1 6 0 s, realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced"));
        randomPrompt.add(new Pair<>("photo of a ", " eating a hat, elegant, luxury, clean, smooth, elegant, beautiful, highly detailed, sharp focus, studio photography, xf iq 4, 1 5 0 mp, 5 0 mm, iso 2 0 0, 1 / 1 6 0 s, realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced"));
        randomPrompt.add(new Pair<>("photo of a ", " wearing a karate gi, elegant, luxury, clean, smooth,  beautiful, highly detailed, sharp focus, studio photography,  realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced"));
        randomPrompt.add(new Pair<>("photo of a ", "  wearing a karate gi, elegant, luxury, clean, smooth,  beautiful, highly detailed, sharp focus, studio photography,  realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced"));
        randomPrompt.add(new Pair<>("photo of a ", "  perched gracefully on top of a stack of old books, with a dimly lit vintage study room in the background, elegant, luxury, clean, smooth,  beautiful, highly detailed, sharp focus, studio photography,  realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced"));
        randomPrompt.add(new Pair<>("photo of a ", "   lazily stretching on a sunny windowsill, with houseplants around and a view of a serene garden outside, elegant, luxury, clean, smooth,  beautiful, highly detailed, sharp focus, studio photography,  realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced"));
        randomPrompt.add(new Pair<>("photo of a ", "  leaping through the air, trying to catch a fluttering butterfly in a sunny flower garden, elegant, luxury, clean, smooth,  beautiful, highly detailed, sharp focus, studio photography,  realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced"));
    }
}
