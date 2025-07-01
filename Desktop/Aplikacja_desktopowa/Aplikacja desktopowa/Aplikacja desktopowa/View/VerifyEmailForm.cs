using Aplikacja_desktopowa.Service;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Aplikacja_desktopowa.View
{
    /// <summary>
    /// Formularz umożliwiający użytkownikowi weryfikację adresu e-mail za pomocą kodu.
    /// </summary>
    public partial class VerifyEmailForm : Form
    {
        private readonly string userEmail;
        private readonly UserService userService = new UserService();

        /// <summary>
        /// Inicjalizuje nową instancję formularza VerifyEmailForm dla danego adresu e-mail.
        /// </summary>
        public VerifyEmailForm(string email)
        {
            InitializeComponent();
            userEmail = email;
        }

        /// <summary>
        /// Obsługuje kliknięcie przycisku weryfikacji kodu. 
        /// Sprawdza poprawność kodu wprowadzonego przez użytkownika i weryfikuje e-mail.
        /// </summary>
        private async void button1_Click(object sender, EventArgs e)
        {
            string enteredCode = textBoxCode.Text.Trim();
            var user = await userService.GetUserByEmailAsync(userEmail);

            if (user != null && user.VerificationCode == enteredCode)
            {
                await userService.MarkEmailAsVerified(userEmail);
                labelStatus.Text = "E-mail został potwierdzony.";
            }
            else
            {
                labelStatus.Text = "Nieprawidłowy kod.";
            }
        }
    }
}
