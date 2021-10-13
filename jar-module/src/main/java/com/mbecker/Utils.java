package com.mbecker;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mbecker.gateway.Notification;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.UserModel;

/**
 * @author Mats Becker, mats.becker@gmail.com
 */
public class Utils {

    private static final Logger LOG = Logger.getLogger(Utils.class);

    // public static final String CONST_NOTIFICATION_CHANNEL_AMQP = "AMQP";
    // public static final String CONST_NOTIFICATION_CHANNEL_EMAIL = "EMAIL";

    /**
     * CONFIG
     */
    public static final String CONFIG_SYS_ENV_PREFIX = "AMQPMFAEVENTS_"; // Global prefix for SYSTEM environemt
                                                                         // variables like "MB_AUTH_CONFIG_AMQP_HOST";

    public static final String CONFIG_IS_SIMULATION = "CONFIG_IS_SIMULATION";

    public Boolean getIsSimulation() {
        return Boolean.parseBoolean(this.config.get(CONFIG_IS_SIMULATION));
    }

    public static final String CONFIG_NOTIFICATION_CHANNELS = "CONFIG_NOTIFICATION_CHANNELS";

    public String[] getNotificationChannels() {
        String[] sschannels = this.config.get(CONFIG_NOTIFICATION_CHANNELS).split(",");
        return sschannels;
    }

    public static final String CONFIG_AMQP_CLIENTID_EVENTS = "CONFIG_AMQP_CLIENTID_EVENTS";
    public static final String CONFIG_AMQP_CLIENTID_AUTHENTICATION = "CONFIG_AMQP_CLIENTID_AUTHENTICATION";
    public static final String CONFIG_AMQP_CLIENTID_REQUIREDACTION = "CONFIG_AMQP_CLIENTID_REQUIREDACTION";
    public String getAMQPClientEvents() {
        return this.config.get(CONFIG_AMQP_CLIENTID_EVENTS);
    }
    public String getAMQPClientAuth() {
        return this.config.get(CONFIG_AMQP_CLIENTID_AUTHENTICATION);
    }
    public String getAMQPClientRequiredAction() {
        return this.config.get(CONFIG_AMQP_CLIENTID_REQUIREDACTION);
    }
    
    public static final String CONFIG_AMQP_HOST = "CONFIG_AMQP_HOST";

    public String getAMQPHost() {
        return this.config.get(CONFIG_AMQP_HOST);
    }

    public static final String CONFIG_AMQP_PORT = "CONFIG_AMQP_PORT";

    public int getAMQPPort() {
        return Integer.parseInt(this.config.get(CONFIG_AMQP_PORT));
    }

    public static final String CONFIG_AMQP_QUEUE = "CONFIG_AMQP_QUEUE";

    public String getAMQPQueue() {
        return this.config.get(CONFIG_AMQP_QUEUE);
    }

    public static final String CONFIG_AMQP_QUEUE_EVENTS = "CONFIG_AMQP_QUEUE_EVENTS";

    public String getAMQPEvents() {
        return this.config.get(CONFIG_AMQP_QUEUE_EVENTS);
    }

    public static final String CONFIG_AMQP_QUEUE_EVENTS_ADMIN = "CONFIG_AMQP_QUEUE_EVENTS_ADMIN";

    public String getAMQPEventsAdmin() {
        return this.config.get(CONFIG_AMQP_QUEUE_EVENTS_ADMIN);
    }

    public static final String CONFIG_AMQP_USERNAME = "CONFIG_AMQP_USERNAME";

    public String getAMQPUsername() {
        return this.config.get(CONFIG_AMQP_USERNAME);
    }

    public static final String CONFIG_AMQP_PASSWORD = "CONFIG_AMQP_PASSWORD";

    public String getAMQPPassword() {
        return this.config.get(CONFIG_AMQP_PASSWORD);
    }

    public static final String CONFIG_AMQP_VHOST = "CONFIG_AMQP_VHOST";

    public String getAMQPVhost() {
        return this.config.get(CONFIG_AMQP_VHOST);
    }

    public static final String CONFIG_SHOULD_SKIP = "CONFIG_SHOULD_SKIP";

    public Boolean getNotificationShouldSkip() {
        return Boolean.parseBoolean(this.config.get(CONFIG_SHOULD_SKIP));
    }

    public static final String CONFIG_NOTIFICATION_TTL = "CONFIG_NOTIFICATION_TTL";

