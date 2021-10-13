package com.mbecker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.mbecker.gateway.MobileToken;

import org.jboss.logging.Logger;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;

public class RequiredActionMobileToken implements RequiredActionProvider {

    private static final Logger LOG = Logger.getLogger(RequiredActionMobileToken.class);

    private Utils utils;

    private ArrayList<FormMessage> errors;
    private ArrayList<FormMessage> success;

    public RequiredActionMobileToken(Utils utils) {
        this.utils = utils;
        this.errors = new ArrayList<FormMessage>();
        this.success = new ArrayList<FormMessage>();
    }

    @Override
    public void close() {
        LOG.info("Close");
    }

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        // Called every time a user authenticates
        // Check that the user's attribute "moble token" is set and at least one
        // MobileToken exists
        // If yes then remove the required action (just in case)
        // If no then reset the user and add the required action to add a new mobile
        // number
        LOG.info("evaluateTriggers");
        UserModel user = context.getUser();
        MobileToken[] mobileTokens = getMobileTokensFromUserAttributes(user);

        if (mobileTokens == null || mobileTokens.length == 0) {
            LOG.info("User's mobile token is either null or empty; add required action");
            user.addRequiredAction(RequiredActionMobileTokenFactory.PROVIDER_ID);
        } else {
            LOG.info("User's mobile token is not empty; remove required action");
            user.removeRequiredAction(RequiredActionMobileTokenFactory.PROVIDER_ID);
        }
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {

        UserModel user = context.getUser();
        MobileToken[] mobileTokens = getMobileTokensFromUserAttributes(user);

        if (mobileTokens != null && mobileTokens.length > 0) {
            LOG.info("User has more than mobile tokens");
            context.success();
            return;
        }

        LoginFormsProvider form = context.form();
        form.setAttribute("realm", context.getRealm());
        String image;
        try {
            String url = context.getActionUrl(true).toString();
            image = generateQrCode(url, 200);
            form.setAttribute(Utils.TEMPLATE_ACTION_MOBILE_TOKEN_QR_CODE, image);
            form.setAttribute(Utils.TEMPLATE_ACTION_MOBILE_TOKEN_QR_CODE + "url", url);
        } catch (WriterException e) {
            LOG.error(e);
        } catch (IOException e) {
            LOG.error(e);
        }

        for (int i = 0; i < this.errors.size(); i++) {
            form.addError(this.errors.get(i));
        }
        for (int i = 0; i < this.success.size(); i++) {
            form.addSuccess(this.success.get(i));
        }

        this.errors.clear();
        this.success.clear();

        context.challenge(form.createForm(Utils.TEMPLATE_NAME_ACTIONREQUIRED_MOBILE_TOKEN));
        return;
    }

    @Override
    public void processAction(RequiredActionContext context) {
        requiredActionChallenge(context);
    }

    private String generateQrCode(String url, int imageSize) throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, imageSize, imageSize);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "png", bos);

        return Base64.getEncoder().encodeToString(bos.toByteArray()); // base64 encode
    }

    private MobileToken[] getMobileTokensFromUserAttributes(UserModel user) {
        Gson gson = new Gson();

        String mobileXToken = user.getFirstAttribute(Utils.ATTR_X_MOBILE_TOKENS);

        return gson.fromJson(mobileXToken, MobileToken[].class);
    }

}
