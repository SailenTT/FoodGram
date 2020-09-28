<?php
include "connectToDB.php";

if(isset($_POST['ID_User'])&&isset($_POST['ID_Restaurant'])){
	$ID_User=$_POST['ID_User'];
    $ID_Restaurant=$_POST['ID_Restaurant'];
    

    //Tramite la funzione di SQL NOW() ottengo la data e il tempo attuale
    $insertQuery="INSERT INTO Posts (ID_User,ID_Restaurant,imgUrl,date) VALUE($ID_User,$ID_Restaurant,'',NOW())";

    if($db->query($insertQuery)){

        http_response_code(200); //operazione effettuata con successo
    }
    else{
        http_response_code(503); //servizio non disponibile a causa di un errore di inserimento
    }
}
else{
    http_response_code(400); //richiesta errata
}

?>
