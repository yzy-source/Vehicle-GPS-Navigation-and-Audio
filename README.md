# Vehicle-GPS-Navigation-and-Audio
车载音频播放系统与GPS导航系统的设计与模拟
====
Accomplish an Android embedded system combining Arduino CMU and GPS Module with Bluetooth, to provide personalized driving service; got the best design award.

## 实验题目
车载音频播放系统与GPS导航系统的设计与模拟
## 项目概述：
1. 实现了车载音频播放与GPS导航的自动化与智能化。
2. 方便用户通过智能手机与车载系统之间的交互。
3. 通过车载音频播放系统与GPS导航系统的设计与模拟实验对嵌入式系统原理课程的熟练掌握与对嵌入式原理知识的深化记忆。
4. 加强本小组成员对嵌入式原理的更深层次的理解与运用。

## 任务分配：
组内每个人齐心协力，配合默契，都脚踏实地做实验，过程中也遇到了一些阻碍，但是我们每个人都是迎难而上，积极地解决问题，最终完成了实验。

## 实现的功能：
#### 1.音频模块：
   (1). 基础音频播放：<br>
   &emsp;&emsp;音量控制；<br>
   &emsp;&emsp;屏幕显示当前播放歌曲的基本信息；<br>
   &emsp;&emsp;歌曲切换（上下首切换、暂停、播放、停止）；<br>
   &emsp;&emsp;播放模式切换（随机播放、单曲播放、顺序播放）；<br>
   (2). 语音识别：通过语音识别按钮控制音乐播放，目前可以识别“上一首”、“下一首”、“暂停”、“播放”、“停止”、“导航”等功能。按下“语音识别”按钮，说出想要实现的功能，即可按语音指令进行播放和导航。<br>
#### 2.GPS模块：
   (1). GPS定位：通过GPS模块接收卫星的经纬度、速度、时间信息。<br>
   (2). 蓝牙模块：实现安卓客户端与Arduino数据通信。<br>
   (3). 数据传输协议：定义了数据传输协议，将位置信息以特定的格式经过蓝牙模块传到安卓手机客户端，并解析在屏幕输出经纬度、当前行驶速度信息。<br>
   (4).  旅游景点语音播报：导航自动开启景点旅游模式，进行个性化的语音提示。当行驶到旅游景点附近，自动语音播报景点介绍（景点历史文化、人文气息、特色等）。比如当行驶到南开大学图书馆附近时，会自动介绍图书馆的相关信息。<br>
   (5). 油量提醒：设定报警油量阈值，实时监测油量情况。油量低于阈值时，进行提醒。使用滑动变阻器对油量值进行模拟，将阻值进行一定的转换作为油量衡量标准。<br>
   (6). 停车位的寻找：通过Google API地图，点击大头钉显示停车位的信息，实时获取当前位置信息以圆点标记，导航找到停车位。<br>
#### 3.GPS模块与音频模块的串接：
   (1). 实现音乐自动挡：根据车速不同播放不同类型的音乐。比如此时车速大于零播放节奏轻快的歌曲，歌曲切换的阈值可根据实际情况进行设置，这里考虑到测试速度过小，以0为界限。<br>
#### 4.实验未按期完成功能与未来展望：
   (1). 特殊地点提醒与导航的路径规划未能实现：当油量过低需要查询附近的加油站或者前方是学校、景区、车站时需要减速行驶；输入目标地点自动查询最优路径并显示。<br>
   (2). GPS导航旅游景点语音播报与微信的连接，当驾驶者关注“南开大学”公众号时，自动切换到景点旅游模式，行驶到南开大学附近进行南开大学的介绍。<br>
   (3). 记录行车轨迹及个性化推荐。利用Android SQLite本地数据库记录行车轨迹并查询；使用机器学习相关知识在每日行车过程中进行个性化推荐如图书馆、电影院等。<br>
   (4). 硬件方面可以将Arduino板接外接电源，这样将程序烧录以后，就可以拔掉USB线，小车可以自由跑。<br>
## 嵌入式实验UML建模：
![img](https://github.com/yzy-source/Vehicle-GPS-Navigation-and-Audio/blob/master/img/uml.png)
## 技术方案原理：
(1). Arduino中初始化设置串口波特率9600，并设置输出引脚。Loop()中给不同的引脚高低电平与L298N电机驱动模块进行连接，可以让小车动起来。<br>
(2). GPS模块与Arduino通过串口连接可以在串口输出经纬度、速度、时间等数据信息，为了测试方便，我们将数据信息在串口监视器打印。<br>
(3). Android程序中的蓝牙可以发现蓝牙设备并进行设备匹配，进行蓝牙连接，将串口数据通过蓝牙传到手机客户端。<br>
(4). 通过Arduino程序中的analogRead()读取滑动变阻器阻值的模拟数据并送到串口。<br>
(5). 我们自定义了一个数据协议：#纬度,经度:速度;阻值*,通过Arduino程序将GPS的$GPRMC串口通信信息的经纬度、速度、滑动变阻器阻值通过蓝牙传到手机客户端进行解析。<br>
(6). 设置按钮（上下首暂停播放等的实现）：在按下按钮触发的事件函数里，调用MediaPlayer类的start()、pause()、stop()实现播放暂停等，利用全局变量musicIndex（代表目前播放的歌位于列表中的位置）实现上下首的切换。<br>
(7). 语音识别：使用Google核心的语音识别技术以及云端技术，可以实现简单的语音人机互动。在实验中，我们使用Android提供的RecognizerIntent类来实现语音识别的核心代码，并借助百度语音助手与Android程序进行云端数据交互。全过程可通过Intent直接调用和传递类内定义的动作和属性，在开启了语音活动后设置相关模式，最终由事件将语音通过网络传递至云服务器，并将解析后的结果文字传递回手机客户端，再进行相应的字符串判断，实现控制功能。<br>
(8). 开始导航：在从蓝牙接收到经度、纬度数据后，转换成自己建立的地图模型里的坐标，按下“开始导航”按钮后，触发按钮的onClick()事件，判断位于哪个建筑物附近，然后将位置信息显示在app右侧界面“位置信息”文本框里，并且播放语音，语音内容包含提示目前位置信息，并且简短介绍当前的建筑。<br>
(9). 播放模式：在界面中有一个按钮，点击按钮可实现播放模式的切换（初始为“顺序播放”，点击后可切换成“随机播放”，再次点击可切换成“单曲循环”，再次点击可回到“顺序播放”）。借助于全局变量musicPattern，每次点击，按照1,2,3,1,2,3........的顺序变化，每次点击后按钮上的文字也会变化，文字代表当前播放模式。<br>
(10). 油量变化：从蓝牙接收数据后，解析出油量数据，判断油量是否低于某个阈值，如果低，则停止当前所有音乐播放，播放警报语音，并且在屏幕上显示短消息。<br>
(11). 音乐自动挡按钮：点击该按钮可开启音乐自动挡功能，该功能根据解析到的速度数据，如果高于某个阈值，说明路况良好，则可以播放轻快愉悦的音乐。再次点击可关闭音乐自动挡功能，这时不能对速度数据做出相应，无法实现反馈。<br>
(12). 界面设计：考虑到普适计算的原理及应用，和车载系统的人机关系，采用最为直观易使用的界面，并加以通俗简洁的文字或语音提示，既保证了使用方法的简便，也使得车行过程中尽可能减少操作的误差。<br>
