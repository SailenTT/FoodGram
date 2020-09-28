<?php
include "connectToDB.php";

if(isset($_POST['ID_Post'])&&isset($_POST['ID_User'])&&isset($_POST['comment'])){
    $ID_Post=$_POST['ID_Post'];
    $ID_User=$_POST['ID_User'];
    $comment=$_POST['comment'];

	/*Query utilizzata per ottenere i dati dell'utente che ha inserito il commento
      per poi restituirli all'applicazioni in modo tale che quest'ultima possa inserire
      subito il commento nella sua interfaccia grafica, senza dover effettuare una richiesta http aggiuntiva.
      Ciò alleggerisce il carico di richieste HTTP agli scrip PHP e il carico di Query al database
    */
    $getUserInfoQuery="SELECT name, surname FROM Users WHERE ID_User=$ID_User";
    
    $addCommentQuery="INSERT INTO Comments (ID_User,ID_Post,comment,date) VALUE($ID_User,$ID_Post,'$comment',NOW())";
    
    if($db->query($addCommentQuery)){

        $result=$db->query($getUserInfoQuery);

        if($result->num_rows>0){

            $row=$result->fetch_assoc();

            http_response_code(200); //operazione effettuata con successo

            echo json_encode($row);
        }
        else{
          	http_response_code(500);
        }
    }

}
else{
	http_response_code(400);
}
?>