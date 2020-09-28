<?php
include "connectToDB.php";

/*
Questo script serve ad ottenere la lista dei ristoranti nel database
per inserirli nello spinner presente nella sezione inserimento di un nuovo post dell'applicazione
*/

$query="SELECT ID_Restaurant,name FROM Restaurants";

$result=$db->query($query);

if($result->num_rows>0){
	$array=array();

	while($row=$result->fetch_assoc()){
    	$array[]=$row;
    }
    
    echo json_encode($array);
    
    http_response_code(200);
}
else{
	http_response_code(204);
}

?>