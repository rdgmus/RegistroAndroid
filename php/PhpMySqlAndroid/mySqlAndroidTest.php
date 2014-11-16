<?php
if(
mysql_connect("localhost","root","myzconun")){
mysql_select_db("scuola");
$sql=mysql_query("SELECT `id_ruolo`, `ruolo` FROM `ruoli_utenti` WHERE 1");
while($row=mysql_fetch_assoc($sql))
	$output[]=$row;
print(json_encode($output));// this will print the output in json
mysql_close();
}else{
	print("[{}]");
}
?>
