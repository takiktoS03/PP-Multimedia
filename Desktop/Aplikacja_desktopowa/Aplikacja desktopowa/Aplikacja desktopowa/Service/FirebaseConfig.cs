using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Aplikacja_desktopowa.Service
{
    public static class FirebaseConfig
    {
        public static void Init()
        {
            string path = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "image-management-cbaee-firebase-adminsdk-fbsvc-5499d8a881.json");
            Environment.SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", path);


        }
    }
}
