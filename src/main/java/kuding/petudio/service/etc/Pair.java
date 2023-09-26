package kuding.petudio.service.etc;

import lombok.Data;

@Data
public class Pair<S, T> {
    private final S first;
    private final T second;
}
