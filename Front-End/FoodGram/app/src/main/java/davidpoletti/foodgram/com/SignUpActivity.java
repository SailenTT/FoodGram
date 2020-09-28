package davidpoletti.foodgram.com;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sign_up_activity);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //faccio in modo che l'attività supporti solo la modalità a schermo verticale
    }

    //Metodo eseguito quando viene cliccato il bottone "Registrati"
    public void onClick_btn_signUp_signUpPage(View view) {  //TODO implementare un controllo dei dati

        String userData[]=new String[4];

        //Inserisco i dati inseriti dall'utente in un array di Stringhe che poi passo al mio AsyncTask
        userData[0]=((EditText)findViewById(R.id.txt_signUpEmail)).getText().toString();
        userData[1]=((EditText)findViewById(R.id.txt_signUpPassword)).getText().toString();
        userData[2]=((EditText)findViewById(R.id.txt_signUpName)).getText().toString();
        userData[3]=((EditText)findViewById(R.id.txt_signUpSurname)).getText().toString();

        int operationType=NetworkActivtiesTask.SIGN_UP_OPERATION;

        Object array[]=new Object[2];

        array[0]=operationType;
        array[1]=userData;

        try {

            boolean result = (Boolean)new NetworkActivtiesTask().execute(array).get();

            if (result) {


                //Se l'operazione è andata a buon fine, ritorna all'attività per il Login

                Intent loginActivity = new Intent(this, LoginActivity.class);

                startActivity(loginActivity);

            } else {
                Toast.makeText(this, "Errore nella registrazione\nRiprova", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    //Metodo eseguito quando viene cliccato il bottone "Login"
    public void onClick_btn_login_SignUpPage(View view){
        Intent loginActivity=new Intent(this, LoginActivity.class);

        startActivity(loginActivity);
    }
}
