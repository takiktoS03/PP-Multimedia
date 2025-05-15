using Aplikacja_desktopowa.Service;
using System;
using System.Linq;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Aplikacja_desktopowa.View
{
    public partial class RegisterForm : Form
    {
        private TextBox textBoxEmail;
        private TextBox textBoxPassword;
        private TextBox textBoxName;
        private Button buttonRegister;
        private Button buttonBack;
        private TextBox textBoxRegisterInfo;
        private Label labelEmail;
        private Label labelPassword;
        private Label labelName;
        private readonly UserService userService = new UserService();

        public RegisterForm()
        {
            InitializeComponent();
            this.Text = "Rejestracja";
            this.StartPosition = FormStartPosition.CenterScreen;
            this.FormClosed += RegisterForm_FormClosed;
        }

        private void RegisterForm_FormClosed(object sender, FormClosedEventArgs e)
        {
            Application.Exit();
        }

        private async void buttonRegister_Click(object sender, EventArgs e)
        {
            string email = textBoxEmail.Text.Trim();
            string password = textBoxPassword.Text.Trim();
            string name = textBoxName.Text.Trim();

            if (string.IsNullOrEmpty(email) || string.IsNullOrEmpty(password) || string.IsNullOrEmpty(name))
            {
                textBoxRegisterInfo.Text = "Wszystkie pola s¹ wymagane.";
                return;
            }

            try
            {

                bool isEmailValid = await CheckEmail(email);
                if (!isEmailValid) return;

                bool isUsernameValid = await CheckName(name);
                if (!isUsernameValid) return;

                bool isPasswordValid = CheckPassword(password);
                if (!isPasswordValid) return;

                textBoxRegisterInfo.Text = "Rejestracja zakoñczona sukcesem.";
            }
            catch (Exception ex)
            {
                textBoxRegisterInfo.Text = $"B³¹d: {ex.Message}";
            }
        }

        private async Task<bool> CheckEmail(string email)
        {
            string emailPattern = @"^[^@\s]+@[^@\s]+\.[^@\s]+$";
            if (!Regex.IsMatch(email, emailPattern))
            {
                textBoxRegisterInfo.Text = "Niepoprawny format adresu e-mail.";
                return false;
            }

            var existingUser = await userService.GetUserByEmailAsync(email);

            if (existingUser != null)
            {
                textBoxRegisterInfo.Text = "E-mail jest ju¿ zarejestrowany.";
                return false; 
            }
            else
            {
                return true; 
            }
        }

        private bool CheckPassword(string password)
        {
            if (password.Length < 8)
            {
                textBoxRegisterInfo.Text = "Has³o musi mieæ co najmniej 8 znaków.";
                return false;
            }

            if (!password.Any(char.IsUpper))
            {
                textBoxRegisterInfo.Text = "Has³o musi zawieraæ co najmniej jedn¹ wielk¹ literê.";
                return false;
            }

            return true;
        }

        private async Task<bool> CheckName(string username)
        {
            var existingUser = await userService.GetUserByUsernameAsync(username);

            if (existingUser != null)
            {
                Console.WriteLine("Nazwa u¿ytkownika jest ju¿ zajêta.");
                return false;
            }
            else
            {
                return true;
            }
        }

        private void buttonBack_Click(object sender, EventArgs e)
        {
            var entryForm = new EntryForm();
            entryForm.Show();
            this.Hide();
        }

        private void InitializeComponent()
        {
            this.textBoxEmail = new System.Windows.Forms.TextBox();
            this.textBoxPassword = new System.Windows.Forms.TextBox();
            this.textBoxName = new System.Windows.Forms.TextBox();
            this.buttonRegister = new System.Windows.Forms.Button();
            this.buttonBack = new System.Windows.Forms.Button();
            this.textBoxRegisterInfo = new System.Windows.Forms.TextBox();
            this.labelEmail = new System.Windows.Forms.Label();
            this.labelPassword = new System.Windows.Forms.Label();
            this.labelName = new System.Windows.Forms.Label();
            this.SuspendLayout();
            // 
            // textBoxEmail
            // 
            this.textBoxEmail.Location = new System.Drawing.Point(100, 30);
            this.textBoxEmail.Name = "textBoxEmail";
            this.textBoxEmail.Size = new System.Drawing.Size(200, 20);
            this.textBoxEmail.TabIndex = 0;
            // 
            // textBoxPassword
            // 
            this.textBoxPassword.Location = new System.Drawing.Point(100, 70);
            this.textBoxPassword.Name = "textBoxPassword";
            this.textBoxPassword.Size = new System.Drawing.Size(200, 20);
            this.textBoxPassword.TabIndex = 1;
            this.textBoxPassword.UseSystemPasswordChar = true;
            // 
            // textBoxName
            // 
            this.textBoxName.Location = new System.Drawing.Point(100, 110);
            this.textBoxName.Name = "textBoxName";
            this.textBoxName.Size = new System.Drawing.Size(200, 20);
            this.textBoxName.TabIndex = 2;
            // 
            // buttonRegister
            // 
            this.buttonRegister.Location = new System.Drawing.Point(100, 150);
            this.buttonRegister.Name = "buttonRegister";
            this.buttonRegister.Size = new System.Drawing.Size(75, 23);
            this.buttonRegister.TabIndex = 3;
            this.buttonRegister.Text = "Register";
            this.buttonRegister.UseVisualStyleBackColor = true;
            this.buttonRegister.Click += new System.EventHandler(this.buttonRegister_Click);
            // 
            // buttonBack
            // 
            this.buttonBack.Location = new System.Drawing.Point(200, 150);
            this.buttonBack.Name = "buttonBack";
            this.buttonBack.Size = new System.Drawing.Size(75, 23);
            this.buttonBack.TabIndex = 8;
            this.buttonBack.Text = "Powrót";
            this.buttonBack.UseVisualStyleBackColor = true;
            this.buttonBack.Click += new System.EventHandler(this.buttonBack_Click);
            // 
            // textBoxRegisterInfo
            // 
            this.textBoxRegisterInfo.Location = new System.Drawing.Point(100, 190);
            this.textBoxRegisterInfo.Name = "textBoxRegisterInfo";
            this.textBoxRegisterInfo.ReadOnly = true;
            this.textBoxRegisterInfo.Size = new System.Drawing.Size(200, 20);
            this.textBoxRegisterInfo.TabIndex = 4;
            // 
            // labelEmail
            // 
            this.labelEmail.AutoSize = true;
            this.labelEmail.Location = new System.Drawing.Point(30, 33);
            this.labelEmail.Name = "labelEmail";
            this.labelEmail.Size = new System.Drawing.Size(35, 13);
            this.labelEmail.TabIndex = 5;
            this.labelEmail.Text = "Email:";
            // 
            // labelPassword
            // 
            this.labelPassword.AutoSize = true;
            this.labelPassword.Location = new System.Drawing.Point(30, 73);
            this.labelPassword.Name = "labelPassword";
            this.labelPassword.Size = new System.Drawing.Size(36, 13);
            this.labelPassword.TabIndex = 6;
            this.labelPassword.Text = "Has³o:";
            // 
            // labelName
            // 
            this.labelName.AutoSize = true;
            this.labelName.Location = new System.Drawing.Point(30, 113);
            this.labelName.Name = "labelName";
            this.labelName.Size = new System.Drawing.Size(29, 13);
            this.labelName.TabIndex = 7;
            this.labelName.Text = "Imiê:";
            // 
            // RegisterForm
            // 
            this.ClientSize = new System.Drawing.Size(400, 250);
            this.Controls.Add(this.labelName);
            this.Controls.Add(this.labelPassword);
            this.Controls.Add(this.labelEmail);
            this.Controls.Add(this.textBoxRegisterInfo);
            this.Controls.Add(this.buttonRegister);
            this.Controls.Add(this.buttonBack);
            this.Controls.Add(this.textBoxName);
            this.Controls.Add(this.textBoxPassword);
            this.Controls.Add(this.textBoxEmail);
            this.Name = "RegisterForm";
            this.ResumeLayout(false);
            this.PerformLayout();
        }

        
    }
}
