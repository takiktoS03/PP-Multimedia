using Aplikacja_desktopowa.Model;
using Aplikacja_desktopowa.Service;
using System;
using System.Collections.Generic;
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

        public UserPhotosForm(string userId)
        {
            _userId = userId;
            Text = "Zdjęcia użytkownika";
            Width = 600;
            Height = 600;
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

            comboBoxSortBy.SelectedIndexChanged += (s, e) =>
            {
                textBoxTagFilter.Visible = comboBoxSortBy.SelectedItem.ToString() == "Tag";
            };

            Load += UserPhotosForm_Load;
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

        private void DisplayPhotos(List<PhotoMetadata> photos)
        {
            for (int i = Controls.Count - 1; i >= 0; i--)
            {
                if (Controls[i] is PictureBox)
                    Controls.RemoveAt(i);
            }

            int y = 50;
            foreach (var photo in photos)
            {
                var pictureBox = new PictureBox
                {
                    Location = new System.Drawing.Point(10, y),
                    Size = new System.Drawing.Size(150, 150),
                    SizeMode = PictureBoxSizeMode.Zoom,
                    Tag = photo
                };

                try { pictureBox.Load(photo.FilePath); } catch { }

                pictureBox.Click += (s, e) =>
                {
                    var clickedPhoto = ((PictureBox)s).Tag as PhotoMetadata;
                    new PhotoEditForm(clickedPhoto).ShowDialog();
                };

                Controls.Add(pictureBox);
                y += 160;
            }
        }
    }
}
