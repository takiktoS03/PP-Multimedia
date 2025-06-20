using System;
using System.Windows.Forms;
using System.Threading.Tasks;
using Aplikacja_desktopowa.Service;
using Aplikacja_desktopowa.Model;
using System.Collections.Generic;
using Aplikacja_desktopowa.View.Utils;

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
            using (var dialog = new OpenFileDialog())
            {
                dialog.Filter = "Obrazy (*.jpg;*.jpeg;*.png)|*.jpg;*.jpeg;*.png";
                dialog.Multiselect = true;
                if (dialog.ShowDialog() == DialogResult.OK)
                {
                    foreach (var filePath in dialog.FileNames)
                    {
                        // Prosty input box do pobrania danych od u¿ytkownika
                        string title = Prompt.ShowDialog("Tytu³ zdjêcia:", "Dodaj zdjêcie");
                        string description = Prompt.ShowDialog("Opis zdjêcia:", "Dodaj zdjêcie");
                        string location = Prompt.ShowDialog("Lokalizacja:", "Dodaj zdjêcie");
                        string tagsInput = Prompt.ShowDialog("Tagi (oddzielone przecinkami):", "Dodaj zdjêcie");
                        var tags = new List<string>();
                        if (!string.IsNullOrWhiteSpace(tagsInput))
                            tags = new List<string>(tagsInput.Split(new[] { ',' }, StringSplitOptions.RemoveEmptyEntries));

                        // Upload pliku do Storage i pobierz URL
                        string fileNameInStorage = Guid.NewGuid().ToString() + System.IO.Path.GetExtension(filePath);
                        string fileUrl = await _photoService.UploadPhotoToStorageAsync(filePath, fileNameInStorage);

                        // Utwórz PhotoMetadata z nowymi polami
                        var photo = new PhotoMetadata
                        {
                            Title = title,
                            Description = description,
                            Location = location,
                            Tags = tags,
                            FilePath = fileUrl,
                            UploadedAt = DateTime.UtcNow,
                            Id = null // zostanie ustawione w AddPhotoAndGetIdAsync
                        };

                        string photoId = await _photoService.AddPhotoAndGetIdAsync(photo, filePath);

                        // Dodaj powi¹zanie do album_photos
                        await _albumService.AddPhotoToAlbumAsync(albumId, photoId);
                    }
                    MessageBox.Show("Zdjêcia zosta³y dodane do albumu.");
                }
            }
        }
    }
}
