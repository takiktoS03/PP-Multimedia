using Aplikacja_desktopowa.Model;
using Aplikacja_desktopowa.Service;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text.RegularExpressions;
using System.Windows.Forms;

namespace Aplikacja_desktopowa.View
{
    public partial class UserPhotosForm : Form
    {
        private readonly string _userId;
        private readonly PhotoService _photoService = new PhotoService();
        private List<PhotoMetadata> userPhotos;
        private List<PhotoMetadata> currentPhotos;

        private ComboBox comboBoxSortBy;
        private TextBox textBoxTagFilter;
        private Button buttonSort;
        private Button buttonAddPhoto;

        public UserPhotosForm(string userId)
        {
            _userId = userId;
            Text = "Zdjęcia użytkownika";
            Width = 900;
            Height = 700;
            AutoScroll = true;

            comboBoxSortBy = new ComboBox
            {
                Left = 10,
                Top = 10,
                Width = 120,
                DropDownStyle = ComboBoxStyle.DropDownList
            };
            comboBoxSortBy.Items.AddRange(new string[] { "Nazwa", "Data dodania", "Tag" });
            comboBoxSortBy.SelectedIndex = 0;
            Controls.Add(comboBoxSortBy);

            textBoxTagFilter = new TextBox
            {
                Left = 140,
                Top = 10,
                Width = 100,
                Visible = false
            };
            Controls.Add(textBoxTagFilter);

            buttonSort = new Button
            {
                Text = "Sortuj",
                Left = 250,
                Top = 10,
                Width = 80
            };
            buttonSort.Click += ButtonSort_Click;
            Controls.Add(buttonSort);

            buttonAddPhoto = new Button
            {
                Text = "Add Photo",
                Left = 340,
                Top = 10,
                Width = 100
            };
            buttonAddPhoto.Click += ButtonAddPhoto_Click;
            Controls.Add(buttonAddPhoto);

            comboBoxSortBy.SelectedIndexChanged += (s, e) =>
            {
                textBoxTagFilter.Visible = comboBoxSortBy.SelectedItem.ToString() == "Tag";
            };

            Load += UserPhotosForm_Load;
            
            this.Resize += (s, e) =>
            {
                if (currentPhotos != null) DisplayPhotos(currentPhotos);
            };
        }

        private async void UserPhotosForm_Load(object sender, EventArgs e)
        {
            userPhotos = await _photoService.GetPhotosByUserIdAsync(_userId);
            currentPhotos = new List<PhotoMetadata>(userPhotos);
            DisplayPhotos(currentPhotos);
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

        private async void ButtonAddPhoto_Click(object sender, EventArgs e)
        {
            using (var addPhotosForm = new AddPhotosForm())
            {
                if (addPhotosForm.ShowDialog() == DialogResult.OK)
                {
                    foreach (var (photo, filePath) in addPhotosForm.PhotoMetadatas.Zip(addPhotosForm.LocalFilePaths, (p, f) => (p, f)))
                    {
                        string fileNameInStorage = Guid.NewGuid().ToString() + Path.GetExtension(filePath);
                        string fileUrl = await _photoService.UploadPhotoToStorageAsync(filePath, fileNameInStorage);

                        photo.FilePath = fileUrl;
                        photo.Id = null;
                        photo.UserId = _userId;

                        await _photoService.AddPhotoAndGetIdAsync(photo, filePath);
                    }
                    MessageBox.Show("Photos have been added.");
                    userPhotos = await _photoService.GetPhotosByUserIdAsync(_userId);
                    currentPhotos = new List<PhotoMetadata>(userPhotos);
                    DisplayPhotos(currentPhotos);
                }
            }
        }

        private async void DisplayPhotos(List<PhotoMetadata> photos)
        {
            for (int i = Controls.Count - 1; i >= 0; i--)
            {
                if (Controls[i] is PictureBox)
                    Controls.RemoveAt(i);
            }

            int margin = 10;
            int thumbWidth = 150;
            int thumbHeight = 150;
            int startY = 50;
            int x = margin;
            int y = startY;
            int maxWidth = this.ClientSize.Width;

            foreach (var photo in photos)
            {
                string thumbFileName = photo.Id + "_thumb.jpg";
                string thumbPath = Path.Combine(Path.GetTempPath(), thumbFileName);

                if (!File.Exists(thumbPath))
                {
                    string sanitizedFileName = Regex.Replace(photo.Id + Path.GetExtension(photo.FilePath), @"[<>:""/\\|?*]", "_");
                    string tempOriginalPath = Path.Combine(Path.GetTempPath(), sanitizedFileName);
                    await _photoService.DownloadPhotoByUrlAsync(photo.FilePath, tempOriginalPath);

                    PhotoService.CreateThumbnail(tempOriginalPath, thumbPath, thumbWidth, thumbHeight);

                    try { File.Delete(tempOriginalPath); } catch { }
                }

                if (x + thumbWidth + margin > maxWidth)
                {
                    x = margin;
                    y += thumbHeight + margin;
                }

                var pictureBox = new PictureBox
                {
                    Location = new Point(x, y),
                    Size = new Size(thumbWidth, thumbHeight),
                    SizeMode = PictureBoxSizeMode.Zoom,
                    Tag = photo
                };

                try { pictureBox.Load(thumbPath); } catch { }
                pictureBox.Click += PictureBox_Click;

                Controls.Add(pictureBox);
                x += thumbWidth + margin;
            }
        }

        private void PictureBox_Click(object sender, EventArgs e)
        {
            if (sender is PictureBox pb && pb.Tag is PhotoMetadata photo)
            {
                var editForm = new PhotoEditForm(photo);
                editForm.ShowDialog();
            }
        }
    }
}
