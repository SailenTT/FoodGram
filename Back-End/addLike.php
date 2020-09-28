<?php
include "connectToDB.php";

if(isset($_GET['ID_Post'])&&isset($_GET['ID_User'])){
	$ID_Post=$_GET['ID_Post'];
    $ID_User=$_GET['ID_User'];
    
    $checkLikeQuery="SELECT ID_Post,ID_User FROM Likes WHERE ID_Post=$ID_Post AND ID_User=$ID_User";
    
  	if($db->query($checkLikeQuery)->num_rows==0){

        $insertLikeQuery="INSERT INTO Likes (ID_Post,ID_User) VALUE ($ID_Post,$ID_User)";

        if($db->query($insertLikeQuery)){
          	http_response_code(200); //operazione effettuata con successo
        }
        else{
          	http_response_code(500);
        }
    }
    else{
    	http_response_code(503);
    }
}
else{
	http_response_code(400);
}
?>