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
import java.util.HashMap;
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

    public void sendEmail(DocumentNotification encodedDocumentNotification, Boolean isBilingual) {
        try {
            DocumentNotification documentNotification = decodeURL(encodedDocumentNotification);
            LOGGER.info("sending document uploaded email");
            notificationClient.sendEmail(isBilingual ? documentUploadedBilingualTemplateId : documentUploadedTemplateId,
                documentNotification.getEmail(), createPersonalisation(documentNotification), null);
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
                documentNotification.getEmail(), createPersonalisation(documentNotification), null);
        } catch (NotificationClientException | UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private Map<String, String> createPersonalisation(DocumentNotification documentNotification) {
        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put("applicant_name", documentNotification.getApplicantName());
        personalisation.put("deceased_name", documentNotification.getDeceasedName());
        personalisation.put("deceased_dod", documentNotification.getDeceasedDod());
        personalisation.put("ccd_reference", documentNotification.getCcdReference());
        personalisation.put("RESPONSE", documentNotification.getCitizenResponse());
        personalisation.put("FILE NAMES", String.join("\n", documentNotification.getFileName()));
        personalisation.put("UPDATE DATE", documentNotification.getCitizenResponseSubmittedDate());
        return personalisation;
    }

    public DocumentNotification decodeURL(DocumentNotification documentNotification)
        throws UnsupportedEncodingException {
        documentNotification.setDeceasedName(decodeURLParam(documentNotification.getDeceasedName()));
        documentNotification.setDeceasedDod(decodeURLParam(documentNotification.getDeceasedDod()));
        documentNotification.setApplicantName(decodeURLParam(documentNotification.getApplicantName()));
        documentNotification.setCcdReference(documentNotification.getCcdReference());
        documentNotification.setEmail(documentNotification.getEmail());
        documentNotification.setCitizenResponse(decodeURLParam(documentNotification.getCitizenResponse()));
        documentNotification.setFileName(documentNotification.getFileName());
        documentNotification.setCitizenResponseSubmittedDate(decodeURLParam(documentNotification
            .getCitizenResponseSubmittedDate()));
        return documentNotification;
    }

    private String decodeURLParam(String uriParam) throws UnsupportedEncodingException {
        return URLDecoder.decode(uriParam, StandardCharsets.UTF_8.toString());
    }
}
