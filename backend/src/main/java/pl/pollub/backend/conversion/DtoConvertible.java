package pl.pollub.backend.conversion;

/**
 * Interface for entities that will be automatically converted to DTOs when returned from the controller.
 *
 * @param <T> DTO type
 */
public interface DtoConvertible<T> {
    T toDto();
}
