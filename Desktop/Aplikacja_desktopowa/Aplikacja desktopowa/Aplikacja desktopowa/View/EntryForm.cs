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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(EntryForm));
            this.buttonLogin = new System.Windows.Forms.Button();
            this.buttonRegister = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // buttonLogin
            // 
            this.buttonLogin.BackColor = System.Drawing.Color.MediumPurple;
            this.buttonLogin.Font = new System.Drawing.Font("Microsoft Sans Serif", 24F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.buttonLogin.ForeColor = System.Drawing.SystemColors.ControlLight;
            this.buttonLogin.Location = new System.Drawing.Point(1017, 316);
            this.buttonLogin.Name = "buttonLogin";
            this.buttonLogin.Size = new System.Drawing.Size(236, 97);
            this.buttonLogin.TabIndex = 0;
            this.buttonLogin.Text = "Login";
            this.buttonLogin.UseVisualStyleBackColor = false;
            this.buttonLogin.Click += new System.EventHandler(this.buttonLogin_Click);
            // 
            // buttonRegister
            // 
            this.buttonRegister.BackColor = System.Drawing.Color.MediumPurple;
            this.buttonRegister.Font = new System.Drawing.Font("Microsoft Sans Serif", 24F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.buttonRegister.ForeColor = System.Drawing.SystemColors.ControlLight;
            this.buttonRegister.Location = new System.Drawing.Point(1017, 519);
            this.buttonRegister.Name = "buttonRegister";
            this.buttonRegister.Size = new System.Drawing.Size(236, 94);
            this.buttonRegister.TabIndex = 1;
            this.buttonRegister.Text = "Register";
            this.buttonRegister.UseVisualStyleBackColor = false;
            this.buttonRegister.Click += new System.EventHandler(this.buttonRegister_Click);
            // 
            // EntryForm
            // 
            this.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(255)))), ((int)(((byte)(192)))), ((int)(((byte)(255)))));
            this.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("$this.BackgroundImage")));
            this.ClientSize = new System.Drawing.Size(1276, 668);
            this.Controls.Add(this.buttonLogin);
            this.Controls.Add(this.buttonRegister);
            this.Name = "EntryForm";
            this.Text = "Welcome";
            this.ResumeLayout(false);

        }
    }
}
