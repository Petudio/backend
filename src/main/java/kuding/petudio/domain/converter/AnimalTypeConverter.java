package kuding.petudio.domain.converter;

import kuding.petudio.domain.type.AnimalType;

public class AnimalTypeConverter {
    public static AnimalType StringToAnimalType(String s) {
        if (s.equals("dog")) {
            return AnimalType.dog;
        }
        return AnimalType.cat;
    }
}