    public int getNotificationTTL() {
        return Integer.parseInt(this.config.get(CONFIG_NOTIFICATION_TTL));
    }

    public static final String CONFIG_NOTIFICATION_CODE_LENGTH = "CONFIG_NOTIFICATION_CODE_LENGTH";

    public int getNotificationCodeLength() {
        return Integer.parseInt(this.config.get(CONFIG_NOTIFICATION_CODE_LENGTH));
    }

    public static final String CONFIG_NOTIFICATION_SHOULD_SEND_ON_STARTUP = "CONFIG_NOTIFICATION_SHOULD_SEND_ON_STARTUP";

    public Boolean getNotificationShouldSendOnStartp() {
        return Boolean.parseBoolean(this.config.get(CONFIG_NOTIFICATION_SHOULD_SEND_ON_STARTUP));
    }

    public static final String CONFIG_DEFAULT_IS_SIMULATION = "false";
    public static final String CONFIG_DEFAULT_NOTIFICATION_SHOULD_SEND_ON_STARTUP = "false";
    public static final String CONFIG_DEFAULT_AMQP_CLIENTID_EVENTS = "mb_amqp_keycloak-events";
    public static final String CONFIG_DEFAULT_AMQP_CLIENTID_AUTHENTICATION = "mb_amqp_keycloak-auth";
    public static final String CONFIG_DEFAULT_AMQP_CLIENTID_REQUIREDACTION = "mb_amqp_keycloak-action";
    public static final String CONFIG_DEFAULT_AMQP_HOST = "localhost";
    public static final String CONFIG_DEFAULT_AMQP_PORT = "5672";
    public static final String CONFIG_DEFAULT_AMQP_QUEUE = "mbnotification";
    public static final String CONFIG_DEFAULT_AMQP_QUEUE_EVENTS = "mbevents";
    public static final String CONFIG_DEFAULT_AMQP_QUEUE_EVENTS_ADMIN = "mbeventsadmin";

    public static final String CONFIG_DEFAULT_NOTIFICATION_TTL = "300";
    public static final String CONFIG_DEFAULT_NOTIFICATION_OCDE_LENGTH = "6";
    public static final String CONFIG_DEFAULT_SHOULD_SKIP = "false";

    public static final String CONFIG_DEFAULT_AMQP_USERNAME = "guest";
    public static final String CONFIG_DEFAULT_AMQP_PASSWORD = "guest";
    public static final String CONFIG_DEFAULT_AMQP_VHOST = "/";

    public static final Integer TTL = 60 * 5;
    public static final Integer CODE_LENGTH = 6;
    public static final String SESSION_AUTH_NOTE_REFRESH = "mobile-x-refresh"; // Should be an integer to see how often
                                                                               // a user refreshed the site
    public static final String AUTH_NOTE_CODE = "mobile-x-code"; // The sessions name/key to store the mobile
                                                                 // verification code in the auth challenge
    public static final String AUTH_NOTE_TTL = "mobile-x-ttl"; // The sessions name/key to store the mobile verification
                                                               // ttl in the auth challenge
    public static final String AUTH_NOTE_SKIP = "mobile-x-skip"; // The sessions name/key to store the skip value

    public static final String ATTR_X_VERIFIED = "mobile-x-verified"; // Is the user verified
    public static final String ATTR_X_VERIFIED_TIMESTAMP = "mobile-x-timestamp"; // The success verification timestamp
    public static final String ATTR_X_CODE = "mobile-x-code";
    public static final String ATTR_X_CODE_TIMESTAMP = "mobile-x-number-timestamp"; // The timestap at which the code
                                                                                    // was sent
    public static final String ATTR_X_NUMBER = "mobile-x-number";
    public static final String ATTR_X_SKIP_ALLOWED = "mobile-x-skip-allowed"; // The user is allowed to skip; overwrites
                                                                              // global environemnt CONFIG_SHOULD_SKIP

    public static final String FORM_MOBILE_X_CODE = "mobile-x-code";
    public static final String FORM_MOBILE_X_NUMBER = "mobile-x-number";

    public static final String ATTR_X_SLECTED_CHANNEL = "mobile-x-selected-channel";

    // Mobile Tokens
    public static final String ATTR_X_MOBILE_TOKENS = "mobile-x-tokens"; // Is the user verified

    /**
     * ACTION REQUIRED
     */

    public static final String TEMPLATE_NAME_ACTIONREQUIRED = "action-mobile.ftl";

