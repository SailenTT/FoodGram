package davidpoletti.foodgram.com;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.HashMap;

public class CommentActivity extends AppCompatActivity {
    private HashMap<String,String> postComments;
    private LinearLayout scrollViewLayout;
    private int currentUserID;
    private int currentPostID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.comment_activity);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //faccio in modo che l'attività supporti solo la modalità a schermo verticale

        /*
        Ottengo dagli Extra dell'Intent l'ID dell'utente e del post che mi serviranno
        per inserirli nel database nel momento in cui l'utente aggiunge un commento
         */
        currentPostID=getIntent().getExtras().getInt("ID_Post");
        currentUserID=getIntent().getExtras().getInt("ID_User");

        scrollViewLayout = findViewById(R.id.commentsScrollViewLayout);

        Object[] taskData=new Object[2];

        taskData[0]=NetworkActivtiesTask.GET_POST_COMMENTS_OPERATION;
        taskData[1]=currentPostID;

        try {
            Object taskResult=new NetworkActivtiesTask().execute(taskData).get();

            if(taskResult!=null) {

                postComments = (HashMap<String, String>) taskResult;

                int i = 0;

                while ((postComments.get("userName" + i)) != null) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        loadPost(i);
                    }

                    i++;
                }
            }
            else{
                Toast.makeText(this,"Al momento non ci sono commenti, potresti inserirne uno Tu!",Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void loadPost(int index){
        String userName=postComments.get("userName"+index);


        if(userName!=null){

            String surname=postComments.get("surname"+index);
            String comment=postComments.get("comment"+index);

            try {

                CardView cardViewContainer=new CardView(this);
                cardViewContainer.setId(View.generateViewId());

                ConstraintLayout cardViewLayout=new ConstraintLayout(this);
                cardViewLayout.setId(View.generateViewId());

                //Creo l'oggetto grafico contente i dati angrafici dell'utente
                TextView txt_userData=new TextView(this);
                txt_userData.setText(userName+" "+surname);
                txt_userData.setTextSize(15);
                txt_userData.setTextColor(Color.parseColor("#FFFFFF"));
                txt_userData.setBackgroundColor(Color.parseColor("#07000000"));
                txt_userData.setId(View.generateViewId());


                //Creo l'oggetto grafico contente il commento
                TextView txt_comment=new TextView(this);
                txt_comment.setText(comment);
                txt_comment.setTextSize(17);
                txt_comment.setTextColor(Color.parseColor("#FFFFFF"));
                txt_comment.setBackgroundColor(Color.parseColor("#07000000"));
                txt_comment.setId(View.generateViewId());

                //Aggiungo i due oggetti grafici al layout
                cardViewLayout.addView(txt_userData);
                cardViewLayout.addView(txt_comment);

                ConstraintSet constraint=new ConstraintSet();
                constraint.clone(cardViewLayout);


                //Definisco le costanti dei vari oggetti grafici
                constraint.connect(txt_userData.getId(),ConstraintSet.LEFT,cardViewLayout.getId(),ConstraintSet.LEFT,0);
                constraint.connect(txt_userData.getId(),ConstraintSet.RIGHT,cardViewLayout.getId(),ConstraintSet.RIGHT,0);
                constraint.connect(txt_userData.getId(),ConstraintSet.TOP,cardViewLayout.getId(),ConstraintSet.TOP,3);
                constraint.connect(txt_userData.getId(),ConstraintSet.BOTTOM,txt_comment.getId(),ConstraintSet.TOP,4);

                constraint.connect(txt_comment.getId(),ConstraintSet.LEFT,cardViewLayout.getId(),ConstraintSet.LEFT,0);
                constraint.connect(txt_comment.getId(),ConstraintSet.RIGHT,cardViewLayout.getId(),ConstraintSet.RIGHT,0);
                constraint.connect(txt_comment.getId(),ConstraintSet.TOP,txt_userData.getId(),ConstraintSet.BOTTOM,4);
                constraint.connect(txt_comment.getId(),ConstraintSet.BOTTOM,cardViewLayout.getId(),ConstraintSet.BOTTOM,3);

                constraint.applyTo(cardViewLayout);

                LinearLayout.LayoutParams linearLayoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                linearLayoutParams.gravity= Gravity.CENTER;
                linearLayoutParams.topMargin=20;
                linearLayoutParams.bottomMargin=20;
                cardViewContainer.setLayoutParams(linearLayoutParams);
                cardViewContainer.setCardBackgroundColor(Color.parseColor("#212121"));

                cardViewContainer.addView(cardViewLayout);

                scrollViewLayout.addView(cardViewContainer);

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void onClick_btnAddComment(View view){

        try {
            Object[] taskParameters=new Object[4];
            taskParameters[0]=NetworkActivtiesTask.ADD_COMMENT_OPERATION;
            taskParameters[1]=currentUserID;
            taskParameters[2]=currentPostID;
            String comment=((EditText)(findViewById(R.id.txt_commentText))).getText().toString();
            taskParameters[3]=comment;

            Object taskResult = (Object) new NetworkActivtiesTask().execute(taskParameters).get();

            HashMap<String,String> newComment=(HashMap<String, String>)taskResult;

            //Se il commento è stato inserito, il Task restituisce
            if(newComment!=null){

                int index=0;

                if(postComments!=null) {
                    index = postComments.size() / 4; //diviso 4 perché l'indice aumenta ogni 4 elementi inseriti
                }
                else{
                    postComments=new HashMap<String, String>();
                }

                postComments.put("ID_User"+index,newComment.get("ID_User"));
                postComments.put("userName"+index,newComment.get("userName"));
                postComments.put("surname"+index,newComment.get("surname"));
                postComments.put("comment"+index,newComment.get("comment"));

                /*
                Verifico che la versione di Android sia uguale o superiore a Lollip perchè in caso contrario non potrebbe
                utilizzare le funzionalità necessarie a creare i post
                 */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ((EditText)findViewById(R.id.txt_commentText)).setText("");

                    loadPost(index);
                }
            }
            else{
                Toast.makeText(this,"Errore nell'inserimento",Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
