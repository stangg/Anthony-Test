<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
 <!--pageEncoding="UTF-8" %>-->

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>  
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<html>
	<head>
		<title>Validation Bank Lookup Simulator</title>
	</head>
	<body class="tundra">
		<h1 align="center" style="font-size: 24px; color: rgb(88, 0, 0);">Validation Bank Lookup Simulator<img src="jsp/gosh_logo.gif"/></h1> 
		<table>
        	<tr>
           		<td>The bank list contained the following xml.</td>
        	</tr>
        	<tr>
           		<td>BankSort:</td>
           		<td>${bank.bankSort}</td>
        	</tr>
        	<tr>
           		<td>BankName:</td>
           		<td>${bank.bankName}</td>
        	</tr>        	
        	<tr>
           		<td>BranchName:</td>
           		<td>${bank.branchName}</td>
        	</tr>   
        	<tr>
           		<td>BankPost:</td>
           		<td>${bank.bankPost}</td>
        	</tr>         	
        </table>
	</body>
</html>