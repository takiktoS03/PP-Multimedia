using Aplikacja_desktopowa.Service;
using System;
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
                // TODO Registration logic
                textBoxRegisterInfo.Text = "Rejestracja zakoñczona sukcesem.";
            }
            catch (Exception ex)
            {
                textBoxRegisterInfo.Text = $"B³¹d: {ex.Message}";
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(RegisterForm));
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
            this.textBoxEmail.Font = new System.Drawing.Font("Microsoft Sans Serif", 21.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.textBoxEmail.Location = new System.Drawing.Point(591, 50);
            this.textBoxEmail.Name = "textBoxEmail";
            this.textBoxEmail.Size = new System.Drawing.Size(374, 40);
            this.textBoxEmail.TabIndex = 0;
            // 
            // textBoxPassword
            // 
            this.textBoxPassword.Font = new System.Drawing.Font("Microsoft Sans Serif", 21.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.textBoxPassword.Location = new System.Drawing.Point(591, 142);
            this.textBoxPassword.Name = "textBoxPassword";
            this.textBoxPassword.Size = new System.Drawing.Size(374, 40);
            this.textBoxPassword.TabIndex = 1;
            this.textBoxPassword.UseSystemPasswordChar = true;
            // 
            // textBoxName
            // 
            this.textBoxName.Font = new System.Drawing.Font("Microsoft Sans Serif", 21.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.textBoxName.Location = new System.Drawing.Point(591, 246);
            this.textBoxName.Name = "textBoxName";
            this.textBoxName.Size = new System.Drawing.Size(374, 40);
            this.textBoxName.TabIndex = 2;
            // 
            // buttonRegister
            // 
            this.buttonRegister.BackColor = System.Drawing.Color.MediumPurple;
            this.buttonRegister.Font = new System.Drawing.Font("Microsoft Sans Serif", 21.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.buttonRegister.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.buttonRegister.Location = new System.Drawing.Point(351, 411);
            this.buttonRegister.Name = "buttonRegister";
            this.buttonRegister.Size = new System.Drawing.Size(226, 64);
            this.buttonRegister.TabIndex = 3;
            this.buttonRegister.Text = "Register";
            this.buttonRegister.UseVisualStyleBackColor = false;
            this.buttonRegister.Click += new System.EventHandler(this.buttonRegister_Click);
            // 
            // buttonBack
            // 
            this.buttonBack.BackColor = System.Drawing.Color.MediumPurple;
            this.buttonBack.Font = new System.Drawing.Font("Microsoft Sans Serif", 21.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.buttonBack.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.buttonBack.Location = new System.Drawing.Point(739, 411);
            this.buttonBack.Name = "buttonBack";
            this.buttonBack.Size = new System.Drawing.Size(226, 64);
            this.buttonBack.TabIndex = 8;
            this.buttonBack.Text = "Powrót";
            this.buttonBack.UseVisualStyleBackColor = false;
            this.buttonBack.Click += new System.EventHandler(this.buttonBack_Click);
            // 
            // textBoxRegisterInfo
            // 
            this.textBoxRegisterInfo.Location = new System.Drawing.Point(659, 348);
            this.textBoxRegisterInfo.Name = "textBoxRegisterInfo";
            this.textBoxRegisterInfo.ReadOnly = true;
            this.textBoxRegisterInfo.Size = new System.Drawing.Size(306, 20);
            this.textBoxRegisterInfo.TabIndex = 4;
            // 
            // labelEmail
            // 
            this.labelEmail.AutoSize = true;
            this.labelEmail.BackColor = System.Drawing.Color.MediumPurple;
            this.labelEmail.Font = new System.Drawing.Font("Microsoft Sans Serif", 21.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.labelEmail.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.labelEmail.Location = new System.Drawing.Point(344, 50);
            this.labelEmail.Name = "labelEmail";
            this.labelEmail.Size = new System.Drawing.Size(97, 33);
            this.labelEmail.TabIndex = 5;
            this.labelEmail.Text = "Email:";
            this.labelEmail.Click += new System.EventHandler(this.labelEmail_Click);
            // 
            // labelPassword
            // 
            this.labelPassword.AutoSize = true;
            this.labelPassword.BackColor = System.Drawing.Color.MediumPurple;
            this.labelPassword.Font = new System.Drawing.Font("Microsoft Sans Serif", 21.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.labelPassword.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.labelPassword.Location = new System.Drawing.Point(344, 142);
            this.labelPassword.Name = "labelPassword";
            this.labelPassword.Size = new System.Drawing.Size(98, 33);
            this.labelPassword.TabIndex = 6;
            this.labelPassword.Text = "Has³o:";
            this.labelPassword.Click += new System.EventHandler(this.labelPassword_Click);
            // 
            // labelName
            // 
            this.labelName.AutoSize = true;
            this.labelName.BackColor = System.Drawing.Color.MediumPurple;
            this.labelName.Font = new System.Drawing.Font("Microsoft Sans Serif", 21.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.labelName.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.labelName.Location = new System.Drawing.Point(344, 246);
            this.labelName.Name = "labelName";
            this.labelName.Size = new System.Drawing.Size(79, 33);
            this.labelName.TabIndex = 7;
            this.labelName.Text = "Imiê:";
            // 
            // RegisterForm
            // 
            this.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("$this.BackgroundImage")));
            this.ClientSize = new System.Drawing.Size(1235, 605);
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

        private void labelEmail_Click(object sender, EventArgs e)
        {

        }

        private void labelPassword_Click(object sender, EventArgs e)
        {

        }
    }
}
