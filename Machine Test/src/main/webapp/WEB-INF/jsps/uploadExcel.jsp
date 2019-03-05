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
    <title>Upload Excel</title>
</head>
<body>


    <form action="/uploadMultipleFiles" method="post" enctype = "multipart/form-data">
        <pre>
            Select Excel File :
            <input type = "file" name = "files" multiple="multiple"/>
         <br />
         <input type = "submit" value = "Upload File"  />
        </pre>
    </form>
</body>
</html>
