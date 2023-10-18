package kuding.petudio.domain;

/**
 * AI 모델중에서 어떠한 AI 모델을 사용했는지 기록
 */
public enum BundleType {
    ANIMAL_TO_HUMAN("/animal-to-human/upload"),
    FOUR_AI_PICTURES("/four-cuts/upload"),
    COPY("/copy/upload"),
    SERVICE4("/service4/upload");

    private final String aiServerPathUrl;

    BundleType(String aiServerPathUrl) {
        this.aiServerPathUrl = aiServerPathUrl;
    }

    public String getAiServerPathUrl() {
        return this.aiServerPathUrl;
    }
}
