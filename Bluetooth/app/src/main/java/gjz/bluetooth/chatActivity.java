package gjz.bluetooth;

import gjz.bluetooth.R;
import gjz.bluetooth.Bluetooth.ServerOrCilent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import android.app.Activity;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.speech.RecognizerIntent;

import org.w3c.dom.Text;

public class chatActivity extends Activity implements OnItemClickListener ,OnClickListener{
    /** Called when the activity is first created. */
	
	//private ListView mListView;
	//private ArrayList<deviceListItem>list;
	//private Button sendButton;
	private Button disconnectButton;
	//private EditText editMsgView;
	//deviceListAdapter mAdapter;
	Context mContext;
	
	/* һЩ��������������������� */
	public static final String PROTOCOL_SCHEME_L2CAP = "btl2cap";
	public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";
	public static final String PROTOCOL_SCHEME_BT_OBEX = "btgoep";
	public static final String PROTOCOL_SCHEME_TCP_OBEX = "tcpobex";

	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1;

	private static double x=10;
	private static double y=8.5;
	private static double speed;
	private static double oilMass;
	
	private BluetoothServerSocket mserverSocket = null;
	private ServerThread startServerThread = null;
	private clientThread clientConnectThread = null;
	private BluetoothSocket socket = null;
	private BluetoothDevice device = null;
	private readThread mreadThread = null;
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	private Button mPlayButton;
	private Button mPauseButton;
	private Button mStopButton;
	private Button mNextButton;
	private Button mPrevButton;
	private Button mQueryButton;
	private Button mMusicChange;
	private Button mMusicPattern;
	private Button mVoiceRecognize;
	private Button mWholeIntroduce;

	private int mMusicIndex;
	private int musicPattern=1;

	private MediaPlayer musicPlayer;
	private MediaPlayer locationPlayer;
	private MediaPlayer introducePlayer;
	private MediaPlayer mOilPlayer;
	private boolean isMusicPlay;

	private TextView mMusicTextView;
	private TextView mLocationTextView;

	private TextView xText;
	private TextView yText;
	private TextView speedText;
	private TextView oilText;

	private String s="";

