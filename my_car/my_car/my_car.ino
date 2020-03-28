#define GPRMC_TERM "$GPRMC,"		//定义要解析的指令，因为这条指令包含定位和时间信息

char nmeaSentence[68];
String latitude;
String longitude;		
String lndSpeed;		
String pass;

void setup()	
{
  Serial.begin(9600);	            //set up the baud rate
  pinMode( 2 , OUTPUT);             //set up the output_pin to control the car to move
  pinMode( 4 , OUTPUT);
  pinMode( 12 , OUTPUT);
  pinMode( 13 , OUTPUT);
  pinMode( 3 , OUTPUT);
  pinMode( 5 , OUTPUT);
  pinMode( 10 , OUTPUT);
  pinMode( 11 , OUTPUT);		
}

void loop()		
{
  digitalWrite( 2 , HIGH );                 //set up the electromotor level 
  digitalWrite( 4 , HIGH );
  digitalWrite( 12 , LOW );
  digitalWrite( 13 , LOW );
  digitalWrite( 3 , HIGH );
  digitalWrite( 5 , HIGH );
  digitalWrite( 10 , LOW );
  digitalWrite( 11 , LOW );

  // For one second we parse GPS data and report some key values
  for (unsigned long start = millis(); millis() - start < 1000;)	     //scan the GPS in one second
  {
    while (Serial.available())	                                             //Serial available
    {
      char c = Serial.read();	                                            //the byte get from the Serial
      switch(c)					
     {
      case '$':					                            //if the byte is $，means the start of a frame data 
        Serial.readBytesUntil('*', nmeaSentence, 67);		           //read the following data and put them in the 'nmeaSentence'，the max number is 67 bytes 
        latitude = parseGprmcLat(nmeaSentence);	                           //get the latitude
        longitude = parseGprmcLon(nmeaSentence);                           //get the longitude
        lndSpeed = parseGprmcSpeed(nmeaSentence);                          //get the speed
        
        int sensorValue = analogRead(A0);
        if((latitude > "")&&(longitude > "")&&(lndSpeed > ""))             //print when the (latitude&&longitude&&lndSpeed) is not null
        {
          pass="#"+longitude+","+latitude+":"+lndSpeed+";"+sensorValue+"*";
          Serial.println(pass);
        }	
      }
    }
  }
}

//Parse GPRMC NMEA sentence data from String
//String must be GPRMC or no data will be parsed
//Return Latitude
String parseGprmcLat(String s)
{
  int pLoc = 0;                                                            //paramater location pointer
  int lEndLoc = 0;                                                        //lat parameter end location
  int dEndLoc = 0;                                                        //direction parameter end location
  String lat;
  
  if(s.substring(0,4) == "GPRM")
  {
    for(int i = 0; i < 4; i++)
    {
      if(i < 3) 
      {
        pLoc = s.indexOf(',', pLoc+1);  
      }
      if(i == 3)
      {
        lEndLoc = s.indexOf(',', pLoc+1);
        lat = s.substring(pLoc+1, lEndLoc);
      }
    }
    return  lat; 
  }
}

//Parse GPRMC NMEA sentence data from String
//String must be GPRMC or no data will be parsed
//Return Longitude
String parseGprmcLon(String s)
{
  int pLoc = 0;                                                  //paramater location pointer
  int lEndLoc = 0;                                               //lat parameter end location
  int dEndLoc = 0;                                               //direction parameter end location
  String lon;

  if(s.substring(0,4) == "GPRM")
  {
    for(int i = 0; i < 6; i++)
    {
      if(i < 5) 
      {
        pLoc = s.indexOf(',', pLoc+1);
      }
      if(i == 5)
      {
        lEndLoc = s.indexOf(',', pLoc+1);
        lon = s.substring(pLoc+1, lEndLoc);
      }
    }
    return lon; 
  }
}

//Parse GPRMC NMEA sentence data from String
//String must be GPRMC or no data will be parsed
//Return speed
String parseGprmcSpeed(String s)
{
  int pLoc = 0;                                                 //paramater location pointer
  int lEndLoc = 0;                                             //lat parameter end location
  int dEndLoc = 0;                                             //direction parameter end location
  String lndSpeed;

  if(s.substring(0,4) == "GPRM")
  {
    for(int i = 0; i < 8; i++)
    {
      if(i < 7) 
      {
        pLoc = s.indexOf(',', pLoc+1);
      }
      else
      {
        lEndLoc = s.indexOf(',', pLoc+1);
        lndSpeed = s.substring(pLoc+1, lEndLoc);
      }
    }
    return lndSpeed; 
  }
}


