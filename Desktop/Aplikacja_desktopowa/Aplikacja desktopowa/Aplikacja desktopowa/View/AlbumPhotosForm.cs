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

        private ComboBox comboBoxSortBy;
        private TextBox textBoxTagFilter;
        private Button buttonSort;
        private List<PhotoMetadata> currentPhotos;

        private Button buttonDeleteAlbum;

        public AlbumPhotosForm(string albumId)
        {
            _albumId = albumId;
            Text = "Zdjêcia w albumie";
            Width = 900;
            Height = 700;
            AutoScroll = true;

            buttonDeleteAlbum = new Button
            {
                Text = "Usuñ ten album",
                Left = 10,
                Top = 10,
                Width = 150
            };
            buttonDeleteAlbum.Click += ButtonDeleteAlbum_Click;
            Controls.Add(buttonDeleteAlbum);

            comboBoxSortBy = new ComboBox
            {
                Left = 170,
                Top = 10,
                Width = 120,
                DropDownStyle = ComboBoxStyle.DropDownList
            };
            comboBoxSortBy.Items.AddRange(new string[] { "Nazwa", "Data dodania", "Tag" });
            comboBoxSortBy.SelectedIndex = 0;
            Controls.Add(comboBoxSortBy);

            textBoxTagFilter = new TextBox
            {
                Left = 300,
                Top = 10,
                Width = 100,
                Visible = false
            };
            Controls.Add(textBoxTagFilter);

            buttonSort = new Button
            {
                Text = "Sortuj",
                Left = 410,
                Top = 10,
                Width = 80
            };
            buttonSort.Click += ButtonSort_Click;
            Controls.Add(buttonSort);

            comboBoxSortBy.SelectedIndexChanged += (s, e) =>
            {
                textBoxTagFilter.Visible = comboBoxSortBy.SelectedItem.ToString() == "Tag";
            };

            Load += AlbumPhotosForm_Load;

            this.Resize += (s, e) =>
            {
                if (currentPhotos != null) DisplayPhotos(currentPhotos);
            };
        }

        private async void AlbumPhotosForm_Load(object sender, EventArgs e)
        {
            var photoIds = await _albumService.GetPhotoIdsForAlbumAsync(_albumId);
            currentPhotos = await _photoService.GetPhotosByIdsAsync(photoIds);
            DisplayPhotos(currentPhotos);
        }

        private async void ButtonDeleteAlbum_Click(object sender, EventArgs e)
        {
            var result = MessageBox.Show("Czy na pewno chcesz usun¹æ ten album wraz z powi¹zaniami?", "PotwierdŸ usuniêcie", MessageBoxButtons.YesNo, MessageBoxIcon.Warning);
            if (result == DialogResult.Yes)
            {
                await _albumService.DeleteAlbumAsync(_albumId);
                MessageBox.Show("Album zosta³ usuniêty.");
                this.Close();
            }
        }

        private async void DisplayPhotos(List<PhotoMetadata> photos)
        {
            for (int i = Controls.Count - 1; i >= 0; i--)
            {
                var ctrl = Controls[i];
                if (ctrl is PictureBox)
                    Controls.RemoveAt(i);
            }

            int margin = 10;
            int thumbWidth = 100;
            int thumbHeight = 100;
            int startY = 50;
            int x = margin;
            int y = startY;
            int maxWidth = this.ClientSize.Width;

            foreach (var photo in photos)
            {
                string thumbFileName = photo.Id + "_thumb.jpg";
                string thumbPath = System.IO.Path.Combine(System.IO.Path.GetTempPath(), thumbFileName);

                if (!System.IO.File.Exists(thumbPath))
                {
                    string sanitizedFileName = System.Text.RegularExpressions.Regex.Replace(
                        photo.Id + System.IO.Path.GetExtension(photo.FilePath), @"[<>:""/\\|?*]", "_");
                    string tempOriginalPath = System.IO.Path.Combine(System.IO.Path.GetTempPath(), sanitizedFileName);
                    await _photoService.DownloadPhotoByUrlAsync(photo.FilePath, tempOriginalPath);

                    PhotoService.CreateThumbnail(tempOriginalPath, thumbPath, thumbWidth, thumbHeight);

                    try { System.IO.File.Delete(tempOriginalPath); } catch { }
                }

                if (x + thumbWidth + margin > maxWidth)
                {
                    x = margin;
                    y += thumbHeight + margin;
                }

                var pictureBox = new PictureBox
                {
                    Location = new System.Drawing.Point(x, y),
                    Size = new System.Drawing.Size(thumbWidth, thumbHeight),
                    SizeMode = PictureBoxSizeMode.Zoom,
                    Cursor = Cursors.Hand,
                    Tag = photo
                };
                try { pictureBox.Load(thumbPath); } catch { }
                pictureBox.Click += Photo_Click;
                Controls.Add(pictureBox);

                x += thumbWidth + margin;
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
                        AlbumPhotosForm_Load(this, EventArgs.Empty);
                    }
                    else
                    {
                        await _photoService.UpdatePhotoAsync(editForm.Photo);
                        MessageBox.Show("Zapisano zmiany metadanych.");
                    }
                }
            }
        }

        private void ButtonSort_Click(object sender, EventArgs e)
        {
            if (currentPhotos == null) return;

            var sortBy = comboBoxSortBy.SelectedItem.ToString();
            List<PhotoMetadata> sorted;

            if (sortBy == "Nazwa")
            {
                sorted = new List<PhotoMetadata>(currentPhotos);
                sorted.Sort((a, b) => string.Compare(a.Title, b.Title, StringComparison.CurrentCultureIgnoreCase));
            }
            else if (sortBy == "Data dodania")
            {
                sorted = new List<PhotoMetadata>(currentPhotos);
                sorted.Sort((a, b) => a.UploadedAt.CompareTo(b.UploadedAt));
            }
            else // Tag
            {
                string tag = textBoxTagFilter.Text.Trim();
                sorted = new List<PhotoMetadata>(currentPhotos);
                sorted = sorted.FindAll(p => p.Tags != null && p.Tags.Contains(tag));
            }

            DisplayPhotos(sorted);
        }

        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(AlbumPhotosForm));
            this.SuspendLayout();
            // 
            // AlbumPhotosForm
            // 
            this.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("$this.BackgroundImage")));
            this.ClientSize = new System.Drawing.Size(1232, 611);
            this.Name = "AlbumPhotosForm";
            this.ResumeLayout(false);

        }
    }
}
