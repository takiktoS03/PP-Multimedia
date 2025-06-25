using Aplikacja_desktopowa.Service;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Aplikacja_desktopowa.View
{
    public partial class LoginForm : Form
    {
        private TextBox textBoxEmail;
        private TextBox textBoxPassword;
        private Button buttonLogin;
        private Button buttonBack;
        private TextBox textBoxLogInfo;
        private Label labelEmail;
        private Label labelPassword;
        private readonly UserService userService = new UserService();

        public LoginForm()
        {
            InitializeComponent();
            this.Text = "Logowanie";
            this.StartPosition = FormStartPosition.CenterScreen;
            this.Load += LoginForm_Load;
            this.FormClosed += LoginForm_FormClosed;
        }

        private void LoginForm_Load(object sender, EventArgs e)
        {
            // nic na razie
        }

        private void LoginForm_FormClosed(object sender, FormClosedEventArgs e)
        {
            Application.Exit();
        }



        private async void buttonLogin_Click(object sender, EventArgs e)
        {
            string email = textBoxEmail.Text.Trim();
            string password = textBoxPassword.Text.Trim();

            var user = await userService.GetUserByEmailAsync(email);
            if (user == null)
            {
                textBoxLogInfo.Text = "Nie znaleziono użytkownika.";
                return;
            }

            if (user.PasswordHash != password)
            {
                textBoxLogInfo.Text = "Błędne hasło.";
                return;
            }

            textBoxLogInfo.Text = $"Zalogowano jako: {user.Name}";

            UserForm userForm = new UserForm(user.Id);
            userForm.Show();
            this.Hide();
        }

        private void buttonBack_Click(object sender, EventArgs e)
        {
            var entryForm = new EntryForm();
            entryForm.Show();
            this.Hide();
        }

        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(LoginForm));
            this.textBoxEmail = new System.Windows.Forms.TextBox();
            this.textBoxPassword = new System.Windows.Forms.TextBox();
            this.buttonLogin = new System.Windows.Forms.Button();
            this.buttonBack = new System.Windows.Forms.Button();
            this.textBoxLogInfo = new System.Windows.Forms.TextBox();
            this.labelEmail = new System.Windows.Forms.Label();
            this.labelPassword = new System.Windows.Forms.Label();
            this.SuspendLayout();
            // 
            // textBoxEmail
            // 
            this.textBoxEmail.Font = new System.Drawing.Font("Microsoft Sans Serif", 21.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.textBoxEmail.Location = new System.Drawing.Point(547, 175);
            this.textBoxEmail.Name = "textBoxEmail";
            this.textBoxEmail.Size = new System.Drawing.Size(383, 40);
            this.textBoxEmail.TabIndex = 0;
            this.textBoxEmail.TextChanged += new System.EventHandler(this.textBoxEmail_TextChanged);
            // 
            // textBoxPassword
            // 
            this.textBoxPassword.Font = new System.Drawing.Font("Microsoft Sans Serif", 21.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.textBoxPassword.Location = new System.Drawing.Point(547, 260);
            this.textBoxPassword.Name = "textBoxPassword";
            this.textBoxPassword.Size = new System.Drawing.Size(383, 40);
            this.textBoxPassword.TabIndex = 1;
            this.textBoxPassword.UseSystemPasswordChar = true;
            // 
            // buttonLogin
            // 
            this.buttonLogin.BackColor = System.Drawing.Color.MediumPurple;
            this.buttonLogin.Font = new System.Drawing.Font("Microsoft Sans Serif", 21.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.buttonLogin.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.buttonLogin.Location = new System.Drawing.Point(331, 371);
            this.buttonLogin.Name = "buttonLogin";
            this.buttonLogin.Size = new System.Drawing.Size(222, 66);
            this.buttonLogin.TabIndex = 2;
            this.buttonLogin.Text = "Log";
            this.buttonLogin.UseVisualStyleBackColor = false;
            this.buttonLogin.Click += new System.EventHandler(this.buttonLogin_Click);
            // 
            // buttonBack
            // 
            this.buttonBack.BackColor = System.Drawing.Color.MediumPurple;
            this.buttonBack.Font = new System.Drawing.Font("Microsoft Sans Serif", 21.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.buttonBack.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.buttonBack.Location = new System.Drawing.Point(708, 371);
            this.buttonBack.Name = "buttonBack";
            this.buttonBack.Size = new System.Drawing.Size(222, 66);
            this.buttonBack.TabIndex = 6;
            this.buttonBack.Text = "Powrót";
            this.buttonBack.UseVisualStyleBackColor = false;
            this.buttonBack.Click += new System.EventHandler(this.buttonBack_Click);
            // 
            // textBoxLogInfo
            // 
            this.textBoxLogInfo.Location = new System.Drawing.Point(547, 322);
            this.textBoxLogInfo.Name = "textBoxLogInfo";
            this.textBoxLogInfo.ReadOnly = true;
            this.textBoxLogInfo.Size = new System.Drawing.Size(383, 20);
            this.textBoxLogInfo.TabIndex = 3;
            // 
            // labelEmail
            // 
            this.labelEmail.AutoSize = true;
            this.labelEmail.BackColor = System.Drawing.Color.MediumPurple;
            this.labelEmail.Font = new System.Drawing.Font("Microsoft Sans Serif", 21.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.labelEmail.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.labelEmail.Location = new System.Drawing.Point(340, 175);
            this.labelEmail.Name = "labelEmail";
            this.labelEmail.Size = new System.Drawing.Size(97, 33);
            this.labelEmail.TabIndex = 4;
            this.labelEmail.Text = "Email:";
            this.labelEmail.Click += new System.EventHandler(this.labelEmail_Click);
            // 
            // labelPassword
            // 
            this.labelPassword.AutoSize = true;
            this.labelPassword.BackColor = System.Drawing.Color.MediumPurple;
            this.labelPassword.Font = new System.Drawing.Font("Microsoft Sans Serif", 21.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.labelPassword.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.labelPassword.Location = new System.Drawing.Point(339, 260);
            this.labelPassword.Name = "labelPassword";
            this.labelPassword.Size = new System.Drawing.Size(98, 33);
            this.labelPassword.TabIndex = 5;
            this.labelPassword.Text = "Hasło:";
            // 
            // LoginForm
            // 
            this.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("$this.BackgroundImage")));
            this.ClientSize = new System.Drawing.Size(1235, 611);
            this.Controls.Add(this.labelPassword);
            this.Controls.Add(this.labelEmail);
            this.Controls.Add(this.textBoxLogInfo);
            this.Controls.Add(this.buttonLogin);
            this.Controls.Add(this.buttonBack);
            this.Controls.Add(this.textBoxEmail);
            this.Controls.Add(this.textBoxPassword);
            this.Name = "LoginForm";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        private void textBoxEmail_TextChanged(object sender, EventArgs e)
        {

        }

        private void labelEmail_Click(object sender, EventArgs e)
        {

        }
    }
}
