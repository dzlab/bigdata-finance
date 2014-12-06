<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">    
<head>
     <style type="text/css">
        #flot-placeholder{width:350px;height:300px;}        
    </style>
    <!--[if lte IE 8]><script language="javascript" type="text/javascript" src="http://www.pureexample.com/js/flot/excanvas.min.js"></script><![endif]-->
    <script type="text/javascript" src="http://www.pureexample.com/js/lib/jquery-1.8.3.min.js"></script>
    <script type="text/javascript" src="http://www.pureexample.com/js/flot/jquery.flot.min.js"></script>
    <script type="text/javascript">
        var data = [[1, 130], [2, 40], [3, 80], [4, 160], [5, 159], [6, 370], [7, 330], [8, 350], [9, 370], [10, 400], [11, 330], [12, 350]];
 
        var dataset = [{label: "line1",data: data}];
 
        var options = {
            series: {
                lines: { show: true },
                points: {
                    radius: 3,
                    show: true
                }
            }
        };
 
        $(document).ready(function () {
            $.plot($("#flot-placeholder"), dataset, options);
        });
    </script>
</head>
<body>
    <div id="flot-placeholder"></div>
</body>
</html>