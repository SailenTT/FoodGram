package davidpoletti.foodgram.com;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //faccio in modo che l'attività supporti solo la modalità a schermo verticale

        //Un toast è un popup grafico contente un messaggio

        Toast.makeText(this,"Creato da David Poletti",Toast.LENGTH_SHORT).show();
    }

    //Metodo eseguito quando viene cliccato il bottone "Login"
    public void onClick_btn_login_loginPage(View view){

        String userData[]=new String[2];

        //L'email e la password dell'utente vengono inserite in un array di string e passate come parametro all'AsyncTask

        userData[0]=((EditText)findViewById(R.id.txt_loginEmail)).getText().toString();
        userData[1]=((EditText)findViewById(R.id.txt_loginPassword)).getText().toString();

        int operationType=NetworkActivtiesTask.LOGIN_OPERATION;

        Object array[]=new Object[2];
        array[0]=operationType;
        array[1]=userData;

        try {

            //Effettuo il Login tramite il mio AsyncTask

            Object result = new NetworkActivtiesTask().execute(array).get();

            String opResult[] = (String[]) result;

            //Controllo che il login sia andato a buon fine

            if (opResult != null) {

                //Faccio partire l'attività principale contenente i vari post

                Intent mainActivity = new Intent(this, MainActivity.class);
                mainActivity.putExtra("ID_User",Integer.parseInt(opResult[0]));
                startActivity(mainActivity);

            } else {
                Toast.makeText(this, "Utente non trovato", Toast.LENGTH_SHORT).show();

                ((EditText)findViewById(R.id.txt_loginPassword)).setText("");
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    //Metodo invocato alla pressione del bottone "Registrati"
    public void onClick_btn_signUp_loginPage(View view){

        //Viene fatta partire l'attività che gestisce la registrazione di un nuovo utente

        Intent signUpActivity=new Intent(this,SignUpActivity.class);

        startActivity(signUpActivity);
    }
}
