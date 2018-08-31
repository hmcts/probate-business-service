package uk.gov.hmcts.probate.services.idgeneration.strategy;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;

public class ProbateStrategy implements IdGenerationStrategy {
    private static final String SPACE = " ";
    private static final String HYPHEN = "-";
    private static final String APPLICANT_FIRST_NAME = "firstName";
    private static final String APPLICANT_LAST_NAME = "lastName";
    private static final String SPECIAL_CHARACTERS = "[^-\\s\\w\\d]";


    @Override
    public String generate(Map<String, String> data) {
        String firstName = data.get(APPLICANT_FIRST_NAME);
        String lastName = data.get(APPLICANT_LAST_NAME);
        if (firstName == null || lastName == null) {
            throw new IllegalArgumentException("Applicant first name or last name cannot be null.");
        }
        return generateUniqueString(firstName.replaceAll(SPECIAL_CHARACTERS, ""), lastName.replaceAll(SPECIAL_CHARACTERS, ""));
    }

    private String generateUniqueString(String firstName, String lastName) {
        try {
            return URLEncoder.encode(
                    firstName.replace(SPACE, HYPHEN) + HYPHEN +
                            lastName.replace(SPACE, HYPHEN) + HYPHEN +
                            UUID.randomUUID(), "UTF-8"
            ).toLowerCase();
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 is unknown");
        }
    }
}