    public static final String TEMPLATE_ACTION_ERROR_CODE_MISSING = "mobileActionErrorCodeMissing";
    public static final String TEMPLATE_ACTION_ERROR_NUMBER_MISSING = "mobileActionErrorNumberMissing";
    public static final String TEMPLATE_ACTION_ERROR_CODE_WRONG = "mobileActionErrorCodeWrong";
    public static final String TEMPLATE_ACTION_ERROR_TTL = "mobileActionErrorTTL";
    public static final String TEMPLATE_ACTION_ERROR_INVALID_NUMBER = "mobileActionErrorInvalidNumber";

    public static final String TEMPLATE_ACTION_SEND_TEXT = "mobileActionSendText";


    public static final String TEMPLATE_ACTION_MOBILE_TOKEN_QR_CODE = "mobileActionQR";
    public static final String TEMPLATE_NAME_ACTIONREQUIRED_MOBILE_TOKEN = "action-mobile-token.ftl";

    /**
     * AUTHENTICATOR
     */
    public static final String TEMPLATE_NAME_AUTH = "auth-mobile.ftl";
    public static final String TEMPLATE_NAME_AUTH_EMAIL = "auth-email.ftl";

    public static final String QUERY_AUTH_NOTIFICATION_SECLETD = "notification";

    public static final String TEMPLATE_AUTH_PAGE_CHANNEL_SELECTED = "mobileAuthChannelSelected";
    public static final String TEMPLATE_AUTH_PAGE_CHANNELS = "mobileAuthChannels";

    public static final String TEMPLATE_AUTH_ERROR_SENT = "mobileAuthErrorSent"; // Error sending sms (amqp message)

    public static final String TEMPLATE_AUTH_SEND_TEXT = "mobileAuthSendText";
    public static final String TEMPLATE_AUTH_EMAIL_SEND_TEXT = "mobileAuthEmailSendText";
    public static final String TEMPLATE_AUTH_EMAIL_SUBJECT = "mobileAuthEmailSubject";

    public static final String TEMPLATE_AUTH_PAGE_REFRESH = "mobileAuthPageRefresh";

    public static final String TEMPLATE_AUTH_PAGE_TRIGGERED_SEND_CONDE = "mobileAuthPageTriggeredSendCode";
    public static final String TEMPLATE_AUTH_PAGE_SEND_OK = "mobileAuthSentOk";

    public static final String TEMPLATE_AUTH_PAGE_EMAIL_SUBJECT = "mobileAuthEmailSubject";

