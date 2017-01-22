<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
    <title>sunum</title>
</head>
<body>

<form:form method="POST" action="ceng553/q1">
            <input type="submit" value="List all actors ( userid and fullname ) who are also directors."/>
</form:form>
<form:form method="POST" action="ceng553/q2">
            <input type="submit" value="List all actors ( userid and fullname ) who acted in 5 or more movies."/>
</form:form>
<form:form method="POST" action="ceng553/q3">
            <input type="submit" value="How many actors have acted in same movies with Edward Norton?"/>
</form:form>
<form:form method="POST" action="ceng553/q4">
            <input type="submit" value="Which collectors collected all movies in which Edward Norton acts?"/>
</form:form>
<form:form method="POST" action="ceng553/q5">
            <input type="submit" value="List 10 collectors ( userid and fullname ) who collect The Shawshank Redemption."/>
</form:form>
<form:form method="POST" action="ceng553/q6">
            <input type="submit" value="6"/>
</form:form>
</body>
</html>