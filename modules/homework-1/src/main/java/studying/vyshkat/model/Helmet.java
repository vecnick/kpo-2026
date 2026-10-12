package studying.vyshkat.model;

import lombok.Builder;
import studying.vyshkat.validation.DomainValidation;

/**
 * Safety helmet registered as non-transport inventory.
 *
 * @param name human-readable helmet name
 * @param inventoryNumber unique inventory number
 */
@Builder
public record Helmet(
        String name,
        String inventoryNumber
) implements Equipment {
    /** Validates helmet data. */
    public Helmet {
        DomainValidation.requireText(name, "Наименование");
        DomainValidation.requireText(inventoryNumber, "Инвентарный номер");
    }
}
