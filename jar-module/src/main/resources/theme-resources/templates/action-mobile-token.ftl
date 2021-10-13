<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true displayMessage=true; section>
	<#if section = "header">
		${msg("mobileActionTokenTitle",realm.displayName)}
	<#elseif section = "form">
		<form id="kc-mobile-mobile-x-code-login-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
			
				<div class="${properties.kcFormGroupClass!}">
					<div class="${properties.kcLabelWrapperClass!}" style="display: flex; width: 100%; flex-direction: column;align-items: center;justify-items: center;justify-content: center;">
						<label for="mobile-x-number" class="${properties.kcLabelClass!}" style="display: inline-flex;justify-content: center;">${msg("mobileActionTokenHelpText")}</label>
						<input hidden type="text" id="mobile-x-number" name="mobile-x-number" class="${properties.kcInputClass!}" autofocus/>
                        <#if mobileActionQR?? >
                            <img src="data:image/png;base64,${mobileActionQR}" width=="200px" height="200px" />
                        </#if>

						<label  for="mobile-x-number2" class="${properties.kcLabelClass!}" style="display: none;justify-content: center;">${mobileActionQRurl}</label>

					</div>				
				</div>
			
		</form>
	<#elseif section = "info" >
		${msg("mobileActionTokenInstruction")}
	</#if>
</@layout.registrationLayout>
