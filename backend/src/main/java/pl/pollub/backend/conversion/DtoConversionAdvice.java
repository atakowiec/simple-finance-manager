package pl.pollub.backend.conversion;

import lombok.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Collection;

/**
 * Controller advice for converting objects that implement DtoConvertible interface to DTOs.
 */
@ControllerAdvice
public class DtoConversionAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  @NonNull MethodParameter returnType,
                                  @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request,
                                  @NonNull ServerHttpResponse response) {

        if (body instanceof DtoConvertible<?> dtoConvertible) {
            return dtoConvertible.toDto();
        }

        if(body instanceof Collection<?> bodyCollection) {
            if(bodyCollection.isEmpty()) {
                return body;
            }

            Object firstElement = bodyCollection.iterator().next();
            if(firstElement instanceof DtoConvertible<?>) {
                return bodyCollection.stream()
                        .map(dtoConvertible -> ((DtoConvertible<?>) dtoConvertible).toDto())
                        .toList();
            }
        }

        return body;
    }
}
