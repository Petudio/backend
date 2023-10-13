package kuding.petudio.etc;

import lombok.Data;

@Data
public class Pair<S, T> {
    private final S first;
    private final T second;
}
