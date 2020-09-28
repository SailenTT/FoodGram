package davidpoletti.foodgram.com;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/*
Un AsyncTask è un thread utilizzato per svolgere operazioni leggere e permette di comunicare con il thread principale che gestisce la UI

Tutte le risposte che questo Task riceve vengono codificate in formato JSON
 */
public class NetworkActivtiesTask extends AsyncTask <Object,Void,Object> {
    //Ad ogni operazione viene assegnato un valore
    public static final int LOGIN_OPERATION=0;
    public static final int SIGN_UP_OPERATION=1;
    public static final int GET_POSTS_BY_TOP_OPERATION =2; //Oltre che prendere tutti i post, questa operazione prenderà anche i mi piace
    public static final int GET_POSTS_BY_NEWEST_OPERATION =3;
    public static final int GET_POSTS_BY_OLDEST_OPERATION =4;
    public static final int GET_POST_COMMENTS_OPERATION=5;
    public static final int ADD_LIKE_OPERATION=6;
    public static final int ADD_POST_OPERATION=7;
    public static final int GET_RESTAURANTS_OPERATION=8;
    public static final int CREATE_IMAGE_FROM_URL_OPERATION=9;
    public static final int ADD_COMMENT_OPERATION=10;
    public static final String SINGUP_SCRIPT_URL="https://davidpoletti.altervista.org/FoodGram/insertUser.php?";
    public final static String LOGIN_SCRIPT_URL="https://davidpoletti.altervista.org/FoodGram/getUser.php";
    public static final String GET_POST_SCRIPT_URL="https://davidpoletti.altervista.org/FoodGram/getPosts.php?";
    public static final String GET_POST_COMMENTS_SCRIPT_URL="https://davidpoletti.altervista.org/FoodGram/getPostComments.php?";
    public static final String GET_RESTAURANTS_SCRIPT_URL ="https://davidpoletti.altervista.org/FoodGram/getRestaurants.php";
    public static final String ADD_POST_SCRIPT_URL="https://davidpoletti.altervista.org/FoodGram/addPost.php";
    public static final String ADD_POST_IMAGE_SCRIPT_URL="https://davidpoletti.altervista.org/FoodGram/addPostImage.php";
    public static final String ADD_LIKE_SCRIPT_URL="https://davidpoletti.altervista.org/FoodGram/addLike.php?";
    public static final String ADD_COMMENT_SCRIPT_URL="https://davidpoletti.altervista.org/FoodGram/addComment.php";


    /*
    Metodo invoca appena viene eseguito l'AsyncTask attuale
    Il metodo riceve come parametro un array di oggetti contente sempre nella cella 0 un intero che identifica il tipo di operazione che deve esere eseguita dal task
    A seconda dell'operazione verrano inseriti altri oggetti nell'array objects (l'array parametro del metodo in questione)
    */

    @Override
    protected Object doInBackground(Object[] objects) {

    int operationType=(int)objects[0]; //prendo il tipo di operazione da fare

        String[] userData;

        //Il metodo attuale restituirà l'oggetto opResult
        Object opResult=null;

        switch (operationType){

            case LOGIN_OPERATION:
                userData=(String[])objects[1];

                opResult=doLoginOperation(userData);

                break;

            case SIGN_UP_OPERATION:
                userData=(String[])objects[1];

                opResult=doSignUpOperation(userData);

                break;

            case GET_POSTS_BY_TOP_OPERATION:
                opResult=doGetPostsOperation(GET_POSTS_BY_TOP_OPERATION);
                break;

            case GET_POSTS_BY_NEWEST_OPERATION:
                opResult=doGetPostsOperation(GET_POSTS_BY_NEWEST_OPERATION);
                break;

            case GET_POSTS_BY_OLDEST_OPERATION:
                opResult=doGetPostsOperation(GET_POSTS_BY_OLDEST_OPERATION);
                break;

            case GET_POST_COMMENTS_OPERATION:
                int postID=(int)objects[1];
                opResult=doGetPostComments(postID);
                break;

            case ADD_LIKE_OPERATION:
                int ID_Post=(int)objects[1];
                int ID_User=(int)objects[2];

                opResult=doAddLikeOperation(ID_Post,ID_User);
                break;

            case ADD_POST_OPERATION:
                int userID= (int)objects[1];
                int restaurantID=(int)objects[2];
                Bitmap imgBitmap=(Bitmap)objects[3];

                opResult=doAddPostOperation(userID,restaurantID,imgBitmap);
                break;

            case GET_RESTAURANTS_OPERATION:
                opResult=getRestaurants();
                break;

            case CREATE_IMAGE_FROM_URL_OPERATION:
                URL imgUrl=(URL)objects[1];

                opResult=doCreateImageFromUrlOperation(imgUrl);
                break;

            case ADD_COMMENT_OPERATION:
                userID=(int)objects[1];
                postID=(int)objects[2];
                String comment=(String)objects[3];

                opResult=doAddCommentOperation(userID,postID,comment);
                break;
        }

        return opResult;
    }

