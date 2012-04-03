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
		<form:form>
    		<table align="center">
          		<tr>
              		<td>Sort Code:</td>
              		<td><input type="text" name="bankSort"/></td>
              		<td><form:errors path="bankSort"/></td>
          		</tr>
          		<tr>
             		<td colspan="2">
                  		<input type="submit" value="Post" />
              		</td>
          		</tr>
      		</table>
  		</form:form>
	</body>
</html>