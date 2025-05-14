using System;
using System.Windows.Forms;

namespace Aplikacja_desktopowa.View
{
    public partial class EntryForm : Form
    {
        private Button buttonLogin;
        private Button buttonRegister;

        public EntryForm()
        {
            InitializeComponent();
            this.FormClosed += EntryForm_FormClosed;
        }

        private void buttonLogin_Click(object sender, EventArgs e)
        {
            LoginForm loginForm = new LoginForm();
            loginForm.Show();
            this.Hide();
        }

        private void buttonRegister_Click(object sender, EventArgs e)
        {
            RegisterForm registerForm = new RegisterForm();
            registerForm.Show();
            this.Hide();
        }

        private void EntryForm_FormClosed(object sender, FormClosedEventArgs e)
        {
            Application.Exit();
        }


        private void InitializeComponent()
        {
            this.buttonLogin = new System.Windows.Forms.Button();
            this.buttonRegister = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // buttonLogin
            // 
            this.buttonLogin.Location = new System.Drawing.Point(100, 50);
            this.buttonLogin.Name = "buttonLogin";
            this.buttonLogin.Size = new System.Drawing.Size(100, 30);
            this.buttonLogin.TabIndex = 0;
            this.buttonLogin.Text = "Login";
            this.buttonLogin.UseVisualStyleBackColor = true;
            this.buttonLogin.Click += new System.EventHandler(this.buttonLogin_Click);
            // 
            // buttonRegister
            // 
            this.buttonRegister.Location = new System.Drawing.Point(100, 100);
            this.buttonRegister.Name = "buttonRegister";
            this.buttonRegister.Size = new System.Drawing.Size(100, 30);
            this.buttonRegister.TabIndex = 1;
            this.buttonRegister.Text = "Register";
            this.buttonRegister.UseVisualStyleBackColor = true;
            this.buttonRegister.Click += new System.EventHandler(this.buttonRegister_Click);
            // 
            // EntryForm
            // 
            this.ClientSize = new System.Drawing.Size(300, 200);
            this.Controls.Add(this.buttonLogin);
            this.Controls.Add(this.buttonRegister);
            this.Name = "EntryForm";
            this.Text = "Welcome";
            this.ResumeLayout(false);
        }
    }
}
