<?php
include "connectToDB.php";

/*
Script non implementato nell'applicazione ma che verrà utilizzato in build future e permetterà
di rimuovere un mi piace inserito da un utente
*/
if(isset($_GET['ID_Like'])){
    $idLike=$_GET['ID_Like'];

    $query="DELETE FROM Likes WHERE ID_Like=$idLike";

    if($db->query($query)){
        http_response_code(200); //operazione effettuata con successo
    }
    else{
        http_response_code(400); //bad request
    }
}
else{
	http_response_code(400);
}

?>