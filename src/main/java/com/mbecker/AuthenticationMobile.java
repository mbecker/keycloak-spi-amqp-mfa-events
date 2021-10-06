package com.mbecker;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.common.util.RandomString;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.theme.Theme;

import jdk.internal.org.jline.utils.Log;

import org.keycloak.common.util.Time;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.mbecker.gateway.GatewayServiceFactory;
import com.mbecker.gateway.Notification;

import java.util.Locale;

import org.keycloak.models.utils.FormMessage;

/**
 * @author Mats Becker, mats.becker@gmail.com
 */
public class AuthenticationMobile implements Authenticator {

    public static String PROVIDER_ID = "MB_MOBILE_NUMBER_AUTHENTICATION";

    private static final Logger LOG = Logger.getLogger(AuthenticationMobile.class);

    private Utils utils;

    public AuthenticationMobile(Utils utils) {
        this.utils = utils;
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {

        LOG.infof("authenticate");

        KeycloakSession session = context.getSession();
        UserModel user = context.getUser();
        String mobileXVerified = user.getFirstAttribute(Utils.ATTR_X_VERIFIED);
        Boolean isMobileXVerified = Boolean.parseBoolean(mobileXVerified);

        int pageRefresh = context.getSession().getAttributeOrDefault(Utils.SESSION_AUTH_NOTE_REFRESH, 0);
        LOG.infof("Page refresh: " + pageRefresh);
        
        LOG.infof("User existing attributes: " + user.getAttributes());

        // (1) Mobile nuber attributes: The mobile number is not yet verified (maybe
        // does net yet exists)
        // --> Add required action to add mobile number and to verify mobile number
        // --> Reset user's attribute
        // --> Succeed the context that the next required action can be shown
        String mobileNumber = user.getFirstAttribute(Utils.ATTR_X_NUMBER);
        if (mobileNumber == null || isMobileXVerified == false) {
            LOG.debug("Mobile number is not verfied or mobile number does not exist");
            user.addRequiredAction(RequiredActionMobile.PROVIDER_ID);
            this.utils.resetUser(user);
            context.success();
            return;
        }

        // (2) Verify that the mobile number is valid
        // --> Add required action to add mobile number and to verify mobile number
        // --> Reset user's attribute
        // --> Succeed the context that the next required action can be shown
        // TODO: Really necessay to valiet mobile users again? Should already be verified in the registration/required context
        if (this.utils.mobileNumberIsValid(mobileNumber) == false) {
            LOG.debug("Mobile number is not valid");
            user.addRequiredAction(RequiredActionMobile.PROVIDER_ID);
            this.utils.resetUser(user);
            context.success();
            return;
        }

        if(this.utils.getNotificationShouldSendOnStartp() == false && pageRefresh == 0){
            LOG.debug("Send not the notification startup; await manually retrigger");
            Response challenge = context.form()
                    .addError(new FormMessage(Utils.TEMPLATE_AUTH_PAGE_REFRESH, Utils.TEMPLATE_AUTH_PAGE_REFRESH))
                    .createForm(Utils.TEMPLATE_NAME_AUTH);
            context.challenge(challenge);
            return;
        }

        // (3) SUCCESS
        // --> SEND NOTIFICATION
        Integer createdAt = Time.currentTime();
        String uuid = context.getUser().getId();
        String realm = context.getRealm().getId();

        String mobileCode = RandomString.randomCode(this.utils.getNotificationCodeLength());
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        authSession.setAuthNote(Utils.AUTH_NOTE_CODE, mobileCode);
        authSession.setAuthNote(Utils.AUTH_NOTE_TTL, Long.toString(System.currentTimeMillis() + (this.utils.getNotificationTTL() * 1000L)));

        // Get the theme's message strings locale
        String smsText = "";
        try {
            Theme theme = session.theme().getTheme(Theme.Type.LOGIN);
            Locale locale = session.getContext().resolveLocale(user);
            String smsAuthText = theme.getMessages(locale).getProperty(Utils.TEMPLATE_AUTH_SEND_TEXT);
            smsText = String.format(smsAuthText, mobileCode, Math.floorDiv(this.utils.getNotificationTTL(), 60));
        } catch (Exception e) {
            // Error getting theme's message strings locale and to crate a localized string
            // Fallback sms text
            smsText = "Code: " + mobileCode;
        }

        Notification notification = new Notification(Notification.Action.AUTHENTICATION, Notification.Type.SMS,
                mobileNumber, smsText, mobileCode, this.utils.getNotificationTTL(), createdAt, uuid, realm);

        GatewayServiceFactory.get(this.utils).send(notification, this.utils.getAMQPQueue());

        context.challenge(
                context.form().setAttribute("realm", context.getRealm()).createForm(Utils.TEMPLATE_NAME_AUTH));
    }

    @Override
    public void action(AuthenticationFlowContext context) {

        LOG.debug("=== action ===");

        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        
        UserModel user = context.getUser();

        // (1) Retry: The user submits a retry
        // --> Remove the attributes for the existing code and the timestamp
        // --> Restart flow
        String tryAgain = formData.getFirst("reset");
        if (tryAgain != null && tryAgain.length() > 0) {
            context.getSession().setAttribute(Utils.SESSION_AUTH_NOTE_REFRESH, 1);
            context.getUser().removeAttribute(Utils.ATTR_X_CODE);
            context.getUser().removeAttribute(Utils.ATTR_X_CODE_TIMESTAMP);
            this.authenticate(context);
            return;
        }

        // (1 Additional) The user clicked "skip=[XXXX]"; skip the required action and continue with
        // the flow
        // The user has an attribue "mobile-x-skip-allowed" (overwrites global env) or the env CONFIG_SHOULD_SKIP (do not forget the prefix) is set to true
        String skipForm = formData.getFirst("skip");
        if ((this.utils.getNotificationShouldSkip() == true || Boolean.parseBoolean(user.getFirstAttribute(Utils.ATTR_X_SKIP_ALLOWED)) == true) && skipForm != null
                && skipForm.length() > 0) {
            context.success();
            return;
        }

        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        String code = authSession.getAuthNote(Utils.AUTH_NOTE_CODE);
        String ttl = authSession.getAuthNote(Utils.AUTH_NOTE_TTL);

        // (1) Retry: false
        // (2) Session verification code and ttL: Does the session verifiation code and
        // session ttl exist
        // --> Responds with a response with an error
        if (code == null || ttl == null) {
            // TODO: better error response thate "code" and "ttl" in sessions not yet set
            Response challenge = context.form()
                    .addError(new FormMessage(Utils.TEMPLATE_AUTH_ERROR_SENT, Utils.TEMPLATE_AUTH_ERROR_SENT))
                    .createForm(Utils.TEMPLATE_NAME_AUTH);
            context.challenge(challenge);
            return;
        }

        // (1) Retry: false
        // (2) Session verification code and ttL: ok
        // (3) Enterd code from html form and session verification code are equal
        // -> Respond with an error
        String enteredCode = formData.getFirst(Utils.ATTR_X_CODE);
        boolean isValid = enteredCode.equals(code);
        if (isValid) {
            if (Long.parseLong(ttl) < System.currentTimeMillis()) {
                // (3) Enterd code from html form and session verification code are equal: true
                // (3).(1) TTL of verification code valid?
                // --> Remove the attributes for the existing code and the timestamp
                context.getUser().removeAttribute(Utils.ATTR_X_CODE);
                context.getUser().removeAttribute(Utils.ATTR_X_CODE_TIMESTAMP);
                Response challenge = context.form()
                        .addError(new FormMessage(Utils.TEMPLATE_ACTION_ERROR_TTL, Utils.TEMPLATE_ACTION_ERROR_TTL))
                        .createForm(Utils.TEMPLATE_NAME_AUTH);
                context.challenge(challenge);
                return;
            } else {
                // (3) Enterd code from html form and session verification code are equal: true
                // (3).(1) TTL of verification code valid: valid
                // --> Success
                context.success();
            }
        } else {
            // (3) Enterd code from html form and session verification code are equal: false
            // (3).(2) Check if the flow execution is required
            AuthenticationExecutionModel execution = context.getExecution();
            if (execution.isRequired()) {
                context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS,
                        context.form().setAttribute("realm", context.getRealm())
                                .addError(new FormMessage(Utils.TEMPLATE_ACTION_ERROR_CODE_WRONG,
                                        Utils.TEMPLATE_ACTION_ERROR_CODE_WRONG))
                                .createForm(Utils.TEMPLATE_NAME_AUTH));
            } else if (execution.isConditional() || execution.isAlternative()) {
                context.attempted();
            }
        }
    }

    @Override
    public boolean requiresUser() {
        LOG.debug("=== requiresUser ===");
        return true;
    }

    // TODO: Is this authenticator configured for this user - Seems to group user
    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        LOG.debug("=== configuredFor ===");
        if (user.getFirstAttribute(Utils.ATTR_X_VERIFIED) == null) {
            LOG.debug("No attribute verified: " + Utils.ATTR_X_VERIFIED);
            return true;
        }
        if (user.getFirstAttribute(Utils.ATTR_X_NUMBER) == null) {
            LOG.debug("No attribute verified: " + Utils.ATTR_X_NUMBER);
            return true;
        }
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        LOG.debug("=== setRequiredActions ===");
    }

    @Override
    public void close() {
        LOG.debug("=== close ===");
    }

}
