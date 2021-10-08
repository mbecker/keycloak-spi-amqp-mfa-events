package com.mbecker.gateway;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import com.google.gson.annotations.Expose;
import com.mbecker.Utils;

import org.jboss.logging.Logger;
import org.keycloak.common.util.RandomString;
import org.keycloak.common.util.Time;
import org.keycloak.email.EmailException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.theme.FreeMarkerException;
import org.keycloak.theme.FreeMarkerUtil;
import org.keycloak.theme.Theme;
import org.keycloak.theme.beans.MessageFormatterMethod;

public class Notification {

    private static final Logger LOG = Logger.getLogger(Notification.class);

    
    public enum Type {
        AMQP, EMAIL
    }

    public enum Action {
        REQUIREDACTION, AUTHENTICATION
    }

    public Notification(KeycloakSession session, UserModel user, RealmModel realm, Utils utils, Action action, Type type, String receiver) {
        this.session = session;
        this.user = user;
        this.uuid = user.getId();
        this.realm = realm;
        this.realmId = realm.getId();
        this.action = action;
        this.type = type;
        this.receiver = receiver;
        this.code = RandomString.randomCode(utils.getNotificationCodeLength());;
        this.ttl = utils.getNotificationTTL();
        this.createdAt = Time.currentTime();
        switch (type)  {
            case AMQP:
                this.setAMQPText();
                break;
            case EMAIL:
                this.setEmail();
                break;
            default:
                break;
        }
    }

    public void setAMQPText() {
        // Get the theme's message strings locale
        String smsText = "Code: " + this.code;
        String messageProperty = Utils.TEMPLATE_AUTH_SEND_TEXT;
        if(this.action == Action.REQUIREDACTION) {
            messageProperty = Utils.TEMPLATE_ACTION_SEND_TEXT;
        }
        try {
            Theme theme = this.session.theme().getTheme(Theme.Type.LOGIN);
            Locale locale = session.getContext().resolveLocale(this.user);
            String smsAuthText = theme.getMessages(locale).getProperty(messageProperty);
            smsText = String.format(smsAuthText, this.code, Math.floorDiv(this.ttl, 60));
        } catch (Exception e) {
            LOG.error(e);
        }
        this.message = smsText;
    }

    public void setEmail() {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("code", this.code);
        attributes.put("ttl", Math.floorDiv(this.ttl, 60));

        List<Object> subjectAttributes = new ArrayList<Object>();

        try {
            this.email = this.processTemplate(Utils.TEMPLATE_AUTH_PAGE_EMAIL_SUBJECT, subjectAttributes, Utils.TEMPLATE_NAME_AUTH_EMAIL, attributes);
        } catch (EmailException e) {
            LOG.error(e);
        }
        
    }

    // See: https://github.com/keycloak/keycloak/blob/3abf9283a8b7c2aaa2ed4f797da246c4f3855ca3/services/src/main/java/org/keycloak/email/freemarker/FreeMarkerEmailTemplateProvider.java#L210
    protected EmailTemplate processTemplate(String subjectKey, List<Object> subjectAttributes, String template, Map<String, Object> attributes) throws EmailException {
        FreeMarkerUtil freeMarker = new FreeMarkerUtil();
        try {
            Theme theme = this.session.theme().getTheme(Theme.Type.EMAIL);
            Locale locale = session.getContext().resolveLocale(this.user);
            attributes.put("locale", locale);
            Properties rb = new Properties();
            rb.putAll(theme.getMessages("auth_mail", locale));
            rb.putAll(realm.getRealmLocalizationTextsByLocale(locale.toLanguageTag()));
            attributes.put("msg", new MessageFormatterMethod(locale, rb));
            attributes.put("properties", theme.getProperties());
            String subject = new MessageFormat(rb.getProperty(subjectKey, subjectKey), locale).format(subjectAttributes.toArray());
            String textTemplate = String.format("email/text/%s", template);
            String textBody;
            try {
                textBody = freeMarker.processTemplate(attributes, textTemplate, theme);
            } catch (final FreeMarkerException e) {
                LOG.error(e);
                throw new EmailException("Failed to template plain text email.", e);
            }
            String htmlTemplate = String.format("email/html/%s", template);
            String htmlBody;
            try {
                htmlBody = freeMarker.processTemplate(attributes, htmlTemplate, theme);
            } catch (final FreeMarkerException e) {
                LOG.error(e);
                throw new EmailException("Failed to template html email.", e);
            }

            return new EmailTemplate(subject, textBody, htmlBody);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e);
            throw new EmailException("Failed to template email", e);
        }
    }

    private final UserModel user;
    @Expose
    private final String uuid;
    private final KeycloakSession session;
    @Expose 
    private final Type type;
    @Expose
    private final Action action;
    @Expose 
    private final String receiver;
    @Expose 
    private final String code;
    @Expose 
    private final int ttl;
    @Expose 
    private final Integer createdAt; // Timestamp in seconda at which the code was generated
    
    private final RealmModel realm;
    @Expose 
    private final String realmId;

    @Expose
    private String message;
    private EmailTemplate email;
    public UserModel getUser() {
        return user;
    }

    public KeycloakSession getSession() {
        return session;
    }

    public Type getType() {
        return type;
    }

    public Action getAction() {
        return action;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getCode() {
        return code;
    }

    public int getTtl() {
        return ttl;
    }

    public Integer getCreatedAt() {
        return createdAt;
    }

    public RealmModel getRealm() {
        return realm;
    }

    public String getRealmId() {
        return realmId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EmailTemplate getEmail() {
        return email;
    }

    public void setEmail(EmailTemplate email) {
        this.email = email;
    }

    

}