	private boolean isTuned=true;
	private boolean isNorPlaying=false;
	private boolean pattern=true;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.chat);
        mContext = this;
        init();
    }
    
	private void init() {		   
		//list = new ArrayList<deviceListItem>();
		//mAdapter = new deviceListAdapter(this, list);
		//mListView = (ListView) findViewById(R.id.list);
		//mListView.setAdapter(mAdapter);
		//mListView.setOnItemClickListener(this);
		//mListView.setFastScrollEnabled(true);
		//editMsgView= (EditText)findViewById(R.id.MessageText);
		//editMsgView.clearFocus();
		
		//sendButton= (Button)findViewById(R.id.btn_msg_send);
		/*sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String msgText =editMsgView.getText().toString();
				if (msgText.length()>0) {
					sendMessageHandle(msgText);	
					editMsgView.setText("");
					editMsgView.clearFocus();
					//close InputMethodManager
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
					imm.hideSoftInputFromWindow(editMsgView.getWindowToken(), 0);
				}else
				Toast.makeText(mContext, "Can't send blank content!", Toast.LENGTH_SHORT).show();
			}
		});   //*/

		mMusicIndex=1;
		mMusicTextView=(TextView)findViewById(R.id.music_view);
		xText=(TextView)findViewById(R.id.longtitude);
		yText=(TextView)findViewById(R.id.latitude);
		speedText=(TextView)findViewById(R.id.car_speed);
		oilText=(TextView)findViewById(R.id.oil_mass);
		mLocationTextView=(TextView)findViewById(R.id.location_view);

		musicPlayer=MediaPlayer.create(chatActivity.this,R.raw.music1);

		mVoiceRecognize=(Button)findViewById(R.id.voice_recognize);
		mVoiceRecognize.setOnClickListener(new myRecognizerIntentListener());

		mWholeIntroduce=(Button)findViewById(R.id.whole_introduce);
		mWholeIntroduce.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				introducePlayer=MediaPlayer.create(chatActivity.this,R.raw.introduce);
				introducePlayer.start();
			}
		});

		mMusicChange=(Button)findViewById(R.id.music_change);
		mMusicChange.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(pattern)
				{
					pattern=false;
				}
				else
				{
					pattern=true;
				}
			}
		});

		mMusicPattern=(Button)findViewById(R.id.music_pattern);
		mMusicPattern.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(musicPattern==1)
				{
					musicPattern=2;
					mMusicPattern.setText("�������");
				}
				else if(musicPattern==2)
				{
					musicPattern=3;
					mMusicPattern.setText("����ѭ��");
				}
				else if(musicPattern==3)
				{
					musicPattern=1;
					mMusicPattern.setText("˳�򲥷�");
				}
			}
		});

		mQueryButton=(Button)findViewById(R.id.query_button);
		mQueryButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if((x*x+y*y)<1)
				{
					mLocationTextView.setText("��λ���������5D����");
					locationPlayer=MediaPlayer.create(chatActivity.this,R.raw.location1);
					locationPlayer.start();
				}
				else if(((x-2)*(x-2)+(y-2.5)*(y-2.5))<1)
				{
					mLocationTextView.setText("��λ���������5B����");
					locationPlayer=MediaPlayer.create(chatActivity.this,R.raw.location2);
					locationPlayer.start();
				}
				else if(((x-1)*(x-1)+(y-3)*(y-3))<1)
				{
					mLocationTextView.setText("��λ�����ʳ�ø���");
					locationPlayer=MediaPlayer.create(chatActivity.this,R.raw.location3);
					locationPlayer.start();
				}
				else if(((x-3)*(x-3)+(y-4.5)*(y-4.5))<1)
				{
					mLocationTextView.setText("��λ�ڼ��������ƹ���ѧԺ����");
					locationPlayer=MediaPlayer.create(chatActivity.this,R.raw.location4);
					locationPlayer.start();
				}
				else if(((x-3.5)*(x-3.5)+(y-5)*(y-5))<1)
				{
					mLocationTextView.setText("��λ�ڲ���ѧԺ����");
					locationPlayer=MediaPlayer.create(chatActivity.this,R.raw.location5);
					locationPlayer.start();
				}
				else if(((x-1.5)*(x-1.5)+(y-6)*(y-6))<1)
				{
					mLocationTextView.setText("��λ�����ѧԺ����");
					locationPlayer=MediaPlayer.create(chatActivity.this,R.raw.location6);
					locationPlayer.start();
				}
				else if(((x+2)*(x+2)+(y-2)*(y-2))<4)
				{
					mLocationTextView.setText("��λ���˶�������");
					locationPlayer=MediaPlayer.create(chatActivity.this,R.raw.location7);
					locationPlayer.start();
				}
				else if(((x-8)*(x-8)+(y-1.5)*(y-1.5))<0.25)
				{
					mLocationTextView.setText("��λ�������񸽽�");
					locationPlayer=MediaPlayer.create(chatActivity.this,R.raw.location8);
					locationPlayer.start();
				}
				else if(((x-8)*(x-8)+(y-4)*(y-4))<2.25)
				{
					mLocationTextView.setText("��λ��ͼ��ݸ���");
					locationPlayer=MediaPlayer.create(chatActivity.this,R.raw.location9);
					locationPlayer.start();
				}
				else if(((x-7.5)*(x-7.5)+(y-6)*(y-6))<1)
				{
					mLocationTextView.setText("��λ�ڽ�ѧ¥����");
					locationPlayer=MediaPlayer.create(chatActivity.this,R.raw.location10);
					locationPlayer.start();
				}
				else if(((x-9.5)*(x-9.5)+(y-8)*(y-8))<1)
				{
					mLocationTextView.setText("��λ��ʵ��¥����");
					locationPlayer=MediaPlayer.create(chatActivity.this,R.raw.location11);
					locationPlayer.start();
				}
				else if(((x-2.5)*(x-2.5)+(y-8)*(y-8))<1)
				{
					mLocationTextView.setText("��λ���̵ظ���");
					locationPlayer=MediaPlayer.create(chatActivity.this,R.raw.location12);
					locationPlayer.start();
				}
				else{
					mLocationTextView.setText("��������ǰ��");
				}    //*/
			}
		});

		mPlayButton=(Button)findViewById(R.id.play);
		mPlayButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				isNorPlaying=true;
				isTuned=false;
				if(isMusicPlay==false)
				{
					if(mMusicIndex==1){
						musicPlayer= MediaPlayer.create(chatActivity.this,R.raw.music1);
						musicPlayer.start();
					}
					if(mMusicIndex==2){
						musicPlayer= MediaPlayer.create(chatActivity.this,R.raw.music2);
						musicPlayer.start();
					}
					if(mMusicIndex==3){
						musicPlayer= MediaPlayer.create(chatActivity.this,R.raw.music3);
						musicPlayer.start();
					}
					if(mMusicIndex==4){
						musicPlayer= MediaPlayer.create(chatActivity.this,R.raw.music4);
						musicPlayer.start();
					}
					if(mMusicIndex==5){
						musicPlayer= MediaPlayer.create(chatActivity.this,R.raw.music5);
						musicPlayer.start();
					}
					if(mMusicIndex==6){
						musicPlayer= MediaPlayer.create(chatActivity.this,R.raw.music6);
						musicPlayer.start();
					}
					if(mMusicIndex==7){
						musicPlayer= MediaPlayer.create(chatActivity.this,R.raw.music7);
						musicPlayer.start();
					}
					if(mMusicIndex==8){
						musicPlayer= MediaPlayer.create(chatActivity.this,R.raw.music8);
						musicPlayer.start();
					}
					if(mMusicIndex==9){
						musicPlayer= MediaPlayer.create(chatActivity.this,R.raw.music9);
						musicPlayer.start();
					}
					if(mMusicIndex==10){
						musicPlayer= MediaPlayer.create(chatActivity.this,R.raw.music10);
						musicPlayer.start();
					}
				}
				else {
					musicPlayer.start();
				}
				isMusicPlay=true;
				if(mMusicIndex==1) mMusicTextView.setText("��ǰ���ţ���ҹС¥�ֶ��硪����Դ��");
				if(mMusicIndex==2) mMusicTextView.setText("��ǰ���ţ�Call Me Maybe����Carly Rae Jepsen");
				if(mMusicIndex==3) mMusicTextView.setText("��ǰ���ţ���Ȼ�ѡ�����嶼�");
				if(mMusicIndex==4) mMusicTextView.setText("��ǰ���ţ�I really like you����Carly Rae Jepsen");
				if(mMusicIndex==5) mMusicTextView.setText("��ǰ���ţ���Ա����Ѧ֮ǫ");
				if(mMusicIndex==6) mMusicTextView.setText("��ǰ���ţ�Sugar����Maroon 5");
				if(mMusicIndex==7) mMusicTextView.setText("��ǰ���ţ���ɫ�߸�Ь�����̽���");
				if(mMusicIndex==8) mMusicTextView.setText("��ǰ���ţ�Stronger����Kelly Clarkson");
				if(mMusicIndex==9) mMusicTextView.setText("��ǰ���ţ�����������Ѳɽ������Ӣ��");
				if(mMusicIndex==10) mMusicTextView.setText("��ǰ���ţ�What Do You Mean����Justin Bieber");
			}
		});

		mPauseButton=(Button)findViewById(R.id.music_pause);
		mPauseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				musicPlayer.pause();
				mMusicTextView.setText("���ֲ�������ͣ��");
			}
		});

		mStopButton=(Button)findViewById(R.id.music_stop);
		mStopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				musicPlayer.stop();
				musicPlayer.reset();
				musicPlayer.release();
				isMusicPlay=false;
				mMusicTextView.setText("���ֲ�����ֹͣ��");
			}
		});

		mPrevButton=(Button)findViewById(R.id.music_previous);
		mPrevButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				isNorPlaying = true;
				isTuned = false;
				musicPlayer.stop();
				if(musicPattern==1){
					if (mMusicIndex > 1) {
						mMusicIndex -= 1;
					} else {
						mMusicIndex = 10;
					}
					if (mMusicIndex == 1) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music1);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���ҹС¥�ֶ��硪����Դ��");
					}
					if (mMusicIndex == 2) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music2);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�Call Me Maybe����Carly Rae Jepsen");
					}
					if (mMusicIndex == 3) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music3);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���Ȼ�ѡ�����嶼�");
					}
					if (mMusicIndex == 4) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music4);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�I really like you����Carly Rae Jepsen");
					}
					if (mMusicIndex == 5) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music5);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���Ա����Ѧ֮ǫ");
					}
					if (mMusicIndex == 6) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music6);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�Sugar����Maroon 5");
					}
					if (mMusicIndex == 7) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music7);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���ɫ�߸�Ь�����̽���");
					}
					if (mMusicIndex == 8) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music8);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�Stronger����Kelly Clarkson");
					}
					if (mMusicIndex == 9) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music9);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�����������Ѳɽ������Ӣ��");
					}
					if (mMusicIndex == 10) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music10);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�What Do You Mean����Justin Bieber");
					}
				}
				if(musicPattern==2)
				{
					double randomTempNum=Math.random()*10+1;
					int randomNum=(int)(randomTempNum/1);
					mMusicIndex=randomNum;
					if (mMusicIndex == 1) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music1);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���ҹС¥�ֶ��硪����Դ��");
					}
					if (mMusicIndex == 2) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music2);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�Call Me Maybe����Carly Rae Jepsen");
					}
					if (mMusicIndex == 3) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music3);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���Ȼ�ѡ�����嶼�");
					}
					if (mMusicIndex == 4) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music4);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�I really like you����Carly Rae Jepsen");
					}
					if (mMusicIndex == 5) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music5);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���Ա����Ѧ֮ǫ");
					}
					if (mMusicIndex == 6) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music6);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�Sugar����Maroon 5");
					}
					if (mMusicIndex == 7) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music7);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���ɫ�߸�Ь�����̽���");
					}
					if (mMusicIndex == 8) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music8);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�Stronger����Kelly Clarkson");
					}
					if (mMusicIndex == 9) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music9);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�����������Ѳɽ������Ӣ��");
					}
					if (mMusicIndex == 10) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music10);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�What Do You Mean����Justin Bieber");
					}
				}
				if(musicPattern==3)
				{
					if (mMusicIndex == 1) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music1);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���ҹС¥�ֶ��硪����Դ��");
					}
					if (mMusicIndex == 2) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music2);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�Call Me Maybe����Carly Rae Jepsen");
					}
					if (mMusicIndex == 3) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music3);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���Ȼ�ѡ�����嶼�");
					}
					if (mMusicIndex == 4) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music4);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�I really like you����Carly Rae Jepsen");
					}
					if (mMusicIndex == 5) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music5);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���Ա����Ѧ֮ǫ");
					}
					if (mMusicIndex == 6) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music6);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�Sugar����Maroon 5");
					}
					if (mMusicIndex == 7) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music7);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���ɫ�߸�Ь�����̽���");
					}
					if (mMusicIndex == 8) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music8);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�Stronger����Kelly Clarkson");
					}
					if (mMusicIndex == 9) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music9);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�����������Ѳɽ������Ӣ��");
					}
					if (mMusicIndex == 10) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music10);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�What Do You Mean����Justin Bieber");
					}
				}
			}
		});

		mNextButton=(Button)findViewById(R.id.music_next);
		mNextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				isNorPlaying=true;
				isTuned=false;
				musicPlayer.stop();
				if(musicPattern==1) {
					if (mMusicIndex == 10) {
						mMusicIndex = 1;
					} else {
						mMusicIndex += 1;
					}
					if (mMusicIndex == 1) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music1);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���ҹС¥�ֶ��硪����Դ��");
					}
					if (mMusicIndex == 2) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music2);
						musicPlayer.start();
						mMusicTextView.setText(
								"��ǰ���ţ�Call Me Maybe����Carly Rae Jepsen");
					}
					if (mMusicIndex == 3) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music3);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���Ȼ�ѡ�����嶼�");
					}
					if (mMusicIndex == 4) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music4);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�I really like you����Carly Rae Jepsen");
					}
					if (mMusicIndex == 5) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music5);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���Ա����Ѧ֮ǫ");
					}
					if (mMusicIndex == 6) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music6);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�Sugar����Maroon 5");
					}
					if (mMusicIndex == 7) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music7);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���ɫ�߸�Ь�����̽���");
					}
					if (mMusicIndex == 8) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music8);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�Stronger����Kelly Clarkson");
					}
					if (mMusicIndex == 9) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music9);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�����������Ѳɽ������Ӣ��");
					}
					if (mMusicIndex == 10) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music10);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�What Do You Mean����Justin Bieber");
					}
				}
				if(musicPattern==2)
				{
					double randomTempNum=Math.random()*10+1;
					int randomNum=(int)(randomTempNum/1);
					mMusicIndex=randomNum;
					if (mMusicIndex == 1) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music1);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���ҹС¥�ֶ��硪����Դ��");
					}
					if (mMusicIndex == 2) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music2);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�Call Me Maybe����Carly Rae Jepsen");
					}
					if (mMusicIndex == 3) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music3);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���Ȼ�ѡ�����嶼�");
					}
					if (mMusicIndex == 4) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music4);;;
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�I really like you����Carly Rae Jepsen");
					}
					if (mMusicIndex == 5) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music5);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���Ա����Ѧ֮ǫ");
					}
					if (mMusicIndex == 6) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music6);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�Sugar����Maroon 5");
					}
					if (mMusicIndex == 7) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music7);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���ɫ�߸�Ь�����̽���");
					}
					if (mMusicIndex == 8) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music8);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�Stronger����Kelly Clarkson");
					}
					if (mMusicIndex == 9) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music9);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�����������Ѳɽ������Ӣ��");
					}
					if (mMusicIndex == 10) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music10);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�What Do You Mean����Justin Bieber");
					}
				}
				if(musicPattern==3)
				{
					if (mMusicIndex == 1) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music1);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���ҹС¥�ֶ��硪����Դ��");
					}
					if (mMusicIndex == 2) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music2);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�Call Me Maybe����Carly Rae Jepsen");
					}
					if (mMusicIndex == 3) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music3);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���Ȼ�ѡ�����嶼�");
					}
					if (mMusicIndex == 4) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music4);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�I really like you����Carly Rae Jepsen");
					}
					if (mMusicIndex == 5) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music5);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���Ա����Ѧ֮ǫ");
					}
					if (mMusicIndex == 6) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music6);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�Sugar����Maroon 5");
					}
					if (mMusicIndex == 7) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music7);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ���ɫ�߸�Ь�����̽���");
					}
					if (mMusicIndex == 8) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music8);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�Stronger����Kelly Clarkson");
					}
					if (mMusicIndex == 9) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music9);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�����������Ѳɽ������Ӣ��");
					}
					if (mMusicIndex == 10) {
						musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music10);
						musicPlayer.start();
						mMusicTextView.setText("��ǰ���ţ�What Do You Mean����Justin Bieber");
					}
				}
			}
		});

		disconnectButton= (Button)findViewById(R.id.btn_disconnect);
		disconnectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
		        if (Bluetooth.serviceOrCilent == ServerOrCilent.CILENT) 
				{
		        	shutdownClient();
				}
				else if (Bluetooth.serviceOrCilent == ServerOrCilent.SERVICE) 
				{
					shutdownServer();
				}
				Bluetooth.isOpen = false;
				Bluetooth.serviceOrCilent=ServerOrCilent.NONE;
				Toast.makeText(mContext, "Connection already disconnected!", Toast.LENGTH_SHORT).show();
			}
		});		
	}

	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		if(requestCode==VOICE_RECOGNITION_REQUEST_CODE&&resultCode==RESULT_OK){
			ArrayList<String> results=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String resultString="";
			resultString=results.get(0);
			Toast.makeText(this,resultString, Toast.LENGTH_LONG).show();
			if(resultString.equals(new String("����")))
			{
				mPlayButton.performClick();
			}
			if(resultString.equals(new String("��ͣ")))
			{
				mPauseButton.performClick();
			}
			if(resultString.equals(new String("ֹͣ")))
			{
				mStopButton.performClick();
			}
			if(resultString.equals(new String("��һ��")))
			{
				mPrevButton.performClick();
			}
			if(resultString.equals(new String("��һ��")))
			{
				mNextButton.performClick();
			}
			if(resultString.equals(new String("����")))
			{
				mQueryButton.performClick();
			}
		}
		super.onActivityResult(requestCode,resultCode,data);
	}

	public class myRecognizerIntentListener implements View.OnClickListener {
		public void onClick(View v) {
			// ��Intent����������ʶ���ģʽ,���ҿ�������ģʽ
			Intent intent = new Intent(
					RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			// ����ģʽ��������ʽ������ʶ��
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			// ��ʾ���Կ�ʼ
			intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "�뿪ʼ����");
			// ��ʼ����ʶ��
			startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
		}
	}

    private Handler LinkDetectedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	//Toast.makeText(mContext, (String)msg.obj, Toast.LENGTH_SHORT).show();
        	if(msg.what==1)
        	{
        		//list.add(new deviceListItem((String)msg.obj, true));
				String temp_s=(String)msg.obj;
				int len=temp_s.length();
				boolean isEnd=false;
				int startPoint=0;
				int endPoint=0;
				int midPoint=0;
				int midPoint_2=0;
				int midPoint_3=0;
				for(int i=0;i<len;i++)
				{
					String sub=new String("");
					sub=temp_s.substring(i,i+1);
					if(sub.equals(new String("*")))
					{
						isEnd=true;
						endPoint=i;
						break;
					}
				}
				if(isEnd)
				{
					s=s+temp_s.substring(0,endPoint+1);
					for(int i=0;i<s.length();i++)
					{
						String mid=s.substring(i,i+1);
						if(mid.equals(new String("#")))
						{
							startPoint=i;
						}
						if(mid.equals(new String(",")))
						{
							midPoint=i;
						}
						if(mid.equals(new String(":")))
						{
							midPoint_2=i;
						}
						if(mid.equals(new String(";")))
						{
							midPoint_3=i;
							break;
						}
					}
					String num_1=s.substring(startPoint+1,midPoint);
					String num_2=s.substring(midPoint+1,midPoint_2);
					String num_3=s.substring(midPoint_2+1,midPoint_3);
					String num_4=s.substring(midPoint_3+1,s.length()-1);
					speedText.setText("�ٶȣ�"+num_3+" ");
					oilText.setText("������"+num_4+" ");
					double temp_x=Double.parseDouble(num_1);
					double temp_y=Double.parseDouble(num_2);
					int e_du=0;
					int  e_min=0;
					int e_sec=0;
					int  n_du=0;
					int  n_min=0;
					int n_sec=0;
					e_du=(int)(temp_x/100);
					n_du=(int)(temp_y/100.0);
					e_min=(int)((((int)(temp_x/1)-e_du*100))/1);
					n_min=(int)((((int)(temp_y/1)-n_du*100))/1);
					e_sec=(int)((temp_x%1.0*60)/1);
					n_sec=(int)((temp_y%1.0*60)/1);
					xText.setText("���ȣ�"+e_du+"��"+e_min+"'"+e_sec+"''"+"E ");
					yText.setText("γ�ȣ�"+n_du+"��"+n_min+"'"+n_sec+"''"+"N ");
					//x=(double)(e_sec-1.0)/3.0;
					//y=(double)(n_sec-3.0)/1.3;
					//mLocationTextView.setText(x+"-----"+y);
					speed=Double.parseDouble(num_3);
					oilMass=Double.parseDouble(num_4);
					midPoint=0;
					midPoint_2=0;
					midPoint_3=0;
					s=temp_s.substring(endPoint+1,temp_s.length());
					if(pattern) {
						if (speed > 0 && ((isTuned && !musicPlayer.isPlaying()) || (isNorPlaying))) {
							musicPlayer.stop();
							mMusicIndex = 6;
							musicPlayer = MediaPlayer.create(chatActivity.this, R.raw.music6);
							musicPlayer.start();
							isTuned = true;
						}
					}
					oilMass=oilMass/10.0;
					String warningStr="��������!!!";
					if(oilMass<35)
					{
						if(musicPlayer.isPlaying())
						{
							musicPlayer.stop();
						}
						if(locationPlayer!=null) {
							if (locationPlayer.isPlaying()) {
								locationPlayer.stop();
							}
						}
						mOilPlayer= MediaPlayer.create(chatActivity.this,R.raw.oil_mass);
						mOilPlayer.start();
						Toast.makeText(chatActivity.this,warningStr,Toast.LENGTH_LONG).show();
					}
				}
				else
				{
					s=s+temp_s;
				}   //*/
        	}
        	else
        	{
        		//list.add(new deviceListItem((String)msg.obj, false));
        	}
			//mAdapter.notifyDataSetChanged();
			//mListView.setSelection(list.size() - 1);
        }
        
    };    
    
    @Override
    public synchronized void onPause() {
        super.onPause();
    }
    @Override
    public synchronized void onResume() {
        super.onResume();
        if(Bluetooth.isOpen)
        {
        	Toast.makeText(mContext, "The connection is open and so is the communication.If connect again,please disconnect first!", Toast.LENGTH_SHORT).show();
        	return;
        }
        if(Bluetooth.serviceOrCilent==ServerOrCilent.CILENT)
        {
			String address = Bluetooth.BlueToothAddress;
			if(!address.equals("null"))
			{
				device = mBluetoothAdapter.getRemoteDevice(address);	
				clientConnectThread = new clientThread();
				clientConnectThread.start();
				Bluetooth.isOpen = true;
			}
			else
			{
				Toast.makeText(mContext, "address is null !", Toast.LENGTH_SHORT).show();
			}
        }
        else if(Bluetooth.serviceOrCilent==ServerOrCilent.SERVICE)
        {        	      	
        	startServerThread = new ServerThread();
        	startServerThread.start();
        	Bluetooth.isOpen = true;
        }
    }
	//�����ͻ���
	private class clientThread extends Thread { 		
		public void run() {
			try {
				//����һ��Socket���ӣ�ֻ��Ҫ��������ע��ʱ��UUID��
				// socket = device.createRfcommSocketToServiceRecord(BluetoothProtocols.OBEX_OBJECT_PUSH_PROTOCOL_UUID);
				socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
				//����
				Message msg2 = new Message();
				msg2.obj = "Wait please!Connecting to the server now:"+Bluetooth.BlueToothAddress;
				msg2.what = 0;
				LinkDetectedHandler.sendMessage(msg2);
				
				socket.connect();
				
				Message msg = new Message();
				msg.obj = "Connected to the server and messages can be sent!";
				msg.what = 0;
				LinkDetectedHandler.sendMessage(msg);
				//������������
				mreadThread = new readThread();
				mreadThread.start();
			} 
			catch (IOException e) 
			{
				Log.e("connect", "", e);
				Message msg = new Message();
				msg.obj = "Failed connecting to the server!Please disconnect the link and try again!";  //���ӷ�����쳣���Ͽ�����������һ�ԡ�
				msg.what = 0;
				LinkDetectedHandler.sendMessage(msg);
			} 
		}
	};

	//����������
	private class ServerThread extends Thread { 
		public void run() {
					
			try {
				/* ����һ������������ 
				 * �����ֱ𣺷��������ơ�UUID	 */	
				mserverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(PROTOCOL_SCHEME_RFCOMM,
						UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));		
				
				Log.d("server", "wait cilent connect...");
				
				Message msg = new Message();
				msg.obj = "���Ժ����ڵȴ��ͻ��˵�����...";
				msg.what = 0;
				LinkDetectedHandler.sendMessage(msg);
				
				/* ���ܿͻ��˵��������� */
				socket = mserverSocket.accept();
				Log.d("server", "accept success !");
				
				Message msg2 = new Message();
				String info = "�ͻ����Ѿ������ϣ����Է�����Ϣ��";
				msg2.obj = info;
				msg.what = 0;
				LinkDetectedHandler.sendMessage(msg2);
				//������������
				mreadThread = new readThread();
				mreadThread.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	/* ֹͣ������ */
	private void shutdownServer() {
		new Thread() {
			public void run() {
				if(startServerThread != null)
				{
					startServerThread.interrupt();
					startServerThread = null;
				}
				if(mreadThread != null)
				{
					mreadThread.interrupt();
					mreadThread = null;
				}				
				try {					
					if(socket != null)
					{
						socket.close();
						socket = null;
					}
					if (mserverSocket != null)
					{
						mserverSocket.close();/* �رշ����� */
						mserverSocket = null;
					}
				} catch (IOException e) {
					Log.e("server", "mserverSocket.close()", e);
				}
			};
		}.start();
	}
	/* ֹͣ�ͻ������� */
	private void shutdownClient() {
		new Thread() {
			public void run() {
				if(clientConnectThread!=null)
				{
					clientConnectThread.interrupt();
					clientConnectThread= null;
				}
				if(mreadThread != null)
				{
					mreadThread.interrupt();
					mreadThread = null;
				}
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					socket = null;
				}
			};
		}.start();
	}
	//��������
	private void sendMessageHandle(String msg) 
	{		
		if (socket == null) 
		{
			Toast.makeText(mContext, "There is no connetion.Please check!", Toast.LENGTH_SHORT).show();
			return;
		}
		try {				
			OutputStream os = socket.getOutputStream(); 
			os.write(msg.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		//list.add(new deviceListItem(msg, false));
		//mAdapter.notifyDataSetChanged();
		//mListView.setSelection(list.size() - 1);
	}
	//��ȡ����
    private class readThread extends Thread { 
        public void run() {
        	
            byte[] buffer = new byte[1024];
            int bytes;
			int iii=0;
            InputStream mmInStream = null;
			String s="";
            
			try {
				mmInStream = socket.getInputStream();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
            while (true) {
                try {
                    // Read from the InputStream
                    if( (bytes = mmInStream.read(buffer)) > 0 )
                    {
	                    byte[] buf_data = new byte[bytes];
				    	for(int i=0; i<bytes; i++)
				    	{
				    		buf_data[i] = buffer[i];
				    	}
						String temp_s = new String(buf_data);
						Message msg = new Message();
						msg.obj = temp_s;
						msg.what = 1;
						LinkDetectedHandler.sendMessage(msg);
						iii++;  //*/
                    }
                } catch (IOException e) {
                	try {
						mmInStream.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                    break;
                }
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (Bluetooth.serviceOrCilent == ServerOrCilent.CILENT) 
		{
        	shutdownClient();
		}
		else if (Bluetooth.serviceOrCilent == ServerOrCilent.SERVICE) 
		{
			shutdownServer();
		}
        Bluetooth.isOpen = false;
		Bluetooth.serviceOrCilent = ServerOrCilent.NONE;
    }
	public class SiriListItem {
		String message;
		boolean isSiri;

		public SiriListItem(String msg, boolean siri) {
			message = msg;
			isSiri = siri;
		}
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
	}	
	public class deviceListItem {
		String message;
		boolean isSiri;

		public deviceListItem(String msg, boolean siri) {
			message = msg;
			isSiri = siri;
		}
	}
}