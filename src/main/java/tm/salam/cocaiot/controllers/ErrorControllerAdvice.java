package tm.salam.cocaiot.controllers;

import org.hibernate.exception.GenericJDBCException;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tm.salam.cocaiot.exceptions.ApiError;
import tm.salam.cocaiot.exceptions.ResourceNotFoundExceptions;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ErrorControllerAdvice extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        List<String> errors = new ArrayList<String>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
        return handleExceptionInternal(
                ex, apiError, headers, apiError.getStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";

        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
        return new ResponseEntity<Object>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        List<String> errors = new ArrayList<String>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " " +
                    violation.getPropertyPath() + ": " + violation.getMessage());
        }

        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
        return new ResponseEntity<Object>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        String error =
                ex.getName() + " should be of type " + ex.getRequiredType().getName();

        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
        return new ResponseEntity<Object>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler(ResourceNotFoundExceptions.class)
    public ResponseEntity<Object> resourceNotFoundException(ResourceNotFoundExceptions ex, WebRequest request) {

        ApiError message = new ApiError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                ex.getLocalizedMessage());


        return new ResponseEntity<Object>(message, new HttpHeaders(), message.getStatus());
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Object> multipartException(MultipartException multipartException, WebRequest webRequest){

        ApiError message=new ApiError(
                HttpStatus.MULTI_STATUS,
                multipartException.getMessage(),
                multipartException.getLocalizedMessage()
        );

        return new ResponseEntity<Object>(message, new HttpHeaders(), message.getStatus());
    }

    @ExceptionHandler(RequestRejectedException.class)
    public ResponseEntity<Object> requestRejectedException(RequestRejectedException requestRejectedException, WebRequest webRequest){

        ApiError message=new ApiError(
                HttpStatus.BAD_REQUEST,
                requestRejectedException.getMessage(),
                requestRejectedException.getLocalizedMessage()
        );

        return new ResponseEntity<Object>(message, new HttpHeaders(), message.getStatus());
    }

    @ExceptionHandler(PSQLException.class)
    public ResponseEntity psqlException(PSQLException psqlException, NativeWebRequest request) {

        ApiError message=new ApiError(
                HttpStatus.FAILED_DEPENDENCY,
                psqlException.getMessage(),
                psqlException.getLocalizedMessage()
        );

        return new ResponseEntity<Object>(message, new HttpHeaders(), message.getStatus());
    }

    @ExceptionHandler(GenericJDBCException.class)
    public ResponseEntity genericJDBCException(GenericJDBCException genericJDBCException, NativeWebRequest request) {

        ApiError message=new ApiError(
                HttpStatus.EXPECTATION_FAILED,
                genericJDBCException.getMessage(),
                genericJDBCException.getLocalizedMessage()
        );

        return new ResponseEntity<Object>(message, new HttpHeaders(), message.getStatus());
    }

    @ExceptionHandler(JpaSystemException.class)
    public ResponseEntity jpaSystemException(JpaSystemException jpaSystemException, NativeWebRequest request) {

        ApiError message=new ApiError(
                HttpStatus.EXPECTATION_FAILED,
                jpaSystemException.getMessage(),
                jpaSystemException.getLocalizedMessage()
        );

        return new ResponseEntity<Object>(message, new HttpHeaders(), message.getStatus());
    }

}
