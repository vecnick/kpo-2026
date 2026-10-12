package studying.vyshkat.model;

import lombok.Builder;
import studying.vyshkat.validation.DomainValidation;

/**
 * Non-electric bicycle registered in the rental fleet.
 *
 * @param name human-readable bicycle name
 * @param inventoryNumber unique inventory number
 * @param beginnerEase beginner-friendliness score
 * @param technicalCondition technical condition score
 */
@Builder
public record Bicycle(
        String name,
        String inventoryNumber,
        int beginnerEase,
        int technicalCondition
) implements RentalTransport {
    /** Validates bicycle data. */
    public Bicycle {
        DomainValidation.requireText(name, "Наименование");
        DomainValidation.requireText(inventoryNumber, "Инвентарный номер");
        DomainValidation.requireScore(
                beginnerEase, "Простота для новичка");
        DomainValidation.requireScore(
                technicalCondition, "Техническое состояние");
    }
}
