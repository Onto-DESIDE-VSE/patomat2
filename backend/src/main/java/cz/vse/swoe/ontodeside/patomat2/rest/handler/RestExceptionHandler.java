package cz.vse.swoe.ontodeside.patomat2.rest.handler;

import cz.vse.swoe.ontodeside.patomat2.exception.AmbiguousOntologyException;
import cz.vse.swoe.ontodeside.patomat2.exception.IncompleteTransformationInputException;
import cz.vse.swoe.ontodeside.patomat2.exception.InvalidFileException;
import cz.vse.swoe.ontodeside.patomat2.exception.NotFoundException;
import cz.vse.swoe.ontodeside.patomat2.exception.OntologyReadException;
import cz.vse.swoe.ontodeside.patomat2.exception.PatOMat2Exception;
import cz.vse.swoe.ontodeside.patomat2.exception.ResourceFetchException;
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

    private static void logException(Throwable ex, HttpServletRequest request) {
        LOG.error("Exception caught when processing request to '{}'.", request.getRequestURI(), ex);
    }

    @ExceptionHandler(PatOMat2Exception.class)
    public ResponseEntity<ErrorInfo> patomat2Exception(PatOMat2Exception ex, HttpServletRequest request) {
        logException(ex, request);
        return new ResponseEntity<>(errorInfo(request, ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static ErrorInfo errorInfo(HttpServletRequest request, Throwable e) {
        return ErrorInfo.createWithMessage(request.getRequestURI(), e.getMessage());
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ErrorInfo> invalidFileException(InvalidFileException ex, HttpServletRequest request) {
        logException(ex, request);
        return new ResponseEntity<>(errorInfo(request, ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IncompleteTransformationInputException.class)
    public ResponseEntity<ErrorInfo> incompleteTransformationInputException(IncompleteTransformationInputException ex,
                                                                            HttpServletRequest request) {
        logException(ex, request);
        return new ResponseEntity<>(errorInfo(request, ex), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(OntologyReadException.class)
    public ResponseEntity<ErrorInfo> ontologyReadException(OntologyReadException ex, HttpServletRequest request) {
        logException(ex, request);
        return new ResponseEntity<>(errorInfo(request, ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AmbiguousOntologyException.class)
    public ResponseEntity<ErrorInfo> ambiguousOntologyException(AmbiguousOntologyException ex,
                                                                HttpServletRequest request) {
        logException(ex, request);
        return new ResponseEntity<>(errorInfo(request, ex), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> notFoundException() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorInfo> illegalStateException(IllegalStateException ex, HttpServletRequest request) {
        logException(ex, request);
        return new ResponseEntity<>(errorInfo(request, ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceFetchException.class)
    public ResponseEntity<ErrorInfo> resourceFetchException(ResourceFetchException ex, HttpServletRequest request) {
        logException(ex, request);
        return new ResponseEntity<>(errorInfo(request, ex), HttpStatus.CONFLICT);
    }
}
