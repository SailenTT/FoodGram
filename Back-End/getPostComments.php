<?php
include "connectToDB.php";

if(isset($_GET['ID_Post'])){
    $ID_Post=$_GET['ID_Post'];

    $query="SELECT u.ID_User, u.name, u.surname, c.comment FROM Comments c INNER JOIN Users u ON c.ID_User=u.ID_User WHERE c.ID_Post=$ID_Post ORDER BY date ASC";

    $result=$db->query($query);

    $array=array();

        if($result->num_rows>0) {
            while ($row = $result->fetch_assoc()){
                $array[] = $row;
            }

            $json=json_encode($array);

            echo $json;
            
            http_response_code(200); //operazione effettuata con successo
        }
        else{
            http_response_code(204); //no result
        }
}
else{
	http_response_code(400);
}

?>