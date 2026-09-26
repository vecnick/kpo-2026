package studying.ioc.locator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import studying.exception.ApplicationErrorCode;
import studying.exception.ApplicationException;

/** Small registry that resolves services by their contract type. */
public final class ServiceLocator {
    /** Registered service implementations indexed by their contracts. */
    private final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

    /**
     * Registers an implementation for a service contract.
     *
     * @param contract service contract
     * @param implementation service implementation
     * @param <T> service type
     */
    public <T> void register(final Class<T> contract, final T implementation) {
        if (contract == null || implementation == null) {
            throw new ApplicationException(
                    ApplicationErrorCode.VALIDATION_ERROR,
                    "Контракт и реализация сервиса обязательны");
        }
        services.put(contract, implementation);
    }

    /**
     * Returns the implementation registered for a service contract.
     *
     * @param contract service contract
     * @param <T> service type
     * @return registered implementation
     */
    public <T> T getRequired(final Class<T> contract) {
        var service = services.get(contract);
        if (service == null) {
            throw new ApplicationException(
                    ApplicationErrorCode.SERVICE_NOT_FOUND,
                    "Сервис не зарегистрирован: " + contract.getName());
        }
        return contract.cast(service);
    }
}
