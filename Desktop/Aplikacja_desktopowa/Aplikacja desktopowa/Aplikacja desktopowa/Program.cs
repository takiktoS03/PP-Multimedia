using Aplikacja_desktopowa.Service;
using Aplikacja_desktopowa.View;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Aplikacja_desktopowa
{
    internal static class Program
    {
        /// <summary>
        /// Główny punkt wejścia dla aplikacji.
        /// </summary>
        [STAThread]
        static void Main()
        {
            FirestoreCopier copier = new FirestoreCopier();
            copier.CopyAsync().GetAwaiter().GetResult();
            FirebaseConfig.Init();
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            //Application.Run(new Form2());
            //Application.Run(new LoginForm());
            Application.Run(new EntryForm());
        }
    }
}
