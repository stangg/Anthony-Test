<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>  
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>  

<script type="text/javascript" src="jsp/Event.js"></script>
<script type="text/javascript" src="jsp/SortedTable.js"></script>

		<script type="text/javascript">//<![CDATA[
			var sourceTable, destTable;
			onload = function() {
				mySorted = new SortedTable();
				mySorted.colorize = function() {
					for (var i=0;i<this.elements.length;i++) {
						if (i%2){
							this.changeClass(this.elements[i],'even','odd');
						} else {
							this.changeClass(this.elements[i],'odd','even');
						}
					}
				}
				mySorted.onsort = mySorted.colorize;
				mySorted.onmove = mySorted.colorize;
				mySorted.colorize();
			}
			function moveRows(s,d) {
				var a = new Array();
				for (var o in s.selectedElements) {
					a.push(s.selectedElements[o]);
				}
				for (var o in a) {
					var elm = a[o];
					var tds = elm.getElementsByTagName('td');
					for (var i in tds) {
						if (tds[i].headers) tds[i].headers = d.table.id+''+tds[i].headers.substr(d.table.id.length);
					}
					d.body.appendChild(a[o]);
					d.deselect(a[o]);
					d.init(d.table);
					d.sort();
					s.deselect(a[o]);
					s.init(s.table);
				}
			}
		//]]></script>
		<style type="text/css">
			/* document styles */
			.disclaimer {border-top:1px solid #ccc;color:#879AB7;padding-top:.5em;font-size:.9em;}
			dl {margin:0 1em;padding:0;}
			dl, form {float:left;}
			li {padding:.1em 0;}
			hr {clear:both;width:100%;background:#fff;height:0;border:0;border-bottom:1px solid #fff;margin:0 0 1em;padding:0;}
			
			/* table styles */
			table {border:0;padding:0;margin:0 0 1em;border-left:1px solid #336;border-top:1px solid #336;float:left;clear:left;}
			tr {border:0;padding:0;margin:0;}
			td, th {border:0;padding:2px 6px;margin:0;border-right:1px solid #336;border-bottom:1px solid #336;background-color:#EAEEF3;}
			td[axis='number'], td[axis='date'] {text-align:right;}
			th {white-space:no-wrap;background-color:#B4C4D1;padding:2px 20px;}
			tfoot td {border-top:1px solid #003;}
			thead th {border-bottom:2px solid #003;}
			.odd td {background-color:#E8ECF1;}
			.even td {background-color:#DDE5EB;}
			.hover td {background-color:#A5B3C9;}
			.sortedminus {background-color:#ecc;}
			.sortedplus {background-color:#cec;}
			.selrow td {background-color:#879AB7;}
		</style>
	</head>
<html>
	<head>
		<title>Batch No Lookup</title>
	</head>
	<body class="tundra">
		<h1 align="center" style="font-size: 24px; color: rgb(88, 0, 0);">Batch No Lookup<img src="jsp/gosh_logo.gif"/></h1> 
		<table class="sorted regroup">
			<thead>
				<tr>
					<th id="File">File name</th>
					<th id="Batch" class="sortedplus">Batch No.</th>
					<th id="Date">Date validated</th>
					<th id="No">No of Records</th>
					<th id="Supplier">Supplier</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${values}" var="entry">
					<tr>
						<td headers="File" axis="string"><c:out value="${entry.fileName}"/></td>
						<td headers="Batch" axis="number"><c:out value="${entry.batchNo}"/></td>
						<td headers="Date" axis="date"><c:out value="${entry.dateValidated}"/></td>
						<td headers="No" axis="number"><c:out value="${entry.noOfRecords}"/></td>
						<td headers="Supplier" axis="string"><c:out value="${entry.supplierName}"/></td>
					</tr>
				</c:forEach>
			</tbody>
        </table>
	</body>
</html>