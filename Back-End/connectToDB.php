<?php
$HOSTNAME="localhost";
$USERNAME="davidpoletti";
$PASSWORD="";
$DATABASE="FoodGram";

$db=new mysqli($HOSTNAME,$USERNAME,$PASSWORD,$DATABASE);

if($db->connect_error){
    die("errore nella connessione");
}

?>