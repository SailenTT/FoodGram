package davidpoletti.foodgram.com;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity{
    private int currentUserID;
    private Spinner spinner;
    private HashMap<String,String> restaurantsData;
    private Bitmap imgBitmap=null;
    public static int GET_IMG_URI_OPERATION_CODE=42;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_post_activity);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //faccio in modo che l'attività supporti solo la modalità a schermo verticale

        //Prendo l'ID dell'utente loggato che inserirò nel database assieme agli altri dati del post
        currentUserID=getIntent().getExtras().getInt("ID_User");

        spinner=findViewById(R.id.spinner_restaurants_names);

        restaurantsData =null;

        try {
            Object[] taskData=new Object[1];

            taskData[0]=NetworkActivtiesTask.GET_RESTAURANTS_OPERATION;

            restaurantsData =(HashMap<String,String>) new NetworkActivtiesTask().execute(taskData).get();

            if(restaurantsData !=null) {

                String[] arrayAdapterData=new String[(restaurantsData.size())/2];

                for (int i = 0; i < (restaurantsData.size())/2; i++) {
                    if(restaurantsData.get("name"+i)!=null) {
                        arrayAdapterData[i] = restaurantsData.get("name" + i);
                    }
                }

                //Creo un ArrayAdapter per inserire nello spinner i nomi dei ristoranti
                ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_spinner_item,arrayAdapterData);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(arrayAdapter);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //Quando viene cliccato il bottone contente l'immagine, questo metodo viene eseguito
    public void onClick_btnImageUpload(View view){

        //Viene creato un nuovo Intent per prendere un'immagine selezionata dell'utente dalla sua galleria
        Intent getPhotoFromGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        startActivityForResult(getPhotoFromGallery, GET_IMG_URI_OPERATION_CODE);

        /*
        Se l'immagine viene caricata correttamente, procedo a mostrare all'utente un anteprima
        dell'immagine che sta per caricare
        */
        if(imgBitmap!=null) {
            ImageButton imageButton = (ImageButton) view;
            imageButton.setImageBitmap(imgBitmap);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GET_IMG_URI_OPERATION_CODE) {

            Uri imgUri = data.getData();

            /*
            Se l'URI (il percorso locale dell'immagine) non è nullo
            procedo con la creazione di una Bitmap da un Uri.
            Il tipo Bitmap è il formato di Android utilizzato per gestire le immagini
            */
            if(imgUri!=null) {

                try {
                    InputStream imgStream = getContentResolver().openInputStream(imgUri);

                    imgBitmap = BitmapFactory.decodeStream(imgStream);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onClick_btnAddPost(View view){

        //Controllo che l'immagine sia stata inserita e convertita in Bitmap
        if(imgBitmap!=null){
            Object[] taskData=new Object[4];
            taskData[0]=NetworkActivtiesTask.ADD_POST_OPERATION;
            taskData[1]=currentUserID;
            taskData[2]=Integer.parseInt(restaurantsData.get("ID_Restaurant"+spinner.getSelectedItemPosition()));
            taskData[3]=imgBitmap;

            try {
                Boolean taskResult = (Boolean) new NetworkActivtiesTask().execute(taskData).get();

                if(taskResult) {
                    Toast.makeText(this, "Post inserito", Toast.LENGTH_LONG).show();

                    //Torno all'attività principale uno volta che il post è stato inserito
                    Intent mainActivity = new Intent(this, MainActivity.class);
                    mainActivity.putExtra("ID_User", currentUserID);
                    startActivity(mainActivity);
                }
                else{
                    Toast.makeText(this,"Errore nell'inserimento\nPerfavore riprova",Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
        else{
            Toast.makeText(this,"Inserisci un'immagine",Toast.LENGTH_SHORT).show();
        }
    }
}
