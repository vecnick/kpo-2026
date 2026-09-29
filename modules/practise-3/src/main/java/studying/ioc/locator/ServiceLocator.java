package studying.ioc.locator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import studying.exception.ApplicationErrorCode;
import studying.exception.ApplicationException;

public final class ServiceLocator {
    private final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

    public <T> void register(final Class<T> contract, final T implementation) {
        if (contract == null || implementation == null) {
            throw new ApplicationException(
                    ApplicationErrorCode.VALIDATION_ERROR,
                    "Контракт и реализация сервиса обязательны");
        }
        services.put(contract, implementation);
    }

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
