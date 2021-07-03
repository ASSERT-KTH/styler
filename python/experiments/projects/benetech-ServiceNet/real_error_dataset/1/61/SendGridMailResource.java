package org.benetech.servicenet.web.rest;
import com.codahale.metrics.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.benetech.servicenet.security.AuthoritiesConstants;
import org.benetech.servicenet.service.SendGridMailService;
import org.benetech.servicenet.service.dto.FeedbackDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing SendGrid Mail.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class SendGridMailResource {

    private SendGridMailService sendGridMailService;

    public SendGridMailResource(SendGridMailService sendGridMailService) {
        this.sendGridMailService = sendGridMailService;
    }

    @PreAuthorize("hasRole('" + AuthoritiesConstants.SACRAMENTO + "')")
    @PostMapping("/feedback")
    @Timed
    public ResponseEntity<Void> sendMail(@RequestBody FeedbackDto feedbackDto) {
        log.debug("REST request send mail : {}", feedbackDto);
        sendGridMailService.sendFeedback(
            feedbackDto.getEmailAddress(),
            feedbackDto.getMessage()
        );
        return ResponseEntity.ok().build();
    }
}
