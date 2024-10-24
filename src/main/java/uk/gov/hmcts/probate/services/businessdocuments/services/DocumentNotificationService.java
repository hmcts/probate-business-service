package uk.gov.hmcts.probate.services.businessdocuments.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.probate.model.documents.DocumentNotification;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DocumentNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentNotificationService.class);

    @Value("${services.notify.documentNotification.templateId}")
    String documentUploadedTemplateId;

    @Value("${services.notify.documentNotification.bilingualTemplateId}")
    String documentUploadedBilingualTemplateId;

    @Value("${services.notify.documentNotification.uploadIssueTemplateId}")
    String documentUploadIssueTemplateId;

    @Value("${services.notify.documentNotification.bilingualUploadIssueTemplateId}")
    String documentUploadIssueBilingualTemplateId;

    @Autowired
    private NotificationClient notificationClient;
    private static final String RESPONSE_DATE_FORMAT = "dd MMMM yyyy";
    private static final String RESPONSE = "## Response";
    private static final String RESPONSE_WELSH = "## Ymateb";
    private static final String FILE_NAME = "## Documents";
    private static final String FILE_NAME_WELSH = "## Dogfennau";

    public void sendEmail(DocumentNotification encodedDocumentNotification, Boolean isBilingual) {
        try {
            DocumentNotification documentNotification = decodeURL(encodedDocumentNotification);
            LOGGER.info("sending document uploaded email");
            notificationClient.sendEmail(isBilingual ? documentUploadedBilingualTemplateId : documentUploadedTemplateId,
                documentNotification.getEmail(), createPersonalisation(documentNotification, isBilingual),
                null);
        } catch (NotificationClientException | UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void sendUploadIssueEmail(DocumentNotification encodedDocumentNotification, Boolean isBilingual) {
        try {
            DocumentNotification documentNotification = decodeURL(encodedDocumentNotification);
            LOGGER.info("sending document upload issue email");
            notificationClient.sendEmail(isBilingual ? documentUploadIssueBilingualTemplateId
                : documentUploadIssueTemplateId,
                documentNotification.getEmail(), createPersonalisation(documentNotification, isBilingual),
                null);
        } catch (NotificationClientException | UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private Map<String, String> createPersonalisation(DocumentNotification documentNotification, Boolean isBilingual) {
        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put("applicant_name", documentNotification.getApplicantName());
        personalisation.put("deceased_name", documentNotification.getDeceasedName());
        personalisation.put("deceased_dod", documentNotification.getDeceasedDod());
        personalisation.put("ccd_reference", documentNotification.getCcdReference());
        personalisation.put("response_heading", getResponse(documentNotification.getCitizenResponse(), isBilingual));
        personalisation.put("RESPONSE", null != documentNotification.getCitizenResponse()
            ? documentNotification.getCitizenResponse() : "");
        personalisation.put("filename_heading", getFileName(documentNotification.getFileName(), isBilingual));
        personalisation.put("FILE NAMES", String.join("\n", documentNotification.getFileName()));
        personalisation.put("UPDATE DATE", getSubmittedDate(documentNotification.getExpectedResponseDate()));
        return personalisation;
    }

    private String getResponse(String citizenResponse, Boolean isBilingual) {
        if (null != citizenResponse && !citizenResponse.isEmpty()) {
            return isBilingual ? RESPONSE_WELSH : RESPONSE;
        }
        return "";
    }

    private String getFileName(List<String> fileName, Boolean isBilingual) {
        if (!fileName.isEmpty()) {
            return isBilingual ? FILE_NAME_WELSH : FILE_NAME;
        }
        return "";

    }

    private String getSubmittedDate(String expectedResponseDate) {
        LocalDate parsedDate = LocalDate.parse(expectedResponseDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(RESPONSE_DATE_FORMAT);
        return parsedDate.format(formatter);
    }

    public DocumentNotification decodeURL(DocumentNotification documentNotification)
        throws UnsupportedEncodingException {
        documentNotification.setDeceasedName(decodeURLParam(documentNotification.getDeceasedName()));
        documentNotification.setDeceasedDod(decodeURLParam(documentNotification.getDeceasedDod()));
        documentNotification.setApplicantName(decodeURLParam(documentNotification.getApplicantName()));
        documentNotification.setCcdReference(documentNotification.getCcdReference());
        documentNotification.setEmail(documentNotification.getEmail());
        documentNotification.setCitizenResponse(documentNotification.getCitizenResponse() != null
            ? decodeURLParam(documentNotification.getCitizenResponse()) : null);
        documentNotification.setFileName(!documentNotification.getFileName().isEmpty()
            ? decodeURLParams(documentNotification.getFileName()) : new ArrayList<>());
        documentNotification.setExpectedResponseDate(decodeURLParam(documentNotification
            .getExpectedResponseDate()));
        return documentNotification;
    }

    private String decodeURLParam(String uriParam) throws UnsupportedEncodingException {
        return URLDecoder.decode(uriParam, StandardCharsets.UTF_8.toString());
    }

    public List<String> decodeURLParams(List<String> encodedParams) throws UnsupportedEncodingException {
        List<String> decodedParams = new ArrayList<>();
        for (String param : encodedParams) {
            decodedParams.add(decodeURLParam(param));
        }
        return decodedParams;
    }
}
