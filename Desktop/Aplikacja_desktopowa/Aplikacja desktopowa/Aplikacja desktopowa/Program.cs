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
        static async Task Main()
        {
            FirestoreCopier copier = new FirestoreCopier();
            await copier.CopyAsync();
            FirebaseConfig.Init();
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            //Application.Run(new Form2());
            Application.Run(new LoginForm());
        }
    }
}
