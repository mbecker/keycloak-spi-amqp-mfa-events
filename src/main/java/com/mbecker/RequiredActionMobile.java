package com.mbecker;

import java.util.Collections;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.mbecker.gateway.GatewayServiceFactory;
import com.mbecker.gateway.Notification;

import org.jboss.logging.Logger;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.common.util.Time;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;

/**
 * @author Mats Becker, mats.becker@gmail.com
 */
public class RequiredActionMobile implements RequiredActionProvider {

    private static final Logger LOG = Logger.getLogger(RequiredActionMobile.class);

    public final static String PROVIDER_ID = "MB_MOBILE_NUMBER_REQUIREDACTION";

    private Utils utils;

    public RequiredActionMobile(Utils utils) {
        this.utils = utils;
    }

    @Override
    public void close() {
        LOG.info("Close");
    }

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        // Called every time a user authenticates
        // Check that the user's attribute "mobile number verified" is set and true
        // If yes then remove the required action (just in case)
        // If no then reset the user and add the required action to add a new mobile
        // number
        LOG.info("evaluateTriggers");
        UserModel user = context.getUser();
        String mobileXVerified = user.getFirstAttribute(Utils.ATTR_X_VERIFIED);
        Boolean isMobileXVerified = Boolean.parseBoolean(mobileXVerified);

        if (!isMobileXVerified) {
            LOG.info("User's mobile number is not yet verified; start verification process");
            this.utils.resetUser(user);
            user.addRequiredAction(RequiredActionMobile.PROVIDER_ID);
        } else {
            LOG.info("User's mobile number is verified; remove verification process");
            user.removeRequiredAction(RequiredActionMobile.PROVIDER_ID);
        }
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {

        LOG.info("requiredActionChallenge");

        UserModel user = context.getUser();
        String mobileXVerified = user.getFirstAttribute(Utils.ATTR_X_VERIFIED);
        Boolean isMobileXVerified = Boolean.parseBoolean(mobileXVerified);

        if (isMobileXVerified) {
            context.success();
            return;
        }

        if (Boolean.parseBoolean(context.getAuthenticationSession().getAuthNote(Utils.AUTH_NOTE_SKIP)) == true) {
            LOG.info("Skip challenge because the user clicked in the form to skip");
            context.success();
            return;
        }

        LOG.info("User existing attributes: " + user.getAttributes());

        // (1) Mobile nuber attributes: The mobile number is not yet verified (maybe
        // does net yet exists)
        // --> Respond with a response to add mobile number; show error
        // "mobileActionErrorNumberMissing"
        String mobileNumber = user.getFirstAttribute(Utils.ATTR_X_NUMBER);
        if (mobileNumber == null) {
            Response challenge = context.form().addError(new FormMessage(Utils.TEMPLATE_ACTION_ERROR_NUMBER_MISSING,
                    Utils.TEMPLATE_ACTION_ERROR_NUMBER_MISSING)).createForm(Utils.TEMPLATE_NAME_ACTIONREQUIRED);
            context.challenge(challenge);
            return;
        }

        // (1) Mobile nuber attributes: exists
        // (2) Check mobile verification code attributes: Does the mobile verification
        // code exists?
        // --> Respond with a response to add the code verification number; show error
        // "mobileActionErrorTTL"
        String mobileCode = user.getFirstAttribute(Utils.ATTR_X_CODE);
        if (mobileCode == null) {
            Response challenge = context.form()
                    .addError(new FormMessage(Utils.TEMPLATE_ACTION_ERROR_TTL, Utils.TEMPLATE_ACTION_ERROR_TTL))
                    .createForm(Utils.TEMPLATE_NAME_ACTIONREQUIRED);
            context.challenge(challenge);
            return;
        }

        // (1) Mobile nuber attributes: exists
        // (2) Check mobile verification code attributes: exists
        // (3) Check mobile verification TTL: Is the TTL still valid?
        // --> Respond with a response to add the code verification number; show error
        // "mobileActionErrorTTL"
        if (!isVerifyTTLOk(context)) {
            return;
        }

        // (1) Mobile nuber attributes: exists
        // (2) Check mobile verification code attributes: exists
        // (3) Check mobile verification TTL: valid
        // -> Show code verification form
        Response challenge = context.form().createForm(Utils.TEMPLATE_NAME_ACTIONREQUIRED);
        context.challenge(challenge);

    }

