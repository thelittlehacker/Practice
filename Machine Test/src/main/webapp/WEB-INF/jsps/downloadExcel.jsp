<%--
  Created by IntelliJ IDEA.
  User: Aditya Pranav
  Date: 05-03-2019
  Time: 03:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page isELIgnored="false" %>
<html>
<head>
    <title>Download  - File according to type</title>
</head>
<body>
<c:forEach items="${filename}" var="file" varStatus="status">
    <c:out value="${status.count}"/> - Excel File Name   :-  ${file} <br><br>
   </c:forEach> <br><br>
<c:forEach items="${columnname}" var="col" varStatus="status">
    <c:out value="${status.count}"/> - File Column Name :-  ${col} <br><br>
</c:forEach>

<form action="download" method="post">

    <pre>
    Column Name :  <input type="text" name="columnName">

    File Type : <select id="filetype" name="filetype">
                <option value="xlsx" selected>xlsx</option>
                <option value="csv" >csv</option>
                <option value="txt">text</option>
                </select>

    Delimiter : <select id="delimiter" name="delimiter">
                <option value="*" selected>*</option>
                <option value="|" >|</option>

                </select>

    <input type = "submit" value = "Download"  />

    </pre>
</form>

</body>
</html>
