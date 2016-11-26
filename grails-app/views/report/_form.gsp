<%@ page import="com.testacl.Report" %>



<div class="fieldcontain ${hasErrors(bean: reportInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="report.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${reportInstance?.name}"/>
</div>

