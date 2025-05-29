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
    public partial class VerifyEmailForm : Form
    {
        private readonly string userEmail;
        private readonly UserService userService = new UserService();

        public VerifyEmailForm(string email)
        {
            InitializeComponent();
            userEmail = email;
        }

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
