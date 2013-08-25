<?php
/* 
   PUT data comes in on the stdin stream, which is php://input
   for PHP compiled as a module, although most documentation claim
   it's php://stdin
*/
$putdata = fopen("php://input", "r");
$fp = fopen("m2.bmp", "w");
$i = 0;
while ($data = fread($putdata, 1024))
{
    echo "data pass: " . $i++ . "\n";
    fwrite($fp, $data);
}
fflush($fp);
fclose($fp);
fclose($putdata);
copy("m2.bmp", $request_uri);
?>
