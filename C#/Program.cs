using Newtonsoft.Json;
using Rcon.Client;
using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace SandstormGuard
{
    class CPlayer : IComparable
    {
        public int ID;
        public string Name;
        public long NetID;
        public IPAddress IP;
        public long Score;

        public int CompareTo(object obj)
        {
            CPlayer temp = obj as CPlayer;
            return this.Score.CompareTo(temp.Score);
        }
    }

    class CMaps
    {
        List<string> Maps = new List<string>();
        public int Count
        {
            get { return Maps.Count; }
        }
        public void Add(string str)
        {
            Maps.Add(str);
        }
        public void Intitial(string str)
        {
            string[] buffer = str.Split('\n');
            foreach (string s in buffer)
            {
                if (!string.IsNullOrWhiteSpace(s))
                {
                    Maps.Add(s.Trim('\t').Trim(' '));
                }
            }
        }
    }


    class Program
    {
        static string szProgramePath = Directory.GetCurrentDirectory() + "/";
        static int msgNow = 0;
        static List<CPlayer> playerList = new List<CPlayer>();
        static CMaps maps = new CMaps();
        static RconClient rcon;
        static int oldPlayers = 0;
        static JsonRoot json;

        static void Msg()
        {
            if (msgNow + 1 > json.message.msgList.Count - 1)
                msgNow = 0;
            else
                msgNow++;
            string str = json.message.msgList[msgNow];
            Say($"[{json.message.msgHeader}] " + str.
                Replace("{player}", playerList.Count.ToString()).
                Replace("{time}", DateTime.Now.ToString(json.message.format.time)).
                Replace("{maps}", maps.Count.ToString()).
                Replace("{bestplayer}", GetBestPlayer()));
        }

        static string GetBestPlayer()
        {
            if (playerList.Count == 0)
                return json.message.format.noBest;
            else
                return playerList[0].Score == 0 ? json.message.format.zeroBest : playerList[0].Name + string.Format(json.message.format.lastBest, playerList[0].Score.ToString()) + (playerList[0].ID == 0 ? json.message.format.botBest : string.Empty);
        }

        static void Log(string Str, ConsoleColor cc = ConsoleColor.Gray)
        {
            Console.ForegroundColor = cc;
            Console.WriteLine($"[{DateTime.Now.ToString()}] | {Str}");
            Console.ResetColor();
            Logger.Instance.WriteLog(Str);
        }

        static void Log(Exception e)
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine($"[{DateTime.Now.ToString()}] | {e.Message}");
            Console.ResetColor();
            Logger.Instance.WriteException(e);
        }

        static bool HasPlayer(List<CPlayer> a, CPlayer b)
        {
            foreach (CPlayer p in a)
            {
                if (p.Name == b.Name)
                    return true;
            }
            return false;
        }

        static void Say(string str)
        {
            Command($"say {str}");
            Log($"已发送消息[{str}]");
        }

        static string Command(string str)
        {
            string formattedText = Encoding.UTF8.GetString(Encoding.UTF8.GetBytes(str));
            return rcon.ExecuteCommand(RconCommand.ServerCommand(formattedText)).ResponseText;
        }

        static long StrToLong(in string str)
        {
            try
            {
                if (string.IsNullOrWhiteSpace(str))
                    return 0;
                return Convert.ToInt64(str);
            }
            catch (Exception)
            {
                return 0;
            }
        }

        static int StrToInt(in string str)
        {
            try
            {
                if (string.IsNullOrWhiteSpace(str))
                    return 0;
                return Convert.ToInt32(str);
            }
            catch (Exception)
            {
                return 0;
            }
        }

        static float StrToFloat(in string str)
        {
            try
            {
                if (string.IsNullOrWhiteSpace(str))
                    return 0;
                return Convert.ToSingle(str);
            }
            catch (Exception)
            {
                return 0.0f;
            }
        }

        static string TrimCVar(in string szCVar)
        {
            if (szCVar.Contains("="))
                return szCVar.Substring(szCVar.IndexOf('=') + 1).Trim().Trim('\"');
            else
                return "";
        }

        static void SoloTwick(int nowPlayer)
        {
            int intNum = json.soloBot.maxBots / json.soloBot.fullSetPlayer * nowPlayer;
            if (nowPlayer > json.soloBot.fullSetPlayer)
                intNum = json.soloBot.maxBots;
            else
            {
                intNum = Math.Max(json.soloBot.minBots, intNum);
                intNum = Math.Min(json.soloBot.maxBots, intNum);
            }
            Log(Command($"gamemodeproperty SoloEnemies {intNum}"));
        }

        static void DifficultTwick(int nowPlayer)
        {
            float flNum = json.difficult.maxDifficult / json.difficult.maxToScale * nowPlayer;
            if (nowPlayer > json.difficult.maxToScale)
                flNum = json.difficult.maxDifficult;
            else
            {
                flNum = Math.Max(json.difficult.minDifficult, flNum);
                flNum = Math.Min(json.difficult.maxDifficult, flNum);
            }
            Log(Command($"gamemodeproperty AIDifficulty {flNum}"));
            Log(Command($"gamemodeproperty ServerHostname {string.Format(json.setting.serverName, Math.Round(flNum, 1))}"));
            if (flNum != json.difficult.minDifficult)
                Say(string.Format(json.message.format.aiDifficult, Math.Round(flNum * 100, 3)));
        }

        static void ReadConfig()
        {
            using (StreamReader sr = new StreamReader(szProgramePath + "config.json"))
            {
                json = JsonConvert.DeserializeObject<JsonRoot>(sr.ReadToEnd());
            }
        }

        static void Kick(CPlayer player, string reason)
        {
            Log(Command($"kick {player.NetID} {reason}"), ConsoleColor.Yellow);
            Log($"已踢出玩家: {player.Name}[{player.NetID}]\t|\t{reason}\t|\t{player.IP}", ConsoleColor.Red);
        }

        static void SetSoloCVar()
        {
            Log("开始执行初始化", ConsoleColor.Cyan);
            Log(Command($"gamemodeproperty AIDifficulty {json.difficult.minDifficult}"), ConsoleColor.Yellow);
            Log(Command($"gamemodeproperty ServerHostname {string.Format(json.setting.serverName, Math.Round(json.difficult.minDifficult, 1))}"), ConsoleColor.Yellow);
            Log(Command($"gamemodeproperty SoloEnemies {json.soloBot.minBots}"), ConsoleColor.Yellow);
            Log(Command($"gamemodeproperty SoloWaves {json.soloBot.soloWaves}"), ConsoleColor.Yellow);
            Log(Command($"gamemodeproperty SoloReinforcementTime {json.soloBot.soloRegain}"), ConsoleColor.Yellow);
        }

        static void SetCVars()
        {
            foreach(string s in json.CVars.defaultSets)
            {
                Log(Command($"gamemodeproperty {s}"), ConsoleColor.Yellow);
            }
        }

        static void Main(string[] args)
        {
            ReadConfig();
            rcon = new RconClient(json.rcon.ipAddress, json.rcon.rconPort);
        reconnct:
            uint round = 0;
            try
            {
                Log("尝试从本地连接到服务器", ConsoleColor.Gray);
                rcon.ExecuteCommand(RconCommand.Auth(json.rcon.passWd));
                Log("已连接服务器", ConsoleColor.Green);
                maps.Intitial(Command("maps"));
                Say("正在执行服务器初始化配置....");
                Task.Factory.StartNew(SetCVars).ContinueWith(m => 
                    {
                        SetSoloCVar();
                        Say("初始化成功！"); 
                    });
                Say("看门狗已经启动~");
            }
            catch (Exception)
            {
                Log("连接失败...准备重新连接...", ConsoleColor.Red);
                Thread.Sleep(15000);
                goto reconnct;
            }

            try
            {
                while (true)
                {

                    if (round + 1 > uint.MaxValue)
                        round = 0;
                    else
                        round++;

                    string[] tempList = rcon.ExecuteCommand(RconCommand.ServerCommand("listplayers")).ResponseText.Split('\n');
                    if (tempList.Length <= 2)
                        return;

                    List<CPlayer> tempPlayer = new List<CPlayer>();
                    string[] aryT = tempList[2].Split('|');

                    for (int i = 0; i < aryT.Length - (aryT.Length % 5); i += 5)
                    {
                        int id = Convert.ToInt32(aryT[i].Trim(' ').Trim('\t'));
                        if (id != 0)
                        {
                            CPlayer cPlayer = new CPlayer()
                            {
                                ID = id,
                                NetID = StrToLong(aryT[i + 2].Trim(' ').Trim('\t')),
                                Name = aryT[i + 1].Trim(' ').Trim('\t'),
                                IP = IPAddress.Parse(aryT[i + 3].Trim(' ').Trim('\t')),
                                Score = StrToLong(aryT[i + 4].Trim(' ').Trim('\t'))
                            };
                            bool kickFlag = false;
                            foreach (string keyWord in json.ban.forbiddenName)
                            {
                                if (cPlayer.Name.Replace(" ", "").Replace("\n", "").Replace("\t", "").Replace(".", "").ToLower().Contains(keyWord.ToLower()))
                                {
                                    kickFlag = true;
                                    Kick(cPlayer, json.ban.kickByName);
                                    break;
                                }
                            }

                            foreach (BanPlayers banPlayersItem in json.ban.banPlayers)
                            {
                                if (banPlayersItem.id == cPlayer.NetID)
                                {
                                    kickFlag = true;
                                    Kick(cPlayer, banPlayersItem.reason);
                                    break;
                                }
                            }

                            if (!kickFlag)
                                tempPlayer.Add(cPlayer);
                        }
                    }

                    if (tempPlayer.Count > 0)
                    {
                        for (int j = 0; j < playerList.Count; j++)
                        {
                            if (!HasPlayer(tempPlayer, playerList[j]))
                                Say(string.Format(json.message.format.leave, playerList[j].Name, playerList[j].NetID));
                        }

                        int tpNum = playerList.Count;
                        for (int j = 0; j < tempPlayer.Count; j++)
                        {
                            if (!HasPlayer(playerList, tempPlayer[j]))
                            {
                                tpNum++;
                                Say(string.Format(json.message.format.join, tpNum, tempPlayer[j].Name));
                            }
                        }

                        playerList = tempPlayer;
                        playerList.Sort();
                        playerList.Reverse();
                        if (oldPlayers != playerList.Count)
                        {
                            oldPlayers = playerList.Count;
                            SoloTwick(playerList.Count);
                            DifficultTwick(playerList.Count);
                        }

                        if (round % json.message.msgIntev == 0)
                        {
                            Msg();
                            Log($"服务器共有{playerList.Count}名玩家");
                        }
                    }
                    else if (!json.setting.hideEmpty)
                        Log("服务器没挂，但是没有玩家");

                    Thread.Sleep(json.message.waitTime * 1000);
                }
            }
            catch (Exception e)
            {
                Log("服务器未响应", ConsoleColor.Red);
                Log(e);
            }
        }
    }
}
