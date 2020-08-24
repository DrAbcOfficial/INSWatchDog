using System;
using System.IO;
using System.Reflection;

namespace SandstormGuard
{
    public class Logger
    {
        #region Instance
        private static object logLock;

        private static Logger _instance;

        private static string logFileName;
        private Logger() { }

        /// <summary>
        /// Logger instance
        /// </summary>
        public static Logger Instance
        {
            get
            {
                if (_instance == null)
                {
                    _instance = new Logger();
                    logLock = new object();
                    logFileName = DateTime.Now.ToString("yyyy-MM-dd") + ".log";
                }
                return _instance;
            }
        }
        #endregion

        /// <summary>
        /// Write log to log file
        /// </summary>
        /// <param name="logContent">Log content</param>
        /// <param name="logType">Log type</param>
        public void WriteLog(string logContent, LogType logType = LogType.Information, string fileName = null)
        {
            try
            {
                string basePath = Directory.GetCurrentDirectory();
                if (!Directory.Exists(basePath + "/Log"))
                {
                    Directory.CreateDirectory(basePath + "/Log");
                }
                if (!Directory.Exists(basePath + "/Log/"))
                {
                    Directory.CreateDirectory(basePath + "/Log/");
                }

                string[] logText = new string[] { DateTime.Now.ToString("hh:mm:ss") + ": " + logType.ToString() + ": " + logContent };
                if (!string.IsNullOrEmpty(fileName))
                {
                    fileName = fileName + "_" + logFileName;
                }
                else
                {
                    fileName = logFileName;
                }

                lock (logLock)
                {
                    File.AppendAllLines(basePath + "/Log/" + fileName, logText);
                }
            }
            catch (Exception) { }
        }

        /// <summary>
        /// Write exception to log file
        /// </summary>
        /// <param name="exception">Exception</param>
        public void WriteException(Exception exception, string specialText = null)
        {
            if (exception != null)
            {
                Type exceptionType = exception.GetType();
                string text = string.Empty;
                if (!string.IsNullOrEmpty(specialText))
                {
                    text = text + specialText + Environment.NewLine;
                }
                text = "Exception: " + exceptionType.Name + Environment.NewLine;
                text += "               " + "Message: " + exception.Message + Environment.NewLine;
                text += "               " + "Source: " + exception.Source + Environment.NewLine;
                text += "               " + "StackTrace: " + exception.StackTrace + Environment.NewLine;
                text += "               " + "TargetSite: " + exception.TargetSite.Name + Environment.NewLine;
                WriteLog(text, LogType.Error);
            }
        }
    }
}