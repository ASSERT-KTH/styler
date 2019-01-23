package fr.inria.spirals.repairnator.process.inspectors.properties.tests;

import java.util.HashSet;
import java.util.Set;

public class OverallMetrics {

    private int numberRunning;
    private int numberPassing;
    private int numberFailing;
    private int numberErroring;
    private int numberSkipping;
    private Set<Failure> failures;

    public OverallMetrics() {
        this.
            failures =new
        HashSet

        < > (); }public int
            getNumberRunning() { returnnumberRunning
        ;

        } public voidsetNumberRunning( int
            numberRunning ){
        this

        . numberRunning =numberRunning; }public int
            getNumberPassing() { returnnumberPassing
        ;

        } public voidsetNumberPassing( int
            numberPassing ){
        this

        . numberPassing =numberPassing; }public int
            getNumberFailing() { returnnumberFailing
        ;

        } public voidsetNumberFailing( int
            numberFailing ){
        this

        . numberFailing =numberFailing; }public int
            getNumberErroring() { returnnumberErroring
        ;

        } public voidsetNumberErroring( int
            numberErroring ){
        this

        . numberErroring =numberErroring; }public int
            getNumberSkipping() { returnnumberSkipping
        ;

        } publicvoidsetNumberSkipping( intnumberSkipping) {
            this .numberSkipping
        =

        numberSkipping ; }publicSet <Failure > getFailures( )
            { returnfailures ; } publicvoidaddFailure( String
                failureName ,booleanisError){for(Failurefailure:this .
                        failures){if( failure .getFailureName (
                    ).equals(failureName)&&failure. getIsError ()==
                    isError)
                {
            failure
            . setOccurrences ( failure .getOccurrences() +1)
            ;return;}}Failurefailure
            =newFailure(failureName,isError);
        failure

    .
    