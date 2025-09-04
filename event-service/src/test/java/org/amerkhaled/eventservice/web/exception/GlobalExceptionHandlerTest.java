package org.amerkhaled.eventservice.web.exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleEntityNotFound_returns404WithMessage() {
        // Arrange
        String msg = "Event not found";
        EntityNotFoundException ex = new EntityNotFoundException(msg);

        // Act
        ResponseEntity<ApiError> response = handler.handleEntityNotFound(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        ApiError body = response.getBody();
        assertEquals(HttpStatus.NOT_FOUND.value(), body.status());
        assertEquals(msg, body.message());
        assertNotNull(body.timestamp());
        assertTrue(body.timestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertNull(body.details());
    }

    @Test
    void handleValidationErrors_returns400WithFieldErrors() throws NoSuchMethodException {
        // Arrange: create a BindingResult with a field error
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.addError(new FieldError("target", "title", "must not be blank"));
        bindingResult.addError(new FieldError("target", "date", "must be in the future"));

        // Create a dummy MethodParameter (required by exception constructor)
        Method m = GlobalExceptionHandlerTest.class.getDeclaredMethod("dummyMethod", String.class);
        MethodParameter methodParameter = new MethodParameter(m, 0);

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        // Act
        ResponseEntity<ApiError> response = handler.handleValidationErrors(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiError body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.status());
        assertEquals("Validation failed", body.message());
        assertNotNull(body.timestamp());
        Map<String, String> details = body.details();
        assertNotNull(details);
        assertEquals(2, details.size());
        assertEquals("must not be blank", details.get("title"));
        assertEquals("must be in the future", details.get("date"));
    }

    @Test
    void handleGeneric_returns500WithGenericMessage() {
        // Arrange
        Exception ex = new Exception("Something bad");

        // Act
        ResponseEntity<ApiError> response = handler.handleGeneric(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ApiError body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.status());
        assertEquals("An unexpected error occurred", body.message());
        assertNotNull(body.timestamp());
        assertNull(body.details());
    }

    // dummy method to construct a MethodParameter for the validation exception
    @SuppressWarnings("unused")
    private static void dummyMethod(String arg) {
        // no-op
    }
}
