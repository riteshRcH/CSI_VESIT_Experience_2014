package org.csiVesit.wirelesselims;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

import org.csiVesit.csiVesitExperience.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.TextView;

public class QuestionsActivity extends Activity implements OnClickListener
{
	//int[] QIDs = {R.array.Q0, R.array.Q1, R.array.Q2, R.array.Q3, R.array.Q4, R.array.Q5, R.array.Q6, R.array.Q7, R.array.Q8, R.array.Q9, R.array.Q10, R.array.Q11, R.array.Q12, R.array.Q13, R.array.Q14, R.array.Q15, R.array.Q16, R.array.Q17, R.array.Q18, R.array.Q19, R.array.Q20, R.array.Q21, R.array.Q22, R.array.Q23, R.array.Q24, R.array.Q25, R.array.Q26, R.array.Q27, R.array.Q28, R.array.Q29, R.array.Q30, R.array.Q31, R.array.Q32, R.array.Q33, R.array.Q34, R.array.Q35, R.array.Q36, R.array.Q37, R.array.Q38, R.array.Q39, R.array.Q40, R.array.Q41, R.array.Q42, R.array.Q43, R.array.Q44 };
	//put QIDs when Qs are changed/added
	static LinkedHashMap<Integer, Boolean> finishedQsID = new LinkedHashMap<Integer, Boolean>();	//Q ID and whether user got the ans correct or not
	Random randomNumGenerator = new Random();
	static int correctAnsCnt, timeOutPerQSecs = 30;
	int currentQID, questionsCnt = 0;
	String[] currentQ;
	String correctAns ="", selectedAns = "", allQuestionsXMLFormat = "";
	
	RadioGroup RadioGrpAns;
	TextView txtViewQuestion, txtViewShowTimeLeft;
	RadioButton radioBtn1, radioBtn2, radioBtn3, radioBtn4; 
	Button btnNext;
	
	ArrayList<String> allQuestions = new ArrayList<String>();
	
