<?php
include "connectToDB.php";

/*
Non essendo possibile inviare in una richiesta http ulteriori dati assieme ad un immagine
è necessario l'uso di uno script a parte come questo che riceva esclusivamente l'immagine da inserire nel database
*/
if(isset($_POST['image'])){
	/*
    Quando l'immagine viene inviata da Android al server, le parti della stringa contente il carattere "+" 
    vengono sostituite con il carattere " ". Qui di seguito viene quindi ripristinata la stringa ai suoi valori originali
    per evitare errori nel caricamento dell'immagine
    */
    $image=base64_decode(str_replace(" ","+",$_POST['image']));
    $imgDirectory="images/img";

	/*
    Dato che lo script può ricevere solo l'immagine e non ulteriori dati, 
    è necessario sapere a quale post deve essere associata l'immagine.
    Per fare ciò, tramite una query viene preso l'ID dell'ultimo post inserito
    */
    $getPostIdQuery="SELECT ID_Post FROM Posts ORDER BY date DESC LIMIT 1";

    $lastPostID=$db->query($getPostIdQuery)->fetch_assoc()['ID_Post'];

	/*
    L'ID del post a cui l'immagine appartiene viene inserito nel nome dell'immagine
    per fare in modo che le immagini possiedano un nome univoco ciascuna
    */
    $imgDirectory.=$lastPostID.".png";

    file_put_contents($imgDirectory,$image);
    
    /*
    Utilizzo un URL assoluto perché l'applicazione dovrà effettuare il caricamento dell'immagine
    di un post dato il suo URL, quindi sarebbe ridondante e mal ottimizzato utilizzare un URL
    locale perché costringerebbe l'applicazione a concatenare ogni volta all'URL fornito una stringa
    utilizzata per trasformarlo in un URL assoluto
    */
    $imgDirectory="http://davidpoletti.altervista.org/FoodGram/".$imgDirectory;

    $updatePostQuery="UPDATE Posts SET imgUrl='$imgDirectory' WHERE ID_Post=$lastPostID";

    if($db->query($updatePostQuery)){
      http_response_code(200); //operazione effettuata con successo
    }
    else{
      http_response_code(500); //errore interno al server
    }
}
else{
	http_response_code(400);  //richiesta errata
}

?>
