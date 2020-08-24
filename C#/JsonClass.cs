using System.Collections.Generic;

public class RconSet
{
    /// <summary>
    /// 
    /// </summary>
    public string ipAddress { get; set; }
    /// <summary>
    /// 
    /// </summary>
    public int rconPort { get; set; }
    /// <summary>
    /// 
    /// </summary>
    public string passWd { get; set; }
}

public class Format
{
    /// <summary>
    /// [MM月dd日 HH:mm]
    /// </summary>
    public string time { get; set; }
    /// <summary>
    /// 不存在的，没有的
    /// </summary>
    public string noBest { get; set; }
    /// <summary>
    /// 不存在的，大家都是0分
    /// </summary>
    public string zeroBest { get; set; }
    /// <summary>
    /// [{0}个香蕉]
    /// </summary>
    public string lastBest { get; set; }
    /// <summary>
    /// ,不过他好像是个BOT啊...
    /// </summary>
    public string botBest { get; set; }
    /// <summary>
    /// AI难度已经调整至{0}%~
    /// </summary>
    public string aiDifficult { get; set; }
    /// <summary>
    /// 看门狗已经启动~
    /// </summary>
    public string started { get; set; }
    /// <summary>
    /// {0}[string]离开了这里，去寻找自己的人♂参
    /// </summary>
    public string leave { get; set; }
    /// <summary>
    /// 第{0}位猛男[string]加入了游戏
    /// </summary>
    public string join { get; set; }
}

public class Message
{
    /// <summary>
    /// 
    /// </summary>
    public int msgIntev { get; set; }
    /// <summary>
    /// 
    /// </summary>
    public int waitTime { get; set; }
    /// <summary>
    /// 垃圾看门狗
    /// </summary>
    public string msgHeader { get; set; }
    /// <summary>
    /// 
    /// </summary>
    public List<string> msgList { get; set; }
    /// <summary>
    /// 
    /// </summary>
    public Format format { get; set; }
}

public class Setting
{
    /// <summary>
    /// 
    /// </summary>
    public bool hideEmpty { get; set; }
    /// <summary>
    /// 【XP怪】ABC的垃圾配置服[复活{0}#10v45满地爬]
    /// </summary>
    public string serverName { get; set; }
    /// <summary>
    /// 
    /// </summary>
    public string defaultCvarSets { get; set; }
}

public class Difficult
{
    /// <summary>
    /// 
    /// </summary>
    public float minDifficult { get; set; }
    /// <summary>
    /// 
    /// </summary>
    public float maxDifficult { get; set; }
    /// <summary>
    /// 
    /// </summary>
    public int minToScale { get; set; }
    /// <summary>
    /// 
    /// </summary>
    public int maxToScale { get; set; }
}

public class SoloBot
{
    /// <summary>
    /// 
    /// </summary>
    public int fullSetPlayer { get; set; }
    /// <summary>
    /// 
    /// </summary>
    public int minBots { get; set; }
    /// <summary>
    /// 
    /// </summary>
    public int maxBots { get; set; }
    /// <summary>
    /// 
    /// </summary>
    public int soloWaves { get; set; }
    /// <summary>
    /// 
    /// </summary>
    public int soloRegain { get; set; }
}

public class BanPlayers
{
    /// <summary>
    /// 
    /// </summary>
    public long id { get; set; }
    /// <summary>
    /// 你不受欢迎
    /// </summary>
    public string reason { get; set; }
}

public class Ban
{
    /// <summary>
    /// 你的名字不受欢迎, 请换一个
    /// </summary>
    public string kickByName { get; set; }
    /// <summary>
    /// 
    /// </summary>
    public List<string> forbiddenName { get; set; }
    /// <summary>
    /// 
    /// </summary>
    public List<BanPlayers> banPlayers { get; set; }
}

public class CVars
{
    /// <summary>
    /// 
    /// </summary>
    public List<string> defaultSets { get; set; }
}

public class JsonRoot
{
    /// <summary>
    /// 
    /// </summary>
    public RconSet rcon { get; set; }
    /// <summary>
    /// 
    /// </summary>
    public Message message { get; set; }
    /// <summary>
    /// 
    /// </summary>
    public Setting setting { get; set; }
    /// <summary>
    /// 
    /// </summary>
    public Difficult difficult { get; set; }
    /// <summary>
    /// 
    /// </summary>
    public SoloBot soloBot { get; set; }
    /// <summary>
    /// 
    /// </summary>
    public Ban ban { get; set; }
    /// <summary>
    /// 
    /// </summary>
    public CVars CVars { get; set; }
}
