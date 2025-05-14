using System;
using System.Windows.Forms;

namespace Aplikacja_desktopowa.View
{
    public class UserForm : Form
    {
        private Button buttonAlbums;
        private Button buttonLogout;

        public UserForm()
        {
            buttonAlbums = new Button
            {
                Text = "PrzejdŸ do albumów",
                Location = new System.Drawing.Point(50, 50),
                Size = new System.Drawing.Size(150, 30)
            };
            buttonAlbums.Click += ButtonAlbums_Click;

            buttonLogout = new Button
            {
                Text = "Wyloguj",
                Location = new System.Drawing.Point(50, 100),
                Size = new System.Drawing.Size(150, 30)
            };
            buttonLogout.Click += ButtonLogout_Click;

            Controls.Add(buttonAlbums);
            Controls.Add(buttonLogout);

            Text = "Strona u¿ytkownika";
            ClientSize = new System.Drawing.Size(250, 180);

            this.FormClosed += UserForm_FormClosed;
        }

        private void ButtonAlbums_Click(object sender, EventArgs e)
        {
            AlbumsForm albumsForm = new AlbumsForm();
            albumsForm.Show();
        }

        private void ButtonLogout_Click(object sender, EventArgs e)
        {
            var entryForm = new EntryForm();
            entryForm.Show();
            this.Hide();
        }

        private void UserForm_FormClosed(object sender, FormClosedEventArgs e)
        {
            Application.Exit();
        }
    }
}
