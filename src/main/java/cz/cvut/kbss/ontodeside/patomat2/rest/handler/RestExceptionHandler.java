package cz.cvut.kbss.ontodeside.patomat2.rest.handler;

import cz.cvut.kbss.ontodeside.patomat2.exception.InvalidFileException;
import cz.cvut.kbss.ontodeside.patomat2.exception.OntologyNotUploadedException;
import cz.cvut.kbss.ontodeside.patomat2.exception.OntologyReadException;
import cz.cvut.kbss.ontodeside.patomat2.exception.PatOMat2Exception;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RestExceptionHandler.class);

    private static void logException(PatOMat2Exception ex, HttpServletRequest request) {
        LOG.error("Exception caught when processing request to '{}'.", request.getRequestURI(), ex);
    }

    @ExceptionHandler(PatOMat2Exception.class)
    public ResponseEntity<ErrorInfo> patomat2Exception(PatOMat2Exception ex, HttpServletRequest request) {
        logException(ex, request);
        return new ResponseEntity<>(errorInfo(request, ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static ErrorInfo errorInfo(HttpServletRequest request, Throwable e) {
        return ErrorInfo.createWithMessage(e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ErrorInfo> invalidFileException(InvalidFileException ex, HttpServletRequest request) {
        logException(ex, request);
        return new ResponseEntity<>(errorInfo(request, ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OntologyNotUploadedException.class)
    public ResponseEntity<ErrorInfo> ontologyNotUploadedException(OntologyNotUploadedException ex, HttpServletRequest request) {
        logException(ex, request);
        return new ResponseEntity<>(errorInfo(request, ex), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(OntologyReadException.class)
    public ResponseEntity<ErrorInfo> ontologyReadException(OntologyReadException ex, HttpServletRequest request) {
        logException(ex, request);
        return new ResponseEntity<>(errorInfo(request, ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
