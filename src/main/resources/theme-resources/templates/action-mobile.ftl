<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true displayMessage=true; section>
	<#if section = "header">
		${msg("mobileActionTitle",realm.displayName)}
	<#elseif section = "form">
		<form id="kc-mobile-mobile-x-code-login-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
			
			<#-- The mobile number is missing in the user's attributes -->
			<#if messagesPerField.existsError('mobileActionErrorNumberMissing') || messagesPerField.existsError('mobileActionErrorTTL') || messagesPerField.existsError('mobileActionErrorInvalidNumber')>
				<div class="${properties.kcFormGroupClass!}">
					<div class="${properties.kcLabelWrapperClass!}">
						<label for="mobile-x-number" class="${properties.kcLabelClass!}">${msg("mobileActionErrorNumberMissingLabel")}</label>
					</div>
					<div class="${properties.kcInputWrapperClass!}">
						<input type="text" id="mobile-x-number" name="mobile-x-number" class="${properties.kcInputClass!}" autofocus/>
						
						<#if messagesPerField.existsError('mobileActionErrorNumberMissing')>
							<span id="input-error-mobile-x-error-code-missing-number" class="${properties.kcInputErrorMessageClass!}"
								aria-live="polite">
								${kcSanitize(messagesPerField.get('mobileActionErrorNumberMissing'))?no_esc}
							</span>
						</#if>

						<#if messagesPerField.existsError('mobileActionErrorTTL')>
							<span id="input-error-mobile-x-error-code-ttl" class="${properties.kcInputErrorMessageClass!}"
								aria-live="polite">
								${kcSanitize(messagesPerField.get('mobileActionErrorTTL'))?no_esc}
							</span>
						</#if>

						<#if messagesPerField.existsError('mobileActionErrorInvalidNumber')>
							<span id="input-error-mobile-x-error-code-invalid-number" class="${properties.kcInputErrorMessageClass!}"
								aria-live="polite">
								${kcSanitize(messagesPerField.get('mobileActionErrorInvalidNumber'))?no_esc}
							</span>
						</#if>

					</div>				
				</div>
			
			<#else>
			<#-- The mobile is in in the user's attributes; no other errors like TTL too short -->

				<div class="${properties.kcFormGroupClass!}">
					<div class="${properties.kcLabelWrapperClass!}">
						<label for="mobile-x-code" class="${properties.kcLabelClass!}">${msg("mobileActionLabel")}</label>
					</div>
					<div class="${properties.kcInputWrapperClass!}">
						<input type="text" id="mobile-x-code" name="mobile-x-code" class="${properties.kcInputClass!}" autofocus/>
						
						<#if messagesPerField.existsError('mobileActionErrorCodeMissing')>
							<span id="input-error-mobile-x-error-code-missing-code" class="${properties.kcInputErrorMessageClass!}"
								aria-live="polite">
								${kcSanitize(messagesPerField.get('mobileActionErrorCodeMissing'))?no_esc}
							</span>
						</#if>

						<#if messagesPerField.existsError('mobileActionErrorCodeWrong')>
							<span id="input-error-mobile-x-error-code-wrong-code" class="${properties.kcInputErrorMessageClass!}"
								aria-live="polite">
								${kcSanitize(messagesPerField.get('mobileActionErrorCodeWrong'))?no_esc}
							</span>
						</#if>
					</div>				
				</div>

			</#if>

			<div class="${properties.kcFormGroupClass!} ${properties.kcFormSettingClass!}">
				<div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
					<div class="${properties.kcFormOptionsWrapperClass!}">
						<span><a href="${url.loginRestartFlowUrl}">${kcSanitize(msg("backToLogin"))?no_esc}</a></span>
					</div>
				</div>

				<div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
					<input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" type="submit" value="${msg("doSubmit")}"/>
				</div>

				<div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
					<input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" type="submit" name="reset" value="${msg("doTryAgain")}"/>
				</div>

				<div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
					<input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" type="submit" name="skip" value="${msg("doIgnore")}"/>
				</div>
			</div>
		</form>
	<#elseif section = "info" >
		${msg("mobileActionInstruction")}
	</#if>
</@layout.registrationLayout>
