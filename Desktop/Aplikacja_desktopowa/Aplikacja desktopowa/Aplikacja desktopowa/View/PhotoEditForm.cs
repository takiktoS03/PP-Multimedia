using Aplikacja_desktopowa.Model;
using Aplikacja_desktopowa.Service;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Net.Http;
using System.Windows.Forms;

namespace Aplikacja_desktopowa.View
{
    public class PhotoEditForm : Form
    {
        public PhotoMetadata Photo { get; private set; }
        public bool RemoveFromAlbum { get; private set; } = false;

        private PhotoService photoService = new PhotoService();

        private TextBox textBoxTitle;
        private TextBox textBoxDescription;
        private TextBox textBoxLocation;
        private TextBox textBoxTags;
        private Button buttonSave;
        private Button buttonRemove;
        private Button buttonDownload;

        public PhotoEditForm(PhotoMetadata photo)
        {
            Photo = photo;

            textBoxTitle = new TextBox { Text = photo.Title, Left = 20, Top = 20, Width = 300 };
            textBoxDescription = new TextBox { Text = photo.Description, Left = 20, Top = 60, Width = 300 };
            textBoxLocation = new TextBox { Text = photo.Location, Left = 20, Top = 100, Width = 300 };
            textBoxTags = new TextBox { Text = string.Join(",", photo.Tags ?? new List<string>()), Left = 20, Top = 140, Width = 300 };

            buttonSave = new Button { Text = "Zapisz", Left = 20, Top = 180, Width = 120 };
            buttonRemove = new Button { Text = "Usuñ", Left = 140, Top = 180, Width = 120 };
            buttonDownload = new Button { Text = "Pobierz zdjêcie", Left = 260, Top = 180, Width = 120 };

            buttonSave.Click += (s, e) =>
            {
                Photo.Title = textBoxTitle.Text;
                Photo.Description = textBoxDescription.Text;
                Photo.Location = textBoxLocation.Text;
                Photo.Tags = new List<string>((textBoxTags.Text ?? "").Split(new[] { ',' }, StringSplitOptions.RemoveEmptyEntries));
                DialogResult = DialogResult.OK;
                Close();
            };

            buttonRemove.Click += (s, e) =>
            {
                RemoveFromAlbum = true;
                DialogResult = DialogResult.OK;
                Close();
            };

            buttonDownload.Click += async (s, e) =>
            {
                try
                {
                    if (string.IsNullOrWhiteSpace(Photo.FilePath))
                    {
                        MessageBox.Show("Brakuje œcie¿ki do pliku (URL zdjêcia).", "B³¹d", MessageBoxButtons.OK, MessageBoxIcon.Error);
                        return;
                    }

                    string localFileName = $"{Photo.Title}.jpg";
                    string localFilePath = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.MyPictures), localFileName);

                    using (HttpClient client = new HttpClient())
                    {
                        var response = await client.GetAsync(Photo.FilePath);
                        response.EnsureSuccessStatusCode();

                        using (var fs = new FileStream(localFilePath, FileMode.Create, FileAccess.Write))
                        {
                            await response.Content.CopyToAsync(fs);
                        }
                    }

                    MessageBox.Show($"Zdjêcie zosta³o pobrane do: {localFilePath}", "Sukces", MessageBoxButtons.OK, MessageBoxIcon.Information);
                }
                catch (Exception ex)
                {
                    MessageBox.Show($"B³¹d podczas pobierania zdjêcia: {ex.Message}", "B³¹d", MessageBoxButtons.OK, MessageBoxIcon.Error);
                }
            };

            Controls.Add(textBoxTitle);
            Controls.Add(textBoxDescription);
            Controls.Add(textBoxLocation);
            Controls.Add(textBoxTags);
            Controls.Add(buttonSave);
            Controls.Add(buttonRemove);
            Controls.Add(buttonDownload);

            Text = "Edycja zdjêcia";
            ClientSize = new System.Drawing.Size(500, 250);
            FormBorderStyle = FormBorderStyle.FixedDialog;
            StartPosition = FormStartPosition.CenterParent;
        }
    }
}
