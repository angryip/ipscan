<?php
$headers = apache_request_headers();

foreach ($headers as $header => $value) {
    echo "$header: $value <br />\n";
}
echo "new <br />";
foreach ($_FILES as $key => $value) echo $key . "<>" . $value . "<br/>\n";
echo file_get_contents('php://input');
?> 