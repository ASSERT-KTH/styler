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
        this.failures = new HashSet<>();
    }

    public int getNumberRunning() {
        return numberRunning;
    }

    public void setNumberRunning( intnumberRunning )
        {this. numberRunning =numberRunning
    ;

    } public intgetNumberPassing( )
        { returnnumberPassing
    ;

    } public voidsetNumberPassing( intnumberPassing )
        {this. numberPassing =numberPassing
    ;

    } public intgetNumberFailing( )
        { returnnumberFailing
    ;

    } public voidsetNumberFailing( intnumberFailing )
        {this. numberFailing =numberFailing
    ;

    } public intgetNumberErroring( )
        { returnnumberErroring
    ;

    } public voidsetNumberErroring( intnumberErroring )
        {this. numberErroring =numberErroring
    ;

    } public intgetNumberSkipping( )
        { returnnumberSkipping
    ;

    } public voidsetNumberSkipping( intnumberSkipping )
        {this. numberSkipping =numberSkipping
    ;

    } publicSet<Failure >getFailures( )
        { returnfailures
    ;

    } public voidaddFailure( StringfailureName , booleanisError )
        { for( Failure failure :this.failures )
            { if(failure.getFailureName().equals(failureName )
                    &&failure.getIsError( ) ==isError )
                {failure.setOccurrences(failure.getOccurrences( ) +1)
                ;return
            ;
        }
        } Failure failure = newFailure(failureName ,isError)
        ;failure.setOccurrences(1)
        ;this.failures.add(failure)
    ;

}