    /**
     * Initialzes a config Map to have a general config with pre-defined config
     * settings The config is set in the following priority: 1. Scope config via SPI
     * configuration 2. Environment parameters (scope config > env parameters; local
     * > global)
     * 
     * @param scope
     * @return Map<String, String>
     */
    private static Map<String, String> getConfig(Config.Scope scope) {
        Map<String, String> config = new HashMap<String, String>();

        String config_notification_channels = System
                .getenv(Utils.CONFIG_SYS_ENV_PREFIX + Utils.CONFIG_NOTIFICATION_CHANNELS);
        if (config_notification_channels == null) {
            config_notification_channels = Notification.Type.AMQP.name();
        }
        config.put(Utils.CONFIG_NOTIFICATION_CHANNELS,
                scope.get(Utils.CONFIG_NOTIFICATION_CHANNELS, config_notification_channels));

        String config_is_simulation = System.getenv(Utils.CONFIG_SYS_ENV_PREFIX + Utils.CONFIG_IS_SIMULATION);
        if (config_is_simulation == null) {
            config_is_simulation = Utils.CONFIG_DEFAULT_IS_SIMULATION;
        }
        config.put(Utils.CONFIG_IS_SIMULATION, scope.get(Utils.CONFIG_IS_SIMULATION, config_is_simulation));

        String config_send_on_startup = System
                .getenv(Utils.CONFIG_SYS_ENV_PREFIX + Utils.CONFIG_NOTIFICATION_SHOULD_SEND_ON_STARTUP);
        if (config_send_on_startup == null) {
            config_send_on_startup = Utils.CONFIG_DEFAULT_NOTIFICATION_SHOULD_SEND_ON_STARTUP;
        }
        config.put(Utils.CONFIG_NOTIFICATION_SHOULD_SEND_ON_STARTUP,
                scope.get(Utils.CONFIG_NOTIFICATION_SHOULD_SEND_ON_STARTUP, config_send_on_startup));

        String config_amqp_client_events = System
                .getenv(Utils.CONFIG_SYS_ENV_PREFIX + Utils.CONFIG_AMQP_CLIENTID_EVENTS);
        if (config_amqp_client_events == null) {
            config_amqp_client_events = Utils.CONFIG_DEFAULT_AMQP_CLIENTID_EVENTS;
        }
        config.put(Utils.CONFIG_AMQP_CLIENTID_EVENTS,
                scope.get(Utils.CONFIG_AMQP_CLIENTID_EVENTS, config_amqp_client_events));

        String config_amqp_client_auth = System
                .getenv(Utils.CONFIG_SYS_ENV_PREFIX + Utils.CONFIG_AMQP_CLIENTID_AUTHENTICATION);
        if (config_amqp_client_auth == null) {
            config_amqp_client_auth = Utils.CONFIG_DEFAULT_AMQP_CLIENTID_AUTHENTICATION;
        }
        config.put(Utils.CONFIG_AMQP_CLIENTID_AUTHENTICATION,
                scope.get(Utils.CONFIG_AMQP_CLIENTID_AUTHENTICATION, config_amqp_client_auth));

        String config_amqp_client_action = System
                .getenv(Utils.CONFIG_SYS_ENV_PREFIX + Utils.CONFIG_AMQP_CLIENTID_REQUIREDACTION);
        if (config_amqp_client_action == null) {
            config_amqp_client_action = Utils.CONFIG_AMQP_CLIENTID_REQUIREDACTION;
        }
        config.put(Utils.CONFIG_AMQP_CLIENTID_REQUIREDACTION,
                scope.get(Utils.CONFIG_AMQP_CLIENTID_REQUIREDACTION, config_amqp_client_action));

        String config_amqp_host = System.getenv(Utils.CONFIG_SYS_ENV_PREFIX + Utils.CONFIG_AMQP_HOST);
        if (config_amqp_host == null) {
            config_amqp_host = Utils.CONFIG_DEFAULT_AMQP_HOST;
        }
        config.put(Utils.CONFIG_AMQP_HOST, scope.get(Utils.CONFIG_AMQP_HOST, config_amqp_host));

        String config_amqp_port = System.getenv(Utils.CONFIG_SYS_ENV_PREFIX + Utils.CONFIG_AMQP_PORT);
        if (config_amqp_port == null) {
            config_amqp_port = Utils.CONFIG_DEFAULT_AMQP_PORT;
        }
        config.put(Utils.CONFIG_AMQP_PORT, scope.get(Utils.CONFIG_AMQP_PORT, config_amqp_port));

        String config_amqp_queue = System.getenv(Utils.CONFIG_SYS_ENV_PREFIX + Utils.CONFIG_AMQP_QUEUE);
        if (config_amqp_queue == null) {
            config_amqp_queue = Utils.CONFIG_DEFAULT_AMQP_QUEUE;
        }
        config.put(Utils.CONFIG_AMQP_QUEUE, scope.get(Utils.CONFIG_AMQP_QUEUE, config_amqp_queue));
        
        String config_amqp_queue_events = System.getenv(Utils.CONFIG_SYS_ENV_PREFIX + Utils.CONFIG_AMQP_QUEUE_EVENTS);
        if (config_amqp_queue_events == null) {
            config_amqp_queue_events = Utils.CONFIG_DEFAULT_AMQP_QUEUE_EVENTS;
        }
        config.put(Utils.CONFIG_AMQP_QUEUE_EVENTS,
                scope.get(CONFIG_SYS_ENV_PREFIX + Utils.CONFIG_AMQP_QUEUE_EVENTS, config_amqp_queue_events));

        String config_amqp_queue_events_admin = System
                .getenv(Utils.CONFIG_SYS_ENV_PREFIX + Utils.CONFIG_AMQP_QUEUE_EVENTS_ADMIN);
        if (config_amqp_queue_events_admin == null) {
            config_amqp_queue_events_admin = Utils.CONFIG_DEFAULT_AMQP_QUEUE_EVENTS_ADMIN;
        }
        config.put(Utils.CONFIG_AMQP_QUEUE_EVENTS_ADMIN,
                scope.get(Utils.CONFIG_AMQP_QUEUE_EVENTS_ADMIN, config_amqp_queue_events_admin));

        String config_amqp_username = System.getenv(Utils.CONFIG_SYS_ENV_PREFIX + Utils.CONFIG_AMQP_USERNAME);
        if (config_amqp_username == null) {
            config_amqp_username = Utils.CONFIG_DEFAULT_AMQP_USERNAME;
        }
        config.put(Utils.CONFIG_AMQP_USERNAME, scope.get(Utils.CONFIG_AMQP_USERNAME, config_amqp_username));

        String config_amqp_password = System.getenv(Utils.CONFIG_SYS_ENV_PREFIX + Utils.CONFIG_AMQP_PASSWORD);
        if (config_amqp_password == null) {
            config_amqp_password = Utils.CONFIG_DEFAULT_AMQP_PASSWORD;
        }
        config.put(Utils.CONFIG_AMQP_PASSWORD, scope.get(Utils.CONFIG_AMQP_PASSWORD, config_amqp_password));

        String config_amqp_vhost = System.getenv(Utils.CONFIG_SYS_ENV_PREFIX + Utils.CONFIG_AMQP_VHOST);
        if (config_amqp_vhost == null) {
            config_amqp_vhost = Utils.CONFIG_DEFAULT_AMQP_VHOST;
        }
        config.put(Utils.CONFIG_AMQP_VHOST, scope.get(Utils.CONFIG_AMQP_VHOST, config_amqp_vhost));

        String config_notification_ttl = System.getenv(Utils.CONFIG_SYS_ENV_PREFIX + Utils.CONFIG_NOTIFICATION_TTL);
        if (config_notification_ttl == null) {
            config_notification_ttl = Utils.CONFIG_DEFAULT_NOTIFICATION_TTL;
        }
        config.put(Utils.CONFIG_NOTIFICATION_TTL, scope.get(Utils.CONFIG_NOTIFICATION_TTL, config_notification_ttl));

        String config_notification_code_length = System
                .getenv(Utils.CONFIG_SYS_ENV_PREFIX + Utils.CONFIG_NOTIFICATION_CODE_LENGTH);
        if (config_notification_code_length == null) {
            config_notification_code_length = Utils.CONFIG_DEFAULT_NOTIFICATION_OCDE_LENGTH;
        }
        config.put(Utils.CONFIG_NOTIFICATION_CODE_LENGTH,
                scope.get(Utils.CONFIG_NOTIFICATION_CODE_LENGTH, config_notification_code_length));

        String config_should_skip = System.getenv(Utils.CONFIG_SYS_ENV_PREFIX + Utils.CONFIG_SHOULD_SKIP);
        if (config_should_skip == null) {
            config_should_skip = Utils.CONFIG_DEFAULT_SHOULD_SKIP;
        }
        config.put(Utils.CONFIG_SHOULD_SKIP, scope.get(Utils.CONFIG_SHOULD_SKIP, config_should_skip));

        LOG.info("Utils config:");
        for (Map.Entry<String, String> entry : config.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            LOG.infof("%s : %s", key, value);
        }

        return config;
    }

