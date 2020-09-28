<?php
include "connectToDB.php";

//Controllo che i parametri necessari per il login siano stati inseriti
if(isset($_POST['email'])&&isset($_POST['password'])){
    $email=$_POST['email'];
    
    //Cifro la password utilizzando la funzione di hash sha256
    $password=hash("sha256",$_POST['password']);

    $query="SELECT ID_User, name, surname FROM Users WHERE email='$email' AND password='$password'";

    $result=$db->query($query);

    if($result->num_rows>0) {

        $result = $result->fetch_assoc();

        $json = json_encode($result);

        http_response_code(200); //operazione effettuata con successo

        echo $json;
    }
    else{
        http_response_code(204); //no result
    }
}
else{
	http_response_code(400);
}
?>