	CountDownTimer countdownTimer30sec = new CountDownTimer(timeOutPerQSecs*1000, 1000)
	{
		@Override
		public void onTick(long millisecsUntilFinish) 
		{
			// TODO Auto-generated method stub
			txtViewShowTimeLeft.setText("Time Left: "+(millisecsUntilFinish/1000));			
		}
		
		@Override
		public void onFinish() 
		{
			// TODO Auto-generated method stub
			if(radioBtn1.isChecked() || radioBtn2.isChecked() || radioBtn3.isChecked() || radioBtn4.isChecked())
			{
				selectedAns = ((RadioButton)findViewById(RadioGrpAns.getCheckedRadioButtonId())).getText().toString();
				if(selectedAns.equals(correctAns))
				{
					correctAnsCnt++;
					finishedQsID.put(currentQID, true);
				}
			}
			Toast.makeText(getBaseContext(), "Times up for Q"+finishedQsID.size()+"!", Toast.LENGTH_SHORT).show();
			RadioGrpAns.clearCheck();
			setNextRandomQuestion();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.we_activity_questions);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		getWindow().setBackgroundDrawableResource(R.drawable.bg2lytwt);
		
		if(customTitleSupported)
		{
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
			TextView myTitleText = (TextView) findViewById(R.id.myTitle);
			myTitleText.setText("Wireless Elims");
			//myTitleText.setTextSize(20);
			//myTitleText.setBackgroundColor(Color.rgb(220, 208, 255));
			//myTitleText.setTextColor(Color.rgb(0,0,139));
		}
		
		loadAllQuestionsFromXMLFile();
		
		getViews();
		setNextRandomQuestion();
		
		radioBtn1.setOnClickListener(this);
		radioBtn2.setOnClickListener(this);
		radioBtn3.setOnClickListener(this);
		radioBtn4.setOnClickListener(this);
		
		btnNext.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				//if(((RadioButton)findViewById(RadioGrpAns.getCheckedRadioButtonId())).getText().toString().equals(correctAns))
				if(selectedAns.equals(correctAns))
				{
					correctAnsCnt++;
					finishedQsID.put(currentQID, true);
				}
				RadioGrpAns.clearCheck();
				setNextRandomQuestion();
			}
		});
		
	}
	void getViews()
	{
		txtViewQuestion = (TextView)findViewById(R.id.txtViewQuestion);
		txtViewShowTimeLeft = (TextView)findViewById(R.id.txtViewShowTimeLeft);
		
		RadioGrpAns = (RadioGroup)findViewById(R.id.RadioGrpAns);
		
		radioBtn1 = (RadioButton)findViewById(R.id.radioBtn1);
		radioBtn2 = (RadioButton)findViewById(R.id.radioBtn2);
		radioBtn3 = (RadioButton)findViewById(R.id.radioBtn3);
		radioBtn4 = (RadioButton)findViewById(R.id.radioBtn4);
		
		btnNext = (Button)findViewById(R.id.btnNext);
	}
	void setNextRandomQuestion()
	{
		if(finishedQsID.size()!=questionsCnt)				//change max limit when no(Qs) change, earlier 45 then 10 then wrt num(Qs) in XML File
		{
			while(finishedQsID.containsKey(currentQID = randomNumGenerator.nextInt(questionsCnt)));	//change range when Qs changed
			finishedQsID.put(currentQID, false);
			currentQ = getQByQID(allQuestions, currentQID);
			//currentQ = getResources().getStringArray(QIDs[currentQID]);
			
			ImageView imgViewQImg = ((ImageView)findViewById(R.id.imgViewQImg));
			if(currentQ[0].startsWith("~["))
			{
				imgViewQImg.setVisibility(View.VISIBLE);
				String imgFileNameNQText[] = currentQ[0].split(":");
				imgViewQImg.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+".CSI VESIT Experience!"+File.separator+MainActivity.currentEvent+File.separator+MainActivity.currentEvent+"QsXML"+File.separator+imgFileNameNQText[0].trim().replaceAll("[~\\[\\]]", "")));
				//using regex replace brackets by nothing leaving out only the image File Name
				txtViewQuestion.setText("Q "+finishedQsID.size()+") "+imgFileNameNQText[1].trim());
			}else
			{
				imgViewQImg.setVisibility(View.GONE);
				txtViewQuestion.setText("Q "+finishedQsID.size()+") "+currentQ[0]);
			}
			
			if(currentQ[1].equals("null"))
				radioBtn1.setVisibility(View.GONE);
			else
			{
				radioBtn1.setVisibility(View.VISIBLE);
				radioBtn1.setText(currentQ[1]);
			}
			
			if(currentQ[2].equals("null"))
				radioBtn2.setVisibility(View.GONE);
			else
			{
				radioBtn2.setVisibility(View.VISIBLE);
				radioBtn2.setText(currentQ[2]);
			}
			
			if(currentQ[3].equals("null"))
				radioBtn3.setVisibility(View.GONE);
			else
			{
				radioBtn3.setVisibility(View.VISIBLE);
				radioBtn3.setText(currentQ[3]);
			}
			
			if(currentQ[4].equals("null"))
				radioBtn4.setVisibility(View.GONE);
			else
			{
				radioBtn4.setVisibility(View.VISIBLE);
				radioBtn4.setText(currentQ[4]);
			}
			
			correctAns = currentQ[5];
			countdownTimer30sec.start();
		}else
		{
			Toast.makeText(getBaseContext(), "All Qs are up", Toast.LENGTH_SHORT).show();
			countdownTimer30sec.cancel();
			finish();
			startActivity(new Intent("org.csiVesit.wirelesselims.WESubmitResults"));
		}
	}
	String[] getQByQID(ArrayList<String> srcArrayList, int num)
	{
		String[] temp = new String[6];
		num *= 6;
		for(int i=0;i<6;i++)
			temp[i]=srcArrayList.get(num+i);
		return temp;
	}
	private void loadAllQuestionsFromXMLFile()
	{
		XmlPullParser xpp = Xml.newPullParser();
        try
        {
        	xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        	//xpp.setInput(new FileReader(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+".CSI VESIT Experience!" +File.separator+currentEvent, currentEvent+"QsXML.xml")));
        	xpp.setInput(new StringReader(DecodeEXIToXML.decodeEXIToXML(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+".CSI VESIT Experience!" +File.separator+MainActivity.currentEvent+File.separator+MainActivity.currentEvent+"QsXML"+File.separator+MainActivity.currentEvent+"QsXML.exi")));
        	int eventType = xpp.getEventType();
			ArrayList<String> al = new ArrayList<String>();
			while (eventType != XmlPullParser.END_DOCUMENT)
	         {
	        	 if(eventType == XmlPullParser.START_TAG)
	        		 	if(xpp.getName().equals("Q") || xpp.getName().equals("Option1") || xpp.getName().equals("Option2") || xpp.getName().equals("Option3") || xpp.getName().equals("Option4") || xpp.getName().equals("CorrectAns"))
	        		 	{
	        		 		xpp.next();
	        		 		al.add(xpp.getText());
	        		 		questionsCnt++;
	        		 	}
	       /*if(eventType == XmlPullParser.TEXT)
	    	   al.add(xpp.getText());*/
	          eventType = xpp.next();
	         }
			for(String s:al)
	        	 allQuestions.add(s);
	         al = null;
		} catch (XmlPullParserException e)
		{
			e.printStackTrace();
		}catch(IOException ioe)
		{
			Toast.makeText(getBaseContext(), "IO Exception occured! Retry", Toast.LENGTH_LONG).show();;
		}
        questionsCnt = questionsCnt/6;			//6 tags per question
	}
	@Override
	public void onClick(View selectedRadioBtn)
	{
		selectedAns = (radioBtn1.isChecked() || radioBtn2.isChecked() || radioBtn3.isChecked() || radioBtn4.isChecked())?((RadioButton)findViewById(RadioGrpAns.getCheckedRadioButtonId())).getText().toString():"";
	}
}
