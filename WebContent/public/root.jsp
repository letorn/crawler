<%@ page contentType="text/html; charset=utf-8"%>
<%
  String ctx = request.getContextPath();
  // String ctx = String.format("%s://%s:%d%s", request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
  request.setAttribute("ctx", ctx);
%>
<script type="text/javascript">var ctx = "${ctx}";</script>