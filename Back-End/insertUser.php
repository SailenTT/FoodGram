<?php
include "connectToDB.php";

//Controllo che i parametri necessari per la registrazione siano stati inseriti
if(isset($_POST['email'])&&isset($_POST['password'])&&isset($_POST['name'])&&isset($_POST['surname'])){
    $email=$_POST['email'];
    $password=hash("sha256",$_POST['password']);
    $name=$_POST['name'];
    $surname=$_POST['surname'];

    $query="INSERT INTO Users (email,password,name,surname) VALUE('$email','$password','$name','$surname')";

    if($db->query($query)){
        http_response_code(200); //operazione effettuata con successo
    }
    else{
        http_response_code(503); //servizio non disponible
    }
}
else{
	http_response_code(400); //richiesta errata (nel caso non siano stati inviati tutti i parametri necessari allo script per effettuare la registrazione)
}

?>