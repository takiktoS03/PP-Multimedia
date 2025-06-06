using Aplikacja_desktopowa.Model;
using Aplikacja_desktopowa.Service;
using Aplikacja_desktopowa.ViewModel;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Aplikacja_desktopowa.View
{
    public class AlbumPhotosForm : Form
    {
        private readonly AlbumService _albumService = new AlbumService();
        private readonly PhotoService _photoService = new PhotoService();
        private readonly string _albumId;

        public AlbumPhotosForm(string albumId)
        {
            _albumId = albumId;
            Text = "Zdjêcia w albumie";
            Load += AlbumPhotosForm_Load;
            AutoScroll = true;
        }

        private async void AlbumPhotosForm_Load(object sender, EventArgs e)
        {
            var photoIds = await _albumService.GetPhotoIdsForAlbumAsync(_albumId);
            var photos = await _photoService.GetPhotosByIdsAsync(photoIds);

            int y = 10;
            foreach (var photo in photos)
            {
                var pictureBox = new PictureBox
                {
                    Location = new System.Drawing.Point(10, y),
                    Size = new System.Drawing.Size(100, 100),
                    SizeMode = PictureBoxSizeMode.Zoom,
                    Cursor = Cursors.Hand,
                    Tag = photo
                };
                try { pictureBox.Load(photo.FilePath); } catch { }
                pictureBox.Click += Photo_Click;
                Controls.Add(pictureBox);
                y += 110;
            }
        }

        private async void Photo_Click(object sender, EventArgs e)
        {
            var pictureBox = sender as PictureBox;
            var photo = pictureBox.Tag as PhotoMetadata;

            using (var editForm = new PhotoEditForm(photo))
            {
                if (editForm.ShowDialog() == DialogResult.OK)
                {
                    if (editForm.RemoveFromAlbum)
                    {
                        await _albumService.RemovePhotoFromAlbumAsync(_albumId, photo.Id);
                        MessageBox.Show("Zdjêcie usuniête z albumu.");
                        Controls.Clear();
                        AlbumPhotosForm_Load(this, EventArgs.Empty); // odœwie¿ widok
                    }
                    else
                    {
                        await _photoService.UpdatePhotoAsync(editForm.Photo);
                        MessageBox.Show("Zapisano zmiany metadanych.");
                    }
                }
            }
        }
    }
}
