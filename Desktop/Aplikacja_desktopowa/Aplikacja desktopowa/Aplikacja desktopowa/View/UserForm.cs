using Aplikacja_desktopowa.Service;
using System;
using System.Windows.Forms;

namespace Aplikacja_desktopowa.View
{
    public class UserForm : Form
    {
        private readonly string _userId;

        private Button buttonAddAlbum;
        private Button buttonAlbums;
        private Button buttonLogout;
        private Button buttonPhotos;

        public UserForm(string userId)
        {
            _userId = userId;

            buttonPhotos = new Button
            {
                Text = "Moje zdjêcia",
                Location = new System.Drawing.Point(50, 10),
                Size = new System.Drawing.Size(150, 30)
            };
            buttonPhotos.Click += ButtonPhotos_Click;

            buttonAddAlbum = new Button
            {
                Text = "Dodaj album",
                Location = new System.Drawing.Point(50, 50),
                Size = new System.Drawing.Size(150, 30)
            };
            buttonAddAlbum.Click += ButtonAddAlbum_Click;

            buttonAlbums = new Button
            {
                Text = "PrzejdŸ do albumów",
                Location = new System.Drawing.Point(50, 100),
                Size = new System.Drawing.Size(150, 30)
            };
            buttonAlbums.Click += ButtonAlbums_Click;

            buttonLogout = new Button
            {
                Text = "Wyloguj",
                Location = new System.Drawing.Point(50, 150),
                Size = new System.Drawing.Size(150, 30)
            };
            buttonLogout.Click += ButtonLogout_Click;

            Controls.Add(buttonPhotos);
            Controls.Add(buttonAddAlbum);
            Controls.Add(buttonAlbums);
            Controls.Add(buttonLogout);

            Text = "Strona u¿ytkownika";
            ClientSize = new System.Drawing.Size(250, 180);

            this.FormClosed += UserForm_FormClosed;
        }

        private void ButtonPhotos_Click(object sender, EventArgs e)
        {
            var form = new UserPhotosForm(_userId);
            form.Show();
        }

        private async void ButtonAddAlbum_Click(object sender, EventArgs e)
        {
            using (var addAlbumForm = new AddAlbumForm())
            {
                if (addAlbumForm.ShowDialog() == DialogResult.OK && !string.IsNullOrWhiteSpace(addAlbumForm.AlbumName))
                {
                    var albumService = new AlbumService();
                    await albumService.AddAlbumAsync(addAlbumForm.AlbumName, addAlbumForm.AlbumDescription, _userId);
                    MessageBox.Show("Album zosta³ dodany.");
                }
            }
        }

        private void ButtonAlbums_Click(object sender, EventArgs e)
        {
            AlbumsForm albumsForm = new AlbumsForm(_userId);
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
