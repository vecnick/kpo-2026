package vyshkat.service;

import vyshkat.model.Transport;

/**
 * Проверяет возможность принять транспорт в парк.
 */
@FunctionalInterface
public interface ServiceCenter {
    /**
     * Проверяет транспорт перед приёмкой на баланс.
     *
     * @param transport проверяемый транспорт
     * @return {@code true}, если транспорт прошёл техосмотр
     */
    boolean inspect(Transport transport);
}