    @Override
    public void processAction(RequiredActionContext context) {

        LOG.info("processAction");

        UserModel user = context.getUser();
        String mobileXVerified = user.getFirstAttribute(Utils.ATTR_X_VERIFIED);
        Boolean isMobileXVerified = Boolean.parseBoolean(mobileXVerified);

        if (isMobileXVerified) {
            context.success();
            return;
        }

        // FormData from html from template page ("action-mobile")
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        LOG.info("FormData form html form: " + formData);

        // The user clicked "reset=[Try again]"; reset all attributes and start process
        // again
        String tryAgain = formData.getFirst("reset");
        if (tryAgain != null && tryAgain.length() > 0) {
            LOG.info("Form clicked: reset=[Try again]");
            this.utils.resetUser(user);
            this.requiredActionChallenge(context);
            return;
        }

        // The user clicked "skip=[XXXX]"; skip the required action and continue with
        // the flow
        String skipForm = formData.getFirst("skip");
        if (this.utils.getNotificationShouldSkip() && skipForm != null && skipForm.length() > 0) {
            LOG.info("Form clicked: skip=[Continue]");
            context.getAuthenticationSession().setAuthNote(Utils.AUTH_NOTE_SKIP, "true");
            context.success();
            return;
        }

        // (1) HTML Form "mobile number" exists: Get the mobile number from the html
        // form attributes
        String mobileNumber = formData.getFirst(Utils.FORM_MOBILE_X_NUMBER);
        if (mobileNumber != null) {
            // (1).(1) Verify that the mobile number is valid
            // --> Reset user
            // --> Respond with a response restart
            if (this.utils.mobileNumberIsValid(mobileNumber) == false) {
                LOG.info("Mobile number verification - Mobile Number is invalid; reset user; show error: "
                        + mobileNumber);
                this.utils.resetUser(user);
                Response challenge = context.form()
                        .addError(new FormMessage(Utils.TEMPLATE_ACTION_ERROR_INVALID_NUMBER,
                                Utils.TEMPLATE_ACTION_ERROR_INVALID_NUMBER))
                        .createForm(Utils.TEMPLATE_NAME_ACTIONREQUIRED);
                context.challenge(challenge);
                return;
            }

            // (1).(2).(0) Generate random string code
            Notification notification = new Notification(context.getSession(), user, context.getRealm(), this.utils,
                    Notification.Action.REQUIREDACTION, Notification.Type.AMQP, mobileNumber);

            LOG.info("Process Action: --> Save mobile number " + mobileNumber);
            user.setSingleAttribute(Utils.ATTR_X_NUMBER, mobileNumber);
            user.setSingleAttribute(Utils.ATTR_X_CODE, notification.getCode());
            user.setSingleAttribute(Utils.ATTR_X_CODE_TIMESTAMP, Integer.toString(notification.getCreatedAt()));

            LOG.info("Process Action: --> Sent notification: " + notification);
            try {
                GatewayServiceFactory.get(this.utils, context.getSession(), notification).send(notification,
                        this.utils.getAMQPQueue());
            } catch (Exception ex) {
                LOG.error(ex);
                Response challenge = context.form()
                        .addError(new FormMessage(Utils.TEMPLATE_AUTH_ERROR_SENT, Utils.TEMPLATE_AUTH_ERROR_SENT))
                        .createForm(Utils.TEMPLATE_NAME_ACTIONREQUIRED);
                context.challenge(challenge);
                return;
            }

            this.requiredActionChallenge(context);
            return;
        }

        // (2).(1) HTML Form "verification code" exists: Get the input code from the
        // html form attributes
        // --> Respond with a response to add the code; show error
        // "mobileActionErrorCodeMissing"
        String mobileXCode = formData.getFirst(Utils.FORM_MOBILE_X_CODE);
        if (mobileXCode == null) {
            Response challenge = context.form().addError(
                    new FormMessage(Utils.TEMPLATE_ACTION_ERROR_CODE_MISSING, Utils.TEMPLATE_ACTION_ERROR_CODE_MISSING))
                    .createForm(Utils.TEMPLATE_NAME_ACTIONREQUIRED);
            context.challenge(challenge);
            return;
        }

        // (2).(1) HTML Form "verification code" exists:true
        // (2).(2) Verify TTL
        // --> Respond with a response to add the code verification code; show error
        // "mobileActionErrorTTL"
        if (!isVerifyTTLOk(context)) {
            return;
        }

        // (2).(1) HTML Form "verification code" exists:true
        // (2).(2) Verify TTL
        // (2).(3) Verify "verification code" is equal "user attributes verification
        // code (sent via notification)"
        // --> Respond with a response to add the code verification code; show error
        // "mobileActionErrorCodeWrong"
        String mobileCode = user.getFirstAttribute(Utils.ATTR_X_CODE);
        if (!mobileXCode.equals(mobileCode)) {
            Response challenge = context.form().addError(
                    new FormMessage(Utils.TEMPLATE_ACTION_ERROR_CODE_WRONG, Utils.TEMPLATE_ACTION_ERROR_CODE_WRONG))
                    .createForm(Utils.TEMPLATE_NAME_ACTIONREQUIRED);
            context.challenge(challenge);
            return;
        }

        // SUCCESS
        // Remove user attributes data
        // Add user attributes data: verfied, verification timestamp
        user.removeAttribute(Utils.ATTR_X_CODE_TIMESTAMP);
        user.removeAttribute(Utils.ATTR_X_CODE);
        user.setAttribute(Utils.ATTR_X_VERIFIED, Collections.singletonList(Boolean.toString(true)));
        user.setAttribute(Utils.ATTR_X_VERIFIED_TIMESTAMP,
                Collections.singletonList(Integer.toString(Time.currentTime())));

        context.success();
    }

    public boolean isVerifyTTLOk(RequiredActionContext context) {

        LOG.info("isVerifyTTLOk");

        String mobileTimestamp = context.getUser().getFirstAttribute(Utils.ATTR_X_CODE_TIMESTAMP);
        try {
            Integer mobileTS = Integer.parseInt(mobileTimestamp);
            Integer now = Time.currentTime();
            Integer diff = Math.abs(now - mobileTS);
            if (diff > Utils.TTL) {
                Response challenge = context.form()
                        .addError(new FormMessage(Utils.TEMPLATE_ACTION_ERROR_TTL, Utils.TEMPLATE_ACTION_ERROR_TTL))
                        .createForm(Utils.TEMPLATE_NAME_ACTIONREQUIRED);
                context.challenge(challenge);
                return false;
            }

        } catch (NumberFormatException e) {
            LOG.info("Error parsing timetsmap ttl: " + e);
            Response challenge = context.form()
                    .addError(new FormMessage(Utils.TEMPLATE_ACTION_ERROR_TTL, Utils.TEMPLATE_ACTION_ERROR_TTL))
                    .createForm(Utils.TEMPLATE_NAME_ACTIONREQUIRED);
            context.challenge(challenge);
            return false;
        }
        return true;
    }

}