    //Operazione che effettua il Login
    private String[] doLoginOperation(String[] userData){

        String data[]=null;

        try {
            String userEmail=userData[0];
            String userPassword=userData[1];


            URL url=new URL(LOGIN_SCRIPT_URL);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();

            httpURLConnection.setRequestMethod("POST");

            PrintWriter webWriter=new PrintWriter(httpURLConnection.getOutputStream());
            webWriter.write("email="+userEmail+"&password="+userPassword);

            webWriter.flush();
            webWriter.close();

            httpURLConnection.connect();

            if(httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK) {

                BufferedReader webReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                String line;

                String jsonString = webReader.readLine();

                while ((line = webReader.readLine()) != null) {
                    jsonString += line;
                }

                if(jsonString!=null) {

                    JSONObject jsonObject = new JSONObject(jsonString);

                    data = new String[3];

                    data[0] = jsonObject.getString("ID_User");
                    data[1] = jsonObject.getString("name");
                    data[2] = jsonObject.getString("surname");

                }
            }
            else{
                System.out.println("Errore nella richiesta");
            }

            httpURLConnection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;

    }

    //Operazione che effettua la registrazione
    private boolean doSignUpOperation(String[] userData){
        boolean result=false;

        try {
            String userEmail=userData[0];
            String userPassword=userData[1];
            String userName=userData[2];
            String userSurname=userData[3];

            URL url=new URL(SINGUP_SCRIPT_URL);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();

            httpURLConnection.setRequestMethod("POST");

            PrintWriter webWriter=new PrintWriter(httpURLConnection.getOutputStream());

            webWriter.write("email="+userEmail+"&password="+userPassword+"&name="+userName+"&surname="+userSurname);

            webWriter.flush();
            webWriter.close();


            if(httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK) {
                result= true;
            }
            else{
                System.out.println("Errore nella richiesta");
            }

            httpURLConnection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }

    //Operazione che prende i post dal database
    private HashMap<String, String> doGetPostsOperation(int operationType){

        String customUrl=GET_POST_SCRIPT_URL;

        switch (operationType){
            case GET_POSTS_BY_NEWEST_OPERATION:
                customUrl+="sortby=new";
                break;

            case GET_POSTS_BY_TOP_OPERATION:
                customUrl+="sortby=top";
                break;

            case GET_POSTS_BY_OLDEST_OPERATION:
                customUrl+="sortby=old";
                break;
        }


        HashMap<String,String> result=null;

        try {
            URL url=new URL(customUrl);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();

            if(httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK) {

                BufferedReader webReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                String jsonResult = webReader.readLine();

                String line;

                while ((line = webReader.readLine()) != null) {
                    jsonResult += line;
                }

                if(jsonResult!=null) {

                    JSONArray jsonArray = new JSONArray(jsonResult);

                    result = new HashMap<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        result.put("ID_Post" + i, jsonObject.getString("ID_Post"));
                        result.put("imgUrl" + i, jsonObject.getString("imgUrl"));
                        result.put("ID_Restaurant" + i, jsonObject.getString("ID_Restaurant"));
                        result.put("restaurantName" + i, jsonObject.getString("restaurantName"));
                        result.put("ID_User" + i, jsonObject.getString("ID_User"));
                        result.put("numLikes" + i, jsonObject.getString("numLikes"));
                        result.put("userName" + i, jsonObject.getString("userName"));
                        result.put("surname" + i, jsonObject.getString("surname"));
                    }
                }
            }

            httpURLConnection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }

    //Operazione che prende i commenti di un determinato post
    private HashMap<String,String> doGetPostComments(int postID){
        HashMap <String,String> result=null;

        try{
            URL customUrl=new URL(GET_POST_COMMENTS_SCRIPT_URL+"ID_Post="+postID);

            HttpURLConnection httpURLConnection=(HttpURLConnection)customUrl.openConnection();

            if(httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                BufferedReader webReader=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                String jsonResult=webReader.readLine();

                String line;

                while((line=webReader.readLine())!=null){
                    jsonResult+=line;
                }

                if(jsonResult!=null) {

                    JSONArray jsonArray = new JSONArray(jsonResult);

                    result = new HashMap<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        result.put("ID_User" + i, jsonObject.getString("ID_User"));
                        result.put("userName" + i, jsonObject.getString("name"));
                        result.put("surname" + i, jsonObject.getString("surname"));
                        result.put("comment" + i, jsonObject.getString("comment"));
                    }
                }

            }

            httpURLConnection.disconnect();

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    //Operazione che prendere i ristoranti nel database. Utilizzata per creare lo spinner nella'Activity AddPostActivity
    private HashMap<String,String> getRestaurants(){
        HashMap<String,String> result=null;

        try {
            URL url = new URL(GET_RESTAURANTS_SCRIPT_URL);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();

            if(httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK){

                BufferedReader webReader=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                String json=webReader.readLine();

                String line;

                while((line=webReader.readLine())!=null){
                    json+=line;
                }

                if(json!=null) {
                    JSONArray jsonArray = new JSONArray(json);

                    result = new HashMap<>();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        result.put("name" + i, jsonObject.getString("name"));
                        result.put("ID_Restaurant" + i, jsonObject.getString("ID_Restaurant"));
                    }
                }
            }

            else{
                System.out.println("Niente ristoranti");
            }

            httpURLConnection.disconnect();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

    //Operazione che effettua l'aggiunta di un post al database
    public boolean doAddPostOperation(int userID, int restaurantID, Bitmap imgBitmap){
        boolean opResult=false;

        try{
            //Converto l'immagine da Bitmap e Stringa codificata in Base64
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            imgBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
            imgBitmap.recycle();
            byte[] imgBytes=baos.toByteArray();
            String encodedImage=Base64.encodeToString(imgBytes,Base64.DEFAULT);

            URL url=new URL(ADD_POST_SCRIPT_URL);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();

            httpURLConnection.setRequestMethod("POST");

            PrintWriter webWriter=new PrintWriter(httpURLConnection.getOutputStream());

            webWriter.write("ID_User="+userID+"&ID_Restaurant="+restaurantID);

            webWriter.flush();
            webWriter.close();

            if(httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK){

                /*
                Inserimento dell'ID utente e dell'ID ristorante avvenuto correttamente
                Ora verrà inserità l'immagine nel database
                */

                httpURLConnection.disconnect();

                url=new URL(ADD_POST_IMAGE_SCRIPT_URL);

                httpURLConnection=(HttpURLConnection)url.openConnection();

                httpURLConnection.setRequestMethod("POST");

                //Utilizzo la proprietà Enctype:multipart/form-data per poter inviare file
                httpURLConnection.setRequestProperty("Enctype","multipart/form-data");

                httpURLConnection.setRequestProperty("Connection","Keep-Alive");

                httpURLConnection.setRequestProperty("Accept-Charset","UTF-8");

                webWriter=new PrintWriter(httpURLConnection.getOutputStream());

                webWriter.write("image="+encodedImage);

                webWriter.flush();
                webWriter.close();


                if(httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                    opResult=true;
                }
                else if(httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_INTERNAL_ERROR){
                    System.out.println("Errore nel caricamento dell'immagine");
                }
                else{
                    System.out.println("Errore nell'inserimento dell'immagine");
                }
            }
            else if(httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_BAD_REQUEST){
                System.out.println("Errore nella richiesta");
            }
            else{
                System.out.println("Errore nell'inserimento");
            }

            httpURLConnection.disconnect();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return opResult;
    }

    //Operazione che crea un immagine dato il suo URL. Utilizzata nell'Activity MainActivity per creare l'immagine del post
    public Bitmap doCreateImageFromUrlOperation(URL imgUrl){
        Bitmap imgBitmap=null;

        try {
            InputStream imgStream = imgUrl.openStream();

            imgBitmap=BitmapFactory.decodeStream(imgStream);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return imgBitmap;
    }

    //Operazione che effettua l'aggiunta di un mi piace ad un determinato post
    public boolean doAddLikeOperation(int ID_Post,int ID_User){
        boolean opResult=false;

        try{
            URL url=new URL(ADD_LIKE_SCRIPT_URL+"ID_Post="+ID_Post+"&ID_User="+ID_User);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();

            httpURLConnection.setRequestMethod("GET");

            if(httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                opResult=true;
            }

            httpURLConnection.disconnect();
        }
        catch (Exception e){
            e.printStackTrace();
        }


        return opResult;
    }


    //Operazione che effettua l'aggiunta di un commento ad un determinato post
    public HashMap<String,String> doAddCommentOperation(int ID_User,int ID_Post,String comment){
        HashMap<String,String> result=null;

        try{

            URL url=new URL(ADD_COMMENT_SCRIPT_URL);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();

            httpURLConnection.setRequestMethod("POST");

            PrintWriter webWriter=new PrintWriter(httpURLConnection.getOutputStream());

            webWriter.write("ID_Post="+ID_Post+"&ID_User="+ID_User+"&comment="+comment);

            webWriter.flush();
            webWriter.close();

            if(httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK){

                BufferedReader webReader=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                String line;

                String jsonResult=webReader.readLine();

                while((line=webReader.readLine())!=null){
                    jsonResult+=line;
                }

                if(jsonResult!=null) {
                    JSONObject jsonObject = new JSONObject(jsonResult);

                    String name = jsonObject.getString("name");
                    String surname = jsonObject.getString("surname");

                    result = new HashMap<String, String>();

                    result.put("ID_User", String.valueOf(ID_User));
                    result.put("userName", name);
                    result.put("surname", surname);
                    result.put("comment", comment);
                }
            }

            httpURLConnection.disconnect();
        }
        catch (Exception e){
            e.printStackTrace();
        }


        return result;
    }

}
