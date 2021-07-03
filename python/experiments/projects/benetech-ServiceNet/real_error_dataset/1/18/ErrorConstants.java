package org.benetech.servicenet.errors;

import java.net.URI;

public final class ErrorConstants {

    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";

    public static final String ERR_VALIDATION = "error.validation";

    public static final String PROBLEM_BASE_URL = "https://www.jhipster.tech/problem";

    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problemwithmessage");

    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraintviolation");

    public static final URI PARAMETERIZED_TYPE = URI.create(PROBLEM_BASE_URL + "/parameterized");

    public static final URI ENTITY_NOT_FOUND_TYPE = URI.create(PROBLEM_BASE_URL + "/entitynotfound");

    public static final URI INVALID_PASSWORD_TYPE = URI.create(PROBLEM_BASE_URL + "/invalidpassword");

    public static final URI EMAIL_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/emailalreadyused");

    public static final URI LOGIN_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/loginalreadyused");

    public static final URI EMAIL_NOT_FOUND_TYPE = URI.create(PROBLEM_BASE_URL + "/emailnotfound");

    public static final URI INCORRECT_FILES_NUMBER = URI.create(PROBLEM_BASE_URL + "/incorrectfilesnumber");
    private ErrorConstants() {
    }
}
