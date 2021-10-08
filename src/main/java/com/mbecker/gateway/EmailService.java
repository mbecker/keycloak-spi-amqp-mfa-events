package com.mbecker.gateway;

import java.util.Map;

import org.jboss.logging.Logger;
import org.keycloak.email.DefaultEmailSenderProvider;
import org.keycloak.email.EmailException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;

public class EmailService implements GatewayService {

    private static final Logger LOG = Logger.getLogger(EmailService.class);

    private DefaultEmailSenderProvider senderProvider;
    private KeycloakSession session;

    public EmailService(KeycloakSession session) {
        this.session = session;
        this.senderProvider = new DefaultEmailSenderProvider(session);
    }

    @Override
    public void send(Notification notification, String routingKey) throws Exception {

    }

    @Override
    public void send(String notification, String routingKey) throws Exception {

    }

    @Override
    public void send(Object obj, String routingKey) throws Exception {
    }

    @Override
    public void sendMail(Map<String, String> config, UserModel user, EmailTemplate emailTemplate)
            throws EmailException {
        this.senderProvider.send(this.session.getContext().getRealm().getSmtpConfig(), user, emailTemplate.getSubject(),
                emailTemplate.getTextBody(), emailTemplate.getHtmlBody());
        LOG.infof("Sent MFA mail to uuid: %s", user.getEmail());
    }

}
