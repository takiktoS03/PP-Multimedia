using System;
using System.Windows.Forms;
using System.Threading.Tasks;
using Aplikacja_desktopowa.Service;

namespace Aplikacja_desktopowa.View
{
    public class AlbumsForm : Form
    {
        private readonly AlbumService _albumService = new AlbumService();

        public AlbumsForm()
        {
            Text = "Albumy";
            Load += AlbumsForm_Load;
            AutoScroll = true;
        }

        private async void AlbumsForm_Load(object sender, EventArgs e)
        {
            var albums = await _albumService.GetAllAlbumsAsync();
            int y = 10;
            foreach (var (id, album) in albums)
            {
                var btn = new Button
                {
                    Text = album.Name,
                    Tag = id,
                    Location = new System.Drawing.Point(10, y),
                    Width = 200
                };
                btn.Click += AlbumButton_Click;
                Controls.Add(btn);
                y += 40;
            }
        }

        private void AlbumButton_Click(object sender, EventArgs e)
        {
            var albumId = (string)((Button)sender).Tag;
            var albumPhotosForm = new AlbumPhotosForm(albumId);
            albumPhotosForm.Show();
        }
    }
}
