<?php
include "connectToDB.php";

//sortby indica il tipo di ordine in cui vengono presi i post dal database
if(isset($_GET['sortby'])){
    $sort=$_GET['sortby'];

    $query="SELECT p.ID_Post, imgUrl, p.ID_Restaurant, p.ID_User, r.name AS restaurantName, COUNT(l.ID_Like) as numLikes, u.name AS userName, u.surname FROM Posts p INNER JOIN Restaurants r INNER JOIN Users u ON  p.ID_Restaurant=r.ID_Restaurant AND p.ID_User=u.ID_User LEFT JOIN  Likes l ON p.ID_Post=l.ID_Post GROUP BY p.ID_Post ORDER BY ";

	//A seconda del tipo di ordinamento richiesto cambia l'ordinamento effettuato dalla query stessa
    switch ($sort){
        case 'top':
            $query.="numLikes DESC";
            break;

        case 'new':
            $query.="p.date DESC";
            break;

        case 'old':
            $query.="p.date ASC";
    }

    $result=$db->query($query);

    if($result->num_rows>0) {

        $array = array();

        while ($row = $result->fetch_assoc()) {
            $array[] = $row;
        }

        $json = json_encode($array);

        echo "$json";
        
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