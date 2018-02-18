package com.example.osman.mandarinquiz;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.opencsv.CSVReader;

public class MainActivity extends WearableActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    Button topButton;
    Button rightButton;
    Button leftButton;
    Button bottomButton;
    TextView charMandarin;
    TextView streakDisp;
    TextView charReview;
    HashMap buttonIDs = new HashMap();

    int answer;
    int button[] = new int[4];
    int streak;
    boolean resetCharReview;
/*
   List<String> mandarin = Arrays.asList("天", "气", "很", "好", "冷", "吗", "酒", "热", "今", "真", "我", "你", "您", "他", "也", "们", "忙", "作", "呢", "早", "啊",
           "都", "请", "坐", "再", "见", "她", "这", "是", "张", "华", "同", "志", "中", "国", "谢谢", "叫", "老", "北", "京", "爱", "姓", "王", "名", "字", "英", "小",
           "上", "海", "姐", "那", "晚", "做", "水");
    List<String> pinyin = Arrays.asList("tiān", "qì", "hěn", "hǎo", "lěng", "ma", "jiǔ", "rè", "jīn", "zhēn", "wǒ", "nǐ", "nín", "tā", "yě", "men", "máng", "zuò",
            "ne", "zǎo", "a", "dōu", "qǐng", "zuò", "zài", "jiàn", "tā", "zhè", "shì", "zhāng", "huá", "tóng", "zhì", "zhōng", "guó", "xièxiè", "jiào", "lǎo", "běi",
            "jīng", "ài", "xìng", "wáng", "míng", "zì", "yīng", "xiǎo", "shàng", "hǎi", "jiě", "nà", "wǎn", "zuò", "shuǐ");
    List<String> translation= Arrays.asList("Day", "Air", "Very", "Good", "Cold", "Question", "Wine", "Hot", "Now", "Truly", "I", "You", "You", "He", "Also", "Them",
            "Busy", "Yesterday", "Question par", "Early", "Question par", "All", "Request/invi", "Sit", "Again", "See", "She", "This", "Yes", "Name", "Flower", "Same",
            "Will", "Middle", "Country", "Thank", "To Call", "Elderly", "North", "Capital", "To Love", "Surname", "King / name", "Given name", "Character", "Flower/ Brave",
            "Small", "Top / Above", "Sea", "Elder Sister", "That", "Late", "Make", "Water");
*/

    List<String> mandarin = new ArrayList<String>();
    List<String> pinyin = new ArrayList<String>();
    List<String> translation = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topButton = (Button) findViewById(R.id.topButton);
        rightButton = (Button) findViewById(R.id.rightButton);
        leftButton = (Button) findViewById(R.id.leftButton);
        bottomButton = (Button) findViewById(R.id.bottomButton);
        charMandarin = (TextView) findViewById(R.id.charMandarin);
        streakDisp = (TextView) findViewById(R.id.streak);
        charReview = (TextView) findViewById(R.id.charReview);

        String packageName = getApplicationContext().getPackageName() + ":";

        buttonIDs.put(packageName + "id/topButton", new Integer(0));
        buttonIDs.put(packageName + "id/rightButton", new Integer(1));
        buttonIDs.put(packageName + "id/bottomButton", new Integer(2));
        buttonIDs.put(packageName + "id/leftButton", new Integer(3));

        streak = 0;
        streakDisp.setText(" Score: " + String.valueOf(streak));
        charReview.setText("");

        //OpenCSV Code

        Uri mandarinList = Uri.parse("android.resource://com.example.osman.androidweardemo/values/mandarinlist.csv");

        CSVReader reader = null;
        reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.mandarinlist)));
        String [] nextLine;
        try {
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                mandarin.add(nextLine[0]);
                pinyin.add(nextLine[1]);
                translation.add(nextLine[2]);

            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("--->", "IO Exception reading");
        }
        System.out.println(mandarin.get(0) + " " + pinyin.get(0) + " " + translation.get(0));
        //End OpenCSV Code

        initTest();
    }

    public void initTest(){
        Random rand = new Random();

        answer = rand.nextInt(mandarin.size());

        randomizeButtons(button);
        charMandarin.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 50);
        if(mandarin.get(answer).length()> 1)
            charMandarin.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        topButton.setText(pinyin.get(button[0]) + "\n" + translation.get(button[0]));
        setColor(topButton, findTone(pinyin.get(button[0])));

        rightButton.setText(pinyin.get(button[1]) + "\n" + translation.get(button[1]));
        setColor(rightButton, findTone(pinyin.get(button[1])));

        leftButton.setText(pinyin.get(button[3]) + "\n" + translation.get(button[3]));
        setColor(leftButton, findTone(pinyin.get(button[3])));

        bottomButton.setText(pinyin.get(button[2]) + "\n" + translation.get(button[2]));
        setColor(bottomButton, findTone(pinyin.get(button[2])));

        charMandarin.setText(mandarin.get(answer));

        resetCharReview = false;

    }

    public void setColor(TextView textButton, int tone){
        switch (tone) {
            case 0:  textButton.setTextColor(Color.rgb(126, 192, 238));//-
                break;
            case 1:  textButton.setTextColor(Color.rgb(0, 51, 0));//up
                break;
            case 2:  textButton.setTextColor(Color.rgb(102, 0, 204));//dip
                break;
            case 3:  textButton.setTextColor(Color.rgb(179, 0, 0));//down
                break;
            default: textButton.setTextColor(Color.DKGRAY);
                break;
        }
    }

    public void randomizeButtons(int button[]){
        boolean validSet = false;
        Random rand;
        while(!validSet){
            for(int i=0; i<4; i++) {
                rand = new Random();
                button[i] = rand.nextInt(mandarin.size());
            }
            rand = new Random();
            button[rand.nextInt(4)] = answer;
            validSet = ((button[0] == answer || button[1] == answer || button[2] == answer || button[3] == answer) && (button[0] != button[1]) && (button[0] != button[2]) &&
                    (button[0] != button[3]) && (button[1] != button[2]) && (button[1] != button[3]) && (button[2] != button[3]));
        }
        Log.i("--->", String.valueOf(String.valueOf(answer) + " " + button[0]) + " " + String.valueOf(button[1]) + " " + String.valueOf(button[2]) + " " + String.valueOf(button[3]));
        Log.i("--->", String.valueOf( (mandarin.size() == pinyin.size ()) && (pinyin.size() == translation.size()) ) );
    }

    public void onClick(View v){
        int pressedButton = (int) buttonIDs.get(v.getResources().getResourceName(v.getId()));
        Log.e("--->", String.valueOf(pressedButton));
        if (button[pressedButton] == answer) {
            Log.i("--->", "Correct");
            streak++;
            Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
            if(resetCharReview){
                charReview.setText(" " + translation.get(answer) + "\n " + pinyin.get(answer) + "\n " + mandarin.get(answer));
            }

            initTest();
        }
        else {
            Log.i("--->", "False");
            streak = 0;
            resetCharReview = true;
            Toast.makeText(getApplicationContext(), "False \n" + mandarin.get(button[pressedButton]) + ": " + pinyin.get(button[pressedButton]) + " | " + translation.get(button[pressedButton]), Toast.LENGTH_SHORT).show();
        }
        streakDisp.setText(" Score: " + String.valueOf(streak));
    }

    public static int findTone(String word) {
        int i, j, k;
        int[] tonePos = {0,0,0,0};
        int result = 4, max = 0;

        char[][] tone =  {{'ā', 'ē', 'ī', 'ō', 'ū', 'ǖ'} , {'á', 'é', 'í', 'ó', 'ú', 'ǘ'} , {'ǎ', 'ě', 'ǐ', 'ǒ', 'ǔ', 'ǚ'} ,
                {'à', 'è', 'ì', 'ò', 'ù', 'ǜ'}};

        for(i = 0; i<word.length(); i++){
            for(j = 0; j < 4; j++){
                for(k = 0; k < 6; k++){
                    if(word.charAt(i) == tone[j][k])
                        tonePos[j] = i;
                }
            }
        }

        for(i=0; i<4; i++){
            System.out.println(tonePos[i]);
            if(tonePos[i] > max){
                max = tonePos[i];
                result = i;
            }
        }
        return result;
    }


}
