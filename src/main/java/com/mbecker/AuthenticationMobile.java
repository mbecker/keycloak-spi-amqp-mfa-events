package com.mbecker;

import java.util.ArrayList;
import java.util.Arrays;

import javax.ws.rs.core.MultivaluedMap;

import com.mbecker.gateway.GatewayService;
import com.mbecker.gateway.GatewayServiceFactory;
import com.mbecker.gateway.Notification;
import com.mbecker.gateway.Notification.Type;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.sessions.AuthenticationSessionModel;

/**
 * @author Mats Becker, mats.becker@gmail.com
 */
public class AuthenticationMobile implements Authenticator {

    public static String PROVIDER_ID = "MB_MOBILE_NUMBER_AUTHENTICATION";

    private static final Logger LOG = Logger.getLogger(AuthenticationMobile.class);

    private Utils utils;

    private ArrayList<FormMessage> errors;
    private ArrayList<FormMessage> success;

    private String selectedNotificationChannel;
    private String[] notificationChannels;

    public AuthenticationMobile(Utils utils) {
        this.utils = utils;
        this.notificationChannels = utils.getNotificationChannels();
        this.selectedNotificationChannel = notificationChannels[0];
        this.errors = new ArrayList<FormMessage>();
        this.success = new ArrayList<FormMessage>();
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {

        LOG.info("=== authenticate ===");

        KeycloakSession session = context.getSession();
        UserModel user = context.getUser();
        String mobileXVerified = user.getFirstAttribute(Utils.ATTR_X_VERIFIED);
        Boolean isMobileXVerified = Boolean.parseBoolean(mobileXVerified);

        int pageRefresh = Helper.parseInteger(context.getAuthenticationSession().getAuthNote(Utils.SESSION_AUTH_NOTE_REFRESH), 0);
        LOG.infof("::::::::::: Authenticate (1): Page Refresh - %d", pageRefresh);

        LOG.info("User existing attributes: " + user.getAttributes());

        // Get the user selected notification channel
        // Check if the user's attribute selected channel is in the list of configured
        // channels (via scope/sys config)
        // The default one is the first channel in the scope/sys config
        String selectedChannelFormUserAttributes = user.getFirstAttribute(Utils.ATTR_X_SLECTED_CHANNEL);
        if (selectedChannelFormUserAttributes != null) {
            if (Arrays.stream(this.notificationChannels).anyMatch(selectedChannelFormUserAttributes::equals)) {
                this.selectedNotificationChannel = selectedChannelFormUserAttributes;
            }
        }
        user.setSingleAttribute(Utils.ATTR_X_SLECTED_CHANNEL, this.selectedNotificationChannel);

        // (1) Mobile nuber attributes: The mobile number is not yet verified (maybe
        // does net yet exists)
        // --> Add required action to add mobile number and to verify mobile number
        // --> Reset user's attribute
        // --> Succeed the context that the next required action can be shown
        String mobileNumber = user.getFirstAttribute(Utils.ATTR_X_NUMBER);
        if (mobileNumber == null || isMobileXVerified == false) {
            LOG.info("Mobile number is not verfied or mobile number does not exist");
            user.addRequiredAction(RequiredActionMobile.PROVIDER_ID);
            this.utils.resetUser(user);
            context.success();
            return;
        }

        // (2) Verify that the mobile number is valid
        // --> Add required action to add mobile number and to verify mobile number
        // --> Reset user's attribute
        // --> Succeed the context that the next required action can be shown
        // TODO: Really necessary to valiet mobile users again? Should already be
        // verified in the registration/required context
        if (this.utils.mobileNumberIsValid(mobileNumber) == false) {
            LOG.info("Mobile number is not valid");
            user.addRequiredAction(RequiredActionMobile.PROVIDER_ID);
            this.utils.resetUser(user);
            context.success();
            return;
        }

        if (this.utils.getNotificationShouldSendOnStartp() == false && pageRefresh == 0) {
            LOG.info("Send not the notification startup; await manually retrigger");
            this.errors.add(new FormMessage(Utils.TEMPLATE_AUTH_PAGE_REFRESH, Utils.TEMPLATE_AUTH_PAGE_REFRESH));
        }

        // (3) SUCCESS
        // --> SEND NOTIFICATION
        if (this.errors.size() == 0) {

            // (3).(1) Identify  which notifcation type the use has selected (and which is configured via scope/env config)
            Type notificationType = Type.AMQP;
            if (this.selectedNotificationChannel.equals(Notification.Type.EMAIL.name())) {
                notificationType = Type.EMAIL;
            }

            // (3).(2) Create the notification
            Notification notification = new Notification(context.getSession(), user, context.getRealm(), this.utils,
                    Notification.Action.AUTHENTICATION, notificationType, mobileNumber);

            // (3).(3) Initialize the gateway service
            // TODO: Really necessay to initialize with session and notification (session used for email; notificato to get the tyoe of gateway like AMQP or email)
            
            try {
                if (notificationType == Type.AMQP) {
                    GatewayService gatewayService = GatewayServiceFactory.get(this.utils);
                    gatewayService.send(notification, this.utils.getAMQPQueue());
                    this.success
                            .add(new FormMessage(Utils.TEMPLATE_AUTH_PAGE_SEND_OK, Utils.TEMPLATE_AUTH_PAGE_SEND_OK));
                } else {
                    GatewayService gatewayService = GatewayServiceFactory.get(this.utils, session, notification);
                    gatewayService.sendMail(session.getContext().getRealm().getSmtpConfig(), context.getUser(),
                            notification.getEmail());
                    this.success
                            .add(new FormMessage(Utils.TEMPLATE_AUTH_PAGE_SEND_OK, Utils.TEMPLATE_AUTH_PAGE_SEND_OK));
                }
                // Store the code and ttl in the user's auth not session to equal it later
                AuthenticationSessionModel authSession = context.getAuthenticationSession();
                authSession.setAuthNote(Utils.AUTH_NOTE_CODE, notification.getCode());
                authSession.setAuthNote(Utils.AUTH_NOTE_TTL,
                        Long.toString(System.currentTimeMillis() + (notification.getTtl() * 1000L)));

            } catch (Exception ex) {
                LOG.error(ex);
                // BUG: Keyclok Tempale does show eitehr success or error messages; not bot at
                // once
                // The success of "Trigger" is added but the email isn't sent
                this.success.clear();
                this.errors.add(new FormMessage(Utils.TEMPLATE_AUTH_ERROR_SENT, Utils.TEMPLATE_AUTH_ERROR_SENT));
            }

        }

        LoginFormsProvider form = context.form();
        form.setAttribute("realm", context.getRealm());
        form.setAttribute(Utils.TEMPLATE_AUTH_PAGE_CHANNEL_SELECTED, this.selectedNotificationChannel);
        form.setAttribute(Utils.TEMPLATE_AUTH_PAGE_CHANNELS, notificationChannels);

        for (int i = 0; i < this.errors.size(); i++) {
            form.addError(this.errors.get(i));
        }
        for (int i = 0; i < this.success.size(); i++) {
            form.addSuccess(this.success.get(i));
        }

        this.errors.clear();
        this.success.clear();

        context.challenge(form.createForm(Utils.TEMPLATE_NAME_AUTH));
    }

    @Override
    public void action(AuthenticationFlowContext context) {

        LOG.info("=== action ===");

        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();

        String formSelectedChannel = formData.getFirst("notification");
        if (formSelectedChannel != null) {
            context.getUser().setSingleAttribute(Utils.ATTR_X_SLECTED_CHANNEL, formSelectedChannel);
            this.selectedNotificationChannel = formSelectedChannel;
        }

        UserModel user = context.getUser();

        // (1) Retry: The user submits a retry
        // --> Remove the attributes for the existing code and the timestamp
        // --> Restart flow
        String tryAgain = formData.getFirst("reset");
        if (tryAgain != null && tryAgain.length() > 0) {
            int pageRefresh = context.getSession().getAttributeOrDefault(Utils.SESSION_AUTH_NOTE_REFRESH, 0);
            LOG.infof("::::::::::: Action (2): Page Refresh - %d", pageRefresh);
            context.getAuthenticationSession().setAuthNote(Utils.SESSION_AUTH_NOTE_REFRESH, String.format("%d", pageRefresh + 1));
            context.getUser().removeAttribute(Utils.ATTR_X_CODE);
            context.getUser().removeAttribute(Utils.ATTR_X_CODE_TIMESTAMP);
            this.success.add(new FormMessage(Utils.TEMPLATE_AUTH_PAGE_TRIGGERED_SEND_CONDE,
                    Utils.TEMPLATE_AUTH_PAGE_TRIGGERED_SEND_CONDE));
            this.authenticate(context);
            return;
        }

        // (1 Additional) The user clicked "skip=[XXXX]"; skip the required action and
        // continue with
        // the flow
        // The user has an attribue "mobile-x-skip-allowed" (overwrites global env) or
        // the env CONFIG_SHOULD_SKIP (do not forget the prefix) is set to true
        String skipForm = formData.getFirst("skip");
        if ((this.utils.getNotificationShouldSkip() == true
                || Boolean.parseBoolean(user.getFirstAttribute(Utils.ATTR_X_SKIP_ALLOWED)) == true) && skipForm != null
                && skipForm.length() > 0) {
            user.removeAttribute(Utils.SESSION_AUTH_NOTE_REFRESH);
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
            // TODO: better error response that "code" and "ttl" in sessions not yet set
            this.errors.add(new FormMessage(Utils.TEMPLATE_AUTH_ERROR_SENT, Utils.TEMPLATE_AUTH_ERROR_SENT));
            this.authenticate(context);
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
                this.errors.add(new FormMessage(Utils.TEMPLATE_ACTION_ERROR_TTL, Utils.TEMPLATE_ACTION_ERROR_TTL));
                this.authenticate(context);
                return;
            } else {
                // (3) Enterd code from html form and session verification code are equal: true
                // (3).(1) TTL of verification code valid: valid
                // --> Success
                user.removeAttribute(Utils.SESSION_AUTH_NOTE_REFRESH);
                context.success();
            }
        } else {
            // (3) Enterd code from html form and session verification code are equal: false
            // (3).(2) Check if the flow execution is required
            AuthenticationExecutionModel execution = context.getExecution();
            if (execution.isRequired()) {
                this.errors.add(new FormMessage(Utils.TEMPLATE_ACTION_ERROR_CODE_WRONG,
                        Utils.TEMPLATE_ACTION_ERROR_CODE_WRONG));
                this.authenticate(context);
                return;
            } else if (execution.isConditional() || execution.isAlternative()) {
                context.attempted();
            }
        }
    }

    @Override
    public boolean requiresUser() {
        LOG.info("=== requiresUser ===");
        return true;
    }

    // TODO: Is this authenticator configured for this user - Seems to group user
    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        LOG.info("=== configuredFor ===");
        if (user.getFirstAttribute(Utils.ATTR_X_VERIFIED) == null) {
            LOG.info("No attribute verified: " + Utils.ATTR_X_VERIFIED);
            return true;
        }
        if (user.getFirstAttribute(Utils.ATTR_X_NUMBER) == null) {
            LOG.info("No attribute verified: " + Utils.ATTR_X_NUMBER);
            return true;
        }
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        LOG.info("=== setRequiredActions ===");
    }

    @Override
    public void close() {
        LOG.info("=== close ===");
    }

}
