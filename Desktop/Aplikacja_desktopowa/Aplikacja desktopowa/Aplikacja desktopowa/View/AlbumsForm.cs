using Aplikacja_desktopowa.Model;
using Aplikacja_desktopowa.Service;
using Aplikacja_desktopowa.View.Utils;
using System;
using System.Collections.Generic;
using System.IO;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Aplikacja_desktopowa.View
{
    public class AlbumsForm : Form
    {
        private readonly AlbumService _albumService = new AlbumService();
        private readonly PhotoService _photoService = new PhotoService();
        private readonly string _userId;

        public AlbumsForm(string userId)
        {
            _userId = userId;
            Text = "Albumy";
            Load += AlbumsForm_Load;
            AutoScroll = true;
        }

        private async void AlbumsForm_Load(object sender, EventArgs e)
        {
            var albums = await _albumService.GetAlbumsByUserIdAsync(_userId);
            int y = 10;

            foreach (var (id, album) in albums)
            {
                var btnOpen = new Button
                {
                    Text = album.Name,
                    Tag = id,
                    Location = new System.Drawing.Point(10, y),
                    Width = 150
                };
                btnOpen.Click += AlbumButton_Click;
                Controls.Add(btnOpen);

                var btnAddPhotos = new Button
                {
                    Text = "Dodaj zdjêcia",
                    Tag = id,
                    Location = new System.Drawing.Point(170, y),
                    Width = 120
                };
                btnAddPhotos.Click += AddPhotosButton_Click;
                Controls.Add(btnAddPhotos);

                y += 40;
            }
        }

        private void AlbumButton_Click(object sender, EventArgs e)
        {
            var albumId = (string)((Button)sender).Tag;
            var albumPhotosForm = new AlbumPhotosForm(albumId);
            albumPhotosForm.Show();
        }

        private async void AddPhotosButton_Click(object sender, EventArgs e)
        {
            var albumId = (string)((Button)sender).Tag;
            using (var addPhotosForm = new AddPhotosForm())
            {
                if (addPhotosForm.ShowDialog() == DialogResult.OK)
                {
                    for (int i = 0; i < addPhotosForm.PhotoMetadatas.Count; i++)
                    {
                        var photo = addPhotosForm.PhotoMetadatas[i];
                        var filePath = addPhotosForm.LocalFilePaths[i];

                        string fileNameInStorage = Guid.NewGuid().ToString() + Path.GetExtension(filePath);
                        string fileUrl = await _photoService.UploadPhotoToStorageAsync(filePath, fileNameInStorage);

                        photo.FilePath = fileUrl;
                        photo.Id = null;

                        string photoId = await _photoService.AddPhotoAndGetIdAsync(photo, filePath);
                        await _albumService.AddPhotoToAlbumAsync(albumId, photoId);
                    }
                    MessageBox.Show("Photos have been added to the album.");
                }
            }
        }
    }
}
