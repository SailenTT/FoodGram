package davidpoletti.foodgram.com;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.net.URL;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    private HashMap<String, String> postsData;
    private int currentUserID;
    private LinearLayout scrollViewLayout;
    public static final int BUTTON_TYPE_LIKES=1;
    public static final int BUTTON_TYPE_COMMENTS=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //TODO alleggerire il carico del'app
        super.onCreate(savedInstanceState);

        currentUserID=getIntent().getExtras().getInt("ID_User");

        setContentView(R.layout.main_activity);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //faccio in modo che l'attività supporti solo la modalità a schermo verticale

        scrollViewLayout=findViewById(R.id.postsScrollViewLayout);

        //TODO usare un GroupView come container del post e dei suoi pulsanti

        try {
            Object[] asyncTaskParameters=new Object[1];
            asyncTaskParameters[0]=NetworkActivtiesTask.GET_POSTS_BY_NEWEST_OPERATION;

            Object result = new NetworkActivtiesTask().execute(asyncTaskParameters).get();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                if(result!=null) {

                    postsData=(HashMap<String, String>) result;

                    int i = 0;

                    while (postsData.get("userName"+i)!=null) {

                        loadPost(i);

                        i++;
                    }
                }
                else{
                    Toast.makeText(this,"Non ci sono post :(",Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this,"Versione tropo vecchia di android",Toast.LENGTH_SHORT);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onClick_btnAddPost(View view){
        Intent addPostActivity=new Intent(this,AddPostActivity.class);
        addPostActivity.putExtra("ID_User",currentUserID);
        startActivity(addPostActivity);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void loadPost(int index){
        String userName=postsData.get("userName"+index);

        if(userName!=null){

            String stringImgUrl=postsData.get("imgUrl"+index);
            String numLikes=postsData.get("numLikes"+index);
            String surname=postsData.get("surname"+index);
            String restaurantName=postsData.get("restaurantName"+index);

            try {
                URL imgUrl = new URL(stringImgUrl);

                /*
                Utilizzo l'AsyncTask per caricare l'immagine dato il suo indirzzo URL perché non è possibile effettuare operazioni di rete sul thread principale dell'applicazione
                 */

                Object[] taskData=new Object[2];
                taskData[0]=NetworkActivtiesTask.CREATE_IMAGE_FROM_URL_OPERATION;
                taskData[1]=imgUrl;

                Bitmap imgBitmap=(Bitmap)new NetworkActivtiesTask().execute(taskData).get();

                //TODO mettere la data del post

                //Controllo che l'immagine sia stata correttamente caricata

                if(imgBitmap!=null) {
                    int ID_Post=Integer.parseInt(postsData.get("ID_Post"+index));

                    //Creo il CardView, cioè il container che conterrà i dati del post

                    CardView cardViewContainer=new CardView(this);
                    cardViewContainer.setId(View.generateViewId());

                    //Creo il layout del container

                    ConstraintLayout cardViewLayout=new ConstraintLayout(this);
                    cardViewLayout.setId(View.generateViewId());

                    //Inserisco il layout dentro il container

                    cardViewContainer.addView(cardViewLayout);

                    TextView txt_username=new TextView(this);
                    txt_username.setText(userName+" "+surname+" presso "+restaurantName);
                    txt_username.setTextSize(20);
                    txt_username.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    txt_username.setTextColor(Color.parseColor("#FFFFFF"));
                    txt_username.setBackgroundColor(Color.parseColor("#07000000"));
                    txt_username.setId(View.generateViewId());

                    ImageView postImage = new ImageView(this);
                    postImage.setImageBitmap(imgBitmap);
                    postImage.setId(View.generateViewId());
                    ConstraintLayout.LayoutParams imgLayoutParam=new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,700);
                    postImage.setLayoutParams(imgLayoutParam);

                    Button likeButton = new Button(this);
                    likeButton.setText(numLikes+" mi piace");
                    ConstraintLayout.LayoutParams likeButtonParam=new ConstraintLayout.LayoutParams(500,100);
                    likeButton.setLayoutParams(likeButtonParam);
                    likeButton.setBackgroundResource(R.drawable.button_style);
                    likeButton.setId(View.generateViewId());
                    likeButton.setOnClickListener(this);

                    //Creo il Tag del bottone che verrà poi utilizzato nel metodo OnClick(View view) per identificare il tipo del bottone e l'ID del post a cui fa riferimento

                    HashMap<String, Integer> likeButtonTag = new HashMap<>();
                    likeButtonTag.put("ID_Post", ID_Post);
                    likeButtonTag.put("ButtonType", BUTTON_TYPE_LIKES);
                    likeButton.setTag(likeButtonTag);

                    Button commentButton = new Button(this);
                    commentButton.setText("Commenti");
                    ConstraintLayout.LayoutParams commentButtonParam=new ConstraintLayout.LayoutParams(500,100);
                    commentButton.setLayoutParams(commentButtonParam);
                    commentButton.setBackgroundResource(R.drawable.button_style);
                    commentButton.setId(View.generateViewId());
                    commentButton.setOnClickListener(this);

                    //Creo il Tag del bottone che verrà poi utilizzato nel metodo OnClick(View view) per identificare il tipo del bottone e l'ID del post a cui fa riferimento

                    HashMap<String, Integer> commentButtonTag = new HashMap<>();
                    commentButtonTag.put("ID_Post", ID_Post);
                    commentButtonTag.put("ButtonType", BUTTON_TYPE_COMMENTS);
                    commentButton.setTag(commentButtonTag);

                    //Inserisco gli elementi grafici dentro il layout del container

                    cardViewLayout.addView(txt_username);
                    cardViewLayout.addView(postImage);
                    cardViewLayout.addView(likeButton);
                    cardViewLayout.addView(commentButton);


                    ConstraintSet constraint=new ConstraintSet();
                    constraint.clone(cardViewLayout);

                    //Creo le costanti per definire la posizione degli oggetti grafici

                    constraint.connect(txt_username.getId(),ConstraintSet.LEFT,cardViewLayout.getId(),ConstraintSet.LEFT,0);
                    constraint.connect(txt_username.getId(),ConstraintSet.RIGHT,cardViewLayout.getId(),ConstraintSet.RIGHT,0);
                    constraint.connect(txt_username.getId(),ConstraintSet.TOP,cardViewLayout.getId(),ConstraintSet.TOP,0);
                    constraint.connect(txt_username.getId(),ConstraintSet.BOTTOM,postImage.getId(),ConstraintSet.TOP,0);

                    constraint.connect(postImage.getId(),ConstraintSet.LEFT,cardViewLayout.getId(),ConstraintSet.LEFT,0);
                    constraint.connect(postImage.getId(),ConstraintSet.RIGHT,cardViewLayout.getId(),ConstraintSet.RIGHT,0);
                    constraint.connect(postImage.getId(),ConstraintSet.TOP,txt_username.getId(),ConstraintSet.TOP,0);
                    constraint.connect(postImage.getId(),ConstraintSet.BOTTOM,cardViewLayout.getId(),ConstraintSet.BOTTOM,0);

                    constraint.connect(likeButton.getId(),ConstraintSet.TOP,postImage.getId(),ConstraintSet.BOTTOM,4);
                    constraint.connect(likeButton.getId(),ConstraintSet.LEFT,cardViewLayout.getId(),ConstraintSet.LEFT,8);
                    constraint.connect(likeButton.getId(),ConstraintSet.RIGHT,commentButton.getId(),ConstraintSet.LEFT,14);
                    constraint.connect(likeButton.getId(),ConstraintSet.BOTTOM,cardViewLayout.getId(),ConstraintSet.BOTTOM,4);

                    constraint.connect(commentButton.getId(),ConstraintSet.TOP,postImage.getId(),ConstraintSet.BOTTOM,4);
                    constraint.connect(commentButton.getId(),ConstraintSet.BOTTOM,cardViewLayout.getId(),ConstraintSet.BOTTOM,4);
                    constraint.connect(commentButton.getId(),ConstraintSet.RIGHT,cardViewLayout.getId(),ConstraintSet.RIGHT,8);
                    constraint.connect(commentButton.getId(),ConstraintSet.LEFT,likeButton.getId(),ConstraintSet.RIGHT,14);

                    //Applico le constraints al layout dentro il container

                    constraint.applyTo(cardViewLayout);

                    LinearLayout.LayoutParams linearLayoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    linearLayoutParams.gravity=Gravity.CENTER;
                    linearLayoutParams.topMargin=20;
                    linearLayoutParams.bottomMargin=20;
                    cardViewContainer.setLayoutParams(linearLayoutParams);
                    cardViewContainer.setCardBackgroundColor(Color.parseColor("#212121"));

                    //Aggiungo il container al Linear Layout dello Scroller View

                    scrollViewLayout.addView(cardViewContainer);

                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {

        //Prendo il Tag del commento

        HashMap<String,Integer> tag=(HashMap)view.getTag();
        int postID=tag.get("ID_Post");

        //Controllo il tipo del bottone cliccato. Il bottone cliccato può essere il bottono per inserire mi piace o per visualizzare i commenti

        if(tag.get("ButtonType")==BUTTON_TYPE_LIKES){

            Object[] taskData=new Object[3];
            taskData[0]=NetworkActivtiesTask.ADD_LIKE_OPERATION;
            taskData[1]=postID;
            taskData[2]=currentUserID;

            try {

                //Inserisco il mi piace dentro il database

                //Se l'utente ha già inserito un mi piace per il post in questione, l'AsyncTask evocato ritorna False. In caso contrario ritorna True

                boolean taskResult=(boolean)new NetworkActivtiesTask().execute(taskData).get();

                if(taskResult){
                    Button likeButton=(Button)view;
                    String likeButtonText=likeButton.getText().toString();
                    int numLikes=Integer.parseInt(likeButtonText.substring(0,(likeButtonText.length()-9)));
                    numLikes++;
                    likeButton.setText((numLikes)+" mi piace");
                }
                else{
                    Toast.makeText(this,"Mi piace già inserito",Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else{

            //Se il bottone premuto corrisponde al bottone commenti, l'applicazione lancia l'attività relativa ai commenti
            Intent commentsActivity=new Intent(this,CommentActivity.class);
            commentsActivity.putExtra("ID_Post",postID);
            commentsActivity.putExtra("ID_User",currentUserID);
            startActivity(commentsActivity);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //In futuro dentro quesato metodo verrà implementato l'ordinamento dell'hashmap contente i post e le relative informazioni di essi
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { }
}