    /**
     * Validates a mobile number
     * 
     * @param mobileNumber
     * @return boolean
     */
    public boolean mobileNumberIsValid(String mobileNumber) {

        // The given argument to compile() method
        // is regular expression. With the help of
        // regular expression we can validate mobile
        // number for which we create an object of
        // Pattern class

        Pattern p = Pattern.compile("^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$");

        // Pattern class contains matcher() method
        // to find matching between given number
        // and regular expression by creating an object of
        // Matcher class
        Matcher m = p.matcher(mobileNumber);

        // Returns boolean value
        return (m.matches());
    }

    /**
     * Reset a given usermodel by removing user's attributes ATTR_X_NUMBER
     * ATTR_X_CODE ATTR_X_CODE_TIMESTAMP ATTR_X_NUMBER ATTR_X_VERIFIED_TIMESTAMP
     * ATTR_X_VERIFIED
     * 
     * @param user
     */
    public void resetUser(UserModel user) {
        LOG.info("resetUser");
        user.removeAttribute(Utils.ATTR_X_NUMBER);
        user.removeAttribute(Utils.ATTR_X_CODE);
        user.removeAttribute(Utils.ATTR_X_CODE_TIMESTAMP);
        user.removeAttribute(Utils.ATTR_X_NUMBER);
        user.removeAttribute(Utils.ATTR_X_VERIFIED_TIMESTAMP);
        user.removeAttribute(Utils.ATTR_X_VERIFIED);
    }

    private Map<String, String> config;

    public Utils(Config.Scope scope) {
        this.config = getConfig(scope);
    }

}
