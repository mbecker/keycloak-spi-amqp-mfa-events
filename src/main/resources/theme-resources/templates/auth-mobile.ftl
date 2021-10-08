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
					<#-- The scope/env config includes multipls notification channels like AMQP, EMAIl; show the button that the the user can trigger it's favourite notification channel -->
					<#if mobileAuthChannels?? && (mobileAuthChannels?size gt 1) >
						<div class="${properties.kcFormOptionsWrapperClass!}" style="margin-top: 1em;display: flex;flex-direction: column;">
							<span class="${properties.kcLabelClass!}" style="font-weight: 600;">${msg("mobileAuthSelectChannelFor")}${mobileAuthChannelSelected}</span>
							<span class="${properties.kcLabelClass!}" style="font-weight: 600;">${msg("mobileAuthSelectChannelSwitchTo")}</span>
							<#list mobileAuthChannels as channel>
									<#if channel != mobileAuthChannelSelected>
										<input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" type="submit" name="notification" value="${channel}" />
									</#if>
								</#list>
						</div>
					</#if>
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