package sk.insomnia.rowingRace.exceptions;

/**
 * Created by bobek on 6/28/2014.
 */
public class RowingRaceException extends Exception {

    private final ExceptionType exceptionType;

    public RowingRaceException(ExceptionType exceptionType){
        super();
        this.exceptionType = exceptionType;
    }

    public RowingRaceException(String message, ExceptionType exceptionType){
        super(message);
        this.exceptionType = exceptionType;
    }

    public ExceptionType getExceptionType(){
        return this.exceptionType;
    }
}
