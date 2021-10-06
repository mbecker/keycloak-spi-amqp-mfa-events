<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
	<#if section = "header">
		${msg("mobileAuthTitle",realm.displayName)}
	<#elseif section = "form">
		<form id="kc-sms-mobile-x-code-login-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
			<div class="${properties.kcFormGroupClass!}">
				<div class="${properties.kcLabelWrapperClass!}">
					<label for="mobile-x-code" class="${properties.kcLabelClass!}">${msg("mobileActionLabel")}</label>
				</div>
				<div class="${properties.kcInputWrapperClass!}">
					<input type="text" id="mobile-x-code" name="mobile-x-code" class="${properties.kcInputClass!}" autofocus/>
				</div>
			</div>
			<div class="${properties.kcFormGroupClass!} ${properties.kcFormSettingClass!}">
				<div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
					<div class="${properties.kcFormOptionsWrapperClass!}">
						<span><a href="${url.loginRestartFlowUrl}">${kcSanitize(msg("backToLogin"))?no_esc}</a></span>
					</div>
				</div>

				<#-- The user should manually trigger the sending of the verification code -->
				<#if messagesPerField.existsError('mobileAuthPageRefresh')>
					<div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
						<input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" type="submit" name="reset" value="${msg("authenticatorCode")}"/>
					</div>
				<#else>
					<#--The user should got a verification code; either he manually triggered it or the code was sent on startup -->
					<div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
						<input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" type="submit" value="${msg("doSubmit")}"/>
					</div>

					<div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
						<input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" type="submit" name="reset" value="${msg("doTryAgain")}"/>
					</div>
				</#if>

				<div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
					<input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" type="submit" name="skip" value="${msg("doIgnore")}"/>
				</div>
				
			</div>
		</form>
	<#elseif section = "info" >
		${msg("mobileAuthInstruction")}
	</#if>
</@layout.registrationLayout>